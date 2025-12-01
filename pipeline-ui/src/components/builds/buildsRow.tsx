import { TableCell, TableRow } from "../ui/table";
import { CheckCircle, XCircle } from "lucide-react";
import type { Build, Pipeline } from "../../types/pipeline.types";

export function BuildsRow({ build }: { build: Build }) {
  const success = build.status === "success";

  return (
    <TableRow
      onClick={() => {
        window.location.href = `/builds/start/${build.id}`;
      }}
      className="hover:cursor-pointer"
    >
      <TableCell className="font-semibold text-left">{build.name}</TableCell>
      <TableCell className="text-left">description</TableCell>
      <TableCell className="text-left">date</TableCell>

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
