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

public class SetNamesIT {

    @Test
    public void testSetNames0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetNames", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetNames");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("namesMetabolites.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetNames",
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
        Pattern pattern0 = Pattern.compile(".*metaboliteA.*");
        Pattern pattern1 = Pattern.compile(".*metaboliteB.*");
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
    public void testSetNames1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetNames", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetNames");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("namesReactions.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetNames",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*reaction1.*");
        Pattern pattern1 = Pattern.compile(".*reaction2.*");
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
    public void testSetNames2() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetNames", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetNames");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String tabFile =IThelper.copyProjectResource("namesPathways.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetNames",
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
        Pattern pattern0 = Pattern.compile(".*groups:name=.CEB.*");
        Pattern pattern1 = Pattern.compile(".*groups:name=.NSP.*");
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
    public void testSetNames3() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetNames", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetNames");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String tabFile =IThelper.copyProjectResource("namesGenes.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetNames",
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
        Pattern pattern0 = Pattern.compile(".*fbc:name=.G1.*");
        Pattern pattern1 = Pattern.compile(".*fbc:name=.G2.*");
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
