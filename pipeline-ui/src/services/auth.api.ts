import axios from "axios";
import axiosInstance from "./axiosInstance";

const API = "http://localhost:8080/api/v1/user/auth";
const dummy_user = {token: import.meta.env.VITE_MOCK_USER}
// if in local mode, return dummy user without making API call
const isLocal = import.meta.env.VITE_ENV === "local";

export async function login(username: string, password: string) {
  if(isLocal) {
    return dummy_user;
  }
  const res = await axiosInstance.post(`${API}/login`, { username, password });
  return res.data;
}

export async function register(data: {
  email: string;
  username: string;
  password: string;
}) {
  if(isLocal)  {
    return dummy_user;
  }
  const res = await axiosInstance.post(`${API}/register`, data);
  return res.data;
}

export async function getCurrentUser(token: string) {
    if(isLocal) {
      return dummy_user;
    }
    const res = await axios.get(`${API}/currentuser`, {
        headers: { Authorization: `Bearer ${token}` },
    });
    return res.data;
}