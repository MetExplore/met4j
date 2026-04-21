package integration;

import org.junit.Assert;
import org.junit.Test;
import utils.IThelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExtractCompartmentsIT {

    @Test
    public void testExtractCompartments0() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ExtractCompartments", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-ExtractCompartments");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.ExtractCompartments",
                "-i",
                inputPathFile,
                "-id",
                "p+e",
                "-o",
                actualOutput.toString()
        );


        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*<reaction.*");
        Pattern pattern1 = Pattern.compile(".*<species\\s.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(215,nbMatch0);
        assertEquals(286,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));


        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testExtractCompartments1() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ExtractCompartments", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-ExtractCompartments");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.ExtractCompartments",
                "-i",
                inputPathFile,
                "-id",
                "-c",
                "-o",
                actualOutput.toString()
        );


        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*<reaction.*");
        Pattern pattern1 = Pattern.compile(".*<species\\s.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(215,nbMatch0);
        assertEquals(286,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));


        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testExtractCompartments2() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ExtractCompartments", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-ExtractCompartments");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.ExtractCompartments",
                "-i",
                inputPathFile,
                "-id",
                "p+e",
                "-tr",
                "-o",
                actualOutput.toString()
        );


        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*<reaction.*");
        Pattern pattern1 = Pattern.compile(".*<species\\s.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(429,nbMatch0);
        assertEquals(347,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));


        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testExtractCompartments3() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ExtractCompartments", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-ExtractCompartments");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.ExtractCompartments",
                "-i",
                inputPathFile,
                "-id",
                "-c",
                "-tr",
                "-o",
                actualOutput.toString()
        );


        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*<reaction.*");
        Pattern pattern1 = Pattern.compile(".*<species\\s.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(429,nbMatch0);
        assertEquals(347,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));


        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
