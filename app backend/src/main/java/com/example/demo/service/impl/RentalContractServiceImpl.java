// app backend/src/main/java/com/example/demo/service/impl/RentalContractServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.ComercialSpace;
import com.example.demo.model.RentalContract;
import com.example.demo.repository.RentalContractRepository;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.service.NotificationService;
import com.example.demo.service.RentalContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalContractServiceImpl implements RentalContractService {
    private final RentalContractRepository contractRepository;
    private final ComercialSpaceRepository spaceRepository;
    private final NotificationService notificationService;

    @Autowired
    public RentalContractServiceImpl(RentalContractRepository contractRepository,
                                     ComercialSpaceRepository spaceRepository,
                                     NotificationService notificationService) {
        this.contractRepository = contractRepository;
        this.spaceRepository = spaceRepository;
        this.notificationService = notificationService;
    }

    @Override
    public RentalContract createContract(RentalContract contract) {
        // Validation
        if (contract == null) {
            throw new BadRequestException("Contract cannot be null");
        }
        if (contract.getSpace() == null) {
            throw new BadRequestException("Space information is required for contract");
        }
        if (contract.getTenant() == null) {
            throw new BadRequestException("Tenant information is required for contract");
        }
        if (contract.getStartDate() == null || contract.getEndDate() == null) {
            throw new BadRequestException("Contract start and end dates are required");
        }
        if (contract.getEndDate().isBefore(contract.getStartDate())) {
            throw new BadRequestException("Contract end date cannot be before start date");
        }
        if (contract.getMonthlyRent() == null || contract.getMonthlyRent() <= 0) {
            throw new BadRequestException("Monthly rent must be a positive value");
        }

        // Verify space exists and is available
        ComercialSpace space = contract.getSpace();
        ComercialSpace existingSpace = spaceRepository.findById(space.getId());
        if (existingSpace == null) {
            throw new ResourceNotFoundException("Space not found with ID: " + space.getId());
        }
        if (!existingSpace.getAvailable()) {
            throw new BadRequestException("The selected space is not available for rent");
        }

        // Generate a contract number
        contract.setContractNumber("RENT-" + System.currentTimeMillis());

        // Set the creation date to now
        contract.setDateCreated(LocalDate.now());

        // Set default status to ACTIVE if not specified
        if (contract.getStatus() == null) {
            contract.setStatus(RentalContract.ContractStatus.valueOf("ACTIVE"));
        }

        // Mark the space as unavailable
        existingSpace.setAvailable(false);
        spaceRepository.update(existingSpace);

        // Save contract
        RentalContract savedContract = contractRepository.save(contract);

        // Send notification to space owner
        notificationService.notifyOwnerAboutNewContract(savedContract);

        return savedContract;
    }

    @Override
    public List<RentalContract> getAllContracts() {
        return contractRepository.findAll();
    }

    @Override
    public RentalContract getContractById(Long id) {
        RentalContract contract = contractRepository.findById(id);
        if (contract == null) {
            throw new ResourceNotFoundException("Contract not found with ID: " + id);
        }
        return contract;
    }

    @Override
    public RentalContract updateContract(RentalContract contract) {
        // Validation
        if (contract == null) {
            throw new BadRequestException("Contract cannot be null");
        }
        if (contract.getId() == null) {
            throw new BadRequestException("Contract ID is required for update");
        }

        // Check if contract exists
        RentalContract existingContract = contractRepository.findById(contract.getId());
        if (existingContract == null) {
            throw new ResourceNotFoundException("Contract not found with ID: " + contract.getId());
        }

        // Check if status has changed for notification
        boolean statusChanged = !existingContract.getStatus().equals(contract.getStatus());

        // Update contract
        RentalContract updatedContract = contractRepository.update(contract);

        // Send notification if status changed
        if (statusChanged) {
            notificationService.notifyTenantAboutContractChange(updatedContract);
        }

        return updatedContract;
    }

    @Override
    public void terminateContract(Long id) {
        // Check if contract exists
        RentalContract contract = contractRepository.findById(id);
        if (contract == null) {
            throw new ResourceNotFoundException("Contract not found with ID: " + id);
        }

        // Update contract status
        contract.setStatus("TERMINATED");
        contractRepository.update(contract);

        // Make the space available again
        ComercialSpace space = contract.getSpace();
        if (space != null) {
            space.setAvailable(true);
            spaceRepository.update(space);

            // Send notification about space availability
            notificationService.notifySpaceStatusChange(space);
        }

        // Send notification to tenant about contract termination
        notificationService.notifyTenantAboutContractChange(contract);
    }

    @Override
    public List<RentalContract> getContractsByTenant(Long tenantId) {
        if (tenantId == null) {
            throw new BadRequestException("Tenant ID cannot be null");
        }

        return contractRepository.findAll().stream()
                .filter(contract -> contract.getTenant() != null
                        && contract.getTenant().getId().equals(tenantId))
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalContract> getContractsByOwner(Long ownerId) {
        if (ownerId == null) {
            throw new BadRequestException("Owner ID cannot be null");
        }

        return contractRepository.findAll().stream()
                .filter(contract -> contract.getSpace().getOwner() != null
                        && contract.getSpace().getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalContract> getContractsBySpace(Long spaceId) {
        if (spaceId == null) {
            throw new BadRequestException("Space ID cannot be null");
        }

        return contractRepository.findAll().stream()
                .filter(contract -> contract.getSpace() != null
                        && contract.getSpace().getId().equals(spaceId))
                .collect(Collectors.toList());
    }

    @Override
    public List<RentalContract> getContractsByStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new BadRequestException("Contract status cannot be null or empty");
        }

        return contractRepository.findAll().stream()
                .filter(contract -> status.equalsIgnoreCase(contract.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public RentalContract renewContract(Long contractId, RentalContract renewalDetails) {
        // Check if contract exists
        RentalContract existingContract = contractRepository.findById(contractId);
        if (existingContract == null) {
            throw new ResourceNotFoundException("Contract not found with ID: " + contractId);
        }

        // Validate renewal details
        if (renewalDetails.getEndDate() == null) {
            throw new BadRequestException("New end date is required for contract renewal");
        }
        if (renewalDetails.getEndDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("New end date cannot be in the past");
        }

        try {
            // Create a new contract based on the existing one with new dates
            RentalContract newContract = new RentalContract();
            newContract.setTenant(existingContract.getTenant());
            newContract.setSpace(existingContract.getSpace());
            newContract.setStartDate(renewalDetails.getStartDate() != null ?
                    renewalDetails.getStartDate() : LocalDate.now());
            newContract.setEndDate(renewalDetails.getEndDate());
            newContract.setMonthlyRent(renewalDetails.getMonthlyRent() != null ?
                    renewalDetails.getMonthlyRent() : existingContract.getMonthlyRent());
            newContract.setSecurityDeposit(renewalDetails.getSecurityDeposit() != null ?
                    renewalDetails.getSecurityDeposit() : existingContract.getSecurityDeposit());
            newContract.setStatus("ACTIVE");
            newContract.setIsPaid(false);
            newContract.setDateCreated(LocalDate.now());
            newContract.setContractNumber("RENEWAL-" + existingContract.getContractNumber());

            // Mark old contract as expired
            existingContract.setStatus("EXPIRED");
            contractRepository.update(existingContract);

            // Save new contract
            RentalContract renewedContract = contractRepository.save(newContract);

            // Send notifications
            notificationService.notifyTenantAboutContractChange(renewedContract);
            notificationService.notifyOwnerAboutNewContract(renewedContract);

            return renewedContract;
        } catch (Exception e) {
            throw new BusinessException("Failed to renew contract: " + e.getMessage(), e);
        }
    }