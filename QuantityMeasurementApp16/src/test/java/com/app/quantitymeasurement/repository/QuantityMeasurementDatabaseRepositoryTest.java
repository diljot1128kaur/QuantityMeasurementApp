package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.unit.LengthUnit;
import com.app.quantitymeasurement.util.ApplicationConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class QuantityMeasurementDatabaseRepositoryTest {
    private QuantityMeasurementDatabaseRepository repository;

    @Before
    public void setUp() {
        System.setProperty("app.db.url", "jdbc:h2:mem:repo_test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
        System.setProperty("app.repository.type", "database");
        ApplicationConfig.reload();
        repository = new QuantityMeasurementDatabaseRepository();
        repository.deleteAll();
    }

    @After
    public void tearDown() {
        if (repository != null) {
            repository.releaseResources();
        }
        System.clearProperty("app.db.url");
        System.clearProperty("app.repository.type");
        ApplicationConfig.reload();
    }

    @Test
    public void saveAndRetrieveAll_shouldPersistEntity() {
        QuantityDTO left = new QuantityDTO(1.0, LengthUnit.FEET);
        QuantityDTO right = new QuantityDTO(12.0, LengthUnit.INCHES);
        repository.save(QuantityMeasurementEntity.successForComparison(left, right, true));

        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        Assert.assertEquals(1, all.size());
        Assert.assertEquals("COMPARE", all.get(0).getOperation());
        Assert.assertEquals(1L, repository.getTotalCount());
    }

    @Test
    public void getMeasurementsByOperation_shouldFilterByOperation() {
        QuantityDTO left = new QuantityDTO(1.0, LengthUnit.FEET);
        QuantityDTO right = new QuantityDTO(12.0, LengthUnit.INCHES);
        repository.save(QuantityMeasurementEntity.successForComparison(left, right, true));
        repository.save(QuantityMeasurementEntity.successForDivision(left, right, 1.0));

        Assert.assertEquals(1, repository.getMeasurementsByOperation("COMPARE").size());
        Assert.assertEquals(1, repository.getMeasurementsByOperation("DIVIDE").size());
    }

    @Test
    public void deleteAll_shouldRemoveAllRecords() {
        QuantityDTO left = new QuantityDTO(1.0, LengthUnit.FEET);
        QuantityDTO right = new QuantityDTO(12.0, LengthUnit.INCHES);
        repository.save(QuantityMeasurementEntity.successForComparison(left, right, true));
        repository.deleteAll();

        Assert.assertEquals(0L, repository.getTotalCount());
    }
}

