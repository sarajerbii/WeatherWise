package com.weatherwise.trip.exception;

public class StopNotFoundException extends RuntimeException {
    public StopNotFoundException(Long id) { super("Étape introuvable : " + id); }
}
