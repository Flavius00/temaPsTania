package com.example.demo.dto;

import com.example.demo.entity.RentalContract;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO pentru entitatea RentalContract.
 *
 * Acest DTO este folosit pentru transferul datelor despre contracte de închiriere
 * între diferitele layere ale aplicației și către/de la client.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar transferul datelor despre contracte
 * - Open/Closed: poate fi extins fără modificarea codului existent
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RentalContractDTO {

    private Long id;

    private String contractNumber;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double monthlyRent;

    private Double securityDeposit;

    private RentalContract.ContractStatus status;

    private Boolean isPaid;

    private LocalDate dateCreated;

    private String notes;

    private RentalContract.PaymentMethod paymentMethod;

    private String signature;

    private Boolean autoRenewal;

    private Boolean earlyTerminationAllowed;

    private Double earlyTerminationFee;

    private Double latePaymentFee;

    private LocalDate actualEndDate;

    private String terminationReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Relații
    private ComercialSpaceDTO.SummaryDTO space;

    private TenantDTO.ListDTO tenant;

    // Informații calculate
    private Long durationInMonths;

    private Double totalValue;

    private Double initialPayment;

    private Long daysUntilExpiration;

    private Boolean isActive;

    private Boolean isExpired;

    private Boolean isNearingExpiration;

    /**
     * Constructor pentru crearea unui RentalContractDTO cu datele de bază.
     *
     * @param startDate data de început
     * @param endDate data de sfârșit
     * @param monthlyRent chiria lunară
     * @param status statusul contractului
     */
    public RentalContractDTO(LocalDate startDate, LocalDate endDate,
                             Double monthlyRent, RentalContract.ContractStatus status) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyRent = monthlyRent;
        this.status = status;
        this.isPaid = false;
        this.dateCreated = LocalDate.now();
    }

    /**
     * Calculează durata contractului în luni.
     *
     * @return durata în luni
     */
    public Long getCalculatedDurationInMonths() {
        if (startDate == null || endDate == null) return null;
        return java.time.Period.between(startDate, endDate).toTotalMonths();
    }

    /**
     * Calculează valoarea totală a contractului.
     *
     * @return valoarea totală
     */
    public Double getCalculatedTotalValue() {
        Long duration = getCalculatedDurationInMonths();
        if (duration == null || monthlyRent == null) return null;
        return monthlyRent * duration;
    }

    /**
     * Calculează suma inițială de plătit (prima lună + garanție).
     *
     * @return suma inițială
     */
    public Double getCalculatedInitialPayment() {
        if (monthlyRent == null) return null;
        double deposit = securityDeposit != null ? securityDeposit : 0.0;
        return monthlyRent + deposit;
    }

    /**
     * Calculează numărul de zile până la expirare.
     *
     * @return numărul de zile până la expirare
     */
    public Long getCalculatedDaysUntilExpiration() {
        if (endDate == null) return null;
        LocalDate now = LocalDate.now();
        if (now.isAfter(endDate)) return 0L;
        return java.time.ChronoUnit.DAYS.between(now, endDate);
    }

    /**
     * Verifică dacă contractul este activ.
     *
     * @return true dacă contractul este activ
     */
    public Boolean getCalculatedIsActive() {
        if (status == null || startDate == null || endDate == null) return false;
        LocalDate now = LocalDate.now();
        return RentalContract.ContractStatus.ACTIVE.equals(status) &&
                !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    /**
     * Verifică dacă contractul a expirat.
     *
     * @return true dacă contractul a expirat
     */
    public Boolean getCalculatedIsExpired() {
        if (endDate == null) return false;
        return LocalDate.now().isAfter(endDate) ||
                RentalContract.ContractStatus.EXPIRED.equals(status);
    }

    /**
     * Verifică dacă contractul este aproape de expirare (în următoarele 30 de zile).
     *
     * @return true dacă este aproape de expirare
     */
    public Boolean getCalculatedIsNearingExpiration() {
        Long days = getCalculatedDaysUntilExpiration();
        return days != null && days <= 30 && days > 0;
    }

    /**
     * Verifică dacă contractul permite terminarea anticipată.
     *
     * @return true dacă permite terminarea anticipată
     */
    public boolean allowsEarlyTermination() {
        return earlyTerminationAllowed != null && earlyTerminationAllowed;
    }

    /**
     * DTO pentru listarea contractelor (informații minime).
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ListDTO {
        private Long id;
        private String contractNumber;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double monthlyRent;
        private RentalContract.ContractStatus status;
        private Boolean isPaid;
        private String spaceName;
        private String tenantName;
        private String ownerName;
        private Long daysUntilExpiration;
        private Boolean isNearingExpiration;
    }

    /**
     * DTO pentru crearea contractelor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private Long spaceId;
        private Long tenantId;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double monthlyRent;
        private Double securityDeposit;
        private String notes;
        private RentalContract.PaymentMethod paymentMethod;
        private String signature;
        private Boolean autoRenewal;
        private Boolean earlyTerminationAllowed;
        private Double earlyTerminationFee;
        private Double latePaymentFee;
    }

    /**
     * DTO pentru actualizarea contractelor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {
        private LocalDate endDate;
        private Double monthlyRent;
        private Double securityDeposit;
        private RentalContract.ContractStatus status;
        private Boolean isPaid;
        private String notes;
        private RentalContract.PaymentMethod paymentMethod;
        private Boolean autoRenewal;
        private Boolean earlyTerminationAllowed;
        private Double earlyTerminationFee;
        private Double latePaymentFee;
    }

    /**
     * DTO pentru rezumatul contractelor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SummaryDTO {
        private Long id;
        private String contractNumber;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double monthlyRent;
        private RentalContract.ContractStatus status;
        private Boolean isPaid;
        private Long daysUntilExpiration;
        private Boolean isActive;
        private Boolean isNearingExpiration;
    }

    /**
     * DTO pentru reînnoirea contractelor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RenewalDTO {
        private LocalDate newStartDate;
        private LocalDate newEndDate;
        private Double newMonthlyRent;
        private Double newSecurityDeposit;
        private String renewalNotes;
        private Boolean autoRenewal;
    }

    /**
     * DTO pentru terminarea contractelor.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TerminationDTO {
        private LocalDate terminationDate;
        private String terminationReason;
        private Double terminationFee;
        private String notes;
    }

    /**
     * DTO pentru statistici despre contracte.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatsDTO {
        private Long id;
        private String contractNumber;
        private LocalDate startDate;
        private LocalDate endDate;
        private Long durationInMonths;
        private Double monthlyRent;
        private Double totalValue;
        private RentalContract.ContractStatus status;
        private String spaceName;
        private String spaceType;
        private String tenantName;
        private String ownerName;
        private RentalContract.PaymentMethod paymentMethod;
        private Boolean autoRenewal;
        private LocalDateTime createdAt;
    }
}