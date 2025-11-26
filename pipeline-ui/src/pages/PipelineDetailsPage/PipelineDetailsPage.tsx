import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import PipelineHeader from "../../components/pipelines/pipelineHeader";
import PipelineDescription from "../../components/pipelines/pipelineDescription";
import StageList from "../../components/pipelines/stageList";
import StageDetails from "../../components/pipelines/stageDetails";

import type { Pipeline, Stage } from "../../types/pipeline.types";
import { getPipeline } from "../../services/pipelines.api";

import { Pencil, Play } from "lucide-react";

export default function PipelineDetailPage() {
  const { id } = useParams();

  // The "real" pipeline (server data)
  const [pipeline, setPipeline] = useState<Pipeline | null>(null);

  // The stage currently displayed on the right side
  const [selectedStageId, setSelectedStageId] = useState<number>(0);

  // Whether the user is editing or just viewing
  const [isEditing, setIsEditing] = useState(false);

  // Editable clone (we do not modify the real pipeline until Save)
  const [editablePipeline, setEditablePipeline] = useState<Pipeline | null>(
    null
  );

  // Load pipeline on mount / when ID changes
  useEffect(() => {
    async function fetchData() {
      if (!id || isNaN(Number(id))) return;

      const data = await getPipeline(Number(id));
      setPipeline(data);

      // Auto-select first stage on load
      if (data.stages.length > 0) {
        setSelectedStageId(data.stages[0].id);
      }
    }

    fetchData();
  }, [id]);

  // While loading
  if (!pipeline) return <div>Loading...</div>;

  // Current displayed stage (from real pipeline)
  const selectedStage = pipeline.stages.find((s) => s.id === selectedStageId);

  // Begin editing -> clone pipeline
  function handleEdit() {
    // Create a deep-ish copy so user can safely edit
    setEditablePipeline(JSON.parse(JSON.stringify(pipeline)));
    setIsEditing(true);
    console.log("Editing pipeline:", pipeline);
  }

  // Save changes and exit edit mode
  async function handleSave() {
    if (!editablePipeline) return;

    // TODO: API PUT call (updatePipeline)
    // const updated = await updatePipeline(editablePipeline.id, editablePipeline);

    setPipeline(editablePipeline); // update UI with edited copy
    setEditablePipeline(null);
    setIsEditing(false);
    console.log("Saved pipeline:", editablePipeline);
  }

  // Cancel editing -> discard clone
  function handleCancel() {
    setEditablePipeline(null);
    setIsEditing(false);
  }

  function handleStartBuild() {
    alert("Start Build - Feature coming soon!");
  }

  // Helper: update a stage inside editablePipeline
  function updateStage(updatedStage: Stage) {
    setEditablePipeline((prev) => {
      if (!prev) return prev;

      return {
        ...prev,
        stages: prev.stages.map((s) =>
          s.id === updatedStage.id ? updatedStage : s
        ),
      };
    });
  }

  function handleAddStage() {
    if (!editablePipeline) return;

    // Create new stage with default values
    const newStage: Stage = {
      id: Math.max(0, ...editablePipeline.stages.map((s) => s.id)) + 1, // next ID
      name: "New Stage",
      description: "",
      commands: "",
      order: editablePipeline.stages.length + 1,
    };

    // Add to editable pipeline
    setEditablePipeline((prev) => {
      if (!prev) return prev;

      return {
        ...prev,
        stages: [...prev.stages, newStage],
      };
    });
    // Select the new stage
    setSelectedStageId(newStage.id);
  }

  // The pipeline used for rendering (depends on edit mode)
  const activePipeline = isEditing ? editablePipeline! : pipeline;

  return (
    <div className="p-20 min-h-screen">
      {/* --- HEADER AREA (Name + Action Buttons) --- */}
      <div className="w-full flex items-center justify-between">
        {/* Pipeline Name — text or input */}
        {isEditing ? (
          <input
            className="text-3xl font-bold border px-3 py-2 rounded-lg w-1/2"
            value={editablePipeline?.name ?? ""}
            onChange={(e) =>
              setEditablePipeline((prev) =>
                prev ? { ...prev, name: e.target.value } : prev
              )
            }
          />
        ) : (
          <PipelineHeader name={pipeline.name} />
        )}

        {/* ACTION BUTTONS */}
        <div className="flex items-center gap-4">
          {!isEditing ? (
            <>
              {/* Edit Button */}
              <button
                onClick={handleEdit}
                className="flex items-center gap-2 px-4 py-2 bg-gray-200 text-gray-700 
                  rounded-xl hover:bg-gray-300 transition-all shadow-sm"
              >
                <Pencil size={18} />
                Edit
              </button>

              {/* Start Build */}
              <button
                onClick={handleStartBuild}
                className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white 
                  rounded-xl hover:bg-blue-700 transition-all shadow shadow-blue-300"
              >
                <Play size={18} />
                Start Build
              </button>
            </>
          ) : (
            <>
              {/* Save */}
              <button
                onClick={handleSave}
                className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white 
                  rounded-xl hover:bg-green-700 transition-all shadow shadow-green-300"
              >
                Save
              </button>

              {/* Cancel */}
              <button
                onClick={handleCancel}
                className="flex items-center gap-2 px-4 py-2 bg-gray-300 text-gray-700 
                  rounded-xl hover:bg-gray-400 transition-all"
              >
                Cancel
              </button>
            </>
          )}
        </div>
      </div>

      {/* --- DESCRIPTION --- */}
      {isEditing ? (
        <textarea
          className="mt-4 w-full border px-3 py-2 rounded-lg"
          rows={3}
          value={editablePipeline?.description ?? ""}
          onChange={(e) =>
            setEditablePipeline((prev) =>
              prev ? { ...prev, description: e.target.value } : prev
            )
          }
        />
      ) : (
        <PipelineDescription text={pipeline.description} />
      )}

      {/* --- STAGES + RIGHT PANEL --- */}
      <div className="flex flex-row mt-10">
        <StageList
          stages={isEditing ? editablePipeline!.stages : pipeline.stages}
          selectedId={selectedStageId}
          onSelect={setSelectedStageId}
          canEdit={isEditing}
          onAddStage={handleAddStage}
        />

        <StageDetails
          stage={activePipeline.stages.find((s) => s.id === selectedStageId)!}
          isEditing={isEditing}
          onChangeStage={updateStage}
        />
      </div>
    </div>
  );
}
