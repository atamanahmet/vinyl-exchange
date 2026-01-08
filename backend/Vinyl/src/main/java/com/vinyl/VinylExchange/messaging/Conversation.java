package com.vinyl.VinylExchange.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;

@Entity
@Table(name = "conversations")
@Getter
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID initiatorId;

    @Column(nullable = false)
    private UUID participantId;

    @Column(nullable = false)
    private UUID relatedListingId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private long initiatorUnreadCount = 0;

    @Column(nullable = false)
    private long participantUnreadCount = 0;

    @Column(nullable = false)
    private LocalDateTime lastMessageAt;

    public Conversation(UUID initiatorId, UUID participantId, UUID relatedListingId) {

        this.initiatorId = initiatorId;
        this.participantId = participantId;
        this.relatedListingId = relatedListingId;
        this.createdAt = LocalDateTime.now();
        this.lastMessageAt = LocalDateTime.now();
    }

    public boolean isUserParticipant(UUID userId) {

        return this.initiatorId.equals(userId) || this.participantId.equals(userId);
    }

    public UUID getOtherParticipant(UUID userId) {

        // return this.initiatorId.equals(userId) ? this.participantId
        // : this.participantId.equals(userId) ? this.initiatorId : null;

        if (this.initiatorId.equals(userId)) {
            return this.participantId;
        } else if (this.participantId.equals(userId)) {
            return this.initiatorId;
        }

        throw new IllegalArgumentException("This User is not part of the conversation");
    }

    public void updateLastMessageTime() {
        this.lastMessageAt = LocalDateTime.now();
    }

    public void resetUnreadCount(UUID userId) {
        if (this.initiatorId.equals(userId)) {
            this.initiatorUnreadCount = 0;
        } else if (this.participantId.equals(userId)) {
            this.participantUnreadCount = 0;
        }
    }

    public void increaseUnreadCount(UUID userId) {
        if (this.initiatorId.equals(userId)) {
            this.initiatorUnreadCount++;
        } else if (this.participantId.equals(userId)) {
            this.participantUnreadCount++;
        }
    }

    public Long getUnreadCount(UUID userId) {
        if (this.initiatorId.equals(userId)) {
            return this.initiatorUnreadCount;
        } else if (this.participantId.equals(userId)) {
            return this.participantUnreadCount;
        }

        return null;
    }
}
