package com.company.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Password repeat is not same!")
public class ChangePasswordException extends RuntimeException {
    public ChangePasswordException() {
        super("Password repeat is not same!");
    }
}
