package com.greenfoxacademy.islandfoxtribes.utilities;

import com.greenfoxacademy.islandfoxtribes.models.errors.Errors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Utilities {

    public static Boolean isStringEmpty(String string) {
        return ("".equals(string) || string == null);
    }

    public static ResponseEntity createBadRequestResponse(String message) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Errors(message));
    }

    public static ResponseEntity createErrorResponse(HttpStatus status, String message) {

        return ResponseEntity.status(status).body(new Errors(message));

    }
}
