package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.entity.QuantityModel;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.unit.IMeasurable;
import com.app.quantitymeasurement.unit.LengthUnit;
import com.app.quantitymeasurement.unit.TemperatureUnit;
import com.app.quantitymeasurement.unit.VolumeUnit;
import com.app.quantitymeasurement.unit.WeightUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);
    private static final double EPSILON = 1e-6;

    private final IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }

    @Override
    public boolean compare(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        QuantityModel<IMeasurable> left = toQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> right = toQuantityModel(thatQuantityDTO);
        try {
            validateSameCategory(left, right);
            boolean result = Math.abs(toBaseUnit(left) - toBaseUnit(right)) < EPSILON;
            repository.save(QuantityMeasurementEntity.successForComparison(thisQuantityDTO, thatQuantityDTO, result));
            return result;
        } catch (RuntimeException ex) {
            repository.save(QuantityMeasurementEntity.error("COMPARE", thisQuantityDTO, thatQuantityDTO, ex.getMessage()));
            LOGGER.error("Comparison failed", ex);
            throw toDomainException("Comparison failed", ex);
        }
    }

    @Override
    public QuantityDTO convert(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        QuantityModel<IMeasurable> source = toQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> target = toQuantityModel(thatQuantityDTO);
        try {
            validateSameCategory(source, target);
            double convertedValue = target.getUnit().convertFromBaseUnit(toBaseUnit(source));
            QuantityDTO result = new QuantityDTO(convertedValue, target.getUnit());
            repository.save(QuantityMeasurementEntity.successForConversion(thisQuantityDTO, thatQuantityDTO, result));
            return result;
        } catch (RuntimeException ex) {
            repository.save(QuantityMeasurementEntity.error("CONVERT", thisQuantityDTO, thatQuantityDTO, ex.getMessage()));
            LOGGER.error("Conversion failed", ex);
            throw toDomainException("Conversion failed", ex);
        }
    }

    @Override
    public QuantityDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return add(thisQuantityDTO, thatQuantityDTO, thisQuantityDTO);
    }

    @Override
    public QuantityDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO) {
        QuantityModel<IMeasurable> left = toQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> right = toQuantityModel(thatQuantityDTO);
        QuantityModel<IMeasurable> target = toQuantityModel(targetUnitDTO);
        try {
            validateArithmeticOperands(left, right, target, "ADD");
            double resultBase = toBaseUnit(left) + toBaseUnit(right);
            double resultValue = target.getUnit().convertFromBaseUnit(resultBase);
            QuantityDTO result = new QuantityDTO(resultValue, target.getUnit());
            repository.save(QuantityMeasurementEntity.successForArithmetic("ADD", thisQuantityDTO, thatQuantityDTO, result));
            return result;
        } catch (RuntimeException ex) {
            repository.save(QuantityMeasurementEntity.error("ADD", thisQuantityDTO, thatQuantityDTO, ex.getMessage()));
            LOGGER.error("Addition failed", ex);
            throw toDomainException("Addition failed", ex);
        }
    }

    @Override
    public QuantityDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return subtract(thisQuantityDTO, thatQuantityDTO, thisQuantityDTO);
    }

    @Override
    public QuantityDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO) {
        QuantityModel<IMeasurable> left = toQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> right = toQuantityModel(thatQuantityDTO);
        QuantityModel<IMeasurable> target = toQuantityModel(targetUnitDTO);
        try {
            validateArithmeticOperands(left, right, target, "SUBTRACT");
            double resultBase = toBaseUnit(left) - toBaseUnit(right);
            double resultValue = target.getUnit().convertFromBaseUnit(resultBase);
            QuantityDTO result = new QuantityDTO(resultValue, target.getUnit());
            repository.save(QuantityMeasurementEntity.successForArithmetic("SUBTRACT", thisQuantityDTO, thatQuantityDTO, result));
            return result;
        } catch (RuntimeException ex) {
            repository.save(QuantityMeasurementEntity.error("SUBTRACT", thisQuantityDTO, thatQuantityDTO, ex.getMessage()));
            LOGGER.error("Subtraction failed", ex);
            throw toDomainException("Subtraction failed", ex);
        }
    }

    @Override
    public double divide(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        QuantityModel<IMeasurable> left = toQuantityModel(thisQuantityDTO);
        QuantityModel<IMeasurable> right = toQuantityModel(thatQuantityDTO);
        try {
            validateSameCategory(left, right);
            left.getUnit().validateOperationSupport("DIVIDE");
            right.getUnit().validateOperationSupport("DIVIDE");
            double divisor = toBaseUnit(right);
            if (Math.abs(divisor) < EPSILON) {
                throw new ArithmeticException("Cannot divide by zero quantity");
            }
            double result = toBaseUnit(left) / divisor;
            if (!Double.isFinite(result)) {
                throw new IllegalArgumentException("Division result is out of range");
            }
            repository.save(QuantityMeasurementEntity.successForDivision(thisQuantityDTO, thatQuantityDTO, result));
            return result;
        } catch (RuntimeException ex) {
            repository.save(QuantityMeasurementEntity.error("DIVIDE", thisQuantityDTO, thatQuantityDTO, ex.getMessage()));
            LOGGER.error("Division failed", ex);
            throw toDomainException("Division failed", ex);
        }
    }

    private QuantityMeasurementException toDomainException(String message, RuntimeException ex) {
        if (ex instanceof QuantityMeasurementException) {
            return (QuantityMeasurementException) ex;
        }
        return new QuantityMeasurementException(message + ": " + ex.getMessage(), ex);
    }

    private QuantityModel<IMeasurable> toQuantityModel(QuantityDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Quantity DTO cannot be null");
        }
        IMeasurable unit = getUnitFrom(dto);
        return new QuantityModel<>(dto.getValue(), unit);
    }

    private IMeasurable getUnitFrom(QuantityDTO dto) {
        String measurementType = dto.getMeasurementType().trim().toUpperCase();
        String unitName = dto.getUnitName();
        if (measurementType.equals("LENGTH") || measurementType.equals("LENGTHUNIT")) {
            return LengthUnit.from(unitName);
        }
        if (measurementType.equals("WEIGHT") || measurementType.equals("WEIGHTUNIT")) {
            return WeightUnit.from(unitName);
        }
        if (measurementType.equals("VOLUME") || measurementType.equals("VOLUMEUNIT")) {
            return VolumeUnit.from(unitName);
        }
        if (measurementType.equals("TEMPERATURE") || measurementType.equals("TEMPERATUREUNIT")) {
            return TemperatureUnit.from(unitName);
        }
        throw new IllegalArgumentException("Unsupported measurement type: " + dto.getMeasurementType());
    }

    private static double toBaseUnit(QuantityModel<IMeasurable> quantityModel) {
        return quantityModel.getUnit().convertToBaseUnit(quantityModel.getValue());
    }

    private static void validateSameCategory(QuantityModel<IMeasurable> left, QuantityModel<IMeasurable> right) {
        if (left.getUnit().getClass() != right.getUnit().getClass()) {
            throw new IllegalArgumentException("Cannot perform operation between different measurement categories: " + left.getUnit().getMeasurementType() + " and " + right.getUnit().getMeasurementType());
        }
    }

    private static void validateArithmeticOperands(QuantityModel<IMeasurable> left, QuantityModel<IMeasurable> right, QuantityModel<IMeasurable> target, String operation) {
        validateSameCategory(left, right);
        validateSameCategory(left, target);
        left.getUnit().validateOperationSupport(operation);
        right.getUnit().validateOperationSupport(operation);
        target.getUnit().validateOperationSupport(operation);
    }
}

