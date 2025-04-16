import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './SpacesPage.css';

function SpacesPage() {
    const [spaces, setSpaces] = useState([]);
    const [filteredSpaces, setFilteredSpaces] = useState([]);
    const [user, setUser] = useState(null);

    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [minArea, setMinArea] = useState('');
    const [maxArea, setMaxArea] = useState('');
    const [locations, setLocations] = useState([]);
    const [spaceTypes, setSpaceTypes] = useState([]);
    const [onlyAvailable, setOnlyAvailable] = useState(true);
    const [sortOption, setSortOption] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        setUser(storedUser);

        const fetchSpaces = async () => {
            try {
                const response = await axios.get('http://localhost:8080/spaces');
                setSpaces(response.data);

                // For owners, filter to only show their spaces
                if (storedUser?.role === 'OWNER') {
                    const ownerSpaces = response.data.filter(space =>
                        space.owner && space.owner.id === storedUser.id
                    );
                    setFilteredSpaces(ownerSpaces);
                } else {
                    // For tenants, show only available spaces by default
                    const availableSpaces = storedUser?.role === 'TENANT'
                        ? response.data.filter(space => space.available)
                        : response.data;
                    setFilteredSpaces(availableSpaces);
                }
            } catch (error) {
                console.error('Error fetching spaces:', error);
            }
        };

        fetchSpaces();
    }, []);

    const handleViewDetails = (space) => {
        navigate(`/space-details/${space.id}`, { state: { spaceData: space } });
    };

    const handleFilter = () => {
        let filtered = spaces;

        // For owners, only show their spaces
        if (user?.role === 'OWNER') {
            filtered = filtered.filter(space => space.owner && space.owner.id === user.id);
        }

        // Apply other filters
        filtered = filtered.filter(space => {
            const price = space.pricePerMonth;
            const area = space.area;
            const location = space.building?.name;
            const type = space.spaceType;

            const matchesPrice = (!minPrice || price >= minPrice) && (!maxPrice || price <= maxPrice);
            const matchesArea = (!minArea || area >= minArea) && (!maxArea || area <= maxArea);
            const matchesLocation = locations.length === 0 || (location && locations.includes(location));
            const matchesType = spaceTypes.length === 0 || (type && spaceTypes.includes(type));
            const matchesAvailability = !onlyAvailable || space.available;

            return matchesPrice && matchesArea && matchesLocation && matchesType && matchesAvailability;
        });

        // Apply sorting
        if (sortOption === 'priceAsc') {
            filtered.sort((a, b) => a.pricePerMonth - b.pricePerMonth);
        } else if (sortOption === 'priceDesc') {
            filtered.sort((a, b) => b.pricePerMonth - a.pricePerMonth);
        } else if (sortOption === 'areaAsc') {
            filtered.sort((a, b) => a.area - b.area);
        } else if (sortOption === 'areaDesc') {
            filtered.sort((a, b) => b.area - a.area);
        }

        setFilteredSpaces(filtered);
    };

    const handleReset = () => {
        setMinPrice('');
        setMaxPrice('');
        setMinArea('');
        setMaxArea('');
        setLocations([]);
        setSpaceTypes([]);
        setOnlyAvailable(user?.role === 'TENANT');
        setSortOption('');

        // Reset to original spaces list based on user role
        if (user?.role === 'OWNER') {
            setFilteredSpaces(spaces.filter(space => space.owner && space.owner.id === user.id));
        } else if (user?.role === 'TENANT') {
            setFilteredSpaces(spaces.filter(space => space.available));
        } else {
            setFilteredSpaces(spaces);
        }
    };

    const handleCreateSpace = () => {
        // Navigate to create space form
        navigate('/spaces/create');
    };

    const uniqueLocations = [...new Set(spaces.map(s => s.building?.name).filter(Boolean))];
    const spaceTypeOptions = ['OFFICE', 'RETAIL', 'WAREHOUSE'];

    return (
        <div className="spaces-container">
            <div className="spaces-header">
                <h2>Commercial Spaces</h2>
                {user?.role === 'OWNER' && (
                    <button className="btn btn-create" onClick={handleCreateSpace}>
                        + Add New Space
                    </button>
                )}
            </div>

            <div className="filter-section">
                <div className="filter-group">
                    <label>Min Price (€/month):</label>
                    <input
                        type="number"
                        value={minPrice}
                        onChange={(e) => setMinPrice(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>Max Price (€/month):</label>
                    <input
                        type="number"
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>Min Area (m²):</label>
                    <input
                        type="number"
                        value={minArea}
                        onChange={(e) => setMinArea(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>Max Area (m²):</label>
                    <input
                        type="number"
                        value={maxArea}
                        onChange={(e) => setMaxArea(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>Sort by:</label>
                    <select value={sortOption} onChange={(e) => setSortOption(e.target.value)}>
                        <option value="">None</option>
                        <option value="priceAsc">Price: Low to High</option>
                        <option value="priceDesc">Price: High to Low</option>
                        <option value="areaAsc">Area: Small to Large</option>
                        <option value="areaDesc">Area: Large to Small</option>
                    </select>
                </div>
                <div className="filter-group checkbox-group">
                    <label>
                        <input
                            type="checkbox"
                            checked={onlyAvailable}
                            onChange={(e) => setOnlyAvailable(e.target.checked)}
                        /> Show only available
                    </label>
                </div>

                <div className="filter-expanded">
                    <div className="filter-section-inner">
                        <div className="filter-column">
                            <label>Filter by Location:</label>
                            <div className="checkbox-list">
                                {uniqueLocations.map(loc => (
                                    <label key={loc} className="checkbox-item">
                                        <input
                                            type="checkbox"
                                            value={loc}
                                            checked={locations.includes(loc)}
                                            onChange={(e) => {
                                                if (e.target.checked) {
                                                    setLocations(prev => [...prev, loc]);
                                                } else {
                                                    setLocations(prev => prev.filter(item => item !== loc));
                                                }
                                            }}
                                        /> {loc}
                                    </label>
                                ))}
                            </div>
                        </div>

                        <div className="filter-column">
                            <label>Filter by Space Type:</label>
                            <div className="checkbox-list">
                                {spaceTypeOptions.map(type => (
                                    <label key={type} className="checkbox-item">
                                        <input
                                            type="checkbox"
                                            value={type}
                                            checked={spaceTypes.includes(type)}
                                            onChange={(e) => {
                                                if (e.target.checked) {
                                                    setSpaceTypes(prev => [...prev, type]);
                                                } else {
                                                    setSpaceTypes(prev => prev.filter(item => item !== type));
                                                }
                                            }}
                                        /> {type.charAt(0) + type.slice(1).toLowerCase()}
                                    </label>
                                ))}
                            </div>
                        </div>
                    </div>

                    <div className="button-group">
                        <button className="btn" onClick={handleFilter}>Apply Filters</button>
                        <button className="btn btn-secondary" onClick={handleReset}>Reset Filters</button>
                    </div>
                </div>
            </div>

            {filteredSpaces.length === 0 ? (
                <p className="no-spaces-message">No spaces match your search criteria.</p>
            ) : (
                <div className="spaces-grid">
                    {filteredSpaces.map((space) => (
                        <div key={space.id} className="space-card">
                            <div className="space-card-header">
                                <h3>{space.name}</h3>
                                <span className={`space-badge ${space.spaceType.toLowerCase()}`}>
                                    {space.spaceType}
                                </span>
                            </div>
                            <div className="space-card-body">
                                <p className="space-description">{space.description}</p>
                                <div className="space-details">
                                    <div className="detail-item">
                                        <span className="detail-label">Area:</span>
                                        <span className="detail-value">{space.area} m²</span>
                                    </div>
                                    <div className="detail-item">
                                        <span className="detail-label">Price:</span>
                                        <span className="detail-value">{space.pricePerMonth} €/month</span>
                                    </div>
                                    <div className="detail-item">
                                        <span className="detail-label">Location:</span>
                                        <span className="detail-value">{space.building?.name || 'N/A'}</span>
                                    </div>
                                    <div className="detail-item">
                                        <span className="detail-label">Status:</span>
                                        <span className={`detail-value ${space.available ? 'available' : 'rented'}`}>
                                            {space.available ? 'Available' : 'Rented'}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div className="space-card-footer">
                                <button
                                    className="btn btn-details"
                                    onClick={() => handleViewDetails(space)}
                                >
                                    View Details
                                </button>
                                {user?.role === 'TENANT' && space.available && (
                                    <button
                                        className="btn btn-action"
                                        onClick={() => navigate('/payment', { state: { selectedSpace: space } })}
                                    >
                                        Rent Now
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default SpacesPage;