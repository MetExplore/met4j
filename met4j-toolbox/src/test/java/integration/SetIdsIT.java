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

public class SetIdsIT {

    @Test
    public void testSetIds0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetIds", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetIds");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("idMetabolites.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetIds",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-t",
            "METABOLITE",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*id=.metaboliteA.*");
        Pattern pattern1 = Pattern.compile(".*id=.metaboliteB.*");
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
    public void testSetIds1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetIds", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetIds");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("idReactions.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetIds",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*id=.reaction1.*");
        Pattern pattern1 = Pattern.compile(".*id=.reaction2.*");
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
    public void testSetIds2() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetIds", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetIds");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String tabFile =IThelper.copyProjectResource("idPathways.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetIds",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-t",
            "PATHWAY",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*groups:id=.CEB.*");
        Pattern pattern1 = Pattern.compile(".*groups:id=.NSP.*");
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
    public void testSetIds3() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetIds", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetIds");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String tabFile =IThelper.copyProjectResource("idGenes.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetIds",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-t",
            "GENE",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*fbc:id=.G1.*");
        Pattern pattern1 = Pattern.compile(".*fbc:id=.G2.*");
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
    public void testSetIds4() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetIds", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetIds");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String tabFile =IThelper.copyProjectResource("idCompartments.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetIds",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-t",
            "COMPARTMENT",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*id=.cytosol.*");
        Pattern pattern1 = Pattern.compile(".*id=.extra.*");
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
    
}
