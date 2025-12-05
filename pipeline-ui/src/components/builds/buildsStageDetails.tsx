/* eslint-disable prefer-const */
import { useEffect, useState } from "react";
import type { StageBuilds } from "../../types/pipeline.types";
import { getBuildStagesLogs } from "../../services/pipelines.api";

type Props = {
  stage: StageBuilds;
};

export default function StageDetails({ stage }: Props) {
  const [logs, setLogs] = useState<string>("");

  useEffect(() => {
    let intervalId: ReturnType<typeof setInterval>;

    async function fetchLogs() {
      try {
        const logData = await getBuildStagesLogs(stage.id);
        setLogs(logData.log);
      } catch (err) {
        console.error("Failed to load logs", err);
      }
    }

    // Initial log load
    fetchLogs();
    if (stage.currentState === "FAILED" || "STOPPED" || "SUCCESS") {
      return () => clearInterval(intervalId);
    }

    // Start polling every 10 seconds
    intervalId = setInterval(fetchLogs, 10000);

    // Cleanup interval on stage change or unmount
  }, [stage.id]);

  if (!stage) {
    return (
      <div className="w-full p-6 bg-white rounded-xl shadow text-gray-500 text-center">
        No Stages
      </div>
    );
  }

  return (
    <div className="w-full p-6 bg-white rounded-xl shadow">
      <h2 className="text-xl font-semibold">{stage.stageName}</h2>

      {/* Logs */}
      <div className="mt-6">
        <h3 className="font-semibold text-gray-700">Logs</h3>
        <pre className="mt-2 p-3 bg-gray-100 rounded-md text-gray-800 whitespace-pre-wrap">
          {logs}
        </pre>
      </div>
    </div>
  );
}
