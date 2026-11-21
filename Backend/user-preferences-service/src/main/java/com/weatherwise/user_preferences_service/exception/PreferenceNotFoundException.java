package com.weatherwise.user_preferences_service.exception;

public class PreferenceNotFoundException extends RuntimeException {

    public PreferenceNotFoundException(String userId) {
        super("Aucune préférence trouvée pour l'utilisateur " + userId);
    }
}
