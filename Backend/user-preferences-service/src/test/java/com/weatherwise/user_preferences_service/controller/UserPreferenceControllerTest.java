package com.weatherwise.user_preferences_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.weatherwise.user_preferences_service.dto.UserPreferenceRequest;
import com.weatherwise.user_preferences_service.dto.UserPreferenceResponse;
import com.weatherwise.user_preferences_service.enums.Language;
import com.weatherwise.user_preferences_service.enums.NotificationChannel;
import com.weatherwise.user_preferences_service.enums.TemperatureUnit;
import com.weatherwise.user_preferences_service.enums.WindSpeedUnit;
import com.weatherwise.user_preferences_service.exception.GlobalExceptionHandler;
import com.weatherwise.user_preferences_service.exception.PreferenceNotFoundException;
import com.weatherwise.user_preferences_service.service.UserPreferenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** Test de la couche web seule (MockMvc standalone) : ne nécessite ni MySQL ni contexte Spring complet. */
@ExtendWith(MockitoExtension.class)
class UserPreferenceControllerTest {

    private static final String USER_ID = "user-1";

    private static final String VALID_PUT_BODY = """
            {
              "temperatureUnit": "FAHRENHEIT",
              "windSpeedUnit": "MPH",
              "language": "EN",
              "notificationChannel": "EMAIL",
              "notificationsEnabled": false
            }
            """;

    @Mock
    private UserPreferenceService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserPreferenceController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static UserPreferenceResponse sample() {
        return new UserPreferenceResponse(1L, USER_ID, TemperatureUnit.CELSIUS, WindSpeedUnit.KM_H,
                Language.FR, NotificationChannel.EMAIL, true);
    }

    @Test
    void getMine_returns200WithProfile() throws Exception {
        when(service.getOrCreate(USER_ID)).thenReturn(sample());

        mockMvc.perform(get("/api/preferences/me").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.temperatureUnit").value("CELSIUS"))
                .andExpect(jsonPath("$.language").value("FR"))
                .andExpect(jsonPath("$.notificationsEnabled").value(true));
    }

    @Test
    void getMine_withoutUserHeader_returns401() throws Exception {
        mockMvc.perform(get("/api/preferences/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verifyNoInteractions(service);
    }

    @Test
    void replaceMine_withValidBody_returns200() throws Exception {
        when(service.replace(eq(USER_ID), any(UserPreferenceRequest.class))).thenReturn(sample());

        mockMvc.perform(put("/api/preferences/me")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PUT_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    @Test
    void replaceMine_withMissingField_returns400() throws Exception {
        String bodyWithoutLanguage = """
                {
                  "temperatureUnit": "CELSIUS",
                  "windSpeedUnit": "KM_H",
                  "notificationChannel": "EMAIL",
                  "notificationsEnabled": true
                }
                """;

        mockMvc.perform(put("/api/preferences/me")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithoutLanguage))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(service);
    }

    @Test
    void replaceMine_withUnknownEnumValue_returns400() throws Exception {
        String bodyWithKelvin = VALID_PUT_BODY.replace("FAHRENHEIT", "KELVIN");

        mockMvc.perform(put("/api/preferences/me")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyWithKelvin))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void toggleNotifications_returns200() throws Exception {
        when(service.updateNotifications(USER_ID, false)).thenReturn(sample());

        mockMvc.perform(patch("/api/preferences/me/notifications")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"notificationsEnabled\": false}"))
                .andExpect(status().isOk());

        verify(service).updateNotifications(USER_ID, false);
    }

    @Test
    void toggleNotifications_withEmptyBody_returns400() throws Exception {
        mockMvc.perform(patch("/api/preferences/me/notifications")
                        .header("X-User-Id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void getByUserId_returns200() throws Exception {
        when(service.getByUserId(USER_ID)).thenReturn(sample());

        mockMvc.perform(get("/api/preferences/{userId}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    @Test
    void getByUserId_whenUnknown_returns404() throws Exception {
        when(service.getByUserId("ghost")).thenThrow(new PreferenceNotFoundException("ghost"));

        mockMvc.perform(get("/api/preferences/{userId}", "ghost"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void resetMine_returns204() throws Exception {
        mockMvc.perform(delete("/api/preferences/me").header("X-User-Id", USER_ID))
                .andExpect(status().isNoContent());

        verify(service).reset(USER_ID);
    }
}
