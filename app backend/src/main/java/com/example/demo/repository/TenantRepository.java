package com.example.demo.repository;

import com.example.demo.entity.SpaceType;
import com.example.demo.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pentru entitatea Tenant.
 *
 * Extinde JpaRepository pentru operațiunile CRUD de bază și adaugă
 * metode de căutare specifice pentru chiriași.
 *
 * Principii SOLID respectate:
 * - Interface Segregation: interfață specifică pentru operațiunile cu Tenant
 * - Dependency Inversion: depinde de abstracțiuni, nu de implementări concrete
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * Caută un chiriaș după username.
     *
     * @param username username-ul căutat
     * @return chiriașul găsit sau Optional.empty()
     */
    Optional<Tenant> findByUsername(String username);

    /**
     * Caută un chiriaș după email.
     *
     * @param email email-ul căutat
     * @return chiriașul găsit sau Optional.empty()
     */
    Optional<Tenant> findByEmail(String email);

    /**
     * Caută chiriași după numele companiei.
     *
     * @param companyName numele companiei căutat
     * @return lista chiriașilor cu compania specificată
     */
    List<Tenant> findByCompanyNameContainingIgnoreCase(String companyName);

    /**
     * Caută un chiriaș după CUI (Tax ID).
     *
     * @param taxId CUI-ul căutat
     * @return chiriașul găsit sau Optional.empty()
     */
    Optional<Tenant> findByTaxId(String taxId);

    /**
     * Verifică dacă există un chiriaș cu CUI-ul specificat.
     *
     * @param taxId CUI-ul de verificat
     * @return true dacă există
     */
    boolean existsByTaxId(String taxId);

    /**
     * Caută chiriași după tipul de business.
     *
     * @param businessType tipul de business căutat
     * @return lista chiriașilor cu tipul de business specificat
     */
    List<Tenant> findByBusinessTypeContainingIgnoreCase(String businessType);

    /**
     * Caută chiriași după tipul preferat de spațiu.
     *
     * @param preferredSpaceType tipul preferat de spațiu
     * @return lista chiriașilor cu preferința specificată
     */
    List<Tenant> findByPreferredSpaceType(SpaceType preferredSpaceType);

    /**
     * Caută chiriași cu contracte active.
     *
     * @return lista chiriașilor cu contracte active
     */
    @Query("SELECT DISTINCT t FROM Tenant t JOIN t.contracts c WHERE c.status = 'ACTIVE'")
    List<Tenant> findTenantsWithActiveContracts();

    /**
     * Caută chiriași fără contracte active.
     *
     * @return lista chiriașilor fără contracte active
     */
    @Query("SELECT t FROM Tenant t WHERE t NOT IN " +
            "(SELECT DISTINCT c.tenant FROM RentalContract c WHERE c.status = 'ACTIVE')")
    List<Tenant> findTenantsWithoutActiveContracts();

    /**
     * Caută chiriași după bugetul maxim.
     *
     * @param maxBudget bugetul maxim de referință
     * @return lista chiriașilor cu buget până la suma specificată
     */
    List<Tenant> findByMaxBudgetLessThanEqual(Double maxBudget);

    /**
     * Caută chiriași după suprafața minimă dorită.
     *
     * @param minArea suprafața minimă de referință
     * @return lista chiriașilor care doresc cel puțin suprafața specificată
     */
    List<Tenant> findByMinAreaLessThanEqual(Double minArea);

    /**
     * Caută chiriași care au contracte într-o anumită clădire.
     *
     * @param buildingId ID-ul clădirii
     * @return lista chiriașilor cu contracte în clădirea specificată
     */
    @Query("SELECT DISTINCT t FROM Tenant t JOIN t.contracts c " +
            "WHERE c.space.building.id = :buildingId AND c.status = 'ACTIVE'")
    List<Tenant> findTenantsByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * Caută chiriași cu contracte pentru un anumit tip de spațiu.
     *
     * @param spaceType tipul de spațiu căutat
     * @return lista chiriașilor cu contracte pentru tipul de spațiu specificat
     */
    @Query("SELECT DISTINCT t FROM Tenant t JOIN t.contracts c " +
            "WHERE c.space.spaceType = :spaceType AND c.status = 'ACTIVE'")
    List<Tenant> findTenantsBySpaceType(@Param("spaceType") SpaceType spaceType);

    /**
     * Calculează cheltuielile totale lunare pentru un chiriaș.
     *
     * @param tenantId ID-ul chiriașului
     * @return suma totală a chiriilor lunare
     */
    @Query("SELECT COALESCE(SUM(c.monthlyRent), 0) FROM RentalContract c " +
            "WHERE c.tenant.id = :tenantId AND c.status = 'ACTIVE'")
    Double calculateTotalMonthlyExpensesForTenant(@Param("tenantId") Long tenantId);

    /**
     * Numără contractele active pentru un chiriaș.
     *
     * @param tenantId ID-ul chiriașului
     * @return numărul de contracte active
     */
    @Query("SELECT COUNT(c) FROM RentalContract c " +
            "WHERE c.tenant.id = :tenantId AND c.status = 'ACTIVE'")
    long countActiveContractsForTenant(@Param("tenantId") Long tenantId);

    /**
     * Numără contractele istorice pentru un chiriaș.
     *
     * @param tenantId ID-ul chiriașului
     * @return numărul total de contracte
     */
    @Query("SELECT COUNT(c) FROM RentalContract c WHERE c.tenant.id = :tenantId")
    long countTotalContractsForTenant(@Param("tenantId") Long tenantId);

    /**
     * Caută chiriași cu multiple contracte active.
     *
     * @return lista chiriașilor cu mai mult de un contract activ
     */
    @Query("SELECT t FROM Tenant t WHERE " +
            "(SELECT COUNT(c) FROM RentalContract c WHERE c.tenant = t AND c.status = 'ACTIVE') > 1")
    List<Tenant> findTenantsWithMultipleActiveContracts();

    /**
     * Caută chiriași după intervalul de suprafață dorit.
     *
     * @param minArea suprafața minimă
     * @param maxArea suprafața maximă
     * @return lista chiriașilor cu preferințele specificate
     */
    @Query("SELECT t FROM Tenant t WHERE " +
            "(:minArea IS NULL OR t.minArea <= :minArea) AND " +
            "(:maxArea IS NULL OR t.maxArea >= :maxArea)")
    List<Tenant> findTenantsByAreaRange(@Param("minArea") Double minArea,
                                        @Param("maxArea") Double maxArea);

    /**
     * Caută chiriași compatibili cu un spațiu disponibil.
     *
     * @param spaceType tipul spațiului
     * @param pricePerMonth prețul pe lună
     * @param area suprafața spațiului
     * @return lista chiriașilor compatibili
     */
    @Query("SELECT t FROM Tenant t WHERE " +
            "(t.preferredSpaceType IS NULL OR t.preferredSpaceType = :spaceType) AND " +
            "(t.maxBudget IS NULL OR t.maxBudget >= :pricePerMonth) AND " +
            "(t.minArea IS NULL OR t.minArea <= :area) AND " +
            "(t.maxArea IS NULL OR t.maxArea >= :area)")
    List<Tenant> findCompatibleTenants(@Param("spaceType") SpaceType spaceType,
                                       @Param("pricePerMonth") Double pricePerMonth,
                                       @Param("area") Double area);

    /**
     * Caută chiriații cu contractele care expiră în curând.
     *
     * @param daysUntilExpiration numărul de zile până la expirare
     * @return lista chiriașilor cu contracte care expiră în perioada specificată
     */
    @Query("SELECT DISTINCT t FROM Tenant t JOIN t.contracts c " +
            "WHERE c.status = 'ACTIVE' AND c.endDate <= CURRENT_DATE + :daysUntilExpiration")
    List<Tenant> findTenantsWithExpiringContracts(@Param("daysUntilExpiration") int daysUntilExpiration);

    /**
     * Statistici - numărul mediu de contracte pe chiriaș.
     *
     * @return numărul mediu de contracte
     */
    @Query("SELECT AVG(SIZE(t.contracts)) FROM Tenant t")
    Double getAverageContractsPerTenant();

    /**
     * Caută chiriații cu cel mai mare număr de contracte.
     *
     * @param limit numărul maxim de rezultate
     * @return lista chiriașilor ordonați după numărul de contracte
     */
    @Query("SELECT t FROM Tenant t ORDER BY SIZE(t.contracts) DESC")
    List<Tenant> findTopTenantsByContractCount(@Param("limit") int limit);
}