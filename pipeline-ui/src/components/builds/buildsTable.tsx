import {
  Table,
  TableBody,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { BuildsRow } from "./buildsRow";
import type { PipelineBuild } from "../../gen";

export default function BuildsTable({ data }: { data: PipelineBuild[] }) {
  return (
    <div className="mt-6 shadow-lg rounded-xl overflow-hidden bg-white">
      <Table>
        <TableHeader>
          <TableRow className="bg-gray-200">
            <TableHead className="w-[30%] font-semibold">Build ID</TableHead>
            <TableHead className="w-[20%] font-semibold">Name</TableHead>
            <TableHead className="w-[15%] font-semibold">Status</TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {data.map((p) => (
            <BuildsRow key={p.id} build={p} />
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
