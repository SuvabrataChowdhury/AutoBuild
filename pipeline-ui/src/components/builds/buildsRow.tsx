import { TableCell, TableRow } from "../ui/table";
import { CheckCircle, Loader2, XCircle } from "lucide-react";
import type { PipelineBuild } from "../../gen";

interface BuildRowProps {
  build: PipelineBuild
};

export function BuildsRow({ build }: BuildRowProps) {
  const state = build.currentState;
  // const pipelineId = build.pipelineId as string;

  // const [data, setData] = useState<Pipeline>();

  // useEffect(() => {
  //   async function fetchData(id: string) {
  //     const {status, data} = await pipelineApiInstance.getPipelineById(id);

  //     if (status !== 200) {
  //       console.error("Error fetching pipeline")
  //     }
  //     setData(data);
  //   }

  //   fetchData(pipelineId);
  // }, [pipelineId]);

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
