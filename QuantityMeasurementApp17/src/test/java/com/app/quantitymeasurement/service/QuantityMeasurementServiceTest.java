package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.unit.LengthUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class QuantityMeasurementServiceTest {

    @Test
    public void compare_shouldReturnTrueAndPersistEntity() {
        IQuantityMeasurementRepository repository = mock(IQuantityMeasurementRepository.class);
        QuantityMeasurementServiceImpl service = new QuantityMeasurementServiceImpl(repository);

        QuantityDTO left = new QuantityDTO(1.0, LengthUnit.FEET);
        QuantityDTO right = new QuantityDTO(12.0, LengthUnit.INCHES);

        boolean result = service.compare(left, right);

        Assert.assertTrue(result);
        verify(repository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    public void add_shouldPersistArithmeticOperation() {
        IQuantityMeasurementRepository repository = mock(IQuantityMeasurementRepository.class);
        QuantityMeasurementServiceImpl service = new QuantityMeasurementServiceImpl(repository);

        QuantityDTO left = new QuantityDTO(1.0, LengthUnit.FEET);
        QuantityDTO right = new QuantityDTO(12.0, LengthUnit.INCHES);

        QuantityDTO result = service.add(left, right);

        Assert.assertEquals("FEET", result.getUnitName());
        ArgumentCaptor<QuantityMeasurementEntity> captor = ArgumentCaptor.forClass(QuantityMeasurementEntity.class);
        verify(repository).save(captor.capture());
        Assert.assertEquals("ADD", captor.getValue().getOperation());
    }
}

