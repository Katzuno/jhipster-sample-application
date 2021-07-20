package com.godrive.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Codes.
 */
@Table("codes")
public class Codes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("mode")
    private String mode;

    @Column("segment")
    private String segment;

    @Column("code")
    private String code;

    @Column("dimension")
    private Float dimension;

    @Column("description")
    private String description;

    @Column("min_val")
    private String minVal;

    @Column("max_val")
    private String maxVal;

    @Column("units")
    private String units;

    @Column("enabled")
    private Boolean enabled;

    @Column("created_at")
    private ZonedDateTime createdAt;

    @Column("updated_at")
    private ZonedDateTime updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Codes id(Long id) {
        this.id = id;
        return this;
    }

    public String getMode() {
        return this.mode;
    }

    public Codes mode(String mode) {
        this.mode = mode;
        return this;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSegment() {
        return this.segment;
    }

    public Codes segment(String segment) {
        this.segment = segment;
        return this;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getCode() {
        return this.code;
    }

    public Codes code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Float getDimension() {
        return this.dimension;
    }

    public Codes dimension(Float dimension) {
        this.dimension = dimension;
        return this;
    }

    public void setDimension(Float dimension) {
        this.dimension = dimension;
    }

    public String getDescription() {
        return this.description;
    }

    public Codes description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMinVal() {
        return this.minVal;
    }

    public Codes minVal(String minVal) {
        this.minVal = minVal;
        return this;
    }

    public void setMinVal(String minVal) {
        this.minVal = minVal;
    }

    public String getMaxVal() {
        return this.maxVal;
    }

    public Codes maxVal(String maxVal) {
        this.maxVal = maxVal;
        return this;
    }

    public void setMaxVal(String maxVal) {
        this.maxVal = maxVal;
    }

    public String getUnits() {
        return this.units;
    }

    public Codes units(String units) {
        this.units = units;
        return this;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public Codes enabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }

    public Codes createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Codes updatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Codes)) {
            return false;
        }
        return id != null && id.equals(((Codes) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Codes{" +
            "id=" + getId() +
            ", mode='" + getMode() + "'" +
            ", segment='" + getSegment() + "'" +
            ", code='" + getCode() + "'" +
            ", dimension=" + getDimension() +
            ", description='" + getDescription() + "'" +
            ", minVal='" + getMinVal() + "'" +
            ", maxVal='" + getMaxVal() + "'" +
            ", units='" + getUnits() + "'" +
            ", enabled='" + getEnabled() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
