package com.app.quantitymeasurement.unit;

public enum LengthUnit implements IMeasurable {
    FEET(1.0),
    INCHES(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(0.393701 / 12.0);

    private final double toFeetFactor;

    LengthUnit(double toFeetFactor) {
        this.toFeetFactor = toFeetFactor;
    }

    @Override
    public double getConversionFactor() {
        return toFeetFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Length value must be a finite number");
        }
        double baseValue = value * toFeetFactor;
        if (!Double.isFinite(baseValue)) {
            throw new IllegalArgumentException("Converted value is out of range");
        }
        return baseValue;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        if (!Double.isFinite(baseValue)) {
            throw new IllegalArgumentException("Length value must be a finite number");
        }
        double convertedValue = baseValue / toFeetFactor;
        if (!Double.isFinite(convertedValue)) {
            throw new IllegalArgumentException("Converted value is out of range");
        }
        return convertedValue;
    }

    public static LengthUnit from(String unitText) {
        if (unitText == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        String normalized = unitText.trim().toUpperCase();
        if (normalized.equals("FOOT") || normalized.equals("FEET")) {
            return FEET;
        }
        if (normalized.equals("INCH") || normalized.equals("INCHES")) {
            return INCHES;
        }
        if (normalized.equals("YARD") || normalized.equals("YARDS") || normalized.equals("YD")) {
            return YARDS;
        }
        if (normalized.equals("CENTIMETER") || normalized.equals("CENTIMETERS") || normalized.equals("CM")) {
            return CENTIMETERS;
        }
        throw new IllegalArgumentException("Unsupported unit: " + unitText);
    }

    @Override
    public String getUnitName() {
        return name();
    }

    @Override
    public IMeasurable getUnitInstance(String unitName) {
        return from(unitName);
    }
}

