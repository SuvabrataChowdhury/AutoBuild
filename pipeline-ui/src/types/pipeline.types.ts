export type PipelineStatus = "success" | "failed" | "running" | "queued"

// All interfaces are subjected to change as per the fields returned by the backend 

export interface Pipeline {
  id: number
  name: string
  stages: Stage[]
}

export interface Stage {
  id: number;
  name: string;
  scriptType: string
  command: string;
  order : number;
}

export interface StageAPIModel {
  name: string;
  scriptType: string
  command: string;
}

export interface PipelineAPIModel {
  name: string
  stages: StageAPIModel[]
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