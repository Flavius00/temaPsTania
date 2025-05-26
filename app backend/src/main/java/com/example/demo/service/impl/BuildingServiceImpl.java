// app backend/src/main/java/com/example/demo/service/impl/BuildingServiceImpl.java
package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Building;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {
    private final BuildingRepository buildingRepository;

    @Autowired
    public BuildingServiceImpl(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Override
    public Building addBuilding(Building building) {
        // Validation
        if (building == null) {
            throw new BadRequestException("Building cannot be null");
        }
        if (building.getName() == null || building.getName().isEmpty()) {
            throw new BadRequestException("Building name is required");
        }
        if (building.getAddress() == null || building.getAddress().isEmpty()) {
            throw new BadRequestException("Building address is required");
        }

        return buildingRepository.save(building);
    }

    @Override
    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }

    @Override
    public Building getBuildingById(Long id) {
        if (id == null) {
            throw new BadRequestException("Building ID cannot be null");
        }

        Building building = buildingRepository.findById(id).orElse(null);
        if (building == null) {
            throw new ResourceNotFoundException("Building not found with ID: " + id);
        }

        return building;
    }

    @Override
    public Building updateBuilding(Building building) {
        // Validation
        if (building == null) {
            throw new BadRequestException("Building cannot be null");
        }
        if (building.getId() == null) {
            throw new BadRequestException("Building ID is required for update");
        }
        if (building.getName() == null || building.getName().isEmpty()) {
            throw new BadRequestException("Building name cannot be empty");
        }
        if (building.getAddress() == null || building.getAddress().isEmpty()) {
            throw new BadRequestException("Building address cannot be empty");
        }

        // Verify building exists
        if (!buildingRepository.findById(building.getId()).isPresent()) {
            throw new ResourceNotFoundException("Building not found with ID: " + building.getId());
        }

        return buildingRepository.save(building);
    }

    @Override
    public void deleteBuilding(Long id) {
        if (id == null) {
            throw new BadRequestException("Building ID cannot be null");
        }

        // Verify building exists
        if (!buildingRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Building not found with ID: " + id);
        }

        buildingRepository.deleteById(id);
    }
}