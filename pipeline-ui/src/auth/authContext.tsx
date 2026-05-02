import { createContext, useContext} from "react";
import { idp } from "../config/authConfig";

type AuthContextType = {
  login: () => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | null>(null);

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function AuthProvider({ children }: any) {
  
  const login = async() => {
    await idp.login();
  };

  const logout = async () => {
    await idp.logout();
  };

  return (
    <AuthContext.Provider value={{login, logout}}>
      {children}
    </AuthContext.Provider>
  );
}

const useAuth = () => useContext(AuthContext)!;

export {useAuth};
