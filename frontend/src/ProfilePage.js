import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './ProfilePage.css';
import { useNavigate } from 'react-router-dom';

function ProfilePage() {
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({});
    const [errorMessage, setErrorMessage] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        if (storedUser) {
            setUser(storedUser);
            setFormData(storedUser);
        }
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSave = async () => {
        try {
            // Validare
            if (!formData.name || !formData.email || !formData.phone) {
                setErrorMessage("Toate câmpurile obligatorii trebuie completate");
                return;
            }

            // Trimite datele la server pentru actualizare
            console.log(formData);
            await axios.put(`http://localhost:8080/users/update/${user.id}`, formData);

            // Fetch separat pentru a obține datele actualizate ale utilizatorului
            const fetchResponse = await axios.get(`http://localhost:8080/users/${user.id}`);
            const updatedUserData = fetchResponse.data;
            console.log(updatedUserData);

            // Actualizează utilizatorul în localStorage și state
            localStorage.setItem('user', JSON.stringify(updatedUserData));
            setUser(updatedUserData);
            setIsEditing(false);
            setErrorMessage("");
        } catch (error) {
            console.error("Eroare la actualizarea profilului:", error);
            setErrorMessage("Nu s-a putut actualiza profilul. Vă rugăm încercați din nou.");
        }
    };

    const handleCancel = () => {
        setFormData(user);
        setIsEditing(false);
        setErrorMessage("");
    };

    const handleLogout = () => {
        localStorage.removeItem('user');
        navigate('/login');
    };

    if (!user) {
        return <div className="loading-container">Se încarcă...</div>;
    }

    return (
        <div className="profile-container">
            <div className="profile-header">
                <h1>Profilul meu</h1>
                <button className="btn-logout" onClick={handleLogout}>Deconectare</button>
            </div>

            <div className="profile-content">
                <div className="profile-sidebar">
                    <div className="profile-role">
                        <span className={`role-badge ${user.role?.toLowerCase()}`}>
                            {user.role === 'OWNER' ? 'Proprietar' :
                                user.role === 'TENANT' ? 'Chiriaș' :
                                    user.role === 'ADMIN' ? 'Administrator' : 'Utilizator'}
                        </span>
                    </div>
                    <div className="profile-navigation">
                        <button className="nav-btn" onClick={() => navigate('/home')}>Pagina principală</button>
                        <button className="nav-btn" onClick={() => navigate('/spaces')}>Spații</button>
                        <button className="nav-btn" onClick={() => navigate('/contracts')}>Contracte</button>
                    </div>
                </div>

                <div className="profile-details">
                    {isEditing ? (
                        <div className="profile-edit-form">
                            <h2>Editare Profil</h2>
                            {errorMessage && <div className="error-message">{errorMessage}</div>}

                            <div className="form-group">
                                <label>Nume complet *</label>
                                <input
                                    type="text"
                                    name="name"
                                    value={formData.name || ''}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Email *</label>
                                <input
                                    type="email"
                                    name="email"
                                    value={formData.email || ''}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Telefon *</label>
                                <input
                                    type="tel"
                                    name="phone"
                                    value={formData.phone || ''}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Adresă</label>
                                <textarea
                                    name="address"
                                    value={formData.address || ''}
                                    onChange={handleChange}
                                    rows="3"
                                />
                            </div>
                            <div className="form-actions">
                                <button className="btn btn-save" onClick={handleSave}>Salvează</button>
                                <button className="btn btn-cancel" onClick={handleCancel}>Anulează</button>
                            </div>
                        </div>
                    ) : (
                        <div className="profile-info">
                            <div className="profile-info-header">
                                <h2>Informații profil</h2>
                                <button className="btn btn-edit" onClick={() => setIsEditing(true)}>
                                    Editează
                                </button>
                            </div>

                            <div className="info-section">
                                <div className="info-item">
                                    <span className="info-label">Nume complet:</span>
                                    <span className="info-value">{user.name}</span>
                                </div>

                                <div className="info-item">
                                    <span className="info-label">Email:</span>
                                    <span className="info-value">{user.email}</span>
                                </div>

                                <div className="info-item">
                                    <span className="info-label">Nume utilizator:</span>
                                    <span className="info-value">{user.username}</span>
                                </div>

                                <div className="info-item">
                                    <span className="info-label">Telefon:</span>
                                    <span className="info-value">{user.phone || 'Nespecificat'}</span>
                                </div>

                                <div className="info-item">
                                    <span className="info-label">Adresă:</span>
                                    <span className="info-value">{user.address || 'Nespecificat'}</span>
                                </div>

                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ProfilePage;