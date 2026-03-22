import { vi } from "vitest";

export const pipelineApiInstance = {
    getPipelineById: vi.fn(),
    createPipeline: vi.fn(),
    deletePipeline: vi.fn(),
    executePipeline: vi.fn()
}

export const pipelineBuildApiInstance = {
    getAllBuilds: vi.fn()
}

export const stageBuildApiInstance = {
    getStageBuildLogs: vi.fn()
}