package com.weatherwise.user_preferences_service.dto;

import com.weatherwise.user_preferences_service.entity.UserPreference;
import com.weatherwise.user_preferences_service.enums.Language;
import com.weatherwise.user_preferences_service.enums.NotificationChannel;
import com.weatherwise.user_preferences_service.enums.TemperatureUnit;
import com.weatherwise.user_preferences_service.enums.WindSpeedUnit;

/** Représentation JSON d'un profil de préférences. */
public record UserPreferenceResponse(
        Long id,
        String userId,
        TemperatureUnit temperatureUnit,
        WindSpeedUnit windSpeedUnit,
        Language language,
        NotificationChannel notificationChannel,
        boolean notificationsEnabled
) {

    public static UserPreferenceResponse from(UserPreference p) {
        return new UserPreferenceResponse(
                p.getId(),
                p.getUserId(),
                p.getTemperatureUnit(),
                p.getWindSpeedUnit(),
                p.getLanguage(),
                p.getNotificationChannel(),
                p.isNotificationsEnabled()
        );
    }
}
