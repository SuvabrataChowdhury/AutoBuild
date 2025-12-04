import {
  Table,
  TableBody,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { PipelineRow } from "./pipelineRow";
import type { Pipeline } from "../../types/pipeline.types";

export function PipelinesTable({ data }: { data: Pipeline[] }) {
  return (
    <div className="mt-6 shadow-lg rounded-xl overflow-hidden bg-white">
      <Table>
        <TableHeader>
          <TableRow className="bg-gray-200">
            <TableHead className="w-[20%] text-left">ID</TableHead>
            <TableHead className="w-[80%] font-semibold">Name</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {data.map((p) => (
            <PipelineRow key={p.id} pipeline={p} />
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
