import { useEffect, useState } from "react";
import type { Pipeline } from "../../types/pipeline.types";
import { getBuildData } from "../../services/pipelines.api";
import PipelineHeader from "../../components/pipelines/pipelineHeader";
import PipelineDescription from "../../components/pipelines/pipelineDescription";
import BuildStageList from "../../components/builds/buildsStageList";
import BuildStageDetails from "../../components/builds/buildsStageDetails";

function BuildsPage() {
  const [pipeline, setPipeline] = useState<Pipeline | null>(null);
  const [selectedStageId, setSelectedStageId] = useState<number>(0);

  const id = window.location.pathname.split("/").pop();

  useEffect(() => {
    async function fetchData() {
      if (!id || isNaN(Number(id))) return;

      //TODO fetch build data instead of pipeline data
      // For now, we use getBuildData which is same as getPipeline in api file to mock response
      const data = await getBuildData(Number(id));
      setPipeline(data);

      // Auto-select first stage if available
      if (data.stages.length > 0) {
        setSelectedStageId(data.stages[0].id);
      }
    }
    fetchData();
  }, [id]);

  if (!pipeline) {
    return <div>Loading...</div>;
  }

  const selectedStage = pipeline.stages.find((s) => s.id === selectedStageId);

  return (
    <div className="min-h-screen p-10 ml-10">
      {/* Header */}
      <div className="justify-center mb-10 mt-10">
        <PipelineHeader name={pipeline.name} />
        <PipelineDescription text={pipeline.description} />
      </div>

      {/* Body */}
      <div className="flex flex-row mt-10 gap-10">
        {/* Stages List */}
        <BuildStageList
          stages={pipeline.stages}
          selectedId={selectedStageId}
          onSelect={setSelectedStageId}
        />
        {selectedStage && <BuildStageDetails stage={selectedStage} />}
      </div>
    </div>
  );
}

export default BuildsPage;
