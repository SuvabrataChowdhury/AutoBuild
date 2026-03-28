import { keycloak } from "../auth/authContext";

/* eslint-disable @typescript-eslint/no-explicit-any */
export default function ProtectedRoute({ children }: any) {

  if (!keycloak.authenticated) {
    keycloak.login();
  }

  return children;
}
