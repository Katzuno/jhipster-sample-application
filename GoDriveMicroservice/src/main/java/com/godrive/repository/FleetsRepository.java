package com.godrive.repository;

import com.godrive.domain.Fleets;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Fleets entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FleetsRepository extends JpaRepository<Fleets, Long> {}
