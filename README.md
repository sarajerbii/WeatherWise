# 🌦️ WeatherWise

**Plateforme intelligente de suivi météorologique** — architecture microservices (Java / Spring Boot, Python / FastAPI, Angular).

> Projet final AWD — Séance 4 : conception et organisation du dépôt (projet en cours de développement).

---

## 👥 Équipe

| Membre | Nom | Microservice |
|---|---|---|
| 1 | _Nom Prénom_ | Weather-Data-Service |
| 2 | _Nom Prénom_ | Forecast-Service |
| 3 | _Nom Prénom_ | Weather-Alert-Service |
| 4 | _Nom Prénom_ | City & Favorites-Service |
| 5 | _Nom Prénom_ | Climate-Analytics-Service |
| Tous | Équipe | AI-Forecast-Service (Python, commun) |

---

## 💡 Idée et problématique

WeatherWise permet de consulter la météo actuelle et les prévisions, gérer ses villes favorites, recevoir des alertes par e-mail, analyser des données climatiques et obtenir des prédictions grâce à un service intelligent.

**Problématique :** comment concevoir une plateforme météorologique modulaire, fournissant des informations actualisées, personnalisées, capable de détecter les conditions à risque et de proposer des prédictions intelligentes, tout en garantissant l'indépendance des services ?

## ✨ Fonctionnalités principales

- Comptes, authentification et autorisations (JWT, rôles USER / ADMIN)
- Météo actuelle
- Prévisions horaires et journalières
- Recherche de villes et gestion des favoris
- Règles d'alerte (seuils) et notifications par e-mail
- Statistiques et graphiques climatiques
- Prédictions et recommandations (IA)

---

## 🏗️ Architecture

Le frontend Angular communique avec les services via **Spring Cloud Gateway**. Chaque microservice Java possède sa **propre base MySQL** ; les échanges se font en **REST/JSON**. L'Alert-Service envoie les e-mails directement via SMTP.

📎 Diagramme : [`Documentation/Diagrams/`](Documentation/Diagrams/)

### Microservices

| Service | Techno | Base | Responsabilité |
|---|---|---|---|
| Weather-Data-Service | Spring Boot | weather_db | Données météo actuelles + historique, cache |
| Forecast-Service | Spring Boot | forecast_db | Prévisions horaires / journalières |
| Weather-Alert-Service | Spring Boot | alert_db | Règles, détection d'alertes, e-mails (SMTP) |
| Auth-Service | Spring Boot + Security | auth_db | Utilisateurs, JWT, rôles, préférences |
| City & Favorites-Service | Spring Boot | location_db | Recherche de villes, favoris |
| Climate-Analytics-Service | Spring Boot | analytics_db | Statistiques, tendances, comparaisons |
| AI-Forecast-Service | Python + FastAPI | — | Prédictions, anomalies, recommandations |
| API Gateway | Spring Cloud Gateway | — | Point d'entrée unique |

### Technologies

Java · Spring Boot · Spring Data JPA · Spring Security / JWT · Spring Cloud Gateway · MySQL · Python · FastAPI · Angular · Docker Compose · GitHub

---

## 📁 Structure du dépôt

```
WeatherWise/
├── Backend/
│   ├── weather-data-service/
│   ├── forecast-service/
│   ├── weather-alert-service/
│   ├── auth-service/
│   ├── city-favorites-service/
│   ├── climate-analytics-service/
│   ├── ai-forecast-service/
│   ├── api-gateway/
│   └── docker-compose.yml
├── Frontend/
├── Documentation/
│   ├── Presentation/
│   └── Diagrams/
└── README.md
```

---

## 🔀 Organisation Git

- `main` : version stable
- `develop` : intégration
- `feature/<service>` : une branche par microservice (ex. `feature/auth-service`)

Workflow : `feature/*` → Pull Request → `develop` (relecture + tests) → `main`.

Règles : commits explicites, endpoints et JSON définis avant intégration, aucun secret publié (utiliser `.env.example`).

**Gestion de projet :** _lien Trello / GitHub Projects_ (À faire → En cours → En revue → Terminé). Réunion hebdomadaire d'avancement.

---

## 📅 Planning prévisionnel (4 semaines)

| Semaine | Objectifs |
|---|---|
| 1 | Conception, squelettes des services, Auth, Gateway |
| 2 | Services métier (météo, prévisions, villes) + frontend de base |
| 3 | Alertes + e-mails, analytics, service IA |
| 4 | Intégration, tests, documentation, démo |

## ⚠️ Risques principaux

- Limites de l'API météo externe → cache + choix du fournisseur adapté
- Intégration entre services → contrats REST définis tôt
- Conflits Git → branches dédiées + Pull Requests
- Charge inégale dans l'équipe → suivi hebdomadaire et réajustement

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
