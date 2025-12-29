package com.vinyl.VinylExchange.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.domain.entity.Cart;
import com.vinyl.VinylExchange.domain.entity.CartItem;
import com.vinyl.VinylExchange.domain.entity.CartValidationIssue;
import com.vinyl.VinylExchange.domain.entity.CheckOutResult;
import com.vinyl.VinylExchange.domain.entity.Listing;
import com.vinyl.VinylExchange.domain.entity.Order;
import com.vinyl.VinylExchange.domain.entity.OrderItem;
import com.vinyl.VinylExchange.domain.entity.OrderStatus;
import com.vinyl.VinylExchange.exception.CheckOutProcessingException;
import com.vinyl.VinylExchange.exception.CheckOutValidationException;

import jakarta.transaction.Transactional;

@Service
public class CheckOutService {

    private final ListingService listingService;
    private final OrderService orderService;
    private final CartService cartService;
    private final OrderItemService orderItemService;

    public CheckOutService(
            ListingService listingService,
            OrderService orderService,
            CartService cartService,
            OrderItemService orderItemService) {

        this.listingService = listingService;
        this.orderService = orderService;
        this.cartService = cartService;
        this.orderItemService = orderItemService;
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
            System.out.println(e.getMessage());
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
                .buyerId(userId)
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();

        order = orderService.saveOrder(order);

        Long totalPrice = 0L;

        for (CartItem item : cartItems) {
            Listing listing = listingMap.get(item.getListingId());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .listing(listing)
                    .unitPrice(listing.getPriceKurus())
                    .quantity(item.getOrderQuantity())
                    .subTotal(listing.getPriceKurus() * item.getOrderQuantity())
                    .build();

            order.getOrderItems().add(orderItem);

            totalPrice = totalPrice + orderItem.getSubTotal();

            listing.setStockQuantity(listing.getStockQuantity() - orderItem.getQuantity());

            orderItemService.saveOrderItem(orderItem);
        }

        order.setTotalPrice(totalPrice);

        return orderService.saveOrder(order);
    }

}
