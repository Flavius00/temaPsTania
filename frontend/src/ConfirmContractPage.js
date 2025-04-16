import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ConfirmContractPage.css';

function ConfirmContractPage() {
    const location = useLocation();
    const navigate = useNavigate();
    const { selectedSpace, renewalContract } = location.state || {};
    const [contractDuration, setContractDuration] = useState(12); // Default 12 months
    const [isRenewal, setIsRenewal] = useState(!!renewalContract);

    if (!selectedSpace) {
        return <p className="text-center mt-4">Missing space information.</p>;
    }

    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) {
        navigate('/login');
        return null;
    }

    const calculateEndDate = (startDate, months) => {
        const date = new Date(startDate);
        date.setMonth(date.getMonth() + months);
        return date.toISOString().split('T')[0];
    };

    const today = new Date().toISOString().split('T')[0];
    const endDate = calculateEndDate(today, contractDuration);
    const totalPrice = selectedSpace.pricePerMonth * contractDuration;
    const securityDeposit = selectedSpace.pricePerMonth * 2;

    const generateContractPDF = (contract) => {
        const doc = new jsPDF();

        // Add header
        doc.setFontSize(20);
        doc.text("Commercial Space Rental Contract", 20, 20);

        // Add contract details
        doc.setFontSize(12);
        doc.text(`Contract Number: ${contract.contractNumber}`, 20, 40);
        doc.text(`Date Created: ${new Date().toLocaleDateString()}`, 20, 50);

        // Parties involved
        doc.setFontSize(14);
        doc.text("Parties", 20, 70);
        doc.setFontSize(12);
        doc.text(`Landlord: ${selectedSpace.owner.name}`, 20, 80);
        doc.text(`Company: ${selectedSpace.owner.companyName || 'N/A'}`, 20, 90);
        doc.text(`Tenant: ${user.name}`, 20, 100);
        doc.text(`Company: ${user.companyName || 'N/A'}`, 20, 110);

        // Property details
        doc.setFontSize(14);
        doc.text("Property Details", 20, 130);
        doc.setFontSize(12);
        doc.text(`Space Name: ${selectedSpace.name}`, 20, 140);
        doc.text(`Address: ${selectedSpace.address}`, 20, 150);
        doc.text(`Space Type: ${selectedSpace.spaceType}`, 20, 160);
        doc.text(`Area: ${selectedSpace.area} m²`, 20, 170);

        // Contract terms
        doc.setFontSize(14);
        doc.text("Contract Terms", 20, 190);
        doc.setFontSize(12);
        doc.text(`Start Date: ${contract.startDate}`, 20, 200);
        doc.text(`End Date: ${contract.endDate}`, 20, 210);
        doc.text(`Monthly Rent: €${contract.monthlyRent}`, 20, 220);
        doc.text(`Security Deposit: €${contract.securityDeposit}`, 20, 230);
        doc.text(`Status: ${contract.status}`, 20, 240);

        // Return the document
        return doc;
    };

    const handleCreateContract = async () => {
        try {
            const contractData = {
                space: selectedSpace,
                tenant: {
                    id: user.id,
                    name: user.name,
                    email: user.email
                },
                startDate: today,
                endDate: endDate,
                monthlyRent: selectedSpace.pricePerMonth,
                securityDeposit: securityDeposit,
                status: "ACTIVE",
                isPaid: true,
                dateCreated: today
            };

            // Handle renewal case
            if (isRenewal && renewalContract) {
                contractData.contractNumber = `RENEWAL-${renewalContract.contractNumber || renewalContract.id}`;
                // Call the renew endpoint instead
                await axios.post(`http://localhost:8080/contracts/${renewalContract.id}/renew`, contractData);
            } else {
                // Normal contract creation
                const response = await axios.post('http://localhost:8080/contracts/create', contractData);
                contractData.contractNumber = response.data.contractNumber;
            }

            // Generate PDF
            const pdf = generateContractPDF(contractData);
            pdf.save(`Rental_Contract_${selectedSpace.name.replace(/\s+/g, '_')}.pdf`);

            // Show success message
            const toast = document.createElement('div');
            toast.innerText = "✅ Contract created successfully!";
            toast.style.position = 'fixed';
            toast.style.bottom = '20px';
            toast.style.right = '20px';
            toast.style.backgroundColor = '#4CAF50';
            toast.style.color = 'white';
            toast.style.padding = '10px 20px';
            toast.style.borderRadius = '5px';
            toast.style.zIndex = '1000';
            document.body.appendChild(toast);
            setTimeout(() => toast.remove(), 3000);

            navigate('/contracts');
        } catch (error) {
            console.error("Contract creation failed", error);
            alert("Failed to create contract. Please try again.");
        }
    };

    const handleCancel = () => {
        navigate('/spaces');
    };

    return (
        <div className="confirm-container">
            <div className="confirm-card">
                <img src={buildingIcon} alt="Building Icon" className="building-icon" />
                <h3>{isRenewal ? 'Renew Rental Contract' : 'Confirm Rental Contract'}</h3>

                <div className="contract-details">
                    <div className="detail-section">
                        <h4>Space Details</h4>
                        <p><strong>Name:</strong> {selectedSpace.name}</p>
                        <p><strong>Type:</strong> {selectedSpace.spaceType}</p>
                        <p><strong>Area:</strong> {selectedSpace.area} m²</p>
                        <p><strong>Location:</strong> {selectedSpace.building?.name || selectedSpace.address}</p>
                    </div>

                    <div className="detail-section">
                        <h4>Contract Terms</h4>
                        <p><strong>Monthly Rent:</strong> €{selectedSpace.pricePerMonth}</p>
                        <p><strong>Security Deposit:</strong> €{securityDeposit}</p>
                        <p><strong>Start Date:</strong> {today}</p>

                        <div className="duration-selector">
                            <label htmlFor="duration">Contract Duration:</label>
                            <select
                                id="duration"
                                value={contractDuration}
                                onChange={(e) => setContractDuration(parseInt(e.target.value))}
                            >
                                <option value="6">6 months</option>
                                <option value="12">12 months</option>
                                <option value="24">24 months</option>
                                <option value="36">36 months</option>
                            </select>
                        </div>

                        <p><strong>End Date:</strong> {endDate}</p>
                    </div>
                </div>

                <div className="price-summary">
                    <h4>Payment Summary</h4>
                    <div className="summary-row">
                        <span>Monthly Rent:</span>
                        <span>€{selectedSpace.pricePerMonth}</span>
                    </div>
                    <div className="summary-row">
                        <span>Contract Duration:</span>
                        <span>{contractDuration} months</span>
                    </div>
                    <div className="summary-row">
                        <span>Security Deposit:</span>
                        <span>€{securityDeposit}</span>
                    </div>
                    <div className="summary-row total">
                        <span>Total First Payment:</span>
                        <span>€{selectedSpace.pricePerMonth + securityDeposit}</span>
                    </div>
                    <div className="summary-row contract-total">
                        <span>Contract Total Value:</span>
                        <span>€{totalPrice}</span>
                    </div>
                </div>

                <div className="terms-agreement">
                    <p className="terms-text">
                        By confirming this contract, you agree to the rental terms and conditions.
                        A PDF of the contract will be generated for your records.
                    </p>
                </div>

                <div className="btn-group">
                    <button className="btn btn-confirm" onClick={handleCreateContract}>
                        {isRenewal ? 'Renew Contract' : 'Confirm Contract'}
                    </button>
                    <button className="btn btn-cancel" onClick={handleCancel}>Cancel</button>
                </div>
            </div>
        </div>
    );
}

export default ConfirmContractPage;