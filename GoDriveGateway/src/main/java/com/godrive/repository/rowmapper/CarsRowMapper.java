package com.godrive.repository.rowmapper;

import com.godrive.domain.Cars;
import com.godrive.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Cars}, with proper type conversions.
 */
@Service
public class CarsRowMapper implements BiFunction<Row, String, Cars> {

    private final ColumnConverter converter;

    public CarsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Cars} stored in the database.
     */
    @Override
    public Cars apply(Row row, String prefix) {
        Cars entity = new Cars();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setCarPlate(converter.fromRow(row, prefix + "_car_plate", String.class));
        entity.setVin(converter.fromRow(row, prefix + "_vin", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", ZonedDateTime.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", ZonedDateTime.class));
        entity.setFleetIdId(converter.fromRow(row, prefix + "_fleet_id_id", Long.class));
        return entity;
    }
}
