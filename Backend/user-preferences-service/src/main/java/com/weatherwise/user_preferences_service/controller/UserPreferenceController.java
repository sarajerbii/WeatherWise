package com.weatherwise.user_preferences_service.controller;

import com.weatherwise.user_preferences_service.dto.NotificationToggleRequest;
import com.weatherwise.user_preferences_service.dto.UserPreferenceRequest;
import com.weatherwise.user_preferences_service.dto.UserPreferenceResponse;
import com.weatherwise.user_preferences_service.exception.MissingUserIdentityException;
import com.weatherwise.user_preferences_service.service.UserPreferenceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST des préférences utilisateur (contrat : /api/preferences/**).
 * <p>
 * ÉTAPE 1 (CRUD seul) : l'identifiant de l'utilisateur est lu dans l'en-tête {@code X-User-Id}.
 * À l'étape Keycloak, il sera remplacé par le claim {@code sub} du JWT
 * (voir {@link #currentUserId(String)}, seul endroit à modifier).
 */
@RestController
@RequestMapping("/api/preferences")
public class UserPreferenceController {

    static final String USER_ID_HEADER = "X-User-Id";

    private final UserPreferenceService service;

    public UserPreferenceController(UserPreferenceService service) {
        this.service = service;
    }

    /** GET /api/preferences/me : lire mes préférences (profil créé si absent). 200 · 401 */
    @GetMapping("/me")
    public UserPreferenceResponse getMine(@RequestHeader(value = USER_ID_HEADER, required = false) String header) {
        return service.getOrCreate(currentUserId(header));
    }

    /** PUT /api/preferences/me : remplacer mes préférences. 200 · 400 · 401 */
    @PutMapping("/me")
    public UserPreferenceResponse replaceMine(
            @RequestHeader(value = USER_ID_HEADER, required = false) String header,
            @Valid @RequestBody UserPreferenceRequest request) {
        return service.replace(currentUserId(header), request);
    }

    /** PATCH /api/preferences/me/notifications : activer / couper les notifications. 200 · 400 · 401 */
    @PatchMapping("/me/notifications")
    public UserPreferenceResponse toggleNotifications(
            @RequestHeader(value = USER_ID_HEADER, required = false) String header,
            @Valid @RequestBody NotificationToggleRequest request) {
        return service.updateNotifications(currentUserId(header), request.notificationsEnabled());
    }

    /**
     * GET /api/preferences/{userId} : lecture inter-services (utilisée par le Weather-Alert-Service).
     * 200 · 404. TODO (étape sécurité) : réserver à ADMIN / service, 403 sinon.
     */
    @GetMapping("/{userId}")
    public UserPreferenceResponse getByUserId(@PathVariable String userId) {
        return service.getByUserId(userId);
    }

    /** DELETE /api/preferences/me : réinitialiser mon profil. 204 · 401 */
    @DeleteMapping("/me")
    public ResponseEntity<Void> resetMine(@RequestHeader(value = USER_ID_HEADER, required = false) String header) {
        service.reset(currentUserId(header));
        return ResponseEntity.noContent().build();
    }

    /**
     * Identifiant de l'utilisateur courant.
     * Étape 1 : en-tête X-User-Id. Étape Keycloak : jwt.getSubject().
     */
    private String currentUserId(String headerValue) {
        if (headerValue == null || headerValue.isBlank()) {
            throw new MissingUserIdentityException("Identifiant utilisateur manquant (en-tête " + USER_ID_HEADER + ")");
        }
        return headerValue.trim();
    }
}
