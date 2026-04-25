import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Globe, Award, MessageSquare, ArrowRight, BookOpen, Send } from 'lucide-react';

const Landing = () => {
    const navigate = useNavigate();
    const [contactForm, setContactForm] = useState({ name: '', email: '', message: '' });
    const [contactStatus, setContactStatus] = useState('');

    const handleContactSubmit = async (e) => {
        e.preventDefault();
        setContactStatus('Sending...');
        try {
            await axios.post('http://localhost:8080/api/contact', contactForm);
            alert('Thank you for contacting us!');
            setContactForm({ name: '', email: '', message: '' });
            setContactStatus('');
        } catch (err) {
            console.error('Contact submit failed', err);
            alert('Failed to send message.');
            setContactStatus('');
        }
    };

    return (
        <div className="container" style={{ paddingTop: '80px', paddingBottom: '80px' }}>
            {/* Hero Section */}
            <section className="hero-section" style={{ textAlign: 'center', marginBottom: '80px' }}>
                <Globe size={48} className="text-primary" style={{ marginBottom: '24px', margin: '0 auto' }} />
                <h1 style={{ fontSize: '3.5rem', fontWeight: '800', marginBottom: '16px', color: 'var(--text-main)' }}>
                    Lexora AI
                </h1>
                <p style={{ fontSize: '1.25rem', color: 'var(--text-muted)', maxWidth: '600px', margin: '0 auto 32px auto' }}>
                    AI-Powered Language Learning & Intelligence Platform.
                </p>
                <div className="flex-center" style={{ gap: '16px' }}>
                    <button className="btn btn-primary" onClick={() => navigate('/auth')}>
                        Get Started <ArrowRight size={18} style={{ marginLeft: '8px' }} />
                    </button>
                    <button className="btn btn-outline" onClick={() => document.getElementById('features').scrollIntoView({ behavior: 'smooth' })}>
                        View Features
                    </button>
                </div>
            </section>

            {/* Features Section */}
            <section id="features" style={{ marginBottom: '80px' }}>
                <div style={{ textAlign: 'center', marginBottom: '40px' }}>
                    <h2 style={{ fontSize: '2rem' }}>Why Choose Lexora AI?</h2>
                    <p className="text-muted mt-2">Enterprise-grade tools scaled down for personal fluency.</p>
                </div>

                <div className="card-grid">
                    <div className="card">
                        <MessageSquare className="text-primary mb-4" size={32} />
                        <h3 style={{ fontSize: '1.2rem', marginBottom: '8px' }}>Interactive AI Tutor</h3>
                        <p className="text-muted text-sm">Simulate real-life conversations with an advanced AI tutor that dynamically adapts to your proficiency level.</p>
                    </div>

                    <div className="card">
                        <Award className="text-primary mb-4" size={32} />
                        <h3 style={{ fontSize: '1.2rem', marginBottom: '8px' }}>Progress Analytics</h3>
                        <p className="text-muted text-sm">Track advanced metrics like lesson streak, strong/weak module mapping, and daily goals completion.</p>
                    </div>

                    <div className="card">
                        <BookOpen className="text-primary mb-4" size={32} />
                        <h3 style={{ fontSize: '1.2rem', marginBottom: '8px' }}>Spaced Repetition</h3>
                        <p className="text-muted text-sm">A centralized vocabulary notebook seamlessly tracks missed problems using spaced repetition logic.</p>
                    </div>
                </div>
            </section>

            {/* Contact Section */}
            <section id="contact" style={{ maxWidth: '500px', margin: '0 auto', textAlign: 'center', marginBottom: '80px' }}>
                <div className="card">
                    <h2 style={{ fontSize: '1.8rem', marginBottom: '8px' }}>Contact Us</h2>
                    <p className="text-muted mb-4 text-sm">Enterprise inquiries or account assistance.</p>

                    <form onSubmit={handleContactSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                        <input
                            type="text"
                            placeholder="Your Name"
                            value={contactForm.name}
                            onChange={(e) => setContactForm({ ...contactForm, name: e.target.value })}
                            required
                            className="input-field"
                        />
                        <input
                            type="email"
                            placeholder="Your Email"
                            value={contactForm.email}
                            onChange={(e) => setContactForm({ ...contactForm, email: e.target.value })}
                            required
                            className="input-field"
                        />
                        <textarea
                            placeholder="Your Message..."
                            value={contactForm.message}
                            onChange={(e) => setContactForm({ ...contactForm, message: e.target.value })}
                            required
                            rows={4}
                            className="input-field"
                            style={{ resize: 'vertical' }}
                        />
                        <button type="submit" className="btn btn-primary" disabled={contactStatus !== ''}>
                            {contactStatus || 'Submit Inquiry'}
                        </button>
                    </form>
                </div>
            </section>

            {/* Footer */}
            <footer style={{ textAlign: 'center', padding: '24px 0', borderTop: '1px solid var(--panel-border)', color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                <p>&copy; {new Date().getFullYear()} Lexora AI. All rights reserved.</p>
            </footer>
        </div>
    );
};

export default Landing;
