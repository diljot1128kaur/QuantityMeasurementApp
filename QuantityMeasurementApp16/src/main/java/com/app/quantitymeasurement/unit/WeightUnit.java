package com.app.quantitymeasurement.unit;

public enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double toKilogramFactor;

    WeightUnit(double toKilogramFactor) {
        this.toKilogramFactor = toKilogramFactor;
    }

    @Override
    public double getConversionFactor() {
        return toKilogramFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Weight value must be a finite number");
        }
        double baseValue = value * toKilogramFactor;
        if (!Double.isFinite(baseValue)) {
            throw new IllegalArgumentException("Converted value is out of range");
        }
        return baseValue;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        if (!Double.isFinite(baseValue)) {
            throw new IllegalArgumentException("Weight value must be a finite number");
        }
        double convertedValue = baseValue / toKilogramFactor;
        if (!Double.isFinite(convertedValue)) {
            throw new IllegalArgumentException("Converted value is out of range");
        }
        return convertedValue;
    }

    public static WeightUnit from(String unitText) {
        if (unitText == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        String normalized = unitText.trim().toUpperCase();
        if (normalized.equals("KG") || normalized.equals("KGS") || normalized.equals("KILOGRAM") || normalized.equals("KILOGRAMS")) {
            return KILOGRAM;
        }
        if (normalized.equals("G") || normalized.equals("GRAM") || normalized.equals("GRAMS")) {
            return GRAM;
        }
        if (normalized.equals("LB") || normalized.equals("LBS") || normalized.equals("POUND") || normalized.equals("POUNDS")) {
            return POUND;
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

