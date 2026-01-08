package com.vinyl.VinylExchange.shared.exception;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException() {
        super("Conversation not found");
    }

}
