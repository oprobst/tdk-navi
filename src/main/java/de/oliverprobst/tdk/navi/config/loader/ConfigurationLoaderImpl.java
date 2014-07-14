package de.oliverprobst.tdk.navi.config.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.oliverprobst.tdk.navi.config.Configuration;

/**
 * This is the implementation of the configuration loader.
 * 
 */
public class ConfigurationLoaderImpl implements ConfigurationLoader {

	/**
	 * The configuration file name for the transformation configuration.
	 */
	public static final String DEFAULT_CONFIG_FILENAME = "config.xml";

	/**
	 * Logger
	 */
	private final Logger log = LoggerFactory
			.getLogger(ConfigurationLoaderImpl.class);

	/**
	 * Constructor
	 */
	protected ConfigurationLoaderImpl() {
		// internal construction
	}

	public Configuration loadConfig(File file) throws IOException,
			ConfigurationFailureException {
		log.info("Load configuration file " + file);

		if (file == null) {
			throw new IllegalArgumentException(
					"The path to the configuration directory can't be null");
		}

		if (!file.exists()) {
			throw new IOException("'" + file.getAbsolutePath() + "' not found");
		}

		FileInputStream fis = new FileInputStream(file);

		try {
			return loadConfig(fis);
		} catch (ConfigurationFailureException e) {
			throw new ConfigurationFailureException(
					"The loaded configuration file (" + file
							+ ") is not valid. " + e.getMessage(), e);
		} finally {
			fis.close();
		}

	}

	public Configuration loadConfig(InputStream inputStream)
			throws IOException, ConfigurationFailureException {

		log.debug("Load configuration inputStream");

		URL config = null;
		Unmarshaller unMarshaller = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(Configuration.class);
			unMarshaller = jaxbContext.createUnmarshaller();

			config = getClass().getClassLoader().getResource(
					"configuration.xsd");

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = schemaFactory.newSchema(config);
			unMarshaller.setSchema(schema);

		} catch (JAXBException e) {
			throw new ConfigRuntimeException(
					"Failed to generate JAXBContext for PCPConfigXML class.", e);
		} catch (SAXException e) {
			throw new ConfigRuntimeException("Failed to parse schema file " + config,
					e);
		}
		try {
			return (Configuration) unMarshaller.unmarshal(inputStream);
		} catch (JAXBException e) {
			throw new ConfigurationFailureException(
					"Failed to parse input stream. XML seems to be invalid. "
							+ e.getMessage(), e);
		}
	}

	public Configuration loadConfig(String path) throws IOException,
			ConfigurationFailureException {

		log.debug("Load configuration from path: " + path);

		if (path == null) {
			throw new IllegalArgumentException(
					"The path to the configuration directory can't be null");
		}
		path = path.replaceAll("\\$\\{user.home\\}",
				System.getProperty("user.home"));

		File configFile = new File(path);
		if (configFile.isDirectory()) {
			log.debug("No filename found. Try default '"
					+ DEFAULT_CONFIG_FILENAME + "'.");
			configFile = new File(path + File.separatorChar
					+ DEFAULT_CONFIG_FILENAME);
		}

		if (!configFile.isFile()) {
			throw new IOException("'" + path
					+ "' is not a valid directory or file");
		}

		return loadConfig(configFile);
	}
}
