export type PipelineStatus = "success" | "failed" | "running" | "queued"

// All interfaces are subjected to change as per the fields returned by the backend 

export interface Pipeline {
  id: number
  name: string
  description: string
  dateCreated?: string
  status?: PipelineStatus
  stages: Stage[]
}

export interface Stage {
  id: number;
  name: string;
  description: string;
  commands: string;
  order: number;
}

export interface Build {
  name: string;
  description: string;
  dateCreated: string;
  id: number;
  pipelineId: number;
  status: PipelineStatus;
  stages: Stage[];
  logs?: string;
}