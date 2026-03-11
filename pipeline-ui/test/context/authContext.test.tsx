import { render, screen, act } from "@testing-library/react";
import { AuthProvider, useAuth } from "../../src/context/authContext";
import { describe, beforeEach, it, expect } from "vitest";

function TestComponent() {
  const { token, login, logout } = useAuth();
  return (
    <div>
      <span data-testid="token">{token}</span>
      <button onClick={() => login("test-token")}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
}

describe("AuthProvider", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it("provides null token by default", () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>,
    );
    expect(screen.getByTestId("token").textContent).toBe("");
  });

  it("login sets token and updates localStorage", () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>,
    );
    act(() => {
      screen.getByText("Login").click();
    });
    expect(screen.getByTestId("token").textContent).toBe("test-token");
    expect(localStorage.getItem("token")).toBe("test-token");
  });

  it("logout clears token and localStorage", () => {
    localStorage.setItem("token", "existing-token");
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>,
    );
    act(() => {
      screen.getByText("Logout").click();
    });
    expect(screen.getByTestId("token").textContent).toBe("");
    expect(localStorage.getItem("token")).toBeNull();
  });

  it("initializes token from localStorage", () => {
    localStorage.setItem("token", "persisted-token");
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>,
    );
    expect(screen.getByTestId("token").textContent).toBe("persisted-token");
  });
});
