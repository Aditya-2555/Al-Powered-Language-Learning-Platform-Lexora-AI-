import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { LogOut, Home, MessageSquare, BookOpen, Activity } from 'lucide-react';

const Navbar = ({ user, onLogout }) => {
    const location = useLocation();

    return (
        <nav style={{
            position: 'sticky',
            top: 0,
            zIndex: 1000,
            background: 'var(--bg-main)',
            borderBottom: '1px solid var(--panel-border)',
            padding: '16px 24px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            boxShadow: '0 1px 3px rgba(0,0,0,0.02)'
        }}>
            <Link to={user ? "/dashboard" : "/"} style={{ textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-main)', letterSpacing: '-0.02em' }}>
                    Lexora<span style={{ color: 'var(--accent)' }}> AI</span>
                </span>
            </Link>

            {user ? (
                <div className="flex-center" style={{ gap: '32px' }}>
                    <div style={{ display: 'flex', gap: '20px' }}>
                        <Link to="/dashboard" style={{ textDecoration: 'none', color: location.pathname === '/dashboard' ? 'var(--primary)' : 'var(--text-muted)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem' }}>
                            <Home size={18} /> Dashboard
                        </Link>
                        {user.targetLanguage && (
                            <>
                                <Link to={`/tutor/${user.targetLanguage}`} style={{ textDecoration: 'none', color: location.pathname.includes('/tutor') ? 'var(--primary)' : 'var(--text-muted)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem' }}>
                                    <MessageSquare size={18} /> Practice
                                </Link>
                                <Link to={`/notebook/${user.targetLanguage}`} style={{ textDecoration: 'none', color: location.pathname.includes('/notebook') ? 'var(--primary)' : 'var(--text-muted)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem' }}>
                                    <BookOpen size={18} /> Lexicon
                                </Link>
                                <Link to={`/analytics/${user.targetLanguage}`} style={{ textDecoration: 'none', color: location.pathname.includes('/analytics') ? 'var(--primary)' : 'var(--text-muted)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.95rem' }}>
                                    <Activity size={18} /> Analytics
                                </Link>
                            </>
                        )}
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '16px', borderLeft: '1px solid var(--panel-border)', paddingLeft: '16px' }}>
                        <span style={{ fontWeight: 500, color: 'var(--text-main)', fontSize: '0.95rem' }}>
                            {user.username}
                        </span>
                        <button onClick={onLogout} className="btn btn-outline" style={{ padding: '6px 12px', fontSize: '0.85rem', display: 'flex', gap: '6px' }}>
                            <LogOut size={14} /> Logout
                        </button>
                    </div>
                </div>
            ) : (
                <div className="flex-center" style={{ gap: '24px' }}>
                    {location.pathname === '/' && (
                        <>
                            <a href="#features" style={{ color: 'var(--text-muted)', textDecoration: 'none', fontWeight: 500, fontSize: '0.95rem' }}>Features</a>
                            <a href="#contact" style={{ color: 'var(--text-muted)', textDecoration: 'none', fontWeight: 500, fontSize: '0.95rem' }}>Contact</a>
                        </>
                    )}
                    <Link to="/auth" className="btn btn-primary" style={{ padding: '8px 20px', textDecoration: 'none' }}>Login</Link>
                </div>
            )}
        </nav>
    );
};

export default Navbar;
