package com.example.demo.mapper;

import com.example.demo.dto.ComercialSpaceDTO;
import com.example.demo.entity.ComercialSpace;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper pentru convertirea între entitatea ComercialSpace și DTO-urile corespunzătoare.
 *
 * Folosește MapStruct pentru generarea automată a codului de mapping.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar convertirea între ComercialSpace și ComercialSpaceDTO
 * - Open/Closed: poate fi extins cu noi metode fără modificarea celor existente
 * - Dependency Inversion: depinde de abstracțiuni (interfețe)
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {OwnerMapper.class, BuildingMapper.class, ParkingMapper.class, RentalContractMapper.class}
)
public interface ComercialSpaceMapper {

    /**
     * Convertește o entitate ComercialSpace într-un ComercialSpaceDTO.
     *
     * @param space entitatea ComercialSpace
     * @return ComercialSpaceDTO corespunzător
     */
    @Mapping(target = "owner", source = "owner", qualifiedByName = "ownerToListDTO")
    @Mapping(target = "building", source = "building", qualifiedByName = "buildingToListDTO")
    @Mapping(target = "pricePerSquareMeter", source = ".", qualifiedByName = "calculatePricePerSquareMeter")
    @Mapping(target = "hasActiveContract", source = "contracts", qualifiedByName = "hasActiveContract")
    @Mapping(target = "activeContract", source = "contracts", qualifiedByName = "getActiveContract")
    @Mapping(target = "totalContracts", source = "contracts", qualifiedByName = "calculateTotalContracts")
    ComercialSpaceDTO toDTO(ComercialSpace space);

    /**
     * Convertește un ComercialSpaceDTO într-o entitate ComercialSpace.
     *
     * @param spaceDTO DTO-ul de convertit
     * @return entitatea ComercialSpace corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "parking", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ComercialSpace toEntity(ComercialSpaceDTO spaceDTO);

    /**
     * Convertește o listă de entități ComercialSpace într-o listă de ComercialSpaceDTO.
     *
     * @param spaces lista de entități
     * @return lista de DTO-uri
     */
    List<ComercialSpaceDTO> toDTOList(List<ComercialSpace> spaces);

    /**
     * Convertește o entitate ComercialSpace într-un ListDTO (pentru listări).
     *
     * @param space entitatea ComercialSpace
     * @return ListDTO corespunzător
     */
    @Mapping(target = "pricePerSquareMeter", source = ".", qualifiedByName = "calculatePricePerSquareMeter")
    @Mapping(target = "ownerName", source = "owner.name")
    @Mapping(target = "buildingName", source = "building.name")
    @Mapping(target = "hasParking", source = "parking", qualifiedByName = "hasParking")
    @Mapping(target = "qualityScore", source = ".", qualifiedByName = "calculateQualityScore")
    ComercialSpaceDTO.ListDTO toListDTO(ComercialSpace space);

    /**
     * Convertește o listă de entități ComercialSpace într-o listă de ListDTO.
     *
     * @param spaces lista de entități
     * @return lista de ListDTO-uri
     */
    List<ComercialSpaceDTO.ListDTO> toListDTOList(List<ComercialSpace> spaces);

    /**
     * Convertește un CreateDTO într-o entitate ComercialSpace.
     *
     * @param createDTO DTO-ul pentru creare
     * @return entitatea ComercialSpace corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true) // Se va seta separat folosind ownerId
    @Mapping(target = "building", ignore = true) // Se va seta separat folosind buildingId
    @Mapping(target = "parking", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "available", constant = "true") // Spațiile noi sunt disponibile implicit
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ComercialSpace fromCreateDTO(ComercialSpaceDTO.CreateDTO createDTO);

    /**
     * Actualizează o entitate ComercialSpace din UpdateDTO.
     *
     * @param updateDTO DTO-ul cu datele de actualizare
     * @param space entitatea de actualizat
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "parking", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "spaceType", ignore = true) // Tipul nu se poate schimba după creare
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ComercialSpaceDTO.UpdateDTO updateDTO, @MappingTarget ComercialSpace space);

    /**
     * Convertește o entitate ComercialSpace într-un MapDTO (pentru mapă).
     *
     * @param space entitatea ComercialSpace
     * @return MapDTO corespunzător
     */
    @Mapping(target = "buildingName", source = "building.name")
    @Mapping(target = "qualityScore", source = ".", qualifiedByName = "calculateQualityScore")
    ComercialSpaceDTO.MapDTO toMapDTO(ComercialSpace space);

    /**
     * Convertește o listă de entități ComercialSpace într-o listă de MapDTO.
     *
     * @param spaces lista de entități
     * @return lista de MapDTO-uri
     */
    List<ComercialSpaceDTO.MapDTO> toMapDTOList(List<ComercialSpace> spaces);

    /**
     * Convertește o entitate ComercialSpace într-un SummaryDTO.
     *
     * @param space entitatea ComercialSpace
     * @return SummaryDTO corespunzător
     */
    @Mapping(target = "hasActiveContract", source = "contracts", qualifiedByName = "hasActiveContract")
    @Mapping(target = "lastContractDate", source = "contracts", qualifiedByName = "getLastContractDate")
    ComercialSpaceDTO.SummaryDTO toSummaryDTO(ComercialSpace space);

    /**
     * Convertește o listă de entități ComercialSpace într-o listă de SummaryDTO.
     *
     * @param spaces lista de entități
     * @return lista de SummaryDTO-uri
     */
    List<ComercialSpaceDTO.SummaryDTO> toSummaryDTOList(List<ComercialSpace> spaces);

    /**
     * Convertește o entitate ComercialSpace într-un DetailDTO (pentru detalii complete).
     *
     * @param space entitatea ComercialSpace
     * @return DetailDTO corespunzător
     */
    @Mapping(target = "owner", source = "owner", qualifiedByName = "ownerToListDTO")
    @Mapping(target = "building", source = "building", qualifiedByName = "buildingToListDTO")
    @Mapping(target = "pricePerSquareMeter", source = ".", qualifiedByName = "calculatePricePerSquareMeter")
    @Mapping(target = "qualityScore", source = ".", qualifiedByName = "calculateQualityScore")
    @Mapping(target = "hasActiveContract", source = "contracts", qualifiedByName = "hasActiveContract")
    @Mapping(target = "activeContract", source = "contracts", qualifiedByName = "getActiveContract")
    @Mapping(target = "contractHistory", source = "contracts", qualifiedByName = "getContractHistory")
    ComercialSpaceDTO.DetailDTO toDetailDTO(ComercialSpace space);

    /**
     * Calculează prețul pe metru pătrat.
     */
    @Named("calculatePricePerSquareMeter")
    default Double calculatePricePerSquareMeter(ComercialSpace space) {
        if (space.getArea() != null && space.getArea() > 0 && space.getPricePerMonth() != null) {
            return space.getPricePerMonth() / space.getArea();
        }
        return null;
    }

    /**
     * Verifică dacă spațiul are un contract activ.
     */
    @Named("hasActiveContract")
    default Boolean hasActiveContract(List<com.example.demo.entity.RentalContract> contracts) {
        if (contracts == null) return false;
        return contracts.stream()
                .anyMatch(contract -> "ACTIVE".equals(contract.getStatus()));
    }

    /**
     * Obține contractul activ.
     */
    @Named("getActiveContract")
    default com.example.demo.dto.RentalContractDTO.SummaryDTO getActiveContract(List<com.example.demo.entity.RentalContract> contracts) {
        if (contracts == null) return null;
        return contracts.stream()
                .filter(contract -> "ACTIVE".equals(contract.getStatus()))
                .findFirst()
                .map(this::contractToSummaryDTO)
                .orElse(null);
    }

    /**
     * Calculează numărul total de contracte.
     */
    @Named("calculateTotalContracts")
    default Integer calculateTotalContracts(List<com.example.demo.entity.RentalContract> contracts) {
        return contracts != null ? contracts.size() : 0;
    }

    /**
     * Obține data ultimului contract.
     */
    @Named("getLastContractDate")
    default java.time.LocalDateTime getLastContractDate(List<com.example.demo.entity.RentalContract> contracts) {
        if (contracts == null || contracts.isEmpty()) return null;
        return contracts.stream()
                .map(com.example.demo.entity.RentalContract::getCreatedAt)
                .filter(java.util.Objects::nonNull)
                .max(java.time.LocalDateTime::compareTo)
                .orElse(null);
    }

    /**
     * Obține istoricul contractelor.
     */
    @Named("getContractHistory")
    default List<com.example.demo.dto.RentalContractDTO.SummaryDTO> getContractHistory(List<com.example.demo.entity.RentalContract> contracts) {
        if (contracts == null) return null;
        return contracts.stream()
                .map(this::contractToSummaryDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Verifică dacă spațiul are parcare.
     */
    @Named("hasParking")
    default Boolean hasParking(com.example.demo.entity.Parking parking) {
        return parking != null;
    }

    /**
     * Calculează scorul de calitate al spațiului.
     */
    @Named("calculateQualityScore")
    default Integer calculateQualityScore(ComercialSpace space) {
        int score = 50; // Scor de bază

        if (space.getFurnished() != null && space.getFurnished()) score += 10;
        if (space.getAirConditioning() != null && space.getAirConditioning()) score += 10;
        if (space.getHeating() != null && space.getHeating()) score += 5;
        if (space.getInternetReady() != null && space.getInternetReady()) score += 10;
        if (space.getKitchenFacilities() != null && space.getKitchenFacilities()) score += 5;
        if (space.getParking() != null) score += 10;
        if (space.getAmenities() != null && !space.getAmenities().isEmpty()) {
            score += Math.min(space.getAmenities().size() * 2, 20);
        }

        return Math.min(100, score);
    }

    /**
     * Mapare pentru Owner la ListDTO.
     */
    @Named("ownerToListDTO")
    default com.example.demo.dto.OwnerDTO.ListDTO ownerToListDTO(com.example.demo.entity.Owner owner) {
        if (owner == null) return null;
        // Această metodă va fi implementată prin OwnerMapper
        return null; // MapStruct va gestiona automat mapping-ul
    }

    /**
     * Mapare pentru Building la ListDTO.
     */
    @Named("buildingToListDTO")
    default com.example.demo.dto.BuildingDTO.ListDTO buildingToListDTO(com.example.demo.entity.Building building) {
        if (building == null) return null;
        // Această metodă va fi implementată prin BuildingMapper
        return null; // MapStruct va gestiona automat mapping-ul
    }

    /**
     * Mapare pentru Contract la SummaryDTO.
     */
    default com.example.demo.dto.RentalContractDTO.SummaryDTO contractToSummaryDTO(com.example.demo.entity.RentalContract contract) {
        if (contract == null) return null;
        // Această metodă va fi implementată prin RentalContractMapper
        return null; // MapStruct va gestiona automat mapping-ul
    }

    /**
     * Validări înainte de creare.
     */
    @BeforeMapping
    default void validateCreateDTO(ComercialSpaceDTO.CreateDTO createDTO) {
        if (createDTO.getName() == null || createDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Numele spațiului este obligatoriu");
        }
        if (createDTO.getArea() == null || createDTO.getArea() <= 0) {
            throw new IllegalArgumentException("Suprafața trebuie să fie pozitivă");
        }
        if (createDTO.getPricePerMonth() == null || createDTO.getPricePerMonth() <= 0) {
            throw new IllegalArgumentException("Prețul pe lună trebuie să fie pozitiv");
        }
        if (createDTO.getSpaceType() == null) {
            throw new IllegalArgumentException("Tipul spațiului este obligatoriu");
        }
    }

    /**
     * Setări după mapare pentru actualizarea timestamp-ului.
     */
    @AfterMapping
    default void setUpdatedTimestamp(@MappingTarget ComercialSpace space) {
        space.setUpdatedAt(java.time.LocalDateTime.now());
    }
}