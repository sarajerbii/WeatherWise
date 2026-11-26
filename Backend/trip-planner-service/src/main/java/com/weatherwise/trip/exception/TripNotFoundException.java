package com.weatherwise.trip.exception;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(Long id) { super("Voyage introuvable : " + id); }
}
