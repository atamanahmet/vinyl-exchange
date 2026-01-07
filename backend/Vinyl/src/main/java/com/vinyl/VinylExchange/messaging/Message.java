package com.vinyl.VinylExchange.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vinyl.VinylExchange.messaging.enums.MessageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long conversationId;

    @Column(nullable = false)
    private UUID senderId;

    private String senderUsername;

    @Enumerated(EnumType.STRING)
    @Builder.Default

    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;

    @Column(nullable = false, length = 2000)
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean isRead;

    public void markAsRead() {
        this.isRead = true;
    }

    public boolean isSentBy(UUID userId) {

        return this.senderId.equals(userId);
    }
}
