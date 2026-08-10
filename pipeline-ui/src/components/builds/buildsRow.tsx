import { TableCell, TableRow } from "../ui/table";
import { CheckCircle, Loader2, XCircle } from "lucide-react";
import type { PipelineBuild } from "../../gen";

interface BuildRowProps {
  build: PipelineBuild
};

export function BuildsRow({ build }: BuildRowProps) {
  const state = build.currentState;

  return (
    <TableRow
      onClick={() => {
        window.location.href = `/builds/${build.id}`;
      }}
      className="hover:cursor-pointer"
    >
      <TableCell className="font-semibold text-left">{build.id}</TableCell>
      <TableCell className="font-semibold text-left">{build.pipelineName}</TableCell>

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
