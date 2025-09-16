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

public class ReactionDistanceMatrixIT {

    @Test
    public void testReactionDistanceMatrix0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ReactionDistanceMatrix", ".csv");
            tmpDir = Files.createTempDirectory("test-input-ReactionDistanceMatrix");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ReactionDistanceMatrix",
            "-i",
            inputPathFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        Pattern pattern0 = Pattern.compile("id,reac1,reac2,reac3,reac4,reac5,reac6,reac7");
        Pattern pattern1 = Pattern.compile("reac1,0.0,1.0,1.0,2.0,3.0,Infinity,Infinity");
        Pattern pattern2 = Pattern.compile("reac2,Infinity,0.0,1.0,1.0,2.0,Infinity,Infinity");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        while (line != null) {
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(8,nbLines);
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(1,nbMatch2);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testReactionDistanceMatrix1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ReactionDistanceMatrix", ".csv");
            tmpDir = Files.createTempDirectory("test-input-ReactionDistanceMatrix");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ReactionDistanceMatrix",
            "-i",
            inputPathFile,
            "-u",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        Pattern pattern0 = Pattern.compile("id,reac1,reac2,reac3,reac4,reac5,reac6,reac7");
        Pattern pattern1 = Pattern.compile("reac1,0.0,1.0,1.0,2.0,3.0,5.0,4.0");
        Pattern pattern2 = Pattern.compile("reac2,1.0,0.0,1.0,1.0,2.0,4.0,3.0");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        while (line != null) {
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(8,nbLines);
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(1,nbMatch2);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testReactionDistanceMatrix2() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ReactionDistanceMatrix", ".csv");
            tmpDir = Files.createTempDirectory("test-input-ReactionDistanceMatrix");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String sideCompoundFileFile =IThelper.copyProjectResource("sides-rdist.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ReactionDistanceMatrix",
            "-i",
            inputPathFile,
            "-u",
            "-sc",
            sideCompoundFileFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        Pattern pattern0 = Pattern.compile("id,reac1,reac2,reac3,reac4,reac5,reac6,reac7");
        Pattern pattern1 = Pattern.compile("reac1,0.0,1.0,1.0,2.0,3.0,5.0,4.0");
        Pattern pattern2 = Pattern.compile("reac2,1.0,0.0,1.0,2.0,3.0,5.0,4.0");
        Pattern pattern3 = Pattern.compile("reac3,1.0,1.0,0.0,1.0,2.0,4.0,3.0");
        Pattern pattern4 = Pattern.compile("reac4,2.0,2.0,1.0,0.0,1.0,3.0,2.0");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        int nbMatch3 = 0;
        int nbMatch4 = 0;
        while (line != null) {
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            if(pattern3.matcher(line).matches()) nbMatch3++;
            if(pattern4.matcher(line).matches()) nbMatch4++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(8,nbLines);
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(1,nbMatch2);
        assertEquals(1,nbMatch3);
        assertEquals(1,nbMatch4);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testReactionDistanceMatrix3() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ReactionDistanceMatrix", ".csv");
            tmpDir = Files.createTempDirectory("test-input-ReactionDistanceMatrix");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String rExcludeFile =IThelper.copyProjectResource("rexclude-rdist.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ReactionDistanceMatrix",
            "-i",
            inputPathFile,
            "-u",
            "-re",
            rExcludeFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        Pattern pattern0 = Pattern.compile("id,reac1,reac2,reac3,reac4,reac5");
        Pattern pattern1 = Pattern.compile("reac1,0.0,1.0,1.0,2.0,3.0");
        Pattern pattern2 = Pattern.compile("reac2,1.0,0.0,1.0,1.0,2.0");
        Pattern pattern3 = Pattern.compile("reac3,1.0,1.0,0.0,1.0,2.0");
        Pattern pattern4 = Pattern.compile("reac4,2.0,1.0,1.0,0.0,1.0");
        Pattern pattern5 = Pattern.compile("reac5,3.0,2.0,2.0,1.0,0.0");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        int nbMatch3 = 0;
        int nbMatch4 = 0;
        int nbMatch5 = 0;
        while (line != null) {
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            if(pattern3.matcher(line).matches()) nbMatch3++;
            if(pattern4.matcher(line).matches()) nbMatch4++;
            if(pattern5.matcher(line).matches()) nbMatch5++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(6,nbLines);
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(1,nbMatch2);
        assertEquals(1,nbMatch3);
        assertEquals(1,nbMatch4);
        assertEquals(1,nbMatch5);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testReactionDistanceMatrix4() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ReactionDistanceMatrix", ".csv");
            tmpDir = Files.createTempDirectory("test-input-ReactionDistanceMatrix");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String sideCompoundFileFile =IThelper.copyProjectResource("sides-rdist.txt",tmpDir);
        String rExcludeFile =IThelper.copyProjectResource("rexclude-rdist.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ReactionDistanceMatrix",
            "-i",
            inputPathFile,
            "-u",
            "-sc",
            sideCompoundFileFile,
            "-re",
            rExcludeFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        Pattern pattern0 = Pattern.compile("id,reac1,reac2,reac3,reac4,reac5");
        Pattern pattern1 = Pattern.compile("reac1,0.0,1.0,1.0,2.0,3.0");
        Pattern pattern2 = Pattern.compile("reac2,1.0,0.0,1.0,2.0,3.0");
        Pattern pattern3 = Pattern.compile("reac3,1.0,1.0,0.0,1.0,2.0");
        Pattern pattern4 = Pattern.compile("reac4,2.0,2.0,1.0,0.0,1.0");
        Pattern pattern5 = Pattern.compile("reac5,3.0,3.0,2.0,1.0,0.0");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        int nbMatch3 = 0;
        int nbMatch4 = 0;
        int nbMatch5 = 0;
        while (line != null) {
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            if(pattern3.matcher(line).matches()) nbMatch3++;
            if(pattern4.matcher(line).matches()) nbMatch4++;
            if(pattern5.matcher(line).matches()) nbMatch5++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(6,nbLines);
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(1,nbMatch2);
        assertEquals(1,nbMatch3);
        assertEquals(1,nbMatch4);
        assertEquals(1,nbMatch5);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testReactionDistanceMatrix5() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ReactionDistanceMatrix", ".csv");
            tmpDir = Files.createTempDirectory("test-input-ReactionDistanceMatrix");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String sideCompoundFileFile =IThelper.copyProjectResource("sides-rdist.txt",tmpDir);
        String rExcludeFile =IThelper.copyProjectResource("rexclude-rdist.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ReactionDistanceMatrix",
            "-i",
            inputPathFile,
            "-sc",
            sideCompoundFileFile,
            "-re",
            rExcludeFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        Pattern pattern0 = Pattern.compile("id,reac1,reac2,reac3,reac4,reac5");
        Pattern pattern1 = Pattern.compile("reac1,0.0,1.0,1.0,2.0,3.0");
        Pattern pattern2 = Pattern.compile("reac2,Infinity,0.0,1.0,2.0,3.0");
        Pattern pattern3 = Pattern.compile("reac3,Infinity,Infinity,0.0,1.0,2.0");
        Pattern pattern4 = Pattern.compile("reac4,Infinity,Infinity,Infinity,0.0,1.0");
        Pattern pattern5 = Pattern.compile("reac5,Infinity,Infinity,Infinity,Infinity,0.0");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        int nbMatch3 = 0;
        int nbMatch4 = 0;
        int nbMatch5 = 0;
        while (line != null) {
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            if(pattern3.matcher(line).matches()) nbMatch3++;
            if(pattern4.matcher(line).matches()) nbMatch4++;
            if(pattern5.matcher(line).matches()) nbMatch5++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(6,nbLines);
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(1,nbMatch2);
        assertEquals(1,nbMatch3);
        assertEquals(1,nbMatch4);
        assertEquals(1,nbMatch5);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
