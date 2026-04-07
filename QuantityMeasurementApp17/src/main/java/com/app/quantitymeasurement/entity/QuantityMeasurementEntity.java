package com.app.quantitymeasurement.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class QuantityMeasurementEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final QuantityDTO thisQuantity;
    private final QuantityDTO thatQuantity;
    private final String operation;
    private final QuantityDTO resultQuantity;
    private final Boolean comparisonResult;
    private final Double divisionResult;
    private final boolean error;
    private final String errorMessage;

    private QuantityMeasurementEntity(QuantityDTO thisQuantity,
                                      QuantityDTO thatQuantity,
                                      String operation,
                                      QuantityDTO resultQuantity,
                                      Boolean comparisonResult,
                                      Double divisionResult,
                                      boolean error,
                                      String errorMessage) {
        this.thisQuantity = thisQuantity;
        this.thatQuantity = thatQuantity;
        this.operation = operation;
        this.resultQuantity = resultQuantity;
        this.comparisonResult = comparisonResult;
        this.divisionResult = divisionResult;
        this.error = error;
        this.errorMessage = errorMessage;
    }

    public static QuantityMeasurementEntity successForComparison(QuantityDTO left, QuantityDTO right, boolean result) {
        return new QuantityMeasurementEntity(left, right, "COMPARE", null, result, null, false, null);
    }

    public static QuantityMeasurementEntity successForConversion(QuantityDTO source, QuantityDTO target, QuantityDTO result) {
        return new QuantityMeasurementEntity(source, target, "CONVERT", result, null, null, false, null);
    }

    public static QuantityMeasurementEntity successForArithmetic(String operation, QuantityDTO left, QuantityDTO right, QuantityDTO result) {
        return new QuantityMeasurementEntity(left, right, operation, result, null, null, false, null);
    }

    public static QuantityMeasurementEntity successForDivision(QuantityDTO left, QuantityDTO right, double result) {
        return new QuantityMeasurementEntity(left, right, "DIVIDE", null, null, result, false, null);
    }

    public static QuantityMeasurementEntity error(String operation, QuantityDTO left, QuantityDTO right, String errorMessage) {
        return new QuantityMeasurementEntity(left, right, operation, null, null, null, true, errorMessage);
    }

    public static QuantityMeasurementEntity restored(QuantityDTO thisQuantity,
                                                     QuantityDTO thatQuantity,
                                                     String operation,
                                                     QuantityDTO resultQuantity,
                                                     Boolean comparisonResult,
                                                     Double divisionResult,
                                                     boolean error,
                                                     String errorMessage) {
        return new QuantityMeasurementEntity(thisQuantity, thatQuantity, operation, resultQuantity, comparisonResult, divisionResult, error, errorMessage);
    }

    public QuantityDTO getThisQuantity() {
        return thisQuantity;
    }

    public QuantityDTO getThatQuantity() {
        return thatQuantity;
    }

    public String getOperation() {
        return operation;
    }

    public QuantityDTO getResultQuantity() {
        return resultQuantity;
    }

    public Boolean getComparisonResult() {
        return comparisonResult;
    }

    public Double getDivisionResult() {
        return divisionResult;
    }

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof QuantityMeasurementEntity)) {
            return false;
        }
        QuantityMeasurementEntity other = (QuantityMeasurementEntity) obj;
        return error == other.error
                && Objects.equals(thisQuantity, other.thisQuantity)
                && Objects.equals(thatQuantity, other.thatQuantity)
                && Objects.equals(operation, other.operation)
                && Objects.equals(resultQuantity, other.resultQuantity)
                && Objects.equals(comparisonResult, other.comparisonResult)
                && Objects.equals(divisionResult, other.divisionResult)
                && Objects.equals(errorMessage, other.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thisQuantity, thatQuantity, operation, resultQuantity, comparisonResult, divisionResult, error, errorMessage);
    }

    @Override
    public String toString() {
        if (error) {
            return "QuantityMeasurementEntity[operation=" + operation + ", error=" + errorMessage + "]";
        }
        if (comparisonResult != null) {
            return "QuantityMeasurementEntity[operation=" + operation + ", result=" + comparisonResult + "]";
        }
        if (divisionResult != null) {
            return "QuantityMeasurementEntity[operation=" + operation + ", result=" + divisionResult + "]";
        }
        return "QuantityMeasurementEntity[operation=" + operation + ", result=" + resultQuantity + "]";
    }
}

