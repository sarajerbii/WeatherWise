package com.weatherwise.user_preferences_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.weatherwise.user_preferences_service.dto.UserPreferenceRequest;
import com.weatherwise.user_preferences_service.dto.UserPreferenceResponse;
import com.weatherwise.user_preferences_service.entity.UserPreference;
import com.weatherwise.user_preferences_service.enums.Language;
import com.weatherwise.user_preferences_service.enums.NotificationChannel;
import com.weatherwise.user_preferences_service.enums.TemperatureUnit;
import com.weatherwise.user_preferences_service.enums.WindSpeedUnit;
import com.weatherwise.user_preferences_service.exception.PreferenceNotFoundException;
import com.weatherwise.user_preferences_service.repository.UserPreferenceRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceTest {

    private static final String USER_ID = "user-1";

    @Mock
    private UserPreferenceRepository repository;

    @InjectMocks
    private UserPreferenceService service;

    @Test
    void getOrCreate_whenAbsent_createsDefaultProfile() {
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(repository.save(any(UserPreference.class))).thenAnswer(inv -> inv.getArgument(0));

        UserPreferenceResponse result = service.getOrCreate(USER_ID);

        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.temperatureUnit()).isEqualTo(TemperatureUnit.CELSIUS);
        assertThat(result.windSpeedUnit()).isEqualTo(WindSpeedUnit.KM_H);
        assertThat(result.language()).isEqualTo(Language.FR);
        assertThat(result.notificationChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(result.notificationsEnabled()).isTrue();
        verify(repository).save(any(UserPreference.class));
    }

    @Test
    void getOrCreate_whenExists_returnsItWithoutSaving() {
        UserPreference existing = UserPreference.defaultsFor(USER_ID);
        existing.setLanguage(Language.EN);
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        UserPreferenceResponse result = service.getOrCreate(USER_ID);

        assertThat(result.language()).isEqualTo(Language.EN);
        verify(repository, never()).save(any(UserPreference.class));
    }

    @Test
    void getByUserId_whenAbsent_throwsNotFound() {
        when(repository.findByUserId("ghost")).thenReturn(Optional.empty());

        assertThrows(PreferenceNotFoundException.class, () -> service.getByUserId("ghost"));
    }

    @Test
    void getByUserId_whenExists_returnsProfile() {
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.of(UserPreference.defaultsFor(USER_ID)));

        UserPreferenceResponse result = service.getByUserId(USER_ID);

        assertThat(result.userId()).isEqualTo(USER_ID);
    }

    @Test
    void replace_updatesAllFields() {
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.of(UserPreference.defaultsFor(USER_ID)));
        when(repository.save(any(UserPreference.class))).thenAnswer(inv -> inv.getArgument(0));
        UserPreferenceRequest request = new UserPreferenceRequest(
                TemperatureUnit.FAHRENHEIT, WindSpeedUnit.MPH, Language.AR, NotificationChannel.EMAIL, false);

        UserPreferenceResponse result = service.replace(USER_ID, request);

        assertThat(result.temperatureUnit()).isEqualTo(TemperatureUnit.FAHRENHEIT);
        assertThat(result.windSpeedUnit()).isEqualTo(WindSpeedUnit.MPH);
        assertThat(result.language()).isEqualTo(Language.AR);
        assertThat(result.notificationsEnabled()).isFalse();
    }

    @Test
    void replace_whenAbsent_createsThenReplaces() {
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(repository.save(any(UserPreference.class))).thenAnswer(inv -> inv.getArgument(0));
        UserPreferenceRequest request = new UserPreferenceRequest(
                TemperatureUnit.FAHRENHEIT, WindSpeedUnit.M_S, Language.EN, NotificationChannel.EMAIL, true);

        UserPreferenceResponse result = service.replace(USER_ID, request);

        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.temperatureUnit()).isEqualTo(TemperatureUnit.FAHRENHEIT);
        assertThat(result.windSpeedUnit()).isEqualTo(WindSpeedUnit.M_S);
        assertThat(result.language()).isEqualTo(Language.EN);
    }

    @Test
    void updateNotifications_changesOnlyTheNotificationFlag() {
        UserPreference existing = UserPreference.defaultsFor(USER_ID);
        existing.setLanguage(Language.EN);
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));
        when(repository.save(any(UserPreference.class))).thenAnswer(inv -> inv.getArgument(0));

        UserPreferenceResponse result = service.updateNotifications(USER_ID, false);

        assertThat(result.notificationsEnabled()).isFalse();
        assertThat(result.language()).isEqualTo(Language.EN);
    }

    @Test
    void reset_whenExists_deletesProfile() {
        UserPreference existing = UserPreference.defaultsFor(USER_ID);
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.of(existing));

        service.reset(USER_ID);

        verify(repository).delete(existing);
    }

    @Test
    void reset_whenAbsent_doesNothing() {
        when(repository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        service.reset(USER_ID);

        verify(repository, never()).delete(any(UserPreference.class));
    }
}
