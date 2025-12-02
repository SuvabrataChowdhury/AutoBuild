import { TableCell, TableRow } from "../ui/table";
import type { Pipeline } from "../../types/pipeline.types";

export function PipelineRow({ pipeline }: { pipeline: Pipeline }) {
  return (
    <TableRow
      onClick={() => {
        window.location.href = `/pipelines/${pipeline.id}`;
      }}
      className="hover:cursor-pointer"
    >
      <TableCell className="font-semibold text-left">{pipeline.id}</TableCell>
      <TableCell className="text-left">{pipeline.name}</TableCell>

      {/* <TableCell>
        {success ? (
          <CheckCircle className="text-green-500" />
        ) : (
          <XCircle className="text-red-500" />
        )}
      </TableCell> */}
    </TableRow>
  );
}
