import axiosInstance from "./axiosInstance";
import type { Build, BuildStageLogs, Pipeline, PipelineAPIModel } from "../types/pipeline.types";

/* eslint-disable @typescript-eslint/no-explicit-any */
// Temporary API base URL, this needs to be configured properly
// TODO: Move to environment variable
const API_BASE_URL = "http://localhost:8080/api/v1";

export async function getPipeline(id: number): Promise<Pipeline> {
    try {
        const response = await axiosInstance.get(`${API_BASE_URL}/pipeline/${id}`);
        return response.data as Pipeline;
    } catch (error:any) {
        errorHandler(error?.response as Response);
        console.error("Error fetching pipeline:", error);
        throw error;
    }
}

export async function getPipelines(): Promise<Pipeline[]> {
    try {
    const response = await axiosInstance.get(`${API_BASE_URL}/pipeline`);
    return response.data as Pipeline[];
  } catch (error:any) {
        errorHandler(error?.response as Response);
    console.error("Error fetching pipelines:", error);
    return [];
  }
}

export async function savePipeline(pipeline: PipelineAPIModel): Promise<Pipeline> {
    try {
        const response = await axiosInstance.post(`${API_BASE_URL}/pipeline`, pipeline);
        return response.data as Pipeline;
   } catch (error:any) {
        const response = errorHandler(error?.response as Response);
        console.error("Error saving pipeline:", error);
        return response as unknown as Pipeline;
    }
}

export async function updatePipeline(id: number, pipeline: PipelineAPIModel): Promise<Pipeline> {
    //TODO: Implement update pipeline
    console.log("Updating pipeline:", id, pipeline);
    try{
        const response = await axiosInstance.put(`${API_BASE_URL}/pipeline/${id}`, pipeline);
        return response.data as Pipeline;   
    } catch (error:any) {
        errorHandler(error?.response as Response);
        console.error("Error updating pipeline:", error);
        return error;
    }
    
}

export async function deletePipeline(id: number) : Promise<boolean> {
    try {
        await axiosInstance.delete(`${API_BASE_URL}/pipeline/${id}`)
        return true
    }catch (error:any) {
        errorHandler(error?.response as Response);
        console.error("Error while deleting Pipeline", error)
        throw error;
    }
}

//build will be a different entity in future with logs, status, etc.
export async function getBuildData(pipelineId: number): Promise<Build> {
    try {
        const response = await axiosInstance.get(`${API_BASE_URL}/pipeline/build/${pipelineId}`);
        return response.data as Build;
    } catch (error:any) {
        errorHandler(error?.response as Response);
        console.error("Error fetching build data:", error);
        throw error;
    }
}

export async function executeBuild(pipelineId: number): Promise<Build> {
    try {
        const req = {
            pipelineId: pipelineId
        }
        const response = await axiosInstance.post(`${API_BASE_URL}/execute/pipeline`, req)
        return response.data as Build;
    } catch (error:any) {
        errorHandler(error?.response as Response);
        console.error("Error executing Build:", error)
        throw error;
    }
}

// On Changing the getting builds iomplementation here, we will change the build type to incorporate the chages
export async function getBuildsList(): Promise<Build[]> {
    try {
        const response = await axiosInstance.get(`${API_BASE_URL}/pipeline/build`)
        return response.data as Build[];
   } catch (error:any) {
        errorHandler(error?.response as Response);
        console.error("Error fetching pipeline:", error)
        throw error;
    }
}

export async function getBuildStagesLogs(id: number): Promise<BuildStageLogs> {
    try {
        const response = await axiosInstance.get(`${API_BASE_URL}/stage/build/logs/${id}`);
        return response.data as BuildStageLogs;
    } catch (error:any) {
        errorHandler(error?.response as Response);
        console.error("Error fetching build stage logs:", error);
        throw error;
    }
}

export function getLiveBuildUpdates(id: number): string {
    return `${API_BASE_URL}/pipeline/build/sse/subscribe/${id}`
}

export async function deleteBuild(id: number) : Promise<void> {
    try {
        await axiosInstance.delete(`${API_BASE_URL}/pipeline/build/${id}`)
   } catch (error:any) {
        errorHandler(error?.response as Response);
        console.error("Error while deleting Build", error)
        throw error;
    }
}

async function errorHandler(response: any) : Promise<string[]> {
   if(response.status === 401) {
         //handle unauthorized access
        window.location.href = "/login";
        return ["Unauthorized access. Redirecting to login."];
    }
    else if(!response.ok) {
        const detail = response.data.detail || "An error occurred";
        const message = detail.split(",")
        return message;
    }
    return response.data;
}




//TODO : on start build, send an execute call to backend

