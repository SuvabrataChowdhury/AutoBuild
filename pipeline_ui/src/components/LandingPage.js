import React from "react";
import "../LandingPage.css";

const builds = [
  { id: 1122, status: "success", time: "2025-11-01 20:47" },
  { id: 1122, status: "success", time: "2025-11-01 20:47" },
  { id: 1122, status: "failed", time: "2025-11-01 20:47" },
  { id: 1122, status: "pending", time: "2025-11-01 20:47" },
  { id: 1122, status: "success", time: "2025-11-01 20:47" },
];

function statusColor(status) {
  switch (status) {
    case "success": return "#4fd64f";
    case "failed": return "#e74c3c";
    case "pending": return "#b2bec3";
    default: return "#4fd64f";
  }
}

function LandingPage({ goToPipeline, logout }) {
  return (
    <div className="lp-main">
      <header className="lp-header">
        <div className="lp-title">AutoBuild <span className="lp-cog">⚙️</span></div>
        <div style={{ display: "flex", alignItems: "center", gap: "16px" }}>
          <div className="lp-username">username 👤</div>
          {logout && (
            <button onClick={logout} className="lp-logout-btn">Logout</button>
          )}
        </div>
      </header>
      <div className="lp-body">
        <section className="lp-buildlist card">
          <h2 className="lp-section-title">Recent Builds</h2>
          {builds.map((build, idx) => (
            <div key={idx} className="lp-build">
              <span style={{
                display: 'inline-block',
                width: '25px', height: '25px', borderRadius: '50%',
                border: '5px solid ' + statusColor(build.status), marginRight: '10px', background: '#fff', boxShadow: '0 1px 6px #d7d7d7',
              }}></span>
              <span style={{ fontWeight: 'bold', color: statusColor(build.status), fontSize: '16px' }}>
                Build #{build.id}
              </span>
              <span style={{ marginLeft: '7px', color: "#636e72", fontWeight: 'normal', fontSize: '15px' }}>
                At {build.time}
              </span>
            </div>
          ))}
        </section>
        <section className="lp-panelgrid card">
          <button onClick={goToPipeline} className="lp-panel">Pipelines</button>
          <button className="lp-panel">Jobs</button>
          <button className="lp-panel">All Builds</button>
          <button className="lp-panel">Configs</button>
        </section>
      </div>
      <section className="lp-section card">
        <ul>
          <li>Landing Page</li>
          <li>Pipelines - LR</li>
          <li>Pipeline - OP</li>
          <li>Build(s,L1)</li>
          <li>Configs(env configs, timings) - later scope</li>
          <li>Stage Templates</li>
        </ul>
      </section>
      <div className="lp-bottom"></div>
    </div>
  );
}

export default LandingPage;
