package com.app.quantitymeasurement.unit;

import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {
    CELSIUS(celsius -> celsius + 273.15, kelvin -> kelvin - 273.15),
    FAHRENHEIT(fahrenheit -> (fahrenheit - 32.0) * 5.0 / 9.0 + 273.15, kelvin -> (kelvin - 273.15) * 9.0 / 5.0 + 32.0),
    KELVIN(kelvin -> kelvin, kelvin -> kelvin);

    private static final SupportsArithmetic SUPPORTS_ARITHMETIC = () -> false;

    private final Function<Double, Double> toKelvin;
    private final Function<Double, Double> fromKelvin;

    TemperatureUnit(Function<Double, Double> toKelvin, Function<Double, Double> fromKelvin) {
        this.toKelvin = toKelvin;
        this.fromKelvin = fromKelvin;
    }

    @Override
    public double getConversionFactor() {
        return 1.0;
    }

    @Override
    public double convertToBaseUnit(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Temperature value must be a finite number");
        }
        double baseValue = toKelvin.apply(value);
        if (!Double.isFinite(baseValue)) {
            throw new IllegalArgumentException("Converted value is out of range");
        }
        return baseValue;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        if (!Double.isFinite(baseValue)) {
            throw new IllegalArgumentException("Temperature value must be a finite number");
        }
        double convertedValue = fromKelvin.apply(baseValue);
        if (!Double.isFinite(convertedValue)) {
            throw new IllegalArgumentException("Converted value is out of range");
        }
        return convertedValue;
    }

    public static TemperatureUnit from(String unitText) {
        if (unitText == null) {
            throw new IllegalArgumentException("Unit cannot be null");
        }
        String normalized = unitText.trim().toUpperCase();
        if (normalized.equals("C") || normalized.equals("CELSIUS")) {
            return CELSIUS;
        }
        if (normalized.equals("F") || normalized.equals("FAHRENHEIT")) {
            return FAHRENHEIT;
        }
        if (normalized.equals("K") || normalized.equals("KELVIN")) {
            return KELVIN;
        }
        throw new IllegalArgumentException("Unsupported unit: " + unitText);
    }

    @Override
    public boolean supportsArithmetic() {
        return SUPPORTS_ARITHMETIC.isSupported();
    }

    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException("Temperature does not support " + operation.toLowerCase() + " operation");
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

