import { useState } from "react";
import { register } from "../services/auth.api";
import { useNavigate, Link } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");

  async function handleRegister(e: React.FormEvent) {
    e.preventDefault();
    try {
      await register({ email, username, password });
      navigate("/login");
    } catch (err: any) {
      console.error(err);
      const message =
        err?.response?.data?.detail || err?.message || "Registration failed";

      setError(message);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 p-4">
      <form
        onSubmit={handleRegister}
        className="bg-white/70 backdrop-blur-lg shadow-xl p-10 rounded-2xl w-full max-w-md space-y-6 border border-white/30"
      >
        {/* Heading */}
        <h1 className="text-3xl font-bold text-center text-gray-800">
          Create Account
        </h1>
        <p className="text-center text-gray-500 mb-4">
          Register to get started
        </p>

        {error && (
          <div className="bg-red-100 border border-red-300 text-red-700 px-4 py-3 rounded-xl">
            {error}
          </div>
        )}

        {/* Email */}
        <div className="space-y-1">
          <label className="text-gray-700 font-medium">Email</label>
          <input
            type="email"
            className="border px-4 py-3 w-full rounded-xl focus:ring-2 focus:ring-blue-400 focus:outline-none transition"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        {/* Username */}
        <div className="space-y-1">
          <label className="text-gray-700 font-medium">Username</label>
          <input
            type="text"
            className="border px-4 py-3 w-full rounded-xl focus:ring-2 focus:ring-blue-400 focus:outline-none transition"
            placeholder="Choose a username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>

        {/* Password */}
        <div className="space-y-1 relative">
          <label className="text-gray-700 font-medium">Password</label>
          <input
            type={showPassword ? "text" : "password"}
            className="border px-4 py-3 w-full rounded-xl focus:ring-2 focus:ring-blue-400 focus:outline-none transition pr-12"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />

          {/* Eye Icon */}
          <button
            type="button"
            className="absolute right-4 top-10 text-gray-500 hover:text-gray-700 transition"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
          </button>
        </div>

        {/* Register Button */}
        <button className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 rounded-xl shadow-md transition active:scale-[0.98]">
          Register
        </button>

        {/* Back to Login */}
        <p className="text-center text-gray-600 text-sm mt-4">
          Already have an account?{" "}
          <Link
            to="/login"
            className="text-blue-600 font-medium hover:underline"
          >
            Login
          </Link>
        </p>
      </form>
    </div>
  );
}
