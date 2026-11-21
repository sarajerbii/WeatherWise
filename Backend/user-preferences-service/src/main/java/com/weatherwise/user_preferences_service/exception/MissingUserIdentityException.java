package com.weatherwise.user_preferences_service.exception;

/** Aucun identifiant utilisateur n'a pu être déterminé pour la requête (HTTP 401). */
public class MissingUserIdentityException extends RuntimeException {

    public MissingUserIdentityException(String message) {
        super(message);
    }
}
