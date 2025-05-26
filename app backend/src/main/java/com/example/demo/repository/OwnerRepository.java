package com.example.demo.repository;

import com.example.demo.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pentru entitatea Owner.
 *
 * Extinde JpaRepository pentru operațiunile CRUD de bază și adaugă
 * metode de căutare specifice pentru proprietari.
 *
 * Principii SOLID respectate:
 * - Interface Segregation: interfață specifică pentru operațiunile cu Owner
 * - Dependency Inversion: depinde de abstracțiuni, nu de implementări concrete
 */
@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    /**
     * Caută un proprietar după username.
     *
     * @param username username-ul căutat
     * @return proprietarul găsit sau Optional.empty()
     */
    Optional<Owner> findByUsername(String username);

    /**
     * Caută un proprietar după email.
     *
     * @param email email-ul căutat
     * @return proprietarul găsit sau Optional.empty()
     */
    Optional<Owner> findByEmail(String email);

    /**
     * Caută proprietari după numele companiei.
     *
     * @param companyName numele companiei căutat
     * @return lista proprietarilor cu compania specificată
     */
    List<Owner> findByCompanyNameContainingIgnoreCase(String companyName);

    /**
     * Caută un proprietar după CUI (Tax ID).
     *
     * @param taxId CUI-ul căutat
     * @return proprietarul găsit sau Optional.empty()
     */
    Optional<Owner> findByTaxId(String taxId);

    /**
     * Verifică dacă există un proprietar cu CUI-ul specificat.
     *
     * @param taxId CUI-ul de verificat
     * @return true dacă există
     */
    boolean existsByTaxId(String taxId);

    /**
     * Caută proprietari după tipul companiei.
     *
     * @param companyType tipul companiei căutat
     * @return lista proprietarilor cu tipul de companie specificat
     */
    List<Owner> findByCompanyType(String companyType);

    /**
     * Caută proprietari care au spații comerciale disponibile.
     *
     * @return lista proprietarilor cu spații disponibile
     */
    @Query("SELECT DISTINCT o FROM Owner o JOIN o.spaces s WHERE s.available = true")
    List<Owner> findOwnersWithAvailableSpaces();

    /**
     * Caută proprietari după numărul de spații deținute.
     *
     * @param minSpaces numărul minim de spații
     * @return lista proprietarilor cu cel puțin numărul specificat de spații
     */
    @Query("SELECT o FROM Owner o WHERE SIZE(o.spaces) >= :minSpaces")
    List<Owner> findOwnersWithMinimumSpaces(@Param("minSpaces") int minSpaces);

    /**
     * Caută proprietarii cu cele mai multe spații.
     *
     * @param limit numărul maxim de rezultate
     * @return lista proprietarilor ordonați după numărul de spații
     */
    @Query("SELECT o FROM Owner o ORDER BY SIZE(o.spaces) DESC")
    List<Owner> findTopOwnersBySpaceCount(@Param("limit") int limit);

    /**
     * Caută proprietari după venitul total estimat (suma chiriilor spațiilor).
     *
     * @param minRevenue venitul minim căutat
     * @return lista proprietarilor cu venitul specificat
     */
    @Query("SELECT o FROM Owner o JOIN o.spaces s " +
            "GROUP BY o " +
            "HAVING SUM(s.pricePerMonth) >= :minRevenue")
    List<Owner> findOwnersByMinimumRevenue(@Param("minRevenue") Double minRevenue);

    /**
     * Caută proprietari care au spații într-o anumită clădire.
     *
     * @param buildingId ID-ul clădirii
     * @return lista proprietarilor cu spații în clădirea specificată
     */
    @Query("SELECT DISTINCT o FROM Owner o JOIN o.spaces s WHERE s.building.id = :buildingId")
    List<Owner> findOwnersByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * Caută proprietari după tipul de spații deținute.
     *
     * @param spaceType tipul de spațiu căutat
     * @return lista proprietarilor cu spații de tipul specificat
     */
    @Query("SELECT DISTINCT o FROM Owner o JOIN o.spaces s WHERE s.spaceType = :spaceType")
    List<Owner> findOwnersBySpaceType(@Param("spaceType") com.example.demo.entity.SpaceType spaceType);

    /**
     * Calculează venitul total pentru un proprietar.
     *
     * @param ownerId ID-ul proprietarului
     * @return venitul total din chirii
     */
    @Query("SELECT COALESCE(SUM(s.pricePerMonth), 0) FROM ComercialSpace s WHERE s.owner.id = :ownerId")
    Double calculateTotalRevenueForOwner(@Param("ownerId") Long ownerId);

    /**
     * Numără spațiile disponibile pentru un proprietar.
     *
     * @param ownerId ID-ul proprietarului
     * @return numărul de spații disponibile
     */
    @Query("SELECT COUNT(s) FROM ComercialSpace s WHERE s.owner.id = :ownerId AND s.available = true")
    long countAvailableSpacesForOwner(@Param("ownerId") Long ownerId);

    /**
     * Numără spațiile ocupate pentru un proprietar.
     *
     * @param ownerId ID-ul proprietarului
     * @return numărul de spații ocupate
     */
    @Query("SELECT COUNT(s) FROM ComercialSpace s WHERE s.owner.id = :ownerId AND s.available = false")
    long countOccupiedSpacesForOwner(@Param("ownerId") Long ownerId);

    /**
     * Caută proprietarii cu rata de ocupare specificată.
     *
     * @param minOccupancyRate rata minimă de ocupare (0-1)
     * @return lista proprietarilor cu rata de ocupare specificată
     */
    @Query("SELECT o FROM Owner o WHERE " +
            "(SELECT COUNT(s) FROM ComercialSpace s WHERE s.owner = o AND s.available = false) * 1.0 / " +
            "(SELECT COUNT(s) FROM ComercialSpace s WHERE s.owner = o) >= :minOccupancyRate")
    List<Owner> findOwnersByMinimumOccupancyRate(@Param("minOccupancyRate") Double minOccupancyRate);

    /**
     * Caută proprietari cu contracte active într-o anumită perioadă.
     *
     * @return lista proprietarilor cu contracte active
     */
    @Query("SELECT DISTINCT o FROM Owner o JOIN o.spaces s JOIN s.contracts c " +
            "WHERE c.status = 'ACTIVE'")
    List<Owner> findOwnersWithActiveContracts();

    /**
     * Statistici pentru proprietari - numărul mediu de spații pe proprietar.
     *
     * @return numărul mediu de spații
     */
    @Query("SELECT AVG(SIZE(o.spaces)) FROM Owner o")
    Double getAverageSpacesPerOwner();

    /**
     * Caută proprietarii fără spații înregistrate.
     *
     * @return lista proprietarilor fără spații
     */
    @Query("SELECT o FROM Owner o WHERE SIZE(o.spaces) = 0")
    List<Owner> findOwnersWithoutSpaces();
}