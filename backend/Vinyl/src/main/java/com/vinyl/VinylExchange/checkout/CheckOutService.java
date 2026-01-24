package com.vinyl.VinylExchange.checkout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.cart.Cart;
import com.vinyl.VinylExchange.cart.CartItem;
import com.vinyl.VinylExchange.cart.CartService;
import com.vinyl.VinylExchange.cart.CartValidationIssue;

import com.vinyl.VinylExchange.listing.Listing;
import com.vinyl.VinylExchange.listing.ListingService;

import com.vinyl.VinylExchange.order.Order;
import com.vinyl.VinylExchange.order.OrderItem;
import com.vinyl.VinylExchange.order.OrderItemService;
import com.vinyl.VinylExchange.order.OrderService;
import com.vinyl.VinylExchange.order.enums.OrderStatus;
import com.vinyl.VinylExchange.shared.FileStorageService;
import com.vinyl.VinylExchange.shared.exception.CheckOutProcessingException;
import com.vinyl.VinylExchange.shared.exception.CheckOutValidationException;

import jakarta.transaction.Transactional;

@Service
public class CheckOutService {

    private static final Logger logger = LoggerFactory.getLogger(CheckOutService.class);

    private final ListingService listingService;
    private final OrderService orderService;
    private final CartService cartService;
    private final OrderItemService orderItemService;
    private final FileStorageService fileStorageService;

    public CheckOutService(
            ListingService listingService,
            OrderService orderService,
            CartService cartService,
            OrderItemService orderItemService,
            FileStorageService fileStorageService) {

        this.listingService = listingService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.orderItemService = orderItemService;
        this.fileStorageService = fileStorageService;
    }

    public CheckOutResult proceedCheckOut(UUID userId) {

        Cart cart = cartService.getCart(userId);

        List<UUID> listingIdsInCart = cart.getCartItems()
                .stream()
                .map(item -> item.getListingId())
                .collect(Collectors.toList());

        Map<UUID, Listing> listingMap = listingService.getListingsByIdsWithLock(listingIdsInCart)
                .stream()
                .collect(Collectors.toMap(listing -> listing.getId(), listing -> listing));

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
                        .type(CartValidationIssue.IssueType.LISTING_DELETED)
                        .message("Listing is no longer available")
                        .errorType(CartValidationIssue.ErrorType.ERROR)
                        .build());
                continue;
            }

            if (!listing.hasEnoughStock(cartItem.getOrderQuantity())) {

                issues.add(CartValidationIssue.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .listingId(cartItem.getListingId())
                        .type(CartValidationIssue.IssueType.INSUFFICIENT_STOCK)
                        .message("Not enough stock available, adjust quantity")
                        .errorType(CartValidationIssue.ErrorType.ERROR)
                        .build());
                continue;
            }

            if (!listing.isAvailable()) {

                issues.add(CartValidationIssue.builder()
                        .cartItemId(cartItem.getCartItemId())
                        .listingId(cartItem.getListingId())
                        .type(CartValidationIssue.IssueType.SOLD_OUT)
                        .message(listing.getTitle() + " - " + listing.getArtistName() + " is sold out")
                        .errorType(CartValidationIssue.ErrorType.ERROR)
                        .build());
                continue;
            }
        }
        return issues;
    }

    private boolean hasCheckoutCancelErrors(List<CartValidationIssue> validationIssues) {

        return validationIssues.stream()
                .anyMatch(issue -> issue.getErrorType() == CartValidationIssue.ErrorType.ERROR);
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
