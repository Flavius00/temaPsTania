package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitatea ComercialSpace - reprezintă un spațiu comercial disponibil pentru închiriere.
 *
 * Conține informații despre:
 * - Proprietățile de bază (nume, descriere, suprafață, preț)
 * - Locația și coordonatele geografice
 * - Facilități și amenajări disponibile
 * - Proprietăți specifice tipului de spațiu
 * - Relații cu proprietarul, clădire, parcare și contracte
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar datele despre spațiul comercial
 * - Open/Closed: poate fi extins cu noi proprietăți fără modificare
 */
@Entity
@Table(name = "comercial_spaces", indexes = {
        @Index(name = "idx_space_available", columnList = "available"),
        @Index(name = "idx_space_type", columnList = "space_type"),
        @Index(name = "idx_space_price", columnList = "price_per_month"),
        @Index(name = "idx_space_owner", columnList = "owner_id"),
        @Index(name = "idx_space_building", columnList = "building_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"contracts", "amenities"}) // Excludem colecțiile pentru a evita referințele circulare
@EqualsAndHashCode(of = {"id", "name"})
public class ComercialSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Numele spațiului este obligatoriu")
    @Size(min = 2, max = 100, message = "Numele spațiului trebuie să aibă între 2 și 100 de caractere")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 1000, message = "Descrierea nu poate avea mai mult de 1000 de caractere")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Suprafața este obligatorie")
    @Positive(message = "Suprafața trebuie să fie pozitivă")
    @Column(name = "area", nullable = false, precision = 10, scale = 2)
    private Double area;

    @NotNull(message = "Prețul pe lună este obligatoriu")
    @Positive(message = "Prețul pe lună trebuie să fie pozitiv")
    @Column(name = "price_per_month", nullable = false, precision = 10, scale = 2)
    private Double pricePerMonth;

    @Size(max = 255, message = "Adresa nu poate avea mai mult de 255 de caractere")
    @Column(name = "address")
    private String address;

    @Column(name = "latitude", precision = 10, scale = 6)
    private Double latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private Double longitude;

    @NotNull(message = "Tipul spațiului este obligatoriu")
    @Enumerated(EnumType.STRING)
    @Column(name = "space_type", nullable = false)
    private SpaceType spaceType;

    @NotNull(message = "Statusul disponibilității este obligatoriu")
    @Column(name = "available", nullable = false)
    @Builder.Default
    private Boolean available = true;

    // Proprietăți specifice pentru birouri
    @Column(name = "floors")
    private Integer floors;

    @Column(name = "number_of_rooms")
    private Integer numberOfRooms;

    @Column(name = "has_reception")
    @Builder.Default
    private Boolean hasReception = false;

    // Proprietăți specifice pentru spații comerciale/retail
    @Column(name = "shop_window_size", precision = 8, scale = 2)
    private Double shopWindowSize;

    @Column(name = "has_customer_entrance")
    @Builder.Default
    private Boolean hasCustomerEntrance = true;

    @Column(name = "max_occupancy")
    private Integer maxOccupancy;

    // Proprietăți specifice pentru depozite
    @Column(name = "ceiling_height", precision = 8, scale = 2)
    private Double ceilingHeight;

    @Column(name = "has_loading_dock")
    @Builder.Default
    private Boolean hasLoadingDock = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "security_level")
    private SecurityLevel securityLevel;

    // Proprietăți generale
    @Column(name = "furnished")
    @Builder.Default
    private Boolean furnished = false;

    @Column(name = "air_conditioning")
    @Builder.Default
    private Boolean airConditioning = false;

    @Column(name = "heating")
    @Builder.Default
    private Boolean heating = false;

    @Column(name = "internet_ready")
    @Builder.Default
    private Boolean internetReady = false;

    @Column(name = "kitchen_facilities")
    @Builder.Default
    private Boolean kitchenFacilities = false;

    @Column(name = "bathroom_facilities")
    @Builder.Default
    private Boolean bathroomFacilities = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relația Many-to-One cu proprietarul.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    /**
     * Relația Many-to-One cu clădirea.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    /**
     * Relația One-to-One cu parcarea.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_id")
    private Parking parking;

    /**
     * Relația One-to-Many cu contractele de închiriere.
     */
    @OneToMany(
            mappedBy = "space",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<RentalContract> contracts = new ArrayList<>();

    /**
     * Lista de facilități/amenajări disponibile.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "space_amenities",
            joinColumns = @JoinColumn(name = "space_id")
    )
    @Column(name = "amenity", length = 100)
    @Builder.Default
    private List<String> amenities = new ArrayList<>();

    /**
     * Enum pentru nivelurile de securitate.
     */
    public enum SecurityLevel {
        LOW("Scăzut"),
        MEDIUM("Mediu"),
        HIGH("Ridicat"),
        MAXIMUM("Maxim");

        private final String displayName;

        SecurityLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Adaugă un contract de închiriere la spațiu.
     *
     * @param contract contractul de adăugat
     */
    public void addContract(RentalContract contract) {
        if (contracts == null) {
            contracts = new ArrayList<>();
        }
        contracts.add(contract);
        contract.setSpace(this);
    }

    /**
     * Elimină un contract de închiriere din spațiu.
     *
     * @param contract contractul de eliminat
     */
    public void removeContract(RentalContract contract) {
        if (contracts != null) {
            contracts.remove(contract);
            contract.setSpace(null);
        }
    }

    /**
     * Adaugă o facilitate la lista de amenajări.
     *
     * @param amenity facilitatea de adăugat
     */
    public void addAmenity(String amenity) {
        if (amenities == null) {
            amenities = new ArrayList<>();
        }
        if (!amenities.contains(amenity)) {
            amenities.add(amenity);
        }
    }

    /**
     * Elimină o facilitate din lista de amenajări.
     *
     * @param amenity facilitatea de eliminat
     */
    public void removeAmenity(String amenity) {
        if (amenities != null) {
            amenities.remove(amenity);
        }
    }

    /**
     * Verifică dacă spațiul are un contract activ.
     *
     * @return true dacă există un contract activ
     */
    public boolean hasActiveContract() {
        return contracts != null && contracts.stream()
                .anyMatch(contract -> "ACTIVE".equals(contract.getStatus()));
    }

    /**
     * Returnează contractul activ al spațiului.
     *
     * @return contractul activ sau null dacă nu există
     */
    public RentalContract getActiveContract() {
        return contracts != null ? contracts.stream()
                .filter(contract -> "ACTIVE".equals(contract.getStatus()))
                .findFirst()
                .orElse(null) : null;
    }

    /**
     * Calculează prețul pe metru pătrat.
     *
     * @return prețul pe metru pătrat
     */
    public double getPricePerSquareMeter() {
        return area != null && area > 0 ? pricePerMonth / area : 0.0;
    }

    /**
     * Verifică dacă spațiul are coordonate geografice.
     *
     * @return true dacă există coordonate
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    /**
     * Verifică dacă spațiul are parcare.
     *
     * @return true dacă există parcare
     */
    public boolean hasParking() {
        return parking != null;
    }

    /**
     * Marchează spațiul ca fiind disponibil.
     */
    public void makeAvailable() {
        this.available = true;
    }

    /**
     * Marchează spațiul ca fiind indisponibil.
     */
    public void makeUnavailable() {
        this.available = false;
    }
}