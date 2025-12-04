import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import type { Build, Pipeline } from "../../types/pipeline.types";
import {
  deleteBuild,
  getBuildData,
  getLiveBuildUpdates,
  getPipeline,
} from "../../services/pipelines.api";

import NavBar from "../../components/common/navBar";
import PipelineHeader from "../../components/pipelines/pipelineHeader";
import BuildStageList from "../../components/builds/buildsStageList";
import BuildStageDetails from "../../components/builds/buildsStageDetails";
import { CheckCircle, Loader, Trash, XCircle } from "lucide-react";
import { booleanFlags } from "../../flags/booleanFlags";

export default function BuildsRunningPage() {
  const { id } = useParams();
  const [build, setBuild] = useState<Build | null>(null);
  const [selectedStageId, setSelectedStageId] = useState<number>(0);
  const [pipeline, setPipeline] = useState<Pipeline>();
  const [isDeleteVisible, setIsDeleteVisible] = useState(false);

  useEffect(() => {
    let intervalId: ReturnType<typeof setInterval>;
    const buildId = id as unknown as number;

    let eventSource: EventSource;

    async function fetchData() {
      const data = await getBuildData(buildId);
      setBuild(data);

      // Auto-select first stage
      if (data.stageBuilds.length > 0) {
        setSelectedStageId(data.stageBuilds[0].id);
      }

      // Load pipeline
      const pipelineData = await getPipeline(data.pipelineId);
      setPipeline(pipelineData);

      // Only open SSE if running
      if (data.currentState === "RUNNING" && booleanFlags.ENABLE_SSE) {
        const url = await getLiveBuildUpdates(buildId);
        eventSource = new EventSource(url);

        eventSource.onmessage = (event) => {
          const parsed = JSON.parse(event.data);
          console.log("SSE Update:", parsed);

          setBuild((prev) => ({
            ...prev!,
            currentState: parsed.currentState,
            stageBuilds: parsed.stageBuilds,
          }));

          // Close stream when job ends
          if (parsed.currentState !== "RUNNING") {
            setIsDeleteVisible(true);
            eventSource.close();
          }
        };

        eventSource.onerror = () => {
          eventSource.close();
        };
      }
    }

    fetchData();

    intervalId = setInterval(fetchData, 10000);

    return () => {
      if (intervalId) clearInterval(intervalId);
      eventSource?.close();
    };
  }, [id]);

  if (!build) return <div>Loading...</div>;

  if (isDeleteVisible === false && build.currentState !== "RUNNING") {
    setIsDeleteVisible(true);
  }
  const selectedStage = build.stageBuilds.find((s) => s.id === selectedStageId);

  async function handleDelete() {
    await deleteBuild(build?.id as unknown as number);
    window.location.href = "/builds";
  }

  let icon = null;
  let color = "";

  switch (build.currentState) {
    case "SUCCESS":
      icon = <CheckCircle className="text-green-600" size={18} />;
      color = "text-green-600";
      break;
    case "FAILED":
      icon = <XCircle className="text-red-600" size={18} />;
      color = "text-red-600";
      break;
    case "RUNNING":
      icon = <Loader className="text-yellow-600 animate-spin" size={25} />;
      color = "text-yellow-600";
      break;
    default:
      color = "text-gray-500";
  }

  return (
    <>
      <NavBar />

      <div className="min-h-screen p-14 ml-10">
        <div className=" mb-10 mt-10 text-center flex-row gap-6 flex">
          <PipelineHeader name={pipeline?.name} />
          <span className={color}>{icon}</span>
          {/* Delete button */}
          <div className="relative group inline-block">
            <button
              onClick={isDeleteVisible ? handleDelete : undefined}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl transition-all shadow-sm
      ${
        isDeleteVisible
          ? "bg-red-400 text-white hover:bg-red-500 cursor-pointer"
          : "bg-gray-300 text-gray-500 cursor-not-allowed"
      }`}
            >
              <Trash size={18} />
              Delete
            </button>

            {/* Tooltip */}
            {!isDeleteVisible && (
              <div
                className="absolute left-1/2 -translate-x-1/2 mt-2 w-max px-3 py-1 
      rounded-md bg-black text-white text-xs opacity-0 group-hover:opacity-100 
      transition-opacity pointer-events-none shadow-lg"
              >
                This Build is running, can't delete now.
              </div>
            )}
          </div>
        </div>

        <div className="flex flex-row gap-10">
          <BuildStageList
            stages={build.stageBuilds}
            selectedId={selectedStageId}
            onSelect={setSelectedStageId}
          />

          {selectedStage && (
            <BuildStageDetails stage={selectedStage} pipeline={pipeline} />
          )}
        </div>
      </div>
    </>
  );
}
