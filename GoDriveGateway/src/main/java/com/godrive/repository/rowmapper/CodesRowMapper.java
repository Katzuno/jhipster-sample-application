package com.godrive.repository.rowmapper;

import com.godrive.domain.Codes;
import com.godrive.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Codes}, with proper type conversions.
 */
@Service
public class CodesRowMapper implements BiFunction<Row, String, Codes> {

    private final ColumnConverter converter;

    public CodesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Codes} stored in the database.
     */
    @Override
    public Codes apply(Row row, String prefix) {
        Codes entity = new Codes();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setMode(converter.fromRow(row, prefix + "_mode", String.class));
        entity.setSegment(converter.fromRow(row, prefix + "_segment", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDimension(converter.fromRow(row, prefix + "_dimension", Float.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setMinVal(converter.fromRow(row, prefix + "_min_val", String.class));
        entity.setMaxVal(converter.fromRow(row, prefix + "_max_val", String.class));
        entity.setUnits(converter.fromRow(row, prefix + "_units", String.class));
        entity.setEnabled(converter.fromRow(row, prefix + "_enabled", Boolean.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", ZonedDateTime.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", ZonedDateTime.class));
        return entity;
    }
}
