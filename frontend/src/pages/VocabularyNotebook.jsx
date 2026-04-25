import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Book, Search, ArrowLeft, Star, Clock, Plus, Save, Trash2, X, Check, XCircle, Loader2 } from 'lucide-react';

const VocabularyNotebook = ({ user }) => {
    const { lang } = useParams();
    const navigate = useNavigate();
    
    const [words, setWords] = useState([]);
    const [loading, setLoading] = useState(true);
    const [apiError, setApiError] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [filterTarget, setFilterTarget] = useState('ALL'); 
    
    const [showAddForm, setShowAddForm] = useState(false);
    const [newWord, setNewWord] = useState({ targetWord: '', nativeMeaning: '', partOfSpeech: 'Noun', exampleTarget: '', exampleNative: '' });

    const [isReviewing, setIsReviewing] = useState(false);
    const [reviewQueue, setReviewQueue] = useState([]);
    const [currentReviewIndex, setCurrentReviewIndex] = useState(0);
    const [showAnswer, setShowAnswer] = useState(false);

    const fetchWords = async () => {
        try {
            const res = await axios.get(`http://localhost:8080/api/notebook/${user.id}/${lang}`);
            if (res.data) setWords(res.data);
            setApiError(false);
        } catch (err) {
            console.error("Failed to fetch vocab", err);
            setApiError(true);
            setWords([
                { id: 901, targetWord: 'Mundo', nativeMeaning: 'World', partOfSpeech: 'Noun', exampleTarget: 'Hola mundo.', exampleNative: 'Hello world.', isFavorite: true, due: true },
                { id: 902, targetWord: 'Siempre', nativeMeaning: 'Always', partOfSpeech: 'Adverb', exampleTarget: 'Siempre estoy feliz.', exampleNative: 'I am always happy.', isFavorite: false, due: false },
                { id: 903, targetWord: 'Correr', nativeMeaning: 'To run', partOfSpeech: 'Verb', exampleTarget: 'Me gusta correr.', exampleNative: 'I like to run.', isFavorite: false, due: true }
            ]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchWords();
    }, [user.id, lang]);

    const handleToggleFavorite = async (id) => {
        try {
            const res = await axios.patch(`http://localhost:8080/api/notebook/${id}/favorite`);
            setWords(words.map(w => w.id === id ? res.data : w));
        } catch (e) {
            console.error(e);
            if (apiError) setWords(words.map(w => w.id === id ? { ...w, isFavorite: !w.isFavorite } : w)); 
        }
    };

    const handleDeleteWord = async (id) => {
        if (!window.confirm("Are you sure you want to delete this word?")) return;
        try {
            await axios.delete(`http://localhost:8080/api/notebook/${id}`);
            setWords(words.filter(w => w.id !== id));
        } catch (e) {
            console.error(e);
            if (apiError) setWords(words.filter(w => w.id !== id)); 
        }
    };

    const handleAddWord = async (e) => {
        e.preventDefault();
        try {
            const res = await axios.post(`http://localhost:8080/api/notebook/${user.id}/${lang}`, newWord);
            setWords([res.data, ...words]);
            setShowAddForm(false);
            setNewWord({ targetWord: '', nativeMeaning: '', partOfSpeech: 'Noun', exampleTarget: '', exampleNative: '' });
        } catch (e) {
            console.error("Failed to add word", e);
            if (apiError) {
                setWords([{ ...newWord, id: Math.random() * 1000, isFavorite: false, due: false }, ...words]);
                setShowAddForm(false);
            } else {
                alert("Failed to add word. Might be a duplicate.");
            }
        }
    };

    const startReview = () => {
        const dueItems = words.filter(w => w.due);
        if (dueItems.length === 0) return;
        setReviewQueue(dueItems);
        setCurrentReviewIndex(0);
        setShowAnswer(false);
        setIsReviewing(true);
    };

    const handleReviewAnswer = async (isCorrect) => {
        const currentItem = reviewQueue[currentReviewIndex];
        try {
            const res = await axios.put(`http://localhost:8080/api/notebook/review/${currentItem.id}?isCorrect=${isCorrect}`);
            setWords(words.map(w => w.id === currentItem.id ? res.data : w));
        } catch (e) {
            console.error(e);
            if (apiError) {
                setWords(words.map(w => w.id === currentItem.id ? { ...w, due: false } : w));
            }
        }

        if (currentReviewIndex + 1 < reviewQueue.length) {
            setCurrentReviewIndex(prev => prev + 1);
            setShowAnswer(false);
        } else {
            setIsReviewing(false);
        }
    };

    const filteredWords = words.filter(w => {
        if (filterTarget === 'FAVORITES' && !w.isFavorite) return false;
        if (filterTarget === 'DUE' && !w.due) return false;
        
        if (searchQuery) {
            const q = searchQuery.toLowerCase();
            return w.targetWord.toLowerCase().includes(q) || w.nativeMeaning.toLowerCase().includes(q);
        }
        return true;
    });

    const dueCount = words.filter(w => w.due).length;

    return (
        <div className="fade-in container" style={{ paddingTop: '40px', paddingBottom: '40px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '16px' }}>
                <div>
                    <button className="btn btn-outline" style={{ padding: '8px 12px', marginBottom: '16px' }} onClick={() => navigate(`/analytics/${lang}`)}>
                        <ArrowLeft size={16} style={{ marginRight: '8px' }} /> Analytics
                    </button>
                    <h1 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <Book color="var(--primary)" /> Lexicon Notebook
                    </h1>
                    <p className="text-muted" style={{ margin: 0, marginTop: '4px' }}>Your personal dictionary and Spaced Repetition log.</p>
                </div>
                <div style={{ display: 'flex', gap: '12px' }}>
                    {dueCount > 0 && (
                        <button className="btn btn-primary" onClick={startReview} style={{ background: 'var(--danger)', borderColor: 'var(--danger)' }}>
                            <Clock size={16} style={{ marginRight: '8px' }} /> Review {dueCount} Due Items
                        </button>
                    )}
                    <button className="btn btn-primary" onClick={() => setShowAddForm(!showAddForm)}>
                        <Plus size={16} style={{ marginRight: '8px' }} /> Add Word
                    </button>
                </div>
            </div>

            {apiError && (
                <div style={{ padding: '12px 16px', background: 'var(--danger-bg)', color: 'var(--danger)', borderRadius: '8px', border: '1px solid var(--danger)' }}>
                    Backend connection failed. Displaying mock fallback data.
                </div>
            )}

            {/* Review Overlay */}
            {isReviewing && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.4)', backdropFilter: 'blur(4px)', zIndex: 9999, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
                    <div className="card" style={{ width: '100%', maxWidth: '500px', textAlign: 'center', position: 'relative' }}>
                        <button style={{ position: 'absolute', top: '16px', right: '16px', background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }} onClick={() => setIsReviewing(false)}>
                            <X size={24} />
                        </button>
                        <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '24px', fontWeight: 600 }}>
                            Reviewing {currentReviewIndex + 1} of {reviewQueue.length}
                        </div>
                        <h2 style={{ fontSize: '2.5rem', margin: '0 0 16px 0', color: 'var(--text-main)' }}>{reviewQueue[currentReviewIndex].targetWord}</h2>
                        
                        {!showAnswer ? (
                            <button className="btn btn-outline" style={{ width: '100%', marginTop: '20px' }} onClick={() => setShowAnswer(true)}>
                                Show Meaning
                            </button>
                        ) : (
                            <div className="fade-in" style={{ marginTop: '20px' }}>
                                <div style={{ fontSize: '1.5rem', color: 'var(--primary)', marginBottom: '16px', fontWeight: 600 }}>{reviewQueue[currentReviewIndex].nativeMeaning}</div>
                                {(reviewQueue[currentReviewIndex].exampleTarget) && (
                                    <div style={{ background: 'var(--bg-color)', padding: '16px', borderRadius: '8px', marginBottom: '24px', fontStyle: 'italic', color: 'var(--text-main)', border: '1px solid var(--panel-border)' }}>
                                        "{reviewQueue[currentReviewIndex].exampleTarget}"
                                        {reviewQueue[currentReviewIndex].exampleNative && <div style={{ fontSize: '0.9rem', marginTop: '8px', color: 'var(--text-muted)', fontStyle: 'normal' }}>{reviewQueue[currentReviewIndex].exampleNative}</div>}
                                    </div>
                                )}
                                
                                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                                    <button className="btn btn-outline" style={{ color: 'var(--danger)', borderColor: 'var(--danger)' }} onClick={() => handleReviewAnswer(false)}>
                                        <XCircle size={18} style={{ marginRight: '8px' }} /> I Forgot
                                    </button>
                                    <button className="btn btn-primary" style={{ background: 'var(--accent)' }} onClick={() => handleReviewAnswer(true)}>
                                        <Check size={18} style={{ marginRight: '8px' }} /> I Remembered
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            )}

            {/* Quick Filters & Search */}
            <div className="card" style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', padding: '16px' }}>
                <div style={{ flex: 1, position: 'relative', minWidth: '250px' }}>
                    <Search size={18} color="var(--text-muted)" style={{ position: 'absolute', left: '16px', top: '12px' }} />
                    <input 
                        className="input-field"
                        type="text" 
                        placeholder="Search for a word or translation..." 
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        style={{ paddingLeft: '45px' }}
                    />
                </div>
                <div style={{ display: 'flex', border: '1px solid var(--panel-border)', borderRadius: '8px', overflow: 'hidden' }}>
                    <button 
                        style={{ padding: '10px 16px', background: filterTarget === 'ALL' ? 'var(--primary-glow)' : 'transparent', border: 'none', color: filterTarget === 'ALL' ? 'var(--primary)' : 'var(--text-main)', cursor: 'pointer', fontWeight: 'bold' }}
                        onClick={() => setFilterTarget('ALL')}
                    >All Words</button>
                    <button 
                        style={{ padding: '10px 16px', background: filterTarget === 'FAVORITES' ? 'var(--primary-glow)' : 'transparent', border: 'none', borderLeft: '1px solid var(--panel-border)', color: filterTarget === 'FAVORITES' ? 'var(--primary)' : 'var(--text-main)', cursor: 'pointer', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '4px' }}
                        onClick={() => setFilterTarget('FAVORITES')}
                    ><Star size={16} /> Favorites</button>
                    <button 
                        style={{ padding: '10px 16px', background: filterTarget === 'DUE' ? 'var(--primary-glow)' : 'transparent', border: 'none', borderLeft: '1px solid var(--panel-border)', color: filterTarget === 'DUE' ? 'var(--primary)' : 'var(--text-main)', cursor: 'pointer', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '4px' }}
                        onClick={() => setFilterTarget('DUE')}
                    ><Clock size={16} /> Due for Review</button>
                </div>
            </div>

            {/* Manual Add Form Overlay */}
            {showAddForm && (
                <form className="card fade-in" onSubmit={handleAddWord} style={{ border: '1px solid var(--primary)' }}>
                    <h3 style={{ marginTop: 0, marginBottom: '16px', fontSize: '1.2rem' }}>Add New Vocabulary</h3>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                        <div>
                            <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 500 }}>Target Word</label>
                            <input required type="text" className="input-field mt-1" value={newWord.targetWord} onChange={e => setNewWord({...newWord, targetWord: e.target.value})} />
                        </div>
                        <div>
                            <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 500 }}>Native Meaning</label>
                            <input required type="text" className="input-field mt-1" value={newWord.nativeMeaning} onChange={e => setNewWord({...newWord, nativeMeaning: e.target.value})} />
                        </div>
                        <div>
                            <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 500 }}>Part of Speech</label>
                            <select className="input-field mt-1" value={newWord.partOfSpeech} onChange={e => setNewWord({...newWord, partOfSpeech: e.target.value})}>
                                <option>Noun</option><option>Verb</option><option>Adjective</option><option>Adverb</option><option>Phrase</option>
                            </select>
                        </div>
                        <div>
                            <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 500 }}>Source / Context</label>
                            <input type="text" className="input-field mt-1" value="MANUAL" disabled style={{ background: 'var(--bg-color)' }} />
                        </div>
                        <div style={{ gridColumn: '1 / -1' }}>
                            <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 500 }}>Example Sentence (Target Language)</label>
                            <input type="text" className="input-field mt-1" value={newWord.exampleTarget} onChange={e => setNewWord({...newWord, exampleTarget: e.target.value})} />
                        </div>
                        <div style={{ gridColumn: '1 / -1' }}>
                            <label style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 500 }}>Example Translation</label>
                            <input type="text" className="input-field mt-1" value={newWord.exampleNative} onChange={e => setNewWord({...newWord, exampleNative: e.target.value})} />
                        </div>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
                        <button type="button" className="btn btn-outline" onClick={() => setShowAddForm(false)}>Cancel</button>
                        <button type="submit" className="btn btn-primary"><Save size={16} style={{ marginRight: '6px' }} /> Save Entry</button>
                    </div>
                </form>
            )}

            {loading ? (
                <div style={{ textAlign: 'center', padding: '40px' }}><Loader2 className="text-primary " style={{ animation: 'spin 1s linear infinite' }} size={40} /></div>
            ) : filteredWords.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
                    <h3 className="text-muted" style={{ fontWeight: 500 }}>No notebook items yet. Start practicing to add words here.</h3>
                    <p className="text-muted mt-2">Change your filters or add a new word to get started!</p>
                </div>
            ) : (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
                    {filteredWords.map((word) => (
                        <div key={word.id} className="card highlight" style={{ position: 'relative', display: 'flex', flexDirection: 'column', padding: '24px' }}>
                            <div style={{ position: 'absolute', top: '16px', right: '16px', display: 'flex', gap: '16px', alignItems: 'center' }}>
                                <Trash2 color="var(--danger)" size={18} style={{ cursor: 'pointer', opacity: 0.6 }} onClick={() => handleDeleteWord(word.id)} />
                                <Star color={word.isFavorite ? "#F59E0B" : "var(--panel-border)"} fill={word.isFavorite ? "#F59E0B" : "transparent"} size={22} style={{ cursor: 'pointer', transition: 'all 0.2s' }} onClick={() => handleToggleFavorite(word.id)} />
                            </div>
                            
                            <div style={{ display: 'flex', gap: '8px', marginBottom: '12px' }}>
                                <span style={{ background: 'var(--bg-color)', padding: '4px 8px', borderRadius: '4px', fontSize: '0.7rem', textTransform: 'uppercase', border: '1px solid var(--panel-border)', fontWeight: 600 }}>{word.partOfSpeech}</span>
                                <span style={{ background: word.due ? 'var(--danger-bg)' : 'var(--primary-glow)', color: word.due ? 'var(--danger)' : 'var(--primary)', padding: '4px 8px', borderRadius: '4px', fontSize: '0.7rem', textTransform: 'uppercase', fontWeight: 700 }}>
                                    {word.due ? 'Needs Review' : 'Learning'}
                                </span>
                            </div>

                            <h2 style={{ margin: '0 0 4px 0', fontSize: '1.6rem', color: 'var(--text-main)' }}>{word.targetWord}</h2>
                            <h3 style={{ margin: '0 0 16px 0', fontSize: '1.1rem', color: 'var(--primary)', fontWeight: 'normal' }}>{word.nativeMeaning}</h3>

                            {(word.exampleTarget || word.exampleNative) && (
                                <div style={{ background: 'var(--bg-color)', borderLeft: '3px solid var(--primary)', padding: '16px', borderRadius: '0 8px 8px 0', marginBottom: '16px', flex: 1 }}>
                                    {word.exampleTarget && <div style={{ fontStyle: 'italic', marginBottom: '4px', fontSize: '1rem' }}>"{word.exampleTarget}"</div>}
                                    {word.exampleNative && <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{word.exampleNative}</div>}
                                </div>
                            )}

                            <div style={{ marginTop: 'auto', borderTop: '1px solid var(--panel-border)', paddingTop: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontWeight: 500 }}>
                                    Source: {word.source || 'MANUAL'}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
            <style jsx="true">{`
                @keyframes spin { 100% { transform: rotate(360deg); } }
            `}</style>
        </div>
    );
};

export default VocabularyNotebook;
