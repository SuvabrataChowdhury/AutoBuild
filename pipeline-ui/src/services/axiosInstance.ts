import axios from "axios";
import { keycloak } from "../context/authContext";

const axiosInstance = axios.create();

axiosInstance.interceptors.request.use((config) => {
  // const token = sessionStorage.getItem("token");
  const token = keycloak.token;

  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

export default axiosInstance;
