import React from 'react';
import { Target, CheckCircle2, Circle } from 'lucide-react';

export default function GoalProgressCard({ goals }) {
  if (!goals || goals.length === 0) {
    return (
      <div className="card">
        <h3>Daily Goals</h3>
        <p className="text-muted text-sm mt-1">Ready for today?</p>
        <div className="pulse mt-3 text-primary">Loading goals...</div>
      </div>
    );
  }

  const getIconForGoal = (type) => {
      switch(type) {
          case 'LESSON': return "📖";
          case 'XP': return "⚡";
          case 'VOCAB': return "🧠";
          default: return "🎯";
      }
  };

  const getLabelForGoal = (goal) => {
      switch(goal.goalType) {
          case 'LESSON': return `Complete ${goal.targetValue} Lesson`;
          case 'XP': return `Earn ${goal.targetValue} XP`;
          case 'VOCAB': return `Learn ${goal.targetValue} Words`;
          default: return goal.goalType;
      }
  }

  const completedCount = goals.filter(g => g.completed).length;
  const progressPercent = Math.round((completedCount / goals.length) * 100) || 0;

  return (
    <div className="card">
      <div className="flex-between align-center" style={{display: 'flex', justifyContent: 'space-between'}}>
        <div>
            <h3 style={{margin: 0, fontSize: '1.2rem'}}>Daily Goals</h3>
            <p className="text-muted text-sm mt-1">{completedCount} of {goals.length} completed</p>
        </div>
        <div style={{ width: '40px', height: '40px', position: 'relative' }}>
            <svg viewBox="0 0 36 36" style={{ width: '100%', height: '100%' }}>
              <path style={{ fill: 'none', stroke: 'var(--panel-border)', strokeWidth: '3' }}
                d="M18 2.0845
                  a 15.9155 15.9155 0 0 1 0 31.831
                  a 15.9155 15.9155 0 0 1 0 -31.831"
              />
              <path style={{ fill: 'none', stroke: 'var(--primary)', strokeWidth: '3', strokeLinecap: 'round' }}
                strokeDasharray={`${progressPercent}, 100`}
                d="M18 2.0845
                  a 15.9155 15.9155 0 0 1 0 31.831
                  a 15.9155 15.9155 0 0 1 0 -31.831"
              />
            </svg>
        </div>
      </div>

      <div className="goal-list mt-4" style={{display: 'flex', flexDirection: 'column', gap: '12px'}}>
        {goals.map((goal, i) => {
            const isDone = goal.completed;
            const progressRatio = Math.min(goal.currentValue / goal.targetValue, 1);
            
            return (
              <div key={i} className={`goal-item ${isDone ? 'completed' : ''}`} style={{display: 'flex', alignItems: 'center', gap: '12px', padding: '12px', background: isDone ? 'var(--card-hover)' : 'var(--bg-main)', borderRadius: '12px', border: '1px solid var(--panel-border)'}}>
                <div className="goal-icon" style={{fontSize: '1.5rem', opacity: isDone ? 1 : 0.8}}>
                    {getIconForGoal(goal.goalType)}
                </div>
                <div className="goal-details" style={{flex: 1}}>
                    <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '6px'}}>
                        <span style={{fontWeight: 600, color: 'var(--text-main)', opacity: isDone ? 0.7 : 1, fontSize: '0.95rem'}}>
                            {getLabelForGoal(goal)}
                        </span>
                        <span className="text-muted text-sm font-semibold">
                            {goal.currentValue} / {goal.targetValue}
                        </span>
                    </div>
                    <div className="progress-bar-container" style={{height: '6px', background: 'var(--panel-border)', borderRadius: '3px', overflow: 'hidden'}}>
                        <div className="progress-fill" style={{height: '100%', background: isDone ? 'var(--accent)' : 'var(--primary)', width: `${progressRatio * 100}%`, transition: 'width 0.5s ease-out'}}></div>
                    </div>
                </div>
                <div className="status-icon" style={{color: isDone ? 'var(--accent)' : 'var(--panel-border)'}}>
                    {isDone ? <CheckCircle2 size={24} /> : <Circle size={24} strokeWidth={2} />}
                </div>
              </div>
            );
        })}
      </div>
    </div>
  );
}
