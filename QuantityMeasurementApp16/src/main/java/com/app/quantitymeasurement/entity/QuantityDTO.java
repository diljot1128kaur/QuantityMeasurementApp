package com.app.quantitymeasurement.entity;

import com.app.quantitymeasurement.unit.IMeasurable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class QuantityDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double value;
    private final String unitName;
    private final String measurementType;

    public QuantityDTO(double value, IMeasurable unit) {
        this(value, unit == null ? null : unit.getUnitName(), unit == null ? null : unit.getMeasurementType());
    }

    public QuantityDTO(double value, String unitName, String measurementType) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Quantity value must be a finite number");
        }
        if (unitName == null || unitName.trim().isEmpty()) {
            throw new IllegalArgumentException("Unit name cannot be null or empty");
        }
        if (measurementType == null || measurementType.trim().isEmpty()) {
            throw new IllegalArgumentException("Measurement type cannot be null or empty");
        }
        this.value = value;
        this.unitName = unitName.trim().toUpperCase();
        this.measurementType = measurementType.trim();
    }

    public double getValue() {
        return value;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QuantityDTO)) {
            return false;
        }
        QuantityDTO other = (QuantityDTO) obj;
        return Double.compare(value, other.value) == 0 && unitName.equals(other.unitName) && measurementType.equals(other.measurementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unitName, measurementType);
    }

    @Override
    public String toString() {
        return "QuantityDTO[value=" + value + ", unitName=" + unitName + ", measurementType=" + measurementType + "]";
    }
}

