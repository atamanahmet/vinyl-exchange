package com.vinyl.VinylExchange.messaging;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.vinyl.VinylExchange.messaging.dto.ConversationDTO;
import com.vinyl.VinylExchange.messaging.dto.MessageDTO;
import com.vinyl.VinylExchange.messaging.dto.SendMessageRequest;
import com.vinyl.VinylExchange.messaging.enums.MessageType;
import com.vinyl.VinylExchange.shared.exception.ConversationNotFoundException;
import com.vinyl.VinylExchange.shared.exception.UnauthorizedActionException;
import com.vinyl.VinylExchange.user.UserService;

public class MessagingService {
    private final MessagingRepository messagingRepository;
    private final ConversationRepository conversationRepository;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public MessagingService(
            MessagingRepository messagingRepository,
            ConversationRepository conversationRepository,
            UserService userService) {

        this.messagingRepository = messagingRepository;
        this.conversationRepository = conversationRepository;
        this.userService = userService;

        logger.info("Messaging service ready.");
    }

    public MessageDTO saveMessage(SendMessageRequest request){
        Message message = new Message()
    }

    @Transactional
    public ConversationDTO startConversation(
            UUID initiatorId,
            UUID participantId,
            UUID relatedListingId) {

        if (initiatorId.equals(participantId)) {
            logger.warn("User tried to message themselves");
            throw new IllegalArgumentException("Same id message initialization issue");
        }

        Conversation conversation = conversationRepository.findBetweenUsers(initiatorId, participantId)
                .orElseGet(() -> {

                    try {
                        Conversation newConversation = new Conversation(initiatorId, participantId, relatedListingId);

                        return conversationRepository.save(newConversation);

                        // if conversation exist, and got existing id error due to spamming, try again
                        // to fetch
                    } catch (DataIntegrityViolationException e) {

                        return conversationRepository.findBetweenUsers(initiatorId, participantId)
                                .orElseThrow(() -> new RuntimeException("Conversation creation issues"));
                    }
                });

        return convertToDTO(conversation, initiatorId);
    }

    @Transactional
    public MessageDTO sendMessage(
            Long conversationId,
            UUID senderId,
            String content,
            MessageType messageType) {

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException());

        if (!conversation.isUserParticipant(senderId)) {
            logger.warn("User is not part of this conversation, userId: " + senderId);
            throw new UnauthorizedActionException(senderId);
        }

        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .content(content)
                .messageType(messageType)
                .build();

        Message savedMessage = messagingRepository.save(message);

        UUID receiverId = conversation.getOtherParticipant(senderId);

        conversation.increaseUnreadCount(receiverId);
        conversation.updateLastMessageTime();

        conversationRepository.save(conversation);

        return new MessageDTO().from(savedMessage);
    }

    public MessageDTO sendMessage(
            Long conversationId,
            UUID senderId,
            String content) {

        return sendMessage(conversationId, senderId, content, MessageType.TEXT);
    }

    public Page<MessageDTO> getMessages(Long conversationId, UUID userId, int page, int size) {

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException());

        if (!conversation.isUserParticipant(userId)) {
            throw new UnauthorizedActionException("User in not part of the conversation ", userId);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Message> messagePage = messagingRepository.findByConversationIdOrderByTimestampDesc(conversationId,
                pageable);

        Page<MessageDTO> messageDTOPage = messagePage.map(message -> new MessageDTO().from(message));

        return messageDTOPage;
    }

    public ConversationDTO convertToDTO(Conversation conversation, UUID initiatorId) {

        UUID otherUserId = conversation.getOtherParticipant(initiatorId);

        String otherUsername = userService.findUsernameByUserId(otherUserId);

        String lastMessagePreview = messagingRepository.findLatestMessageByConversationId(conversation.getId())
                .map(message -> message.getContent().length() > 50 ? message.getContent().substring(0, 50)
                        : message.getContent())
                .orElse("No messages yet");

        return new ConversationDTO().from(
                conversation,
                otherUsername,
                "",
                lastMessagePreview);
    }

    @Transactional(readOnly = true)
    public List<ConversationDTO> getUserConversations(UUID userId) {

        List<Conversation> conversationList = conversationRepository.findAllByUserId(userId);

        List<ConversationDTO> conversationDTOList = conversationList.stream()
                .map((conv) -> convertToDTO(conv, userId))
                .collect(Collectors.toList());

        return conversationDTOList;
    }
}
