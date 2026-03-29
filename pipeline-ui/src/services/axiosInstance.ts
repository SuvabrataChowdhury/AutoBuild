import axios from "axios";
import { idp } from "../config/authConfig";
// import { keycloak } from "../auth/authContext";

const axiosInstance = axios.create();

axiosInstance.interceptors.request.use((config) => {
  const token = idp.getToken();

  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

export default axiosInstance;
