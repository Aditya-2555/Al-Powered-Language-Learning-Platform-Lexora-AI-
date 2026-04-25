import React, { useState } from 'react';
import axios from 'axios';
import { ArrowRight, Globe } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const LanguageSetup = ({ user, onUpdate }) => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        nativeLanguage: user?.nativeLanguage || 'en'
    });
    const [loading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setErrorMsg('');

        try {
            const res = await axios.put(`http://localhost:8080/api/users/${user.id}/languages`, formData);
            onUpdate(res.data);
            navigate('/dashboard');
        } catch (err) {
            console.error(err);
            setErrorMsg(err.response?.data?.message || 'Failed to save languages.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="glass-panel fade-in" style={{ maxWidth: '500px', margin: '60px auto', textAlign: 'center', padding: '60px 40px' }}>
            <Globe size={48} color="var(--primary)" style={{ marginBottom: '24px' }} />
            <h1 style={{ fontSize: '2.5rem', marginBottom: '16px' }}>Welcome!</h1>
            <p style={{ color: 'var(--text-muted)', marginBottom: '30px', fontSize: '1.1rem' }}>Let's set up your language profile to get started.</p>

            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                <div>
                    <div style={{ textAlign: 'left', color: 'var(--text-muted)', fontSize: '0.95rem', marginBottom: '8px', marginLeft: '8px' }}>My Native Language</div>
                    <select
                        name="nativeLanguage"
                        value={formData.nativeLanguage}
                        onChange={handleChange}
                        className="input-field"
                    >
                        <option value="en">English</option>
                        <option value="es">Spanish</option>
                        <option value="fr">French</option>
                        <option value="de">German</option>
                    </select>
                </div>

                {errorMsg && <div style={{ color: 'var(--danger)', fontSize: '0.9rem', textAlign: 'left' }}>{errorMsg}</div>}

                <button type="submit" className="btn btn-primary" disabled={loading} style={{ padding: '16px', marginTop: '20px', fontSize: '1.1rem' }}>
                    {loading ? 'Saving...' : 'Continue to Dashboard'} <ArrowRight size={20} style={{ marginLeft: '8px' }} />
                </button>
            </form>
        </div>
    );
};

export default LanguageSetup;
