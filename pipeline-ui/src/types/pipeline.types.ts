export type PipelineStatus = "success" | "failed" | "running" | "queued"

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