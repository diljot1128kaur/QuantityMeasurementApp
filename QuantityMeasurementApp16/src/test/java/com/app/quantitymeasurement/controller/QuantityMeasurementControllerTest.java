package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.unit.LengthUnit;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuantityMeasurementControllerTest {

    @Test
    public void performComparison_shouldDelegateToService() {
        IQuantityMeasurementService service = mock(IQuantityMeasurementService.class);
        QuantityMeasurementController controller = new QuantityMeasurementController(service);

        QuantityDTO left = new QuantityDTO(1.0, LengthUnit.FEET);
        QuantityDTO right = new QuantityDTO(12.0, LengthUnit.INCHES);

        when(service.compare(left, right)).thenReturn(true);

        Assert.assertTrue(controller.performComparison(left, right));
    }

    @Test
    public void performConversion_shouldReturnServiceResult() {
        IQuantityMeasurementService service = mock(IQuantityMeasurementService.class);
        QuantityMeasurementController controller = new QuantityMeasurementController(service);

        QuantityDTO source = new QuantityDTO(1.0, LengthUnit.FEET);
        QuantityDTO target = new QuantityDTO(0.0, LengthUnit.INCHES);
        QuantityDTO converted = new QuantityDTO(12.0, LengthUnit.INCHES);

        when(service.convert(source, target)).thenReturn(converted);

        Assert.assertEquals(converted, controller.performConversion(source, target));
    }
}

