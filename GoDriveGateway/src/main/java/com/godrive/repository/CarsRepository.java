package com.godrive.repository;

import com.godrive.domain.Cars;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Cars entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarsRepository extends R2dbcRepository<Cars, Long>, CarsRepositoryInternal {
    @Query("SELECT * FROM cars entity WHERE entity.fleet_id_id = :id")
    Flux<Cars> findByFleetId(Long id);

    @Query("SELECT * FROM cars entity WHERE entity.fleet_id_id IS NULL")
    Flux<Cars> findAllWhereFleetIdIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Cars> findAll();

    @Override
    Mono<Cars> findById(Long id);

    @Override
    <S extends Cars> Mono<S> save(S entity);
}

interface CarsRepositoryInternal {
    <S extends Cars> Mono<S> insert(S entity);
    <S extends Cars> Mono<S> save(S entity);
    Mono<Integer> update(Cars entity);

    Flux<Cars> findAll();
    Mono<Cars> findById(Long id);
    Flux<Cars> findAllBy(Pageable pageable);
    Flux<Cars> findAllBy(Pageable pageable, Criteria criteria);
}
