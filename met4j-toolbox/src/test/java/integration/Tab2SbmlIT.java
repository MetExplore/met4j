package integration;

import org.junit.Test;
import utils.IThelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class Tab2SbmlIT {

    @Test
    public void testTab2Sbml0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Tab2Sbml", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-Tab2Sbml");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            fail("Creation of the temporary directory");
        }
        
        String inFile =IThelper.copyProjectResource("toy_model.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Tab2Sbml",
            "-i",
            inFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*reaction .*");
        Pattern pattern1 = Pattern.compile(".*species .*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(7,nbMatch0);
        assertEquals(8,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testTab2Sbml1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Tab2Sbml", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-Tab2Sbml");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            fail("Creation of the temporary directory");
        }
        
        String inFile =IThelper.copyProjectResource("toy_model_otherColumns.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Tab2Sbml",
            "-i",
            inFile,
            "-ci",
            "2",
            "-cf",
            "3",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*reaction .*");
        Pattern pattern1 = Pattern.compile(".*species .*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(7,nbMatch0);
        assertEquals(8,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testTab2Sbml2() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Tab2Sbml", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-Tab2Sbml");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            fail("Creation of the temporary directory");
        }
        
        String inFile =IThelper.copyProjectResource("toy_model_otherSigns.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Tab2Sbml",
            "-i",
            inFile,
            "-irr",
            "->",
            "-rev",
            "=",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*reaction .*");
        Pattern pattern1 = Pattern.compile(".*species .*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(7,nbMatch0);
        assertEquals(8,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testTab2Sbml3() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Tab2Sbml", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-Tab2Sbml");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            fail("Creation of the temporary directory");
        }
        
        String inFile =IThelper.copyProjectResource("toy_model_cobra.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Tab2Sbml",
            "-i",
            inFile,
            "-M_c",
            "-b",
            "e",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*<compartment[^>]+id=\"e\".*");
        Pattern pattern1 = Pattern.compile(".*id=.M_A_e.*");
        Pattern pattern2 = Pattern.compile(".*id=.R_reac2.*");
        Pattern pattern3 = Pattern.compile(".*reaction .*");
        Pattern pattern4 = Pattern.compile(".*species .*");
        Pattern pattern5 = Pattern.compile(".*boundaryCondition\s*=\s*\"true\".*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        int nbMatch3 = 0;
        int nbMatch4 = 0;
        int nbMatch5 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            if(pattern3.matcher(line).matches()) nbMatch3++;
            if(pattern4.matcher(line).matches()) nbMatch4++;
            if(pattern5.matcher(line).matches()) nbMatch5++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(1,nbMatch2);
        assertEquals(7,nbMatch3);
        assertEquals(8,nbMatch4);
        assertEquals(3,nbMatch5);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));


        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testTab2Sbml4() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Tab2Sbml", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-Tab2Sbml");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            fail("Creation of the temporary directory");
        }
        
        String inFile =IThelper.copyProjectResource("toy_model.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Tab2Sbml",
            "-i",
            inFile,
            "-id",
            "myModel",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*myModel.*");
        int nbMatch0 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(1,nbMatch0);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testTab2Sbml5() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Tab2Sbml", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-Tab2Sbml");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            fail("Creation of the temporary directory");
        }

        String inFile =IThelper.copyProjectResource("toy_model_wrong.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Tab2Sbml",
                "-i",
                inFile,
                "-id",
                "myModel",
                "--ignore-failed-read",
                "-o",
                actualOutput.toString()
        );


        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*reaction .*");
        Pattern pattern1 = Pattern.compile(".*species .*");
        Pattern pattern2 = Pattern.compile(".*compartment .*");
        Pattern pattern3 = Pattern.compile(".*id=.A_x.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        int nbMatch3 = 0;
        while (line != null) {
            System.out.println(line);
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            if(pattern3.matcher(line).matches()) nbMatch3++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(3,nbMatch2);
        assertEquals(10,nbMatch0);
        assertEquals(9,nbMatch1);
        assertEquals(1,nbMatch3);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));


        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testTab2Sbml6() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Tab2Sbml", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-Tab2Sbml");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            fail("Creation of the temporary directory");
        }

        String inFile =IThelper.copyProjectResource("toy_model_wrong.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Tab2Sbml",
                "-i",
                inFile,
                "-id",
                "myModel",
                "-o",
                actualOutput.toString()
        );
        assertEquals(1, result.exitCode());
    }

    
}
