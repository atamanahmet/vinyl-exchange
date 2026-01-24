package com.vinyl.VinylExchange.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.listing.ListingService;

import com.vinyl.VinylExchange.order.enums.CancelRequestStatus;
import com.vinyl.VinylExchange.order.enums.OrderItemStatus;
import com.vinyl.VinylExchange.order.enums.OrderStatus;

import com.vinyl.VinylExchange.shared.exception.CancelRequestNotFoundException;
import com.vinyl.VinylExchange.shared.exception.InvalidOrderOperationException;
import com.vinyl.VinylExchange.shared.exception.ListingNotFoundException;
import com.vinyl.VinylExchange.shared.exception.OrderItemNotFoundException;
import com.vinyl.VinylExchange.shared.exception.UnauthorizedActionException;

import jakarta.transaction.Transactional;

@Service
public class CancelService {
    private static final Logger logger = LoggerFactory.getLogger(CancelService.class);
    private static final int AUTO_APPROVE_HOURS = 2;

    private final CancelRequestRepository cancelRequestRepository;
    private final OrderItemService orderItemService;
    private final OrderService orderService;
    private final ListingService listingService;

    public CancelService(
            CancelRequestRepository cancelRequestRepository,
            OrderItemService orderItemService,
            OrderService orderService,
            ListingService listingService) {
        this.cancelRequestRepository = cancelRequestRepository;
        this.orderItemService = orderItemService;
        this.orderService = orderService;
        this.listingService = listingService;
    }

    @Transactional
    public CancelRequest requestCancel(UUID orderItemId, UUID userId, String reason) {

        OrderItem orderItem = orderItemService.getOrderItemById(orderItemId)
                .orElseThrow(() -> new OrderItemNotFoundException("Order item not found"));

        if (!orderItem.getOrder().getBuyerId().equals(userId)) {

            throw new UnauthorizedActionException("User is not own this item");
        }

        if (orderItem.getStatus() == OrderItemStatus.DELIVERED ||
                orderItem.getStatus() == OrderItemStatus.CANCELLED ||
                orderItem.getStatus() == OrderItemStatus.CANCEL_PENDING) {

            throw new InvalidOrderOperationException(
                    "Cannot request cancel for item with status: " + orderItem.getStatus());
        }

        boolean hasPendingRequest = cancelRequestRepository
                .existsByOrderItemAndStatus(orderItem, CancelRequestStatus.PENDING);

        if (hasPendingRequest) {

            throw new InvalidOrderOperationException("Cancel request already pending for this item");
        }

        CancelRequest request = CancelRequest.builder()
                .orderItem(orderItem)
                .requestedBy(userId)
                .status(CancelRequestStatus.PENDING)
                .previousOrderStatus(orderItem.getStatus())
                .reason(reason)
                .requestedAt(LocalDateTime.now())
                .build();

        // if in 2 hours auto approve
        // if status changed in 2 hours, put in pending for review
        if (shouldAutoApprove(orderItem)) {

            if (orderItem.getStatus() != OrderItemStatus.PENDING &&
                    orderItem.getStatus() != OrderItemStatus.PROCESSING) {

                orderItem.setStatus(OrderItemStatus.CANCEL_PENDING);

                logger.warn("Cannot approve, item status changed {}", orderItem.getStatus());
            } else {

                request.setStatus(CancelRequestStatus.AUTO_APPROVED);
                request.setReviewedAt(LocalDateTime.now());

                orderItem.setStatus(OrderItemStatus.CANCELLED);
                processCancel(orderItem);

            }
        } else {

            orderItem.setStatus(OrderItemStatus.CANCEL_PENDING);
        }

        orderItemService.saveOrderItem(orderItem);

        return cancelRequestRepository.save(request);
    }

    @Transactional
    public CancelRequest approveCancel(UUID requestId, UUID reviewerId, String reviewNote) {

        CancelRequest request = cancelRequestRepository.findById(requestId)
                .orElseThrow(() -> new CancelRequestNotFoundException("Cancel request not found"));

        if (request.getStatus() != CancelRequestStatus.PENDING) {
            throw new InvalidOrderOperationException("Can only approve pending cancel requests");
        }

        OrderItem orderItem = request.getOrderItem();

        if (!orderItem.getSellerId().equals(reviewerId)) {
            throw new UnauthorizedActionException("Only seller or admin can review cancel requests");
        }

        request.setStatus(CancelRequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewerId);
        request.setReviewNote(reviewNote);

        orderItem.setStatus(OrderItemStatus.CANCELLED);
        orderItemService.saveOrderItem(orderItem);

        processCancel(orderItem);

        return cancelRequestRepository.save(request);
    }

    @Transactional
    public CancelRequest rejectCancel(UUID requestId, UUID reviewerId, String reviewNote) {

        CancelRequest request = cancelRequestRepository.findById(requestId)
                .orElseThrow(() -> new CancelRequestNotFoundException("Request not found"));

        if (request.getStatus() != CancelRequestStatus.PENDING) {
            throw new InvalidOrderOperationException("Can only reject pending cancel requests");
        }

        OrderItem orderItem = request.getOrderItem();

        request.setStatus(CancelRequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewerId);
        request.setReviewNote(reviewNote);

        orderItem.setStatus(request.getPreviousOrderStatus());

        orderItemService.saveOrderItem(orderItem);

        return cancelRequestRepository.save(request);
    }

    private boolean shouldAutoApprove(OrderItem orderItem) {
        LocalDateTime timeLimit = LocalDateTime.now().minusHours(AUTO_APPROVE_HOURS);
        return orderItem.getCreatedAt().isAfter(timeLimit) &&
                orderItem.getStatus() == OrderItemStatus.PENDING;
    }

    private void processCancel(OrderItem orderItem) {

        Order order = orderItem.getOrder();

        Long newTotalPrice = order.getOrderItems().stream()
                .filter(item -> item.getStatus() != OrderItemStatus.CANCELLED)
                .mapToLong(OrderItem::getSubTotal)
                .sum();

        order.setTotalPrice(newTotalPrice);

        try {
            listingService.restoreStock(orderItem.getListingId(), orderItem.getQuantity());
        } catch (ListingNotFoundException e) {
            logger.warn("Could not restore stock for listing {}: {}",
                    orderItem.getListingId(), e.getMessage());
        }

        boolean allItemsCancelled = order.getOrderItems().stream()
                .allMatch(item -> item.getStatus() == OrderItemStatus.CANCELLED);

        if (allItemsCancelled) {
            order.setStatus(OrderStatus.CANCELLED);
            logger.info("All items cancelled, order {} marked as cancelled", order.getId());
        }

        orderService.saveOrder(order);
    }

    @Transactional
    public void cancelCancellationRequest(UUID requestId, UUID userId) {

        CancelRequest request = cancelRequestRepository.findById(requestId)
                .orElseThrow(() -> new CancelRequestNotFoundException("Cancel request not found"));

        if (!request.getRequestedBy().equals(userId)) {
            throw new UnauthorizedActionException("User does not own this cancellation request");
        }

        if (request.getStatus() != CancelRequestStatus.PENDING) {
            throw new InvalidOrderOperationException("Can only cancel pending requests");
        }

        OrderItem orderItem = request.getOrderItem();

        request.setStatus(CancelRequestStatus.CANCELLED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(userId);

        cancelRequestRepository.save(request);

        orderItem.setStatus(request.getPreviousOrderStatus());

        orderItemService.saveOrderItem(orderItem);
    }

    public List<CancelRequest> getPendingRequestsForSeller(UUID sellerId) {
        return cancelRequestRepository.findPendingRequestsForSeller(sellerId);
    }

    public List<CancelRequest> getRequestsForOrderItem(UUID orderItemId) {
        return cancelRequestRepository.findByOrderItemId(orderItemId);
    }

}
