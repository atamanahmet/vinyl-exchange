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
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.vinyl.VinylExchange.listing.Listing;
import com.vinyl.VinylExchange.listing.ListingService;

import com.vinyl.VinylExchange.messaging.dto.ConversationDTO;
import com.vinyl.VinylExchange.messaging.dto.MessageDTO;
import com.vinyl.VinylExchange.messaging.enums.MessageType;

import com.vinyl.VinylExchange.shared.exception.ListingNotFoundException;
import com.vinyl.VinylExchange.shared.exception.UnauthorizedActionException;
import com.vinyl.VinylExchange.user.UserService;

@Service
public class MessagingService {
    private final MessagingRepository messagingRepository;
    private final ConversationRepository conversationRepository;
    private final UserService userService;
    private final ListingService listingService;

    private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

    public MessagingService(
            MessagingRepository messagingRepository,
            ConversationRepository conversationRepository,
            UserService userService,
            ListingService listingService) {

        this.messagingRepository = messagingRepository;
        this.conversationRepository = conversationRepository;
        this.userService = userService;
        this.listingService = listingService;

        logger.info("Messaging service ready.");
    }

    @Transactional
    public Conversation startConversation(
            UUID initiatorId,
            String initiatorUsername,
            UUID relatedListingId) {

        Listing listing = listingService.findListingById(relatedListingId);
        // to check availability, throws exception if not exist or available
        if (!listing.isAvailable()) {
            throw new ListingNotFoundException("Listing is not available for trade, cannot start conversation");
        }

        UUID participantId = listing.getOwnerId();
        String participantUsername = listing.getOwnerUsername();

        Conversation conversation = conversationRepository
                .findBetweenUsers(initiatorId, participantId, relatedListingId)
                .orElseGet(() -> {

                    try {
                        Conversation newConversation = new Conversation(initiatorId, initiatorUsername, participantId,
                                participantUsername, relatedListingId);

                        return conversationRepository.save(newConversation);

                        // if conversation exist, but got existing id error due to spamming, try again
                    } catch (DataIntegrityViolationException e) {

                        return conversationRepository.findBetweenUsers(initiatorId, participantId, relatedListingId)
                                .orElseThrow(
                                        () -> new RuntimeException("Conversation creation issues: MessagingService"));
                    }
                });

        return conversation;
    }

    @Transactional
    public MessageDTO sendMessage(
            UUID senderId,
            UUID relatedListingId,
            String content,
            MessageType messageType) {

        String senderUsername = userService.findUsernameById(senderId);

        Listing relatedListing = listingService.findListingById(relatedListingId);

        UUID participantId = relatedListing.getOwner().getId();

        String participantUsername = relatedListing.getOwner().getUsername();

        Conversation conversation = getOrStartConversation(senderId, relatedListingId);

        if (!conversation.isUserParticipant(senderId)) {
            logger.warn("User is not part of this conversation, userId: " + senderId);
            throw new UnauthorizedActionException(senderId);
        }

        Message message = Message.builder()
                .conversationId(conversation.getId())
                .senderId(senderId)
                .senderUsername(senderUsername)
                .receiverUsername(participantUsername)
                .content(content)
                .messageType(messageType == null ? MessageType.TEXT : messageType)
                .build();

        Message savedMessage = messagingRepository.save(message);

        conversation.increaseUnreadCount(participantId);
        conversation.updateLastMessageTime();

        conversationRepository.save(conversation);

        return new MessageDTO().from(savedMessage);
    }

    public Page<MessageDTO> getMessages(UUID userId, UUID listingId, int page, int size) {

        System.out.println("getting messages...");

        Conversation conversation = getOrStartConversation(userId, listingId);

        System.out.println("conversation id " + conversation.getId());
        System.out.println("initiator id " + conversation.getInitiatorId());
        System.out.println("reciever id " + conversation.getParticipantId());

        if (!conversation.isUserParticipant(userId)) {
            throw new UnauthorizedActionException("User in not part of the conversation ", userId);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Message> messagePage = messagingRepository.findByConversationIdOrderByTimestampAsc(conversation.getId(),
                pageable);

        // messagePage.iterator().forEachRemaining(message ->
        // System.out.println(message.getContent()));

        Page<MessageDTO> messageDTOPage = messagePage.map(message -> new MessageDTO().from(message));

        return messageDTOPage;
    }

    public ConversationDTO getThisConversationDTO(UUID userId, UUID listingId) {
        Conversation conversation = getOrStartConversation(userId, listingId);

        return convertToDTO(conversation, listingId);
    }

    public ConversationDTO convertToDTO(Conversation conversation, UUID initiatorId) {

        String lastMessagePreview = messagingRepository.findLatestMessageByConversationId(conversation.getId())
                .map(message -> message.getContent().length() > 50 ? message.getContent().substring(0, 50)
                        : message.getContent())
                .orElse("No messages yet");

        return new ConversationDTO().from(
                conversation,
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

    public Conversation getOrStartConversation(UUID initiatorId, UUID relatedListingId) {

        Listing listing = listingService.findListingById(relatedListingId);
        // to check availability, throws exception if not exist or available
        if (!listing.isAvailable()) {
            throw new ListingNotFoundException("Listing is not available for trade, cannot start conversation");
        }

        UUID participantId = listing.getOwnerId();
        String participantUsername = listing.getOwnerUsername();

        String initiatorUsername = userService.findUsernameById(initiatorId);

        Conversation conversation = conversationRepository
                .findBetweenUsers(initiatorId, participantId, relatedListingId)
                .orElseGet(() -> {

                    try {
                        Conversation newConversation = new Conversation(initiatorId, initiatorUsername, participantId,
                                participantUsername, relatedListingId);

                        return conversationRepository.save(newConversation);

                        // if conversation exist, but got existing id error due to spamming, try again
                    } catch (DataIntegrityViolationException e) {

                        return conversationRepository.findBetweenUsers(initiatorId, participantId, relatedListingId)
                                .orElseThrow(
                                        () -> new RuntimeException("Conversation creation issues: MessagingService"));
                    }
                });

        return conversation;
    }

    public UUID getConversationId(UUID listingId, UUID initiatorId) {

        Conversation conversation = getOrStartConversation(initiatorId, listingId);

        return conversation.getId();
    }
}
