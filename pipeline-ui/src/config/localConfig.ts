import { Configuration } from "../gen/configuration";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;
const isLocal = import.meta.env.VITE_ENV === "local";

// Use environment variable to fetch the BASE_URL
export const localConfig = new Configuration({
    basePath: BASE_URL,
    accessToken: isLocal ? "dummy" : undefined,
    baseOptions: {
        headers: {
            Authorization: `Bearer ${isLocal ? "dummy" : ""}`
        }
    }
});