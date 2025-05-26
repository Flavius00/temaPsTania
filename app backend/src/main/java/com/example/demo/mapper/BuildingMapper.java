package com.example.demo.mapper;

import com.example.demo.dto.BuildingDTO;
import com.example.demo.entity.Building;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper pentru convertirea între entitatea Building și DTO-urile corespunzătoare.
 *
 * Folosește MapStruct pentru generarea automată a codului de mapping.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar convertirea între Building și BuildingDTO
 * - Open/Closed: poate fi extins cu noi metode fără modificarea celor existente
 * - Dependency Inversion: depinde de abstracțiuni (interfețe)
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ComercialSpaceMapper.class}
)
public interface BuildingMapper {

    /**
     * Convertește o entitate Building într-un BuildingDTO.
     *
     * @param building entitatea Building
     * @return BuildingDTO corespunzător
     */
    @Mapping(target = "totalSpaces", source = "spaces", qualifiedByName = "calculateTotalSpaces")
    @Mapping(target = "availableSpaces", source = "spaces", qualifiedByName = "calculateAvailableSpaces")
    @Mapping(target = "occupiedSpaces", source = "spaces", qualifiedByName = "calculateOccupiedSpaces")
    @Mapping(target = "occupancyRate", source = "spaces", qualifiedByName = "calculateOccupancyRate")
    @Mapping(target = "averageRentPerSpace", source = "spaces", qualifiedByName = "calculateAverageRent")
    @Mapping(target = "totalRevenue", source = "spaces", qualifiedByName = "calculateTotalRevenue")
    @Mapping(target = "spaces", source = "spaces", qualifiedByName = "mapSpacesToSummary")
    BuildingDTO toDTO(Building building);

    /**
     * Convertește un BuildingDTO într-o entitate Building.
     *
     * @param buildingDTO DTO-ul de convertit
     * @return entitatea Building corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Building toEntity(BuildingDTO buildingDTO);

    /**
     * Convertește o listă de entități Building într-o listă de BuildingDTO.
     *
     * @param buildings lista de entități
     * @return lista de DTO-uri
     */
    List<BuildingDTO> toDTOList(List<Building> buildings);

    /**
     * Convertește o entitate Building într-un ListDTO (pentru listări).
     *
     * @param building entitatea Building
     * @return ListDTO corespunzător
     */
    @Mapping(target = "totalSpaces", source = "spaces", qualifiedByName = "calculateTotalSpaces")
    @Mapping(target = "availableSpaces", source = "spaces", qualifiedByName = "calculateAvailableSpaces")
    @Mapping(target = "occupancyRate", source = "spaces", qualifiedByName = "calculateOccupancyRate")
    @Mapping(target = "qualityScore", source = ".", qualifiedByName = "calculateQualityScore")
    BuildingDTO.ListDTO toListDTO(Building building);

    /**
     * Convertește o listă de entități Building într-o listă de ListDTO.
     *
     * @param buildings lista de entități
     * @return lista de ListDTO-uri
     */
    List<BuildingDTO.ListDTO> toListDTOList(List<Building> buildings);

    /**
     * Convertește un CreateDTO într-o entitate Building.
     *
     * @param createDTO DTO-ul pentru creare
     * @return entitatea Building corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Building fromCreateDTO(BuildingDTO.CreateDTO createDTO);

    /**
     * Actualizează o entitate Building din UpdateDTO.
     *
     * @param updateDTO DTO-ul cu datele de actualizare
     * @param building entitatea de actualizat
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(BuildingDTO.UpdateDTO updateDTO, @MappingTarget Building building);

    /**
     * Convertește o entitate Building într-un MapDTO (pentru mapă).
     *
     * @param building entitatea Building
     * @return MapDTO corespunzător
     */
    @Mapping(target = "availableSpaces", source = "spaces", qualifiedByName = "calculateAvailableSpaces")
    @Mapping(target = "totalSpaces", source = "spaces", qualifiedByName = "calculateTotalSpaces")
    @Mapping(target = "occupancyRate", source = "spaces", qualifiedByName = "calculateOccupancyRate")
    @Mapping(target = "qualityScore", source = ".", qualifiedByName = "calculateQualityScore")
    BuildingDTO.MapDTO toMapDTO(Building building);

    /**
     * Convertește o listă de entități Building într-o listă de MapDTO.
     *
     * @param buildings lista de entități
     * @return lista de MapDTO-uri
     */
    List<BuildingDTO.MapDTO> toMapDTOList(List<Building> buildings);

    /**
     * Convertește o entitate Building într-un StatsDTO (pentru statistici).
     *
     * @param building entitatea Building
     * @return StatsDTO corespunzător
     */
    @Mapping(target = "totalSpaces", source = "spaces", qualifiedByName = "calculateTotalSpaces")
    @Mapping(target = "availableSpaces", source = "spaces", qualifiedByName = "calculateAvailableSpaces")
    @Mapping(target = "occupiedSpaces", source = "spaces", qualifiedByName = "calculateOccupiedSpaces")
    @Mapping(target = "occupancyRate", source = "spaces", qualifiedByName = "calculateOccupancyRate")
    @Mapping(target = "averageRentPerSpace", source = "spaces", qualifiedByName = "calculateAverageRent")
    @Mapping(target = "totalRevenue", source = "spaces", qualifiedByName = "calculateTotalRevenue")
    @Mapping(target = "activeContracts", source = "spaces", qualifiedByName = "calculateActiveContracts")
    @Mapping(target = "averageSpaceSize", source = "spaces", qualifiedByName = "calculateAverageSpaceSize")
    @Mapping(target = "age", source = ".", qualifiedByName = "calculateAge")
    @Mapping(target = "qualityScore", source = ".", qualifiedByName = "calculateQualityScore")
    BuildingDTO.StatsDTO toStatsDTO(Building building);

    /**
     * Calculează numărul total de spații.
     */
    @Named("calculateTotalSpaces")
    default Integer calculateTotalSpaces(List<com.example.demo.entity.ComercialSpace> spaces) {
        return spaces != null ? spaces.size() : 0;
    }

    /**
     * Calculează numărul de spații disponibile.
     */
    @Named("calculateAvailableSpaces")
    default Integer calculateAvailableSpaces(List<com.example.demo.entity.ComercialSpace> spaces) {
        if (spaces == null) return 0;
        return (int) spaces.stream()
                .filter(space -> space.getAvailable() != null && space.getAvailable())
                .count();
    }

    /**
     * Calculează numărul de spații ocupate.
     */
    @Named("calculateOccupiedSpaces")
    default Integer calculateOccupiedSpaces(List<com.example.demo.entity.ComercialSpace> spaces) {
        if (spaces == null) return 0;
        return (int) spaces.stream()
                .filter(space -> space.getAvailable() != null && !space.getAvailable())
                .count();
    }

    /**
     * Calculează rata de ocupare.
     */
    @Named("calculateOccupancyRate")
    default Double calculateOccupancyRate(List<com.example.demo.entity.ComercialSpace> spaces) {
        if (spaces == null || spaces.isEmpty()) return 0.0;
        long occupied = spaces.stream()
                .filter(space -> space.getAvailable() != null && !space.getAvailable())
                .count();
        return (double) occupied / spaces.size();
    }

    /**
     * Calculează chiria medie per spațiu.
     */
    @Named("calculateAverageRent")
    default Double calculateAverageRent(List<com.example.demo.entity.ComercialSpace> spaces) {
        if (spaces == null || spaces.isEmpty()) return 0.0;
        return spaces.stream()
                .filter(space -> space.getPricePerMonth() != null)
                .mapToDouble(com.example.demo.entity.ComercialSpace::getPricePerMonth)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculează venitul total.
     */
    @Named("calculateTotalRevenue")
    default Double calculateTotalRevenue(List<com.example.demo.entity.ComercialSpace> spaces) {
        if (spaces == null) return 0.0;
        return spaces.stream()
                .filter(space -> space.getAvailable() != null && !space.getAvailable())
                .mapToDouble(space -> space.getPricePerMonth() != null ? space.getPricePerMonth() : 0.0)
                .sum();
    }

    /**
     * Calculează numărul de contracte active.
     */
    @Named("calculateActiveContracts")
    default Integer calculateActiveContracts(List<com.example.demo.entity.ComercialSpace> spaces) {
        if (spaces == null) return 0;
        return (int) spaces.stream()
                .flatMap(space -> space.getContracts() != null ? space.getContracts().stream() : java.util.stream.Stream.empty())
                .filter(contract -> "ACTIVE".equals(contract.getStatus()))
                .count();
    }

    /**
     * Calculează suprafața medie a spațiilor.
     */
    @Named("calculateAverageSpaceSize")
    default Double calculateAverageSpaceSize(List<com.example.demo.entity.ComercialSpace> spaces) {
        if (spaces == null || spaces.isEmpty()) return 0.0;
        return spaces.stream()
                .filter(space -> space.getArea() != null)
                .mapToDouble(com.example.demo.entity.ComercialSpace::getArea)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculează vârsta clădirii.
     */
    @Named("calculateAge")
    default Integer calculateAge(Building building) {
        if (building.getYearBuilt() == null) return null;
        return java.time.Year.now().getValue() - building.getYearBuilt();
    }

    /**
     * Calculează scorul de calitate al clădirii.
     */
    @Named("calculateQualityScore")
    default Integer calculateQualityScore(Building building) {
        int score = 50; // Scor de bază

        // Verifică dacă este modernă (construită după 2000)
        if (building.getYearBuilt() != null && building.getYearBuilt() >= 2000) {
            score += 15;
        }

        if (building.getElevatorAvailable() != null && building.getElevatorAvailable()) {
            score += 10;
        }

        if (building.getAirConditioning() != null && building.getAirConditioning()) {
            score += 10;
        }

        if (building.getSecuritySystem() != null && building.getSecuritySystem()) {
            score += 10;
        }

        if (building.getAccessibilityFeatures() != null && building.getAccessibilityFeatures()) {
            score += 5;
        }

        if (building.getParkingSpots() != null && building.getParkingSpots() > 0) {
            score += 10;
        }

        return Math.min(100, score);
    }

    /**
     * Mapează spațiile la format sumar.
     */
    @Named("mapSpacesToSummary")
    default List<com.example.demo.dto.ComercialSpaceDTO.SummaryDTO> mapSpacesToSummary(List<com.example.demo.entity.ComercialSpace> spaces) {
        if (spaces == null) return null;
        // Această metodă va fi implementată prin ComercialSpaceMapper
        return null; // MapStruct va gestiona automat mapping-ul
    }

    /**
     * Validări înainte de creare.
     */
    @BeforeMapping
    default void validateCreateDTO(BuildingDTO.CreateDTO createDTO) {
        if (createDTO.getName() == null || createDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Numele clădirii este obligatoriu");
        }
        if (createDTO.getAddress() == null || createDTO.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Adresa este obligatorie");
        }
    }

    /**
     * Setări după mapare pentru actualizarea timestamp-ului.
     */
    @AfterMapping
    default void setUpdatedTimestamp(@MappingTarget Building building) {
        building.setUpdatedAt(java.time.LocalDateTime.now());
    }

    /**
     * Verifică dacă clădirea are coordonate geografice.
     */
    default boolean hasCoordinates(Building building) {
        return building.getLatitude() != null && building.getLongitude() != null;
    }

    /**
     * Verifică dacă clădirea are facilități premium.
     */
    default boolean hasPremiumFeatures(Building building) {
        return (building.getElevatorAvailable() != null && building.getElevatorAvailable()) &&
                (building.getAirConditioning() != null && building.getAirConditioning()) &&
                (building.getSecuritySystem() != null && building.getSecuritySystem()) &&
                (building.getAccessibilityFeatures() != null && building.getAccessibilityFeatures());
    }
}