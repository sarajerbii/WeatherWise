package com.weatherwise.user_preferences_service.exception;

/** Corps JSON standard des erreurs renvoyées par le service. */
public record ErrorResponse(String timestamp, int status, String error, String message, String path) {
}
