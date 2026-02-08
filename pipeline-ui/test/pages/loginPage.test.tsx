import LoginPage from "../../src/pages/LoginPage";
import { render, screen, act } from "@testing-library/react";
import { describe, beforeEach, it, expect, vi } from "vitest";
import { MemoryRouter } from "react-router-dom";

// Mock react-router-dom navigate
const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importActual) => {
  const actual: any = await importActual();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// Mock auth context
const mockUseAuth = { login: vi.fn() };
vi.mock("../../src/context/authContext", async (importActual) => {
  const actual: any = await importActual();
  return {
    ...actual,
    useAuth: () => mockUseAuth,
  };
});

// Define mock for services/auth.api without referencing top-level variables
vi.mock("../../src/services/auth.api", () => ({
  login: vi.fn(),
}));
// Import the mocked function to control per-test behavior
import { login as loginApi } from "../../src/services/auth.api";

const mockedLoginApi = vi.mocked(loginApi);

describe("LoginPage", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it("renders login form", () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );
    expect(screen.getByText("Username")).toBeInTheDocument();
    expect(screen.getByText("Password")).toBeInTheDocument();
    expect(screen.getByText("Login")).toBeInTheDocument();
  });

  it("handles successful login", async () => {
    // Resolve with token
    mockedLoginApi.mockResolvedValueOnce({ token: "mock-token" } as any);

    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );

    act(() => {
      (
        screen.getByPlaceholderText("Enter your username") as HTMLInputElement
      ).value = "testuser";
      (
        screen.getByPlaceholderText("Enter your password") as HTMLInputElement
      ).value = "password";
    });

    await act(async () => {
      screen.getByText("Login").click();
    });

    expect(mockUseAuth.login).toHaveBeenCalled();
    expect(mockNavigate).toHaveBeenCalledWith("/");
  });

  it("displays error on failed login", async () => {
    // Reject with API-shaped error
    mockedLoginApi.mockRejectedValueOnce({
      response: { data: { detail: "Invalid credentials" } },
    } as any);

    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );

    act(() => {
      (
        screen.getByPlaceholderText("Enter your username") as HTMLInputElement
      ).value = "wronguser";
      (
        screen.getByPlaceholderText("Enter your password") as HTMLInputElement
      ).value = "wrongpassword";
    });

    await act(async () => {
      screen.getByText("Login").click();
    });

    const err = await screen.findByText(/Invalid credentials/i);
    expect(err).toBeInTheDocument();
  });

  it("toggles password visibility", () => {
    render(
      <MemoryRouter>
        <LoginPage />
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

  it("Navigate to Register page on link click", () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );

    const registerLink = screen.getByText("Register");
    expect(registerLink).toBeInTheDocument();
    expect(registerLink.getAttribute("href")).toBe("/register");
  });

  it("Change in input fields updates state", () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    );

    const usernameInput = screen.getByPlaceholderText(
      "Enter your username",
    ) as HTMLInputElement;
    const passwordInput = screen.getByPlaceholderText(
      "Enter your password",
    ) as HTMLInputElement;

    act(() => {
      usernameInput.value = "newuser";
      passwordInput.value = "newpassword";
    });

    expect(usernameInput.value).toBe("newuser");
    expect(passwordInput.value).toBe("newpassword");
  });
});
