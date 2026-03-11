import { render, screen, act } from "@testing-library/react";
import { describe, beforeEach, it, expect, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";
import RegistrationPage from "../../src/pages/RegistrationPage";

// Mock react-router-dom navigate
const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importActual) => {
  const actual: any = await importActual();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// Define mock for services/auth.api without referencing top-level variables
vi.mock("../../src/services/auth.api", () => ({
  register: vi.fn(),
}));

// Import the mocked function to control per-test behavior
import { register } from "../../src/services/auth.api";

const mockedRegister = vi.mocked(register);

describe("RegistrationPage", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it("renders registration form", () => {
    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );
    expect(screen.getByText("Username")).toBeInTheDocument();
    expect(screen.getByText("Email")).toBeInTheDocument();
    expect(screen.getByText("Password")).toBeInTheDocument();
  });

  it("handles successful registration", async () => {
    // Resolve successfully
    mockedRegister.mockResolvedValueOnce({} as any);
    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );

    // Fill out and submit form
    act(() => {
      (
        screen.getByPlaceholderText("Enter your email") as HTMLInputElement
      ).value = "sam@sam.com";
      (
        screen.getByPlaceholderText("Choose a username") as HTMLInputElement
      ).value = "sam";
      (
        screen.getByPlaceholderText("Enter your password") as HTMLInputElement
      ).value = "password";
    });
    await act(async () => {
      screen.getByText("Register").click();
    });

    // Expect navigation to login page
    expect(mockNavigate).toHaveBeenCalledWith("/login");
  });

  it("displays error on registration failure", async () => {
    // Reject with error
    mockedRegister.mockRejectedValueOnce({
      response: { data: { detail: "Registration error" } },
    });

    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );

    // Fill out and submit form
    act(() => {
      (
        screen.getByPlaceholderText("Enter your email") as HTMLInputElement
      ).value = "sam@sam.com";
      (
        screen.getByPlaceholderText("Choose a username") as HTMLInputElement
      ).value = "sam";
      (
        screen.getByPlaceholderText("Enter your password") as HTMLInputElement
      ).value = "password";
    });
    await act(async () => {
      screen.getByText("Register").click();
    });

    // Expect error message to be displayed
    expect(screen.getByText("Registration error")).toBeInTheDocument();
  });

  it("toggles password visibility", () => {
    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );

    const passwordInput = screen.getByPlaceholderText(
      "Enter your password",
    ) as HTMLInputElement;
    const toggleButton = screen.getByTestId("togglePassword");
    expect(passwordInput.type).toBe("password");

    act(() => {
      toggleButton.click();
    });
    expect(passwordInput.type).toBe("text");

    act(() => {
      toggleButton.click();
    });
    expect(passwordInput.type).toBe("password");
  });

  it("Navigate to Login page on link click", () => {
    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );

    act(() => {
      screen.getByText("Login").click();
    });
    const registerLink = screen.getByText("Login");

    expect(registerLink.getAttribute("href")).toBe("/login");
  });

  it("shows generic error if no detail in error response", async () => {
    // Reject with generic error
    mockedRegister.mockRejectedValueOnce(new Error("Network Error"));

    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );

    // Fill out and submit form
    act(() => {
      (
        screen.getByPlaceholderText("Enter your email") as HTMLInputElement
      ).value = "sam@sam.com";
      (
        screen.getByPlaceholderText("Choose a username") as HTMLInputElement
      ).value = "sam";
      (
        screen.getByPlaceholderText("Enter your password") as HTMLInputElement
      ).value = "password";
    });
    await act(async () => {
      screen.getByText("Register").click();
    });

    // Expect generic error message to be displayed
    expect(screen.getByText("Network Error")).toBeInTheDocument();
  });

  it("Sets the input values correctly", () => {
    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );

    const emailInput = screen.getByPlaceholderText(
      "Enter your email",
    ) as HTMLInputElement;
    const usernameInput = screen.getByPlaceholderText(
      "Choose a username",
    ) as HTMLInputElement;
    const passwordInput = screen.getByPlaceholderText(
      "Enter your password",
    ) as HTMLInputElement;

    act(() => {
      emailInput.value = "sam@sam.com";
      usernameInput.value = "sam";
      passwordInput.value = "password";
    });

    expect(emailInput.value).toBe("sam@sam.com");
    expect(usernameInput.value).toBe("sam");
    expect(passwordInput.value).toBe("password");
  });

  it("has required fields", () => {
    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );

    const emailInput = screen.getByPlaceholderText(
      "Enter your email",
    ) as HTMLInputElement;
    const usernameInput = screen.getByPlaceholderText(
      "Choose a username",
    ) as HTMLInputElement;
    const passwordInput = screen.getByPlaceholderText(
      "Enter your password",
    ) as HTMLInputElement;

    expect(emailInput).toBeRequired();
    expect(usernameInput).toBeRequired();
    expect(passwordInput).toBeRequired();
  });

  it("cannot submit form with empty fields", async () => {
    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );
    await act(async () => {
      screen.getByText("Register").click();
    });

    expect(mockedRegister).not.toHaveBeenCalled();
  });

  it("proper email format is validated", async () => {
    render(
      <MemoryRouter>
        <RegistrationPage />
      </MemoryRouter>,
    );

    const emailInput = screen.getByPlaceholderText(
      "Enter your email",
    ) as HTMLInputElement;

    act(() => {
      emailInput.value = "invalid-email";
    });

    await act(async () => {
      screen.getByText("Register").click();
    });

    expect(mockedRegister).not.toHaveBeenCalled();
  });
});
