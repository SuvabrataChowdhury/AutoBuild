import {
    PipelineApi,
    PipelineBuildApi,
    StageBuildApi
} from '../gen/api';

import { getApiConfiguration } from '../config/apiConfiguration';
import axiosInstance from './axiosInstance';


export const pipelineApiInstance = new PipelineApi(getApiConfiguration(), undefined, axiosInstance);
export const pipelineBuildApiInstance = new PipelineBuildApi(getApiConfiguration(), undefined, axiosInstance);
export const stageBuildApiInstance = new StageBuildApi(getApiConfiguration(), undefined, axiosInstance);

const API_BASE_URL = "http://localhost:8080/api/v1";

export function getLiveBuildUpdates(id: string): string {
    return `${API_BASE_URL}/pipeline/build/sse/subscribe/${id}`
}