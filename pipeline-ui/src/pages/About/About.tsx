import "./About.css";
import NavBar from "../../components/common/navBar";

export default function About() {
  const team = [
    { name: "Suvabrata Chowdhury", img: "/img/suva.jpg" },
    { name: "Vivian Rodrigues", img: "/img/sam.jpg" },
    { name: "Meenakshi Sarkar", img: "/img/meena.jpg" },
    { name: "Baibhab Dey", img: "/img/baibhab.jpg" },
  ];

  return (
    <>
      <NavBar></NavBar>
      <div className="about-container">
        {/* Hero */}
        <div className="hero">
          <h1 className="hero-title">Automation Made Simple</h1>
          <p className="hero-text">
            Streamlining processes with intelligence and efficiency.
          </p>
        </div>

        {/* Mission Section */}
        <div className="mission-section">
          <h1 className="mission-title">Our Mission</h1>
          <p className="mission-text">
            Our mission is to build an intelligent pipeline automation platform
            that simplifies workflows, enhances collaboration, and accelerates
            project delivery — all while maintaining clean design and
            user-friendly experiences. We aim to empower teams to focus on
            innovation rather than manual tasks.
          </p>
        </div>

        {/* Team Section */}
        <h2 className="team-title">Meet the Team</h2>

        <div className="team-grid">
          {team.map((member) => (
            <div key={member.name} className="card">
              <img src={member.img} alt={member.name} className="avatar" />
              <h3 className="name">{member.name}</h3>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}
