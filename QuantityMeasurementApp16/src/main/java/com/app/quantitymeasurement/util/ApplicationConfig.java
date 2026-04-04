package com.app.quantitymeasurement.util;

import com.app.quantitymeasurement.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public final class ApplicationConfig {
    private static final String FILE_NAME = "application.properties";

    private static volatile ApplicationConfig instance;

    private final Properties properties;

    private ApplicationConfig() {
        this.properties = loadProperties();
    }

    public static ApplicationConfig getInstance() {
        if (instance == null) {
            synchronized (ApplicationConfig.class) {
                if (instance == null) {
                    instance = new ApplicationConfig();
                }
            }
        }
        return instance;
    }

    public static synchronized void reload() {
        instance = new ApplicationConfig();
    }

    public String getRepositoryType() {
        return get("app.repository.type", "cache");
    }

    public String getDbDriver() {
        return get("app.db.driver", "org.h2.Driver");
    }

    public String getDbUrl() {
        return get("app.db.url", "jdbc:h2:mem:quantitydb;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false");
    }

    public String getDbUsername() {
        return get("app.db.username", "sa");
    }

    public String getDbPassword() {
        return get("app.db.password", "");
    }

    public int getPoolInitialSize() {
        return Integer.parseInt(get("app.db.pool.initialSize", "2"));
    }

    public int getPoolMaxSize() {
        return Integer.parseInt(get("app.db.pool.maxSize", "8"));
    }

    public long getPoolTimeoutMillis() {
        return Long.parseLong(get("app.db.pool.timeoutMillis", "10000"));
    }

    public String getSchemaPath() {
        return get("app.db.schema.path", "db/schema.sql");
    }

    private String get(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.trim().isEmpty()) {
            return systemValue.trim();
        }
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private Properties loadProperties() {
        Properties loaded = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (Objects.nonNull(inputStream)) {
                loaded.load(inputStream);
            }
            return loaded;
        } catch (IOException ex) {
            throw new DatabaseException("Unable to load application properties", ex);
        }
    }
}

