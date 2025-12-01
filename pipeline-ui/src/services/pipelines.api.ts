import type { Build, Pipeline } from "../types/pipeline.types";

export async function getPipeline(id: number): Promise<Pipeline> {
    const data: Pipeline = await getPipelines().then(pipelines => pipelines.find(p => p.id === id)!);
    return data;
};

//build will be a different entity in future with logs, status, etc.
export async function getBuildData(pipelineId: number): Promise<Pipeline> {
    return getPipeline(pipelineId);
}

// On Changing the getting builds iomplementation here, we will change the build type to incorporate the chages
export async function getBuildsList(): Promise<Build[]> {
    return [
        {
            id: 1,
            name: "Build 1",
            description: "First build description",
            dateCreated: "12/25/2025",
            status: "success",
            stages: [
                {
                    id: 1,
                    name: "Build Stage",
                    description: "This stage handles the build process.",
                    commands: "npm install && npm run build",
                    order: 1
                }
            ],
            pipelineId: 0
        },
        {
            id: 2,
            name: "Build 2",
            description: "Second build description",
            dateCreated: "01/05/2026",
            status: "failed",
            stages: [
                {
                    id: 1,
                    name: "Test Stage",
                    description: "This stage runs all unit and integration tests.",
                    commands: "npm test",
                    order: 1
                }
            ],
            pipelineId: 0
        }
    ]
}

//TODO : on start build, send an execute call to backend

export async function getPipelines(): Promise<Pipeline[]> {
    return [
        {
            id: 1,
            name: "Lorem Ipsum Pipeline",
            description: "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
            dateCreated: "12/23/2025",
            status: "success",
            stages: []
        },
        {
            id: 2,
            name: "Dolor Sit Amet Pipeline",
            description: "Contrary to popular belief, Lorem Ipsum is not simply random text.",
            dateCreated: "01/15/2026",
            status: "failed",
            stages: []
        },
        {
            id: 3,
            name: "Consectetur Adipiscing Pipeline",
            description: "It has roots in a piece of classical Latin literature from 45 BC.",
            dateCreated: "02/10/2026",
            status: "running",
            stages: [
                {
                    id: 1,
                    name: "Build Stage",
                    description: "This stage handles the build process.",
                    commands: "npm install && npm run build",
                    order: 1
                },
                {
                    id: 2,
                    name: "Test Stage",
                    description: "This stage runs all unit and integration tests.",
                    commands: "npm test",
                    order: 2
                },
                {
                    id: 3,
                    name: "Deploy Stage",
                    description: "This stage deploys the application to the production environment.",
                    commands: "npm run deploy",
                    order: 3
                }
            ]
        }
    ];
}
