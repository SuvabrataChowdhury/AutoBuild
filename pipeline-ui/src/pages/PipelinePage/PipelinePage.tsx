import { useEffect, useState } from "react";
import { SearchBar } from "../../components/pipelines/searchBar";
import { PipelinesTable } from "../../components/pipelines/pipelinesTable";
import { Button } from "../../components/ui/button";
import type { Pipeline } from "../../types/pipeline.types";
import { getPipelines } from "../../services/pipelines.api";
import "./PipelinePage.css";
import NavBar from "../../components/common/navBar";
import { useNavigate } from "react-router-dom";

export default function PipelinePage() {
  const [search, setSearch] = useState("");

  const [data, setData] = useState<Pipeline[]>([]);

  const filtered = data.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase()),
  );
  const navigate = useNavigate();

  useEffect(() => {
    async function fetchData() {
      const pipelines = await getPipelines();
      setData(pipelines);
    }
    fetchData();
  }, []);

  function onCreate(): void {
    navigate("/pipelines/0");
  }

  return (
    <>
      <NavBar></NavBar>
      <div className="main-container min-h-screen justify-center display-flex">
        <div className="container p-20 text-center">
          <h1 className="text-2xl font-bold text-center">MY PIPELINES</h1>

          <div className="flex justify-between items-center mt-10">
            <SearchBar value={search} onChange={setSearch} />

            <Button
              variant="outline"
              className="rounded-full px-6"
              onClick={onCreate}
            >
              Create
            </Button>
          </div>

          <PipelinesTable data={filtered} />
        </div>
      </div>
    </>
  );
}
