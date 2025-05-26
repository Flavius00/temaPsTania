package com.example.demo.mapper;

import com.example.demo.dto.RentalContractDTO;
import com.example.demo.entity.RentalContract;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Mapper pentru convertirea între entitatea RentalContract și DTO-urile corespunzătoare.
 *
 * Folosește MapStruct pentru generarea automată a codului de mapping.
 *
 * Principii SOLID respectate:
 * - Single Responsibility: doar convertirea între RentalContract și RentalContractDTO
 * - Open/Closed: poate fi extins cu noi metode fără modificarea celor existente
 * - Dependency Inversion: depinde de abstracțiuni (interfețe)
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ComercialSpaceMapper.class, TenantMapper.class}
)
public interface RentalContractMapper {

    /**
     * Convertește o entitate RentalContract într-un RentalContractDTO.
     *
     * @param contract entitatea RentalContract
     * @return RentalContractDTO corespunzător
     */
    @Mapping(target = "space", source = "space", qualifiedByName = "spaceToSummaryDTO")
    @Mapping(target = "tenant", source = "tenant", qualifiedByName = "tenantToListDTO")
    @Mapping(target = "durationInMonths", source = ".", qualifiedByName = "calculateDurationInMonths")
    @Mapping(target = "totalValue", source = ".", qualifiedByName = "calculateTotalValue")
    @Mapping(target = "initialPayment", source = ".", qualifiedByName = "calculateInitialPayment")
    @Mapping(target = "daysUntilExpiration", source = ".", qualifiedByName = "calculateDaysUntilExpiration")
    @Mapping(target = "isActive", source = ".", qualifiedByName = "calculateIsActive")
    @Mapping(target = "isExpired", source = ".", qualifiedByName = "calculateIsExpired")
    @Mapping(target = "isNearingExpiration", source = ".", qualifiedByName = "calculateIsNearingExpiration")
    RentalContractDTO toDTO(RentalContract contract);

    /**
     * Convertește un RentalContractDTO într-o entitate RentalContract.
     *
     * @param contractDTO DTO-ul de convertit
     * @return entitatea RentalContract corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contractNumber", ignore = true) // Se generează automat
    @Mapping(target = "space", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "status", constant = "PENDING") // Status implicit
    @Mapping(target = "isPaid", constant = "false") // Implicit neplătit
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RentalContract toEntity(RentalContractDTO contractDTO);

    /**
     * Convertește o listă de entități RentalContract într-o listă de RentalContractDTO.
     *
     * @param contracts lista de entități
     * @return lista de DTO-uri
     */
    List<RentalContractDTO> toDTOList(List<RentalContract> contracts);

    /**
     * Convertește o entitate RentalContract într-un ListDTO (pentru listări).
     *
     * @param contract entitatea RentalContract
     * @return ListDTO corespunzător
     */
    @Mapping(target = "spaceName", source = "space.name")
    @Mapping(target = "tenantName", source = "tenant.name")
    @Mapping(target = "ownerName", source = "space.owner.name")
    @Mapping(target = "daysUntilExpiration", source = ".", qualifiedByName = "calculateDaysUntilExpiration")
    @Mapping(target = "isNearingExpiration", source = ".", qualifiedByName = "calculateIsNearingExpiration")
    RentalContractDTO.ListDTO toListDTO(RentalContract contract);

    /**
     * Convertește o listă de entități RentalContract într-o listă de ListDTO.
     *
     * @param contracts lista de entități
     * @return lista de ListDTO-uri
     */
    List<RentalContractDTO.ListDTO> toListDTOList(List<RentalContract> contracts);

    /**
     * Convertește un CreateDTO într-o entitate RentalContract.
     *
     * @param createDTO DTO-ul pentru creare
     * @return entitatea RentalContract corespunzătoare
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contractNumber", ignore = true) // Se generează automat
    @Mapping(target = "space", ignore = true) // Se va seta separat folosind spaceId
    @Mapping(target = "tenant", ignore = true) // Se va seta separat folosind tenantId
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "isPaid", constant = "false")
    @Mapping(target = "actualEndDate", ignore = true)
    @Mapping(target = "terminationReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RentalContract fromCreateDTO(RentalContractDTO.CreateDTO createDTO);

    /**
     * Actualizează o entitate RentalContract din UpdateDTO.
     *
     * @param updateDTO DTO-ul cu datele de actualizare
     * @param contract entitatea de actualizat
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contractNumber", ignore = true)
    @Mapping(target = "space", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "startDate", ignore = true) // Data de start nu se modifică
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "signature", ignore = true) // Semnătura nu se modifică
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(RentalContractDTO.UpdateDTO updateDTO, @MappingTarget RentalContract contract);

    /**
     * Convertește o entitate RentalContract într-un SummaryDTO.
     *
     * @param contract entitatea RentalContract
     * @return SummaryDTO corespunzător
     */
    @Mapping(target = "daysUntilExpiration", source = ".", qualifiedByName = "calculateDaysUntilExpiration")
    @Mapping(target = "isActive", source = ".", qualifiedByName = "calculateIsActive")
    @Mapping(target = "isNearingExpiration", source = ".", qualifiedByName = "calculateIsNearingExpiration")
    RentalContractDTO.SummaryDTO toSummaryDTO(RentalContract contract);

    /**
     * Convertește o listă de entități RentalContract într-o listă de SummaryDTO.
     *
     * @param contracts lista de entități
     * @return lista de SummaryDTO-uri
     */
    List<RentalContractDTO.SummaryDTO> toSummaryDTOList(List<RentalContract> contracts);

    /**
     * Convertește o entitate RentalContract într-un StatsDTO (pentru statistici).
     *
     * @param contract entitatea RentalContract
     * @return StatsDTO corespunzător
     */
    @Mapping(target = "durationInMonths", source = ".", qualifiedByName = "calculateDurationInMonths")
    @Mapping(target = "totalValue", source = ".", qualifiedByName = "calculateTotalValue")
    @Mapping(target = "spaceName", source = "space.name")
    @Mapping(target = "spaceType", source = "space.spaceType")
    @Mapping(target = "tenantName", source = "tenant.name")
    @Mapping(target = "ownerName", source = "space.owner.name")
    RentalContractDTO.StatsDTO toStatsDTO(RentalContract contract);

    /**
     * Calculează durata contractului în luni.
     */
    @Named("calculateDurationInMonths")
    default Long calculateDurationInMonths(RentalContract contract) {
        if (contract.getStartDate() == null || contract.getEndDate() == null) return null;
        return java.time.Period.between(contract.getStartDate(), contract.getEndDate()).toTotalMonths();
    }

    /**
     * Calculează valoarea totală a contractului.
     */
    @Named("calculateTotalValue")
    default Double calculateTotalValue(RentalContract contract) {
        Long duration = calculateDurationInMonths(contract);
        if (duration == null || contract.getMonthlyRent() == null) return null;
        return contract.getMonthlyRent() * duration;
    }

    /**
     * Calculează suma inițială de plătit.
     */
    @Named("calculateInitialPayment")
    default Double calculateInitialPayment(RentalContract contract) {
        if (contract.getMonthlyRent() == null) return null;
        double deposit = contract.getSecurityDeposit() != null ? contract.getSecurityDeposit() : 0.0;
        return contract.getMonthlyRent() + deposit;
    }

    /**
     * Calculează numărul de zile până la expirare.
     */
    @Named("calculateDaysUntilExpiration")
    default Long calculateDaysUntilExpiration(RentalContract contract) {
        if (contract.getEndDate() == null) return null;
        LocalDate now = LocalDate.now();
        if (now.isAfter(contract.getEndDate())) return 0L;
        return java.time.ChronoUnit.DAYS.between(now, contract.getEndDate());
    }

    /**
     * Verifică dacă contractul este activ.
     */
    @Named("calculateIsActive")
    default Boolean calculateIsActive(RentalContract contract) {
        if (contract.getStatus() == null || contract.getStartDate() == null || contract.getEndDate() == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return RentalContract.ContractStatus.ACTIVE.equals(contract.getStatus()) &&
                !now.isBefore(contract.getStartDate()) && !now.isAfter(contract.getEndDate());
    }

    /**
     * Verifică dacă contractul a expirat.
     */
    @Named("calculateIsExpired")
    default Boolean calculateIsExpired(RentalContract contract) {
        if (contract.getEndDate() == null) return false;
        return LocalDate.now().isAfter(contract.getEndDate()) ||
                RentalContract.ContractStatus.EXPIRED.equals(contract.getStatus());
    }

    /**
     * Verifică dacă contractul este aproape de expirare.
     */
    @Named("calculateIsNearingExpiration")
    default Boolean calculateIsNearingExpiration(RentalContract contract) {
        Long days = calculateDaysUntilExpiration(contract);
        return days != null && days <= 30 && days > 0;
    }

    /**
     * Mapare pentru Space la SummaryDTO.
     */
    @Named("spaceToSummaryDTO")
    default com.example.demo.dto.ComercialSpaceDTO.SummaryDTO spaceToSummaryDTO(com.example.demo.entity.ComercialSpace space) {
        if (space == null) return null;
        // Această metodă va fi implementată prin ComercialSpaceMapper
        return null; // MapStruct va gestiona automat mapping-ul
    }

    /**
     * Mapare pentru Tenant la ListDTO.
     */
    @Named("tenantToListDTO")
    default com.example.demo.dto.TenantDTO.ListDTO tenantToListDTO(com.example.demo.entity.Tenant tenant) {
        if (tenant == null) return null;
        // Această metodă va fi implementată prin TenantMapper
        return null; // MapStruct va gestiona automat mapping-ul
    }

    /**
     * Validări înainte de creare.
     */
    @BeforeMapping
    default void validateCreateDTO(RentalContractDTO.CreateDTO createDTO) {
        if (createDTO.getSpaceId() == null) {
            throw new IllegalArgumentException("ID-ul spațiului este obligatoriu");
        }
        if (createDTO.getTenantId() == null) {
            throw new IllegalArgumentException("ID-ul chiriașului este obligatoriu");
        }
        if (createDTO.getStartDate() == null) {
            throw new IllegalArgumentException("Data de început este obligatorie");
        }
        if (createDTO.getEndDate() == null) {
            throw new IllegalArgumentException("Data de sfârșit este obligatorie");
        }
        if (createDTO.getStartDate().isAfter(createDTO.getEndDate())) {
            throw new IllegalArgumentException("Data de început nu poate fi după data de sfârșit");
        }
        if (createDTO.getMonthlyRent() == null || createDTO.getMonthlyRent() <= 0) {
            throw new IllegalArgumentException("Chiria lunară trebuie să fie pozitivă");
        }
    }

    /**
     * Setări după mapare pentru actualizarea timestamp-ului.
     */
    @AfterMapping
    default void setUpdatedTimestamp(@MappingTarget RentalContract contract) {
        contract.setUpdatedAt(java.time.LocalDateTime.now());
    }

    /**
     * Setări după creare pentru valori implicite.
     */
    @AfterMapping
    default void setContractDefaults(@MappingTarget RentalContract contract) {
        if (contract.getStatus() == null) {
            contract.setStatus(RentalContract.ContractStatus.PENDING);
        }
        if (contract.getIsPaid() == null) {
            contract.setIsPaid(false);
        }
        if (contract.getAutoRenewal() == null) {
            contract.setAutoRenewal(false);
        }
        if (contract.getEarlyTerminationAllowed() == null) {
            contract.setEarlyTerminationAllowed(false);
        }
    }
}