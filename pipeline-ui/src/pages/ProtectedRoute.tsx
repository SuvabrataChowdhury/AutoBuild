import { useAuth } from "../context/authContext";
import { Navigate } from "react-router-dom";

/* eslint-disable @typescript-eslint/no-explicit-any */
export default function ProtectedRoute({ children }: any) {
  const { token } = useAuth();

  if (!token) return <Navigate to="/login" replace />;

  return children;
}
