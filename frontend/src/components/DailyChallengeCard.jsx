import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Sparkles, CheckCircle2, XCircle, Loader2 } from 'lucide-react';

export default function DailyChallengeCard({ userId, languageCode, onComplete }) {
  const [challenge, setChallenge] = useState(null);
  const [selected, setSelected] = useState(null);
  const [status, setStatus] = useState('idle'); 
  const [earnedXp, setEarnedXp] = useState(0);

  useEffect(() => {
    if (userId && languageCode) {
      axios.get(`http://localhost:8080/api/challenge?userId=${userId}&languageCode=${languageCode}`)
        .then(res => setChallenge(res.data))
        .catch(err => console.error("Could not load challenge", err));
    }
  }, [userId, languageCode]);

  const handleSubmit = () => {
    if (!selected) return;
    setStatus('loading');
    axios.post(`http://localhost:8080/api/challenge/submit?userId=${userId}&languageCode=${languageCode}`, { answer: selected })
      .then(res => {
        if (res.data.xpEarned > 0) {
          setStatus('correct');
          setEarnedXp(res.data.xpEarned);
          if (onComplete) onComplete(res.data.xpEarned);
        } else {
          setStatus('incorrect');
        }
      })
      .catch(err => {
        setStatus('idle');
        console.error(err);
      });
  };

  if (!challenge) {
    return (
      <div className="card flex-center" style={{ minHeight: '200px', flexDirection: 'column', gap: '12px' }}>
        <Loader2 className="spinner text-primary" size={32} style={{ animation: 'spin 1s infinite linear' }} />
        <div className="text-muted">Loading Challenge...</div>
      </div>
    );
  }

  if (challenge.isCompleted || status === 'correct') {
    return (
      <div className="card challenge-card" style={{ border: '2px solid var(--accent)', background: 'var(--bg-main)' }}>
        <div className="challenge-header" style={{display: 'flex', alignItems: 'center', gap: '8px'}}>
          <Sparkles className="text-accent" size={24} />
          <h3 style={{margin: 0, fontSize: '1.2rem', color: 'var(--text-main)'}}>Daily Challenge</h3>
        </div>
        <p className="mt-2 text-muted" style={{ fontWeight: 500 }}>You earned {earnedXp || 20} XP. Excellent work today!</p>
        <div className="flex-center mt-4">
          <CheckCircle2 color="var(--accent)" size={48} />
        </div>
        <div className="mt-4 p-3 fade-in" style={{ background: 'rgba(16, 185, 129, 0.1)', borderRadius: '8px', borderLeft: '3px solid var(--accent)' }}>
            {challenge.explanation}
        </div>
      </div>
    );
  }

  let options = [];
  try { options = JSON.parse(challenge.optionsJson); } catch (e) { options = []; }

  return (
    <div className="card challenge-card" style={{ borderTop: '4px solid #8B5CF6' }}>
      <div className="challenge-header" style={{display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '1rem'}}>
        <Sparkles color="#8B5CF6" size={24} />
        <h3 style={{margin: 0, fontSize: '1.2rem'}}>Daily Challenge</h3>
      </div>
      <p className="question-text" style={{fontSize: '1.1rem', marginBottom: '1rem', fontWeight: 600}}>{challenge.question}</p>
      
      <div className="options-grid" style={{display: 'grid', gap: '12px'}}>
        {options.map((opt, i) => (
          <button 
            key={i} 
            className={`btn btn-outline`}
            style={{
                textAlign: 'left',
                justifyContent: 'flex-start',
                padding: '12px 16px',
                borderColor: selected === opt ? (status === 'incorrect' ? 'var(--danger)' : '#8B5CF6') : 'var(--panel-border)',
                backgroundColor: selected === opt ? (status === 'incorrect' ? 'var(--danger-bg)' : 'rgba(139, 92, 246, 0.05)') : 'var(--bg-main)',
                color: 'var(--text-main)',
                fontWeight: 500
            }}
            onClick={() => { setSelected(opt); setStatus('idle'); }}
          >
            {opt}
          </button>
        ))}
      </div>

      {status === 'incorrect' && (
        <div className="mt-3 fade-in" style={{color: 'var(--danger)', display: 'flex', alignItems: 'center', gap: '8px', fontWeight: 500}}>
          <XCircle size={18} /> That's not correct. Try again!
        </div>
      )}

      <button className="btn w-full mt-4" 
        style={{ background: '#8B5CF6', color: 'white', padding: '12px', fontSize: '1rem' }} 
        disabled={!selected || status === 'loading'} 
        onClick={handleSubmit}>
        {status === 'loading' ? 'Checking...' : 'Submit Answer'}
      </button>
      <style jsx="true">{`
          @keyframes spin { 100% { transform: rotate(360deg); } }
      `}</style>
    </div>
  );
}
