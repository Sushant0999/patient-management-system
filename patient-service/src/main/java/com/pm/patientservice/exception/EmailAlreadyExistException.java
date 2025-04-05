package com.pm.patientservice.exception;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailAlreadyExistException extends RuntimeException {

    public EmailAlreadyExistException(@NotBlank(message = "Email is required") @Email(message = "email should be valid") String s) {
       super(s);
    }
}
