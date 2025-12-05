export type PipelineStatus = "SUCCESS" | "FAILED" | "RUNNING" | "STOPPED" | "WAITING";

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
  stageName: string
  stageId : number
  currentState: PipelineStatus
}

export interface Build {
  id: number
  pipelineId: number
  pipelineName: string
  currentState: PipelineStatus
  stageBuilds: StageBuilds[]
}

export interface BuildStageLogs {
  log: string
}