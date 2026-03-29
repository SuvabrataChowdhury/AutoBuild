import { idp } from "../config/authConfig";

/* eslint-disable @typescript-eslint/no-explicit-any */
export default function ProtectedRoute({ children }: any) {

  if (!idp.isAuthenticated()) {
    idp.login();
  }

  return children;
}
