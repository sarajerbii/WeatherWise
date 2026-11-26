package com.weatherwise.user_preferences_service.entity;

import com.weatherwise.user_preferences_service.enums.Language;
import com.weatherwise.user_preferences_service.enums.NotificationChannel;
import com.weatherwise.user_preferences_service.enums.TemperatureUnit;
import com.weatherwise.user_preferences_service.enums.WindSpeedUnit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Préférences d'un utilisateur.
 * <p>
 * L'utilisateur n'est pas stocké ici : il est référencé uniquement par son identifiant
 * Keycloak (claim {@code sub} du JWT), sans clé étrangère entre bases et sans mot de passe.
 */
@Entity
@Table(
        name = "user_preferences",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_preferences_user_id", columnNames = "user_id")
)
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identifiant Keycloak de l'utilisateur (claim "sub"). */
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperature_unit", nullable = false, length = 20)
    private TemperatureUnit temperatureUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "wind_speed_unit", nullable = false, length = 20)
    private WindSpeedUnit windSpeedUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 10)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_channel", nullable = false, length = 20)
    private NotificationChannel notificationChannel;

    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled;

    protected UserPreference() {
        // requis par JPA
    }

    /** Profil par défaut, créé à la première connexion de l'utilisateur. */
    public static UserPreference defaultsFor(String userId) {
        UserPreference p = new UserPreference();
        p.userId = userId;
        p.temperatureUnit = TemperatureUnit.CELSIUS;
        p.windSpeedUnit = WindSpeedUnit.KM_H;
        p.language = Language.FR;
        p.notificationChannel = NotificationChannel.EMAIL;
        p.notificationsEnabled = true;
        return p;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public TemperatureUnit getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(TemperatureUnit temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public WindSpeedUnit getWindSpeedUnit() {
        return windSpeedUnit;
    }

    public void setWindSpeedUnit(WindSpeedUnit windSpeedUnit) {
        this.windSpeedUnit = windSpeedUnit;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}
