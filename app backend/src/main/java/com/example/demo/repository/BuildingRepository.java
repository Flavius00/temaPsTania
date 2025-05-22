package com.example.demo.repository;

import com.example.demo.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    // Metode de căutare personalizate pot fi adăugate aici
}