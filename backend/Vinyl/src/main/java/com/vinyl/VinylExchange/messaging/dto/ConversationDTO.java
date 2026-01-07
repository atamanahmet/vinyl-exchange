package com.vinyl.VinylExchange.messaging.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vinyl.VinylExchange.messaging.Conversation;

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
public class ConversationDTO {

    private Long id;

    private UUID initiatorId;

    private UUID participantId;

    private UUID relatedListingId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastMessageAt;

    private String lastMessagePreview;

    public static ConversationDTO from(Conversation conversation) {

        return ConversationDTO.builder()
                .id(conversation.getId())
                .initiatorId(conversation.getInitiatorId())
                .participantId(conversation.getParticipantId())
                .relatedListingId(conversation.getRelatedListingId())
                .createdAt(conversation.getCreatedAt())
                .lastMessageAt(conversation.getLastMessageAt())
                .build();
    }
}
