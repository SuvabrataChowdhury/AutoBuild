import axios from "axios";
import type { Build, Pipeline, PipelineAPIModel } from "../types/pipeline.types";

// Temporary API base URL, this needs to be configured properly
// TODO: Move to environment variable
const API_BASE_URL = "http://localhost:8080/api/v1";

export async function getPipeline(id: number): Promise<Pipeline> {
    try {
        const response = await axios.get(`${API_BASE_URL}/pipeline/${id}`);
        return response.data as Pipeline;
    } catch (error) {
        console.error("Error fetching pipeline:", error);
        throw error;
    }
}

export async function getPipelines(): Promise<Pipeline[]> {
    try {
    const response = await axios.get(`${API_BASE_URL}/pipeline`);
    return response.data as Pipeline[];
  } catch (error) {
    console.error("Error fetching pipelines:", error);
    return [];
  }
}

export async function savePipeline(pipeline: PipelineAPIModel): Promise<Pipeline> {
    try {
        const response = await axios.post(`${API_BASE_URL}/pipeline`, pipeline);
        return response.data as Pipeline;
    } catch (error) {
        console.error("Error saving pipeline:", error);
        throw error;
    }
}

export async function updatePipeline(id: number, pipeline: PipelineAPIModel): Promise<Pipeline> {
    //TODO: Implement update pipeline
    return {} as Pipeline;
}

//build will be a different entity in future with logs, status, etc.
export async function getBuildData(pipelineId: number): Promise<Pipeline> {
    return getPipeline(pipelineId);
}

// On Changing the getting builds iomplementation here, we will change the build type to incorporate the chages
export async function getBuildsList(): Promise<Build[]> {
    return null as any;
}

//TODO : on start build, send an execute call to backend

