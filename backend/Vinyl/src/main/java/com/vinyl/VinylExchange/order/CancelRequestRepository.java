package com.vinyl.VinylExchange.order;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vinyl.VinylExchange.order.enums.CancelRequestStatus;

@Repository
public interface CancelRequestRepository extends JpaRepository<CancelRequest, UUID> {

    boolean existsByOrderItemAndStatus(OrderItem orderItem, CancelRequestStatus status);

    @Query("SELECT cr FROM CancelRequest cr " +
            "JOIN cr.orderItem oi " +
            "WHERE oi.sellerId = :sellerId " +
            "AND cr.status = 'PENDING'")
    List<CancelRequest> findPendingRequestsForSeller(@Param("sellerId") UUID sellerId);

    List<CancelRequest> findByOrderItemId(UUID orderItemId);
}
