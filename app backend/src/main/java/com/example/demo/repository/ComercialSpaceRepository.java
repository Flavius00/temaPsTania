package com.example.demo.repository;

import com.example.demo.entity.ComercialSpace;
import com.example.demo.entity.SpaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComercialSpaceRepository extends JpaRepository<ComercialSpace, Long> {

    List<ComercialSpace> findByAvailable(Boolean available);

    List<ComercialSpace> findBySpaceType(SpaceType spaceType);

    List<ComercialSpace> findByOwnerId(Long ownerId);

    List<ComercialSpace> findByBuildingId(Long buildingId);

    List<ComercialSpace> findByPricePerMonthBetween(Double minPrice, Double maxPrice);
}