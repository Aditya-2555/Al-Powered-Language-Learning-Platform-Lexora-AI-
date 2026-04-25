import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ArrowLeft, Send, Utensils, PlaneTakeoff, Hotel, ShoppingBag, UserPlus, Map, Loader2, MessageCircle, Info, Mic, LayoutDashboard, Flag } from 'lucide-react';

const scenariosList = [
    { id: 'restaurant', title: 'Ordering at a Restaurant', icon: <Utensils size={32} />, desc: 'Practice requesting a table, ordering food, and asking for the bill.' },
    { id: 'airport', title: 'Airport Customs', icon: <PlaneTakeoff size={32} />, desc: 'Answer questions from border patrol and navigate the airport.' },
    { id: 'hotel', title: 'Hotel Check-In', icon: <Hotel size={32} />, desc: 'Reserve a room, complain about issues, and check out.' },
    { id: 'intro', title: 'Self Introduction', icon: <UserPlus size={32} />, desc: 'Learn how to introduce yourself to a new group of native speakers.' },
    { id: 'directions', title: 'Asking for Directions', icon: <Map size={32} />, desc: 'Find your way around the city by asking locals for help.' },
    { id: 'shopping', title: 'Shopping at a Market', icon: <ShoppingBag size={32} />, desc: 'Bargain for goods, ask for prices, and complete transactions.' }
];

const Scenarios = ({ user }) => {
    const { lang } = useParams();
    const navigate = useNavigate();
    
    const [activeScenario, setActiveScenario] = useState(null);
    const [session, setSession] = useState(null);
    const [messages, setMessages] = useState([]);
    
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
    const [evaluation, setEvaluation] = useState(null);
    
    const messagesEndRef = useRef(null);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, loading, evaluation]);

    const handleStartScenario = async (scenario) => {
        setActiveScenario(scenario);
        setLoading(true);
        try {
            const res = await axios.post(`http://localhost:8080/api/chat/scenario/start/${user.id}/${lang}?scenario=${scenario.title}`);
            setSession(res.data);
            setMessages([]);
        } catch(e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    };

    const handleSend = async () => {
        if (!input.trim() || loading) return;
        
        const userText = input;
        setInput('');
        setLoading(true);
        
        setMessages(prev => [...prev, { role: 'user', content: userText }]);
        
        try {
            const payload = {
                userId: user.id,
                languageCode: lang,
                message: userText,
                scenario: activeScenario.title
            };
            const res = await axios.post(`http://localhost:8080/api/chat/send?sessionId=${session.id}`, payload);
            setMessages(prev => [...prev, {
                role: 'tutor',
                content: res.data.replyInTargetLanguage,
                translation: res.data.translationInNativeLanguage
            }]);
        } catch(e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    };

    const handleEvaluate = async () => {
        setLoading(true);
        try {
            const res = await axios.post(`http://localhost:8080/api/chat/scenario/evaluate/${session.id}`);
            // res.data is expected to be the JSON string returned by evaluateScenario
            setEvaluation(res.data);
        } catch(e) {
            console.error("Evaluation failed", e);
        } finally {
            setLoading(false);
        }
    };

    if (!activeScenario) {
        return (
            <div className="fade-in" style={{ maxWidth: '1000px', margin: '0 auto' }}>
                <div style={{ display: 'flex', alignItems: 'center', marginBottom: '24px' }}>
                    <button className="btn btn-outline" onClick={() => navigate('/dashboard')}><ArrowLeft size={16} style={{ marginRight: '8px' }} /> Dashboard</button>
                    <h2 style={{ margin: '0 0 0 16px' }}>Roleplay Sandbox</h2>
                </div>
                <p style={{ color: 'var(--text-muted)', marginBottom: '24px' }}>Select an immersive situation below. The AI will act rigorously as your counterpart in {lang.toUpperCase()}. Finish the conversation to trigger a dynamic performance evaluation.</p>
                
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '16px' }}>
                    {scenariosList.map(s => (
                        <div key={s.id} className="glass-panel" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center', padding: '24px', cursor: 'pointer', transition: 'all 0.2s', border: '1px solid var(--panel-border)' }} onClick={() => handleStartScenario(s)}>
                            <div style={{ padding: '16px', background: 'rgba(56, 189, 248, 0.1)', color: 'var(--info)', borderRadius: '50%', marginBottom: '16px' }}>
                                {s.icon}
                            </div>
                            <h3 style={{ margin: '0 0 8px 0' }}>{s.title}</h3>
                            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', margin: 0 }}>{s.desc}</p>
                            <button className="btn btn-outline" style={{ marginTop: '16px', width: '100%' }}>Launch Scenario</button>
                        </div>
                    ))}
                </div>
            </div>
        );
    }

    // Roleplay Chat Interface
    return (
        <div className="fade-in" style={{ maxWidth: '900px', margin: '0 auto', display: 'flex', flexDirection: 'column', height: 'calc(100vh - 120px)' }}>
            <div className="glass-panel" style={{ padding: '16px 20px', borderBottom: '1px solid var(--panel-border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderRadius: '12px 12px 0 0' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <button className="btn btn-outline" style={{ padding: '6px 12px' }} onClick={() => setActiveScenario(null)}><ArrowLeft size={16} /></button>
                    <div>
                        <h3 style={{ margin: 0 }}>{activeScenario.title}</h3>
                        <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Roleplaying in {lang.toUpperCase()}</div>
                    </div>
                </div>
                {!evaluation && <button className="btn btn-primary" style={{ background: 'var(--danger)', borderColor: 'var(--danger)' }} onClick={handleEvaluate} disabled={loading || messages.length === 0}><Flag size={16} style={{ marginRight: '6px' }} /> End & Evaluate</button>}
            </div>

            <div className="glass-panel" style={{ flex: 1, overflowY: 'auto', padding: '20px', display: 'flex', flexDirection: 'column', gap: '20px', borderRadius: '0' }}>
                {messages.length === 0 && (
                    <div style={{ textAlign: 'center', color: 'var(--text-muted)', marginTop: '40px' }}>
                        {activeScenario.icon}
                        <h3>Scenario Initialized</h3>
                        <p>Say hello to start the roleplay. You are in {activeScenario.title}.</p>
                    </div>
                )}

                {messages.map((msg, idx) => (
                    <div key={idx} style={{ alignSelf: msg.role === 'user' ? 'flex-end' : 'flex-start', maxWidth: '80%' }}>
                        <div style={{
                            padding: '12px 16px', borderRadius: '12px',
                            background: msg.role === 'user' ? 'var(--primary)' : 'var(--bg-main)',
                            color: msg.role === 'user' ? '#000' : 'var(--text-main)',
                            border: msg.role === 'tutor' ? '1px solid var(--panel-border)' : 'none',
                        }}>
                            {msg.content}
                        </div>
                        {msg.translation && <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '4px', textAlign: 'left', paddingLeft: '4px' }}>"{msg.translation}"</div>}
                    </div>
                ))}

                {loading && !evaluation && (
                    <div style={{ alignSelf: 'flex-start', padding: '16px' }}><Loader2 className="spinner" size={20} style={{ animation: 'spin 1s infinite' }} /></div>
                )}

                {evaluation && (
                    <div style={{ background: 'linear-gradient(135deg, rgba(88, 204, 2, 0.1) 0%, rgba(56, 189, 248, 0.1) 100%)', border: '1px solid var(--primary)', borderRadius: '16px', padding: '24px', marginTop: '20px' }}>
                        <h2 style={{ margin: '0 0 16px 0', borderBottom: '1px solid var(--primary)', paddingBottom: '8px' }}>Scenario Evaluation Report</h2>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '24px' }}>
                            <div>
                                <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'var(--primary)' }}>{evaluation.fluencyScore} <span style={{ fontSize: '1rem', color: 'var(--text-muted)' }}>/ 100</span></div>
                                <div style={{ color: 'var(--text-main)', fontWeight: 'bold' }}>Fluency Score</div>
                            </div>
                            <div>
                                <h4 style={{ margin: '0 0 8px 0', color: 'var(--warning)' }}>Grammar Issues</h4>
                                <ul style={{ margin: 0, paddingLeft: '20px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                                    {evaluation.grammarIssues?.map((iss, i) => <li key={i}>{iss}</li>)}
                                </ul>
                            </div>
                        </div>
                        <div style={{ marginTop: '20px' }}>
                            <h4 style={{ margin: '0 0 8px 0', color: 'var(--info)' }}>Suggested Vocabulary to Review</h4>
                            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                                {evaluation.suggestedVocabulary?.map((voc, i) => (
                                    <span key={i} style={{ background: 'rgba(255,255,255,0.1)', padding: '4px 10px', borderRadius: '12px', fontSize: '0.8rem' }}>{voc}</span>
                                ))}
                            </div>
                        </div>
                        <div style={{ marginTop: '20px', padding: '16px', background: 'var(--bg-main)', borderRadius: '8px', borderLeft: '3px solid var(--primary)' }}>
                            {evaluation.overallFeedback}
                        </div>
                        <div style={{ textAlign: 'center', marginTop: '24px' }}>
                            <button className="btn btn-outline" onClick={() => navigate('/dashboard')}><LayoutDashboard size={16} style={{ marginRight: '8px' }} /> Return to Dashboard</button>
                        </div>
                    </div>
                )}
                
                <div ref={messagesEndRef} />
            </div>

            {!evaluation && (
                 <div className="glass-panel" style={{ padding: '20px', borderTop: '1px solid var(--panel-border)', borderRadius: '0 0 12px 12px', display: 'flex', gap: '12px' }}>
                    <input
                        type="text"
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyDown={(e) => { if (e.key === 'Enter') handleSend(); }}
                        placeholder="Say something to your roleplay partner..."
                        className="input-field"
                        style={{ flex: 1 }}
                        disabled={loading}
                    />
                    <button className="btn btn-primary" style={{ borderRadius: '12px', padding: '0 24px' }} onClick={handleSend} disabled={loading || !input.trim()}>
                        <Send size={20} />
                    </button>
                </div>
            )}
            <style jsx="true">{`
                @keyframes spin { 100% { transform: rotate(360deg); } }
            `}</style>
        </div>
    );
};

export default Scenarios;
