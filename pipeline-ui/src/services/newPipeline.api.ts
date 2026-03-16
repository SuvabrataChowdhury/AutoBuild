import {
    PipelineApi
} from '../gen/api';

import {
    Configuration
} from '../gen/configuration';

import axiosInstance from './axiosInstance';

export const pipelineApiInstance = new PipelineApi(new Configuration(), undefined, axiosInstance);