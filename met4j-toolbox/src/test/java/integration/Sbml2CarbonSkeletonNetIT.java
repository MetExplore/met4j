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

public class Sbml2CarbonSkeletonNetIT {

    @Test
    public void testSbml2CarbonSkeletonNet0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2CarbonSkeletonNet", ".txt");
            tmpDir = Files.createTempDirectory("test-input-Sbml2CarbonSkeletonNet");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputAAMFile =IThelper.copyProjectResource("Human-GEM_pathways-transitions.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2CarbonSkeletonNet",
            "-i",
            inputPathFile,
            "-g",
            inputAAMFile,
            "-f",
            "gml",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*node.*");
        Pattern pattern1 = Pattern.compile(".*edge.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
        assertTrue(0<nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbml2CarbonSkeletonNet1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2CarbonSkeletonNet", ".txt");
            tmpDir = Files.createTempDirectory("test-input-Sbml2CarbonSkeletonNet");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputAAMFile =IThelper.copyProjectResource("Human-GEM_pathways-transitions.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2CarbonSkeletonNet",
            "-i",
            inputPathFile,
            "-g",
            inputAAMFile,
            "-ks",
            "-f",
            "gml",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*node.*");
        Pattern pattern1 = Pattern.compile(".*edge.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
        assertTrue(0<nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbml2CarbonSkeletonNet2() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2CarbonSkeletonNet", ".txt");
            tmpDir = Files.createTempDirectory("test-input-Sbml2CarbonSkeletonNet");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputAAMFile =IThelper.copyProjectResource("Human-GEM_pathways-transitions.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2CarbonSkeletonNet",
            "-i",
            inputPathFile,
            "-g",
            inputAAMFile,
            "-mc",
            "-f",
            "gml",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*node.*");
        Pattern pattern1 = Pattern.compile(".*edge.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
        assertTrue(0<nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbml2CarbonSkeletonNet3() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2CarbonSkeletonNet", ".txt");
            tmpDir = Files.createTempDirectory("test-input-Sbml2CarbonSkeletonNet");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputAAMFile =IThelper.copyProjectResource("Human-GEM_pathways-transitions.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2CarbonSkeletonNet",
            "-i",
            inputPathFile,
            "-g",
            inputAAMFile,
            "-me",
            "-f",
            "gml",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*node.*");
        Pattern pattern1 = Pattern.compile(".*edge.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
        assertTrue(0<nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbml2CarbonSkeletonNet4() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2CarbonSkeletonNet", ".txt");
            tmpDir = Files.createTempDirectory("test-input-Sbml2CarbonSkeletonNet");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputAAMFile =IThelper.copyProjectResource("Human-GEM_pathways-transitions.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2CarbonSkeletonNet",
            "-i",
            inputPathFile,
            "-g",
            inputAAMFile,
            "-ri",
            "-f",
            "gml",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*node.*");
        Pattern pattern1 = Pattern.compile(".*edge.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
        assertTrue(0<nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbml2CarbonSkeletonNet5() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2CarbonSkeletonNet", ".txt");
            tmpDir = Files.createTempDirectory("test-input-Sbml2CarbonSkeletonNet");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputAAMFile =IThelper.copyProjectResource("Human-GEM_pathways-transitions.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2CarbonSkeletonNet",
            "-i",
            inputPathFile,
            "-g",
            inputAAMFile,
            "-un",
            "-f",
            "gml",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*node.*");
        Pattern pattern1 = Pattern.compile(".*edge.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
        assertTrue(0<nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbml2CarbonSkeletonNet6() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2CarbonSkeletonNet", ".txt");
            tmpDir = Files.createTempDirectory("test-input-Sbml2CarbonSkeletonNet");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputAAMFile =IThelper.copyProjectResource("Human-GEM_pathways-transitions.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2CarbonSkeletonNet",
            "-i",
            inputPathFile,
            "-g",
            inputAAMFile,
            "-f",
            "matrix",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = ",";
        int ncol = 0;
        while (line != null) {
            int l = line.split(sep).length;
            if (l>ncol) ncol=l;
            nbLines++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(112,nbLines);
        assertEquals(112,ncol);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbml2CarbonSkeletonNet7() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2CarbonSkeletonNet", ".txt");
            tmpDir = Files.createTempDirectory("test-input-Sbml2CarbonSkeletonNet");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputAAMFile =IThelper.copyProjectResource("Human-GEM_pathways-transitions2.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2CarbonSkeletonNet",
            "-i",
            inputPathFile,
            "-g",
            inputAAMFile,
            "-fi",
            "-f",
            "gml",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*node.*");
        Pattern pattern1 = Pattern.compile(".*edge.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
        assertTrue(0<nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
