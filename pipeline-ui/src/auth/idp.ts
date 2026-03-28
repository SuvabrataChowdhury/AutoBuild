import type { UserInfo } from "../types/user.types";
import Keycloak, { type KeycloakUserInfo } from "keycloak-js";

interface Idp {
    login(): Promise<void>,
    logout(): Promise<void>,
    getToken(): string | undefined,
    isAuthenticated(): boolean,
    getUserInfo(): Promise<UserInfo>
}

class KeycloakIdp implements Idp {
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