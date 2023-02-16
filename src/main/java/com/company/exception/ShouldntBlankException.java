package com.company.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "New password or repeat new password should not blank!")
public class ShouldntBlankException extends RuntimeException {
    public ShouldntBlankException() {
        super("New password or repeat new password should not blank!");
    }
}
