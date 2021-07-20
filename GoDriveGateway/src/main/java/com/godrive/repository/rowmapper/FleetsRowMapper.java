package com.godrive.repository.rowmapper;

import com.godrive.domain.Fleets;
import com.godrive.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Fleets}, with proper type conversions.
 */
@Service
public class FleetsRowMapper implements BiFunction<Row, String, Fleets> {

    private final ColumnConverter converter;

    public FleetsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Fleets} stored in the database.
     */
    @Override
    public Fleets apply(Row row, String prefix) {
        Fleets entity = new Fleets();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", ZonedDateTime.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", ZonedDateTime.class));
        return entity;
    }
}
