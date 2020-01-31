/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.sbml.jsbml.JSBML;

/**
 * Utilitary class to configure the Log4J logger of jsbml
 *
 * @author Benjamin
 * @since 3.0
 */
public class JSBMLUtils {

    /**
     * The default log4j properties
     */

    /*
    public static final String defaultProp = "log4j.rootCategory=ERROR, stderr\n"
            + "log4j.logger.org.sbml= stderr\n"
            + "#\n"
            + "# Console Display\n"
            + "#\n"
            + "log4j.appender.stderr=org.apache.log4j.ConsoleAppender\n"
            + "log4j.appender.stderr.Target=System.err\n"
            + "log4j.appender.stderr.layout=org.apache.log4j.PatternLayout\n"
            + "log4j.appender.stderr.Threshold=DEBUG\n"
            + "# Pattern to output the caller's file name and line number.\n"
            + "log4j.appender.stderr.layout.ConversionPattern=%5p (%F:%L) - %m%n\n"
            + "# Display only the message at the WARN level for the test packages\n"
            + "# Comment this line or put it at the DEBUG level to get the message from the SimpleTreeNodeChangeListener\n"
            + "#log4j.logger.org.sbml.jsbml.util=DEBUG\n"
            + "log4j.logger.org.sbml.jsbml.test=WARN";*/

    public static final String defaultProp = "log4j.rootLogger=OFF\n";
    static Logger logger = Logger.getLogger(JSBML.class);

    /**
     * Set the level (severity) of the logged message
     *
     * @param lev the level from which Log4j will start logging messages
     */
    public static void setJSBMLlogToLevel(Level lev) {
        logger.setLevel(lev);
    }

    /**
     * Uses {@link #defaultProp} to set our default configuration to the jsbml
     * logger
     *
     * @throws IOException if an error occurred when reading our default configuration
     *                     as an input stream
     */
    public static void setDefaultLog4jConfiguration() throws IOException {

//        Properties SysProps = System.getProperties();
//        SysProps.setProperty("log4j.defaultInitOverride", "true");
//
//        Properties props = new Properties();
//
//        InputStream configStream = new ByteArrayInputStream(
//                defaultProp.getBytes());
//        props.load(configStream);
//        configStream.close();
//
//        LogManager.resetConfiguration();
//        PropertyConfigurator.configure(props);

        setJSBMLlogToLevel(Level.OFF);
        logger.getRootLogger().setLevel(Level.OFF);
        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            logger.setLevel(Level.OFF);
        }

    }

    /**
     * Update or set a new property in the Log4J configuration
     *
     * @param propertiesAsString the property and its value to set/ update
     * @throws IOException if an error occurred when reading the input as an input
     *                     stream
     */
    public static void updateLog4jConfiguration(String propertiesAsString)
            throws IOException {

        Properties SysProps = System.getProperties();
        SysProps.setProperty("log4j.defaultInitOverride", "true");

        Properties props = new Properties();

        InputStream configStream = new ByteArrayInputStream(
                propertiesAsString.getBytes());
        props.load(configStream);
        configStream.close();

        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);

    }

    /**
     * Add a new log File to the log4j configuration
     *
     * @param logFilePath the path of the new log file
     */
    public static void addFileLog(String logFilePath) {

        FileAppender appender = new FileAppender();
        appender.setName("MyFileAppender");
        appender.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        appender.setFile(logFilePath);
        appender.setAppend(true);
        appender.setThreshold(Level.ERROR);
        appender.activateOptions();

        logger.addAppender(appender);
    }

}
