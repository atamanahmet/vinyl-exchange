package com.vinyl.VinylExchange.messaging;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vinyl.VinylExchange.messaging.dto.MessageDTO;
import com.vinyl.VinylExchange.messaging.dto.SendMessageRequest;

import com.vinyl.VinylExchange.security.principal.UserPrincipal;

@Controller
@RequestMapping("/api/messages")
public class MessagingController {

    private final MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody SendMessageRequest request) {

        System.out.println("message request: " + request.getContent() + "listingId: " +
                request.getRelatedListingId());

        MessageDTO messageDTO = messagingService.sendMessage(
                userPrincipal.getId(),
                request.getRelatedListingId(),
                request.getContent(), null);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messageDTO);
    }

    @GetMapping("/conversation/{listingId}")
    public ResponseEntity<?> getMessages(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable(name = "listingId") UUID listingId
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "50") int size
    ) {

        Page<MessageDTO> messagePage = messagingService.getMessages(userPrincipal.getId(), listingId, 0, 50);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messagePage);

    }

}
