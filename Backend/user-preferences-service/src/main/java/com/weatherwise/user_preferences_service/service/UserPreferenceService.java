package com.weatherwise.user_preferences_service.service;

import com.weatherwise.user_preferences_service.dto.UserPreferenceRequest;
import com.weatherwise.user_preferences_service.dto.UserPreferenceResponse;
import com.weatherwise.user_preferences_service.entity.UserPreference;
import com.weatherwise.user_preferences_service.exception.PreferenceNotFoundException;
import com.weatherwise.user_preferences_service.repository.UserPreferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logique métier du CRUD des préférences.
 * Chaque méthode reçoit l'identifiant Keycloak de l'utilisateur ({@code userId}).
 */
@Service
@Transactional
public class UserPreferenceService {

    private final UserPreferenceRepository repository;

    public UserPreferenceService(UserPreferenceRepository repository) {
        this.repository = repository;
    }

    /** READ (moi) : retourne le profil, créé avec les valeurs par défaut s'il n'existe pas encore. */
    public UserPreferenceResponse getOrCreate(String userId) {
        return UserPreferenceResponse.from(findOrCreate(userId));
    }

    /** READ (inter-services) : retourne le profil d'un utilisateur, 404 s'il n'existe pas. */
    @Transactional(readOnly = true)
    public UserPreferenceResponse getByUserId(String userId) {
        UserPreference preference = repository.findByUserId(userId)
                .orElseThrow(() -> new PreferenceNotFoundException(userId));
        return UserPreferenceResponse.from(preference);
    }

    /** UPDATE (PUT) : remplace toutes les préférences ; crée le profil s'il n'existe pas. */
    public UserPreferenceResponse replace(String userId, UserPreferenceRequest request) {
        UserPreference preference = findOrCreate(userId);
        preference.setTemperatureUnit(request.temperatureUnit());
        preference.setWindSpeedUnit(request.windSpeedUnit());
        preference.setLanguage(request.language());
        preference.setNotificationChannel(request.notificationChannel());
        preference.setNotificationsEnabled(request.notificationsEnabled());
        return UserPreferenceResponse.from(repository.save(preference));
    }

    /** UPDATE (PATCH) : active ou coupe uniquement les notifications. */
    public UserPreferenceResponse updateNotifications(String userId, boolean enabled) {
        UserPreference preference = findOrCreate(userId);
        preference.setNotificationsEnabled(enabled);
        return UserPreferenceResponse.from(repository.save(preference));
    }

    /**
     * DELETE : réinitialise le profil en le supprimant. Le prochain GET /me recréera
     * un profil par défaut. Sans effet (et sans erreur) si le profil n'existe pas.
     */
    public void reset(String userId) {
        repository.findByUserId(userId).ifPresent(repository::delete);
    }

    private UserPreference findOrCreate(String userId) {
        return repository.findByUserId(userId)
                .orElseGet(() -> repository.save(UserPreference.defaultsFor(userId)));
    }
}
