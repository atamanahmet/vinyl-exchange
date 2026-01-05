package com.vinyl.VinylExchange.shared.exception;

import com.vinyl.VinylExchange.user.UserStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(UserStatus oldStatus, UserStatus newStatus) {
        super("Cannot transition from " + oldStatus + " to " + newStatus);
    }

}
