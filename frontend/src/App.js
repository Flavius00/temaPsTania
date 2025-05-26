// frontend/src/App.js - Add WebSocket integration
import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { connect, disconnect } from './helper/websocket';
import Login from './Login';
import HomePage from './HomePage';
import SpacesPage from './SpacesPage';
import ContractsPage from './ContractsPage';
import ConfirmContractPage from './ConfirmContractPage';
import MapPage from './MapPage';
import SpaceDetailsPage from './SpaceDetailsPage';
import ProfilePage from './ProfilePage';
import BuildingsPage from './BuildingsPage';
import CreateSpacePage from './CreateSpacePage';
import RentalContractPage from './RentalContractPage';
import NotificationCenter from './components/NotificationCenter';

function App() {
    const [user, setUser] = useState(() => {
        const storedUser = localStorage.getItem('user');
        return storedUser ? JSON.parse(storedUser) : null;
    });
    const [showNotifications, setShowNotifications] = useState(false);

    useEffect(() => {
        const handleStorageChange = () => {
            const updatedUser = JSON.parse(localStorage.getItem('user'));
            setUser(updatedUser);
        };

        window.addEventListener('storage', handleStorageChange);

        // Connect to WebSocket if user is logged in
        if (user) {
            connect(
                () => console.log('WebSocket Connected'),
                (error) => console.error('WebSocket Error:', error)
            );
        }

        return () => {
            window.removeEventListener('storage', handleStorageChange);
            // Disconnect WebSocket when component unmounts
            disconnect();
        };
    }, [user]);

    // Toggle notification panel
    const toggleNotifications = () => {
        setShowNotifications(!showNotifications);
    };

    return (
        <Router>
            {user && (
                <div className="notification-icon" onClick={toggleNotifications}>
                    <i className="fas fa-bell"></i>
                </div>
            )}

            {user && showNotifications && (
                <div className="notification-panel">
                    <NotificationCenter />
                </div>
            )}

            <Routes>
                <Route path="/login" element={user ? <Navigate to="/home" replace /> : <Login setUser={setUser} />} />
                <Route path="/home" element={user ? <HomePage setUser={setUser} /> : <Navigate to="/login" />} />
                <Route path="/spaces" element={user ? <SpacesPage /> : <Navigate to="/login" />} />
                <Route path="/spaces/create" element={user ? <CreateSpacePage /> : <Navigate to="/login" />} />
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="*" element={<Navigate to="/login" replace />} />
                <Route path="/contracts" element={user ? <ContractsPage /> : <Navigate to="/login" />} />
                <Route path="/payment" element={user ? <RentalContractPage />  : <Navigate to="/login" />} />
                <Route path="/payment/confirm" element={user ?  <ConfirmContractPage />: <Navigate to="/login" />} />
                <Route path="/map" element={user ? <MapPage /> : <Navigate to="/login" />} />
                <Route path="/space-details/:id" element={user ? <SpaceDetailsPage /> : <Navigate to="/login" />} />
                <Route path="/profile" element={user ? <ProfilePage /> : <Navigate to="/login" />} />
                <Route path="/buildings" element={user ? <BuildingsPage /> : <Navigate to="/login" />} />
            </Routes>
        </Router>
    );
}

export default App;