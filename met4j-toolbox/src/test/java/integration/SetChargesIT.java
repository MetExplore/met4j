package integration;

import org.junit.Test;
import utils.IThelper;
import org.junit.Assert;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.Assert.*;

public class SetChargesIT {

    @Test
    public void testSetCharges0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetCharges", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetCharges");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("charges.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetCharges",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*fbc:charge=.2.*");
        Pattern pattern1 = Pattern.compile(".*fbc:charge=.-3.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSetCharges1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetCharges", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetCharges");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("chargesWithComment.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetCharges",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*fbc:charge=.2.*");
        Pattern pattern1 = Pattern.compile(".*fbc:charge=.-3.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertNotEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSetCharges2() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetCharges", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetCharges");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("charges.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetCharges",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-n",
            "1",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*fbc:charge=.2.*");
        Pattern pattern1 = Pattern.compile(".*fbc:charge=.-3.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertNotEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSetCharges3() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetCharges", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetCharges");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("chargesDifferentColumns.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetCharges",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-ci",
            "2",
            "-cc",
            "3",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*fbc:charge=.2.*");
        Pattern pattern1 = Pattern.compile(".*fbc:charge=.-3.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSetCharges4() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetCharges", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetCharges");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String tabFile =IThelper.copyProjectResource("chargesXF.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetCharges",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-p",
            "-s",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*fbc:charge=.-1000.*");
        int nbMatch0 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(3,nbMatch0);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
