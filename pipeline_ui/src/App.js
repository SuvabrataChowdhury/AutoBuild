import React, { useState } from "react";
import LandingPage from "./components/LandingPage";
import PipelinePage from "./components/PipelinePage";
import LoginPage from "./components/LoginPage";

function App() {
  const [loggedIn, setLoggedIn] = useState(false);

  function handleLogin(credentials) {
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
    return <LoginPage onLogin={handleLogin} />;
  }

  return (
    <div>
      {page === "landing" && <LandingPage goToPipeline={goToPipeline} logout={logout} />}
      {page === "pipeline" && <PipelinePage goToLanding={goToLanding} logout={logout} />}
    </div>
  );
}

export default App;
