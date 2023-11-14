package com.qcomit.EmployeemanegementsystemAPI.service.exceptions;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(@NotEmpty(message = "Username cannot be empty") String message) {
        super(message);
    }
}
