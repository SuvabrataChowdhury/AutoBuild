import type { Pipeline } from "../types/pipeline.types";

export async function getPipeline(id: number): Promise<Pipeline> {
    const data: Pipeline = await getPipelines().then(pipelines => pipelines.find(p => p.id === id)!);
    return data;
};


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
