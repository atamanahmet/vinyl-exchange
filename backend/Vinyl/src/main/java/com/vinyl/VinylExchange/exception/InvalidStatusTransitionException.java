package com.vinyl.VinylExchange.exception;

import com.vinyl.VinylExchange.domain.entity.UserStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(UserStatus oldStatus, UserStatus newStatus) {
        super("Cannot transition from " + oldStatus + " to " + newStatus);
    }

}
