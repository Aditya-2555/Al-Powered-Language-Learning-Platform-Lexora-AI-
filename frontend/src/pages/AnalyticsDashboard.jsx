import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ArrowLeft, Target, Flame, CheckCircle, Crosshair, TrendingUp, Activity, Play, AlertCircle, BookOpen, MessageCircle, Star } from 'lucide-react';

const AnalyticsDashboard = ({ user }) => {
    const { lang } = useParams();
    const navigate = useNavigate();
    const [stats, setStats] = useState(null);
    const [achievements, setAchievements] = useState([]);
    const [recommendation, setRecommendation] = useState(null);
    const [loading, setLoading] = useState(true);

    const langNames = { en: 'English', es: 'Spanish', fr: 'French', de: 'German' };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [resStats, resAch, resRec] = await Promise.all([
                    axios.get(`http://localhost:8080/api/progress/dashboard/${user.id}/${lang}`),
                    axios.get(`http://localhost:8080/api/progress/achievements/${user.id}`),
                    axios.get(`http://localhost:8080/api/progress/recommendation/${user.id}/${lang}`)
                ]);
                if(resStats.data) setStats(resStats.data);
                if(resAch.data) setAchievements(resAch.data);
                if(resRec.data) setRecommendation(resRec.data);
            } catch (err) {
                console.error("Failed to fetch dashboard data", err);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [user.id, lang]);

    if (loading) return (
        <div className="flex-center" style={{ height: '60vh' }}>
            <div className="text-primary text-center">
                <Activity size={48} className="pulse" />
                <h2 className="mt-4" style={{ fontSize: '1.2rem', fontWeight: 500 }}>Loading analytics engine...</h2>
            </div>
        </div>
    );

    if (!stats) return (
        <div style={{ textAlign: 'center', marginTop: '100px' }}>
            <p>No data found for this language yet.</p>
            <button className="btn btn-primary mt-4" onClick={() => navigate('/dashboard')}>Back</button>
        </div>
    );

    return (
        <div className="fade-in container" style={{ maxWidth: '1000px', paddingTop: '40px', paddingBottom: '40px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div>
                    <button className="btn btn-outline mb-4" style={{ padding: '8px 12px' }} onClick={() => navigate('/dashboard')}>
                        <ArrowLeft size={16} style={{ marginRight: '8px' }} /> Dashboard
                    </button>
                    <h1 style={{ margin: 0 }}>Progress Analytics</h1>
                    <p className="text-muted" style={{ margin: 0, marginTop: '4px' }}>{langNames[lang]} Learning Profile for {user.username}</p>
                </div>
                <div className="card" style={{ padding: '16px 24px', display: 'flex', alignItems: 'center', gap: '20px', borderLeft: '4px solid var(--primary)' }}>
                    <div style={{ textAlign: 'right' }}>
                        <div style={{ fontSize: '0.85rem', color: 'var(--primary)', textTransform: 'uppercase', letterSpacing: '1px', fontWeight: 'bold' }}>
                            Next Recommended Action
                        </div>
                        {recommendation ? (
                            <>
                                <div style={{ fontWeight: 'bold', fontSize: '1.2rem', marginBottom: '4px' }}>{recommendation.title}</div>
                                <div className="text-muted text-sm" style={{ maxWidth: '300px' }}>{recommendation.description}</div>
                            </>
                        ) : (
                            <div className="text-muted">Calculating optimum path...</div>
                        )}
                    </div>
                    <button className="btn btn-primary" style={{ padding: '12px', borderRadius: '50%' }} onClick={() => {
                        if (!recommendation) return;
                        if (recommendation.type === 'VOCABULARY') navigate(`/notebook/${lang}`);
                        else if (recommendation.type === 'MISTAKES') navigate(`/mistakes/${lang}`);
                        else navigate(`/lesson/${lang}?topic=${recommendation.topic}&diff=${recommendation.difficulty}`);
                    }}>
                        <Play size={20} style={{ marginLeft: '2px' }} />
                    </button>
                </div>
            </div>

            {/* Top Level KPIs */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: '16px' }}>
                <div className="card p-3" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                    <Target className="text-primary" size={28} />
                    <div style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>{stats.totalXp}</div>
                    <div className="text-muted text-sm">Total XP</div>
                </div>
                <div className="card p-3" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                    <Flame color="var(--warning)" size={28} />
                    <div style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>{stats.currentStreak}</div>
                    <div className="text-muted text-sm">Day Streak</div>
                </div>
                <div className="card p-3" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                    <CheckCircle className="text-primary" size={28} />
                    <div style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>{stats.lessonsCompleted}</div>
                    <div className="text-muted text-sm">Lessons Passed</div>
                </div>
                <div className="card p-3" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                    <Crosshair color="#8B5CF6" size={28} />
                    <div style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>{stats.accuracyPercentage}%</div>
                    <div className="text-muted text-sm">Accuracy</div>
                </div>
                <div className="card p-3" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                    <BookOpen color="#3B82F6" size={28} />
                    <div style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>{stats.lessonsCompleted * 5}</div>
                    <div className="text-muted text-sm">Words Learned</div>
                </div>
                <div className="card p-3" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                    <Activity color="var(--danger)" size={28} />
                    <div style={{ fontSize: '1.8rem', fontWeight: 'bold' }}>{stats.lessonsCompleted > 0 ? '3' : '0'}</div>
                    <div className="text-muted text-sm">Due Reviews</div>
                </div>
            </div>

            {/* Weekly Activity Graph */}
            <div className="card">
                <h3 style={{ margin: '0 0 16px 0', display: 'flex', alignItems: 'center', gap: '8px', fontSize: '1.1rem' }}>
                    <Activity size={20} className="text-primary" /> Weekly Activity Pulse
                </h3>
                <div style={{ display: 'flex', alignItems: 'flex-end', gap: '8px', height: '120px', padding: '16px 0', borderBottom: '1px solid var(--panel-border)' }}>
                    {(stats.weeklyActivityPulse || [0,0,0,0,0,0,0]).map((h, i) => {
                        // dynamically calculate relative height for UX
                        const maxVal = Math.max(...(stats.weeklyActivityPulse || [10]), 100);
                        const relativeHeight = Math.max((h / maxVal) * 100, 10);
                        
                        return (
                        <div key={i} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px', height: '100%' }}>
                            <div style={{ flex: 1, display: 'flex', alignItems: 'flex-end', width: '100%', justifyContent: 'center' }}>
                                <div style={{ 
                                    width: '100%', 
                                    maxWidth: '40px', 
                                    height: `${relativeHeight}%`, 
                                    background: h > 0 ? 'var(--primary)' : 'rgba(37, 99, 235, 0.1)', 
                                    borderRadius: '4px 4px 0 0',
                                    transition: 'height 0.3s ease'
                                }} title={`${h} Est. XP`} />
                            </div>
                            <div style={{ fontSize: '0.75rem', fontWeight: 500, color: 'var(--text-muted)' }}>{['Mon','Tue','Wed','Thu','Fri','Sat','Sun'][i]}</div>
                        </div>
                    )})}
                </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
                {/* Level Progress */}
                <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <h3 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '8px', fontSize: '1.1rem' }}><TrendingUp size={20} /> Current Level: {stats.currentLevel}</h3>
                        <span style={{ fontWeight: 600, color: 'var(--primary)' }}>{stats.levelProgressPercentage}% to Level {stats.currentLevel + 1}</span>
                    </div>
                    <div style={{ background: 'var(--panel-border)', height: '12px', borderRadius: '8px', overflow: 'hidden' }}>
                        <div style={{
                            background: 'var(--primary)',
                            height: '100%',
                            width: `${stats.levelProgressPercentage}%`,
                            transition: 'width 1s ease-out'
                        }} />
                    </div>
                    
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '16px', padding: '16px', background: 'var(--bg-color)', borderRadius: '8px', border: '1px solid var(--panel-border)' }}>
                        <div style={{ textAlign: 'center', flex: 1 }}>
                            <div className="text-muted text-sm mb-1">Strongest Topic</div>
                            <div style={{ fontWeight: 600, color: 'var(--primary)', fontSize: '1.1rem' }}>{stats.strongestTopic}</div>
                        </div>
                        <div style={{ width: '1px', background: 'var(--panel-border)' }} />
                        <div style={{ textAlign: 'center', flex: 1 }}>
                            <div className="text-muted text-sm mb-1">Weakest Topic</div>
                            <div style={{ fontWeight: 600, color: 'var(--danger)', fontSize: '1.1rem' }}>{stats.weakestTopic}</div>
                        </div>
                    </div>
                </div>

                {/* Recent Mistakes List */}
                <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '12px', cursor: 'pointer', borderColor: 'var(--danger)' }} onClick={() => navigate(`/mistakes/${lang}`)}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--panel-border)', paddingBottom: '12px' }}>
                        <h3 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '8px', fontSize: '1.1rem' }}>
                            <AlertCircle size={20} color="var(--danger)" /> Issue Tracker
                        </h3>
                        <button className="btn btn-outline" style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '0.8rem', padding: '4px 8px' }}>
                            <Play size={14} /> Review
                        </button>
                    </div>
                    <div style={{ overflowY: 'auto', maxHeight: '180px', paddingRight: '8px' }}>
                        {stats.recentMistakes && stats.recentMistakes.length > 0 ? stats.recentMistakes.map((m, i) => (
                            <div key={i} style={{ padding: '12px', background: 'var(--danger-bg)', borderRadius: '8px', margin: '4px 0', borderLeft: '3px solid var(--danger)' }}>
                                <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{m.topic}</div>
                                <div style={{ fontWeight: 'bold', fontSize: '0.95rem' }}>{m.content}</div>
                            </div>
                        )) : (
                            <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '20px' }}>No recent errors.</div>
                        )}
                    </div>
                </div>
            </div>
            
            {/* Recent Activity Log */}
            <div className="card">
                <h3 style={{ margin: '0 0 16px 0', fontSize: '1.1rem' }}>Activity Feed</h3>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    {stats.recentActivities && stats.recentActivities.length > 0 ? stats.recentActivities.map((a, i) => (
                        <div key={i} style={{ display: 'flex', alignItems: 'center', gap: '16px', padding: '16px', background: 'var(--bg-color)', borderRadius: '8px', border: '1px solid var(--panel-border)' }}>
                            <div style={{ padding: '10px', background: a.isCorrect ? 'rgba(37,99,235,0.1)' : 'var(--danger-bg)', borderRadius: '8px' }}>
                                {a.activityType === 'LESSON' ? <BookOpen size={18} color={a.isCorrect ? "var(--primary)" : "var(--danger)"} /> : <MessageCircle size={18} className="text-primary" />}
                            </div>
                            <div style={{ flex: 1 }}>
                                <div style={{ fontWeight: 600 }}>{a.activityType === 'LESSON' ? `Lesson: ${a.topic}` : 'Tutor Chat'}</div>
                                <div className="text-muted text-sm">{a.content}</div>
                            </div>
                            <div className="text-muted text-sm">
                                {new Date(a.createdAt).toLocaleString()}
                            </div>
                        </div>
                    )) : (
                        <div style={{ textAlign: 'center', color: 'var(--text-muted)', padding: '20px' }}>No activity yet.</div>
                    )}
                </div>
            </div>

            {/* Trophy Case Ribbon */}
            <div className="card" style={{ marginTop: '8px' }}>
                <h3 style={{ margin: '0 0 16px 0', display: 'flex', alignItems: 'center', gap: '8px', fontSize: '1.1rem' }}>
                    <Star size={20} color="#F59E0B" fill="#F59E0B" /> Trophy Case
                </h3>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '16px' }}>
                    {achievements.length > 0 ? achievements.map((ach, i) => {
                        const getIcon = (name) => {
                            const props = { size: 32, color: 'var(--primary)' };
                            switch(name) {
                                case 'Flame': return <Flame {...props} color="var(--warning)" />;
                                case 'Star': return <Star {...props} color="var(--warning)" />;
                                case 'CheckCircle': return <CheckCircle {...props} />;
                                case 'Book': return <BookOpen {...props} color="#8B5CF6" />;
                                default: return <Target {...props} />;
                            }
                        };
                        return (
                            <div key={i} title={ach.description} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', background: 'var(--bg-color)', padding: '16px', borderRadius: '12px', minWidth: '110px', border: '1px solid var(--panel-border)' }}>
                                <div style={{ marginBottom: '8px' }}>{getIcon(ach.iconIdentifier)}</div>
                                <div style={{ fontWeight: 600, fontSize: '0.85rem', textAlign: 'center' }}>{ach.title}</div>
                            </div>
                        );
                    }) : (
                        <div className="text-muted" style={{ fontStyle: 'italic', padding: '10px' }}>No achievements yet. Keep learning!</div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default AnalyticsDashboard;
