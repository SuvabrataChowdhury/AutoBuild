import { TableCell, TableRow } from "../ui/table";
import { CheckCircle, Loader2, XCircle } from "lucide-react";
import type { Build, Pipeline } from "../../types/pipeline.types";
import { useEffect, useState } from "react";
import { getPipeline } from "../../services/pipelines.api";

export function BuildsRow({ build }: { build: Build }) {
  const state = build.currentState;
  const pipelineId = build.pipelineId;

  const [data, setData] = useState<Pipeline>();

  useEffect(() => {
    async function fetchData(id: number) {
      const build = await getPipeline(id);
      setData(build);
    }
    fetchData(pipelineId);
  }, [pipelineId]);

  return (
    <TableRow
      onClick={() => {
        window.location.href = `/builds/${build.id}`;
      }}
      className="hover:cursor-pointer"
    >
      <TableCell className="font-semibold text-left">{build.id}</TableCell>
      <TableCell className="font-semibold text-left">{data?.name}</TableCell>

      <TableCell>
        {state === "SUCCESS" && <CheckCircle className="text-green-500" />}
        {state === "FAILED" && <XCircle className="text-red-500" />}
        {state === "RUNNING" && (
          <Loader2 className="animate-spin text-blue-500" />
        )}
      </TableCell>
    </TableRow>
  );
}
