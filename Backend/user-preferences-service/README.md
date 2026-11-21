# User-Preferences-Service

Microservice WeatherWise de gestion des **préférences utilisateur** (Membre 4 — Ghassen Denguir).

| | |
|---|---|
| Techno | Java 17 · Spring Boot 4.1.2 · Spring Data JPA · MySQL 8 |
| Port | `8085` |
| Base | `preferences_db` (créée automatiquement) |
| Route passerelle | `/api/preferences/**` |

## État d'avancement

**Étape 1 : CRUD seul.** Pas encore de Keycloak ni de contrôle de rôles.
L'identifiant de l'utilisateur est lu dans l'en-tête HTTP `X-User-Id` (temporaire).
À l'étape sécurité, il sera remplacé par le claim `sub` du JWT (méthode `currentUserId` du contrôleur).

## Lancer

```bash
# MySQL doit tourner sur localhost:3306
export DB_USERNAME=root
export DB_PASSWORD=...        # laisser vide si root n'a pas de mot de passe
./mvnw spring-boot:run        # (mvnw.cmd sous Windows)
./mvnw test                   # tests unitaires (sans MySQL)
```

## Contrat d'API

| Méthode | Endpoint | Description | Codes |
|---|---|---|---|
| GET | `/api/preferences/me` | Lire mes préférences (profil créé si absent) | 200 · 401 |
| PUT | `/api/preferences/me` | Remplacer mes préférences | 200 · 400 · 401 |
| PATCH | `/api/preferences/me/notifications` | Activer / couper les notifications | 200 · 400 · 401 |
| GET | `/api/preferences/{userId}` | Lecture inter-services | 200 · 404 (403 à venir) |
| DELETE | `/api/preferences/me` | Réinitialiser mon profil | 204 · 401 |

Valeurs par défaut du profil : `CELSIUS`, `KM_H`, `FR`, `EMAIL`, notifications activées.

Énumérations : `TemperatureUnit` (CELSIUS, FAHRENHEIT) · `WindSpeedUnit` (KM_H, M_S, MPH) ·
`Language` (FR, EN, AR) · `NotificationChannel` (EMAIL).

## Exemples (curl)

```bash
curl -H "X-User-Id: user-1" http://localhost:8085/api/preferences/me

curl -X PUT http://localhost:8085/api/preferences/me \
  -H "X-User-Id: user-1" -H "Content-Type: application/json" \
  -d '{"temperatureUnit":"FAHRENHEIT","windSpeedUnit":"MPH","language":"EN","notificationChannel":"EMAIL","notificationsEnabled":true}'

curl -X PATCH http://localhost:8085/api/preferences/me/notifications \
  -H "X-User-Id: user-1" -H "Content-Type: application/json" \
  -d '{"notificationsEnabled":false}'

curl http://localhost:8085/api/preferences/user-1

curl -X DELETE -H "X-User-Id: user-1" http://localhost:8085/api/preferences/me
```

Réponse d'erreur type :

```json
{ "timestamp": "2026-11-21T13:00:00Z", "status": 400, "error": "Bad Request",
  "message": "language : language est obligatoire", "path": "/api/preferences/me" }
```

## Structure

```
src/main/java/com/weatherwise/user_preferences_service/
├── UserPreferencesServiceApplication.java
├── controller/   UserPreferenceController
├── service/      UserPreferenceService
├── repository/   UserPreferenceRepository
├── entity/       UserPreference
├── dto/          UserPreferenceRequest · NotificationToggleRequest · UserPreferenceResponse
├── enums/        TemperatureUnit · WindSpeedUnit · Language · NotificationChannel
└── exception/    GlobalExceptionHandler · ErrorResponse · PreferenceNotFoundException · MissingUserIdentityException
```

## Prochaines étapes

1. Spring Security (OAuth2 resource server) + Keycloak : `sub` du JWT, rôles USER / ADMIN, 403 sur `GET /{userId}`.
2. Documentation Swagger / OpenAPI.
3. Événement `PreferencesUpdated` (proposé) et Dockerfile / entrée `docker-compose`.
