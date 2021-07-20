package com.godrive.repository;

import com.godrive.domain.Codes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Codes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CodesRepository extends JpaRepository<Codes, Long> {}
