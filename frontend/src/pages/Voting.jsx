import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';
import './Voting.css';

export default function Voting() {
  const [candidates, setCandidates] = useState([]);
  const [selectedCandidate, setSelectedCandidate] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isVoting, setIsVoting] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [hasVoted, setHasVoted] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCandidates();
    checkVotingStatus();
  }, []);

  const fetchCandidates = async () => {
    try {
      const res = await API.get('/candidates');
      setCandidates(res.data);
    } catch (err) {
      setError('Failed to load candidates');
    } finally {
      setIsLoading(false);
    }
  };

  const checkVotingStatus = async () => {
    try {
      const res = await API.get('/has-voted');
      setHasVoted(res.data.hasVoted);
    } catch (err) {
      // If token is invalid, redirect to login
      if (err.response?.status === 401) {
        navigate('/');
      }
    }
  };

  const handleVote = async () => {
    if (!selectedCandidate) {
      setError('Please select a candidate before voting');
      return;
    }

    setIsVoting(true);
    setError('');
    setMessage('');

    try {
      const res = await API.post('/vote', { candidateId: selectedCandidate.id });
      setMessage('Your vote has been securely encrypted and saved!');
      setHasVoted(true);
      
      // Clear selection after successful vote
      setSelectedCandidate(null);
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Failed to submit vote';
      setError(errorMsg);
    } finally {
      setIsVoting(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/');
  };

  if (isLoading) {
    return (
      <div className="voting-container">
        <div className="loading">Loading candidates...</div>
      </div>
    );
  }

  if (hasVoted) {
    return (
      <div className="voting-container">
        <div className="voting-card">
          <div className="voted-success">
            <h2>Vote Successfully Cast!</h2>
            <p>Your vote has been securely encrypted and stored.</p>
            <div className="security-info">
              <h3>Security Features Applied:</h3>
              <ul>
                <li>Vote encrypted with AES-256</li>
                <li>Digital signature verified</li>
                <li>Secure authentication completed</li>
                <li>Vote integrity maintained</li>
              </ul>
            </div>
            <button onClick={handleLogout} className="logout-btn">
              Logout
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="voting-container">
      <div className="voting-header">
        <h1>Secure Online Voting System</h1>
        <p>Cast your vote securely with encryption and digital signatures</p>
        <button onClick={handleLogout} className="logout-btn">
          Logout
        </button>
      </div>

      <div className="voting-card">
        <h2>Select Your Candidate</h2>
        
        <div className="candidates-grid">
          {candidates.map((candidate) => (
            <div
              key={candidate.id}
              className={`candidate-card ${selectedCandidate?.id === candidate.id ? 'selected' : ''}`}
              onClick={() => setSelectedCandidate(candidate)}
            >
              <div className="candidate-symbol">
                {candidate.symbol}
              </div>
              <h3>{candidate.name}</h3>
              <p className="party">{candidate.party}</p>
              <p className="description">{candidate.description}</p>
              <div className="selection-indicator">
                {selectedCandidate?.id === candidate.id && 'Selected'}
              </div>
            </div>
          ))}
        </div>

        {selectedCandidate && (
          <div className="selection-summary">
            <h3>Your Selection:</h3>
            <div className="selected-info">
              <strong>{selectedCandidate.name}</strong> ({selectedCandidate.party})
            </div>
          </div>
        )}

        {message && <div className="success-message">{message}</div>}
        {error && <div className="error-message">{error}</div>}

        <button
          onClick={handleVote}
          className="vote-btn"
          disabled={!selectedCandidate || isVoting}
        >
          {isVoting ? 'Encrypting and Saving...' : 'Submit Vote'}
        </button>

        <div className="security-notice">
          <h4>Security Information:</h4>
          <p>Your vote will be encrypted using AES-256 and signed with RSA-2048 digital signature before storage.</p>
        </div>
      </div>
    </div>
  );
}
