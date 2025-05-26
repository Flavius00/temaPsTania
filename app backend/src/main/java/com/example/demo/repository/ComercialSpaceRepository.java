package com.example.demo.repository;

import com.example.demo.model.ComercialSpace;
import com.example.demo.constants.SpaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pentru entitatea ComercialSpace.
 *
 * Extinde JpaRepository pentru operațiunile CRUD de bază și adaugă
 * metode de căutare specifice pentru spații comerciale.
 *
 * Principii SOLID respectate:
 * - Interface Segregation: interfață specifică pentru operațiunile cu ComercialSpace
 * - Dependency Inversion: depinde de abstracțiuni, nu de implementări concrete
 */
@Repository
public interface ComercialSpaceRepository extends JpaRepository<ComercialSpace, Long> {

    /**
     * Caută spații comerciale disponibile.
     *
     * @param available statusul de disponibilitate
     * @return lista spațiilor cu statusul specificat
     */
    List<ComercialSpace> findByAvailable(Boolean available);

    /**
     * Caută spații comerciale după tip.
     *
     * @param spaceType tipul de spațiu căutat
     * @return lista spațiilor de tipul specificat
     */
    List<ComercialSpace> findBySpaceType(SpaceType spaceType);

    /**
     * Caută spații comerciale după proprietar.
     *
     * @param ownerId ID-ul proprietarului
     * @return lista spațiilor proprietarului specificat
     */
    List<ComercialSpace> findByOwnerId(Long ownerId);

    /**
     * Caută spații comerciale după clădire.
     *
     * @param buildingId ID-ul clădirii
     * @return lista spațiilor din clădirea specificată
     */
    List<ComercialSpace> findByBuildingId(Long buildingId);

    /**
     * Caută spații comerciale în intervalul de preț specificat.
     *
     * @param minPrice prețul minim
     * @param maxPrice prețul maxim
     * @return lista spațiilor cu prețurile în intervalul specificat
     */
    List<ComercialSpace> findByPricePerMonthBetween(Double minPrice, Double maxPrice);

    /**
     * Caută spații comerciale în intervalul de suprafață specificat.
     *
     * @param minArea suprafața minimă
     * @param maxArea suprafața maximă
     * @return lista spațiilor cu suprafața în intervalul specificat
     */
    List<ComercialSpace> findByAreaBetween(Double minArea, Double maxArea);

    /**
     * Caută spații comerciale după nume (case insensitive).
     *
     * @param name numele căutat
     * @return lista spațiilor cu numele specificat
     */
    List<ComercialSpace> findByNameContainingIgnoreCase(String name);

    /**
     * Caută spații comerciale cu preț mai mic decât valoarea specificată.
     *
     * @param maxPrice prețul maxim
     * @return lista spațiilor cu preț mai mic decât specificat
     */
    List<ComercialSpace> findByPricePerMonthLessThanEqual(Double maxPrice);

    /**
     * Caută spații comerciale cu suprafață mai mare decât valoarea specificată.
     *
     * @param minArea suprafața minimă
     * @return lista spațiilor cu suprafață mai mare decât specificat
     */
    List<ComercialSpace> findByAreaGreaterThanEqual(Double minArea);

    /**
     * Caută spații comerciale cu aer condiționat.
     *
     * @param airConditioning true pentru spații cu aer condiționat
     * @return lista spațiilor cu/fără aer condiționat
     */
    List<ComercialSpace> findByAirConditioning(Boolean airConditioning);

    /**
     * Caută spații comerciale mobilate.
     *
     * @param furnished true pentru spații mobilate
     * @return lista spațiilor mobilate/nemobilate
     */
    List<ComercialSpace> findByFurnished(Boolean furnished);

    /**
     * Caută spații comerciale cu parcare.
     *
     * @return lista spațiilor care au parcare asociată
     */
    @Query("SELECT s FROM ComercialSpace s WHERE s.parking IS NOT NULL")
    List<ComercialSpace> findSpacesWithParking();

    /**
     * Caută spații comerciale fără parcare.
     *
     * @return lista spațiilor fără parcare
     */
    @Query("SELECT s FROM ComercialSpace s WHERE s.parking IS NULL")
    List<ComercialSpace> findSpacesWithoutParking();

    /**
     * Caută spații comerciale cu contract activ.
     *
     * @return lista spațiilor cu contract activ
     */
    @Query("SELECT DISTINCT s FROM ComercialSpace s JOIN s.contracts c WHERE c.status = 'ACTIVE'")
    List<ComercialSpace> findSpacesWithActiveContract();

    /**
     * Caută spații comerciale fără contract activ.
     *
     * @return lista spațiilor fără contract activ
     */
    @Query("SELECT s FROM ComercialSpace s WHERE s NOT IN " +
            "(SELECT DISTINCT c.space FROM RentalContract c WHERE c.status = 'ACTIVE')")
    List<ComercialSpace> findSpacesWithoutActiveContract();

    /**
     * Caută spații comerciale după criteriile specificate.
     *
     * @param spaceType tipul de spațiu (poate fi null)
     * @param minPrice prețul minim (poate fi null)
     * @param maxPrice prețul maxim (poate fi null)
     * @param minArea suprafața minimă (poate fi null)
     * @param maxArea suprafața maximă (poate fi null)
     * @param available disponibilitatea (poate fi null)
     * @return lista spațiilor care îndeplinesc criteriile
     */
    @Query("SELECT s FROM ComercialSpace s WHERE " +
            "(:spaceType IS NULL OR s.spaceType = :spaceType) AND " +
            "(:minPrice IS NULL OR s.pricePerMonth >= :minPrice) AND " +
            "(:maxPrice IS NULL OR s.pricePerMonth <= :maxPrice) AND " +
            "(:minArea IS NULL OR s.area >= :minArea) AND " +
            "(:maxArea IS NULL OR s.area <= :maxArea) AND " +
            "(:available IS NULL OR s.available = :available)")
    List<ComercialSpace> findSpacesByCriteria(@Param("spaceType") SpaceType spaceType,
                                              @Param("minPrice") Double minPrice,
                                              @Param("maxPrice") Double maxPrice,
                                              @Param("minArea") Double minArea,
                                              @Param("maxArea") Double maxArea,
                                              @Param("available") Boolean available);

    /**
     * Caută spațiile cu cel mai mare preț pe metru pătrat.
     *
     * @param limit numărul maxim de rezultate
     * @return lista spațiilor ordonată după prețul pe mp
     */
    @Query("SELECT s FROM ComercialSpace s ORDER BY (s.pricePerMonth / s.area) DESC")
    List<ComercialSpace> findSpacesWithHighestPricePerSquareMeter(@Param("limit") int limit);

    /**
     * Caută spațiile cu cel mai mic preț pe metru pătrat.
     *
     * @param limit numărul maxim de rezultate
     * @return lista spațiilor ordonată după prețul pe mp (crescător)
     */
    @Query("SELECT s FROM ComercialSpace s ORDER BY (s.pricePerMonth / s.area) ASC")
    List<ComercialSpace> findSpacesWithLowestPricePerSquareMeter(@Param("limit") int limit);

    /**
     * Caută spații comerciale într-o anumită zonă geografică.
     *
     * @param minLat latitudinea minimă
     * @param maxLat latitudinea maximă
     * @param minLng longitudinea minimă
     * @param maxLng longitudinea maximă
     * @return lista spațiilor din zona specificată
     */
    @Query("SELECT s FROM ComercialSpace s WHERE " +
            "s.latitude BETWEEN :minLat AND :maxLat AND " +
            "s.longitude BETWEEN :minLng AND :maxLng")
    List<ComercialSpace> findSpacesInArea(@Param("minLat") Double minLat,
                                          @Param("maxLat") Double maxLat,
                                          @Param("minLng") Double minLng,
                                          @Param("maxLng") Double maxLng);

    /**
     * Numără spațiile după tip.
     *
     * @param spaceType tipul de spațiu
     * @return numărul de spații de tipul specificat
     */
    long countBySpaceType(SpaceType spaceType);

    /**
     * Numără spațiile disponibile după tip.
     *
     * @param spaceType tipul de spațiu
     * @return numărul de spații disponibile de tipul specificat
     */
    long countBySpaceTypeAndAvailable(SpaceType spaceType, Boolean available);

    /**
     * Calculează prețul mediu pentru un tip de spațiu.
     *
     * @param spaceType tipul de spațiu
     * @return prețul mediu pentru tipul specificat
     */
    @Query("SELECT AVG(s.pricePerMonth) FROM ComercialSpace s WHERE s.spaceType = :spaceType")
    Double getAveragePriceBySpaceType(@Param("spaceType") SpaceType spaceType);

    /**
     * Calculează suprafața medie pentru un tip de spațiu.
     *
     * @param spaceType tipul de spațiu
     * @return suprafața medie pentru tipul specificat
     */
    @Query("SELECT AVG(s.area) FROM ComercialSpace s WHERE s.spaceType = :spaceType")
    Double getAverageAreaBySpaceType(@Param("spaceType") SpaceType spaceType);

    /**
     * Caută spații comerciale cu facilități specifice.
     *
     * @param amenity facilitatea căutată
     * @return lista spațiilor cu facilitatea specificată
     */
    @Query("SELECT s FROM ComercialSpace s JOIN s.amenities a WHERE a = :amenity")
    List<ComercialSpace> findSpacesByAmenity(@Param("amenity") String amenity);

    /**
     * Caută spațiile cu cele mai multe facilități.
     *
     * @param limit numărul maxim de rezultate
     * @return lista spațiilor ordonată după numărul de facilități
     */
    @Query("SELECT s FROM ComercialSpace s ORDER BY SIZE(s.amenities) DESC")
    List<ComercialSpace> findSpacesWithMostAmenities(@Param("limit") int limit);

    /**
     * Statistici - prețul mediu per metru pătrat.
     *
     * @return prețul mediu per mp
     */
    @Query("SELECT AVG(s.pricePerMonth / s.area) FROM ComercialSpace s")
    Double getAveragePricePerSquareMeter();

    /**
     * Caută spații comerciale fără coordonate geografice.
     *
     * @return lista spațiilor fără coordonate
     */
    @Query("SELECT s FROM ComercialSpace s WHERE s.latitude IS NULL OR s.longitude IS NULL")
    List<ComercialSpace> findSpacesWithoutCoordinates();

    /**
     * Caută spațiile cu contractele care expiră în curând.
     *
     * @param daysUntilExpiration numărul de zile până la expirare
     * @return lista spațiilor cu contracte care expiră în perioada specificată
     */
    @Query("SELECT DISTINCT s FROM ComercialSpace s JOIN s.contracts c " +
            "WHERE c.status = 'ACTIVE' AND c.endDate <= CURRENT_DATE + :daysUntilExpiration")
    List<ComercialSpace> findSpacesWithExpiringContracts(@Param("daysUntilExpiration") int daysUntilExpiration);

}