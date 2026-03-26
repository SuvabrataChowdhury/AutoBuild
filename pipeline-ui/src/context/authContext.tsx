import Keycloak from "keycloak-js";
import { createContext, useContext, useState } from "react";

const keycloak = new Keycloak({
  url: 'http://localhost:8180',  // Your Keycloak server
  realm: 'SpringBootRealm',
  clientId: 'pipeline-app'
});

const authenticated = await keycloak.init({
  onLoad: 'login-required', 
  checkLoginIframe: true 
});
// console.log(authenticated ? 'User authenticated' : 'Not authenticated');


type AuthContextType = {
  // token: string | null;
  login: (token: string) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | null>(null);

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function AuthProvider({ children }: any) {
  // const [token, setToken] = useState(sessionStorage.getItem("token"));

  // const login = (t: string) => {
  //   sessionStorage.setItem("token", t);
  //   setToken(t);
  // };

  const login = async() => {
    await keycloak.login();
  };

  // const logout = () => {
  //   sessionStorage.removeItem("token");
  //   setToken(null);
  // };

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
