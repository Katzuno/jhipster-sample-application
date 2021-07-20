package com.godrive.repository;

import com.godrive.domain.Codes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Codes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CodesRepository extends R2dbcRepository<Codes, Long>, CodesRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<Codes> findAll();

    @Override
    Mono<Codes> findById(Long id);

    @Override
    <S extends Codes> Mono<S> save(S entity);
}

interface CodesRepositoryInternal {
    <S extends Codes> Mono<S> insert(S entity);
    <S extends Codes> Mono<S> save(S entity);
    Mono<Integer> update(Codes entity);

    Flux<Codes> findAll();
    Mono<Codes> findById(Long id);
    Flux<Codes> findAllBy(Pageable pageable);
    Flux<Codes> findAllBy(Pageable pageable, Criteria criteria);
}
