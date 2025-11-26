import React from "react";

export default function PipelineHeader({ name }: { name: string }) {
  return (
    <div className="mb-4">
      <h1 className="text-2xl font-bold">{name}</h1>
    </div>
  );
}
