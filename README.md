# 🌦️ WeatherWise

**Plateforme intelligente de suivi météorologique** — architecture microservices (Java / Spring Boot, Keycloak, Django, Angular).

> Projet final AWD — Séance 4 : conception et organisation du dépôt (projet en cours de développement).

---

## 👥 Équipe

| Membre | Nom | Microservice |
|---|---|---|
| 1 | _Nom Prénom_ | Weather-Data-Service |
| 2 | _Nom Prénom_ | Forecast-Service |
| 3 | _Nom Prénom_ | Weather-Alert-Service |
| 4 | _Nom Prénom_ | User-Preferences-Service |
| 5 | _Nom Prénom_ | City & Favorites-Service |
| 6 | _Nom Prénom_ | Trip-Planner-Service |
| Tous | Équipe | Keycloak (configuration) + Weather-Insights-Service (Django) |

---

## 💡 Idée et problématique

WeatherWise est une application web de suivi météo : météo actuelle, prévisions, villes favorites, préférences, alertes par e-mail, planification de voyages et recommandations. L'authentification est confiée à **Keycloak**, et un service commun **Django** calcule un indice de confort et produit des recommandations.

**Problématique :** comment concevoir une plateforme météo modulaire, personnalisée, capable de détecter les risques et d'aider à planifier ses déplacements, tout en garantissant l'indépendance des services et la sécurité des accès ?

## ✨ Fonctionnalités principales

- Inscription, authentification et autorisations centralisées (Keycloak, rôles USER / ADMIN)
- Météo actuelle et prévisions horaires / journalières
- Recherche de villes et gestion des favoris
- Règles d'alerte (seuils) et notifications par e-mail avec historique
- Préférences utilisateur (unités, langue, notifications)
- Planification de voyages avec résumé météo par étape
- Recommandations et indice de confort

---

## 🏗️ Architecture

Le frontend Angular obtient un jeton JWT auprès de **Keycloak**, puis envoie ses requêtes à **Spring Cloud Gateway**, qui valide le jeton et route vers les services. Chaque microservice possède sa **propre base MySQL** ; les échanges se font en **REST/JSON**. Le Weather-Alert-Service envoie les e-mails directement via SMTP.

📎 Diagrammes : [`Documentation/Diagrams/`](Documentation/Diagrams/)

### Microservices

| Service | Techno | Base | Port | Responsabilité |
|---|---|---|---|---|
| Weather-Data-Service | Spring Boot | weather_db | 8081 | Données météo actuelles et historique, cache |
| Forecast-Service | Spring Boot | forecast_db | 8082 | Prévisions horaires / journalières |
| Weather-Alert-Service | Spring Boot | alert_db | 8083 | Règles, alertes, notifications e-mail (SMTP) |
| City & Favorites-Service | Spring Boot | location_db | 8084 | Recherche de villes, favoris |
| User-Preferences-Service | Spring Boot + Spring Security | preferences_db | 8085 | Préférences utilisateur |
| Trip-Planner-Service | Spring Boot | planner_db | 8086 | Voyages et étapes, risque météo par étape |
| Weather-Insights-Service | Django + DRF (Gunicorn) | insights_db | 8000 | Indice de confort, niveau de risque, recommandations |
| API Gateway | Spring Cloud Gateway | — | 8080 | Point d'entrée unique, validation des JWT |
| Keycloak | Keycloak (realm `weatherwise`) | keycloak_db | 8180 | Comptes, rôles, OAuth2 / OpenID Connect |

Les ports sont indicatifs. Eureka (8761) et Config Server (8888) sont optionnels.

### Routes exposées par la passerelle

| Route | Service cible | Rôle requis |
|---|---|---|
| `/api/weather/**` | Weather-Data-Service | USER |
| `/api/forecast/**` | Forecast-Service | USER |
| `/api/alerts/**` | Weather-Alert-Service | USER (ADMIN pour la supervision) |
| `/api/cities/**` | City & Favorites-Service | USER |
| `/api/preferences/**` | User-Preferences-Service | USER |
| `/api/trips/**` | Trip-Planner-Service | USER |
| `/api/insights/**` | Weather-Insights-Service | USER (ADMIN pour les règles) |

### Sécurité

1. Le frontend redirige l'utilisateur vers Keycloak (Authorization Code + PKCE).
2. Keycloak renvoie un jeton JWT.
3. Le frontend appelle la passerelle avec `Authorization: Bearer <token>`.
4. La passerelle valide la signature et la durée du jeton (JWKS).
5. Chaque microservice contrôle le rôle (USER / ADMIN) et utilise l'identifiant `sub` du jeton.

### Technologies

Java · Spring Boot · Spring Data JPA · Spring Security (resource server) · Keycloak · Spring Cloud Gateway · MySQL · Django REST Framework · Angular · Spring Mail / MailHog · Swagger / OpenAPI · Docker Compose · GitHub

---

## 📁 Structure du dépôt

```
WeatherWise/
├── Backend/
│   ├── weather-data-service/
│   ├── forecast-service/
│   ├── weather-alert-service/
│   ├── city-favorites-service/
│   ├── user-preferences-service/
│   ├── trip-planner-service/
│   ├── weather-insights-service/   (Django)
│   ├── api-gateway/
│   ├── keycloak/                   (export du realm, sans secrets)
│   ├── eureka-server/              (si retenu)
│   ├── config-server/              (si retenu)
│   └── docker-compose.yml
├── Frontend/
├── Documentation/
│   ├── Presentation/
│   └── Diagrams/
└── README.md
```

---

## 🔀 Organisation Git et gestion de projet

- `main` : version stable
- `develop` : intégration des fonctionnalités
- `feature/<service>` : une branche par service (ex. `feature/trip-planner-service`), plus `feature/keycloak-config`

Workflow : `feature/*` → Pull Request → `develop` (relecture + tests) → `main`.

Règles : issues pour les tâches importantes, endpoints et JSON définis avant intégration, commits explicites, aucun secret publié (mots de passe, clés API, secrets Keycloak, identifiants SMTP), fichier `.env.example` fourni.

**Suivi :** _lien Trello / Jira / GitHub Projects_ (À faire → En cours → En revue → Terminé), réunion hebdomadaire.

---

## 📅 Planning prévisionnel (4 semaines)

| Période | Travaux prévus |
|---|---|
| Semaine 1 | Architecture, contrats REST, diagrammes UML, installation de Keycloak, dépôt GitHub |
| Semaine 2 | Microservices Java : entités, repositories, services, contrôleurs, tests |
| Semaine 3 | Service Django, intégration Keycloak, notifications, passerelle, premières pages frontend |
| Semaine 4 | Tests d'intégration, Docker Compose, documentation, démonstration |

Parcours prioritaire : s'authentifier → consulter la météo → afficher les prévisions → configurer une alerte → recevoir un e-mail.

## ⚠️ Risques principaux

| Risque | Solution prévue |
|---|---|
| Retard d'un service | Livrables intermédiaires, suivi hebdomadaire |
| Incompatibilité entre API | Contrats REST et schémas JSON définis à l'avance |
| Indisponibilité / quotas de l'API météo | Cache, données de test, limitation des appels |
| Mauvaise configuration de Keycloak | Realm exporté et versionné, tests des autorisations |
| Conflits Git | Branches dédiées, Pull Requests |
| Difficulté de lancement global | Docker Compose, intégration progressive |
| Échec d'envoi d'une notification | Statut enregistré dans `alert_db`, nouvelle tentative |

---

## 🚀 Lancement

_À compléter au fil du développement_ :

```bash
cd Backend
docker compose up --build
```

## 📄 Documentation

- Présentation séance 4 : [`Documentation/Presentation/`](Documentation/Presentation/)
- Diagrammes : [`Documentation/Diagrams/`](Documentation/Diagrams/)
