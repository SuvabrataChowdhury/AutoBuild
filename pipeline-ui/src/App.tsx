import React, { useState } from "react";
import { Routes, Route, RouterProvider } from "react-router-dom";
import { router } from "./router/router";
// import LoginPage from "./components/LoginPage";

function App() {
  const [loggedIn, setLoggedIn] = useState(false);

  function handleLogin(credentials: any) {
    if (
      credentials.username === "demo" &&
      credentials.password === "password"
    ) {
      setLoggedIn(true);
      return true;
    }
    return false;
  }

  const [page, setPage] = useState("landing");

  function goToPipeline() {
    setPage("pipeline");
  }

  function goToLanding() {
    setPage("landing");
  }

  function logout() {
    setLoggedIn(false);
    setPage("landing");
  }

  if (!loggedIn) {
    // return <LoginPage onLogin={handleLogin} />;
  }

  return <RouterProvider router={router} />;
}

export default App;
