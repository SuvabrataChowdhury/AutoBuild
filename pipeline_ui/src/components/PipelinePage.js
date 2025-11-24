import React, { useState } from "react";
import "../PipelinePage.css";

const steps = [
  { key: "init", label: "Init" },
  { key: "checkstyle", label: "Checkstyle" },
  { key: "unit", label: "Unit Tests" },
  { key: "integration", label: "Integration Tests" },
  { key: "cleanup", label: "Cleanup" }
];

function PipelinePage({ goToLanding, logout }) {
  const [selectedStep, setSelectedStep] = useState(null);

  return (
    <div className="pp-main">
      <header className="pp-header">
        <span className="pp-title">AutoBuild <span className="pp-cog">⚙️</span></span>
        <span className="pp-path">Pipeline 🧩</span>
        <span style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          <button onClick={goToLanding} className="pp-btn">⬅️</button>
          {logout && (
            <button onClick={logout} className="pp-logout-btn">Logout</button>
          )}
        </span>
      </header>
      <main className="pp-content card">
        <div className="pp-pipeline-header">
          <span className="pp-pipeline-title">CI Pipeline</span>
          <span className="pp-pipeline-desc">Pipeline Used for generic CI</span>
        </div>
        <div className="pp-steps">
          {steps.map((step, idx) => (
            <button
              key={step.key}
              className={
                "pp-step-btn" +
                (step.key === "checkstyle" ? " pp-step-big" : "") +
                (step.key === "init" ? " pp-step-init" : "") +
                (selectedStep === step.key ? " pp-step-selected" : "")
              }
              onClick={() => setSelectedStep(step.key)}
            >
              {step.label}
            </button>
          ))}
        </div>
        <div className="pp-run-edit">
          <span>✏️</span><span>▶️</span>
        </div>
        {selectedStep === "checkstyle" && (
          <div className="pp-bashdesc">
            <div className="pp-bash">
              <div className="pp-bash-header">bash</div>
              <pre>
#!/bin/bash
mvn clean checkstyle:check
npm run prettier
              </pre>
            </div>
            <div className="pp-desc">
              <div className="pp-desc-header">Description</div>
              <div>Used for static check on the code.</div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default PipelinePage;
