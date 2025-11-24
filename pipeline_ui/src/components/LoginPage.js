import React, { useState } from "react";
import "../LoginPage.css";

function LoginPage({ onLogin }) {
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const success = onLogin(form);
    if (!success) {
      setError("Invalid username or password.");
    }
  };

  return (
    <div style={{ background: "#eef0f3", height: "100vh", display: "flex", alignItems: "center", justifyContent: "center" }}>
      <form
        onSubmit={handleSubmit}
        style={{
          background: "#fff",
          padding: "30px",
          borderRadius: "15px",
          boxShadow: "0px 0px 8px #bbb",
          minWidth: "340px"
        }}
      >
        <h2 style={{ marginBottom: "22px", textAlign: "center" }}>Login</h2>
        <input
          type="text"
          name="username"
          placeholder="Username"
          value={form.username}
          onChange={handleChange}
          style={{
            display: "block", width: "100%", padding: "10px", marginBottom: "14px", fontSize: "16px", borderRadius: "7px", border: "1px solid #bbb"
          }}
          autoFocus
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={form.password}
          onChange={handleChange}
          style={{
            display: "block", width: "100%", padding: "10px", marginBottom: "14px", fontSize: "16px", borderRadius: "7px", border: "1px solid #bbb"
          }}
        />
        {error && <div style={{ color: "#d11c1c", marginBottom: "10px" }}>{error}</div>}
        <button
          type="submit"
          style={{
            width: "100%",
            padding: "12px",
            background: "#222",
            color: "#fff",
            fontWeight: "bold",
            fontSize: "18px",
            borderRadius: "9px",
            border: "none",
            cursor: "pointer"
          }}
        >
          Login
        </button>
      </form>
    </div>
  );
}

export default LoginPage;
