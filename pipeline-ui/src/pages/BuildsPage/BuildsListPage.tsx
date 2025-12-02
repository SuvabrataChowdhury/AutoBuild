import { useEffect, useState } from "react";
import { SearchBar } from "../../components/pipelines/searchBar";
import type { Build } from "../../types/pipeline.types";
import { getBuildsList, getPipeline } from "../../services/pipelines.api";
import BuildsTable from "../../components/builds/buildsTable";
import { useParams } from "react-router-dom";

export default function BuildsListPage() {
  const { id } = useParams();
  const [search, setSearch] = useState("");

  const [data, setData] = useState<Build[]>([]);

  const filtered = data.filter(async (p) => {
    const pipeline = await getPipeline(p.pipelineId);
    pipeline.name.toLowerCase().includes(search.toLowerCase());
  });

  useEffect(() => {
    async function fetchData() {
      const builds = await getBuildsList();
      setData(builds);
    }
    fetchData();
  }, [id]);

  return (
    <div className="main-container min-h-screen justify-center display-flex">
      <div className="container p-20 text-center">
        <h1 className="text-2xl font-bold text-center">MY BUILDS</h1>

        <div className="flex justify-between items-center mt-10">
          <SearchBar value={search} onChange={setSearch} />
        </div>

        <BuildsTable data={filtered} />
      </div>
    </div>
  );
}
