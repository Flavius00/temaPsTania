package com.example.demo.repository;

import com.example.demo.entity.RentalContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalContractRepository extends JpaRepository<RentalContract, Long> {

    List<RentalContract> findByTenantId(Long tenantId);

    List<RentalContract> findBySpaceOwnerId(Long ownerId);

    List<RentalContract> findBySpaceId(Long spaceId);

    List<RentalContract> findByStatus(String status);

    List<RentalContract> findByStartDateAfterAndEndDateBefore(LocalDate startDate, LocalDate endDate);
}