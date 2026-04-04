package com.app.quantitymeasurement.util;

import com.app.quantitymeasurement.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class ConnectionPool {
    private final ApplicationConfig config;
    private final LinkedBlockingQueue<Connection> availableConnections;
    private final List<Connection> allConnections;
    private final int maxSize;

    public ConnectionPool(ApplicationConfig config) {
        this.config = config;
        this.maxSize = config.getPoolMaxSize();
        this.availableConnections = new LinkedBlockingQueue<>();
        this.allConnections = Collections.synchronizedList(new ArrayList<>());
        initialize();
    }

    public Connection acquireConnection() {
        try {
            Connection connection = availableConnections.poll(config.getPoolTimeoutMillis(), TimeUnit.MILLISECONDS);
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            synchronized (allConnections) {
                if (allConnections.size() < maxSize) {
                    Connection newConnection = createConnection();
                    allConnections.add(newConnection);
                    return newConnection;
                }
            }
            throw new DatabaseException("Connection pool exhausted");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new DatabaseException("Interrupted while waiting for database connection", ex);
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to validate database connection", ex);
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            if (!connection.isClosed()) {
                availableConnections.offer(connection);
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to release database connection", ex);
        }
    }

    public String getStatistics() {
        Map<String, Integer> stats = new ConcurrentHashMap<>();
        stats.put("total", allConnections.size());
        stats.put("available", availableConnections.size());
        stats.put("inUse", Math.max(0, allConnections.size() - availableConnections.size()));
        return stats.toString();
    }

    public void close() {
        synchronized (allConnections) {
            for (Connection connection : allConnections) {
                try {
                    if (!connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    throw new DatabaseException("Failed while closing pooled connections", ex);
                }
            }
            allConnections.clear();
            availableConnections.clear();
        }
    }

    private void initialize() {
        try {
            Class.forName(config.getDbDriver());
            int initialSize = Math.min(config.getPoolInitialSize(), maxSize);
            for (int i = 0; i < initialSize; i++) {
                Connection connection = createConnection();
                allConnections.add(connection);
                availableConnections.offer(connection);
            }
        } catch (ClassNotFoundException ex) {
            throw new DatabaseException("Database driver not found: " + config.getDbDriver(), ex);
        }
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(config.getDbUrl(), config.getDbUsername(), config.getDbPassword());
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to create database connection", ex);
        }
    }
}

