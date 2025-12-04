import type { Stage } from "../../types/pipeline.types";
import { Plus, Trash2, ArrowUp, ArrowDown } from "lucide-react";

interface Props {
  stages: Stage[];
  selectedId: number;
  onSelect: (id: number) => void;
  canEdit: boolean;
  onAddStage: () => void;
  onDeleteStage: (id: number) => void;
  onMoveStageUp: (id: number) => void;
  onMoveStageDown: (id: number) => void;
}

export default function StageList({
  stages,
  selectedId,
  onSelect,
  canEdit,
  onAddStage,
  onDeleteStage,
  onMoveStageUp,
  onMoveStageDown,
}: Props) {
  return (
    <div className="w-30 border-r pr-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold">Stages</h2>

        {/* {canEdit && (
          <button
            onClick={onAddStage}
            className="h-10 w-10 rounded-full flex items-center px-3 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-all shadow"
          >
            <Plus size={16} />
          </button>
        )} */}
      </div>

      <div className="space-y-3">
        {stages.map((stage, index) => {
          const isSelected = selectedId === stage.id;

          if (!canEdit) {
            return (
              <div
                key={stage.id}
                onClick={() => onSelect(stage.id)}
                className={`
        w-10 h-10 rounded-full cursor-pointer text-sm font-medium
        border transition-all duration-200 select-none
        ${
          isSelected
            ? "bg-blue-600 text-white border-blue-700 shadow-md shadow-blue-200 scale-[1.02]"
            : "bg-white text-gray-700 border-gray-300 hover:bg-gray-100 hover:shadow-sm"
        }
      `}
              ></div>
            );
          }

          return (
            <div
              key={stage.id}
              onClick={() => onSelect(stage.id)}
              className={`border rounded-lg p-4 cursor-pointer transition-all ${
                isSelected
                  ? "bg-blue-50 border-blue-400 shadow-sm"
                  : "bg-white hover:bg-gray-50"
              }`}
            >
              <div className="flex justify-between items-center">
                <div>
                  <p className="font-medium text-lg">{stage.name}</p>
                  <p className="text-xs text-gray-500">Order: {stage.order}</p>
                </div>

                {canEdit && (
                  <div className="flex flex-col gap-2 ml-4">
                    {/* Move Up */}
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        onMoveStageUp(stage.id);
                      }}
                      disabled={index === 0}
                      className="p-1 rounded hover:bg-gray-200 disabled:opacity-30"
                    >
                      <ArrowUp size={16} />
                    </button>

                    {/* Move Down */}
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        onMoveStageDown(stage.id);
                      }}
                      disabled={index === stages.length - 1}
                      className="p-1 rounded hover:bg-gray-200 disabled:opacity-30"
                    >
                      <ArrowDown size={16} />
                    </button>

                    {/* Delete */}
                    <button
                      onClick={(e) => {
                        e.stopPropagation();
                        onDeleteStage(stage.id);
                      }}
                      className="p-1 rounded hover:bg-red-100 text-red-600"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                )}
              </div>
            </div>
          );
        })}

        {canEdit && (
          <div className="flex justify-center mt-6">
            <button
              onClick={onAddStage}
              className="h-10 w-10 rounded-full flex items-center justify-center 
                   bg-blue-600 text-white hover:bg-blue-700 transition-all shadow"
            >
              <Plus size={20} />
            </button>
          </div>
        )}

        {stages.length === 0 && (
          <p className="text-gray-500 text-sm">No stages yet.</p>
        )}
      </div>
    </div>
  );
}
