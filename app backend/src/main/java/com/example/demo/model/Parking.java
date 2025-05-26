package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entitatea Parking - reprezintă facilitățile de parcare asociate unui spațiu comercial.
 *
 * Conține informații despre:
 * - Numărul de locuri de parcare
 * - Prețul per loc de parcare
 * - Tipul și caracteristicile parcării
 * - Relația cu spațiul comercial asociat
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar datele despre parcare
 * - Open/Closed: poate fi extins cu noi proprietăți fără modificare
 */
@Entity
@Table(name = "parkings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"space"}) // Excludem referința pentru a evita referințele circulare
@EqualsAndHashCode(of = {"id"})
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Positive(message = "Numărul de locuri de parcare trebuie să fie pozitiv")
    @Column(name = "number_of_spots", nullable = false)
    private Integer numberOfSpots;

    @PositiveOrZero(message = "Prețul per loc de parcare trebuie să fie pozitiv sau zero")
    @Column(name = "price_per_spot", precision = 10, scale = 2)
    @Builder.Default
    private Double pricePerSpot = 0.0;

    @Column(name = "covered")
    @Builder.Default
    private Boolean covered = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "parking_type")
    private ParkingType parkingType;

    @Column(name = "reserved_spots")
    @Builder.Default
    private Integer reservedSpots = 0;

    @Column(name = "disabled_access_spots")
    @Builder.Default
    private Integer disabledAccessSpots = 0;

    @Column(name = "electric_charging_spots")
    @Builder.Default
    private Integer electricChargingSpots = 0;

    @Column(name = "security_cameras")
    @Builder.Default
    private Boolean securityCameras = false;

    @Column(name = "security_guard")
    @Builder.Default
    private Boolean securityGuard = false;

    @Column(name = "access_card_required")
    @Builder.Default
    private Boolean accessCardRequired = false;

    @Column(name = "height_restriction", precision = 5, scale = 2)
    private Double heightRestriction;

    @Column(name = "operating_hours")
    private String operatingHours;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relația One-to-One cu spațiul comercial.
     * Parcarea este asociată unui singur spațiu comercial.
     */
    @OneToOne(mappedBy = "parking", fetch = FetchType.LAZY)
    private ComercialSpace space;

    /**
     * Enum pentru tipurile de parcare.
     */
    public enum ParkingType {
        SURFACE("La suprafață", "Parcare la nivel străzii"),
        UNDERGROUND("Subterană", "Parcare subterană"),
        MULTI_LEVEL("Multi-nivel", "Parcare cu mai multe niveluri"),
        GARAGE("Garaj", "Garaj închis"),
        STREET("Stradală", "Parcare pe stradă");

        private final String displayName;
        private final String description;

        ParkingType(String displayName, String description) {
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
     * Calculează numărul de locuri disponibile pentru închiriere.
     *
     * @return numărul de locuri disponibile
     */
    public int getAvailableSpots() {
        return Math.max(0, numberOfSpots - reservedSpots);
    }

    /**
     * Calculează prețul total pentru parcare (toate locurile).
     *
     * @return prețul total
     */
    public double getTotalPrice() {
        return numberOfSpots * pricePerSpot;
    }

    /**
     * Verifică dacă parcarea are facilități pentru persoane cu dizabilități.
     *
     * @return true dacă există locuri pentru persoane cu dizabilități
     */
    public boolean hasDisabledAccess() {
        return disabledAccessSpots != null && disabledAccessSpots > 0;
    }

    /**
     * Verifică dacă parcarea are facilități de încărcare electrică.
     *
     * @return true dacă există locuri cu încărcare electrică
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
     * Verifică dacă parcarea este acoperită.
     *
     * @return true dacă este acoperită
     */
    public boolean isCovered() {
        return covered != null && covered;
    }

    /**
     * Verifică dacă parcarea are restricții de înălțime.
     *
     * @return true dacă există restricții de înălțime
     */
    public boolean hasHeightRestriction() {
        return heightRestriction != null && heightRestriction > 0;
    }

    /**
     * Returnează un scor de calitate pentru parcare (0-100).
     *
     * @return scorul de calitate
     */
    public int getQualityScore() {
        int score = 50; // Scor de bază

        if (isCovered()) score += 15;
        if (isSecured()) score += 20;
        if (hasDisabledAccess()) score += 10;
        if (hasElectricCharging()) score += 15;
        if (parkingType == ParkingType.UNDERGROUND || parkingType == ParkingType.GARAGE) score += 10;

        return Math.min(100, score);
    }

    /**
     * Rezervă un număr de locuri de parcare.
     *
     * @param spotsToReserve numărul de locuri de rezervat
     * @return true dacă rezervarea a fost posibilă
     */
    public boolean reserveSpots(int spotsToReserve) {
        if (spotsToReserve <= 0 || spotsToReserve > getAvailableSpots()) {
            return false;
        }

        this.reservedSpots = (this.reservedSpots == null ? 0 : this.reservedSpots) + spotsToReserve;
        return true;
    }

    /**
     * Eliberează un număr de locuri de parcare rezervate.
     *
     * @param spotsToRelease numărul de locuri de eliberat
     * @return true dacă eliberarea a fost posibilă
     */
    public boolean releaseSpots(int spotsToRelease) {
        if (spotsToRelease <= 0 || this.reservedSpots == null || spotsToRelease > this.reservedSpots) {
            return false;
        }

        this.reservedSpots -= spotsToRelease;
        return true;
    }
}