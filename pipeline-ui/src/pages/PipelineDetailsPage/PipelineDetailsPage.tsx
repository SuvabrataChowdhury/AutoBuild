import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import PipelineHeader from "../../components/pipelines/pipelineHeader";
import StageList from "../../components/pipelines/stageList";
import StageDetails from "../../components/pipelines/stageDetails";

import type {
  Pipeline,
  PipelineAPIModel,
  Stage,
  StageAPIModel,
} from "../../types/pipeline.types";
import {
  deletePipeline,
  executeBuild,
  getBuildsList,
  getPipeline,
  savePipeline,
} from "../../services/pipelines.api";

import { Pencil, Play, Trash } from "lucide-react";
import NavBar from "../../components/common/navBar";

export default function PipelineDetailPage() {
  const { id } = useParams();

  // The "real" pipeline (server data)
  const [pipeline, setPipeline] = useState<Pipeline | null>(null);

  // The stage currently displayed on the right side
  const [selectedStageId, setSelectedStageId] = useState<number>(0);

  // Whether the user is editing or just viewing
  const [isEditing, setIsEditing] = useState(false);

  //Create flow
  const [isCreateMode, setIsCreateMode] = useState(false);
  const saveOrCreate = isCreateMode ? "Create" : "Save";

  // Editable clone (we do not modify the real pipeline until Save)
  const [editablePipeline, setEditablePipeline] = useState<Pipeline | null>(
    null
  );
  const [isDeleteVisible, setIsDeleteVisible] = useState(false);

  async function checkDelete(id: number) {
    const response = await getBuildsList();
    let flag = true;
    //checking if pipeline id exists in the builds list
    response.forEach((build) => {
      if (build.pipelineId === id) {
        flag = false;
      }
    });

    return flag;
  }

  // Load pipeline on mount / when ID changes
  useEffect(() => {
    async function fetchData() {
      if (id === "0") {
        // New pipeline template
        const newPipeline: Pipeline = {
          id: 0,
          name: "New Pipeline",
          stages: [],
        };
        setEditablePipeline(newPipeline);
        setIsEditing(true);
        setPipeline(newPipeline);
        setIsCreateMode(true);
        return;
      }
      const data = await getPipeline(id as unknown as number);
      setPipeline(data);

      setIsDeleteVisible(await checkDelete(id as unknown as number));

      // Auto-select first stage on load
      if (data.stages.length > 0) {
        setSelectedStageId(data.stages[0].id);
      }
    }

    fetchData();
  }, [id]);

  // While loading
  if (!pipeline) return <div>Loading...</div>;

  //   // Current displayed stage (from real pipeline)
  //   const selectedStage = pipeline.stages.find((s) => s.id === selectedStageId);

  // Begin editing -> clone pipeline
  function handleEdit() {
    // Create a deep-ish copy so user can safely edit
    setEditablePipeline(JSON.parse(JSON.stringify(pipeline)));
    setIsEditing(true);
    console.log("Editing pipeline:", pipeline);
  }

  async function handleDelete() {
    const response = await deletePipeline(id as unknown as number);
    if (response) {
      console.log("Pipeline deleted");
      window.location.href = "/pipelines";
    }
  }

  function removeOrderFromStages(stages: Stage[]): StageAPIModel[] {
    return stages.map((stage) => ({
      name: stage.name,
      scriptType: stage.scriptType,
      command: stage.command,
    }));
  }

  // Save changes and exit edit mode
  async function handleSave() {
    if (isCreateMode) {
      setIsCreateMode(false);
      const pipelineToSave = {
        name: editablePipeline!.name,
        stages: removeOrderFromStages(editablePipeline!.stages),
      } as unknown as PipelineAPIModel;
      const data = await savePipeline(pipelineToSave as PipelineAPIModel);
      window.location.href = `/pipelines/${data.id}`;
      return;
    }

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
    if (isCreateMode) {
      window.location.href = "/pipelines";
    }
    setEditablePipeline(null);
    setIsEditing(false);
  }

  async function handleStartBuild() {
    const data = await executeBuild(id as unknown as number);
    window.location.href = `/builds/${data.id}`;
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

    const nextId =
      editablePipeline.stages.length === 0
        ? 1
        : Math.max(...editablePipeline.stages.map((s) => s.id)) + 1;

    const newStage: Stage = {
      id: nextId,
      name: "New Stage",
      scriptType: "bash",
      command: "",
      order: editablePipeline.stages.length + 1,
    };

    setEditablePipeline((prev) => ({
      ...prev!,
      stages: [...prev!.stages, newStage],
    }));

    setSelectedStageId(nextId);
  }

  function handleDeleteStage(stageId: number) {
    if (!editablePipeline) return;

    const stages = editablePipeline.stages
      .filter((s) => s.id !== stageId)
      .map((s, i) => ({ ...s, order: i + 1 }));

    setEditablePipeline((prev) => ({ ...prev!, stages }));

    // ensure valid selection
    if (stages.length === 0) {
      setSelectedStageId(0);
    } else if (!stages.some((s) => s.id === selectedStageId)) {
      setSelectedStageId(stages[0].id);
    }
  }

  function moveStage(stageId: number, direction: "up" | "down") {
    if (!editablePipeline) return;

    const stages = [...editablePipeline.stages].sort(
      (a, b) => a.order - b.order
    );
    const index = stages.findIndex((s) => s.id === stageId);

    if (direction === "up" && index > 0) {
      [stages[index - 1], stages[index]] = [stages[index], stages[index - 1]];
    }
    if (direction === "down" && index < stages.length - 1) {
      [stages[index], stages[index + 1]] = [stages[index + 1], stages[index]];
    }

    const reordered = stages.map((s, i) => ({ ...s, order: i + 1 }));

    setEditablePipeline((prev) =>
      prev ? { ...prev, stages: reordered } : prev
    );
  }

  // The pipeline used for rendering (depends on edit mode)
  const activePipeline = isEditing ? editablePipeline! : pipeline;

  return (
    <>
      <NavBar></NavBar>
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
                      This stage cannot be deleted because it is used in a
                      build.
                    </div>
                  )}
                </div>

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
                {/* Save Or Create*/}
                <button
                  onClick={handleSave}
                  className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white 
                  rounded-xl hover:bg-green-700 transition-all shadow shadow-green-300"
                >
                  {saveOrCreate}
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

        {/* --- DESCRIPTION ---
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
      )} */}

        {/* --- STAGES + RIGHT PANEL --- */}
        <div className="flex flex-row mt-10">
          <StageList
            stages={isEditing ? editablePipeline!.stages : pipeline.stages}
            selectedId={selectedStageId}
            onSelect={setSelectedStageId}
            canEdit={isEditing}
            onAddStage={handleAddStage}
            onDeleteStage={handleDeleteStage}
            onMoveStageUp={(id: number) => moveStage(id, "up")}
            onMoveStageDown={(id: number) => moveStage(id, "down")}
          />

          <StageDetails
            stage={activePipeline.stages.find((s) => s.id === selectedStageId)!}
            isEditing={isEditing}
            onChangeStage={updateStage}
          />
        </div>
      </div>
    </>
  );
}
