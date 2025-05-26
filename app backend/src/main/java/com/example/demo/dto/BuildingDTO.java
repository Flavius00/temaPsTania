package com.example.demo.dto;

import com.example.demo.entity.Building;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pentru entitatea Building.
 *
 * Acest DTO este folosit pentru transferul datelor despre clădiri
 * între diferitele layere ale aplicației și către/de la client.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar transferul datelor despre clădiri
 * - Open/Closed: poate fi extins fără modificarea codului existent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildingDTO {

    private Long id;

    private String name;

    private String address;

    private Integer totalFloors;

    private Integer yearBuilt;

    private Double latitude;

    private Double longitude;

    private String description;

    private Double totalArea;

    private Integer parkingSpots;

    private Boolean elevatorAvailable;

    private Boolean accessibilityFeatures;

    private Boolean securitySystem;

    private Boolean airConditioning;

    private Building.BuildingType buildingType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Informații calculate sau agregate
    private Integer totalSpaces;

    private Integer availableSpaces;

    private Integer occupiedSpaces;

    private Double occupancyRate;

    private Double averageRentPerSpace;

    private Double totalRevenue;

    private List<ComercialSpaceDTO.SummaryDTO> spaces;

    /**
     * Constructor pentru crearea unui BuildingDTO cu datele de bază.
     *
     * @param name numele clădirii
     * @param address adresa clădirii
     * @param totalFloors numărul de etaje
     * @param yearBuilt anul construcției
     */
    public BuildingDTO(String name, String address, Integer totalFloors, Integer yearBuilt) {
        this.name = name;
        this.address = address;
        this.totalFloors = totalFloors;
        this.yearBuilt = yearBuilt;
    }

    /**
     * Verifică dacă clădirea are coordonate geografice.
     *
     * @return true dacă are coordonate
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    /**
     * Verifică dacă clădirea are spații disponibile.
     *
     * @return true dacă are spații disponibile
     */
    public boolean hasAvailableSpaces() {
        return availableSpaces != null && availableSpaces > 0;
    }

    /**
     * Calculează vârsta clădirii.
     *
     * @return vârsta în ani
     */
    public Integer getAge() {
        if (yearBuilt == null) {
            return null;
        }
        return java.time.Year.now().getValue() - yearBuilt;
    }

    /**
     * Calculează rata de ocupare.
     *
     * @return rata de ocupare (0-1)
     */
    public Double getCalculatedOccupancyRate() {
        if (totalSpaces == null || totalSpaces == 0) {
            return 0.0;
        }
        int occupied = occupiedSpaces != null ? occupiedSpaces : 0;
        return (double) occupied / totalSpaces;
    }

    /**
     * Verifică dacă clădirea este modernă (construită după 2000).
     *
     * @return true dacă este modernă
     */
    public boolean isModern() {
        return yearBuilt != null && yearBuilt >= 2000;
    }

    /**
     * Verifică dacă clădirea are facilități premium.
     *
     * @return true dacă are facilități premium
     */
    public boolean hasPremiumFeatures() {
        return (elevatorAvailable != null && elevatorAvailable) &&
                (airConditioning != null && airConditioning) &&
                (securitySystem != null && securitySystem) &&
                (accessibilityFeatures != null && accessibilityFeatures);
    }

    /**
     * Calculează scorul de calitate al clădirii (0-100).
     *
     * @return scorul de calitate
     */
    public Integer getQualityScore() {
        int score = 50; // Scor de bază

        if (isModern()) score += 15;
        if (elevatorAvailable != null && elevatorAvailable) score += 10;
        if (airConditioning != null && airConditioning) score += 10;
        if (securitySystem != null && securitySystem) score += 10;
        if (accessibilityFeatures != null && accessibilityFeatures) score += 5;
        if (parkingSpots != null && parkingSpots > 0) score += 10;

        return Math.min(100, score);
    }

    /**
     * DTO pentru listarea clădirilor (informații minime).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListDTO {
        private Long id;
        private String name;
        private String address;
        private Integer totalFloors;
        private Integer yearBuilt;
        private Building.BuildingType buildingType;
        private Integer totalSpaces;
        private Integer availableSpaces;
        private Double occupancyRate;
        private Boolean elevatorAvailable;
        private Boolean airConditioning;
        private Integer qualityScore;
    }

    /**
     * DTO pentru crearea clădirilor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private String name;
        private String address;
        private Integer totalFloors;
        private Integer yearBuilt;
        private Double latitude;
        private Double longitude;
        private String description;
        private Double totalArea;
        private Integer parkingSpots;
        private Boolean elevatorAvailable;
        private Boolean accessibilityFeatures;
        private Boolean securitySystem;
        private Boolean airConditioning;
        private Building.BuildingType buildingType;
    }

    /**
     * DTO pentru actualizarea clădirilor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {
        private String name;
        private String address;
        private Integer totalFloors;
        private Integer yearBuilt;
        private Double latitude;
        private Double longitude;
        private String description;
        private Double totalArea;
        private Integer parkingSpots;
        private Boolean elevatorAvailable;
        private Boolean accessibilityFeatures;
        private Boolean securitySystem;
        private Boolean airConditioning;
        private Building.BuildingType buildingType;
    }

    /**
     * DTO pentru mapă (informații geografice).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MapDTO {
        private Long id;
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private Building.BuildingType buildingType;
        private Integer availableSpaces;
        private Integer totalSpaces;
        private Double occupancyRate;
        private Integer qualityScore;
    }

    /**
     * DTO pentru statistici despre clădire.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatsDTO {
        private Long id;
        private String name;
        private String address;
        private Integer totalSpaces;
        private Integer availableSpaces;
        private Integer occupiedSpaces;
        private Double occupancyRate;
        private Double averageRentPerSpace;
        private Double totalRevenue;
        private Integer activeContracts;
        private Double averageSpaceSize;
        private Building.BuildingType buildingType;
        private Integer age;
        private Integer qualityScore;
    }
}