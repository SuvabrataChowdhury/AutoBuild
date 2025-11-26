import React from "react";
import type { Stage } from "../../types/pipeline.types";
import StageCommandsEditor from "./stageCommandsEditor";

type Props = {
  stage: Stage;
  isEditing: boolean;
  onChangeStage: (updated: Stage) => void;
};

export default function StageDetails({
  stage,
  isEditing,
  onChangeStage,
}: Props) {
  // If no stage selected, show placeholder
  if (!stage) {
    return (
      <div className="w-full p-6 bg-white rounded-xl shadow text-gray-500 text-center">
        No Stages
      </div>
    );
  }
  // Helper for updating fields safely
  function updateField(key: keyof Stage, value: any) {
    onChangeStage({ ...stage, [key]: value });
  }

  return (
    <div className="w-full p-6 bg-white rounded-xl shadow">
      {/* ------ NAME --------- */}
      {isEditing ? (
        <input
          className="text-xl font-semibold border px-3 py-2 rounded-lg w-full"
          value={stage.name}
          onChange={(e) => updateField("name", e.target.value)}
        />
      ) : (
        <h2 className="text-xl font-semibold">{stage.name}</h2>
      )}

      {/* ----- DESCRIPTION -------- */}
      <div className="mt-4">
        <h3 className="font-semibold text-gray-700">Description</h3>

        {isEditing ? (
          <textarea
            className="mt-2 w-full border px-3 py-2 rounded-md"
            rows={3}
            value={stage.description}
            onChange={(e) => updateField("description", e.target.value)}
          />
        ) : (
          <p className="mt-2 text-gray-600 whitespace-pre-line">
            {stage.description}
          </p>
        )}
      </div>

      {/* ------ COMMANDS ------- */}
      <div className="mt-6">
        <h3 className="font-semibold text-gray-700">Commands</h3>

        {isEditing ? (
          <StageCommandsEditor
            commands={stage.commands}
            onChange={(cmdStr) => updateField("commands", cmdStr)}
          />
        ) : (
          // Show commands as multi-line text (preserves formatting)
          <pre className="mt-2 p-3 bg-gray-100 rounded-md text-gray-800 whitespace-pre-wrap">
            {stage.commands}
          </pre>
        )}
      </div>
    </div>
  );
}
