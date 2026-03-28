import Keycloak from "keycloak-js";
import { createContext, useContext, useState } from "react";

const keycloak = new Keycloak({
  url: 'http://localhost:8180', 
  realm: 'SpringBootRealm',
  clientId: 'pipeline-app'
});

await keycloak.init({
  onLoad: 'login-required', 
  checkLoginIframe: true 
});

type AuthContextType = {
  // token: string | null;
  login: (token: string) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | null>(null);

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function AuthProvider({ children }: any) {
  
  const login = async() => {
    await keycloak.login();
  };

  const logout = async () => {
    await keycloak.logout();
  };

  return (
    <AuthContext.Provider value={{login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

const useAuth = () => useContext(AuthContext)!;

export {keycloak, useAuth};
