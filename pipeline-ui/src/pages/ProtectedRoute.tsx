import { useAuth } from "../context/authContext";
import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ children }: any) {
  const { token } = useAuth();

  if (!token) return <Navigate to="/login" replace />;

  return children;
}
