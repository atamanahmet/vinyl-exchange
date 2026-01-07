package com.vinyl.VinylExchange.messaging.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.vinyl.VinylExchange.messaging.Message;
import com.vinyl.VinylExchange.messaging.enums.MessageType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class MessageDTO {

    private long id;
    private long conversationId;
    private UUID senderId;
    private String senderUsername;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String content;
    private LocalDateTime timestamp;
    private boolean isRead;

    public static MessageDTO from(Message message, String senderUserName) {

        return MessageDTO.builder()
                .conversationId(message.getConversationId())
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderUsername(senderUserName)
                .messageType(message.getMessageType())
                .content(message.getContent())
                .isRead(message.isRead())
                .timestamp(message.getTimestamp())
                .build();
    }
}
