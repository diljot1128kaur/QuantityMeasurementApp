package com.app.quantitymeasurement.unit;

public enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double toLitreFactor;

    VolumeUnit(double toLitreFactor) {
        this.toLitreFactor = toLitreFactor;
    }

    @Override
    public double getConversionFactor() {
        return toLitreFactor;
    }

    @Override
    public double convertToBaseUnit(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Volume value must be a finite number");
        }
        double baseValue = value * toLitreFactor;
        if (!Double.isFinite(baseValue)) {
            throw new IllegalArgumentException("Converted value is out of range");
        }
        return baseValue;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        if (!Double.isFinite(baseValue)) {
            throw new IllegalArgumentException("Volume value must be a finite number");
        }
        double convertedValue = baseValue / toLitreFactor;
        if (!Double.isFinite(convertedValue)) {
            throw new IllegalArgumentException("Converted value is out of range");
        }
        return convertedValue;
    }

    public static VolumeUnit from(String unitText) {
        if (unitText == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        String normalized = unitText.trim().toUpperCase();
        if (normalized.equals("L") || normalized.equals("LITRE") || normalized.equals("LITRES") || normalized.equals("LITER") || normalized.equals("LITERS")) {
            return LITRE;
        }
        if (normalized.equals("ML") || normalized.equals("MILLILITRE") || normalized.equals("MILLILITRES") || normalized.equals("MILLILITER") || normalized.equals("MILLILITERS")) {
            return MILLILITRE;
        }
        if (normalized.equals("GAL") || normalized.equals("GALLON") || normalized.equals("GALLONS")) {
            return GALLON;
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

