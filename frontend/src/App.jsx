import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import axios from 'axios';

// Add a response interceptor to globally unwrap the {success, data, message} envelope from Spring Boot
axios.interceptors.response.use(
  (response) => {
    // If the backend wraps the response and success is true, return just the data payload
    if (response.data && response.data.hasOwnProperty('success') && response.data.success) {
        // We override response.data with the nested payload to prevent breaking all existing React fetch calls
        response.data = response.data.data;
    }
    return response;
  },
  (error) => {
    // Also try to unwrap error messages if present
    if (error.response && error.response.data && !error.response.data.success) {
        console.error("Backend Error:", error.response.data.message);
        // You could dispatch a global toast notification here
    }
    return Promise.reject(error);
  }
);
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Dashboard from './pages/Dashboard';
import Lesson from './pages/Lesson';
import Tutor from './pages/Tutor';
import AdminDashboard from './pages/AdminDashboard';
import Landing from './pages/Landing';
import LanguageSetup from './pages/LanguageSetup';
import AnalyticsDashboard from './pages/AnalyticsDashboard';
import MistakesReview from './pages/MistakesReview';
import VocabularyNotebook from './pages/VocabularyNotebook';
import Scenarios from './pages/Scenarios';
import SpeakingPractice from './pages/SpeakingPractice';

function App() {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('linguaUser');
    return saved ? JSON.parse(saved) : null;
  });

  const login = (userData) => {
    setUser(userData);
    localStorage.setItem('linguaUser', JSON.stringify(userData));
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('linguaUser');
  };

  return (
    <Router>
      <div className="container">
        <Navbar user={user} onLogout={logout} />
        <Routes>
          <Route path="/" element={user ? (user.role === 'ADMIN' ? <Navigate to="/admin" /> : (!user.nativeLanguage ? <Navigate to="/setup" /> : <Navigate to="/dashboard" />)) : <Landing />} />
          <Route path="/auth" element={user ? <Navigate to="/" /> : <Home onLogin={login} />} />
          <Route path="/setup" element={user && !user.nativeLanguage && user.role !== 'ADMIN' ? <LanguageSetup user={user} onUpdate={login} /> : <Navigate to="/" />} />
          <Route path="/dashboard" element={user && user.role !== 'ADMIN' && user.nativeLanguage ? <Dashboard user={user} /> : <Navigate to="/" />} />
          <Route path="/admin" element={user && user.role === 'ADMIN' ? <AdminDashboard user={user} /> : <Navigate to="/" />} />
          <Route path="/lesson/:lang" element={user && user.role !== 'ADMIN' && user.nativeLanguage ? <Lesson user={user} /> : <Navigate to="/" />} />
          <Route path="/tutor/:lang" element={user && user.role !== 'ADMIN' && user.nativeLanguage ? <Tutor user={user} /> : <Navigate to="/" />} />
          <Route path="/analytics/:lang" element={user && user.role !== 'ADMIN' && user.nativeLanguage ? <AnalyticsDashboard user={user} /> : <Navigate to="/" />} />
          <Route path="/mistakes/:lang" element={user && user.role !== 'ADMIN' && user.nativeLanguage ? <MistakesReview user={user} /> : <Navigate to="/" />} />
          <Route path="/notebook/:lang" element={user && user.role !== 'ADMIN' && user.nativeLanguage ? <VocabularyNotebook user={user} /> : <Navigate to="/" />} />
          <Route path="/scenarios/:lang" element={user && user.role !== 'ADMIN' && user.nativeLanguage ? <Scenarios user={user} /> : <Navigate to="/" />} />
          <Route path="/speaking/:lang" element={user && user.role !== 'ADMIN' && user.nativeLanguage ? <SpeakingPractice user={user} /> : <Navigate to="/" />} />
          <Route path="*" element={<div style={{ textAlign: 'center', marginTop: '100px' }}><h2>404 - Page Not Found</h2><button className="btn btn-primary" onClick={() => window.location.href='/'}>Go Home</button></div>} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
