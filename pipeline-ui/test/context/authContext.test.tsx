import { render, screen, act } from "@testing-library/react";
import { AuthProvider, useAuth } from "../../src/auth/authContext";
import { describe, beforeEach, it, expect, vi } from "vitest";
import { idp } from "../../src/config/authConfig";

vi.mock("../../src/config/authConfig");

function TestComponent() {
  const {login, logout } = useAuth();
  return (
    <div>
      <span data-testid="token">{idp.getToken()}</span>
      <button onClick={() => login()}>Login</button>
      <button onClick={logout}>Logout</button>
    </div>
  );
}

describe("AuthProvider", () => {
  beforeEach(() => {
    sessionStorage.clear();
  });

  it("provides null token by default", () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>,
    );
    expect(screen.getByTestId("token").textContent).toBe("");
  });
});
