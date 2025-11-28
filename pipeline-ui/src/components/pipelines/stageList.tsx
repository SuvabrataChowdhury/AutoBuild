import type { Stage } from "../../types/pipeline.types";

interface Props {
  stages: Stage[];
  selectedId: number;
  onSelect: (id: number) => void;
  canEdit: boolean;
  onAddStage: () => void;
}

export default function StageList({
  stages,
  selectedId,
  onSelect,
  canEdit,
  onAddStage,
}: Props) {
  return (
    <div className="flex flex-col items-center gap-0 py-4 mr-10">
      {stages.map((stage, index) => (
        <div key={stage.id} className="flex flex-col items-center">
          {/* Circle */}
          <button
            onClick={() => onSelect(stage.id)}
            className={`
              rounded-full border-2 flex items-center justify-center transition-all duration-300
              ${
                selectedId === stage.id
                  ? "w-10 h-10 bg-blue-500 border-blue-600 text-white scale-125 shadow-lg"
                  : "w-6 h-6 bg-gray-200 border-gray-400 text-gray-700"
              }
            `}
          >
            {selectedId === stage.id ? stage.order : ""}
          </button>

          {/* Connector */}
          {index < stages.length - 1 && (
            <div className="w-1 h-10 bg-gray-300"></div>
          )}
        </div>
      ))}

      {/* Add Stage Button (only while editing) */}
      {canEdit && (
        <button
          onClick={onAddStage}
          className="bg-blue-600 h-6 w-6 text-white rounded-3xl hover:bg-blue-700 transition shadow"
        >
          +
        </button>
      )}
    </div>
  );
}
