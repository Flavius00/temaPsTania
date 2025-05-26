package com.example.demo.dto;

import com.example.demo.entity.SpaceType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pentru entitatea Tenant.
 *
 * Acest DTO este folosit pentru transferul datelor despre chiriași
 * între diferitele layere ale aplicației și către/de la client.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar transferul datelor despre chiriași
 * - Open/Closed: poate fi extins fără modificarea codului existent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantDTO {

    private Long id;

    private String name;

    private String email;

    private String username;

    private String phone;

    private String address;

    private String profilePictureUrl;

    private Boolean active;

    private String companyName;

    private String businessType;

    private String taxId;

    private String registrationNumber;

    private String bankAccount;

    private String bankName;

    private SpaceType preferredSpaceType;

    private Double maxBudget;

    private Double minArea;

    private Double maxArea;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Informații calculate sau agregate
    private Integer totalContracts;

    private Integer activeContracts;

    private Double totalMonthlyExpenses;

    private Double averageRentPaid;

    private List<RentalContractDTO.SummaryDTO> contracts;

    /**
     * Constructor pentru crearea unui TenantDTO cu datele de bază.
     *
     * @param name numele chiriașului
     * @param email email-ul chiriașului
     * @param companyName numele companiei
     * @param businessType tipul de business
     */
    public TenantDTO(String name, String email, String companyName, String businessType) {
        this.name = name;
        this.email = email;
        this.companyName = companyName;
        this.businessType = businessType;
        this.active = true;
    }

    /**
     * Verifică dacă chiriașul este activ.
     *
     * @return true dacă chiriașul este activ
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Verifică dacă chiriașul are informații complete despre companie.
     *
     * @return true dacă are informații complete
     */
    public boolean hasCompleteCompanyInfo() {
        return companyName != null && !companyName.trim().isEmpty() &&
                businessType != null && !businessType.trim().isEmpty();
    }

    /**
     * Verifică dacă chiriașul are contracte active.
     *
     * @return true dacă are contracte active
     */
    public boolean hasActiveContracts() {
        return activeContracts != null && activeContracts > 0;
    }

    /**
     * Verifică dacă chiriașul are preferințe setate pentru căutarea spațiilor.
     *
     * @return true dacă are preferințe setate
     */
    public boolean hasSearchPreferences() {
        return preferredSpaceType != null ||
                maxBudget != null ||
                (minArea != null || maxArea != null);
    }

    /**
     * Verifică dacă un spațiu comercial se potrivește cu preferințele chiriașului.
     *
     * @param spaceType tipul spațiului
     * @param pricePerMonth prețul pe lună
     * @param area suprafața
     * @return true dacă spațiul se potrivește
     */
    public boolean isCompatibleWithSpace(SpaceType spaceType, Double pricePerMonth, Double area) {
        // Verifică tipul de spațiu
        if (preferredSpaceType != null && !preferredSpaceType.equals(spaceType)) {
            return false;
        }

        // Verifică bugetul
        if (maxBudget != null && pricePerMonth != null && pricePerMonth > maxBudget) {
            return false;
        }

        // Verifică suprafața minimă
        if (minArea != null && area != null && area < minArea) {
            return false;
        }

        // Verifică suprafața maximă
        if (maxArea != null && area != null && area > maxArea) {
            return false;
        }

        return true;
    }

    /**
     * Calculează utilizarea bugetului.
     *
     * @return procentajul din buget utilizat (0-1)
     */
    public Double getBudgetUtilization() {
        if (maxBudget == null || maxBudget == 0 || totalMonthlyExpenses == null) {
            return null;
        }
        return Math.min(1.0, totalMonthlyExpenses / maxBudget);
    }

    /**
     * DTO pentru listarea chiriașilor (informații minime).
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
        private String businessType;
        private Boolean active;
        private Integer activeContracts;
        private Double totalMonthlyExpenses;
        private SpaceType preferredSpaceType;
        private LocalDateTime createdAt;
    }

    /**
     * DTO pentru crearea chiriașilor.
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
        private String businessType;
        private String taxId;
        private String registrationNumber;
        private String bankAccount;
        private String bankName;
        private SpaceType preferredSpaceType;
        private Double maxBudget;
        private Double minArea;
        private Double maxArea;
    }

    /**
     * DTO pentru actualizarea chiriașilor.
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
        private String businessType;
        private String registrationNumber;
        private String bankAccount;
        private String bankName;
        private SpaceType preferredSpaceType;
        private Double maxBudget;
        private Double minArea;
        private Double maxArea;
        private Boolean active;
    }

    /**
     * DTO pentru căutarea de spații compatibile.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCriteriaDTO {
        private SpaceType preferredSpaceType;
        private Double maxBudget;
        private Double minArea;
        private Double maxArea;
        private String preferredLocation;
        private List<String> requiredAmenities;
    }

    /**
     * DTO pentru statistici despre chiriaș.
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
        private String businessType;
        private Integer totalContracts;
        private Integer activeContracts;
        private Double totalMonthlyExpenses;
        private Double averageRentPaid;
        private Double budgetUtilization;
        private SpaceType preferredSpaceType;
        private LocalDateTime lastContractDate;
        private Integer contractsThisYear;
    }
}