package com.example.demo.repository;

import com.example.demo.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pentru entitatea Building.
 *
 * Extinde JpaRepository pentru operațiunile CRUD de bază și adaugă
 * metode de căutare specifice pentru clădiri.
 *
 * Principii SOLID respectate:
 * - Interface Segregation: interfață specifică pentru operațiunile cu Building
 * - Dependency Inversion: depinde de abstracțiuni, nu de implementări concrete
 */
@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    /**
     * Caută clădiri după nume (case insensitive).
     *
     * @param name numele căutat
     * @return lista clădirilor cu numele specificat
     */
    List<Building> findByNameContainingIgnoreCase(String name);

    /**
     * Caută clădiri după adresă (case insensitive).
     *
     * @param address adresa căutată
     * @return lista clădirilor cu adresa specificată
     */
    List<Building> findByAddressContainingIgnoreCase(String address);

    /**
     * Caută o clădire după numele exact.
     *
     * @param name numele exact al clădirii
     * @return clădirea găsită sau Optional.empty()
     */
    Optional<Building> findByName(String name);

    /**
     * Caută clădiri după anul construcției.
     *
     * @param yearBuilt anul construcției
     * @return lista clădirilor construite în anul specificat
     */
    List<Building> findByYearBuilt(Integer yearBuilt);

    /**
     * Caută clădiri construite după un anumit an.
     *
     * @param year anul de referință
     * @return lista clădirilor construite după anul specificat
     */
    List<Building> findByYearBuiltGreaterThan(Integer year);

    /**
     * Caută clădiri construite înainte de un anumit an.
     *
     * @param year anul de referință
     * @return lista clădirilor construite înainte de anul specificat
     */
    List<Building> findByYearBuiltLessThan(Integer year);

    /**
     * Caută clădiri după numărul de etaje.
     *
     * @param totalFloors numărul de etaje
     * @return lista clădirilor cu numărul specificat de etaje
     */
    List<Building> findByTotalFloors(Integer totalFloors);

    /**
     * Caută clădiri cu numărul minim de etaje.
     *
     * @param minFloors numărul minim de etaje
     * @return lista clădirilor cu cel puțin numărul specificat de etaje
     */
    List<Building> findByTotalFloorsGreaterThanEqual(Integer minFloors);

    /**
     * Caută clădiri după tipul de clădire.
     *
     * @param buildingType tipul de clădire
     * @return lista clădirilor de tipul specificat
     */
    List<Building> findByBuildingType(Building.BuildingType buildingType);

    /**
     * Caută clădiri cu lift disponibil.
     *
     * @param elevatorAvailable true pentru clădiri cu lift
     * @return lista clădirilor cu/fără lift
     */
    List<Building> findByElevatorAvailable(Boolean elevatorAvailable);

    /**
     * Caută clădiri cu facilități de accesibilitate.
     *
     * @param accessibilityFeatures true pentru clădiri cu facilități de accesibilitate
     * @return lista clădirilor cu/fără facilități de accesibilitate
     */
    List<Building> findByAccessibilityFeatures(Boolean accessibilityFeatures);

    /**
     * Caută clădiri cu aer condiționat.
     *
     * @param airConditioning true pentru clădiri cu aer condiționat
     * @return lista clădirilor cu/fără aer condiționat
     */
    List<Building> findByAirConditioning(Boolean airConditioning);

    /**
     * Caută clădiri cu spații disponibile.
     *
     * @return lista clădirilor care au spații disponibile
     */
    @Query("SELECT DISTINCT b FROM Building b JOIN b.spaces s WHERE s.available = true")
    List<Building> findBuildingsWithAvailableSpaces();

    /**
     * Caută clădiri fără spații disponibile.
     *
     * @return lista clădirilor complet ocupate
     */
    @Query("SELECT b FROM Building b WHERE b NOT IN " +
            "(SELECT DISTINCT s.building FROM ComercialSpace s WHERE s.available = true)")
    List<Building> findBuildingsWithoutAvailableSpaces();

    /**
     * Caută clădiri într-o anumită zonă geografică (dreptunghi definit de coordonate).
     *
     * @param minLat latitudinea minimă
     * @param maxLat latitudinea maximă
     * @param minLng longitudinea minimă
     * @param maxLng longitudinea maximă
     * @return lista clădirilor din zona specificată
     */
    @Query("SELECT b FROM Building b WHERE " +
            "b.latitude BETWEEN :minLat AND :maxLat AND " +
            "b.longitude BETWEEN :minLng AND :maxLng")
    List<Building> findBuildingsInArea(@Param("minLat") Double minLat,
                                       @Param("maxLat") Double maxLat,
                                       @Param("minLng") Double minLng,
                                       @Param("maxLng") Double maxLng);

    /**
     * Caută clădiri într-o rază specificată față de un punct (aproximativ).
     *
     * @param latitude latitudinea punctului central
     * @param longitude longitudinea punctului central
     * @param radiusInKm raza în kilometri
     * @return lista clădirilor din raza specificată
     */
    @Query("SELECT b FROM Building b WHERE " +
            "SQRT(POWER((b.latitude - :latitude) * 111.32, 2) + " +
            "POWER((b.longitude - :longitude) * 111.32 * COS(RADIANS(b.latitude)), 2)) <= :radiusInKm")
    List<Building> findBuildingsWithinRadius(@Param("latitude") Double latitude,
                                             @Param("longitude") Double longitude,
                                             @Param("radiusInKm") Double radiusInKm);

    /**
     * Numără spațiile totale dintr-o clădire.
     *
     * @param buildingId ID-ul clădirii
     * @return numărul total de spații
     */
    @Query("SELECT COUNT(s) FROM ComercialSpace s WHERE s.building.id = :buildingId")
    long countTotalSpacesInBuilding(@Param("buildingId") Long buildingId);

    /**
     * Numără spațiile disponibile dintr-o clădire.
     *
     * @param buildingId ID-ul clădirii
     * @return numărul de spații disponibile
     */
    @Query("SELECT COUNT(s) FROM ComercialSpace s WHERE s.building.id = :buildingId AND s.available = true")
    long countAvailableSpacesInBuilding(@Param("buildingId") Long buildingId);

    /**
     * Calculează rata de ocupare pentru o clădire.
     *
     * @param buildingId ID-ul clădirii
     * @return rata de ocupare (0-1)
     */
    @Query("SELECT " +
            "CASE WHEN COUNT(s) = 0 THEN 0.0 " +
            "ELSE (COUNT(s) - COUNT(CASE WHEN s.available = true THEN 1 END)) * 1.0 / COUNT(s) " +
            "END " +
            "FROM ComercialSpace s WHERE s.building.id = :buildingId")
    Double calculateOccupancyRateForBuilding(@Param("buildingId") Long buildingId);

    /**
     * Calculează venitul total generat de o clădire.
     *
     * @param buildingId ID-ul clădirii
     * @return venitul total din chirii
     */
    @Query("SELECT COALESCE(SUM(s.pricePerMonth), 0) FROM ComercialSpace s " +
            "WHERE s.building.id = :buildingId AND s.available = false")
    Double calculateTotalRevenueForBuilding(@Param("buildingId") Long buildingId);

    /**
     * Caută clădirile cu cea mai mare rată de ocupare.
     *
     * @param limit numărul maxim de rezultate
     * @return lista clădirilor ordonată după rata de ocupare
     */
    @Query("SELECT b FROM Building b " +
            "ORDER BY " +
            "(SELECT COUNT(s) - COUNT(CASE WHEN s.available = true THEN 1 END) " +
            "FROM ComercialSpace s WHERE s.building = b) * 1.0 / " +
            "(SELECT COUNT(s) FROM ComercialSpace s WHERE s.building = b) DESC")
    List<Building> findBuildingsWithHighestOccupancy(@Param("limit") int limit);

    /**
     * Caută clădiri după criteriile specificate.
     *
     * @param buildingType tipul de clădire (poate fi null)
     * @param minFloors numărul minim de etaje (poate fi null)
     * @param maxFloors numărul maxim de etaje (poate fi null)
     * @param hasElevator true dacă trebuie să aibă lift (poate fi null)
     * @param hasAirConditioning true dacă trebuie să aibă aer condiționat (poate fi null)
     * @return lista clădirilor care îndeplinesc criteriile
     */
    @Query("SELECT b FROM Building b WHERE " +
            "(:buildingType IS NULL OR b.buildingType = :buildingType) AND " +
            "(:minFloors IS NULL OR b.totalFloors >= :minFloors) AND " +
            "(:maxFloors IS NULL OR b.totalFloors <= :maxFloors) AND " +
            "(:hasElevator IS NULL OR b.elevatorAvailable = :hasElevator) AND " +
            "(:hasAirConditioning IS NULL OR b.airConditioning = :hasAirConditioning)")
    List<Building> findBuildingsByCriteria(@Param("buildingType") Building.BuildingType buildingType,
                                           @Param("minFloors") Integer minFloors,
                                           @Param("maxFloors") Integer maxFloors,
                                           @Param("hasElevator") Boolean hasElevator,
                                           @Param("hasAirConditioning") Boolean hasAirConditioning);

    /**
     * Caută clădiri cu cel mai mare număr de spații.
     *
     * @param limit numărul maxim de rezultate
     * @return lista clădirilor ordonată după numărul de spații
     */
    @Query("SELECT b FROM Building b ORDER BY SIZE(b.spaces) DESC")
    List<Building> findBuildingsWithMostSpaces(@Param("limit") int limit);

    /**
     * Caută clădiri fără coordonate geografice.
     *
     * @return lista clădirilor fără coordonate
     */
    @Query("SELECT b FROM Building b WHERE b.latitude IS NULL OR b.longitude IS NULL")
    List<Building> findBuildingsWithoutCoordinates();

    /**
     * Statistici - numărul mediu de spații pe clădire.
     *
     * @return numărul mediu de spații
     */
    @Query("SELECT AVG(SIZE(b.spaces)) FROM Building b")
    Double getAverageSpacesPerBuilding();

    /**
     * Statistici - anul mediu de construcție.
     *
     * @return anul mediu de construcție
     */
    @Query("SELECT AVG(b.yearBuilt) FROM Building b WHERE b.yearBuilt IS NOT NULL")
    Double getAverageYearBuilt();
}