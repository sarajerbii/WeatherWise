# User-Preferences-Service

Microservice WeatherWise de gestion des **préférences utilisateur** (Membre 4 — Ghassen Denguir).

| | |
|---|---|
| Techno | Java 17 · Spring Boot 3.5.x · Spring Cloud 2025.0.0 · Spring Data JPA · MySQL 8 |
| Port | `8085` |
| Base | `preferences_db` (MySQL, créée automatiquement) |
| Serveur MySQL | `localhost:3306` (serveur MySQL 8 partagé avec `location_db` du City & Favorites-Service) |
| Route passerelle | `/api/preferences/**` |

## État d'avancement

**Étape 1 : CRUD seul.** Pas encore de Keycloak ni de contrôle de rôles.
L'identifiant de l'utilisateur est lu dans l'en-tête HTTP `X-User-Id` (temporaire).
À l'étape sécurité, il sera remplacé par le claim `sub` du JWT (méthode `currentUserId` du contrôleur).

## Lancer

```bash
# Ordre de démarrage (atelier) : MySQL, puis le serveur Eureka (projet « eureka », port 8761), puis ce service
# MySQL doit tourner sur localhost:3306
export DB_USERNAME=root
export DB_PASSWORD=...        # laisser vide si root n'a pas de mot de passe
# Optionnel (valeurs par défaut) : DB_HOST=localhost  DB_PORT=3306  DB_NAME=preferences_db
./mvnw spring-boot:run        # (mvnw.cmd sous Windows)
./mvnw test                   # tests unitaires (sans MySQL)
```

## Base de données

Selon le rapport (§4.2 à §4.4), ce service utilise **MySQL 8** : base `preferences_db` sur le **serveur MySQL partagé, port 3306**
(avec `location_db`). Les ports 33061 et 33062 sont réservés aux serveurs MySQL dédiés à Insights et Keycloak : ne pas les utiliser ici.

## Eureka et Actuator (ateliers)

- Enregistrement dans Eureka sous le nom `USER-PREFERENCES-SERVICE` : http://localhost:8761
- `GET http://localhost:8085/actuator/health` (détail des composants : db, diskSpace, ping…)
- `GET http://localhost:8085/actuator/info`
- `GET http://localhost:8085/actuator/metrics`
- Test de panne (atelier) : arrêter MySQL, rappeler `/actuator/health` (DOWN) et regarder le statut dans le dashboard Eureka, puis redémarrer MySQL.

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
