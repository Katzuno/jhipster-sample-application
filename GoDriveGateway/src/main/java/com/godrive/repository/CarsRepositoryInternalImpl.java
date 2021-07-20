package com.godrive.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.godrive.domain.Cars;
import com.godrive.repository.rowmapper.CarsRowMapper;
import com.godrive.repository.rowmapper.FleetsRowMapper;
import com.godrive.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Cars entity.
 */
@SuppressWarnings("unused")
class CarsRepositoryInternalImpl implements CarsRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final FleetsRowMapper fleetsMapper;
    private final CarsRowMapper carsMapper;

    private static final Table entityTable = Table.aliased("cars", EntityManager.ENTITY_ALIAS);
    private static final Table fleetIdTable = Table.aliased("fleets", "fleetId");

    public CarsRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        FleetsRowMapper fleetsMapper,
        CarsRowMapper carsMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.fleetsMapper = fleetsMapper;
        this.carsMapper = carsMapper;
    }

    @Override
    public Flux<Cars> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Cars> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Cars> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CarsSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(FleetsSqlHelper.getColumns(fleetIdTable, "fleetId"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(fleetIdTable)
            .on(Column.create("fleet_id_id", entityTable))
            .equals(Column.create("id", fleetIdTable));

        String select = entityManager.createSelect(selectFrom, Cars.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Cars> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Cars> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Cars process(Row row, RowMetadata metadata) {
        Cars entity = carsMapper.apply(row, "e");
        entity.setFleetId(fleetsMapper.apply(row, "fleetId"));
        return entity;
    }

    @Override
    public <S extends Cars> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Cars> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Cars with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Cars entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CarsSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("car_plate", table, columnPrefix + "_car_plate"));
        columns.add(Column.aliased("vin", table, columnPrefix + "_vin"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));
        columns.add(Column.aliased("updated_at", table, columnPrefix + "_updated_at"));

        columns.add(Column.aliased("fleet_id_id", table, columnPrefix + "_fleet_id_id"));
        return columns;
    }
}
