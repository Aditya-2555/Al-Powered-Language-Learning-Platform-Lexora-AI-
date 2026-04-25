import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ArrowLeft, Mic, Volume2, MicOff, RefreshCw, Layers } from 'lucide-react';
import { useSpeech } from '../hooks/useSpeech';

// A collection of basic to intermediate phrases varying per language
const phraseBank = {
    es: ["Hola, ¿cómo estás?", "Me gustaría pedir la cuenta, por favor.", "No entiendo muy bien.", "¿Dónde está la estación de tren?"],
    fr: ["Bonjour, comment ça va?", "Je voudrais l'addition, s'il vous plaît.", "Je ne comprends pas très bien.", "Où est la gare?"],
    de: ["Hallo, wie geht es dir?", "Ich möchte bitte die Rechnung.", "Ich verstehe nicht ganz.", "Wo ist der Bahnhof?"],
    en: ["Hello, how are you?", "I would like the check, please.", "I don't understand very well.", "Where is the train station?"]
};

const SpeakingPractice = ({ user }) => {
    const { lang } = useParams();
    const navigate = useNavigate();
    const { speak, listen, isListening, error } = useSpeech();
    
    const [currentPhraseIdx, setCurrentPhraseIdx] = useState(0);
    const [history, setHistory] = useState([]);
    
    const phrases = phraseBank[lang] || phraseBank['en'];
    const currentPhrase = phrases[currentPhraseIdx];
    
    const [showResult, setShowResult] = useState(false);
    const [lastAttempt, setLastAttempt] = useState(null);

    useEffect(() => {
        fetchHistory();
    }, [lang]);

    const fetchHistory = async () => {
        try {
            const res = await axios.get(`http://localhost:8080/api/speaking/${user.id}/${lang}`);
            if (res.data) setHistory(res.data);
        } catch (e) {
            console.error(e);
        }
    };

    const handleListen = () => {
        speak(currentPhrase, lang);
    };

    const normalizeStr = (s) => s.toLowerCase().replace(/[.,?!¿¡]/g, '').trim();

    const handleRecord = () => {
        setShowResult(false);
        listen(lang, async (transcript, err) => {
            if (err) return;
            
            // Very primitive implicit scoring algorithm without massive compute
            const cleanTarget = normalizeStr(currentPhrase);
            const cleanTranscript = normalizeStr(transcript);
            
            // Levenshtein / Word match heuristic (simplified heavily)
            const targetWords = cleanTarget.split(' ');
            const spokenWords = cleanTranscript.split(' ');
            
            let matched = 0;
            targetWords.forEach(tw => {
                if(spokenWords.includes(tw)) matched++;
            });
            const score = Math.round((matched / targetWords.length) * 100);
            
            setLastAttempt({ text: transcript, score: score });
            setShowResult(true);

            // Log attempt
            try {
                await axios.post(`http://localhost:8080/api/speaking/${user.id}/${lang}`, {
                    targetPhrase: currentPhrase,
                    transcribedText: transcript,
                    confidenceScore: score
                });
                fetchHistory();
            } catch(e) { console.error(e); }
        });
    };

    const nextPhrase = () => {
        setShowResult(false);
        setCurrentPhraseIdx((prev) => (prev + 1) % phrases.length);
    };

    return (
        <div className="fade-in" style={{ maxWidth: '800px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div>
                    <button className="btn btn-outline" style={{ padding: '8px 12px', marginBottom: '16px' }} onClick={() => navigate('/dashboard')}>
                        <ArrowLeft size={16} style={{ marginRight: '8px' }} /> Dashboard
                    </button>
                    <h1 style={{ margin: 0 }}>Pronunciation Lab</h1>
                    <p style={{ color: 'var(--text-muted)', margin: 0 }}>Practice native speaking flows natively derived via your browser engine.</p>
                </div>
            </div>

            {error && (
                <div style={{ background: 'rgba(255, 75, 75, 0.1)', border: '1px solid var(--danger)', color: 'var(--danger)', padding: '16px', borderRadius: '8px' }}>
                    <strong>Browser Error:</strong> {error}
                </div>
            )}

            <div className="glass-panel" style={{ textAlign: 'center', padding: '40px 20px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '24px' }}>
                <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '2px' }}>Target Phrase</div>
                <h2 style={{ fontSize: '2.5rem', margin: 0, fontWeight: 'normal', color: 'var(--text-main)' }}>"{currentPhrase}"</h2>
                
                <div style={{ display: 'flex', gap: '16px', marginTop: '16px' }}>
                    <button className="btn btn-outline" style={{ padding: '12px 24px', display: 'flex', alignItems: 'center', gap: '8px' }} onClick={handleListen}>
                        <Volume2 size={24} /> Hear It
                    </button>
                    <button 
                        className="btn btn-primary" 
                        style={{ padding: '12px 24px', display: 'flex', alignItems: 'center', gap: '8px', background: isListening ? 'var(--danger)' : 'var(--primary)', borderColor: isListening ? 'var(--danger)' : 'var(--primary)' }} 
                        onClick={handleRecord}
                        disabled={isListening}
                    >
                        {isListening ? <><MicOff size={24} /> Listening...</> : <><Mic size={24} /> Hold & Speak</>}
                    </button>
                </div>

                {showResult && lastAttempt && (
                    <div className="fade-in" style={{ width: '100%', maxWidth: '500px', background: 'var(--bg-main)', border: '1px solid var(--panel-border)', padding: '24px', borderRadius: '12px', marginTop: '20px' }}>
                        <div style={{ textAlign: 'left', marginBottom: '16px' }}>
                            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '4px' }}>Browser Transcribed You Ass:</div>
                            <div style={{ fontSize: '1.2rem', color: lastAttempt.score > 70 ? 'var(--info)' : 'var(--danger)' }}>"{lastAttempt.text}"</div>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderTop: '1px solid var(--panel-border)', paddingTop: '16px' }}>
                            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }}>
                                <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Match Accuracy</span>
                                <span style={{ fontSize: '2rem', fontWeight: 'bold', color: lastAttempt.score > 70 ? 'var(--info)' : 'var(--danger)' }}>{lastAttempt.score}%</span>
                            </div>
                            <button className="btn btn-primary" onClick={nextPhrase} style={{ padding: '8px 16px' }}>Next Phrase &rarr;</button>
                        </div>
                    </div>
                )}
            </div>

            <div className="glass-panel" style={{ marginTop: '20px' }}>
                <h3 style={{ margin: '0 0 16px 0', borderBottom: '1px solid var(--panel-border)', paddingBottom: '12px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Layers size={20} /> Attempt Log
                </h3>
                {history.length === 0 ? (
                    <div style={{ color: 'var(--text-muted)', fontStyle: 'italic' }}>No attempts saved yet! Get on the mic!</div>
                ) : (
                    <div style={{ display: 'grid', gridTemplateColumns: 'minmax(200px, 1fr)', gap: '12px', maxHeight: '300px', overflowY: 'auto', paddingRight: '8px' }}>
                        {history.map((h, i) => (
                            <div key={i} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: 'rgba(255,255,255,0.05)', padding: '12px 16px', borderRadius: '8px' }}>
                                <div style={{ flex: 1, paddingRight: '16px' }}>
                                    <div style={{ fontWeight: 'bold', fontSize: '0.9rem', marginBottom: '4px' }}>Target: {h.targetPhrase}</div>
                                    <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Spoken: {h.transcribedText}</div>
                                </div>
                                <div style={{ fontWeight: 'bold', fontSize: '1.2rem', color: h.confidenceScore > 70 ? 'var(--info)' : (h.confidenceScore > 30 ? 'var(--warning)' : 'var(--danger)') }}>
                                    {h.confidenceScore}%
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
            
            <style jsx="true">{`
                .spinner { /* existing defined globally mostly */ }
            `}</style>
        </div>
    );
}

export default SpeakingPractice;
