import React, { useState } from 'react';
import axios from 'axios';
import { Sparkles, ArrowRight, UserPlus, LogIn } from 'lucide-react';

const Home = ({ onLogin }) => {
    const [isLogin, setIsLogin] = useState(true);
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        username: '',
        password: '',
        nativeLanguage: null,
        targetLanguage: null
    });
    const [loading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!formData.username.trim() || !formData.password.trim()) return;
        if (!isLogin && (!formData.name.trim() || !formData.email.trim())) {
            setErrorMsg('Please provide your name and email to sign up.');
            return;
        }

        setLoading(true);
        setErrorMsg('');

        try {
            const endpoint = isLogin ? '/api/users/login' : '/api/users/signup';
            const payload = isLogin
                ? { username: formData.username, password: formData.password }
                : formData; // includes name, email, nativeLanguage, targetLanguage

            const res = await axios.post(`http://localhost:8080${endpoint}`, payload);
            if (!isLogin) {
                // Mimic waiting for email dispatch mentally for UX
                setTimeout(() => {
                    onLogin(res.data);
                }, 500);
            } else {
                onLogin(res.data);
            }
        } catch (err) {
            console.error(err);
            if (err.code === 'ERR_NETWORK') {
                setErrorMsg('Network error: Cannot connect to server. Ensure backend is running.');
            } else if (err.response && err.response.data && err.response.data.message) {
                setErrorMsg(err.response.data.message);
            } else if (err.response && err.response.data && err.response.data.error) {
                setErrorMsg(err.response.data.error);
            } else {
                setErrorMsg(err.message || 'Authentication failed. Check credentials or connection.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="glass-panel fade-in" style={{ maxWidth: '600px', margin: '40px auto', textAlign: 'center', padding: '60px 40px' }}>
            <Sparkles size={48} color="var(--primary)" style={{ marginBottom: '24px' }} />
            <h1 style={{ fontSize: '3rem', marginBottom: '16px' }}>Learn a language for free. Forever.</h1>

            <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginBottom: '30px' }}>
                <button
                    className={`btn ${isLogin ? 'btn-primary' : 'btn-outline'}`}
                    onClick={() => { setIsLogin(true); setErrorMsg(''); }}
                >
                    <LogIn size={18} style={{ marginRight: '8px' }} /> Login
                </button>
                <button
                    className={`btn ${!isLogin ? 'btn-primary' : 'btn-outline'}`}
                    onClick={() => { setIsLogin(false); setErrorMsg(''); }}
                >
                    <UserPlus size={18} style={{ marginRight: '8px' }} /> Sign Up
                </button>
            </div>

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px', maxWidth: '300px', margin: '0 auto' }}>

                {!isLogin && (
                    <>
                        <input
                            type="text"
                            name="name"
                            placeholder="Full Name"
                            value={formData.name}
                            onChange={handleChange}
                            required={!isLogin}
                            className="input-field"
                        />
                        <input
                            type="email"
                            name="email"
                            placeholder="Email Address"
                            value={formData.email}
                            onChange={handleChange}
                            required={!isLogin}
                            className="input-field"
                        />
                    </>
                )}

                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={formData.username}
                    onChange={handleChange}
                    required
                    className="input-field"
                />

                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={formData.password}
                    onChange={handleChange}
                    required
                    className="input-field"
                />

                {!isLogin && (
                    <div style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '10px' }}>
                        You will select your languages after signing up!
                    </div>
                )}

                {errorMsg && <div style={{ color: 'var(--danger)', fontSize: '0.9rem', textAlign: 'left' }}>{errorMsg}</div>}

                <button type="submit" className="btn btn-primary" disabled={loading} style={{ padding: '16px', marginTop: '10px' }}>
                    {loading ? 'Processing...' : (isLogin ? 'Login' : 'Create Account')} <ArrowRight size={20} style={{ marginLeft: '8px' }} />
                </button>
            </form>
        </div>
    );
};

export default Home;
