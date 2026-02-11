// package com.vinyl.VinylExchange.messaging;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.Payload;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Controller;

// import com.vinyl.VinylExchange.dto.MessageDTO;
// import com.vinyl.VinylExchange.dto.SendMessageRequest;

// @Controller
// public class ChatWebSocketController {

// Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);

// private final MessagingService messagingService;
// private final SimpMessagingTemplate messagingTemplate;

// public ChatWebSocketController(
// MessagingService messagingService,
// SimpMessagingTemplate messagingTemplate) {

// this.messagingService = messagingService;
// this.messagingTemplate = messagingTemplate;

// logger.info("ChatWebSocket ready.");
// }

// @MessageMapping("/chat.app")
// public void sendMessage(
// @Payload SendMessageRequest request) {

// }

// }
