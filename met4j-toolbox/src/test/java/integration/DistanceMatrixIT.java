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

public class DistanceMatrixIT {

    @Test
    public void testDistanceMatrix0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-DistanceMatrix", ".csv");
            tmpDir = Files.createTempDirectory("test-input-DistanceMatrix");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.DistanceMatrix",
            "-i",
            inputPathFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        Pattern pattern0 = Pattern.compile("id,A,A_ext,B,C,D,D_ext,E,E_ext");
        Pattern pattern1 = Pattern.compile("B,1.0,Infinity,0.0,2.0,1.0,2.0,Infinity,Infinity");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(9,nbLines);
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testDistanceMatrix1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-DistanceMatrix", ".csv");
            tmpDir = Files.createTempDirectory("test-input-DistanceMatrix");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String sideCompoundFileFile =IThelper.copyProjectResource("toy_model_sides.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.DistanceMatrix",
            "-i",
            inputPathFile,
            "-sc",
            sideCompoundFileFile,
            "-dw",
            "-u",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        Pattern pattern0 = Pattern.compile("id,A,B,C,D,D_ext,E,E_ext");
        Pattern pattern1 = Pattern.compile("A,0.0,4.0,4.0,20.0,21.0,24.0,25.0");
        Pattern pattern2 = Pattern.compile("B,4.0,0.0,8.0,16.0,17.0,20.0,21.0");
        Pattern pattern3 = Pattern.compile("C,4.0,8.0,0.0,16.0,17.0,20.0,21.0");
        Pattern pattern4 = Pattern.compile("D,8.0,4.0,4.0,0.0,1.0,4.0,5.0");
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
    
}
