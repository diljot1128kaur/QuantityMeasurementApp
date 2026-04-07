package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;

public final class QuantityMeasurementController {
    private final IQuantityMeasurementService quantityMeasurementService;

    public QuantityMeasurementController(IQuantityMeasurementService quantityMeasurementService) {
        if (quantityMeasurementService == null) {
            throw new IllegalArgumentException("Quantity measurement service cannot be null");
        }
        this.quantityMeasurementService = quantityMeasurementService;
    }

    public boolean performComparison(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return quantityMeasurementService.compare(thisQuantityDTO, thatQuantityDTO);
    }

    public QuantityDTO performConversion(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return quantityMeasurementService.convert(thisQuantityDTO, thatQuantityDTO);
    }

    public QuantityDTO performAddition(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return quantityMeasurementService.add(thisQuantityDTO, thatQuantityDTO);
    }

    public QuantityDTO performAddition(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO) {
        return quantityMeasurementService.add(thisQuantityDTO, thatQuantityDTO, targetUnitDTO);
    }

    public QuantityDTO performSubtraction(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return quantityMeasurementService.subtract(thisQuantityDTO, thatQuantityDTO);
    }

    public QuantityDTO performSubtraction(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO) {
        return quantityMeasurementService.subtract(thisQuantityDTO, thatQuantityDTO, targetUnitDTO);
    }

    public double performDivision(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
        return quantityMeasurementService.divide(thisQuantityDTO, thatQuantityDTO);
    }
}

