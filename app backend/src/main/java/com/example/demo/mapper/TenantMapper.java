package com.example.demo.mapper;

import com.example.demo.dto.TenantDTO;
import com.example.demo.entity.Tenant;
import com.example.demo.entity.User;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper pentru convertirea între entitatea Tenant și DTO-urile corespunzătoare.
 *
 * Folosește MapStruct pentru generarea automată a codului de mapping.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar convertirea între Tenant și TenantDTO
 * - Open/Closed: poate fi extins cu noi metode fără modificarea celor existente
 * - Dependency Inversion: depinde de abstracțiuni (interfețe)
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {RentalContractMapper.class}
)
public interface TenantMapper {

    /**
     * Convertește o entitate Tenant într-un TenantDTO.
     *
     * @param tenant entitatea Tenant
     * @return TenantDTO corespunzător
     */
    @Mapping(target = "totalContracts", source = "contracts", qualifiedByName = "calculateTotalContracts")
    @Mapping(target = "activeContracts", source = "contracts", qualifiedByName = "calculateActiveContracts")
    @Mapping(target = "totalMonthlyExpenses", source = "contracts", qualifiedByName = "calculateTotalExpenses")
    @Mapping(target = "averageRentPaid", source = "contracts", qualifiedByName = "calculateAverageRent")
    @Mapping(target = "contracts", source = "contracts", qualifiedByName = "mapContractsToSummary")
    TenantDTO toDTO(Tenant tenant);

    /**
     * Convertește un TenantDTO într-o entitate Tenant.
     *
     * @param tenantDTO DTO-ul de convertit
     * @return entitatea Tenant corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "TENANT")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Tenant toEntity(TenantDTO tenantDTO);

    /**
     * Convertește o listă de entități Tenant într-o listă de TenantDTO.
     *
     * @param tenants lista de entități
     * @return lista de DTO-uri
     */
    List<TenantDTO> toDTOList(List<Tenant> tenants);

    /**
     * Convertește o entitate Tenant într-un ListDTO (pentru listări).
     *
     * @param tenant entitatea Tenant
     * @return ListDTO corespunzător
     */
    @Mapping(target = "activeContracts", source = "contracts", qualifiedByName = "calculateActiveContracts")
    @Mapping(target = "totalMonthlyExpenses", source = "contracts", qualifiedByName = "calculateTotalExpenses")
    TenantDTO.ListDTO toListDTO(Tenant tenant);

    /**
     * Convertește o listă de entități Tenant într-o listă de ListDTO.
     *
     * @param tenants lista de entități
     * @return lista de ListDTO-uri
     */
    List<TenantDTO.ListDTO> toListDTOList(List<Tenant> tenants);

    /**
     * Convertește un CreateDTO într-o entitate Tenant.
     *
     * @param createDTO DTO-ul pentru creare
     * @return entitatea Tenant corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "TENANT")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Tenant fromCreateDTO(TenantDTO.CreateDTO createDTO);

    /**
     * Actualizează o entitate Tenant din UpdateDTO.
     *
     * @param updateDTO DTO-ul cu datele de actualizare
     * @param tenant entitatea de actualizat
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(TenantDTO.UpdateDTO updateDTO, @MappingTarget Tenant tenant);

    /**
     * Convertește o entitate Tenant într-un SearchCriteriaDTO.
     *
     * @param tenant entitatea Tenant
     * @return SearchCriteriaDTO corespunzător
     */
    @Mapping(target = "preferredLocation", ignore = true)
    @Mapping(target = "requiredAmenities", ignore = true)
    TenantDTO.SearchCriteriaDTO toSearchCriteriaDTO(Tenant tenant);

    /**
     * Convertește o entitate Tenant într-un StatsDTO (pentru statistici).
     *
     * @param tenant entitatea Tenant
     * @return StatsDTO corespunzător
     */
    @Mapping(target = "totalContracts", source = "contracts", qualifiedByName = "calculateTotalContracts")
    @Mapping(target = "activeContracts", source = "contracts", qualifiedByName = "calculateActiveContracts")
    @Mapping(target = "totalMonthlyExpenses", source = "contracts", qualifiedByName = "calculateTotalExpenses")
    @Mapping(target = "averageRentPaid", source = "contracts", qualifiedByName = "calculateAverageRent")
    @Mapping(target = "budgetUtilization", source = ".", qualifiedByName = "calculateBudgetUtilization")
    @Mapping(target = "lastContractDate", source = "contracts", qualifiedByName = "getLastContractDate")
    @Mapping(target = "contractsThisYear", source = "contracts", qualifiedByName = "calculateContractsThisYear")
    TenantDTO.StatsDTO toStatsDTO(Tenant tenant);

    /**
     * Calculează numărul total de contracte.
     */
    @Named("calculateTotalContracts")
    default Integer calculateTotalContracts(List<com.example.demo.entity.RentalContract> contracts) {
        return contracts != null ? contracts.size() : 0;
    }

    /**
     * Calculează numărul de contracte active.
     */
    @Named("calculateActiveContracts")
    default Integer calculateActiveContracts(List<com.example.demo.entity.RentalContract> contracts) {
        if (contracts == null) return 0;
        return (int) contracts.stream()
                .filter(contract -> "ACTIVE".equals(contract.getStatus()))
                .count();
    }

    /**
     * Calculează cheltuielile totale lunare.
     */
    @Named("calculateTotalExpenses")
    default Double calculateTotalExpenses(List<com.example.demo.entity.RentalContract> contracts) {
        if (contracts == null) return 0.0;
        return contracts.stream()
                .filter(contract -> "ACTIVE".equals(contract.getStatus()))
                .mapToDouble(contract -> contract.getMonthlyRent() != null ? contract.getMonthlyRent() : 0.0)
                .sum();
    }

    /**
     * Calculează chiria medie plătită.
     */
    @Named("calculateAverageRent")
    default Double calculateAverageRent(List<com.example.demo.entity.RentalContract> contracts) {
        if (contracts == null || contracts.isEmpty()) return 0.0;
        return contracts.stream()
                .filter(contract -> contract.getMonthlyRent() != null)
                .mapToDouble(com.example.demo.entity.RentalContract::getMonthlyRent)
                .average()
                .orElse(0.0);
    }

    /**
     * Calculează utilizarea bugetului.
     */
    @Named("calculateBudgetUtilization")
    default Double calculateBudgetUtilization(Tenant tenant) {
        if (tenant.getMaxBudget() == null || tenant.getMaxBudget() == 0) return null;

        double totalExpenses = tenant.getContracts() != null ?
                tenant.getContracts().stream()
                        .filter(contract -> "ACTIVE".equals(contract.getStatus()))
                        .mapToDouble(contract -> contract.getMonthlyRent() != null ? contract.getMonthlyRent() : 0.0)
                        .sum() : 0.0;

        return Math.min(1.0, totalExpenses / tenant.getMaxBudget());
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
     * Calculează contractele din acest an.
     */
    @Named("calculateContractsThisYear")
    default Integer calculateContractsThisYear(List<com.example.demo.entity.RentalContract> contracts) {
        if (contracts == null) return 0;
        int currentYear = java.time.Year.now().getValue();
        return (int) contracts.stream()
                .filter(contract -> contract.getDateCreated() != null)
                .filter(contract -> contract.getDateCreated().getYear() == currentYear)
                .count();
    }

    /**
     * Mapează contractele la format sumar.
     */
    @Named("mapContractsToSummary")
    default List<com.example.demo.dto.RentalContractDTO.SummaryDTO> mapContractsToSummary(List<com.example.demo.entity.RentalContract> contracts) {
        if (contracts == null) return null;
        // Această metodă va fi implementată prin RentalContractMapper
        return null; // MapStruct va gestiona automat mapping-ul
    }

    /**
     * Validări înainte de creare.
     */
    @BeforeMapping
    default void validateCreateDTO(TenantDTO.CreateDTO createDTO) {
        if (createDTO.getUsername() == null || createDTO.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username-ul este obligatoriu");
        }
        if (createDTO.getEmail() == null || createDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email-ul este obligatoriu");
        }
    }

    /**
     * Setări după mapare.
     */
    @AfterMapping
    default void setTenantDefaults(@MappingTarget Tenant tenant) {
        if (tenant.getRole() == null) {
            tenant.setRole(User.UserRole.TENANT);
        }
        if (tenant.getActive() == null) {
            tenant.setActive(true);
        }
    }

    /**
     * Validarea compatibilității cu un spațiu.
     */
    default boolean isCompatibleWithSpace(Tenant tenant, com.example.demo.entity.SpaceType spaceType,
                                          Double pricePerMonth, Double area) {
        // Verifică tipul de spațiu
        if (tenant.getPreferredSpaceType() != null && !tenant.getPreferredSpaceType().equals(spaceType)) {
            return false;
        }

        // Verifică bugetul
        if (tenant.getMaxBudget() != null && pricePerMonth != null && pricePerMonth > tenant.getMaxBudget()) {
            return false;
        }

        // Verifică suprafața minimă
        if (tenant.getMinArea() != null && area != null && area < tenant.getMinArea()) {
            return false;
        }

        // Verifică suprafața maximă
        if (tenant.getMaxArea() != null && area != null && area > tenant.getMaxArea()) {
            return false;
        }

        return true;
    }
}