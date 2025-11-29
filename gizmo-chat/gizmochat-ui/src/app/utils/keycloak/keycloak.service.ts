import {Injectable} from '@angular/core';
import Keycloak from 'keycloak-js';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak: Keycloak | undefined;

  constructor(
    private router: Router
  ) {
  }

  get keycloak(): Keycloak {
    this._keycloak ??= new Keycloak({
      url: 'http://localhost:9090',
      realm: 'gizmo-chat',
      clientId: 'gizmo-chat-app'
    });
    return this._keycloak;
  }

  async init() {
    const authenticated: boolean = await this.keycloak.init({
      onLoad: 'login-required'
    });
  }

  async login() {
    await this.keycloak.login()
  }

  get userId(): string {
    return this.keycloak?.tokenParsed?.sub as string;
  }

  get isTokenValid(): boolean {
    return !this.keycloak.isTokenExpired();
  }

  get fullName(): string {
    return this.keycloak.tokenParsed?.['name'] as string;
  }

  logout() {
    return this.keycloak.logout({redirectUri: 'http://localhost:4200'});
  }

  accountManagement() {
    return this.keycloak.accountManagement();
  }
}
