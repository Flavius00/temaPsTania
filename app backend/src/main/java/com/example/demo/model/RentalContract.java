package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entitatea RentalContract - reprezintă un contract de închiriere între un proprietar și un chiriaș.
 *
 * Conține informații despre:
 * - Părțile contractante (spațiu, proprietar, chiriaș)
 * - Perioada contractului (dată început, dată sfârșit)
 * - Condițiile financiare (chirie, garanție)
 * - Statusul și detaliile contractului
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar datele despre contractul de închiriere
 * - Open/Closed: poate fi extins cu noi proprietăți fără modificare
 */
@Entity
@Table(name = "rental_contracts", indexes = {
        @Index(name = "idx_contract_status", columnList = "status"),
        @Index(name = "idx_contract_tenant", columnList = "tenant_id"),
        @Index(name = "idx_contract_space", columnList = "space_id"),
        @Index(name = "idx_contract_dates", columnList = "start_date, end_date"),
        @Index(name = "idx_contract_number", columnList = "contract_number", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"space", "tenant"}) // Excludem referințele pentru a evita referințele circulare
@EqualsAndHashCode(of = {"id", "contractNumber"})
public class RentalContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "contract_number", unique = true, nullable = false, length = 50)
    private String contractNumber;

    @NotNull(message = "Data de început este obligatorie")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Data de sfârșit este obligatorie")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Chiria lunară este obligatorie")
    @Positive(message = "Chiria lunară trebuie să fie pozitivă")
    @Column(name = "monthly_rent", nullable = false, precision = 10, scale = 2)
    private Double monthlyRent;

    @Positive(message = "Garanția trebuie să fie pozitivă")
    @Column(name = "security_deposit", precision = 10, scale = 2)
    private Double securityDeposit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ContractStatus status = ContractStatus.PENDING;

    @Column(name = "is_paid")
    @Builder.Default
    private Boolean isPaid = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_created")
    private LocalDate dateCreated;

    @Size(max = 1000, message = "Notele nu pot avea mai mult de 1000 de caractere")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Size(max = 255, message = "Semnătura nu poate avea mai mult de 255 caractere")
    @Column(name = "signature")
    private String signature;

    @Column(name = "auto_renewal")
    @Builder.Default
    private Boolean autoRenewal = false;

    @Column(name = "early_termination_allowed")
    @Builder.Default
    private Boolean earlyTerminationAllowed = false;

    @Column(name = "early_termination_fee", precision = 10, scale = 2)
    private Double earlyTerminationFee;

    @Column(name = "late_payment_fee", precision = 10, scale = 2)
    private Double latePaymentFee;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Size(max = 500, message = "Motivul terminării nu poate avea mai mult de 500 caractere")
    @Column(name = "termination_reason")
    private String terminationReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relația Many-to-One cu spațiul comercial.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private ComercialSpace space;

    /**
     * Relația Many-to-One cu chiriașul.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * Enum pentru statusurile contractului.
     */
    public enum ContractStatus {
        PENDING("În așteptare", "Contractul este în așteptarea aprobării"),
        ACTIVE("Activ", "Contractul este activ și în vigoare"),
        EXPIRED("Expirat", "Contractul și-a atins data de expirare"),
        TERMINATED("Terminat", "Contractul a fost terminat înainte de termen"),
        CANCELLED("Anulat", "Contractul a fost anulat"),
        RENEWED("Reînnoit", "Contractul a fost reînnoit");

        private final String displayName;
        private final String description;

        ContractStatus(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Enum pentru metodele de plată.
     */
    public enum PaymentMethod {
        CASH("Numerar", "Plată în numerar"),
        BANK_TRANSFER("Transfer bancar", "Transfer bancar"),
        CARD("Card", "Plată cu cardul"),
        CHECK("Cec", "Plată prin cec"),
        ONLINE("Online", "Plată online");

        private final String displayName;
        private final String description;

        PaymentMethod(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Metoda executată înainte de persistența entității.
     * Generează automat numărul contractului dacă nu există.
     */
    @PrePersist
    public void prePersist() {
        if (contractNumber == null || contractNumber.isEmpty()) {
            generateContractNumber();
        }
        if (dateCreated == null) {
            dateCreated = LocalDate.now();
        }
    }

    /**
     * Generează un număr unic pentru contract.
     */
    private void generateContractNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.contractNumber = "RENT-" + timestamp + "-" + uuid;
    }

    /**
     * Verifică dacă contractul este activ.
     *
     * @return true dacă contractul este activ
     */
    public boolean isActive() {
        return ContractStatus.ACTIVE.equals(status) &&
                LocalDate.now().isAfter(startDate.minusDays(1)) &&
                LocalDate.now().isBefore(endDate.plusDays(1));
    }

    /**
     * Verifică dacă contractul a expirat.
     *
     * @return true dacă contractul a expirat
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate) || ContractStatus.EXPIRED.equals(status);
    }

    /**
     * Verifică dacă contractul poate fi reînnoit.
     *
     * @return true dacă poate fi reînnoit
     */
    public boolean canBeRenewed() {
        return (ContractStatus.ACTIVE.equals(status) || ContractStatus.EXPIRED.equals(status)) &&
                (autoRenewal || LocalDate.now().isAfter(endDate.minusDays(30)));
    }

    /**
     * Calculează durata contractului în luni.
     *
     * @return durata în luni
     */
    public long getDurationInMonths() {
        return java.time.Period.between(startDate, endDate).toTotalMonths();
    }

    /**
     * Calculează valoarea totală a contractului.
     *
     * @return valoarea totală
     */
    public double getTotalValue() {
        return monthlyRent * getDurationInMonths();
    }

    /**
     * Calculează suma inițială de plătit (prima lună + garanție).
     *
     * @return suma inițială
     */
    public double getInitialPayment() {
        double deposit = securityDeposit != null ? securityDeposit : 0.0;
        return monthlyRent + deposit;
    }

    /**
     * Activează contractul.
     */
    public void activate() {
        this.status = ContractStatus.ACTIVE;
        this.isPaid = true;
    }

    /**
     * Termină contractul înainte de termen.
     *
     * @param reason motivul terminării
     */
    public void terminate(String reason) {
        this.status = ContractStatus.TERMINATED;
        this.actualEndDate = LocalDate.now();
        this.terminationReason = reason;
    }

    /**
     * Marchează contractul ca expirat.
     */
    public void expire() {
        this.status = ContractStatus.EXPIRED;
        this.actualEndDate = endDate;
    }

    /**
     * Reînnoiește contractul cu o nouă dată de sfârșit.
     *
     * @param newEndDate noua dată de sfârșit
     */
    public void renew(LocalDate newEndDate) {
        this.endDate = newEndDate;
        this.status = ContractStatus.RENEWED;
    }

    /**
     * Anulează contractul.
     *
     * @param reason motivul anulării
     */
    public void cancel(String reason) {
        this.status = ContractStatus.CANCELLED;
        this.terminationReason = reason;
        this.actualEndDate = LocalDate.now();
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
     * Calculează taxa pentru terminarea anticipată.
     *
     * @return taxa de terminare anticipată
     */
    public double getEarlyTerminationFeeAmount() {
        if (!allowsEarlyTermination() || earlyTerminationFee == null) {
            return 0.0;
        }
        return earlyTerminationFee;
    }

    /**
     * Calculează numărul de zile rămase până la expirare.
     *
     * @return numărul de zile rămase
     */
    public long getDaysUntilExpiration() {
        if (isExpired()) {
            return 0;
        }
        return java.time.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    /**
     * Verifică dacă contractul este aproape de expirare (în următoarele 30 de zile).
     *
     * @return true dacă este aproape de expirare
     */
    public boolean isNearingExpiration() {
        return getDaysUntilExpiration() <= 30 && getDaysUntilExpiration() > 0;
    }
}