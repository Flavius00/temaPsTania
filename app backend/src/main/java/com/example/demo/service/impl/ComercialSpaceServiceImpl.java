// app backend/src/main/java/com/example/demo/service/impl/ComercialSpaceServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.ComercialSpace;
import com.example.demo.repository.ComercialSpaceRepository;
import com.example.demo.service.ComercialSpaceService;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComercialSpaceServiceImpl implements ComercialSpaceService {
    private final ComercialSpaceRepository spaceRepository;
    private final NotificationService notificationService;

    @Autowired
    public ComercialSpaceServiceImpl(ComercialSpaceRepository spaceRepository,
                                     NotificationService notificationService) {
        this.spaceRepository = spaceRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ComercialSpace addSpace(ComercialSpace space) {
        // Validation
        if (space == null) {
            throw new BadRequestException("Space cannot be null");
        }
        if (space.getName() == null || space.getName().isEmpty()) {
            throw new BadRequestException("Space name is required");
        }
        if (space.getArea() == null || space.getArea() <= 0) {
            throw new BadRequestException("Space area must be a positive value");
        }
        if (space.getPricePerMonth() == null || space.getPricePerMonth() <= 0) {
            throw new BadRequestException("Space price must be a positive value");
        }
        if (space.getSpaceType() == null || space.getSpaceType().isEmpty()) {
            throw new BadRequestException("Space type is required");
        }

        // Set next ID in sequence
        space.setId((long) (spaceRepository.findAll().size() + 1));

        // Save space
        ComercialSpace savedSpace = spaceRepository.save(space);

        // Send notification about new space
        notificationService.notifyNewSpace(savedSpace);

        return savedSpace;
    }

    @Override
    public List<ComercialSpace> getAllSpaces() {
        return spaceRepository.findAll();
    }

    @Override
    public ComercialSpace getSpaceById(Long id) {
        ComercialSpace space = spaceRepository.findById(id);
        if (space == null) {
            throw new ResourceNotFoundException("Commercial space not found with ID: " + id);
        }
        return space;
    }

    @Override
    public ComercialSpace updateSpace(ComercialSpace space) {
        // Validation
        if (space == null) {
            throw new BadRequestException("Space cannot be null");
        }
        if (space.getId() == null) {
            throw new BadRequestException("Space ID is required for update");
        }

        // Check if space exists
        ComercialSpace existingSpace = spaceRepository.findById(space.getId());
        if (existingSpace == null) {
            throw new ResourceNotFoundException("Commercial space not found with ID: " + space.getId());
        }

        // Check if status has changed for notification
        boolean statusChanged = existingSpace.getAvailable() != space.getAvailable();

        // Update space
        ComercialSpace updatedSpace = spaceRepository.update(space);

        // Send notification if status changed
        if (statusChanged) {
            notificationService.notifySpaceStatusChange(updatedSpace);
        }

        return updatedSpace;
    }

    @Override
    public void deleteSpace(Long id) {
        // Check if space exists
        ComercialSpace existingSpace = spaceRepository.findById(id);
        if (existingSpace == null) {
            throw new ResourceNotFoundException("Commercial space not found with ID: " + id);
        }

        // Delete space
        spaceRepository.deleteById(id);

        // Send notification about space deletion
        existingSpace.setAvailable(false);
        notificationService.notifySpaceStatusChange(existingSpace);
    }

    @Override
    public List<ComercialSpace> getAvailableSpaces() {
        return spaceRepository.findAll().stream()
                .filter(ComercialSpace::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComercialSpace> getSpacesByType(String spaceType) {
        if (spaceType == null || spaceType.isEmpty()) {
            throw new BadRequestException("Space type cannot be null or empty");
        }

        return spaceRepository.findAll().stream()
                .filter(space -> spaceType.equalsIgnoreCase(space.getSpaceType()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ComercialSpace> getSpacesByOwner(Long ownerId) {
        if (ownerId == null) {
            throw new BadRequestException("Owner ID cannot be null");
        }

        return spaceRepository.findAll().stream()
                .filter(space -> space.getOwner() != null && space.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ComercialSpace> getSpacesByBuilding(Long buildingId) {
        if (buildingId == null) {
            throw new BadRequestException("Building ID cannot be null");
        }

        return spaceRepository.findAll().stream()
                .filter(space -> space.getBuilding() != null && space.getBuilding().getId().equals(buildingId))
                .collect(Collectors.toList());
    }
}