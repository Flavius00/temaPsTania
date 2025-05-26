package com.example.demo.repository;

import com.example.demo.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pentru entitatea Parking.
 *
 * Extinde JpaRepository pentru operațiunile CRUD de bază și adaugă
 * metode de căutare specifice pentru parcări.
 *
 * Principii SOLID respectate:
 * - Interface Segregation: interfață specifică pentru operațiunile cu Parking
 * - Dependency Inversion: depinde de abstracțiuni, nu de implementări concrete
 */
@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {

    /**
     * Caută parcări după tipul de parcare.
     *
     * @param parkingType tipul de parcare căutat
     * @return lista parcărilor de tipul specificat
     */
    List<Parking> findByParkingType(Parking.ParkingType parkingType);

    /**
     * Caută parcări acoperite sau neacoperite.
     *
     * @param covered true pentru parcări acoperite
     * @return lista parcărilor acoperite/neacoperite
     */
    List<Parking> findByCovered(Boolean covered);

    /**
     * Caută parcări cu numărul minim de locuri.
     *
     * @param minSpots numărul minim de locuri
     * @return lista parcărilor cu cel puțin numărul specificat de locuri
     */
    List<Parking> findByNumberOfSpotsGreaterThanEqual(Integer minSpots);

    /**
     * Caută parcări cu prețul per loc mai mic decât valoarea specificată.
     *
     * @param maxPrice prețul maxim per loc
     * @return lista parcărilor cu preț mai mic decât specificat
     */
    List<Parking> findByPricePerSpotLessThanEqual(Double maxPrice);

    /**
     * Caută parcări cu facilități pentru persoane cu dizabilități.
     *
     * @return lista parcărilor cu locuri pentru persoane cu dizabilități
     */
    @Query("SELECT p FROM Parking p WHERE p.disabledAccessSpots > 0")
    List<Parking> findParkingsWithDisabledAccess();

    /**
     * Caută parcări cu facilități de încărcare electrică.
     *
     * @return lista parcărilor cu locuri de încărcare electrică
     */
    @Query("SELECT p FROM Parking p WHERE p.electricChargingSpots > 0")
    List<Parking> findParkingsWithElectricCharging();

    /**
     * Caută parcări securizate (cu camere sau pază).
     *
     * @return lista parcărilor securizate
     */
    @Query("SELECT p FROM Parking p WHERE p.securityCameras = true OR p.securityGuard = true OR p.accessCardRequired = true")
    List<Parking> findSecuredParkings();

    /**
     * Caută parcări cu acces controlat (necesită card de acces).
     *
     * @param accessCardRequired true pentru parcări cu acces controlat
     * @return lista parcărilor cu/fără acces controlat
     */
    List<Parking> findByAccessCardRequired(Boolean accessCardRequired);

    /**
     * Caută parcări cu restricții de înălțime.
     *
     * @return lista parcărilor cu restricții de înălțime
     */
    @Query("SELECT p FROM Parking p WHERE p.heightRestriction IS NOT NULL AND p.heightRestriction > 0")
    List<Parking> findParkingsWithHeightRestriction();

    /**
     * Caută parcări după intervalul de preț per loc.
     *
     * @param minPrice prețul minim per loc
     * @param maxPrice prețul maxim per loc
     * @return lista parcărilor cu prețurile în intervalul specificat
     */
    List<Parking> findByPricePerSpotBetween(Double minPrice, Double maxPrice);

    /**
     * Caută parcări după numărul de locuri rezervate.
     *
     * @param reservedSpots numărul de locuri rezervate
     * @return lista parcărilor cu numărul specificat de locuri rezervate
     */
    List<Parking> findByReservedSpots(Integer reservedSpots);

    /**
     * Caută parcări cu locuri disponibile pentru rezervare.
     *
     * @return lista parcărilor cu locuri disponibile
     */
    @Query("SELECT p FROM Parking p WHERE p.numberOfSpots > p.reservedSpots")
    List<Parking> findParkingsWithAvailableSpots();

    /**
     * Caută parcări complet rezervate.
     *
     * @return lista parcărilor complet rezervate
     */
    @Query("SELECT p FROM Parking p WHERE p.numberOfSpots = p.reservedSpots")
    List<Parking> findFullyReservedParkings();

    /**
     * Calculează numărul total de locuri de parcare din sistem.
     *
     * @return numărul total de locuri
     */
    @Query("SELECT SUM(p.numberOfSpots) FROM Parking p")
    Long getTotalParkingSpots();

    /**
     * Calculează numărul total de locuri rezervate din sistem.
     *
     * @return numărul total de locuri rezervate
     */
    @Query("SELECT SUM(p.reservedSpots) FROM Parking p")
    Long getTotalReservedSpots();

    /**
     * Calculează numărul total de locuri disponibile din sistem.
     *
     * @return numărul total de locuri disponibile
     */
    @Query("SELECT SUM(p.numberOfSpots - p.reservedSpots) FROM Parking p")
    Long getTotalAvailableSpots();

    /**
     * Calculează prețul mediu per loc de parcare.
     *
     * @return prețul mediu per loc
     */
    @Query("SELECT AVG(p.pricePerSpot) FROM Parking p")
    Double getAveragePricePerSpot();

    /**
     * Caută parcările cu cel mai mare număr de locuri.
     *
     * @param limit numărul maxim de rezultate
     * @return lista parcărilor ordonată după numărul de locuri
     */
    @Query("SELECT p FROM Parking p ORDER BY p.numberOfSpots DESC")
    List<Parking> findLargestParkings(@Param("limit") int limit);

    /**
     * Caută parcările cu cel mai mic preț per loc.
     *
     * @param limit numărul maxim de rezultate
     * @return lista parcărilor ordonată după preț (crescător)
     */
    @Query("SELECT p FROM Parking p ORDER BY p.pricePerSpot ASC")
    List<Parking> findCheapestParkings(@Param("limit") int limit);

    /**
     * Caută parcările cu cel mai mare preț per loc.
     *
     * @param limit numărul maxim de rezultate
     * @return lista parcărilor ordonată după preț (descrescător)
     */
    @Query("SELECT p FROM Parking p ORDER BY p.pricePerSpot DESC")
    List<Parking> findMostExpensiveParkings(@Param("limit") int limit);

    /**
     * Numără parcările după tip.
     *
     * @param parkingType tipul de parcare
     * @return numărul de parcări de tipul specificat
     */
    long countByParkingType(Parking.ParkingType parkingType);

    /**
     * Numără parcările acoperite.
     *
     * @param covered true pt parcări acoperite
     * @return numărul de parcări acoperite/neacoperite
     */
    long countByCovered(Boolean covered);

    /**
     * Calculează rata de ocupare pentru toate parcările.
     *
     * @return rata medie de ocupare (0-1)
     */
    @Query("SELECT " +
            "CASE WHEN SUM(p.numberOfSpots) = 0 THEN 0.0 " +
            "ELSE SUM(p.reservedSpots) * 1.0 / SUM(p.numberOfSpots) " +
            "END " +
            "FROM Parking p")
    Double getOverallOccupancyRate();

    /**
     * Caută parcări cu scor de calitate ridicat.
     * Calculează scorul pe baza facilităților disponibile.
     *
     * @param minScore scorul minim de calitate
     * @return lista parcărilor cu scorul specificat
     */
    @Query("SELECT p FROM Parking p WHERE " +
            "(CASE WHEN p.covered = true THEN 15 ELSE 0 END) + " +
            "(CASE WHEN p.securityCameras = true OR p.securityGuard = true OR p.accessCardRequired = true THEN 20 ELSE 0 END) + " +
            "(CASE WHEN p.disabledAccessSpots > 0 THEN 10 ELSE 0 END) + " +
            "(CASE WHEN p.electricChargingSpots > 0 THEN 15 ELSE 0 END) + " +
            "(CASE WHEN p.parkingType IN ('UNDERGROUND', 'GARAGE') THEN 10 ELSE 0 END) + 50 >= :minScore")
    List<Parking> findParkingsWithMinimumQualityScore(@Param("minScore") int minScore);

    /**
     * Caută parcări asociate cu spații comerciale dintr-o anumită clădire.
     *
     * @param buildingId ID-ul clădirii
     * @return lista parcărilor asociate cu spații din clădirea specificată
     */
    @Query("SELECT p FROM Parking p JOIN p.space s WHERE s.building.id = :buildingId")
    List<Parking> findParkingsByBuildingId(@Param("buildingId") Long buildingId);

    /**
     * Caută parcări asociate cu spații comerciale de un anumit tip.
     *
     * @param spaceType tipul de spațiu comercial
     * @return lista parcărilor asociate cu spații de tipul specificat
     */
    @Query("SELECT p FROM Parking p JOIN p.space s WHERE s.spaceType = :spaceType")
    List<Parking> findParkingsBySpaceType(@Param("spaceType") com.example.demo.entity.SpaceType spaceType);

    /**
     * Caută parcări neocupate (fără spațiu comercial asociat).
     *
     * @return lista parcărilor fără spațiu asociat
     */
    @Query("SELECT p FROM Parking p WHERE p.space IS NULL")
    List<Parking> findUnassignedParkings();

    /**
     * Calculează valoarea totală a unei parcări (numărul de locuri * prețul per loc).
     *
     * @param parkingId ID-ul parcării
     * @return valoarea totală a parcării
     */
    @Query("SELECT (p.numberOfSpots * p.pricePerSpot) FROM Parking p WHERE p.id = :parkingId")
    Double calculateTotalValueForParking(@Param("parkingId") Long parkingId);
}