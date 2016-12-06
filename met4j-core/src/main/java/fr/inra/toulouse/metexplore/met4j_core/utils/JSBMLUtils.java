/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.utils;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class JSBMLUtils {
	
	public static final String defaultProp="log4j.rootCategory=ERROR, console\n"
			+ "log4j.logger.org.sbml=ERROR, console\n"
			+ "#\n"
			+ "# Console Display\n"
			+ "#\n"
			+ "log4j.appender.console=org.apache.log4j.ConsoleAppender\n"
			+ "log4j.appender.console.layout=org.apache.log4j.PatternLayout\n"
			+ "log4j.appender.console.Threshold=DEBUG\n"
			+ "# Pattern to output the caller's file name and line number.\n"
			+ "log4j.appender.console.layout.ConversionPattern=%5p (%F:%L) - %m%n\n"
			+ "# Display only the message at the WARN level for the test packages\n"
			+ "# Comment this line or put it at the DEBUG level to get the message from the SimpleTreeNodeChangeListener\n"
			+ "#log4j.logger.org.sbml.jsbml.util=DEBUG\n"
			+ "log4j.logger.org.sbml.jsbml.test=WARN";


	public static void setJSBMLlogToLevel(Level lev){
		Logger.getLogger("org.sbml").setLevel(lev);
	}
	
	
	public static void setDefaultLog4jConfiguration() throws FileNotFoundException,IOException{
		
		Properties SysProps = System.getProperties();
		SysProps.setProperty("log4j.defaultInitOverride", "true");

		Properties props = new Properties(); 

		InputStream configStream = new ByteArrayInputStream(defaultProp.getBytes());
		props.load(configStream); 
		configStream.close(); 

		LogManager.resetConfiguration(); 
		PropertyConfigurator.configure(props);
		

	}
	
	

	public static void updateLog4jConfiguration(String propertiesAsString) throws IOException{
		
		Properties SysProps = System.getProperties();
		SysProps.setProperty("log4j.defaultInitOverride", "true");

		Properties props = new Properties(); 

		InputStream configStream = new ByteArrayInputStream(propertiesAsString.getBytes());
		props.load(configStream); 
		configStream.close(); 

		LogManager.resetConfiguration(); 
		PropertyConfigurator.configure(props); 

	}

}
