import { useEffect, useState } from "react";
import { StageBuildCurrentStateEnum, type StageBuild } from "../../gen";
import { stageBuildApiInstance } from "../../services/pipelines.api";

type Props = {
  stage: StageBuild;
};

export default function StageDetails({ stage }: Props) {
  const [logs, setLogs] = useState<string>("");

  useEffect(() => {
    let intervalId: ReturnType<typeof setInterval> | null = null;

    async function fetchLogs() {
      try {
        const {status, data} = await stageBuildApiInstance.getStageBuildLogs(stage.id as string);

        if (status !== 200) {
          console.error("Error getting stage build log");
        }

        setLogs(data.log);
      } catch (err) {
        console.error("Failed to load logs", err);
      }
    }

    // Load logs once immediately
    fetchLogs();

    const isFinal = ([StageBuildCurrentStateEnum.Failed, StageBuildCurrentStateEnum.Stopped, StageBuildCurrentStateEnum.Success] as StageBuildCurrentStateEnum[]).includes(
      stage.currentState ?? StageBuildCurrentStateEnum.Failed
    );

    // Only start polling if NOT in a final state
    if (!isFinal) {
      intervalId = setInterval(fetchLogs, 10000);
    }

    // Cleanup interval always
    return () => {
      if (intervalId) clearInterval(intervalId);
    };
  }, [stage.id, stage.currentState]);

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
