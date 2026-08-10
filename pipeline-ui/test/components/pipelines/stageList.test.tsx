import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import StageList from "../../../src/components/pipelines/stageList";
import type { Stage } from "../../../src/gen/api";

const mockStages: Stage[] = [
  { id: "stage-1", name: "Build", scriptType: "bash", command: "npm build" },
  { id: "stage-2", name: "Test", scriptType: "bash", command: "npm test" },
  { id: "stage-3", name: "Deploy", scriptType: "bash", command: "npm deploy" },
];

const defaultProps = {
  stages: mockStages,
  selectedId: "stage-1",
  onSelect: vi.fn(),
  canEdit: false,
  onAddStage: vi.fn(),
  onDeleteStage: vi.fn(),
  onMoveStageUp: vi.fn(),
  onMoveStageDown: vi.fn(),
};

describe("StageList", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("Rendering", () => {
    it("renders the Stages heading", () => {
      render(<StageList {...defaultProps} />);
      expect(screen.getByText("Stages")).toBeInTheDocument();
    });

    it("renders empty message when no stages", () => {
      render(<StageList {...defaultProps} stages={[]} />);
      expect(screen.getByText("No stages yet.")).toBeInTheDocument();
    });

    it("does not render empty message when stages exist", () => {
      render(<StageList {...defaultProps} />);
      expect(screen.queryByText("No stages yet.")).not.toBeInTheDocument();
    });
  });

  describe("canEdit=false mode", () => {
    it("renders a circle div per stage", () => {
      render(<StageList {...defaultProps} canEdit={false} />);
      const circles = screen.getAllByTestId
        ? document.querySelectorAll(".rounded-full.cursor-pointer")
        : [];
      expect(circles.length).toBe(mockStages.length);
    });

    it("calls onSelect with stage id when circle is clicked", () => {
      const onSelect = vi.fn();
      render(<StageList {...defaultProps} canEdit={false} onSelect={onSelect} />);
      const circles = document.querySelectorAll(".rounded-full.cursor-pointer");
      fireEvent.click(circles[1]);
      expect(onSelect).toHaveBeenCalledWith("stage-2");
    });

    it("does not show the Add stage button", () => {
      render(<StageList {...defaultProps} canEdit={false} />);
      expect(screen.queryByTestId("Plus")).not.toBeInTheDocument();
    });
  });

  describe("canEdit=true mode", () => {
    const editProps = { ...defaultProps, canEdit: true };

    it("renders stage names in cards", () => {
      render(<StageList {...editProps} />);
      expect(screen.getByText("Build")).toBeInTheDocument();
      expect(screen.getByText("Test")).toBeInTheDocument();
      expect(screen.getByText("Deploy")).toBeInTheDocument();
    });

    it("renders move up, move down, and delete buttons per stage", () => {
      render(<StageList {...editProps} />);
      expect(screen.getAllByTestId("MoveUp")).toHaveLength(mockStages.length);
      expect(screen.getAllByTestId("MoveDown")).toHaveLength(mockStages.length);
      expect(screen.getAllByTestId("StageDelete")).toHaveLength(mockStages.length);
    });

    it("disables MoveUp for the first stage", () => {
      render(<StageList {...editProps} />);
      const moveUpButtons = screen.getAllByTestId("MoveUp");
      expect(moveUpButtons[0]).toBeDisabled();
      expect(moveUpButtons[1]).not.toBeDisabled();
    });

    it("disables MoveDown for the last stage", () => {
      render(<StageList {...editProps} />);
      const moveDownButtons = screen.getAllByTestId("MoveDown");
      expect(moveDownButtons[moveDownButtons.length - 1]).toBeDisabled();
      expect(moveDownButtons[0]).not.toBeDisabled();
    });

    it("calls onSelect when a stage card is clicked", () => {
      const onSelect = vi.fn();
      render(<StageList {...editProps} onSelect={onSelect} />);
      const cards = screen.getAllByTestId("TestStage");
      fireEvent.click(cards[1]);
      expect(onSelect).toHaveBeenCalledWith("stage-2");
    });

    it("calls onMoveStageUp with stage id when MoveUp is clicked", () => {
      const onMoveStageUp = vi.fn();
      render(<StageList {...editProps} onMoveStageUp={onMoveStageUp} />);
      const moveUpButtons = screen.getAllByTestId("MoveUp");
      fireEvent.click(moveUpButtons[1]);
      expect(onMoveStageUp).toHaveBeenCalledWith("stage-2");
    });

    it("calls onMoveStageDown with stage id when MoveDown is clicked", () => {
      const onMoveStageDown = vi.fn();
      render(<StageList {...editProps} onMoveStageDown={onMoveStageDown} />);
      const moveDownButtons = screen.getAllByTestId("MoveDown");
      fireEvent.click(moveDownButtons[0]);
      expect(onMoveStageDown).toHaveBeenCalledWith("stage-1");
    });

    it("calls onDeleteStage with stage id when Delete is clicked", () => {
      const onDeleteStage = vi.fn();
      render(<StageList {...editProps} onDeleteStage={onDeleteStage} />);
      const deleteButtons = screen.getAllByTestId("StageDelete");
      fireEvent.click(deleteButtons[0]);
      expect(onDeleteStage).toHaveBeenCalledWith("stage-1");
    });

    it("move/delete button clicks do not propagate to onSelect", () => {
      const onSelect = vi.fn();
      render(<StageList {...editProps} onSelect={onSelect} />);
      const moveUpButtons = screen.getAllByTestId("MoveUp");
      fireEvent.click(moveUpButtons[1]);
      expect(onSelect).not.toHaveBeenCalled();
    });

    it("renders the Add stage button", () => {
      render(<StageList {...editProps} />);
      expect(screen.getByTestId("Plus")).toBeInTheDocument();
    });

    it("calls onAddStage when Add button is clicked", () => {
      const onAddStage = vi.fn();
      render(<StageList {...editProps} onAddStage={onAddStage} />);
      fireEvent.click(screen.getByTestId("Plus"));
      expect(onAddStage).toHaveBeenCalledTimes(1);
    });

    it("applies selected styles to the active stage", () => {
      render(<StageList {...editProps} selectedId="stage-2" />);
      const cards = screen.getAllByTestId("TestStage");
      expect(cards[1].parentElement).toHaveClass("bg-blue-50");
      expect(cards[0].parentElement).not.toHaveClass("bg-blue-50");
    });
  });
});
