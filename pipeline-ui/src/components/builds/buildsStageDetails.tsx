import type { Stage } from "../../types/pipeline.types";

type Props = {
  stage: Stage;
};

export default function StageDetails({ stage }: Props) {
  // If no stage selected, show placeholder
  if (!stage) {
    return (
      <div className="w-full p-6 bg-white rounded-xl shadow text-gray-500 text-center">
        No Stages
      </div>
    );
  }

  return (
    <div className="w-full p-6 bg-white rounded-xl shadow">
      <h2 className="text-xl font-semibold">{stage.name}</h2>

      {/* ----- DESCRIPTION -------- */}
      <div className="mt-4">
        <h3 className="font-semibold text-gray-700">Description</h3>
        <p className="mt-2 text-gray-600 whitespace-pre-line">
          {stage.description}
        </p>
      </div>

      {/* ------ COMMANDS ------- */}
      <div className="mt-6">
        <h3 className="font-semibold text-gray-700">Logs</h3>
        <pre className="mt-2 p-3 bg-gray-100 rounded-md text-gray-800 whitespace-pre-wrap">
          {stage.commands}
        </pre>
      </div>
    </div>
  );
}
