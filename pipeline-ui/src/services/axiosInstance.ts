import axios from "axios";
import { keycloak } from "../auth/authContext";

const axiosInstance = axios.create();

axiosInstance.interceptors.request.use((config) => {
  const token = keycloak.token;

  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

export default axiosInstance;
