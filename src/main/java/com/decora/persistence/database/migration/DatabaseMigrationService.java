package com.decora.persistence.database.migration;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.cfg.AvailableSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseMigrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseMigrationService.class);

    private static void closeQuietly(final Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Error closing connection", e);
        }
    }

    public final void execute(final boolean dryRun) {

        final Properties hibernateProperties = new Properties();

        try (final FileInputStream fileInputStream = new FileInputStream("hibernate.properties")) {
            hibernateProperties.load(fileInputStream);
        } catch (IOException e) {
            LOGGER.error("Could not load hibernate.properties", e);
            return;
        }

        final String driverClassName = hibernateProperties.getProperty(AvailableSettings.DRIVER);

        final Driver driver;
        try {
            driver = (Driver) Class.forName(driverClassName).newInstance();
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Could not load driver class: {}", driverClassName, e);
            return;
        }

        final String url = hibernateProperties.getProperty(AvailableSettings.URL);

        final Properties driverProperties = new Properties();
        driverProperties.setProperty("url", url);

        final String user = hibernateProperties.getProperty(AvailableSettings.USER);
        if (user != null) {
            driverProperties.setProperty("user", user);
        }

        final String password = hibernateProperties.getProperty(AvailableSettings.PASS);
        if (password != null) {
            driverProperties.setProperty("password", password);
        }

        final Connection connection;
        try {
            connection = driver.connect(url, driverProperties);
        } catch (SQLException e) {
            LOGGER.error("Could not connect to database: {}", url, e);
            return;
        }

        execute(dryRun, connection);
    }

    private void execute(final boolean dryRun, final Connection connection) {
        try {
            connection.setAutoCommit(false);

            final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(this.getClass().getClassLoader());
            final Liquibase liquibase = new Liquibase("changelog/master.xml", resourceAccessor, new JdbcConnection(connection));

            if (dryRun) {
                final StringWriter sqlWriter = new StringWriter();
                liquibase.update(new Contexts(), sqlWriter);
                LOGGER.info("\n{}", sqlWriter.toString());
            } else {
                liquibase.update(new Contexts());
            }

        } catch (SQLException | LiquibaseException e) {
            LOGGER.error("", e);
            return;
        } finally {
            closeQuietly(connection);
        }
    }
}
