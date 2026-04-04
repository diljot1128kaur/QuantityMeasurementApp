package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {
    private static volatile QuantityMeasurementCacheRepository instance;

    private final List<QuantityMeasurementEntity> cache;

    private QuantityMeasurementCacheRepository() {
        this.cache = new ArrayList<>();
    }

    public static QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) {
            synchronized (QuantityMeasurementCacheRepository.class) {
                if (instance == null) {
                    instance = new QuantityMeasurementCacheRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public synchronized void save(QuantityMeasurementEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        cache.add(entity);
    }

    @Override
    public synchronized List<QuantityMeasurementEntity> getAllMeasurements() {
        return Collections.unmodifiableList(new ArrayList<>(cache));
    }

    @Override
    public synchronized List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
        return cache.stream().filter(entity -> entity.getOperation().equalsIgnoreCase(operation)).collect(Collectors.toList());
    }

    @Override
    public synchronized List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        return cache.stream().filter(entity -> {
            if (entity.getThisQuantity() == null) {
                return false;
            }
            return entity.getThisQuantity().getMeasurementType().equalsIgnoreCase(measurementType);
        }).collect(Collectors.toList());
    }

    @Override
    public synchronized long getTotalCount() {
        return cache.size();
    }

    @Override
    public synchronized void deleteAll() {
        cache.clear();
    }
}

