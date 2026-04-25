import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { BookOpen, MessageCircle, Star, Activity, Mic, ChevronRight, Archive } from 'lucide-react';
import GoalProgressCard from '../components/GoalProgressCard';
import DailyChallengeCard from '../components/DailyChallengeCard';

const languages = [
    { code: 'en', name: 'English', flag: '🇬🇧' },
    { code: 'es', name: 'Spanish', flag: '🇪🇸' },
    { code: 'fr', name: 'French', flag: '🇫🇷' },
    { code: 'de', name: 'German', flag: '🇩🇪' }
];

const Dashboard = ({ user }) => {
    const navigate = useNavigate();
    const [progressData, setProgressData] = useState({});
    const [loading, setLoading] = useState(true);
    const displayLanguages = languages.filter(lang => lang.code !== user.nativeLanguage);
    const [focusLang, setFocusLang] = useState(displayLanguages[0]?.code || 'es');
    const [goals, setGoals] = useState([]);

    useEffect(() => {
        const fetchProgress = async () => {
            try {
                const promises = displayLanguages.map(lang =>
                    axios.get(`http://localhost:8080/api/users/${user.id}/progress/${lang.code}`)
                );
                const results = await Promise.all(promises);

                const newProgress = {};
                displayLanguages.forEach((lang, index) => {
                    newProgress[lang.code] = results[index].data;
                });
                setProgressData(newProgress);
            } catch (err) {
                console.error("Error fetching progress", err);
            } finally {
                setLoading(false);
            }
        };

        fetchProgress();
    }, [user.id, user.nativeLanguage]);

    useEffect(() => {
        if (focusLang) {
            axios.get(`http://localhost:8080/api/goals?userId=${user.id}&languageCode=${focusLang}`)
                .then(res => setGoals(res.data))
                .catch(err => console.error("Could not fetch goals", err));
        }
    }, [focusLang, user.id]);

    if (loading) return (
        <div className="flex-center" style={{ height: '60vh' }}>
            <div className="text-primary text-center">
                <Activity size={48} className="pulse" />
                <h2 className="mt-4" style={{ fontSize: '1.2rem', fontWeight: 500 }}>Loading Dashboard...</h2>
            </div>
        </div>
    );

    const activeLangObj = languages.find(l => l.code === focusLang);

    return (
        <div className="fade-in container" style={{ paddingTop: '40px', paddingBottom: '40px' }}>
            <header className="mb-4" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                <div>
                    <h1 style={{ margin: 0 }}>Welcome back, {user.username}!</h1>
                    <p className="text-muted" style={{ fontSize: '1rem', marginTop: '4px' }}>Ready to conquer your daily goals?</p>
                </div>
            </header>

            <div className="dashboard-grid">
                {/* LEFT COLUMN */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                    
                    {/* Focus Language Banner */}
                    <div className="card" style={{ padding: '0', overflow: 'hidden' }}>
                        <div style={{ background: 'var(--card-hover)', padding: '24px', display: 'flex', gap: '24px', alignItems: 'center', borderBottom: '1px solid var(--panel-border)' }}>
                            <div style={{ fontSize: '3rem', filter: 'drop-shadow(0 2px 4px rgba(0,0,0,0.1))' }}>
                                {activeLangObj?.flag}
                            </div>
                            <div style={{ flex: 1 }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                                    <div>
                                        <h2 style={{ margin: 0, fontSize: '1.4rem' }}>{activeLangObj?.name} Focus</h2>
                                        <div style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
                                            {displayLanguages.map(l => (
                                                <button 
                                                    key={l.code}
                                                    onClick={() => setFocusLang(l.code)}
                                                    className={`btn btn-outline`} 
                                                    style={{ padding: '4px 12px', fontSize: '0.85rem', borderRadius: '20px', 
                                                        borderColor: focusLang === l.code ? 'var(--primary)' : 'var(--panel-border)',
                                                        background: focusLang === l.code ? 'var(--primary)' : 'transparent',
                                                        color: focusLang === l.code ? '#fff' : 'var(--text-main)',
                                                    }}
                                                >
                                                    {l.name}
                                                </button>
                                            ))}
                                        </div>
                                    </div>
                                    <div style={{ textAlign: 'right' }}>
                                        <div style={{ fontSize: '1.6rem', fontWeight: 'bold', color: 'var(--primary)' }}>{progressData[focusLang]?.xp || 0} XP</div>
                                        <div className="text-muted text-sm flex-center" style={{ gap: '4px', fontWeight: 600 }}>
                                            <Star size={16} fill="var(--warning)" color="var(--warning)" /> Level {progressData[focusLang]?.level || 1}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <div style={{ padding: '24px' }}>
                            <div className="card-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))' }}>
                                <button className="card highlight flex-center" style={{ flexDirection: 'column', gap: '12px', padding: '24px', boxShadow: 'none' }} onClick={() => navigate(`/lesson/${focusLang}`)}>
                                    <BookOpen size={32} className="text-primary" />
                                    <span style={{ fontWeight: 600, fontSize: '0.95rem' }}>Lesson</span>
                                </button>
                                <button className="card highlight flex-center" style={{ flexDirection: 'column', gap: '12px', padding: '24px', boxShadow: 'none' }} onClick={() => navigate(`/tutor/${focusLang}`)}>
                                    <MessageCircle size={32} color="var(--accent)" />
                                    <span style={{ fontWeight: 600, fontSize: '0.95rem' }}>Practice</span>
                                </button>
                                <button className="card highlight flex-center" style={{ flexDirection: 'column', gap: '12px', padding: '24px', boxShadow: 'none' }} onClick={() => navigate(`/notebook/${focusLang}`)}>
                                    <Archive size={32} color="var(--primary)" />
                                    <span style={{ fontWeight: 600, fontSize: '0.95rem' }}>Lexicon</span>
                                </button>
                                <button className="card highlight flex-center" style={{ flexDirection: 'column', gap: '12px', padding: '24px', boxShadow: 'none' }} onClick={() => navigate(`/speaking/${focusLang}`)}>
                                    <Mic size={32} color="var(--warning)" />
                                    <span style={{ fontWeight: 600, fontSize: '0.95rem' }}>Speaking</span>
                                </button>
                                <button className="card highlight flex-center" style={{ flexDirection: 'column', gap: '12px', padding: '24px', boxShadow: 'none' }} onClick={() => navigate(`/analytics/${focusLang}`)}>
                                    <Activity size={32} color="#8B5CF6" />
                                    <span style={{ fontWeight: 600, fontSize: '0.95rem' }}>Analytics</span>
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Daily Challenge Widget */}
                    <DailyChallengeCard userId={user.id} languageCode={focusLang} onComplete={(xp) => {
                        axios.get(`http://localhost:8080/api/goals?userId=${user.id}&languageCode=${focusLang}`)
                            .then(res => setGoals(res.data));
                    }} />
                </div>

                {/* RIGHT COLUMN */}
                <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
                    
                    <GoalProgressCard goals={goals} />

                    <div className="card" style={{ background: 'var(--bg-main)', border: '1px solid var(--primary)' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <h3 style={{ margin: 0, fontSize: '1.1rem' }}>Next Up</h3>
                            <div className="pulse" style={{ background: 'var(--primary-glow)', color: 'var(--primary)', padding: '4px 8px', borderRadius: '12px', fontSize: '0.7rem', fontWeight: 'bold' }}>RECOMMENDED</div>
                        </div>
                        <p className="mt-3 text-sm text-muted">Continue your streak by completing the next vocabulary drill.</p>
                        <button className="btn btn-primary w-full mt-4 flex-center" style={{ gap: '8px' }} onClick={() => navigate(`/lesson/${focusLang}`)}>
                            Start Drill <ChevronRight size={18} />
                        </button>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default Dashboard;
