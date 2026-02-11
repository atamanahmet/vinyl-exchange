package com.vinyl.VinylExchange.exception;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException() {
        super("Conversation not found");
    }

}
