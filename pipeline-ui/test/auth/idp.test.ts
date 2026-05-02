import { describe, it, expect, vi, beforeEach } from "vitest";
import { KeycloakIdp, LocalMockIdp } from "../../src/auth/idp";
import type Keycloak from "keycloak-js";

describe("KeycloakIdp", () => {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  let mockKeycloak: any;
  let keycloakIdp: KeycloakIdp;

  beforeEach(() => {
    mockKeycloak = {
      login: vi.fn().mockResolvedValue(undefined),
      logout: vi.fn().mockResolvedValue(undefined),
      token: "test-token",
      authenticated: true,
      loadUserInfo: vi.fn().mockResolvedValue({
        preferred_username: "testuser",
        email: "test@example.com",
      }),
    };
    keycloakIdp = new KeycloakIdp(mockKeycloak as Keycloak);
  });

  it("calls keycloak.login()", async () => {
    await keycloakIdp.login();
    expect(mockKeycloak.login).toHaveBeenCalled();
  });

  it("calls keycloak.logout()", async () => {
    await keycloakIdp.logout();
    expect(mockKeycloak.logout).toHaveBeenCalled();
  });

  it("returns the token from keycloak", () => {
    expect(keycloakIdp.getToken()).toBe("test-token");
  });

  it("returns authenticated status from keycloak", () => {
    expect(keycloakIdp.isAuthenticated()).toBe(true);
  });

  it("returns mapped user info from keycloak", async () => {
    const userInfo = await keycloakIdp.getUserInfo();
    expect(userInfo).toEqual({ username: "testuser", email: "test@example.com" });
  });
});

describe("LocalMockIdp", () => {
  const mockUser = { username: "mockuser", email: "mock@example.com" };
  let localIdp: LocalMockIdp;

  beforeEach(() => {
    localIdp = new LocalMockIdp(mockUser);
  });

  it("login resolves without error", async () => {
    await expect(localIdp.login()).resolves.toBeUndefined();
  });

  it("logout resolves without error", async () => {
    await expect(localIdp.logout()).resolves.toBeUndefined();
  });

  it("returns dummy token", () => {
    expect(localIdp.getToken()).toBe("dummy");
  });

  it("returns true for isAuthenticated", () => {
    expect(localIdp.isAuthenticated()).toBe(true);
  });

  it("returns stored user info", async () => {
    const userInfo = await localIdp.getUserInfo();
    expect(userInfo).toEqual(mockUser);
  });
});
