package de.oliverprobst.tdk.navi.config.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.oliverprobst.tdk.navi.config.Configuration;

/**
 * 
 */
public interface ConfigurationLoader {

    /**
     * Load a configuration.
     * 
     * @param path The configuration base directory. The variable ${user.home}
     *            is allowed.
     * @return The loaded configuration object
     * @throws IOException on any file handling error.
     * @throws ConfigurationFailureException If the configuration parsed
     *             contains errors.
     */
    Configuration loadConfig(String path) throws IOException, ConfigurationFailureException;

    /**
     * Load a transformation configuration.
     * 
     * @param file The configuration file for the transformation.
     * @return The loaded configuration object
     * @throws IOException on any file handling error.
     * @throws ConfigurationFailureException If the configuration parsed
     *             contains errors.
     */
    Configuration loadConfig(File file) throws IOException, ConfigurationFailureException;

    /**
     * Load a transformation configuration.
     * 
     * @param stream The configuration stream for the transformation.
     * @return The loaded configuration object
     * @throws IOException on any file handling error.
     * @throws ConfigurationFailureException If the configuration parsed
     *             contains errors.
     */
    Configuration loadConfig(InputStream stream) throws IOException, ConfigurationFailureException;
}
