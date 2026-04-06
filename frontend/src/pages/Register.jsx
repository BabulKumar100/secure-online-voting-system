import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../services/api";

export default function Register() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const register = async () => {
    if (!email.trim() || !password.trim()) {
      setError("Please enter both email and password");
      return;
    }

    try {
      await API.post("/auth/register", { email, password });
      setMessage("Registration complete. You can now log in.");
      setError("");
      setTimeout(() => navigate("/"), 1200);
    } catch (err) {
     const message =
         err.response?.data?.error || err.response?.data?.message || "Registration failed. Please try again.";
     setError(message);
    }
  };

  return (
    <div className="page-shell">
      <div className="auth-card">
        <div className="brand">SecureVote</div>
        <h1>Create account</h1>
        <p className="subtitle">Join the secure voting platform.</p>

        {message && <div className="success">{message}</div>}
        {error && <div className="alert">{error}</div>}

        <label>Email</label>
        <input
          type="email"
          placeholder="you@example.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <label>Password</label>
        <input
          type="password"
          placeholder="Create password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button className="primary-button" onClick={register}>
          Register
        </button>

        <p className="small-text">
          Already registered? <Link to="/">Login here</Link>
        </p>
      </div>
    </div>
  );
}