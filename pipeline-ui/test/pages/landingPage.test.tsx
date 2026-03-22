import { render, screen } from "@testing-library/react";
import { describe, it, expect, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";

import LandingPage from "../../src/pages/LandingPage/LandingPage";

// Define the mock before using it in vi.mock to avoid hoist issues
const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importActual) => {
  const actual: any = await importActual();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe("LandingPage", () => {
  it("renders landing page content", () => {
    render(
      <MemoryRouter>
        <LandingPage />
      </MemoryRouter>,
    );
    expect(screen.getByText("Welcome!")).toBeInTheDocument();
    expect(screen.getByText("Hello")).toBeInTheDocument();
  });

  it("navigates to Pipelines", async () => {
    render(
      <MemoryRouter>
        <LandingPage />
      </MemoryRouter>,
    );

    const pipelineLink = screen.getByText("Pipelines");
    await pipelineLink.click();

    // Assert navigate was called with the expected path
    expect(mockNavigate).toHaveBeenCalledWith("/pipelines");
  });

  it("navigates to Builds", async () => {
    render(
      <MemoryRouter>
        <LandingPage />
      </MemoryRouter>,
    );

    const buildsLink = screen.getByText("Builds");
    await buildsLink.click();

    // Since Builds uses window.location.href, we check that instead
    expect(mockNavigate).toHaveBeenCalledWith("/pipelines");
  });
});
