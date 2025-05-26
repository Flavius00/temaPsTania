package com.example.demo.entity;

/**
 * Enum pentru tipurile de spații comerciale disponibile în sistem.
 *
 * Definește tipurile principale de spații comerciale cu numele lor
 * de afișare pentru interfața de utilizator.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar definirea tipurilor de spații
 * - Open/Closed: noi tipuri pot fi adăugate fără modificarea codului existent
 */
public enum SpaceType {
    OFFICE("Birou", "Spațiu destinat activităților de birou"),
    RETAIL("Spațiu comercial", "Spațiu destinat vânzării cu amănuntul"),
    WAREHOUSE("Depozit", "Spațiu destinat depozitării și logisticii"),
    RESTAURANT("Restaurant", "Spațiu destinat serviciilor de alimentație"),
    INDUSTRIAL("Industrial", "Spațiu destinat producției industriale"),
    MEDICAL("Medical", "Spațiu destinat serviciilor medicale"),
    EDUCATIONAL("Educațional", "Spațiu destinat activităților educaționale"),
    RECREATIONAL("Recreațional", "Spațiu destinat activităților recreaționale");

    private final String displayName;
    private final String description;

    /**
     * Constructor pentru enum-ul SpaceType.
     *
     * @param displayName numele afișat în interfață
     * @param description descrierea tipului de spațiu
     */
    SpaceType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Returnează numele afișat pentru tipul de spațiu.
     *
     * @return numele afișat
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returnează descrierea tipului de spațiu.
     *
     * @return descrierea
     */
    public String getDescription() {
        return description;
    }

    /**
     * Caută un tip de spațiu după numele afișat.
     *
     * @param displayName numele afișat căutat
     * @return tipul de spațiu găsit sau null
     */
    public static SpaceType findByDisplayName(String displayName) {
        for (SpaceType type : values()) {
            if (type.displayName.equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Verifică dacă tipul de spațiu necesită facilități speciale.
     *
     * @return true dacă necesită facilități speciale
     */
    public boolean requiresSpecialFacilities() {
        return this == RESTAURANT || this == MEDICAL || this == INDUSTRIAL;
    }

    /**
     * Returnează categoria generală a tipului de spațiu.
     *
     * @return categoria generală
     */
    public SpaceCategory getCategory() {
        switch (this) {
            case OFFICE:
            case EDUCATIONAL:
                return SpaceCategory.PROFESSIONAL;
            case RETAIL:
            case RESTAURANT:
                return SpaceCategory.COMMERCIAL;
            case WAREHOUSE:
            case INDUSTRIAL:
                return SpaceCategory.INDUSTRIAL;
            case MEDICAL:
            case RECREATIONAL:
                return SpaceCategory.SPECIALIZED;
            default:
                return SpaceCategory.OTHER;
        }
    }

    /**
     * Enum pentru categoriile generale de spații.
     */
    public enum SpaceCategory {
        PROFESSIONAL("Profesional"),
        COMMERCIAL("Comercial"),
        INDUSTRIAL("Industrial"),
        SPECIALIZED("Specializat"),
        OTHER("Altele");

        private final String displayName;

        SpaceCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}