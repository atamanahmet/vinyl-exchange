package com.vinyl.VinylExchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.vinyl.VinylExchange.domain.enums.ErrorType;
import com.vinyl.VinylExchange.domain.enums.IssueType;
import com.vinyl.VinylExchange.domain.CheckOutResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.entity.Cart;
import com.vinyl.VinylExchange.domain.entity.CartItem;
import com.vinyl.VinylExchange.dto.CartValidationIssue;

import com.vinyl.VinylExchange.domain.entity.Listing;

import com.vinyl.VinylExchange.domain.entity.Order;
import com.vinyl.VinylExchange.domain.entity.OrderItem;
import com.vinyl.VinylExchange.domain.enums.OrderStatus;

import com.vinyl.VinylExchange.exception.CheckOutProcessingException;
import com.vinyl.VinylExchange.exception.CheckOutValidationException;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CheckOutService {

    private static final Logger logger = LoggerFactory.getLogger(CheckOutService.class);

    private final ListingService listingService;
    private final OrderService orderService;
    private final CartService cartService;
    private final OrderItemService orderItemService;
    private final FileStorageService fileStorageService;


    public CheckOutResult proceedCheckOut(UUID userId) {

        Cart cart = cartService.getCart(userId);

        List<UUID> listingIdsInCart = cart.getCartItems()
                .stream()
                .map(CartItem::getListingId)
                .collect(Collectors.toList());

        Map<UUID, Listing> listingMap = listingService.getListingsByIdsWithLock(listingIdsInCart)
                .stream()
                .collect(Collectors.toMap(Listing::getId, listing -> listing));

        List<CartValidationIssue> validationIssues = validateCartItems(cart.getCartItems(), listingMap);

        if (hasCheckoutCancelErrors(validationIssues)) {

            throw new CheckOutValidationException(validationIssues);
        }

        try {

            Order order = createOrder(userId, cart.getCartItems(), listingMap);

            CheckOutResult checkOutResult = CheckOutResult.builder()
                    .success(true)
                    .order(order)
                    .message("CheckOut completed successfully")
                    .build();

            cartService.clearCart(userId);

            return checkOutResult;

        } catch (Exception e) {

            logger.error("Checkout failed for user id: {}", userId, e);
            throw new CheckOutProcessingException();
        }
    }

    private List<CartValidationIssue> validateCartItems(List<CartItem> cartItems, Map<UUID, Listing> listingMap) {

        List<CartValidationIssue> issues = new ArrayList<>();

        for (CartItem cartItem : cartItems) {

            Listing listing = listingMap.get(cartItem.getListingId());

            if (listing == null) {

                issues.add(CartValidationIssue.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .listingId(cartItem.getListingId())
                        .type(IssueType.LISTING_DELETED)
                        .message("Listing is no longer available")
                        .errorType(ErrorType.ERROR)
                        .build());
                continue;
            }

            if (!listing.hasEnoughStock(cartItem.getOrderQuantity())) {

                issues.add(CartValidationIssue.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .listingId(cartItem.getListingId())
                        .type(IssueType.INSUFFICIENT_STOCK)
                        .message("Not enough stock available, adjust quantity")
                        .errorType(ErrorType.ERROR)
                        .build());
                continue;
            }

            if (!listing.isAvailable()) {

                issues.add(CartValidationIssue.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .listingId(cartItem.getListingId())
                        .type(IssueType.SOLD_OUT)
                        .message(listing.getTitle() + " - " + listing.getArtistName() + " is sold out")
                        .errorType(ErrorType.ERROR)
                        .build());
                continue;
            }
        }
        return issues;
    }

    private boolean hasCheckoutCancelErrors(List<CartValidationIssue> validationIssues) {

        return validationIssues.stream()
                .anyMatch(issue -> issue.getErrorType() == ErrorType.ERROR);
    }

    @Transactional
    private Order createOrder(UUID userId, List<CartItem> cartItems, Map<UUID, Listing> listingMap) {

        Order order = Order.builder()
                .orderNumber(orderService.getNextOrderNumber())
                .buyerId(userId)
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();

        // saved first for order number seq generation,
        order = orderService.saveOrder(order);

        Long totalPrice = 0L;

        List<Listing> listingsToUpdate = new ArrayList<>();

        for (CartItem item : cartItems) {

            Listing listing = listingMap.get(item.getListingId());

            String listingMainImagePath = fileStorageService.getMainImagePath(listing.getId());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .listingId(listing.getId())
                    .listingTitle(listing.getTitle())
                    .listingMainImageUrl(listingMainImagePath)
                    .sellerId(listing.getOwnerId())
                    .unitPrice(listing.getPriceKurus())
                    .quantity(item.getOrderQuantity())
                    .subTotal(listing.getPriceKurus() * item.getOrderQuantity())
                    .build();

            // saved first for single sourc of truth :createdAt
            OrderItem savedItem = orderItemService.saveOrderItemAndFlush(orderItem);

            savedItem.setShippingDeadline(savedItem.getCreatedAt().plusDays(5));
            savedItem.setExpectedDeliveryDate(savedItem.getCreatedAt().plusDays(7));

            order.getOrderItems().add(savedItem);

            totalPrice += savedItem.getSubTotal();

            listing.setStockQuantity(listing.getStockQuantity() - savedItem.getQuantity());

            listingsToUpdate.add(listing);

        }

        order.setTotalPrice(totalPrice);

        listingService.saveAllListing(listingsToUpdate);
        orderItemService.saveAllOrderItems(order.getOrderItems());

        logger.info("Order createdand populated with orderNumber: {}", order.getOrderNumber());

        return orderService.saveOrder(order);
    }
}
