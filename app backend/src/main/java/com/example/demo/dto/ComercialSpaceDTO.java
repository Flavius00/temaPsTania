package com.example.demo.dto;

import com.example.demo.entity.ComercialSpace;
import com.example.demo.entity.SpaceType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pentru entitatea ComercialSpace.
 *
 * Acest DTO este folosit pentru transferul datelor despre spații comerciale
 * între diferitele layere ale aplicației și către/de la client.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar transferul datelor despre spații comerciale
 * - Open/Closed: poate fi extins fără modificarea codului existent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComercialSpaceDTO {

    private Long id;

    private String name;

    private String description;

    private Double area;

    private Double pricePerMonth;

    private String address;

    private Double latitude;

    private Double longitude;

    private SpaceType spaceType;

    private Boolean available;

    // Proprietăți specifice pentru birouri
    private Integer floors;
    private Integer numberOfRooms;
    private Boolean hasReception;

    // Proprietăți specifice pentru spații comerciale/retail
    private Double shopWindowSize;
    private Boolean hasCustomerEntrance;
    private Integer maxOccupancy;

    // Proprietăți specifice pentru depozite
    private Double ceilingHeight;
    private Boolean hasLoadingDock;
    private ComercialSpace.SecurityLevel securityLevel;

    // Proprietăți generale
    private Boolean furnished;
    private Boolean airConditioning;
    private Boolean heating;
    private Boolean internetReady;
    private Boolean kitchenFacilities;
    private Boolean bathroomFacilities;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Relații
    private OwnerDTO.ListDTO owner;
    private BuildingDTO.ListDTO building;
    private ParkingDTO parking;

    // Facilități
    private List<String> amenities;

    // Informații calculate
    private Double pricePerSquareMeter;
    private Boolean hasActiveContract;
    private RentalContractDTO.SummaryDTO activeContract;
    private Integer totalContracts;

    /**
     * Constructor pentru crearea unui ComercialSpaceDTO cu datele de bază.
     *
     * @param name numele spațiului
     * @param area suprafața
     * @param pricePerMonth prețul pe lună
     * @param spaceType tipul de spațiu
     * @param available disponibil sau nu
     */
    public ComercialSpaceDTO(String name, Double area, Double pricePerMonth,
                             SpaceType spaceType, Boolean available) {
        this.name = name;
        this.area = area;
        this.pricePerMonth = pricePerMonth;
        this.spaceType = spaceType;
        this.available = available;
    }

    /**
     * Calculează prețul pe metru pătrat.
     *
     * @return prețul pe metru pătrat
     */
    public Double getCalculatedPricePerSquareMeter() {
        if (area != null && area > 0 && pricePerMonth != null) {
            return pricePerMonth / area;
        }
        return null;
    }

    /**
     * Verifică dacă spațiul este disponibil.
     *
     * @return true dacă este disponibil
     */
    public boolean isAvailable() {
        return available != null && available;
    }

    /**
     * Verifică dacă spațiul are coordonate geografice.
     *
     * @return true dacă are coordonate
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    /**
     * Verifică dacă spațiul are parcare.
     *
     * @return true dacă are parcare
     */
    public boolean hasParking() {
        return parking != null;
    }

    /**
     * Verifică dacă spațiul are facilități premium.
     *
     * @return true dacă are facilități premium
     */
    public boolean hasPremiumFeatures() {
        return (furnished != null && furnished) ||
                (airConditioning != null && airConditioning) ||
                (internetReady != null && internetReady) ||
                (kitchenFacilities != null && kitchenFacilities);
    }

    /**
     * Calculează scorul de calitate al spațiului (0-100).
     *
     * @return scorul de calitate
     */
    public Integer getQualityScore() {
        int score = 50; // Scor de bază

        if (furnished != null && furnished) score += 10;
        if (airConditioning != null && airConditioning) score += 10;
        if (heating != null && heating) score += 5;
        if (internetReady != null && internetReady) score += 10;
        if (kitchenFacilities != null && kitchenFacilities) score += 5;
        if (hasParking()) score += 10;
        if (amenities != null && !amenities.isEmpty()) score += amenities.size() * 2;

        return Math.min(100, score);
    }

    /**
     * DTO pentru listarea spațiilor comerciale (informații minime).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListDTO {
        private Long id;
        private String name;
        private Double area;
        private Double pricePerMonth;
        private Double pricePerSquareMeter;
        private SpaceType spaceType;
        private Boolean available;
        private String ownerName;
        private String buildingName;
        private String address;
        private Boolean hasParking;
        private Integer qualityScore;
    }

    /**
     * DTO pentru crearea spațiilor comerciale.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private String name;
        private String description;
        private Double area;
        private Double pricePerMonth;
        private String address;
        private Double latitude;
        private Double longitude;
        private SpaceType spaceType;
        private Boolean available;
        private Long ownerId;
        private Long buildingId;

        // Proprietăți specifice
        private Integer floors;
        private Integer numberOfRooms;
        private Boolean hasReception;
        private Double shopWindowSize;
        private Boolean hasCustomerEntrance;
        private Integer maxOccupancy;
        private Double ceilingHeight;
        private Boolean hasLoadingDock;
        private ComercialSpace.SecurityLevel securityLevel;

        // Proprietăți generale
        private Boolean furnished;
        private Boolean airConditioning;
        private Boolean heating;
        private Boolean internetReady;
        private Boolean kitchenFacilities;
        private Boolean bathroomFacilities;

        private List<String> amenities;
    }

    /**
     * DTO pentru actualizarea spațiilor comerciale.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {
        private String name;
        private String description;
        private Double area;
        private Double pricePerMonth;
        private String address;
        private Double latitude;
        private Double longitude;
        private Boolean available;

        // Proprietăți specifice
        private Integer floors;
        private Integer numberOfRooms;
        private Boolean hasReception;
        private Double shopWindowSize;
        private Boolean hasCustomerEntrance;
        private Integer maxOccupancy;
        private Double ceilingHeight;
        private Boolean hasLoadingDock;
        private ComercialSpace.SecurityLevel securityLevel;

        // Proprietăți generale
        private Boolean furnished;
        private Boolean airConditioning;
        private Boolean heating;
        private Boolean internetReady;
        private Boolean kitchenFacilities;
        private Boolean bathroomFacilities;

        private List<String> amenities;
    }

    /**
     * DTO pentru căutarea spațiilor comerciale.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchDTO {
        private SpaceType spaceType;
        private Double minArea;
        private Double maxArea;
        private Double minPrice;
        private Double maxPrice;
        private String location;
        private Boolean available;
        private Boolean furnished;
        private Boolean airConditioning;
        private Boolean hasParking;
        private List<String> requiredAmenities;
        private String sortBy; // price, area, pricePerSqm
        private String sortOrder; // asc, desc
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
        private Double area;
        private Double pricePerMonth;
        private Double latitude;
        private Double longitude;
        private SpaceType spaceType;
        private Boolean available;
        private String buildingName;
        private String address;
        private Integer qualityScore;
    }

    /**
     * DTO pentru rezumatul spațiilor comerciale.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SummaryDTO {
        private Long id;
        private String name;
        private Double area;
        private Double pricePerMonth;
        private SpaceType spaceType;
        private Boolean available;
        private String address;
        private Boolean hasActiveContract;
        private LocalDateTime lastContractDate;
    }

    /**
     * DTO pentru detaliile complete ale spațiului comercial.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DetailDTO {
        private Long id;
        private String name;
        private String description;
        private Double area;
        private Double pricePerMonth;
        private Double pricePerSquareMeter;
        private String address;
        private Double latitude;
        private Double longitude;
        private SpaceType spaceType;
        private Boolean available;

        // Proprietăți specifice
        private Integer floors;
        private Integer numberOfRooms;
        private Boolean hasReception;
        private Double shopWindowSize;
        private Boolean hasCustomerEntrance;
        private Integer maxOccupancy;
        private Double ceilingHeight;
        private Boolean hasLoadingDock;
        private ComercialSpace.SecurityLevel securityLevel;

        // Proprietăți generale
        private Boolean furnished;
        private Boolean airConditioning;
        private Boolean heating;
        private Boolean internetReady;
        private Boolean kitchenFacilities;
        private Boolean bathroomFacilities;

        // Relații
        private OwnerDTO.ListDTO owner;
        private BuildingDTO.ListDTO building;
        private ParkingDTO parking;

        // Facilități și informații suplimentare
        private List<String> amenities;
        private Integer qualityScore;
        private Boolean hasActiveContract;
        private RentalContractDTO.SummaryDTO activeContract;
        private List<RentalContractDTO.SummaryDTO> contractHistory;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}