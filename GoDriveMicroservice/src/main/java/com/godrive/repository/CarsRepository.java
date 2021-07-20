package com.godrive.repository;

import com.godrive.domain.Cars;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Cars entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarsRepository extends JpaRepository<Cars, Long> {}
