import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ArrowLeft, Loader2, Target, CheckCircle, XCircle } from 'lucide-react';
import AchievementModal from '../components/AchievementModal';

const MistakesReview = ({ user }) => {
    const { lang } = useParams();
    const navigate = useNavigate();
    
    const [mistakes, setMistakes] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [loading, setLoading] = useState(true);
    
    const [userAnswer, setUserAnswer] = useState('');
    const [isCorrect, setIsCorrect] = useState(null); 
    const [newUnlocks, setNewUnlocks] = useState([]);

    useEffect(() => {
        fetchMistakes();
    }, [user.id, lang]);

    const fetchMistakes = async () => {
        try {
            const res = await axios.get(`http://localhost:8080/api/progress/mistakes/${user.id}/${lang}`);
            if (res.data) setMistakes(res.data);
        } catch (err) {
            console.error("Failed to fetch mistakes", err);
        } finally {
            setLoading(false);
        }
    };

    const handleCheck = async () => {
        if (!userAnswer.trim()) return;

        const currentMistake = mistakes[currentIndex];
        const correctTarget = currentMistake.correctAnswer?.toLowerCase().trim() || "";
        const givenAnswer = userAnswer.toLowerCase().trim();
        
        let correct = false;
        if (correctTarget && givenAnswer === correctTarget) {
            correct = true;
        } else if (!correctTarget) {
            correct = true;
        }

        if (correct) {
            setIsCorrect(true);
            try {
                const res = await axios.post(`http://localhost:8080/api/progress/mistakes/resolve/${currentMistake.id}`);
                if (res.data?.newlyUnlockedAchievements?.length > 0) {
                    setNewUnlocks(res.data.newlyUnlockedAchievements);
                }
            } catch(e) { 
                console.error("Failed to resolve mistake on backend", e); 
            }
        } else {
            setIsCorrect(false);
        }
    };

    const handleNext = () => {
        setIsCorrect(null);
        setUserAnswer('');
        
        if (currentIndex < mistakes.length - 1) {
            setCurrentIndex(currentIndex + 1);
        } else {
            setLoading(true);
            fetchMistakes(); // Refresh list to clear resolved ones
            setCurrentIndex(0);
        }
    };

    if (loading) {
        return (
            <div className="flex-center mt-8">
                <Loader2 className="spinner text-primary" size={48} style={{ animation: 'spin 1s infinite' }} />
                <p className="mt-4 text-muted font-semibold">Loading your issue tracker...</p>
                <style jsx="true">{`@keyframes spin { 100% { transform: rotate(360deg); } }`}</style>
            </div>
        );
    }

    if (mistakes.length === 0) {
        return (
            <div className="container fade-in" style={{ maxWidth: '600px', textAlign: 'center', paddingTop: '40px' }}>
                <button className="btn btn-outline mb-4" onClick={() => navigate(`/analytics/${lang}`)}>
                    <ArrowLeft size={16} style={{ marginRight: '8px' }} /> Back to Analytics
                </button>
                <div className="card" style={{ padding: '40px' }}>
                    <CheckCircle size={64} className="text-primary mb-4" style={{ margin: '0 auto' }} />
                    <h2 style={{ fontSize: '2rem' }}>All Clear!</h2>
                    <p className="text-muted mt-2">You have no pending mistakes to review. Great job!</p>
                </div>
            </div>
        );
    }

    const currentMistake = mistakes[currentIndex];

    return (
        <div className="container fade-in" style={{ maxWidth: '800px', paddingTop: '40px', paddingBottom: '40px' }}>
            <div className="flex-center" style={{ justifyContent: 'space-between', marginBottom: '24px' }}>
                <button className="btn btn-outline" onClick={() => navigate(`/analytics/${lang}`)}>
                    <ArrowLeft size={16} style={{ marginRight: '8px' }} /> Return
                </button>
                <div style={{ color: 'var(--danger)', fontWeight: 'bold', background: 'var(--bg-main)', padding: '6px 12px', border: '1px solid var(--panel-border)', borderRadius: '20px' }}>
                    {mistakes.length - currentIndex} Mistakes Remaining
                </div>
            </div>

            <div className="card" style={{ padding: '32px' }}>
                <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '8px', borderBottom: '1px solid var(--panel-border)', paddingBottom: '16px', marginBottom: '24px' }}>
                    <Target className="text-primary" size={24} />
                    <h2 style={{ fontSize: '1.4rem', margin: 0, color: 'var(--text-main)' }}>Issue Tracker Review</h2>
                </div>

                <div className="mb-4">
                    <p className="text-muted text-sm font-semibold">Original Problem ({currentMistake.topic}):</p>
                    <div style={{ fontSize: '1.4rem', fontWeight: 600, padding: '20px', background: 'var(--bg-color)', borderRadius: '12px', marginTop: '8px', border: '1px solid var(--panel-border)' }}>
                        {currentMistake.content}
                    </div>
                </div>

                <div className="mb-4">
                    <p className="text-muted text-sm font-semibold">Your Mistake:</p>
                    <div style={{ padding: '16px', borderLeft: '4px solid var(--danger)', background: 'var(--danger-bg)', marginTop: '8px', color: 'var(--danger)', borderRadius: '0 12px 12px 0', fontSize: '1.1rem', fontWeight: 500 }}>
                        "{currentMistake.mistake}"
                    </div>
                </div>

                <input 
                    type="text" 
                    value={userAnswer}
                    onChange={(e) => setUserAnswer(e.target.value)}
                    onKeyDown={(e) => { if (e.key === 'Enter' && isCorrect === null) handleCheck(); }}
                    placeholder="Type the correct answer from memory..."
                    className="input-field"
                    style={{ padding: '16px', fontSize: '1.1rem', marginBottom: '24px' }}
                    disabled={isCorrect !== null}
                />

                {isCorrect !== null && (
                    <div className="fade-in" style={{
                        padding: '24px', borderRadius: '12px', marginBottom: '24px',
                        background: isCorrect ? 'var(--primary-glow)' : 'var(--danger-bg)',
                        border: `1px solid ${isCorrect ? 'var(--primary)' : 'var(--danger)'}`
                    }}>
                        <div className="flex-center" style={{ justifyContent: 'flex-start', gap: '16px', alignItems: 'flex-start' }}>
                            {isCorrect ? <CheckCircle className="text-primary" size={32} /> : <XCircle color="var(--danger)" size={32} />}
                            <div style={{ flex: 1 }}>
                                <h3 style={{ color: isCorrect ? 'var(--primary)' : 'var(--danger)', margin: 0, marginBottom: '12px', fontSize: '1.3rem' }}>
                                    {isCorrect ? 'Correct! (+5 XP)' : 'Still Incorrect'}
                                </h3>
                                
                                <div style={{ background: 'var(--bg-main)', padding: '20px', borderRadius: '12px', border: '1px solid var(--panel-border)' }}>
                                    <p className="text-muted text-sm font-semibold">Correct Answer:</p>
                                    <p style={{ fontWeight: 600, fontSize: '1.2rem', marginBottom: '16px', color: 'var(--text-main)' }}>{currentMistake.correctAnswer || "Not recorded"}</p>
                                    
                                    {currentMistake.explanation && (
                                        <>
                                            <p className="text-muted text-sm font-semibold">Explanation:</p>
                                            <p style={{ color: 'var(--text-main)' }}>{currentMistake.explanation}</p>
                                        </>
                                    )}
                                </div>
                            </div>
                        </div>
                        <div style={{ textAlign: 'right', marginTop: '24px', paddingTop: '16px', borderTop: `1px solid ${isCorrect ? 'var(--primary)' : 'var(--danger)'}` }}>
                            <button className={`btn ${isCorrect ? 'btn-primary' : 'btn-outline'}`} style={{ padding: '12px 24px' }} onClick={handleNext}>
                                {isCorrect ? 'Continue' : 'Try Again Later'}
                            </button>
                        </div>
                    </div>
                )}

                {isCorrect === null && (
                    <button className="btn btn-primary w-full" style={{ padding: '16px', fontSize: '1.1rem' }} disabled={!userAnswer.trim()} onClick={handleCheck}>
                        Submit
                    </button>
                )}
            </div>

            <AchievementModal achievements={newUnlocks} onClose={() => setNewUnlocks([])} />
        </div>
    );
};

export default MistakesReview;
