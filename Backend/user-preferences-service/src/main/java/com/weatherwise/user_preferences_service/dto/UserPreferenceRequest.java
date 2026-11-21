package com.weatherwise.user_preferences_service.dto;

import com.weatherwise.user_preferences_service.enums.Language;
import com.weatherwise.user_preferences_service.enums.NotificationChannel;
import com.weatherwise.user_preferences_service.enums.TemperatureUnit;
import com.weatherwise.user_preferences_service.enums.WindSpeedUnit;
import jakarta.validation.constraints.NotNull;

/** Corps de PUT /api/preferences/me : remplacement complet des préférences. */
public record UserPreferenceRequest(
        @NotNull(message = "temperatureUnit est obligatoire") TemperatureUnit temperatureUnit,
        @NotNull(message = "windSpeedUnit est obligatoire") WindSpeedUnit windSpeedUnit,
        @NotNull(message = "language est obligatoire") Language language,
        @NotNull(message = "notificationChannel est obligatoire") NotificationChannel notificationChannel,
        @NotNull(message = "notificationsEnabled est obligatoire") Boolean notificationsEnabled
) {
}
