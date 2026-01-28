package com.vinyl.VinylExchange.messaging;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vinyl.VinylExchange.listing.ListingService;
import com.vinyl.VinylExchange.messaging.dto.ConversationDTO;
import com.vinyl.VinylExchange.messaging.dto.MessageDTO;
import com.vinyl.VinylExchange.messaging.dto.MessagePageResponse;
import com.vinyl.VinylExchange.messaging.dto.SendMessageRequest;
import com.vinyl.VinylExchange.messaging.dto.StartConversationRequest;
import com.vinyl.VinylExchange.messaging.dto.UnreadCountResponse;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

@Controller
@RequestMapping("/api/messages")
public class MessagingController {

        private final MessagingService messagingService;

        public MessagingController(MessagingService messagingService, ListingService listingService) {
                this.messagingService = messagingService;
        }

        @PostMapping
        public ResponseEntity<?> sendMessage(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @RequestBody SendMessageRequest request) {

                MessageDTO messageDTO = messagingService.sendMessage(
                                userPrincipal.getId(),
                                userPrincipal.getUsername(),
                                request.getConversationId(),
                                request.getRelatedListingId(),
                                request.getContent(), null);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(messageDTO);
        }

        @PostMapping("/start")
        public ResponseEntity<?> startConversation(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @RequestBody StartConversationRequest request) {

                // only when starting convo
                ConversationDTO conversationDTO = messagingService.startConversation(
                                userPrincipal.getId(),
                                userPrincipal.getUsername(),
                                request.relatedListingId());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(conversationDTO);
        }

        @GetMapping("/conversation/{converstaionId}")
        public ResponseEntity<?> getMessagesByConversationId(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PathVariable(name = "converstaionId") UUID converstaionId
        // @RequestParam(defaultValue = "0") int page,
        // @RequestParam(defaultValue = "50") int size
        ) {
                // checking if user participant with userId
                ConversationDTO conversationDTO = messagingService.getConversationDTO(converstaionId,
                                userPrincipal.getId());

                Page<MessageDTO> messagePage = messagingService.getMessages(userPrincipal.getId(), converstaionId, 0,
                                50);

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(new MessagePageResponse(
                                                conversationDTO,
                                                messagePage));
        }

        @GetMapping("/conversations")
        public ResponseEntity<?> getConversations(@AuthenticationPrincipal UserPrincipal userPrincipal) {

                List<ConversationDTO> conversationsDTO = messagingService.getUserConversations(userPrincipal.getId());

                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(conversationsDTO);
        }

        @DeleteMapping("/conversations")
        public ResponseEntity<?> deleteMyConversations(
                        @AuthenticationPrincipal UserPrincipal userPrincipal) {

                messagingService.deleteMyConversations(userPrincipal.getId());

                return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .build();
        }

        @DeleteMapping("/conversation/{conversationId}")
        public ResponseEntity<?> deleteThisConversation(
                        @AuthenticationPrincipal UserPrincipal userPrincipal,
                        @PathVariable(name = "conversationId", required = true) UUID conversationId) {

                messagingService.deleteThisConversation(userPrincipal.getId(), conversationId);

                return ResponseEntity
                                .status(HttpStatus.NO_CONTENT)
                                .build();
        }

        @GetMapping("/unread")
        public ResponseEntity<UnreadCountResponse> getUnreadCount(
                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
                Long count = messagingService.getUserTotalUnreadCount(userPrincipal.getId());
                return ResponseEntity.ok(new UnreadCountResponse(count));
        }
}
