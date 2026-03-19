import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import StageCommandsEditor from "../../../src/components/pipelines/stageCommandsEditor";

describe("StageCommandsEditor", () => {
  const mockOnChange = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("Rendering", () => {
    it("renders textarea element", () => {
      render(<StageCommandsEditor commands="" onChange={mockOnChange} />);

      const textarea = screen.getByRole("textbox");
      expect(textarea).toBeInTheDocument();
      expect(textarea.tagName).toBe("TEXTAREA");
    });

    it("renders with placeholder text", () => {
      render(<StageCommandsEditor commands="" onChange={mockOnChange} />);

      const textarea = screen.getByPlaceholderText("Enter commands here...");
      expect(textarea).toBeInTheDocument();
    });

    it("displays initial commands value", () => {
      const initialCommands = "npm install\nnpm test";
      render(
        <StageCommandsEditor
          commands={initialCommands}
          onChange={mockOnChange}
        />,
      );

      const textarea = screen.getByRole("textbox") as HTMLTextAreaElement;
      expect(textarea.value).toBe(initialCommands);
    });

    it("renders empty textarea when commands is empty string", () => {
      render(<StageCommandsEditor commands="" onChange={mockOnChange} />);

      const textarea = screen.getByRole("textbox") as HTMLTextAreaElement;
      expect(textarea.value).toBe("");
    });
  });

  describe("User Input", () => {
    it("calls onChange when user types", () => {
      render(<StageCommandsEditor commands="" onChange={mockOnChange} />);

      const textarea = screen.getByRole("textbox");
      fireEvent.change(textarea, { target: { value: "npm install" } });

      expect(mockOnChange).toHaveBeenCalledTimes(1);
      expect(mockOnChange).toHaveBeenCalledWith("npm install");
    });

    it("calls onChange with correct value for each keystroke", () => {
      render(<StageCommandsEditor commands="" onChange={mockOnChange} />);

      const textarea = screen.getByRole("textbox");

      fireEvent.change(textarea, { target: { value: "n" } });
      expect(mockOnChange).toHaveBeenCalledWith("n");

      fireEvent.change(textarea, { target: { value: "npm" } });
      expect(mockOnChange).toHaveBeenCalledWith("npm");

      fireEvent.change(textarea, { target: { value: "npm install" } });
      expect(mockOnChange).toHaveBeenCalledWith("npm install");

      expect(mockOnChange).toHaveBeenCalledTimes(3);
    });

    it("handles multi-line input", () => {
      render(<StageCommandsEditor commands="" onChange={mockOnChange} />);

      const textarea = screen.getByRole("textbox");
      const multiLineCommands = "npm install\nnpm test\nnpm run build";

      fireEvent.change(textarea, { target: { value: multiLineCommands } });

      expect(mockOnChange).toHaveBeenCalledWith(multiLineCommands);
    });

    it("handles clearing text", () => {
      render(
        <StageCommandsEditor commands="npm install" onChange={mockOnChange} />,
      );

      const textarea = screen.getByRole("textbox");
      fireEvent.change(textarea, { target: { value: "" } });

      expect(mockOnChange).toHaveBeenCalledWith("");
    });

    it("updates when commands prop changes", () => {
      const { rerender } = render(
        <StageCommandsEditor commands="npm install" onChange={mockOnChange} />,
      );

      let textarea = screen.getByRole("textbox") as HTMLTextAreaElement;
      expect(textarea.value).toBe("npm install");

      rerender(
        <StageCommandsEditor commands="npm test" onChange={mockOnChange} />,
      );

      textarea = screen.getByRole("textbox") as HTMLTextAreaElement;
      expect(textarea.value).toBe("npm test");
    });
  });
});
