export type PipelineStatus = "SUCCESS" | "FAILED" | "RUNNING" | "queued"

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

export interface StageBuilds {
  id: number
  stageId : number
  currentState: PipelineStatus
}

export interface Build {
  id: number
  pipelineId: number
  currentState: PipelineStatus
  stageBuilds: StageBuilds[]
}