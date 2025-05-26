package com.example.demo.dto;

import com.example.demo.entity.Parking;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO pentru entitatea Parking.
 *
 * Acest DTO este folosit pentru transferul datelor despre parcări
 * între diferitele layere ale aplicației și către/de la client.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar transferul datelor despre parcări
 * - Open/Closed: poate fi extins fără modificarea codului existent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParkingDTO {

    private Long id;

    private Integer numberOfSpots;

    private Double pricePerSpot;

    private Boolean covered;

    private Parking.ParkingType parkingType;

    private Integer reservedSpots;

    private Integer disabledAccessSpots;

    private Integer electricChargingSpots;

    private Boolean securityCameras;

    private Boolean securityGuard;

    private Boolean accessCardRequired;

    private Double heightRestriction;

    private String operatingHours;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Informații calculate
    private Integer availableSpots;

    private Double totalPrice;

    private Double occupancyRate;

    private Integer qualityScore;

    /**
     * Constructor pentru crearea unui ParkingDTO cu datele de bază.
     *
     * @param numberOfSpots numărul de locuri
     * @param pricePerSpot prețul per loc
     * @param parkingType tipul de parcare
     * @param covered acoperită sau nu
     */
    public ParkingDTO(Integer numberOfSpots, Double pricePerSpot,
                      Parking.ParkingType parkingType, Boolean covered) {
        this.numberOfSpots = numberOfSpots;
        this.pricePerSpot = pricePerSpot;
        this.parkingType = parkingType;
        this.covered = covered;
        this.reservedSpots = 0;
    }

    /**
     * Calculează numărul de locuri disponibile.
     *
     * @return numărul de locuri disponibile
     */
    public Integer getCalculatedAvailableSpots() {
        if (numberOfSpots == null) return 0;
        int reserved = reservedSpots != null ? reservedSpots : 0;
        return Math.max(0, numberOfSpots - reserved);
    }

    /**
     * Calculează prețul total pentru toate locurile.
     *
     * @return prețul total
     */
    public Double getCalculatedTotalPrice() {
        if (numberOfSpots == null || pricePerSpot == null) return 0.0;
        return numberOfSpots * pricePerSpot;
    }

    /**
     * Calculează rata de ocupare.
     *
     * @return rata de ocupare (0-1)
     */
    public Double getCalculatedOccupancyRate() {
        if (numberOfSpots == null || numberOfSpots == 0) return 0.0;
        int reserved = reservedSpots != null ? reservedSpots : 0;
        return (double) reserved / numberOfSpots;
    }

    /**
     * Verifică dacă parcarea este acoperită.
     *
     * @return true dacă este acoperită
     */
    public boolean isCovered() {
        return covered != null && covered;
    }

    /**
     * Verifică dacă parcarea are facilități pentru persoane cu dizabilități.
     *
     * @return true dacă are facilități pentru persoane cu dizabilități
     */
    public boolean hasDisabledAccess() {
        return disabledAccessSpots != null && disabledAccessSpots > 0;
    }

    /**
     * Verifică dacă parcarea are facilități de încărcare electrică.
     *
     * @return true dacă are facilități de încărcare electrică
     */
    public boolean hasElectricCharging() {
        return electricChargingSpots != null && electricChargingSpots > 0;
    }

    /**
     * Verifică dacă parcarea este securizată.
     *
     * @return true dacă are sisteme de securitate
     */
    public boolean isSecured() {
        return (securityCameras != null && securityCameras) ||
                (securityGuard != null && securityGuard) ||
                (accessCardRequired != null && accessCardRequired);
    }

    /**
     * Verifică dacă parcarea are restricții de înălțime.
     *
     * @return true dacă are restricții de înălțime
     */
    public boolean hasHeightRestriction() {
        return heightRestriction != null && heightRestriction > 0;
    }

    /**
     * Calculează scorul de calitate al parcării (0-100).
     *
     * @return scorul de calitate
     */
    public Integer getCalculatedQualityScore() {
        int score = 50; // Scor de bază

        if (isCovered()) score += 15;
        if (isSecured()) score += 20;
        if (hasDisabledAccess()) score += 10;
        if (hasElectricCharging()) score += 15;
        if (parkingType == Parking.ParkingType.UNDERGROUND ||
                parkingType == Parking.ParkingType.GARAGE) {
            score += 10;
        }

        return Math.min(100, score);
    }

    /**
     * DTO pentru listarea parcărilor (informații minime).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListDTO {
        private Long id;
        private Integer numberOfSpots;
        private Integer availableSpots;
        private Double pricePerSpot;
        private Double totalPrice;
        private Boolean covered;
        private Parking.ParkingType parkingType;
        private Double occupancyRate;
        private Integer qualityScore;
        private Boolean hasDisabledAccess;
        private Boolean hasElectricCharging;
    }

    /**
     * DTO pentru crearea parcărilor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private Integer numberOfSpots;
        private Double pricePerSpot;
        private Boolean covered;
        private Parking.ParkingType parkingType;
        private Integer disabledAccessSpots;
        private Integer electricChargingSpots;
        private Boolean securityCameras;
        private Boolean securityGuard;
        private Boolean accessCardRequired;
        private Double heightRestriction;
        private String operatingHours;
    }

    /**
     * DTO pentru actualizarea parcărilor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {
        private Integer numberOfSpots;
        private Double pricePerSpot;
        private Boolean covered;
        private Parking.ParkingType parkingType;
        private Integer reservedSpots;
        private Integer disabledAccessSpots;
        private Integer electricChargingSpots;
        private Boolean securityCameras;
        private Boolean securityGuard;
        private Boolean accessCardRequired;
        private Double heightRestriction;
        private String operatingHours;
    }

    /**
     * DTO pentru statistici despre parcare.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatsDTO {
        private Long id;
        private Integer numberOfSpots;
        private Integer availableSpots;
        private Integer reservedSpots;
        private Double occupancyRate;
        private Double pricePerSpot;
        private Double totalPrice;
        private Parking.ParkingType parkingType;
        private Boolean covered;
        private Integer qualityScore;
        private Boolean hasDisabledAccess;
        private Boolean hasElectricCharging;
        private Boolean isSecured;
    }
}