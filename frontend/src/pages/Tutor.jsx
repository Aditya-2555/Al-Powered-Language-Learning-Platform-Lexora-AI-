import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ArrowLeft, Send, Loader2, MessageCircle, Languages, Info, Edit3, PlusCircle } from 'lucide-react';
import AchievementModal from '../components/AchievementModal';

const Tutor = ({ user }) => {
    const { lang } = useParams();
    const navigate = useNavigate();
    
    const [sessions, setSessions] = useState([]);
    const [activeSession, setActiveSession] = useState(null);
    const [messages, setMessages] = useState([]);
    
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
    const [loadingSessions, setLoadingSessions] = useState(true);
    const [newUnlocks, setNewUnlocks] = useState([]);

    const messagesEndRef = useRef(null);
    const langNames = { en: 'English', es: 'Spanish', fr: 'French', de: 'German' };

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, loading]);

    useEffect(() => {
        const fetchSessions = async () => {
            try {
                const res = await axios.get(`http://localhost:8080/api/chat/sessions/${user.id}/${lang}`);
                if (res.data) setSessions(res.data);
            } catch (err) {
                console.error("Failed to load sessions", err);
            } finally {
                setLoadingSessions(false);
            }
        };
        fetchSessions();
    }, [user.id, lang]);

    const loadSession = async (session) => {
        setActiveSession(session);
        setLoading(true);
        try {
            const res = await axios.get(`http://localhost:8080/api/chat/messages/${session.id}`);
            if (res.data) setMessages(res.data);
        } catch(e) {
            console.error(e);
        } finally {
            setLoading(false);
        }
    };

    const handleCreateSession = async () => {
        try {
            const res = await axios.post(`http://localhost:8080/api/chat/session/start/${user.id}/${lang}`);
            if (res.data) {
                setSessions([res.data, ...sessions]);
                setActiveSession(res.data);
                setMessages([]);
            }
        } catch(e) {
            console.error("Failed to create session", e);
        }
    };

    const handleSend = async (forcedInput = null) => {
        if (loading) return;
        const textToSend = forcedInput || input;
        if (!textToSend.trim()) return;

        setInput('');
        setLoading(true);

        const newMsg = {
            role: 'user',
            content: textToSend
        };
        setMessages(prev => [...prev, newMsg]);

        try {
            const payload = {
                userId: user.id,
                languageCode: lang,
                message: textToSend,
                level: user.level || 'Beginner'
            };
            
            const endpoint = activeSession ? `http://localhost:8080/api/chat/send?sessionId=${activeSession.id}` : `http://localhost:8080/api/chat/send`;

            const res = await axios.post(endpoint, payload);
            
            const tutorMsg = {
                role: 'tutor',
                content: res.data.replyInTargetLanguage,
                translation: res.data.translationInNativeLanguage,
                explanation: res.data.grammarTipInNativeLanguage,
                correction: res.data.correctionInNativeLanguage,
                suggestedReply: res.data.suggestedReply
            };
            
            setMessages(prev => [...prev, tutorMsg]);

            const actPayload = {
                userId: user.id,
                languageCode: lang,
                activityType: 'TUTOR_CHAT',
                topic: 'Conversation',
                isCorrect: res.data.correctionInNativeLanguage ? false : true,
                content: textToSend,
                mistake: res.data.correctionInNativeLanguage || null,
                correctAnswer: res.data.suggestedReply || null,
                explanation: res.data.grammarTipInNativeLanguage || res.data.translationInNativeLanguage || null
            };
            const actRes = await axios.post('http://localhost:8080/api/progress/activity', actPayload);
            if (actRes.data?.newlyUnlockedAchievements?.length > 0) {
                setNewUnlocks(actRes.data.newlyUnlockedAchievements);
            }
        } catch (err) {
            console.error("Chat failure:", err);
            setMessages(prev => [...prev, { role: 'tutor', content: 'Connection Error. Please try again.' }]);
        } finally {
            setLoading(false);
        }
    };

    const handleQuickAction = (action) => {
        let prompt = "";
        switch(action) {
            case 'Translate': prompt = "Can you translate the last thing you said verbatim into my native language and break down the vocabulary?"; break;
            case 'Grammar': prompt = "Can you explain the exact grammar rules you used in your last sentence?"; break;
            case 'Harder': prompt = "Your language is too simple. Please escalate the difficulty significantly."; break;
            case 'Easier': prompt = "I don't understand. Can you rephrase using much simpler vocabulary?"; break;
            default: return;
        }
        handleSend(prompt);
    };

    if (loadingSessions) {
        return <div style={{ textAlign: 'center', marginTop: '100px' }}><Loader2 className="spinner text-primary" size={48} style={{ animation: 'spin 1s infinite' }} /></div>;
    }

    if (!activeSession && sessions.length > 0) {
        return (
            <div className="fade-in container" style={{ maxWidth: '800px', paddingTop: '40px' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px' }}>
                    <button className="btn btn-outline" onClick={() => navigate('/dashboard')}><ArrowLeft size={16} style={{ marginRight: '8px' }} /> Dashboard</button>
                    <h2 style={{ margin: 0, fontSize: '1.5rem' }}>AI Practice Sessions</h2>
                    <button className="btn btn-primary" onClick={handleCreateSession}><PlusCircle size={16} style={{ marginRight: '8px' }}/> New Chat</button>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                    {sessions.map(s => (
                        <div key={s.id} className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', cursor: 'pointer' }} onClick={() => loadSession(s)}>
                            <div>
                                <h3 style={{ margin: '0 0 8px 0', fontSize: '1.1rem' }}>{s.title}</h3>
                                <div className="text-muted text-sm">{new Date(s.startedAt).toLocaleString()}</div>
                            </div>
                            <MessageCircle color="var(--primary)" />
                        </div>
                    ))}
                </div>
            </div>
        );
    }

    if (!activeSession && sessions.length === 0) {
        handleCreateSession();
        return <div style={{ textAlign: 'center', marginTop: '100px' }}><Loader2 className="spinner text-primary" size={48} style={{ animation: 'spin 1s infinite' }} /></div>;
    }

    return (
        <div className="fade-in container" style={{ maxWidth: '1000px', paddingTop: '24px', display: 'flex', gap: '20px', height: 'calc(100vh - 80px)' }}>
            
            <div className="card" style={{ flex: 1, display: 'flex', flexDirection: 'column', padding: 0, overflow: 'hidden' }}>
                <div style={{ padding: '16px 24px', background: 'var(--bg-color)', borderBottom: '1px solid var(--panel-border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                         <button className="btn btn-outline" style={{ padding: '6px 10px' }} onClick={() => setActiveSession(null)}><ArrowLeft size={16} /></button>
                         <h3 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '8px', fontSize: '1.1rem' }}><MessageCircle size={20} className="text-primary" /> Active AI Session</h3>
                    </div>
                    <div className="text-muted text-sm font-semibold">{langNames[lang]} Tutor</div>
                </div>

                <div style={{ flex: 1, overflowY: 'auto', padding: '24px', display: 'flex', flexDirection: 'column', gap: '24px', background: 'var(--bg-main)' }}>
                    {messages.length === 0 && (
                        <div style={{ textAlign: 'center', color: 'var(--text-muted)', marginTop: '40px' }}>
                            <MessageCircle size={48} color="var(--panel-border)" style={{ marginBottom: '16px' }} />
                            <h3>Session Created</h3>
                            <p>Say hello to your {langNames[lang]} tutor!</p>
                        </div>
                    )}
                    
                    {messages.map((msg, idx) => (
                        <div key={idx} style={{ 
                            display: 'flex', 
                            flexDirection: 'column',
                            alignItems: msg.role === 'user' ? 'flex-end' : 'flex-start',
                            maxWidth: '85%',
                            alignSelf: msg.role === 'user' ? 'flex-end' : 'flex-start'
                        }}>
                            <div className={`msg ${msg.role === 'user' ? 'user' : 'tutor'}`}>
                                {msg.content}
                            </div>
                            
                            {msg.role === 'tutor' && (
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginTop: '8px', width: '100%', paddingLeft: '12px' }}>
                                    
                                    {msg.correction && (
                                        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '8px', background: 'var(--danger-bg)', padding: '10px 14px', borderRadius: '8px', borderLeft: '3px solid var(--danger)' }}>
                                            <Edit3 size={16} color="var(--danger)" style={{ marginTop: '2px' }} />
                                            <div>
                                                <div style={{ fontSize: '0.75rem', color: 'var(--danger)', fontWeight: 'bold', textTransform: 'uppercase' }}>Correction</div>
                                                <div style={{ fontSize: '0.9rem', color: 'var(--text-main)', marginTop: '2px' }}>{msg.correction}</div>
                                            </div>
                                        </div>
                                    )}

                                    {msg.translation && (
                                        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '8px', background: 'rgba(37, 99, 235, 0.05)', padding: '10px 14px', borderRadius: '8px', borderLeft: '3px solid var(--primary)' }}>
                                            <Languages size={16} className="text-primary" style={{ marginTop: '2px' }} />
                                            <div>
                                                <div style={{ fontSize: '0.75rem', color: 'var(--primary)', fontWeight: 'bold', textTransform: 'uppercase' }}>Translation</div>
                                                <div style={{ fontSize: '0.9rem', color: 'var(--text-main)', marginTop: '2px' }}>{msg.translation}</div>
                                            </div>
                                        </div>
                                    )}

                                    {msg.explanation && (
                                        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '8px', background: 'rgba(16, 185, 129, 0.05)', padding: '10px 14px', borderRadius: '8px', borderLeft: '3px solid var(--accent)' }}>
                                            <Info size={16} color="var(--accent)" style={{ marginTop: '2px' }} />
                                            <div>
                                                <div style={{ fontSize: '0.75rem', color: 'var(--accent)', fontWeight: 'bold', textTransform: 'uppercase' }}>Grammar Insight</div>
                                                <div style={{ fontSize: '0.9rem', color: 'var(--text-main)', marginTop: '2px' }}>{msg.explanation}</div>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            )}
                        </div>
                    ))}
                    
                    {loading && (
                        <div style={{ alignSelf: 'flex-start' }}>
                            <div className="spinner" style={{ width: '8px', height: '8px', background: 'var(--primary)', borderRadius: '50%', display: 'inline-block', margin: '4px', animation: 'bounce 1.4s infinite ease-in-out both' }}></div>
                            <div className="spinner" style={{ width: '8px', height: '8px', background: 'var(--primary)', borderRadius: '50%', display: 'inline-block', margin: '4px', animation: 'bounce 1.4s infinite ease-in-out both', animationDelay: '0.2s' }}></div>
                            <div className="spinner" style={{ width: '8px', height: '8px', background: 'var(--primary)', borderRadius: '50%', display: 'inline-block', margin: '4px', animation: 'bounce 1.4s infinite ease-in-out both', animationDelay: '0.4s' }}></div>
                        </div>
                    )}
                    <div ref={messagesEndRef} />
                </div>

                <div style={{ padding: '20px', background: 'var(--bg-color)', borderTop: '1px solid var(--panel-border)' }}>
                    
                    <div style={{ display: 'flex', overflowX: 'auto', gap: '10px', paddingBottom: '16px' }} className="hide-scrollbar">
                        <button className="btn btn-outline" style={{ fontSize: '0.8rem', padding: '6px 14px', borderRadius: '20px', whiteSpace: 'nowrap', background: 'var(--bg-main)' }} onClick={() => handleQuickAction('Translate')} disabled={loading}><Languages size={14} style={{ marginRight: '6px' }} /> Translate Preceding</button>
                        <button className="btn btn-outline" style={{ fontSize: '0.8rem', padding: '6px 14px', borderRadius: '20px', whiteSpace: 'nowrap', background: 'var(--bg-main)' }} onClick={() => handleQuickAction('Grammar')} disabled={loading}><Info size={14} style={{ marginRight: '6px' }} /> Explain Grammar</button>
                        <button className="btn btn-outline" style={{ fontSize: '0.8rem', padding: '6px 14px', borderRadius: '20px', whiteSpace: 'nowrap', color: 'var(--danger)', borderColor: 'var(--danger)', background: 'var(--bg-main)' }} onClick={() => handleQuickAction('Harder')} disabled={loading}>Make Harder</button>
                        <button className="btn btn-outline" style={{ fontSize: '0.8rem', padding: '6px 14px', borderRadius: '20px', whiteSpace: 'nowrap', color: 'var(--primary)', borderColor: 'var(--primary)', background: 'var(--bg-main)' }} onClick={() => handleQuickAction('Easier')} disabled={loading}>Make Easier</button>
                    </div>

                    <div style={{ display: 'flex', gap: '12px' }}>
                        <input
                            type="text"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={(e) => { if (e.key === 'Enter') handleSend(); }}
                            placeholder={`Type in ${langNames[lang]} or ${langNames[user.nativeLanguage]}...`}
                            className="input-field"
                            style={{ flex: 1, borderRadius: '24px' }}
                            disabled={loading}
                        />
                        <button 
                            className="btn btn-primary" 
                            style={{ borderRadius: '50%', width: '48px', height: '48px', padding: 0 }} 
                            onClick={() => handleSend()} 
                            disabled={loading || !input.trim()}
                        >
                            <Send size={20} />
                        </button>
                    </div>
                </div>
            </div>

            <AchievementModal achievements={newUnlocks} onClose={() => setNewUnlocks([])} />
            <style jsx="true">{`
                @keyframes bounce {
                  0%, 80%, 100% { transform: scale(0); }
                  40% { transform: scale(1); }
                }
                .hide-scrollbar::-webkit-scrollbar { display: none; }
                .hide-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
            `}</style>
        </div>
    );
};

export default Tutor;
