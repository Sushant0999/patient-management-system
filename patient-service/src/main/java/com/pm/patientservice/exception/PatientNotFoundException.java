package com.pm.patientservice.exception;

import java.util.UUID;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String s, UUID patientId) {
        super(s);
    }
}
