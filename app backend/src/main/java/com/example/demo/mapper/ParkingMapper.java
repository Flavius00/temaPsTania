package com.example.demo.mapper;

import com.example.demo.dto.ParkingDTO;
import com.example.demo.entity.Parking;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper pentru convertirea între entitatea Parking și DTO-urile corespunzătoare.
 *
 * Folosește MapStruct pentru generarea automată a codului de mapping.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar convertirea între Parking și ParkingDTO
 * - Open/Closed: poate fi extins cu noi metode fără modificarea celor existente
 * - Dependency Inversion: depinde de abstracțiuni (interfețe)
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ParkingMapper {

    /**
     * Convertește o entitate Parking într-un ParkingDTO.
     *
     * @param parking entitatea Parking
     * @return ParkingDTO corespunzător
     */
    @Mapping(target = "availableSpots", source = ".", qualifiedByName = "calculateAvailableSpots")
    @Mapping(target = "totalPrice", source = ".", qualifiedByName = "calculateTotalPrice")
    @Mapping(target = "occupancyRate", source = ".", qualifiedByName = "calculateOccupancyRate")
    @Mapping(target = "qualityScore", source = ".", qualifiedByName = "calculateQualityScore")
    ParkingDTO toDTO(Parking parking);

    /**
     * Convertește un ParkingDTO într-o entitate Parking.
     *
     * @param parkingDTO DTO-ul de convertit
     * @return entitatea Parking corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "space", ignore = true)
    @Mapping(target = "reservedSpots", constant = "0") // Implicit 0 locuri rezervate
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Parking toEntity(ParkingDTO parkingDTO);

    /**
     * Convertește o listă de entități Parking într-o listă de ParkingDTO.
     *
     * @param parkings lista de entități
     * @return lista de DTO-uri
     */
    List<ParkingDTO> toDTOList(List<Parking> parkings);

    /**
     * Convertește o entitate Parking într-un ListDTO (pentru listări).
     *
     * @param parking entitatea Parking
     * @return ListDTO corespunzător
     */
    @Mapping(target = "availableSpots", source = ".", qualifiedByName = "calculateAvailableSpots")
    @Mapping(target = "totalPrice", source = ".", qualifiedByName = "calculateTotalPrice")
    @Mapping(target = "occupancyRate", source = ".", qualifiedByName = "calculateOccupancyRate")
    @Mapping(target = "qualityScore", source = ".", qualifiedByName = "calculateQualityScore")
    @Mapping(target = "hasDisabledAccess", source = ".", qualifiedByName = "hasDisabledAccess")
    @Mapping(target = "hasElectricCharging", source = ".", qualifiedByName = "hasElectricCharging")
    ParkingDTO.ListDTO toListDTO(Parking parking);

    /**
     * Convertește o listă de entități Parking într-o listă de ListDTO.
     *
     * @param parkings lista de entități
     * @return lista de ListDTO-uri
     */
    List<ParkingDTO.ListDTO> toListDTOList(List<Parking> parkings);

    /**
     * Convertește un CreateDTO într-o entitate Parking.
     *
     * @param createDTO DTO-ul pentru creare
     * @return entitatea Parking corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "space", ignore = true)
    @Mapping(target = "reservedSpots", constant = "0") // Implicit 0 locuri rezervate
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Parking fromCreateDTO(ParkingDTO.CreateDTO createDTO);

    /**
     * Actualizează o entitate Parking din UpdateDTO.
     *
     * @param updateDTO DTO-ul cu datele de actualizare
     * @param parking entitatea de actualizat
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "space", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ParkingDTO.UpdateDTO updateDTO, @MappingTarget Parking parking);

    /**
     * Convertește o entitate Parking într-un StatsDTO (pentru statistici).
     *
     * @param parking entitatea Parking
     * @return StatsDTO corespunzător
     */
    @Mapping(target = "availableSpots", source = ".", qualifiedByName = "calculateAvailableSpots")
    @Mapping(target = "occupancyRate", source = ".", qualifiedByName = "calculateOccupancyRate")
    @Mapping(target = "totalPrice", source = ".", qualifiedByName = "calculateTotalPrice")
    @Mapping(target = "qualityScore", source = ".", qualifiedByName = "calculateQualityScore")
    @Mapping(target = "hasDisabledAccess", source = ".", qualifiedByName = "hasDisabledAccess")
    @Mapping(target = "hasElectricCharging", source = ".", qualifiedByName = "hasElectricCharging")
    @Mapping(target = "isSecured", source = ".", qualifiedByName = "isSecured")
    ParkingDTO.StatsDTO toStatsDTO(Parking parking);

    /**
     * Calculează numărul de locuri disponibile.
     */
    @Named("calculateAvailableSpots")
    default Integer calculateAvailableSpots(Parking parking) {
        if (parking.getNumberOfSpots() == null) return 0;
        int reserved = parking.getReservedSpots() != null ? parking.getReservedSpots() : 0;
        return Math.max(0, parking.getNumberOfSpots() - reserved);
    }

    /**
     * Calculează prețul total pentru toate locurile.
     */
    @Named("calculateTotalPrice")
    default Double calculateTotalPrice(Parking parking) {
        if (parking.getNumberOfSpots() == null || parking.getPricePerSpot() == null) return 0.0;
        return parking.getNumberOfSpots() * parking.getPricePerSpot();
    }

    /**
     * Calculează rata de ocupare.
     */
    @Named("calculateOccupancyRate")
    default Double calculateOccupancyRate(Parking parking) {
        if (parking.getNumberOfSpots() == null || parking.getNumberOfSpots() == 0) return 0.0;
        int reserved = parking.getReservedSpots() != null ? parking.getReservedSpots() : 0;
        return (double) reserved / parking.getNumberOfSpots();
    }

    /**
     * Calculează scorul de calitate al parcării.
     */
    @Named("calculateQualityScore")
    default Integer calculateQualityScore(Parking parking) {
        int score = 50; // Scor de bază

        if (parking.getCovered() != null && parking.getCovered()) {
            score += 15;
        }

        boolean isSecured = (parking.getSecurityCameras() != null && parking.getSecurityCameras()) ||
                (parking.getSecurityGuard() != null && parking.getSecurityGuard()) ||
                (parking.getAccessCardRequired() != null && parking.getAccessCardRequired());
        if (isSecured) {
            score += 20;
        }

        if (parking.getDisabledAccessSpots() != null && parking.getDisabledAccessSpots() > 0) {
            score += 10;
        }

        if (parking.getElectricChargingSpots() != null && parking.getElectricChargingSpots() > 0) {
            score += 15;
        }

        if (parking.getParkingType() == Parking.ParkingType.UNDERGROUND ||
                parking.getParkingType() == Parking.ParkingType.GARAGE) {
            score += 10;
        }

        return Math.min(100, score);
    }

    /**
     * Verifică dacă parcarea are facilități pentru persoane cu dizabilități.
     */
    @Named("hasDisabledAccess")
    default Boolean hasDisabledAccess(Parking parking) {
        return parking.getDisabledAccessSpots() != null && parking.getDisabledAccessSpots() > 0;
    }

    /**
     * Verifică dacă parcarea are facilități de încărcare electrică.
     */
    @Named("hasElectricCharging")
    default Boolean hasElectricCharging(Parking parking) {
        return parking.getElectricChargingSpots() != null && parking.getElectricChargingSpots() > 0;
    }

    /**
     * Verifică dacă parcarea este securizată.
     */
    @Named("isSecured")
    default Boolean isSecured(Parking parking) {
        return (parking.getSecurityCameras() != null && parking.getSecurityCameras()) ||
                (parking.getSecurityGuard() != null && parking.getSecurityGuard()) ||
                (parking.getAccessCardRequired() != null && parking.getAccessCardRequired());
    }

    /**
     * Validări înainte de creare.
     */
    @BeforeMapping
    default void validateCreateDTO(ParkingDTO.CreateDTO createDTO) {
        if (createDTO.getNumberOfSpots() == null || createDTO.getNumberOfSpots() <= 0) {
            throw new IllegalArgumentException("Numărul de locuri de parcare trebuie să fie pozitiv");
        }
        if (createDTO.getPricePerSpot() != null && createDTO.getPricePerSpot() < 0) {
            throw new IllegalArgumentException("Prețul per loc nu poate fi negativ");
        }
    }

    /**
     * Validări înainte de actualizare.
     */
    @BeforeMapping
    default void validateUpdateDTO(ParkingDTO.UpdateDTO updateDTO) {
        if (updateDTO.getNumberOfSpots() != null && updateDTO.getNumberOfSpots() <= 0) {
            throw new IllegalArgumentException("Numărul de locuri de parcare trebuie să fie pozitiv");
        }
        if (updateDTO.getPricePerSpot() != null && updateDTO.getPricePerSpot() < 0) {
            throw new IllegalArgumentException("Prețul per loc nu poate fi negativ");
        }
        if (updateDTO.getReservedSpots() != null && updateDTO.getReservedSpots() < 0) {
            throw new IllegalArgumentException("Numărul de locuri rezervate nu poate fi negativ");
        }
    }

    /**
     * Setări după mapare pentru actualizarea timestamp-ului.
     */
    @AfterMapping
    default void setUpdatedTimestamp(@MappingTarget Parking parking) {
        parking.setUpdatedAt(java.time.LocalDateTime.now());
    }

    /**
     * Setări după creare pentru valori implicite.
     */
    @AfterMapping
    default void setParkingDefaults(@MappingTarget Parking parking) {
        if (parking.getReservedSpots() == null) {
            parking.setReservedSpots(0);
        }
        if (parking.getPricePerSpot() == null) {
            parking.setPricePerSpot(0.0);
        }
        if (parking.getCovered() == null) {
            parking.setCovered(false);
        }
        if (parking.getSecurityCameras() == null) {
            parking.setSecurityCameras(false);
        }
        if (parking.getSecurityGuard() == null) {
            parking.setSecurityGuard(false);
        }
        if (parking.getAccessCardRequired() == null) {
            parking.setAccessCardRequired(false);
        }
    }
}