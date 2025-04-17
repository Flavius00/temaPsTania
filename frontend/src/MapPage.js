import React, { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './MapPage.css';
import 'leaflet/dist/leaflet.css';

// Fix Leaflet icon issue
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png'),
});

// Custom icons for different space types
const officeIcon = new L.Icon({
    iconUrl: 'https://cdn-icons-png.flaticon.com/512/1802/1802977.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});

const retailIcon = new L.Icon({
    iconUrl: 'https://cdn-icons-png.flaticon.com/512/3163/3163478.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});

const warehouseIcon = new L.Icon({
    iconUrl: 'https://cdn-icons-png.flaticon.com/512/2574/2574579.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
    popupAnchor: [0, -32]
});

function MapPage() {
    const [spaces, setSpaces] = useState([]);
    const [filteredSpaces, setFilteredSpaces] = useState([]);
    const [filter, setFilter] = useState('ALL');
    const [availableOnly, setAvailableOnly] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchSpaces = async () => {
            setIsLoading(true);
            try {
                const res = await axios.get('http://localhost:8080/spaces/getAll');
                setSpaces(res.data);
                setFilteredSpaces(res.data);
                console.log(res.data);
            } catch (err) {
                console.error('Error fetching spaces for map', err);
            } finally {
                setIsLoading(false);
            }
        };

        fetchSpaces();
    }, []);

    useEffect(() => {
        let filtered = spaces;

        if (filter !== 'ALL') {
            filtered = filtered.filter(space => space.spaceType === filter);
        }

        if (availableOnly) {
            filtered = filtered.filter(space => space.available);
        }

        setFilteredSpaces(filtered);
    }, [spaces, filter, availableOnly]);

    const getIcon = (spaceType) => {
        switch (spaceType) {
            case 'OFFICE':
                return officeIcon;
            case 'RETAIL':
                return retailIcon;
            case 'WAREHOUSE':
                return warehouseIcon;
            default:
                return new L.Icon.Default();
        }
    };

    const handleViewDetails = (spaceId) => {
        navigate(`/space-details/${spaceId}`);
    };

    return (
        <div className="map-container">
            <div className="map-header">
                <h2>Commercial Spaces Map</h2>
                <div className="map-filters">
                    <div className="filter-group">
                        <label>Space Type:</label>
                        <select value={filter} onChange={(e) => setFilter(e.target.value)}>
                            <option value="ALL">All Types</option>
                            <option value="OFFICE">Office</option>
                            <option value="RETAIL">Retail</option>
                            <option value="WAREHOUSE">Warehouse</option>
                        </select>
                    </div>
                    <div className="filter-checkbox">
                        <label>
                            <input
                                type="checkbox"
                                checked={availableOnly}
                                onChange={(e) => setAvailableOnly(e.target.checked)}
                            />
                            Show only available spaces
                        </label>
                    </div>
                </div>
            </div>

            <div className="map-legend">
                <div className="legend-item">
                    <img src="https://cdn-icons-png.flaticon.com/512/1802/1802977.png" alt="Office" width="20" />
                    <span>Office</span>
                </div>
                <div className="legend-item">
                    <img src="https://cdn-icons-png.flaticon.com/512/3163/3163478.png" alt="Retail" width="20" />
                    <span>Retail</span>
                </div>
                <div className="legend-item">
                    <img src="https://cdn-icons-png.flaticon.com/512/2574/2574579.png" alt="Warehouse" width="20" />
                    <span>Warehouse</span>
                </div>
            </div>

            {isLoading ? (
                <div className="loading-container">
                    <p>Loading map data...</p>
                </div>
            ) : (
                <MapContainer
                    center={[46.77, 23.59]}
                    zoom={6}
                    scrollWheelZoom={true}
                    style={{ height: 'calc(100vh - 180px)', width: '100%', borderRadius: '10px' }}
                >
                    <TileLayer
                        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />
                    {filteredSpaces.map((space) => {
                        if (!space.latitude || !space.longitude) return null;

                        return (
                            <Marker
                                key={space.id}
                                position={[space.latitude, space.longitude]}
                                icon={getIcon(space.spaceType)}
                            >
                                <Popup>
                                    <div className="map-popup">
                                        <h3>{space.name}</h3>
                                        <p className="popup-type">{space.spaceType || 'Unknown'}</p>
                                        <p>{space.description}</p>
                                        <div className="popup-details">
                                            <p><strong>Area:</strong> {space.area} m²</p>
                                            <p><strong>Price:</strong> {space.pricePerMonth} €/month</p>
                                            <p><strong>Status:</strong>
                                                <span className={space.available ? 'available' : 'rented'}>
                                                    {space.available ? ' Available' : ' Rented'}
                                                </span>
                                            </p>
                                        </div>
                                        <button
                                            className="popup-button"
                                            onClick={() => handleViewDetails(space.id)}
                                        >
                                            View Details
                                        </button>
                                    </div>
                                </Popup>
                            </Marker>
                        );
                    })}
                </MapContainer>
            )}

            <div className="map-stats">
                <p>Showing {filteredSpaces.length} spaces on the map</p>
                <p>Available spaces: {filteredSpaces.filter(s => s.available).length}</p>
            </div>
        </div>
    );
}

export default MapPage;
