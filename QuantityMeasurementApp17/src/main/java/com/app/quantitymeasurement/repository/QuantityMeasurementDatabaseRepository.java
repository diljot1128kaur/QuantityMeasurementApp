package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.DatabaseException;
import com.app.quantitymeasurement.util.ApplicationConfig;
import com.app.quantitymeasurement.util.ConnectionPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public final class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {
    private static final String INSERT_SQL = "INSERT INTO quantity_measurement_entity (this_value, this_unit_name, this_measurement_type, that_value, that_unit_name, that_measurement_type, operation, result_value, result_unit_name, result_measurement_type, comparison_result, division_result, is_error, error_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT this_value, this_unit_name, this_measurement_type, that_value, that_unit_name, that_measurement_type, operation, result_value, result_unit_name, result_measurement_type, comparison_result, division_result, is_error, error_message FROM quantity_measurement_entity ORDER BY id";
    private static final String SELECT_BY_OPERATION_SQL = "SELECT this_value, this_unit_name, this_measurement_type, that_value, that_unit_name, that_measurement_type, operation, result_value, result_unit_name, result_measurement_type, comparison_result, division_result, is_error, error_message FROM quantity_measurement_entity WHERE operation = ? ORDER BY id";
    private static final String SELECT_BY_TYPE_SQL = "SELECT this_value, this_unit_name, this_measurement_type, that_value, that_unit_name, that_measurement_type, operation, result_value, result_unit_name, result_measurement_type, comparison_result, division_result, is_error, error_message FROM quantity_measurement_entity WHERE LOWER(this_measurement_type) = ? OR LOWER(that_measurement_type) = ? OR LOWER(result_measurement_type) = ? ORDER BY id";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM quantity_measurement_entity";
    private static final String DELETE_ALL_SQL = "DELETE FROM quantity_measurement_entity";

    private final ConnectionPool connectionPool;
    private final ApplicationConfig config;

    public QuantityMeasurementDatabaseRepository() {
        this(ApplicationConfig.getInstance());
    }

    public QuantityMeasurementDatabaseRepository(ApplicationConfig config) {
        this(new ConnectionPool(config), config);
    }

    public QuantityMeasurementDatabaseRepository(ConnectionPool connectionPool, ApplicationConfig config) {
        this.connectionPool = connectionPool;
        this.config = config;
        initializeSchema();
    }

    @Override
    public void save(QuantityMeasurementEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        Connection connection = connectionPool.acquireConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            connection.setAutoCommit(false);
            bindQuantity(statement, 1, entity.getThisQuantity());
            bindQuantity(statement, 4, entity.getThatQuantity());
            statement.setString(7, entity.getOperation());
            bindQuantity(statement, 8, entity.getResultQuantity());
            statement.setObject(11, entity.getComparisonResult());
            statement.setObject(12, entity.getDivisionResult());
            statement.setBoolean(13, entity.isError());
            statement.setString(14, entity.getErrorMessage());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            rollbackQuietly(connection);
            throw new DatabaseException("Failed to save quantity measurement entity", ex);
        } finally {
            resetAutoCommitQuietly(connection);
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return query(SELECT_ALL_SQL, null);
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
        return query(SELECT_BY_OPERATION_SQL, statement -> statement.setString(1, operation));
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        String normalized = measurementType.toLowerCase(Locale.ROOT);
        return query(SELECT_BY_TYPE_SQL, statement -> {
            statement.setString(1, normalized);
            statement.setString(2, normalized);
            statement.setString(3, normalized);
        });
    }

    @Override
    public long getTotalCount() {
        Connection connection = connectionPool.acquireConnection();
        try (PreparedStatement statement = connection.prepareStatement(COUNT_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            return 0;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to count quantity measurements", ex);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public void deleteAll() {
        Connection connection = connectionPool.acquireConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ALL_SQL)) {
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to delete quantity measurements", ex);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    @Override
    public String getPoolStatistics() {
        return connectionPool.getStatistics();
    }

    @Override
    public void releaseResources() {
        connectionPool.close();
    }

    private void initializeSchema() {
        String sql = readSchemaFile(config.getSchemaPath());
        if (sql.trim().isEmpty()) {
            return;
        }
        Connection connection = connectionPool.acquireConnection();
        try (Statement statement = connection.createStatement()) {
            for (String part : sql.split(";")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    statement.execute(trimmed);
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to initialize schema", ex);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    private String readSchemaFile(String schemaPath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(schemaPath);
        if (Objects.isNull(inputStream)) {
            throw new DatabaseException("Schema file not found: " + schemaPath);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            throw new DatabaseException("Failed to read schema file", ex);
        }
    }

    private List<QuantityMeasurementEntity> query(String sql, StatementBinder binder) {
        List<QuantityMeasurementEntity> results = new ArrayList<>();
        Connection connection = connectionPool.acquireConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(statement);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(fromRow(resultSet));
                }
            }
            return results;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to run query", ex);
        } finally {
            connectionPool.releaseConnection(connection);
        }
    }

    private QuantityMeasurementEntity fromRow(ResultSet resultSet) throws SQLException {
        QuantityDTO thisQuantity = toDto(resultSet, "this_value", "this_unit_name", "this_measurement_type");
        QuantityDTO thatQuantity = toDto(resultSet, "that_value", "that_unit_name", "that_measurement_type");
        QuantityDTO resultQuantity = toDto(resultSet, "result_value", "result_unit_name", "result_measurement_type");

        Boolean comparisonResult = (Boolean) resultSet.getObject("comparison_result");
        Double divisionResult = (Double) resultSet.getObject("division_result");
        boolean isError = resultSet.getBoolean("is_error");
        String errorMessage = resultSet.getString("error_message");

        return QuantityMeasurementEntity.restored(
                thisQuantity,
                thatQuantity,
                resultSet.getString("operation"),
                resultQuantity,
                comparisonResult,
                divisionResult,
                isError,
                errorMessage
        );
    }

    private QuantityDTO toDto(ResultSet resultSet, String valueColumn, String unitColumn, String typeColumn) throws SQLException {
        Double value = (Double) resultSet.getObject(valueColumn);
        String unitName = resultSet.getString(unitColumn);
        String type = resultSet.getString(typeColumn);
        if (value == null || unitName == null || type == null) {
            return null;
        }
        return new QuantityDTO(value, unitName, type);
    }

    private void bindQuantity(PreparedStatement statement, int startIndex, QuantityDTO quantity) throws SQLException {
        if (quantity == null) {
            statement.setObject(startIndex, null);
            statement.setObject(startIndex + 1, null);
            statement.setObject(startIndex + 2, null);
            return;
        }
        statement.setDouble(startIndex, quantity.getValue());
        statement.setString(startIndex + 1, quantity.getUnitName());
        statement.setString(startIndex + 2, quantity.getMeasurementType());
    }

    private void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void resetAutoCommitQuietly(Connection connection) {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
        }
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}


