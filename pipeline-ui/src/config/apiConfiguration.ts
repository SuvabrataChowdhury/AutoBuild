import { Configuration } from "../gen/configuration";
import { localConfig } from './localConfig';

export function getApiConfiguration() {
    const isLocal = import.meta.env.VITE_ENV === "local";
    if (isLocal) {
        return localConfig;
    } else {
        return new Configuration();
    }
}