import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import ApiService from "../../service/ApiService";
import '../../style/login&register.css';
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';

const LoginPage = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [message, setMessage] = useState(null);
    const [showForgotPassword, setShowForgotPassword] = useState(false);
    const [showOtpModal, setShowOtpModal] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [otp, setOtp] = useState('');
    const [newPassword, setNewPassword] = useState({
        password: '',
        confirmPassword: ''
    });
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await ApiService.loginUser(formData);
            if (response.status === 200) {
                setMessage("User Successfully Logged in");
                localStorage.setItem('token', response.token);
                localStorage.setItem('role', response.role);
                setTimeout(() => {
                    navigate("/profile")
                }, 1000);
            }
        } catch (error) {
            // Check if the error is related to password
            if (error.response?.data?.error === 'auth_failed' || 
                error.response?.status === 401 || 
                error.response?.data?.message?.toLowerCase().includes('password')) {
                setMessage("Entered password is not matching");
            } else {
                setMessage(error.response?.data.message || error.message || "Unable to login");
            }
        }
    }

    const handleForgotPassword = async () => {
        if (!formData.email) {
            setMessage("Please enter your email address");
            return;
        }
        try {
            await ApiService.sendOtp(formData.email);
            setShowForgotPassword(false);
            setShowOtpModal(true);
            setMessage("OTP sent to your email");
        } catch (error) {
            setMessage(error.response?.data?.message || "Failed to send OTP");
        }
    };

    const verifyOtp = async () => {
        try {
            await ApiService.verifyOtp(formData.email, otp);
            setShowOtpModal(false);
            setShowNewPassword(true);
            setMessage("OTP verified");
        } catch (error) {
            setMessage("Invalid OTP");
        }
    };

    const handlePasswordReset = async () => {
        if (newPassword.password !== newPassword.confirmPassword) {
            setMessage("Passwords do not match");
            return;
        }
        try {
            await ApiService.resetPassword(formData.email, newPassword.password);
            setMessage("Password reset successful!");
            setShowNewPassword(false);
        } catch (error) {
            setMessage("Password reset failed");
        }
    };

    const handleGoogleSuccess = async (credentialResponse) => {
        try {
          console.log("Google credential received:", credentialResponse);
          const response = await ApiService.googleLogin(credentialResponse.credential);
          console.log("Backend response:", response);
          
          if (response.status === 200) {
            localStorage.setItem('token', response.token);
            localStorage.setItem('role', response.role);
            navigate("/profile");
          }
        } catch (error) {
          console.error("Google login error:", error);
          setMessage(error.response?.data?.message || "Google login failed");
        }
      };

    const handleGoogleError = () => {
        setMessage("Google login failed");
    };

    return (
        <div className="login-container">
            <div className="login-form">
                <h2>Welcome Back</h2>
                {message && <p className="message">{message}</p>}
                
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Email Address</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder="Enter your email"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Password</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Enter your password"
                            required
                        />
                    </div>

                    <button type="submit" className="login-btn">Sign In</button>

                    <div className="form-footer">
                        <span onClick={() => setShowForgotPassword(true)}>
                            Forgot Password?
                        </span>
                        <p>
                            Don't have an account? <a href="/register">Create Account</a>
                        </p>
                    </div>
                </form>

                <GoogleOAuthProvider clientId="646368842911-c8fbrq65ih2usst9huv7q6hbq9cjtpc7.apps.googleusercontent.com">
                    <div className="google-signin">
                        <GoogleLogin
                            onSuccess={handleGoogleSuccess}
                            onError={handleGoogleError}
                            useOneTap
                        />
                    </div>
                </GoogleOAuthProvider>
            </div>

            {showForgotPassword && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Reset Password</h3>
                        <input
                            type="email"
                            placeholder="Enter registered email"
                            value={formData.email}
                            onChange={(e) => setFormData({...formData, email: e.target.value})}
                        />
                        <div className="modal-actions">
                            <button onClick={handleForgotPassword}>Send OTP</button>
                            <button onClick={() => setShowForgotPassword(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {showOtpModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Enter OTP</h3>
                        <input
                            type="text"
                            placeholder="6-digit OTP"
                            value={otp}
                            onChange={(e) => setOtp(e.target.value)}
                        />
                        <div className="modal-actions">
                            <button onClick={verifyOtp}>Verify</button>
                            <button onClick={() => setShowOtpModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}

            {showNewPassword && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Set New Password</h3>
                        <input
                            type="password"
                            placeholder="New password"
                            onChange={(e) => setNewPassword({...newPassword, password: e.target.value})}
                        />
                        <input
                            type="password"
                            placeholder="Confirm password"
                            onChange={(e) => setNewPassword({...newPassword, confirmPassword: e.target.value})}
                        />
                        <div className="modal-actions">
                            <button onClick={handlePasswordReset}>Reset Password</button>
                            <button onClick={() => setShowNewPassword(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default LoginPage;