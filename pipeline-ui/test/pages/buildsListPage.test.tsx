import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { MemoryRouter } from "react-router-dom";

import BuildsListPage from "../../src/pages/BuildsPage/BuildsListPage";
import { getBuildsList, getPipeline } from "../../src/services/pipelines.api";

// Mock the API calls
vi.mock("../../src/services/pipelines.api", () => ({
  getBuildsList: vi.fn(),
  getPipeline: vi.fn(),
}));

describe("BuildsListPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders builds list page content", () => {
    vi.mocked(getBuildsList).mockResolvedValueOnce([]);

    render(
      <MemoryRouter>
        <BuildsListPage />
      </MemoryRouter>,
    );
    expect(screen.getByText("MY BUILDS")).toBeInTheDocument();
  });

  it("fetches builds on load and displays them", async () => {
    const mockBuilds = [
      {
        id: 1,
        pipelineId: 10,
        pipelineName: "Pipeline 1",
        currentState: "SUCCESS",
        stageBuilds: [],
      },
      {
        id: 2,
        pipelineId: 20,
        pipelineName: "Pipeline 2",
        currentState: "FAILED",
        stageBuilds: [],
      },
    ];

    // Mock the pipelines that will be fetched by BuildsRow
    vi.mocked(getPipeline)
      .mockResolvedValueOnce({ id: 10, name: "Build 1" } as any)
      .mockResolvedValueOnce({ id: 20, name: "Build 2" } as any);

    vi.mocked(getBuildsList).mockResolvedValueOnce(mockBuilds as any);

    render(
      <MemoryRouter>
        <BuildsListPage />
      </MemoryRouter>,
    );

    expect(getBuildsList).toHaveBeenCalled();

    // Wait for the builds to be rendered (the names come from getPipeline)
    const build1 = await screen.findByText("Build 1");
    const build2 = await screen.findByText("Build 2");
    expect(build1).toBeInTheDocument();
    expect(build2).toBeInTheDocument();
  });

  it("Filters builds based on search input", async () => {
    const mockBuilds = [
      {
        id: 1,
        pipelineId: 10,
        pipelineName: "Pipeline 1",
        currentState: "SUCCESS",
        stageBuilds: [],
      },
      {
        id: 2,
        pipelineId: 20,
        pipelineName: "Pipeline 2",
        currentState: "FAILED",
        stageBuilds: [],
      },
    ];

    // Mock the pipelines that will be fetched by BuildsRow
    vi.mocked(getPipeline)
      .mockResolvedValueOnce({ id: 10, name: "Build 1" } as any)
      .mockResolvedValueOnce({ id: 20, name: "Build 2" } as any);

    vi.mocked(getBuildsList).mockResolvedValueOnce(mockBuilds as any);

    render(
      <MemoryRouter>
        <BuildsListPage />
      </MemoryRouter>,
    );

    // Wait for the builds to be rendered
    await screen.findByText("Build 1");
    await screen.findByText("Build 2");

    // Type in the search input
    const searchInput = screen.getByPlaceholderText("Value");
    fireEvent.change(searchInput, { target: { value: "Pipeline 1" } });

    // Wait for the filtering to take effect
    await waitFor(() => {
      expect(screen.getByText("Build 1")).toBeInTheDocument();
      expect(screen.queryByText("Build 2")).not.toBeInTheDocument();
    });
  });
});
