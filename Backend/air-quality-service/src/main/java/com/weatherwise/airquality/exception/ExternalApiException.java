package com.weatherwise.airquality.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Renvoie le code HTTP 502 (problème avec un service externe)
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String message) {
        super(message);
    }
}
