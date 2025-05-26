package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pentru entitatea Owner.
 *
 * Acest DTO este folosit pentru transferul datelor despre proprietari
 * între diferitele layere ale aplicației și către/de la client.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar transferul datelor despre proprietari
 * - Open/Closed: poate fi extins fără modificarea codului existent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnerDTO {

    private Long id;

    private String name;

    private String email;

    private String username;

    private String phone;

    private String address;

    private String profilePictureUrl;

    private Boolean active;

    private String companyName;

    private String taxId;

    private String companyType;

    private String registrationNumber;

    private String bankAccount;

    private String bankName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Informații calcularte sau agregate
    private Integer totalSpaces;

    private Integer availableSpaces;

    private Integer occupiedSpaces;

    private Double totalRevenue;

    private Double occupancyRate;

    private List<ComercialSpaceDTO.SummaryDTO> spaces;

    /**
     * Constructor pentru crearea unui OwnerDTO cu datele de bază.
     *
     * @param name numele proprietarului
     * @param email email-ul proprietarului
     * @param companyName numele companiei
     * @param taxId CUI-ul fiscal
     */
    public OwnerDTO(String name, String email, String companyName, String taxId) {
        this.name = name;
        this.email = email;
        this.companyName = companyName;
        this.taxId = taxId;
        this.active = true;
    }

    /**
     * Verifică dacă proprietarul este activ.
     *
     * @return true dacă proprietarul este activ
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Verifică dacă proprietarul are informații complete despre companie.
     *
     * @return true dacă are informații complete
     */
    public boolean hasCompleteCompanyInfo() {
        return companyName != null && !companyName.trim().isEmpty() &&
                taxId != null && !taxId.trim().isEmpty();
    }

    /**
     * Verifică dacă proprietarul are spații comerciale.
     *
     * @return true dacă are spații
     */
    public boolean hasSpaces() {
        return totalSpaces != null && totalSpaces > 0;
    }

    /**
     * Verifică dacă proprietarul are spații disponibile.
     *
     * @return true dacă are spații disponibile
     */
    public boolean hasAvailableSpaces() {
        return availableSpaces != null && availableSpaces > 0;
    }

    /**
     * Calculează rata de ocupare.
     *
     * @return rata de ocupare (0-1)
     */
    public double getCalculatedOccupancyRate() {
        if (totalSpaces == null || totalSpaces == 0) {
            return 0.0;
        }
        int occupied = occupiedSpaces != null ? occupiedSpaces : 0;
        return (double) occupied / totalSpaces;
    }

    /**
     * Returnează venitul lunar estimat.
     *
     * @return venitul lunar
     */
    public Double getMonthlyRevenue() {
        return totalRevenue;
    }

    /**
     * DTO pentru listarea proprietarilor (informații minime).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListDTO {
        private Long id;
        private String name;
        private String email;
        private String companyName;
        private String taxId;
        private Boolean active;
        private Integer totalSpaces;
        private Integer availableSpaces;
        private Double totalRevenue;
        private Double occupancyRate;
        private LocalDateTime createdAt;
    }

    /**
     * DTO pentru crearea proprietarilor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private String name;
        private String email;
        private String username;
        private String password;
        private String phone;
        private String address;
        private String companyName;
        private String taxId;
        private String companyType;
        private String registrationNumber;
        private String bankAccount;
        private String bankName;
    }

    /**
     * DTO pentru actualizarea proprietarilor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {
        private String name;
        private String email;
        private String phone;
        private String address;
        private String profilePictureUrl;
        private String companyName;
        private String companyType;
        private String registrationNumber;
        private String bankAccount;
        private String bankName;
        private Boolean active;
    }

    /**
     * DTO pentru statistici despre proprietar.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatsDTO {
        private Long id;
        private String name;
        private String companyName;
        private Integer totalSpaces;
        private Integer availableSpaces;
        private Integer occupiedSpaces;
        private Double occupancyRate;
        private Double totalRevenue;
        private Double averageRentPerSpace;
        private Integer activeContracts;
        private LocalDateTime lastActivity;
    }
}