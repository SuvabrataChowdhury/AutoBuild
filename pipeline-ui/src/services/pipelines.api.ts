/* eslint-disable @typescript-eslint/no-explicit-any */
// Temporary API base URL, this needs to be configured properly
// TODO: Move to environment variable

import {
    PipelineApi,
    PipelineBuildApi,
    StageBuildApi
} from '../gen/api';

import {
    Configuration
} from '../gen/configuration';

import { localConfig } from '../config/localConfig';


import axiosInstance from './axiosInstance';
const isLocal = import.meta.env.VITE_ENV === "local";

export const pipelineApiInstance = new PipelineApi(isLocal? localConfig : new Configuration(), undefined, axiosInstance);
export const pipelineBuildApiInstance = new PipelineBuildApi(isLocal? localConfig : new Configuration(), undefined, axiosInstance);
export const stageBuildApiInstance = new StageBuildApi(isLocal? localConfig : new Configuration(), undefined, axiosInstance);

const API_BASE_URL = "http://localhost:8080/api/v1";

export function getLiveBuildUpdates(id: string): string {
    return `${API_BASE_URL}/pipeline/build/sse/subscribe/${id}`
}

//TODO : on start build, send an execute call to backend
