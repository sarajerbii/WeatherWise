import { EnvironmentConfig } from './environment.model';

/**
 * Mode `local` (npm run start:local) :
 *  - authentification simulée (comme le mode démo), sans Keycloak ni passerelle ;
 *  - les routes listées dans `liveRoutes` sont envoyées à de VRAIS microservices
 *    (via le proxy de développement `proxy.conf.json`) ; les autres restent simulées.
 *
 * Aujourd'hui : City & Favorites-Service (http://localhost:8084) et Trip-Planner-Service (http://localhost:8086).
 * Ajoutez ici d'autres routes (ex. '/api/weather') au fur et à mesure que les services existent,
 * et ajoutez la cible correspondante dans `proxy.conf.json`.
 */
export const environment: EnvironmentConfig = {
  production: false,
  demoMode: true,
  liveRoutes: ['/api/cities', '/api/trips'],
  sendUserIdHeader: true,
  gatewayUrl: '', // URLs relatives : le proxy Angular redirige vers les microservices
  keycloak: {
    issuer: 'http://localhost:8180/realms/weatherwise',
    clientId: 'weatherwise-frontend',
    realm: 'weatherwise',
    redirectUri: typeof window !== 'undefined' ? `${window.location.origin}/auth/callback` : 'http://localhost:4200/auth/callback',
    postLogoutRedirectUri: typeof window !== 'undefined' ? window.location.origin : 'http://localhost:4200',
    scope: 'openid profile email roles',
  },
  apiRoutes: {
    weather: '/api/weather',
    forecast: '/api/forecast',
    alerts: '/api/alerts',
    cities: '/api/cities',
    preferences: '/api/preferences',
    trips: '/api/trips',
    insights: '/api/insights',
  },
};
