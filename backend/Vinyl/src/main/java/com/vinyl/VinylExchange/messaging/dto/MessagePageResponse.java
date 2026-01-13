package com.vinyl.VinylExchange.messaging.dto;

import org.springframework.data.domain.Page;

public record MessagePageResponse(
        ConversationDTO conversationDTO,
        Page<MessageDTO> messagePage) {

}
