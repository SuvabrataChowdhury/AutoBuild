import Keycloak from "keycloak-js";
import { KeycloakIdp, LocalMockIdp, type Idp } from "../auth/idp";

const isKeycloak = import.meta.env.VITE_IDP === "keycloak";

let idp: Idp;

if (isKeycloak) {
    const keycloak = new Keycloak({
        url: import.meta.env.VITE_KC_URL,
        realm: import.meta.env.VITE_KC_REALM,
        clientId: import.meta.env.VITE_KC_CLIENTID
    });

    await keycloak.init({
        onLoad: 'login-required',
        checkLoginIframe: true
    });

    idp = new KeycloakIdp(keycloak);
} else {
    idp = new LocalMockIdp({
        username: import.meta.env.VITE_MOCK_USER,
        email: import.meta.env.VITE_MOCK_USER_EMAIL
    });
}

export {idp};