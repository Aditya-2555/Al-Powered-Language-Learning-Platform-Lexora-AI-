import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { ArrowLeft, CheckCircle, XCircle, Loader2 } from 'lucide-react';
import AchievementModal from '../components/AchievementModal';

const Lesson = ({ user }) => {
    const { lang } = useParams();
    const navigate = useNavigate();

    const [difficulty, setDifficulty] = useState(null);
    const [lesson, setLesson] = useState(null);
    const [userAnswer, setUserAnswer] = useState('');
    const [isCorrect, setIsCorrect] = useState(null);
    
    const [loading, setLoading] = useState(false);
    const [savingVocab, setSavingVocab] = useState(false);
    const [vocabSaved, setVocabSaved] = useState(false);
    
    const [streak, setStreak] = useState(0);
    const [achievements, setAchievements] = useState([]);
    
    const [sentenceOrder, setSentenceOrder] = useState([]);

    useEffect(() => {
        if (lesson?.type === 'SentenceOrder' && lesson?.options) {
            const rawOptions = JSON.parse(lesson.options);
            const currentSentence = sentenceOrder.map(idx => rawOptions[idx]).join(' ');
            setUserAnswer(currentSentence);
        }
    }, [sentenceOrder, lesson]);

    const fetchLesson = async (selectedDifficulty) => {
        if (!user?.id) return;
        setLoading(true);
        setLesson(null);
        setUserAnswer('');
        setIsCorrect(null);
        setSentenceOrder([]);
        setVocabSaved(false);

        try {
            const res = await axios.get(`http://localhost:8080/api/lessons/${lang}?userId=${user.id}&difficulty=${selectedDifficulty}`);
            
            let data = res.data;
            if (Array.isArray(data) && data.length > 0) data = data[0];
            
            setLesson(data);
        } catch (err) {
            console.error('API Error: Could not fetch lesson', err);
        } finally {
            setLoading(false);
        }
    };

    const handleStart = (level) => {
        setDifficulty(level);
        fetchLesson(level);
    };

    const handleCheck = async () => {
        if (!userAnswer.trim() || isCorrect !== null || !lesson) return;

        const correctTarget = (lesson.correctAnswer || '').toLowerCase().trim();
        const userInput = userAnswer.toLowerCase().trim();
        const score = (userInput === correctTarget);

        setIsCorrect(score);

        const payload = {
            userId: user.id,
            languageCode: lang,
            activityType: 'LESSON',
            topic: lesson.topic || lesson.type,
            sourceLessonId: lesson.id,
            isCorrect: score,
            content: lesson.content || '',
            mistake: score ? null : userInput,
            correctAnswer: lesson.correctAnswer || '',
            explanation: lesson.explanation || ''
        };

        try {
            let newlyUnlocked = [];

            const actRes = await axios.post('http://localhost:8080/api/progress/activity', payload);
            if (actRes.data?.newlyUnlockedAchievements) {
                newlyUnlocked = [...newlyUnlocked, ...actRes.data.newlyUnlockedAchievements];
            }

            if (score) {
                setStreak(streak + 1);
                const compRes = await axios.post(`http://localhost:8080/api/progress/lesson-complete/${user.id}/${lang}?correctCount=1&totalCount=1`);
                
                if (compRes.data?.newlyUnlockedAchievements) {
                    newlyUnlocked = [...newlyUnlocked, ...compRes.data.newlyUnlockedAchievements];
                }
            } else {
                setStreak(0);
            }

            if (newlyUnlocked.length > 0) setAchievements(newlyUnlocked);
        } catch (err) {
            console.error('Activity tracking failed:', err);
        }
    };

    const handleSaveVocab = async () => {
        if (!lesson || vocabSaved) return;
        setSavingVocab(true);
        try {
            await axios.post(`http://localhost:8080/api/notebook/${user.id}/${lang}`, {
                targetWord: lesson.correctAnswer || lesson.content,
                nativeMeaning: lesson.explanation || 'Saved from review',
                partOfSpeech: 'Phrase',
                source: 'LESSON',
                exampleTarget: lesson.content,
                exampleNative: lesson.explanation
            });
            setVocabSaved(true);
        } catch (e) {
            console.error("Lexicon error", e);
        } finally {
            setSavingVocab(false);
        }
    };

    if (!difficulty) {
        return (
            <div className="container fade-in" style={{ maxWidth: '600px', textAlign: 'center', paddingTop: '40px' }}>
                <button className="btn btn-outline mb-4" onClick={() => navigate('/dashboard')}>
                    <ArrowLeft size={16} style={{ marginRight: '8px' }} /> Dashboard
                </button>
                <div className="card" style={{ padding: '40px' }}>
                    <h2 style={{ fontSize: '1.8rem', marginBottom: '8px' }}>Select Difficulty</h2>
                    <p className="text-muted">Choose your challenge level.</p>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginTop: '32px' }}>
                        <button className="btn btn-outline" onClick={() => handleStart('Beginner')} style={{ padding: '16px', fontSize: '1.1rem' }}>🌱 Beginner</button>
                        <button className="btn btn-outline" onClick={() => handleStart('Intermediate')} style={{ padding: '16px', fontSize: '1.1rem' }}>⭐ Intermediate</button>
                        <button className="btn btn-outline" onClick={() => handleStart('Advanced')} style={{ padding: '16px', fontSize: '1.1rem' }}>🔥 Advanced</button>
                    </div>
                </div>
            </div>
        );
    }

    if (loading) {
        return (
            <div className="flex-center mt-8">
                <Loader2 className="spinner text-primary" size={48} style={{ animation: 'spin 1s infinite' }} />
                <p className="text-muted mt-4 font-semibold">Generating lesson...</p>
                <style jsx="true">{`@keyframes spin { 100% { transform: rotate(360deg); } }`}</style>
            </div>
        );
    }

    if (!lesson) {
        return (
            <div className="text-center mt-8">
                <p className="text-muted">Failed to load lesson.</p>
                <button className="btn btn-primary mt-4" onClick={() => fetchLesson(difficulty)}>Try Again</button>
            </div>
        );
    }

    let parsedOptions = [];
    if (lesson.options) {
        try { parsedOptions = JSON.parse(lesson.options); } catch (e) {}
    }

    return (
        <div className="container fade-in" style={{ maxWidth: '600px', paddingTop: '40px', paddingBottom: '40px' }}>
            <div className="flex-center" style={{ justifyContent: 'space-between', marginBottom: '24px' }}>
                <button className="btn btn-outline" onClick={() => setDifficulty(null)}>
                    <ArrowLeft size={16} style={{ marginRight: '8px' }} /> End Session
                </button>
                <div style={{ color: 'var(--warning)', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '6px', background: 'var(--bg-main)', padding: '6px 12px', borderRadius: '20px', border: '1px solid var(--panel-border)' }}>
                    🔥 Streak: {streak}
                </div>
            </div>

            <div className="card" style={{ padding: '32px' }}>
                <h3 className="text-muted text-sm" style={{ textTransform: 'uppercase', marginBottom: '8px', letterSpacing: '1px' }}>[{lesson.topic || lesson.type}]</h3>
                <h2 className="mb-4" style={{ fontSize: '1.6rem' }}>{lesson.instruction || "Answer the question"}</h2>

                <div className="mb-4" style={{ padding: '24px', background: 'var(--bg-color)', borderRadius: '12px', border: '1px solid var(--panel-border)', fontSize: '1.4rem', color: 'var(--text-main)', textAlign: 'center', fontWeight: 500 }}>
                    {lesson.content || 'Content empty...'}
                </div>

                {lesson.type === 'SentenceOrder' && parsedOptions.length > 0 ? (
                    <div className="mb-4">
                        <div style={{ minHeight: '80px', padding: '16px', background: 'var(--bg-color)', border: '2px dashed var(--panel-border)', borderRadius: '12px', marginBottom: '16px', display: 'flex', gap: '8px', flexWrap: 'wrap', alignItems: 'center' }}>
                            {sentenceOrder.length === 0 && <span className="text-muted" style={{ margin: '0 auto' }}>Build your sentence...</span>}
                            {sentenceOrder.map((idx) => (
                                <button key={`chosen-${idx}`} onClick={() => isCorrect === null && setSentenceOrder(sentenceOrder.filter((i) => i !== idx))} className="btn btn-primary" style={{ padding: '8px 16px', borderRadius: '20px', boxShadow: 'none' }}>
                                    {parsedOptions[idx]}
                                </button>
                            ))}
                        </div>

                        <div className="flex-center" style={{ gap: '10px', flexWrap: 'wrap' }}>
                            {parsedOptions.map((opt, i) => (
                                <button key={`avail-${i}`} disabled={sentenceOrder.includes(i) || isCorrect !== null} onClick={() => setSentenceOrder([...sentenceOrder, i])} className="btn btn-outline" style={{ opacity: sentenceOrder.includes(i) ? 0.3 : 1, borderRadius: '20px', background: 'var(--bg-main)' }}>
                                    {opt}
                                </button>
                            ))}
                        </div>
                    </div>
                ) : parsedOptions.length > 0 ? (
                    <div style={{ display: 'grid', gap: '12px', marginBottom: '24px' }}>
                        {parsedOptions.map((opt, i) => (
                            <button key={i} className={`btn btn-outline`} style={{ justifyContent: 'flex-start', padding: '16px', fontSize: '1.05rem', background: userAnswer === opt ? 'var(--primary-glow)' : 'var(--bg-main)', borderColor: userAnswer === opt ? 'var(--primary)' : 'var(--panel-border)', color: userAnswer === opt ? 'var(--primary)' : 'var(--text-main)', fontWeight: userAnswer === opt ? 600 : 400 }} onClick={() => setUserAnswer(opt)} disabled={isCorrect !== null}>
                                {opt}
                            </button>
                        ))}
                    </div>
                ) : (
                    <input type="text" value={userAnswer} onChange={(e) => setUserAnswer(e.target.value)} placeholder="Type your answer here..." className="input-field" style={{ padding: '16px', fontSize: '1.1rem', marginBottom: '24px' }} disabled={isCorrect !== null} />
                )}

                {isCorrect !== null && (
                    <div className="mb-4 fade-in" style={{ padding: '24px', borderRadius: '12px', background: isCorrect ? 'var(--primary-glow)' : 'var(--danger-bg)', border: `1px solid ${isCorrect ? 'var(--primary)' : 'var(--danger)'}` }}>
                        <div className="flex-center" style={{ gap: '16px', alignItems: 'flex-start' }}>
                            {isCorrect ? <CheckCircle className="text-primary" size={32} /> : <XCircle color="var(--danger)" size={32} />}
                            <div style={{ flex: 1 }}>
                                <h3 style={{ margin: 0, fontSize: '1.4rem', color: isCorrect ? 'var(--primary)' : 'var(--danger)' }}>{isCorrect ? 'Correct!' : 'Incorrect'}</h3>
                                <p style={{ fontWeight: '600', marginTop: '8px', fontSize: '1.1rem' }}>Answer: {lesson.correctAnswer}</p>
                                <p className="text-muted text-sm mt-2" style={{ lineHeight: 1.5 }}>💡 {lesson.explanation}</p>
                                
                                <button className="btn btn-outline mt-4" style={{ background: 'var(--bg-main)' }} onClick={handleSaveVocab} disabled={savingVocab || vocabSaved}>
                                    {vocabSaved ? "✅ Saved to Notebook" : "⭐ Save to Lexicon"}
                                </button>
                            </div>
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '20px', borderTop: `1px solid ${isCorrect ? 'var(--primary)' : 'var(--danger)'}`, paddingTop: '16px' }}>
                            {!isCorrect && <button className="btn btn-outline" style={{ background: 'var(--bg-main)' }} onClick={() => { setIsCorrect(null); setUserAnswer(''); setSentenceOrder([]); }}>Retry Lesson</button>}
                            <button className="btn btn-primary" onClick={() => fetchLesson(difficulty)}>Next Lesson</button>
                        </div>
                    </div>
                )}

                {isCorrect === null && (
                    <button className="btn btn-primary w-full" style={{ padding: '16px', fontSize: '1.1rem' }} disabled={!userAnswer.trim()} onClick={handleCheck}>
                        Submit Answer
                    </button>
                )}
            </div>

            <AchievementModal achievements={achievements} onClose={() => setAchievements([])} />
        </div>
    );
};

export default Lesson;