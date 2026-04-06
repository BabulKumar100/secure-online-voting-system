import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../services/api";

const defaultResults = { A: 0, B: 0, C: 0 };

export default function Dashboard() {
  const [results, setResults] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const vote = async (candidate) => {
    try {
      await API.post("/vote", { candidate });
      getResults();
    } catch {
      setError("Unable to submit vote. Please try again.");
    }
  };

  const getResults = async () => {
    setLoading(true);
    try {
      const res = await API.get("/results", {
        params: { t: Date.now() },
        headers: { "Cache-Control": "no-cache" },
      });
      setResults(res.data);
      setError("");
    } catch {
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

  const candidates = ["A", "B", "C"];

  useEffect(() => {
    getResults();
  }, []);

  return (
    <div className="page-shell">
      <div className="dashboard-card">
        <div className="dashboard-header">
          <div>
            <h1>Secure Voting Dashboard</h1>
            <p className="subtitle">Cast your vote and view encrypted results securely.</p>
          </div>
          <button className="secondary-button" onClick={logout}>
            Log out
          </button>
        </div>

        {error && <div className="alert">{error}</div>}

        <div className="vote-actions">
          <button className="primary-button" onClick={() => vote("A")}>Vote A</button>
          <button className="primary-button" onClick={() => vote("B")}>Vote B</button>
          <button className="primary-button" onClick={() => vote("C")}>Vote C</button>
        </div>

        <div className="results-section">
          <h2>Live Results</h2>
          <div className="results-grid">
            {loading ? (
              <div className="empty-state">Loading results...</div>
            ) : (
              candidates.map((candidate) => (
                <div key={candidate} className="result-card">
                  <span className="candidate-name">{candidate}</span>
                  <span className="vote-count">{results?.[candidate] || 0}</span>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}