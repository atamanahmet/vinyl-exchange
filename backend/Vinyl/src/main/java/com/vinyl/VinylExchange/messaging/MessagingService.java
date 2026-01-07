package com.vinyl.VinylExchange.messaging;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vinyl.VinylExchange.messaging.dto.MessageDTO;

public class MessagingService {
    private final MessagingRepository messagingRepository;
    private final ConversationRepository conversationRepository;

    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public MessagingService(MessagingRepository messagingRepository, ConversationRepository conversationRepository) {
        this.messagingRepository = messagingRepository;
        this.conversationRepository = conversationRepository;

        logger.info("Messaging service ready.");
    }

    public MessageDTO sendMessage(Long conversationId, UUID senderId) {
        return new MessageDTO();
    }
}
