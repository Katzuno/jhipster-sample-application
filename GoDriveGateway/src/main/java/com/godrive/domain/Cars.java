package com.godrive.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Cars.
 */
@Table("cars")
public class Cars implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("car_plate")
    private String carPlate;

    @Column("vin")
    private String vin;

    @Column("created_at")
    private ZonedDateTime createdAt;

    @Column("updated_at")
    private ZonedDateTime updatedAt;

    @JsonIgnoreProperties(value = { "cars" }, allowSetters = true)
    @Transient
    private Fleets fleetId;

    @Column("fleet_id_id")
    private Long fleetIdId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cars id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Cars name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Cars description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCarPlate() {
        return this.carPlate;
    }

    public Cars carPlate(String carPlate) {
        this.carPlate = carPlate;
        return this;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    public String getVin() {
        return this.vin;
    }

    public Cars vin(String vin) {
        this.vin = vin;
        return this;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Cars createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Cars updatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Fleets getFleetId() {
        return this.fleetId;
    }

    public Cars fleetId(Fleets fleets) {
        this.setFleetId(fleets);
        this.fleetIdId = fleets != null ? fleets.getId() : null;
        return this;
    }

    public void setFleetId(Fleets fleets) {
        this.fleetId = fleets;
        this.fleetIdId = fleets != null ? fleets.getId() : null;
    }

    public Long getFleetIdId() {
        return this.fleetIdId;
    }

    public void setFleetIdId(Long fleets) {
        this.fleetIdId = fleets;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cars)) {
            return false;
        }
        return id != null && id.equals(((Cars) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cars{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", carPlate='" + getCarPlate() + "'" +
            ", vin='" + getVin() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
