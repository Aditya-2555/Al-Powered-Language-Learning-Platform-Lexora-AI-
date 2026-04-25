import React, { useEffect, useState } from 'react';
import { Award, Star, Flame, CheckCircle, Book, Target } from 'lucide-react';

const AchievementModal = ({ achievements, onClose }) => {
    const [currentIdx, setCurrentIdx] = useState(0);

    useEffect(() => {
        if (!achievements || achievements.length === 0) return;
        
        const timer = setTimeout(() => {
            if (currentIdx < achievements.length - 1) {
                setCurrentIdx(c => c + 1);
            } else {
                onClose();
            }
        }, 3500); // Display each for 3.5 seconds

        return () => clearTimeout(timer);
    }, [currentIdx, achievements, onClose]);

    if (!achievements || achievements.length === 0) return null;

    const currentBadge = achievements[currentIdx];

    const getIcon = (name) => {
        const props = { size: 64, color: 'var(--primary)' };
        switch(name) {
            case 'Flame': return <Flame {...props} color="var(--warning)" />;
            case 'Star': return <Star {...props} color="var(--info)" />;
            case 'CheckCircle': return <CheckCircle {...props} />;
            case 'Book': return <Book {...props} color="#a855f7" />;
            case 'Target': return <Target {...props} color="var(--danger)" />;
            default: return <Award {...props} color="#ffd700" />;
        }
    };

    return (
        <div style={{
            position: 'fixed',
            top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: 'rgba(15, 23, 42, 0.8)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 9999,
            animation: 'fadeIn 0.3s ease-out'
        }}>
            <div style={{
                background: 'var(--panel-bg)',
                border: '2px solid var(--primary)',
                borderRadius: '16px',
                padding: '40px',
                textAlign: 'center',
                boxShadow: '0 0 30px rgba(88, 204, 2, 0.3)',
                minWidth: '350px',
                animation: 'scaleUp 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275)'
            }}>
                <div style={{ marginBottom: '20px', animation: 'spinPulse 1s ease-in-out' }}>
                    {getIcon(currentBadge.iconIdentifier)}
                </div>
                <h2 style={{ margin: '0 0 8px 0', color: 'var(--text-main)', fontSize: '1.8rem', textTransform: 'uppercase', letterSpacing: '2px' }}>Achievement Unlocked!</h2>
                <h3 style={{ margin: '0 0 16px 0', color: 'var(--primary)', fontSize: '1.4rem' }}>{currentBadge.title}</h3>
                <p style={{ color: 'var(--text-muted)' }}>{currentBadge.description}</p>
                <div style={{ marginTop: '24px', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                    Badge {currentIdx + 1} of {achievements.length}
                </div>
            </div>

            <style jsx="true">{`
                @keyframes scaleUp {
                    0% { transform: scale(0.8); opacity: 0; }
                    100% { transform: scale(1); opacity: 1; }
                }
                @keyframes spinPulse {
                    0% { transform: scale(1) rotate(-10deg); }
                    50% { transform: scale(1.2) rotate(10deg); }
                    100% { transform: scale(1) rotate(0deg); }
                }
            `}</style>
        </div>
    );
};

export default AchievementModal;
