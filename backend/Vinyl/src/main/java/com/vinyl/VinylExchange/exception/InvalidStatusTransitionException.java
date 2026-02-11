package com.vinyl.VinylExchange.exception;

import com.vinyl.VinylExchange.domain.enums.UserStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(UserStatus oldStatus, UserStatus newStatus) {
        super("Cannot transition from " + oldStatus + " to " + newStatus);
    }

}
