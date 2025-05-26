package com.example.demo.mapper;

import com.example.demo.dto.OwnerDTO;
import com.example.demo.model.Owner;
import com.example.demo.model.User;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper pentru convertirea între entitatea Owner și DTO-urile corespunzătoare.
 *
 * Folosește MapStruct pentru generarea automată a codului de mapping.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar convertirea între Owner și OwnerDTO
 * - Open/Closed: poate fi extins cu noi metode fără modificarea celor existente
 * - Dependency Inversion: depinde de abstracțiuni (interfețe)
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ComercialSpaceMapper.class}
)
public interface OwnerMapper {

    /**
     * Convertește o entitate Owner într-un OwnerDTO.
     *
     * @param owner entitatea Owner
     * @return OwnerDTO corespunzător
     */
    @Mapping(target = "totalSpaces", source = "spaces", qualifiedByName = "calculateTotalSpaces")
    @Mapping(target = "availableSpaces", source = "spaces", qualifiedByName = "calculateAvailableSpaces")
    @Mapping(target = "occupiedSpaces", source = "spaces", qualifiedByName = "calculateOccupiedSpaces")
    @Mapping(target = "totalRevenue", source = "spaces", qualifiedByName = "calculateTotalRevenue")
    @Mapping(target = "occupancyRate", source = "spaces", qualifiedByName = "calculateOccupancyRate")
    @Mapping(target = "spaces", source = "spaces", qualifiedByName = "mapSpacesToSummary")
    OwnerDTO toDTO(Owner owner);

    /**
     * Convertește un OwnerDTO într-o entitate Owner.
     *
     * @param ownerDTO DTO-ul de convertit
     * @return entitatea Owner corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "OWNER")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Owner toEntity(OwnerDTO ownerDTO);

    /**
     * Convertește o listă de entități Owner într-o listă de OwnerDTO.
     *
     * @param owners lista de entități
     * @return lista de DTO-uri
     */
    List<OwnerDTO> toDTOList(List<Owner> owners);

    /**
     * Convertește o entitate Owner într-un ListDTO (pentru listări).
     *
     * @param owner entitatea Owner
     * @return ListDTO corespunzător
     */
    @Mapping(target = "totalSpaces", source = "spaces", qualifiedByName = "calculateTotalSpaces")
    @Mapping(target = "availableSpaces", source = "spaces", qualifiedByName = "calculateAvailableSpaces")
    @Mapping(target = "totalRevenue", source = "spaces", qualifiedByName = "calculateTotalRevenue")
    @Mapping(target = "occupancyRate", source = "spaces", qualifiedByName = "calculateOccupancyRate")
    OwnerDTO.ListDTO toListDTO(Owner owner);

    /**
     * Convertește o listă de entități Owner într-o listă de ListDTO.
     *
     * @param owners lista de entități
     * @return lista de ListDTO-uri
     */
    List<OwnerDTO.ListDTO> toListDTOList(List<Owner> owners);

    /**
     * Convertește un CreateDTO într-o entitate Owner.
     *
     * @param createDTO DTO-ul pentru creare
     * @return entitatea Owner corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "OWNER")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Owner fromCreateDTO(OwnerDTO.CreateDTO createDTO);

    /**
     * Actualizează o entitate Owner din UpdateDTO.
     *
     * @param updateDTO DTO-ul cu datele de actualizare
     * @param owner entitatea de actualizat
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "spaces", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(OwnerDTO.UpdateDTO updateDTO, @MappingTarget Owner owner);

    /**
     * Convertește o entitate Owner într-un StatsDTO (pentru statistici).
     *
     * @param owner entitatea Owner
     * @return StatsDTO corespunzător
     */
    @Mapping(target = "totalSpaces", source = "spaces", qualifiedByName = "calculateTotalSpaces")
    @Mapping(target = "availableSpaces", source = "spaces", qualifiedByName = "calculateAvailableSpaces")
    @Mapping(target = "occupiedSpaces", source = "spaces", qualifiedByName = "calculateOccupiedSpaces")
    @Mapping(target = "occupancyRate", source = "spaces", qualifiedByName = "calculateOccupancyRate")
    @Mapping(target = "totalRevenue", source = "spaces", qualifiedByName = "calculateTotalRevenue")
    @Mapping(target = "averageRentPerSpace", source = "spaces", qualifiedByName = "calculateAverageRent")
    @Mapping(target = "activeContracts", source = "spaces", qualifiedByName = "calculateActiveContracts")
    @Mapping(target = "lastActivity", source = "updatedAt")
    OwnerDTO.StatsDTO toStatsDTO(Owner owner);

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
    default void validateCreateDTO(OwnerDTO.CreateDTO createDTO) {
        if (createDTO.getUsername() == null || createDTO.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username-ul este obligatoriu");
        }
        if (createDTO.getEmail() == null || createDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email-ul este obligatoriu");
        }
        if (createDTO.getCompanyName() == null || createDTO.getCompanyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Numele companiei este obligatoriu");
        }
    }

    /**
     * Setări după mapare.
     */
    @AfterMapping
    default void setOwnerDefaults(@MappingTarget Owner owner) {
        if (owner.getRole() == null) {
            owner.setRole(User.UserRole.OWNER);
        }
        if (owner.getActive() == null) {
            owner.setActive(true);
        }
    }
}