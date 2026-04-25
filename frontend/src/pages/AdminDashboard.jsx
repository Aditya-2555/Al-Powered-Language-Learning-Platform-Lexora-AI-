import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Users } from 'lucide-react';

const AdminDashboard = ({ user }) => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const res = await axios.get(`http://localhost:8080/api/admin/users/${user.id}`);
                setUsers(res.data);
            } catch (err) {
                console.error("Failed to fetch users", err);
            } finally {
                setLoading(false);
            }
        };
        fetchUsers();
    }, []);

    if (loading) return <div style={{ textAlign: 'center', marginTop: '50px' }}>Loading platform data...</div>;

    return (
        <div className="fade-in">
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '32px' }}>
                <Users size={32} color="var(--primary)" />
                <h2 style={{ margin: 0 }}>Admin Dashboard</h2>
            </div>

            <div className="glass-panel" style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                    <thead>
                        <tr style={{ borderBottom: '1px solid var(--panel-border)', color: 'var(--text-muted)' }}>
                            <th style={{ padding: '12px' }}>ID</th>
                            <th style={{ padding: '12px' }}>Username</th>
                            <th style={{ padding: '12px' }}>Role</th>
                            <th style={{ padding: '12px' }}>Native Lang</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map(u => (
                            <tr key={u.id} style={{ borderBottom: '1px solid var(--panel-border)' }}>
                                <td style={{ padding: '12px' }}>{u.id}</td>
                                <td style={{ padding: '12px', fontWeight: 'bold' }}>{u.username}</td>
                                <td style={{ padding: '12px' }}>
                                    <span style={{
                                        background: u.role === 'ADMIN' ? 'rgba(255, 75, 75, 0.2)' : 'rgba(88, 204, 2, 0.2)',
                                        color: u.role === 'ADMIN' ? 'var(--danger)' : 'var(--primary)',
                                        padding: '4px 8px', borderRadius: '4px', fontSize: '0.8rem', fontWeight: 'bold'
                                    }}>
                                        {u.role || 'USER'}
                                    </span>
                                </td>
                                <td style={{ padding: '12px' }}>{u.nativeLanguage || 'N/A'}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default AdminDashboard;
