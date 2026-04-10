import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../services/api";

export default function Dashboard() {
  const [statistics, setStatistics] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const getStatistics = async () => {
    setLoading(true);
    try {
      const res = await API.get("/statistics");
      setStatistics(res.data);
      setError("");
    } catch (err) {
      setError("Session expired or backend unavailable.");
      localStorage.removeItem("token");
      navigate("/");
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  const goToVoting = () => {
    navigate("/voting");
  };

  useEffect(() => {
    getStatistics();
  }, []);

  return (
    <div className="page-shell">
      <div className="dashboard-card">
        <div className="dashboard-header">
          <div>
            <h1>Secure Voting Dashboard</h1>
            <p className="subtitle">Manage your secure voting experience</p>
          </div>
          <button className="secondary-button" onClick={logout}>
            Log out
          </button>
        </div>

        {error && <div className="alert">{error}</div>}

        <div className="dashboard-actions">
          <button className="primary-button voting-btn" onClick={goToVoting}>
            Cast Your Vote
          </button>
        </div>

        <div className="system-status">
          <h2>System Status</h2>
          {loading ? (
            <div className="loading">Loading system information...</div>
          ) : (
            <div className="status-grid">
              <div className="status-card">
                <h3>Security</h3>
                <div className="status-indicator secure">
                  {statistics?.security || "Enabled"}
                </div>
                <p>AES-256 Encryption</p>
              </div>
              
              <div className="status-card">
                <h3>Total Votes</h3>
                <div className="status-indicator votes">
                  {statistics?.totalVotes || 0}
                </div>
                <p>Verified votes cast</p>
              </div>
              
              <div className="status-card">
                <h3>Candidates</h3>
                <div className="status-indicator candidates">
                  {statistics?.candidatesCount || 0}
                </div>
                <p>Active candidates</p>
              </div>
              
              <div className="status-card">
                <h3>Authentication</h3>
                <div className="status-indicator auth">
                  2FA + JWT
                </div>
                <p>Multi-factor security</p>
              </div>
            </div>
          )}
        </div>

        <div className="security-features">
          <h2>Security Features</h2>
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">Confidentiality</div>
              <h3>Vote Encryption</h3>
              <p>Your vote is encrypted using AES-256 before storage</p>
            </div>
            
            <div className="feature-card">
              <div className="feature-icon">Integrity</div>
              <h3>Digital Signatures</h3>
              <p>Each vote is signed with RSA-2048 to prevent tampering</p>
            </div>
            
            <div className="feature-card">
              <div className="feature-icon">Authentication</div>
              <h3>Secure Login</h3>
              <p>Two-factor authentication with OTP verification</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}