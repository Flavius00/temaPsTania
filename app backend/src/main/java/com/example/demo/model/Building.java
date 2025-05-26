package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitatea Building - reprezintă o clădire care conține spații comerciale.
 *
 * Conține informații despre:
 * - Numele și adresa clădirii
 * - Numărul de etaje și anul construcției
 * - Coordonatele geografice
 * - Lista spațiilor comerciale din clădire
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar datele despre clădire
 * - Open/Closed: poate fi extinsă cu noi proprietăți fără modificare
 */
@Entity
@Table(name = "buildings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"spaces"}) // Excludem lista pentru a evita referințele circulare
@EqualsAndHashCode(of = {"id", "name", "address"})
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Numele clădirii este obligatoriu")
    @Size(min = 2, max = 100, message = "Numele clădirii trebuie să aibă între 2 și 100 de caractere")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Adresa este obligatorie")
    @Size(max = 255, message = "Adresa nu poate avea mai mult de 255 de caractere")
    @Column(name = "address", nullable = false)
    private String address;

    @Positive(message = "Numărul de etaje trebuie să fie pozitiv")
    @Column(name = "total_floors")
    private Integer totalFloors;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @Column(name = "latitude", precision = 10, scale = 6)
    private Double latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private Double longitude;

    @Size(max = 500, message = "Descrierea nu poate avea mai mult de 500 de caractere")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_area")
    private Double totalArea;

    @Column(name = "parking_spots")
    private Integer parkingSpots;

    @Column(name = "elevator_available")
    @Builder.Default
    private Boolean elevatorAvailable = false;

    @Column(name = "accessibility_features")
    @Builder.Default
    private Boolean accessibilityFeatures = false;

    @Column(name = "security_system")
    @Builder.Default
    private Boolean securitySystem = false;

    @Column(name = "air_conditioning")
    @Builder.Default
    private Boolean airConditioning = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "building_type")
    private BuildingType buildingType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Relația One-to-Many cu spațiile comerciale.
     *
     * O clădire poate conține multiple spații comerciale.
     * Folosim FetchType.LAZY pentru performanță optimă.
     */
    @OneToMany(
            mappedBy = "building",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ComercialSpace> spaces = new ArrayList<>();

    /**
     * Enum pentru tipurile de clădiri.
     */
    public enum BuildingType {
        OFFICE_BUILDING("Clădire de birouri"),
        SHOPPING_CENTER("Centru comercial"),
        MIXED_USE("Utilizare mixtă"),
        WAREHOUSE("Depozit"),
        INDUSTRIAL("Industrial");

        private final String displayName;

        BuildingType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Adaugă un spațiu comercial la clădire.
     *
     * @param space spațiul comercial de adăugat
     */
    public void addSpace(ComercialSpace space) {
        if (spaces == null) {
            spaces = new ArrayList<>();
        }
        spaces.add(space);
        space.setBuilding(this);
    }

    /**
     * Elimină un spațiu comercial din clădire.
     *
     * @param space spațiul comercial de eliminat
     */
    public void removeSpace(ComercialSpace space) {
        if (spaces != null) {
            spaces.remove(space);
            space.setBuilding(null);
        }
    }

    /**
     * Returnează numărul total de spații din clădire.
     *
     * @return numărul de spații
     */
    public int getTotalSpacesCount() {
        return spaces != null ? spaces.size() : 0;
    }

    /**
     * Returnează numărul de spații disponibile din clădire.
     *
     * @return numărul de spații disponibile
     */
    public long getAvailableSpacesCount() {
        return spaces != null ? spaces.stream()
                .filter(space -> space.getAvailable() != null && space.getAvailable())
                .count() : 0;
    }

    /**
     * Verifică dacă clădirea are spații disponibile.
     *
     * @return true dacă există spații disponibile
     */
    public boolean hasAvailableSpaces() {
        return getAvailableSpacesCount() > 0;
    }

    /**
     * Returnează rata de ocupare a clădirii.
     *
     * @return rata de ocupare (0-1)
     */
    public double getOccupancyRate() {
        int totalSpaces = getTotalSpacesCount();
        if (totalSpaces == 0) {
            return 0.0;
        }
        long occupiedSpaces = totalSpaces - getAvailableSpacesCount();
        return (double) occupiedSpaces / totalSpaces;
    }

    /**
     * Verifică dacă clădirea are coordonate geografice.
     *
     * @return true dacă există coordonate
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}