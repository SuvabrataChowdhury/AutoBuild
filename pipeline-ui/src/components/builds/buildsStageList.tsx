import type React from "react";
import { CheckCircle, XCircle, Loader } from "lucide-react";
import type { StageBuild } from "../../gen";

interface Props {
  stages: StageBuild[];
  selectedId: string;
  onSelect: (id: string) => void;
}

export default function BuildStageList({
  stages,
  selectedId,
  onSelect,
}: Props) {
  return (
    <div className="flex flex-col gap-4 w-64">
      <h2 className="text-xl font-semibold">Stages</h2>

      {stages.map((stage) => {
        const isSelected = stage.id === selectedId;

        const stateMap: Record<string, { icon: React.ReactNode; color: string }> = {
          SUCCESS: { icon: <CheckCircle className="text-green-600" size={18} aria-label="success-circle-stage"/>, color: "text-green-600" },
          FAILED: { icon: <XCircle className="text-red-600" size={18} />, color: "text-red-600" },
          RUNNING: { icon: <Loader className="text-yellow-600 animate-spin" size={18} />, color: "text-yellow-600" },
          STOPPED: { icon: <XCircle className="text-gray-600" size={18} />, color: "text-gray-600" },
          WAITING: { icon: <Loader className="text-blue-600 animate-spin" size={18} />, color: "text-blue-600" },
        };
        const { icon = null, color = "text-gray-500" } = stateMap[stage.currentState ?? ""] ?? {};

        return (
          <button
            key={stage.id}
            onClick={() => onSelect(stage.id as string)}
            className={`
              flex items-center justify-between px-4 py-2 rounded-lg border text-sm 
              ${
                isSelected
                  ? "bg-blue-100 border-blue-400"
                  : "bg-gray-100 hover:bg-gray-200 border-gray-300"
              }
            `}
          >
            <span className="font-medium">{stage.stageName}</span>
            <span className={color}>{icon}</span>
          </button>
        );
      })}
    </div>
  );
}
