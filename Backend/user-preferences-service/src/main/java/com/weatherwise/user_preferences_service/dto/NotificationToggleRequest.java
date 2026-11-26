package com.weatherwise.user_preferences_service.dto;

import jakarta.validation.constraints.NotNull;

/** Corps de PATCH /api/preferences/me/notifications. */
public record NotificationToggleRequest(
        @NotNull(message = "notificationsEnabled est obligatoire") Boolean notificationsEnabled
) {
}
