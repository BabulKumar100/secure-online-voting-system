import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../services/api';
import './OtpVerification.css';

export default function OtpVerification() {
  const [email, setEmail] = useState('');
  const [otp, setOtp] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleVerifyOtp = async () => {
    if (!email.trim() || !otp.trim()) {
      setError('Please enter both email and OTP');
      return;
    }

    setIsLoading(true);
    setError('');
    setMessage('');

    try {
      const res = await API.post('/auth/verify-otp', { email, otp });
      localStorage.setItem('token', res.data.token);
      setMessage('Login successful! Redirecting...');
      
      setTimeout(() => {
        navigate('/dashboard');
      }, 1500);
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Invalid OTP. Please try again.';
      setError(errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleResendOtp = async () => {
    if (!email.trim()) {
      setError('Please enter your email address');
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const res = await API.post('/auth/resend-otp', { email });
      setMessage('OTP has been resent to your email');
    } catch (err) {
      const errorMsg = err.response?.data?.error || 'Failed to resend OTP';
      setError(errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="otp-container">
      <div className="otp-card">
        <h2>Verify OTP</h2>
        <p className="otp-subtitle">Enter the 6-digit OTP sent to your email</p>
        
        <div className="form-group">
          <label>Email Address</label>
          <input
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="form-input"
          />
        </div>

        <div className="form-group">
          <label>OTP Code</label>
          <input
            type="text"
            placeholder="Enter 6-digit OTP"
            value={otp}
            onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
            className="form-input otp-input"
            maxLength={6}
          />
        </div>

        {message && <div className="success-message">{message}</div>}
        {error && <div className="error-message">{error}</div>}

        <button 
          onClick={handleVerifyOtp} 
          className="verify-btn"
          disabled={isLoading}
        >
          {isLoading ? 'Verifying...' : 'Verify OTP'}
        </button>

        <div className="resend-section">
          <span>Didn't receive OTP?</span>
          <button 
            onClick={handleResendOtp} 
            className="resend-btn"
            disabled={isLoading}
          >
            Resend OTP
          </button>
        </div>

        <div className="back-to-login">
          <button onClick={() => navigate('/')} className="back-btn">
            Back to Login
          </button>
        </div>
      </div>
    </div>
  );
}
