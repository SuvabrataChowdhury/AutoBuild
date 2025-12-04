import { useEffect, useState } from "react";
import { Settings } from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function NavBar() {
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const t = setTimeout(() => setLoading(false), 600);
    return () => clearTimeout(t);
  }, []);

  if (loading) {
    return (
      <div className="fixed inset-0 flex items-center justify-center bg-[#0F0F11] text-white z-50">
        <Settings className="animate-spin" size={48} />
        <p className="ml-4 text-xl tracking-wide">Loading...</p>
      </div>
    );
  }

  return (
    <nav
      className="
        w-full sticky top-0 z-40
        h-12
        bg-[#444]   /* lighter than pure black (#000 orrgb(75, 75, 75)) */
        text-white
        shadow-lg
        py-4 px-8 flex items-center justify-between
      "
    >
      {/* Left Logo (Clickable Home) */}
      <div
        className="flex items-center gap-3 group cursor-pointer select-none"
        onClick={() => navigate("/")}
      >
        <Settings
          size={28}
          className="transition-transform duration-300 group-hover:rotate-180"
        />
        <span className="text-xl font-semibold tracking-wide">AutoBuild</span>
      </div>

      {/* Center Navigation */}
      <div className="flex items-center gap-8 text-lg">
        <NavButton label="Home" route="/" />
        <NavButton label="Pipelines" route="/pipelines" />
        <NavButton label="Builds" route="/builds" />
      </div>

      <div className="w-[80px]"></div>
    </nav>
  );
}

function NavButton({ label, route }: { label: string; route: string }) {
  const navigate = useNavigate();

  return (
    <button
      onClick={() => navigate(route)}
      className="
        relative px-1 py-2 rounded-lg
        hover:bg-white/10 
        transition-all tracking-wide
      "
    >
      {label}
      <span
        className="
          absolute left-1/2 bottom-1 -translate-x-1/2 
          w-0 h-0.5 bg-white transition-all duration-300 
          hover:w-full
        "
      ></span>
    </button>
  );
}
