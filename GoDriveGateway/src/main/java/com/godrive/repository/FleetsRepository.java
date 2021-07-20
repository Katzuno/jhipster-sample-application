package com.godrive.repository;

import com.godrive.domain.Fleets;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Fleets entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FleetsRepository extends R2dbcRepository<Fleets, Long>, FleetsRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Fleets> findAll();

    @Override
    Mono<Fleets> findById(Long id);

    @Override
    <S extends Fleets> Mono<S> save(S entity);
}

interface FleetsRepositoryInternal {
    <S extends Fleets> Mono<S> insert(S entity);
    <S extends Fleets> Mono<S> save(S entity);
    Mono<Integer> update(Fleets entity);

    Flux<Fleets> findAll();
    Mono<Fleets> findById(Long id);
    Flux<Fleets> findAllBy(Pageable pageable);
    Flux<Fleets> findAllBy(Pageable pageable, Criteria criteria);
}
