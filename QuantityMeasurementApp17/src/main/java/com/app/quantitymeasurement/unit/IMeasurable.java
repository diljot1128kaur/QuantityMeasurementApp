package com.app.quantitymeasurement.unit;

public interface IMeasurable {
    SupportsArithmetic DEFAULT_SUPPORTS_ARITHMETIC = () -> true;

    double getConversionFactor();

    double convertToBaseUnit(double value);

    double convertFromBaseUnit(double baseValue);

    String getUnitName();

    default String getMeasurementType() {
        return getClass().getSimpleName();
    }

    default IMeasurable getUnitInstance(String unitName) {
        throw new UnsupportedOperationException("Unit lookup is not implemented for " + getMeasurementType());
    }

    default boolean supportsArithmetic() {
        return DEFAULT_SUPPORTS_ARITHMETIC.isSupported();
    }

    default void validateOperationSupport(String operation) {
        if (operation == null || operation.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation cannot be null or empty");
        }
    }
}

