import Keycloak from "keycloak-js";
import { createContext, useContext, useState } from "react";

export const keycloak = new Keycloak({
  url: 'http://localhost:8180',  // Your Keycloak server
  realm: 'SpringBootRealm',
  clientId: 'pipeline-ui'
});

// const authenticated = await keycloak.init();
// console.log(authenticated ? 'User authenticated' : 'Not authenticated');

type AuthContextType = {
  token: string | null;
  login: (token: string) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | null>(null);

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function AuthProvider({ children }: any) {
  const [token, setToken] = useState(sessionStorage.getItem("token"));

  const login = (t: string) => {
    sessionStorage.setItem("token", t);
    setToken(t);
  };

  const logout = () => {
    sessionStorage.removeItem("token");
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext)!;
