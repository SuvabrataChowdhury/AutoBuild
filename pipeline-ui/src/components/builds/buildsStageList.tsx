import { CheckCircle, XCircle, Loader } from "lucide-react";
import type { StageBuilds } from "../../types/pipeline.types";

interface Props {
  stages: StageBuilds[];
  selectedId: number;
  onSelect: (id: number) => void;
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

        let icon = null;
        let color = "";

        switch (stage.currentState) {
          case "SUCCESS":
            icon = <CheckCircle className="text-green-600" size={18} />;
            color = "text-green-600";
            break;
          case "FAILED":
            icon = <XCircle className="text-red-600" size={18} />;
            color = "text-red-600";
            break;
          case "RUNNING":
            icon = (
              <Loader className="text-yellow-600 animate-spin" size={18} />
            );
            color = "text-yellow-600";
            break;
          default:
            color = "text-gray-500";
        }

        return (
          <button
            key={stage.id}
            onClick={() => onSelect(stage.id)}
            className={`
              flex items-center justify-between px-4 py-2 rounded-lg border text-sm 
              ${
                isSelected
                  ? "bg-blue-100 border-blue-400"
                  : "bg-gray-100 hover:bg-gray-200 border-gray-300"
              }
            `}
          >
            <span className="font-medium">{stage.id}</span>
            <span className={color}>{icon}</span>
          </button>
        );
      })}
    </div>
  );
}
