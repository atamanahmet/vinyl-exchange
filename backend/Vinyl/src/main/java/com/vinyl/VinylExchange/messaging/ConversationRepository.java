package com.vinyl.VinylExchange.messaging;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

        @Query("Select c from Conversation c " +
                        "WHERE c.initiatorId= :userId OR c.participantId = :userId " +
                        "ORDER BY c.lastMessageAt DESC")
        List<Conversation> findAllByUserId(@Param("userId") UUID userId);

        @Query("Select c from Conversation c " +
                        "WHERE c.initiatorId= :userIdOne OR c.participantId = :userIdTwo " +
                        "ORDER BY c.lastMessageAt DESC")
        Optional<Conversation> findBetweenUsers(@Param("userIdOne") UUID userIdOne, @Param("userIdTwo") UUID userIdTwo);

        Optional<Conversation> findByRelatedListingId(UUID listingId);
}
