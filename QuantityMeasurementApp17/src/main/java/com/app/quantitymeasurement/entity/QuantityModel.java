package com.app.quantitymeasurement.entity;

import com.app.quantitymeasurement.unit.IMeasurable;

public final class QuantityModel<U extends IMeasurable> {
    private final double value;
    private final U unit;

    public QuantityModel(double value, U unit) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Quantity value must be a finite number");
        }
        if (unit == null) {
            throw new IllegalArgumentException("Quantity unit cannot be null");
        }
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public U getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return "QuantityModel[value=" + value + ", unit=" + unit.getUnitName() + "]";
    }
}

