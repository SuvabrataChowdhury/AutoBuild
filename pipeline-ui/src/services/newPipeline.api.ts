import {
    PipelineApi,
    PipelineBuildApi,
    StageBuildApi
} from '../gen/api';

import {
    Configuration
} from '../gen/configuration';

import axiosInstance from './axiosInstance';

export const pipelineApiInstance = new PipelineApi(new Configuration(), undefined, axiosInstance);
export const pipelineBuildApiInstance = new PipelineBuildApi(new Configuration(), undefined, axiosInstance);
export const stageBuildApiInstance = new StageBuildApi(new Configuration(), undefined, axiosInstance);