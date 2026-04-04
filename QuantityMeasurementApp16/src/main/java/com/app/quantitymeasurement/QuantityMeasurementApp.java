package com.app.quantitymeasurement;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.unit.LengthUnit;
import com.app.quantitymeasurement.unit.TemperatureUnit;
import com.app.quantitymeasurement.unit.VolumeUnit;
import com.app.quantitymeasurement.unit.WeightUnit;
import com.app.quantitymeasurement.util.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QuantityMeasurementApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuantityMeasurementApp.class);

    private static volatile QuantityMeasurementApp instance;

    private final IQuantityMeasurementRepository repository;
    private final QuantityMeasurementController controller;

    private QuantityMeasurementApp() {
        this.repository = createRepository();
        IQuantityMeasurementService service = new QuantityMeasurementServiceImpl(repository);
        this.controller = new QuantityMeasurementController(service);
        LOGGER.info("Initialized repository type: {}", repository.getClass().getSimpleName());
    }

    public static QuantityMeasurementApp getInstance() {
        if (instance == null) {
            synchronized (QuantityMeasurementApp.class) {
                if (instance == null) {
                    instance = new QuantityMeasurementApp();
                }
            }
        }
        return instance;
    }

    private IQuantityMeasurementRepository createRepository() {
        String repositoryType = ApplicationConfig.getInstance().getRepositoryType();
        if ("database".equalsIgnoreCase(repositoryType)) {
            return new QuantityMeasurementDatabaseRepository();
        }
        return QuantityMeasurementCacheRepository.getInstance();
    }

    public void runDemonstrations() {
        QuantityDTO length1 = new QuantityDTO(1.0, LengthUnit.FEET);
        QuantityDTO length2 = new QuantityDTO(12.0, LengthUnit.INCHES);
        QuantityDTO inchesTarget = new QuantityDTO(0.0, LengthUnit.INCHES);

        LOGGER.info("Length compare result: {}", controller.performComparison(length1, length2));
        LOGGER.info("Length conversion result: {}", controller.performConversion(length1, inchesTarget));
        LOGGER.info("Length addition result: {}", controller.performAddition(length1, length2));

        QuantityDTO volume1 = new QuantityDTO(1.0, VolumeUnit.LITRE);
        QuantityDTO volume2 = new QuantityDTO(500.0, VolumeUnit.MILLILITRE);
        LOGGER.info("Volume subtraction result: {}", controller.performSubtraction(volume1, volume2));

        QuantityDTO weight1 = new QuantityDTO(2.0, WeightUnit.KILOGRAM);
        QuantityDTO weight2 = new QuantityDTO(1000.0, WeightUnit.GRAM);
        LOGGER.info("Weight division result: {}", controller.performDivision(weight1, weight2));

        QuantityDTO temp1 = new QuantityDTO(0.0, TemperatureUnit.CELSIUS);
        QuantityDTO temp2 = new QuantityDTO(32.0, TemperatureUnit.FAHRENHEIT);
        QuantityDTO tempTarget = new QuantityDTO(0.0, TemperatureUnit.CELSIUS);
        LOGGER.info("Temperature compare result: {}", controller.performComparison(temp1, temp2));
        LOGGER.info("Temperature conversion result: {}", controller.performConversion(temp2, tempTarget));

        try {
            controller.performAddition(temp1, temp2, tempTarget);
        } catch (QuantityMeasurementException ex) {
            LOGGER.warn("Temperature addition not supported: {}", ex.getMessage());
        }

        LOGGER.info("Stored operation count: {}", repository.getTotalCount());
        LOGGER.info("Pool statistics: {}", repository.getPoolStatistics());
    }

    public void deleteAllMeasurements() {
        repository.deleteAll();
    }

    public void closeResources() {
        repository.releaseResources();
    }

    public static void main(String[] args) {
        QuantityMeasurementApp app = QuantityMeasurementApp.getInstance();
        try {
            app.runDemonstrations();
        } finally {
            app.closeResources();
        }
    }
}

