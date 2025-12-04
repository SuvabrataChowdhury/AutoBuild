import axios from "axios";
import type { Build, BuildStageLogs, Pipeline, PipelineAPIModel } from "../types/pipeline.types";

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
    console.log("Updating pipeline:", id, pipeline);
    return {id} as Pipeline;
}

export async function deletePipeline(id: number) : Promise<boolean> {
    try {
        await axios.delete(`${API_BASE_URL}/pipeline/${id}`)
        return true
    }
    catch(error) {
        console.error("Error while deleting Pipeline", error)
        throw error;
    }
}

//build will be a different entity in future with logs, status, etc.
export async function getBuildData(pipelineId: number): Promise<Build> {
    try {
        const response = await axios.get(`${API_BASE_URL}/pipeline/build/${pipelineId}`);
        return response.data as Build;
    } catch (error) {
        console.error("Error fetching build data:", error);
        throw error;
    }
}

export async function executeBuild(pipelineId: number): Promise<Build> {
    try {
        const req = {
            pipelineId: pipelineId
        }
        const response = await axios.post(`${API_BASE_URL}/execute/pipeline`, req)
        return response.data as Build;
    } catch(error) {
        console.error("Error executing Build:", error)
        throw error;
    }
}

// On Changing the getting builds iomplementation here, we will change the build type to incorporate the chages
export async function getBuildsList(): Promise<Build[]> {
    try {
        const response = await axios.get(`${API_BASE_URL}/pipeline/build`)
        return response.data as Build[];
    } catch (error) {
        console.error("Error fetching pipeline:", error)
        throw error;
    }
}

export async function getBuildStagesLogs(id: number): Promise<BuildStageLogs> {
    try {
        const response = await axios.get(`${API_BASE_URL}/stage/build/logs/${id}`);
        return response.data as BuildStageLogs;
    } catch (error) {
        console.error("Error fetching build stage logs:", error);
        throw error;
    }
}

export async function getLiveBuildUpdates(id: number): Promise<string> {
    return `${API_BASE_URL}/pipeline/build/sse/subscribe/${id}`
}

export async function deleteBuild(id: number) : Promise<void> {
    try {
        await axios.delete(`${API_BASE_URL}/pipeline/build/${id}`)
    }
    catch(error) {
        console.error("Error while deleting Build", error)
        throw error;
    }
}




//TODO : on start build, send an execute call to backend

