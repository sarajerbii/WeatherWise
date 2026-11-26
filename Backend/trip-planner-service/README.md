# Trip-Planner-Service — WeatherWise

Microservice de planification de voyages selon la météo (Java 17, Spring Boot 3.3, Spring Data JPA, PostgreSQL `planner_db`, port **8086**).
Sécurisé par Keycloak (resource server JWT, realm `weatherwise`).

## Fonctionnalités
- CRUD des voyages d'un utilisateur (titre, description, dates, statut)
- CRUD des étapes d'un voyage (ville, date d'arrivée, date de départ)
- Résumé météo et **niveau de risque** (LOW / MODERATE / HIGH) par étape, via le Weather-Data-Service
- Recommandations par étape via le Weather-Insights-Service (Django)
- Un voyage n'est accessible que par son propriétaire (claim `sub` du JWT) ; sinon 404

## Lancer
```bash
# 1. PostgreSQL
docker compose up -d postgres
# 2. Service
mvn spring-boot:run
# Tests unitaires
mvn test
```
Swagger UI : http://localhost:8086/swagger-ui.html

## Endpoints (rôle USER ou ADMIN)
| Méthode | URL | Description |
|---|---|---|
| POST | `/api/trips` | Créer un voyage |
| GET | `/api/trips` | Lister mes voyages |
| GET | `/api/trips/{id}` | Détail d'un voyage (avec étapes et risque global) |
| PUT | `/api/trips/{id}` | Modifier un voyage |
| DELETE | `/api/trips/{id}` | Supprimer un voyage |
| POST | `/api/trips/{id}/stops` | Ajouter une étape |
| GET | `/api/trips/{id}/stops` | Lister les étapes |
| GET | `/api/trips/{id}/stops/{stopId}` | Détail d'une étape |
| PUT | `/api/trips/{id}/stops/{stopId}` | Modifier une étape |
| DELETE | `/api/trips/{id}/stops/{stopId}` | Supprimer une étape |
| GET | `/api/trips/{id}/weather` | Mettre à jour/consulter météo et risque par étape |
| GET | `/api/trips/{id}/recommendations` | Recommandations par étape |

Via la passerelle : `http://localhost:8080/api/trips/**` (route déjà prévue dans le rapport).

## Règles de gestion
- `endDate >= startDate` pour un voyage ; `departureDate >= arrivalDate` pour une étape
- Les dates d'une étape doivent être comprises dans la période du voyage
- Modifier un voyage est refusé si une étape sortirait de la nouvelle période
- Modifier une étape réinitialise son résumé météo et son risque
- Niveau de risque : seuils configurables dans `application.yml` (`weatherwise.risk.*`)

## Contrats supposés avec les autres services (à aligner avec l'équipe)
**Weather-Data-Service** : `GET /api/weather/forecast?city=&from=&to=&granularity=DAILY` →
```json
[{ "temperatureMin": 12.0, "temperatureMax": 19.5, "precipitationProbability": 40.0,
   "precipitationAmount": 2.1, "windSpeed": 28.0, "condition": "Nuageux" }]
```
**Weather-Insights-Service** : `POST /api/insights/recommendations` avec
`{ "city", "temperature", "windSpeed", "precipitationProbability" }` →
```json
{ "comfortIndex": 7.5, "riskLevel": "LOW",
  "recommendations": [{ "category": "UMBRELLA", "message": "Prévoir un parapluie" }] }
```
Si l'un des services est indisponible, l'endpoint répond quand même (200) avec `forecastAvailable=false` et un message,
sans faire échouer tout le voyage. Les champs inconnus sont ignorés ; adapter `WeatherDataClient`, `InsightsClient`
et `ForecastDto` si le format réel diffère.

## Structure
```
src/main/java/com/weatherwise/trip/
  config/      SecurityConfig, OpenApiConfig, AppProperties
  controller/  TripController, TripStopController
  service/     TripService, TripStopService, TripWeatherService, RiskCalculator, TripMapper
  client/      WeatherDataClient, InsightsClient
  entity/      Trip, TripStop, TripStatus, RiskLevel
  repository/  TripRepository, TripStopRepository
  dto/ exception/
```
Branche Git : `feature/trip-planner-service`.
