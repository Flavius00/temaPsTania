package com.example.demo.repository;

import com.example.demo.model.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pentru entitatea RentalContract.
 *
 * Extinde JpaRepository pentru operațiunile CRUD de bază și adaugă
 * metode de căutare specifice pentru contracte de închiriere.
 *
 * Principii SOLID respectate:
 * - Interface Segregation: interfață specifică pentru operațiunile cu RentalContract
 * - Dependency Inversion: depinde de abstracțiuni, nu de implementări concrete
 */
@Repository
public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {

    /**
     * Caută un contract după numărul de contract.
     *
     * @param contractNumber numărul contractului căutat
     * @return contractul găsit sau Optional.empty()
     */
    Optional<RentalContract> findByContractNumber(String contractNumber);

    /**
     * Verifică dacă există un contract cu numărul specificat.
     *
     * @param contractNumber numărul contractului de verificat
     * @return true dacă există
     */
    boolean existsByContractNumber(String contractNumber);

    /**
     * Caută contracte după chiriaș.
     *
     * @param tenantId ID-ul chiriașului
     * @return lista contractelor chiriașului specificat
     */
    List<RentalContract> findByTenantId(Long tenantId);

    /**
     * Caută contracte după spațiul comercial.
     *
     * @param spaceId ID-ul spațiului comercial
     * @return lista contractelor pentru spațiul specificat
     */
    List<RentalContract> findBySpaceId(Long spaceId);

    /**
     * Caută contracte după proprietarul spațiului.
     *
     * @param ownerId ID-ul proprietarului
     * @return lista contractelor proprietarului specificat
     */
    List<RentalContract> findBySpaceOwnerId(Long ownerId);

    /**
     * Caută contracte după status.
     *
     * @param status statusul contractului
     * @return lista contractelor cu statusul specificat
     */
    List<RentalContract> findByStatus(RentalContract.ContractStatus status);

    /**
     * Caută contracte plătite sau neplătite.
     *
     * @param isPaid statusul plății
     * @return lista contractelor plătite/neplătite
     */
    List<RentalContract> findByIsPaid(Boolean isPaid);

    /**
     * Caută contracte după metoda de plată.
     *
     * @param paymentMethod metoda de plată
     * @return lista contractelor cu metoda de plată specificată
     */
    List<RentalContract> findByPaymentMethod(RentalContract.PaymentMethod paymentMethod);

    /**
     * Caută contracte active.
     *
     * @return lista contractelor active
     */
    @Query("SELECT c FROM RentalContract c WHERE c.status = 'ACTIVE' AND c.startDate <= CURRENT_DATE AND c.endDate >= CURRENT_DATE")
    List<RentalContract> findActiveContracts();

    /**
     * Caută contracte care expiră în următoarele zile specificate.
     *
     * @param days numărul de zile până la expirare
     * @return lista contractelor care expiră în perioada specificată
     */
    @Query("SELECT c FROM RentalContract c WHERE c.status = 'ACTIVE' AND c.endDate BETWEEN CURRENT_DATE AND (CURRENT_DATE + :days)")
    List<RentalContract> findContractsExpiringInDays(@Param("days") int days);

    /**
     * Caută contractele expirate.
     *
     * @return lista contractelor expirate
     */
    @Query("SELECT c FROM RentalContract c WHERE c.endDate < CURRENT_DATE AND c.status = 'ACTIVE'")
    List<RentalContract> findExpiredContracts();

    /**
     * Caută contracte în intervalul de date specificat.
     *
     * @param startDate data de început
     * @param endDate data de sfârșit
     * @return lista contractelor din intervalul specificat
     */
    List<RentalContract> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Caută contracte care se termină în intervalul specificat.
     *
     * @param startDate data de început
     * @param endDate data de sfârșit
     * @return lista contractelor care se termină în intervalul specificat
     */
    List<RentalContract> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Caută contracte cu chiria în intervalul specificat.
     *
     * @param minRent chiria minimă
     * @param maxRent chiria maximă
     * @return lista contractelor cu chiria în intervalul specificat
     */
    List<RentalContract> findByMonthlyRentBetween(Double minRent, Double maxRent);

    /**
     * Caută contracte cu auto-renewal activat.
     *
     * @param autoRenewal true pentru contracte cu auto-renewal
     * @return lista contractelor cu/fără auto-renewal
     */
    List<RentalContract> findByAutoRenewal(Boolean autoRenewal);

    /**
     * Caută contracte care permit terminarea anticipată.
     *
     * @param earlyTerminationAllowed true pentru contracte cu terminare anticipată permisă
     * @return lista contractelor cu/fără terminare anticipată permisă
     */
    List<RentalContract> findByEarlyTerminationAllowed(Boolean earlyTerminationAllowed);

    /**
     * Calculează venitul total dintr-un spațiu comercial.
     *
     * @param spaceId ID-ul spațiului comercial
     * @return venitul total din contractele pentru spațiul specificat
     */
    @Query("SELECT SUM(c.monthlyRent) FROM RentalContract c WHERE c.space.id = :spaceId AND c.status = 'ACTIVE'")
    Double getTotalRevenueForSpace(@Param("spaceId") Long spaceId);

    /**
     * Calculează venitul total pentru un proprietar.
     *
     * @param ownerId ID-ul proprietarului
     * @return venitul total din contractele proprietarului specificat
     */
    @Query("SELECT SUM(c.monthlyRent) FROM RentalContract c WHERE c.space.owner.id = :ownerId AND c.status = 'ACTIVE'")
    Double getTotalRevenueForOwner(@Param("ownerId") Long ownerId);

    /**
     * Calculează cheltuielile totale pentru un chiriaș.
     *
     * @param tenantId ID-ul chiriașului
     * @return cheltuielile totale ale chiriașului specificat
     */
    @Query("SELECT SUM(c.monthlyRent) FROM RentalContract c WHERE c.tenant.id = :tenantId AND c.status = 'ACTIVE'")
    Double getTotalExpensesForTenant(@Param("tenantId") Long tenantId);

    /**
     * Numără contractele active pentru un chiriaș.
     *
     * @param tenantId ID-ul chiriașului
     * @return numărul de contracte active
     */
    @Query("SELECT COUNT(c) FROM RentalContract c WHERE c.tenant.id = :tenantId AND c.status = 'ACTIVE'")
    long countActiveContractsForTenant(@Param("tenantId") Long tenantId);

    /**
     * Numără contractele active pentru un proprietar.
     *
     * @param ownerId ID-ul proprietarului
     * @return numărul de contracte active
     */
    @Query("SELECT COUNT(c) FROM RentalContract c WHERE c.space.owner.id = :ownerId AND c.status = 'ACTIVE'")
    long countActiveContractsForOwner(@Param("ownerId") Long ownerId);

    /**
     * Caută contractele cu cea mai mare chirie.
     *
     * @param limit numărul maxim de rezultate
     * @return lista contractelor ordonată după chirie (descrescător)
     */
    @Query("SELECT c FROM RentalContract c ORDER BY c.monthlyRent DESC")
    List<RentalContract> findContractsWithHighestRent(@Param("limit") int limit);

    /**
     * Caută contractele cu cea mai mică chirie.
     *
     * @param limit numărul maxim de rezultate
     * @return lista contractelor ordonată după chirie (crescător)
     */
    @Query("SELECT c FROM RentalContract c ORDER BY c.monthlyRent ASC")
    List<RentalContract> findContractsWithLowestRent(@Param("limit") int limit);

    /**
     * Caută contractele cu cea mai lungă durată.
     *
     * @param limit numărul maxim de rezultate
     * @return lista contractelor ordonată după durată (descrescător)
     */
    @Query("SELECT c FROM RentalContract c ORDER BY (c.endDate - c.startDate) DESC")
    List<RentalContract> findLongestContracts(@Param("limit") int limit);

    /**
     * Calculează durata medie a contractelor.
     *
     * @return durata medie în zile
     */
    @Query("SELECT AVG(c.endDate - c.startDate) FROM RentalContract c")
    Double getAverageContractDuration();

    /**
     * Calculează chiria medie.
     *
     * @return chiria medie
     */
    @Query("SELECT AVG(c.monthlyRent) FROM RentalContract c")
    Double getAverageRent();

    /**
     * Caută contracte pentru un tip specific de spațiu comercial.
     *
     * @param spaceType tipul de spațiu comercial
     * @return lista contractelor pentru tipul de spațiu specificat
     */
    @Query("SELECT c FROM RentalContract c WHERE c.space.spaceType = :spaceType")
    List<RentalContract> findContractsBySpaceType(@Param("spaceType") com.example.demo.entity.SpaceType spaceType);

    /**
     * Caută contracte pentru spații dintr-o anumită clădire.
     *
     * @param buildingId ID-ul clădirii
     * @return lista contractelor pentru spații din clădirea specificată
     */
    @Query("SELECT c FROM RentalContract c WHERE c.space.building.id = :buildingId")
    List<RentalContract> findContractsByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * Numără contractele după status.
     *
     * @param status statusul contractului
     * @return numărul de contracte cu statusul specificat
     */
    long countByStatus(RentalContract.ContractStatus status);

    /**
     * Caută contractele care necesită reînnoire (aproape de expirare și cu auto-renewal).
     *
     * @param daysBeforeExpiration numărul de zile înainte de expirare
     * @return lista contractelor care necesită reînnoire
     */
    @Query("SELECT c FROM RentalContract c WHERE " +
            "c.status = 'ACTIVE' AND " +
            "c.autoRenewal = true AND " +
            "c.endDate BETWEEN CURRENT_DATE AND (CURRENT_DATE + :daysBeforeExpiration)")
    List<RentalContract> findContractsForRenewal(@Param("daysBeforeExpiration") int daysBeforeExpiration);

    /**
     * Caută contractele neplătite care au depășit termenul de plată.
     *
     * @return lista contractelor neplătite în întârziere
     */
    @Query("SELECT c FROM RentalContract c WHERE " +
            "c.isPaid = false AND " +
            "c.status = 'ACTIVE' AND " +
            "c.startDate < CURRENT_DATE")
    List<RentalContract> findOverdueContracts();

    /**
     * Statistici pe luni - contractele create într-o anumită lună.
     *
     * @param year anul
     * @param month luna
     * @return numărul de contracte create în luna specificată
     */
    @Query("SELECT COUNT(c) FROM RentalContract c WHERE " +
            "YEAR(c.dateCreated) = :year AND MONTH(c.dateCreated) = :month")
    long countContractsCreatedInMonth(@Param("year") int year, @Param("month") int month);
}