package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.entity.QuantityDTO;

public interface IQuantityMeasurementService {
    boolean compare(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);

    QuantityDTO convert(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);

    QuantityDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);

    QuantityDTO add(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO);

    QuantityDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);

    QuantityDTO subtract(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO, QuantityDTO targetUnitDTO);

    double divide(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO);
}

