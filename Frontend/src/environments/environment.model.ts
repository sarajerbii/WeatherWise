export interface EnvironmentConfig {
  production: boolean;
  demoMode: boolean;
  liveRoutes: string[];
  sendUserIdHeader: boolean;
  gatewayUrl: string;
  keycloak: {
    issuer: string;
    clientId: string;
    realm: string;
    redirectUri: string;
    postLogoutRedirectUri: string;
    scope: string;
  };
  apiRoutes: Record<string, string>;
}

