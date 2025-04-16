import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './ContractsPage.css';

function ContractsPage() {
    const [contracts, setContracts] = useState([]);
    const [filteredContracts, setFilteredContracts] = useState([]);
    const [user, setUser] = useState(null);

    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [contractStatuses, setContractStatuses] = useState([]);
    const [sortOption, setSortOption] = useState('');
    const [spaceTypes, setSpaceTypes] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {
        const storedUser = JSON.parse(localStorage.getItem('user'));
        setUser(storedUser);

        const fetchContracts = async () => {
            try {
                let response;

                // Get contracts based on user role
                if (storedUser?.role === 'TENANT') {
                    response = await axios.get(`http://localhost:8080/contracts/tenant/${storedUser.id}`);
                } else if (storedUser?.role === 'OWNER') {
                    // First get owner's spaces
                    const spacesResponse = await axios.get(`http://localhost:8080/spaces/owner/${storedUser.id}`);
                    const spaceIds = spacesResponse.data.map(space => space.id);

                    // Then get contracts for those spaces
                    const contractPromises = spaceIds.map(spaceId =>
                        axios.get(`http://localhost:8080/contracts/space/${spaceId}`)
                    );

                    const contractResponses = await Promise.all(contractPromises);
                    const allContracts = contractResponses.flatMap(res => res.data);

                    // Set as response for consistent handling below
                    response = { data: allContracts };
                } else {
                    // Admin gets all contracts
                    response = await axios.get('http://localhost:8080/contracts');
                }

                setContracts(response.data);
                setFilteredContracts(response.data);
            } catch (error) {
                console.error('Error fetching contracts:', error);
            }
        };

        fetchContracts();
    }, []);

    const handleViewDetails = (contract) => {
        navigate(`/contract-details/${contract.id}`, { state: { contractData: contract } });
    };

    const handleFilter = () => {
        let filtered = contracts.filter(contract => {
            const startDateObj = contract.startDate ? new Date(contract.startDate) : null;
            const endDateObj = contract.endDate ? new Date(contract.endDate) : null;
            const filterStartDateObj = startDate ? new Date(startDate) : null;
            const filterEndDateObj = endDate ? new Date(endDate) : null;

            const matchesStartDate = !filterStartDateObj || (startDateObj && startDateObj >= filterStartDateObj);
            const matchesEndDate = !filterEndDateObj || (endDateObj && endDateObj <= filterEndDateObj);

            const matchesStatus = contractStatuses.length === 0 ||
                (contract.status && contractStatuses.includes(contract.status));

            const matchesSpaceType = spaceTypes.length === 0 ||
                (contract.space?.spaceType && spaceTypes.includes(contract.space.spaceType));

            return matchesStartDate && matchesEndDate && matchesStatus && matchesSpaceType;
        });

        // Apply sorting
        if (sortOption === 'dateAsc') {
            filtered.sort((a, b) => new Date(a.startDate) - new Date(b.startDate));
        } else if (sortOption === 'dateDesc') {
            filtered.sort((a, b) => new Date(b.startDate) - new Date(a.startDate));
        } else if (sortOption === 'priceAsc') {
            filtered.sort((a, b) => a.monthlyRent - b.monthlyRent);
        } else if (sortOption === 'priceDesc') {
            filtered.sort((a, b) => b.monthlyRent - a.monthlyRent);
        }

        setFilteredContracts(filtered);
    };

    const handleReset = () => {
        setStartDate('');
        setEndDate('');
        setContractStatuses([]);
        setSpaceTypes([]);
        setSortOption('');
        setFilteredContracts(contracts);
    };

    const handleRenewContract = (contract) => {
        navigate('/payment/confirm', {
            state: {
                selectedSpace: contract.space,
                renewalContract: contract
            }
        });
    };

    const handleTerminateContract = async (contractId) => {
        if (window.confirm('Are you sure you want to terminate this contract? This action cannot be undone.')) {
            try {
                await axios.delete(`http://localhost:8080/contracts/${contractId}`);

                // Update contracts list
                setContracts(prevContracts => {
                    const updatedContracts = prevContracts.map(c =>
                        c.id === contractId ? { ...c, status: 'TERMINATED' } : c
                    );
                    setFilteredContracts(updatedContracts);
                    return updatedContracts;
                });

                alert('Contract terminated successfully');
            } catch (error) {
                console.error('Error terminating contract:', error);
                alert('Failed to terminate contract');
            }
        }
    };

    const statusOptions = ['ACTIVE', 'PENDING', 'EXPIRED', 'TERMINATED'];
    const spaceTypeOptions = ['OFFICE', 'RETAIL', 'WAREHOUSE'];

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    };

    return (
        <div className="contracts-container">
            <div className="contracts-header">
                <h2>Rental Contracts</h2>
            </div>

            <div className="filter-section">
                <div className="filter-group">
                    <label>From Date:</label>
                    <input
                        type="date"
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>To Date:</label>
                    <input
                        type="date"
                        value={endDate}
                        onChange={(e) => setEndDate(e.target.value)}
                    />
                </div>
                <div className="filter-group">
                    <label>Sort by:</label>
                    <select value={sortOption} onChange={(e) => setSortOption(e.target.value)}>
                        <option value="">None</option>
                        <option value="dateAsc">Start Date: Oldest First</option>
                        <option value="dateDesc">Start Date: Newest First</option>
                        <option value="priceAsc">Rent: Low to High</option>
                        <option value="priceDesc">Rent: High to Low</option>
                    </select>
                </div>

                <div className="filter-expanded">
                    <div className="filter-section-inner">
                        <div className="filter-column">
                            <label>Filter by Status:</label>
                            <div className="checkbox-list">
                                {statusOptions.map(status => (
                                    <label key={status} className="checkbox-item">
                                        <input
                                            type="checkbox"
                                            value={status}
                                            checked={contractStatuses.includes(status)}
                                            onChange={(e) => {
                                                if (e.target.checked) {
                                                    setContractStatuses(prev => [...prev, status]);
                                                } else {
                                                    setContractStatuses(prev =>
                                                        prev.filter(item => item !== status)
                                                    );
                                                }
                                            }}
                                        /> {status.charAt(0) + status.slice(1).toLowerCase()}
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
                                                    setSpaceTypes(prev =>
                                                        prev.filter(item => item !== type)
                                                    );
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

            {filteredContracts.length === 0 ? (
                <p className="no-contracts-message">No contracts match your search criteria.</p>
            ) : (
                <div className="contracts-table-container">
                    <table className="contracts-table">
                        <thead>
                        <tr>
                            <th>Contract #</th>
                            <th>Space</th>
                            {user?.role !== 'TENANT' && <th>Tenant</th>}
                            {user?.role !== 'OWNER' && <th>Owner</th>}
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Monthly Rent</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredContracts.map(contract => (
                            <tr key={contract.id} className={`status-${contract.status?.toLowerCase()}`}>
                                <td>{contract.contractNumber || `CONT-${contract.id}`}</td>
                                <td>{contract.space?.name || 'N/A'}</td>
                                {user?.role !== 'TENANT' && (
                                    <td>{contract.tenant?.name || 'N/A'}</td>
                                )}
                                {user?.role !== 'OWNER' && (
                                    <td>{contract.space?.owner?.name || 'N/A'}</td>
                                )}
                                <td>{formatDate(contract.startDate)}</td>
                                <td>{formatDate(contract.endDate)}</td>
                                <td>{contract.monthlyRent} €</td>
                                <td>
                                        <span className={`status-badge ${contract.status?.toLowerCase()}`}>
                                            {contract.status || 'N/A'}
                                        </span>
                                </td>
                                <td className="action-buttons">
                                    <button
                                        className="btn-action btn-view"
                                        onClick={() => handleViewDetails(contract)}
                                    >
                                        View
                                    </button>

                                    {/* Tenant can renew contracts that are active or almost expired */}
                                    {user?.role === 'TENANT' &&
                                        (contract.status === 'ACTIVE' || contract.status === 'EXPIRED') && (
                                            <button
                                                className="btn-action btn-renew"
                                                onClick={() => handleRenewContract(contract)}
                                            >
                                                Renew
                                            </button>
                                        )}

                                    {/* Owner can terminate active contracts */}
                                    {user?.role === 'OWNER' && contract.status === 'ACTIVE' && (
                                        <button
                                            className="btn-action btn-terminate"
                                            onClick={() => handleTerminateContract(contract.id)}
                                        >
                                            Terminate
                                        </button>
                                    )}

                                    {/* Admin can manage all contracts */}
                                    {user?.role === 'ADMIN' && contract.status === 'ACTIVE' && (
                                        <button
                                            className="btn-action btn-terminate"
                                            onClick={() => handleTerminateContract(contract.id)}
                                        >
                                            Terminate
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

export default ContractsPage;