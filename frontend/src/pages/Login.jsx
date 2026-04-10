import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../services/api";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const login = async () => {
    if (!email.trim() || !password.trim()) {
      setError("Please enter both email and password");
      return;
    }

    setIsLoading(true);
    setError("");
    setMessage("");

    try {
      const res = await API.post("/auth/login", { email, password });
      
      // Login successful, now need OTP verification
      setMessage(res.data.message);
      
      // Redirect to OTP verification page with email
      setTimeout(() => {
        navigate('/otp-verification', { state: { email } });
      }, 1500);
      
    } catch (err) {
      const message = err.response?.data?.error || "Unable to login. Check email and password.";
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="page-shell">
      <div className="auth-card">
        <div className="brand">SecureVote</div>
        <h1>Welcome back</h1>
        <p className="subtitle">Login to your secure voting account with 2FA protection.</p>

        {message && <div className="success-message">{message}</div>}
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
          placeholder="Your password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button className="primary-button" onClick={login} disabled={isLoading}>
          {isLoading ? 'Sending OTP...' : 'Login & Send OTP'}
        </button>

        <div className="security-notice">
          <p>After login, a 6-digit OTP will be sent to your email for verification.</p>
        </div>

        <p className="small-text">
          New here? <Link to="/register">Create an account</Link>
        </p>
      </div>
    </div>
  );
}
