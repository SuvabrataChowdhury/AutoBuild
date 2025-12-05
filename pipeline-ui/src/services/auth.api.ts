import axios from "axios";
import axiosInstance from "./axiosInstance";

const API = "http://localhost:8080/api/v1/user/auth";

export async function login(username: string, password: string) {
  const res = await axiosInstance.post(`${API}/login`, { username, password });
  return res.data;
}

export async function register(data: {
  email: string;
  username: string;
  password: string;
}) {
  const res = await axiosInstance.post(`${API}/register`, data);
  return res.data;
}

export async function getCurrentUser(token: string) {
    const res = await axios.get(`${API}/currentuser`, {
        headers: { Authorization: `Bearer ${token}` },
    });
    return res.data;
}