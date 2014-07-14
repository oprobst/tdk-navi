package de.oliverprobst.tdk.navi.config.loader;

/**
 * Factory creating classes of the Configuration loader interface.
 * 
 */
public final class ConfigurationFactory {

    /**
     * @return A instance of the ConfigurationLoader interface.
     */
    public static ConfigurationLoader getConfigurationLoader() {
        return new ConfigurationLoaderImpl();
    }

    /**
     * Constructor
     */
    private ConfigurationFactory() {
        // Utility class
    }
}
