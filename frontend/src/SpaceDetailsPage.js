import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './SpaceDetailsPage.css';

function SpaceDetailsPage() {
    const { id } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    const [space, setSpace] = useState(null);
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({});

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        setUser(storedUser);

        const fetchSpace = async () => {
            setLoading(true);
            try {
                // Use space data from state if available, otherwise fetch from API
                if (location.state?.spaceData) {
                    setSpace(location.state.spaceData);
                    setFormData(location.state.spaceData);
                } else {
                    const response = await axios.get(`http://localhost:8080/spaces/details/${id}`);
                    setSpace(response.data);
                    setFormData(response.data);
                }
            } catch (error) {
                console.error('Error fetching space details:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchSpace();
    }, [id, location.state]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => {
            if (name.includes('.')) {
                const [objName, objProp] = name.split('.');
                return {
                    ...prev,
                    [objName]: {
                        ...prev[objName],
                        [objProp]: value
                    }
                };
            }
            return { ...prev, [name]: value };
        });
    };

    const handleCheckboxChange = (e) => {
        const { name, checked } = e.target;
        setFormData(prev => ({ ...prev, [name]: checked }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await axios.post('http://localhost:8080/spaces/update', formData);
            setSpace(formData);
            setIsEditing(false);
            alert('Space updated successfully!');
        } catch (error) {
            console.error('Error updating space:', error);
            alert('Failed to update space. Please try again.');
        }
    };

    const handleDelete = async () => {
        if (window.confirm('Are you sure you want to delete this space? This action cannot be undone.')) {
            try {
                await axios.post(`http://localhost:8080/spaces/delete/${id}`);
                navigate('/spaces');
                alert('Space deleted successfully!');
            } catch (error) {
                console.error('Error deleting space:', error);
                alert('Failed to delete space. Please try again.');
            }
        }
    };

    const handleRent = () => {
        navigate('/payment', { state: { selectedSpace: space } });
    };

    if (loading) {
        return <div className="loading">Loading space details...</div>;
    }

    if (!space) {
        return <div className="error-message">Space not found</div>;
    }

    const canEdit = user?.role === 'OWNER' && space.owner?.id === user.id;
    const canRent = user?.role === 'TENANT' && space.available;

    const renderAmenities = () => {
        if (!space.amenities || space.amenities.length === 0) {
            return <p>No amenities listed</p>;
        }

        return (
            <ul className="amenities-list">
                {space.amenities.map((amenity, index) => (
                    <li key={index}>{amenity}</li>
                ))}
            </ul>
        );
    };

    const renderSpecificDetails = () => {
        switch (space.spaceType) {
            case 'OFFICE':
                return (
                    <div className="specific-details">
                        <h3>Office Details</h3>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <span className="detail-label">Floors:</span>
                                <span className="detail-value">{space.floors || 'N/A'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Number of Rooms:</span>
                                <span className="detail-value">{space.numberOfRooms || 'N/A'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Reception Area:</span>
                                <span className="detail-value">{space.hasReception ? 'Yes' : 'No'}</span>
                            </div>
                        </div>
                    </div>
                );
            case 'RETAIL':
                return (
                    <div className="specific-details">
                        <h3>Retail Details</h3>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <span className="detail-label">Shop Window Size:</span>
                                <span className="detail-value">{space.shopWindowSize || 'N/A'} m</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Customer Entrance:</span>
                                <span className="detail-value">{space.hasCustomerEntrance ? 'Yes' : 'No'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Max Occupancy:</span>
                                <span className="detail-value">{space.maxOccupancy || 'N/A'} people</span>
                            </div>
                        </div>
                    </div>
                );
            case 'WAREHOUSE':
                return (
                    <div className="specific-details">
                        <h3>Warehouse Details</h3>
                        <div className="detail-grid">
                            <div className="detail-item">
                                <span className="detail-label">Ceiling Height:</span>
                                <span className="detail-value">{space.ceilingHeight || 'N/A'} m</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Loading Dock:</span>
                                <span className="detail-value">{space.hasLoadingDock ? 'Yes' : 'No'}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Security Level:</span>
                                <span className="detail-value">{space.securityLevel || 'N/A'}</span>
                            </div>
                        </div>
                    </div>
                );
            default:
                return null;
        }
    };

    return (
        <div className="space-details-container">
            <div className="details-header">
                <button className="btn btn-back" onClick={() => navigate('/spaces')}>
                    ← Back to Spaces
                </button>
                <h2>{space.name}</h2>
                <div className="space-type-badge">{space.spaceType}</div>
            </div>

            {isEditing ? (
                <form className="edit-form" onSubmit={handleSubmit}>
                    <div className="form-section">
                        <h3>Basic Information</h3>
                        <div className="form-group">
                            <label>Name:</label>
                            <input
                                type="text"
                                name="name"
                                value={formData.name || ''}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Description:</label>
                            <textarea
                                name="description"
                                value={formData.description || ''}
                                onChange={handleChange}
                                rows="4"
                            />
                        </div>
                        <div className="form-row">
                            <div className="form-group">
                                    <label>Price (€/month):</label>
                                <input
                                    type="number"
                                    name="pricePerMonth"
                                    value={formData.pricePerMonth || ''}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label>Area (m²):</label>
                                <input
                                    type="number"
                                    name="area"
                                    value={formData.area || ''}
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>
                        <div className="form-group">
                            <label>Address:</label>
                            <input
                                type="text"
                                name="address"
                                value={formData.address || ''}
                                onChange={handleChange}
                            />
                        </div>
                        <div className="form-group checkbox">
                            <label>
                                <input
                                    type="checkbox"
                                    name="available"
                                    checked={formData.available || false}
                                    onChange={handleCheckboxChange}
                                />
                                Available for Rent
                            </label>
                        </div>
                    </div>

                    {/* Type-specific fields would go here */}

                    <div className="form-actions">
                        <button type="submit" className="btn btn-save">Save Changes</button>
                        <button
                            type="button"
                            className="btn btn-cancel"
                            onClick={() => setIsEditing(false)}
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            ) : (
                <div className="details-content">
                    <div className="main-details">
                        <div className="details-section">
                            <h3>Space Overview</h3>
                            <p className="space-description">{space.description}</p>

                            <div className="key-details">
                                <div className="detail-item">
                                    <span className="detail-label">Price:</span>
                                    <span className="detail-value">{space.pricePerMonth} €/month</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Building:</span>
                                    <span className="detail-value">{space.building?.name || 'N/A'}</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Address:</span>
                                    <span className="detail-value">{space.address || 'N/A'}</span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Status:</span>
                                    <span className={`detail-value status ${space.available ? 'available' : 'rented'}`}>
                                        {space.available ? 'Available' : 'Rented'}
                                    </span>
                                </div>
                                <div className="detail-item">
                                    <span className="detail-label">Owner:</span>
                                    <span className="detail-value">{space.owner?.name || 'N/A'}</span>
                                </div>
                                {space.parking && (
                                    <div className="detail-item">
                                        <span className="detail-label">Parking:</span>
                                        <span className="detail-value">
                                            {space.parking.numberOfSpots} spots ({space.parking.pricePerSpot} €/spot)
                                        </span>
                                    </div>
                                )}
                            </div>
                        </div>

                        {renderSpecificDetails()}

                        <div className="details-section">
                            <h3>Amenities</h3>
                            {renderAmenities()}
                        </div>
                    </div>

                    <div className="details-sidebar">
                        <div className="action-card">
                            <h3>Actions</h3>
                            {canEdit && (
                                <>
                                    <button
                                        className="btn btn-edit"
                                        onClick={() => setIsEditing(true)}
                                    >
                                        Edit Space
                                    </button>
                                    <button
                                        className="btn btn-delete"
                                        onClick={handleDelete}
                                    >
                                        Delete Space
                                    </button>
                                </>
                            )}
                            {canRent && (
                                <button
                                    className="btn btn-rent"
                                    onClick={handleRent}
                                >
                                    Rent This Space
                                </button>
                            )}
                        </div>

                        <div className="contact-card">
                            <h3>Contact Information</h3>
                            {space.owner ? (
                                <div className="contact-details">
                                    <p><strong>Owner:</strong> {space.owner.name}</p>
                                    <p><strong>Email:</strong> {space.owner.email}</p>
                                    <p><strong>Phone:</strong> {space.owner.phone}</p>
                                    <p><strong>Company:</strong> {space.owner.companyName}</p>
                                </div>
                            ) : (
                                <p>Contact information not available</p>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default SpaceDetailsPage;