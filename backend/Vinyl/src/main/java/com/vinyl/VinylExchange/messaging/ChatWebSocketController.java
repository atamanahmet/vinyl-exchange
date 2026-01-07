package com.vinyl.VinylExchange.messaging;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.vinyl.VinylExchange.messaging.dto.MessageDTO;
import com.vinyl.VinylExchange.messaging.dto.SendMessageRequest;
import com.vinyl.VinylExchange.security.principal.UserPrincipal;

@Controller
public class ChatWebSocketController {
    private final MessagingService messagingService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(
            MessagingService messagingService,
            SimpMessagingTemplate messagingTemplate) {

        this.messagingService = messagingService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.app")
    public void sendMessage(
            @Payload SendMessageRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

    }

}
