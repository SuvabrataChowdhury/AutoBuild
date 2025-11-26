import { TableCell, TableRow } from "../ui/table";
import { CheckCircle, XCircle } from "lucide-react";
import type { Pipeline } from "../../types/pipeline.types";

export function PipelineRow({ pipeline }: { pipeline: Pipeline }) {
  const success = pipeline.status === "success";

  return (
    <TableRow
      onClick={() => {
        window.location.href = `/pipelines/${pipeline.id}`;
      }}
      className="hover:cursor-pointer"
    >
      <TableCell className="font-semibold text-left">{pipeline.name}</TableCell>
      <TableCell className="text-left">{pipeline.description}</TableCell>
      <TableCell className="text-left">{pipeline.dateCreated}</TableCell>

      <TableCell>
        {success ? (
          <CheckCircle className="text-green-500" />
        ) : (
          <XCircle className="text-red-500" />
        )}
      </TableCell>
    </TableRow>
  );
}
