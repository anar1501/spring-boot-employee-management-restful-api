package com.company.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Your Confirmation code already confirmed!")
public class UserAlreadyConfirmedException extends RuntimeException {
    public UserAlreadyConfirmedException() {
        super("Your Confirmation code already confirmed!");
    }
}