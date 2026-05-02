import type { UserInfo } from "../types/user.types";
import Keycloak, { type KeycloakUserInfo } from "keycloak-js";

export interface Idp {
    login(): Promise<void>,
    logout(): Promise<void>,
    getToken(): string | undefined,
    isAuthenticated(): boolean,
    getUserInfo(): Promise<UserInfo>
}

export class KeycloakIdp implements Idp {
    private keycloakInstance: Keycloak;

    constructor(keycloak: Keycloak) {
        this.keycloakInstance = keycloak;
    }

    async login(): Promise<void> {
        await this.keycloakInstance.login();
    }

    async logout(): Promise<void> {
        await this.keycloakInstance.logout();
    }

    getToken(): string | undefined {
        return this.keycloakInstance.token;
    }

    isAuthenticated(): boolean {
        return this.keycloakInstance.authenticated;
    }

    async getUserInfo(): Promise<UserInfo> {
        const keycloakUserInfo: KeycloakUserInfo = await this.keycloakInstance.loadUserInfo();

        const userInfo: UserInfo = {
            username: keycloakUserInfo.preferred_username,
            email: keycloakUserInfo.email
        }

        return userInfo;
    }
}

export class LocalMockIdp implements Idp {
    private userInfo: UserInfo;

    constructor(userInfo: UserInfo) {
        this.userInfo = userInfo;
    }

    async login(): Promise<void> {
        
    }

    async logout(): Promise<void> {
        
    }

    getToken(): string | undefined {
        return "dummy";
    }

    isAuthenticated(): boolean {
        return true;
    }

    async getUserInfo(): Promise<UserInfo> {
        return this.userInfo;
    }
}