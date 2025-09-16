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

public class GetEntitiesIT {

    @Test
    public void testGetEntities0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GetEntities", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-GetEntities");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GetEntities",
            "-i",
            sbmlFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*GENE.*");
        Pattern pattern1 = Pattern.compile(".*METABOLITE.*");
        Pattern pattern2 = Pattern.compile(".*REACTION.*");
        Pattern pattern3 = Pattern.compile(".*PATHWAY.*");
        Pattern pattern4 = Pattern.compile(".*COMPARTMENT.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        int nbMatch3 = 0;
        int nbMatch4 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            if(pattern3.matcher(line).matches()) nbMatch3++;
            if(pattern4.matcher(line).matches()) nbMatch4++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(86,nbMatch0);
        assertEquals(111,nbMatch1);
        assertEquals(75,nbMatch2);
        assertEquals(3,nbMatch3);
        assertEquals(5,nbMatch4);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testGetEntities1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GetEntities", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-GetEntities");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GetEntities",
            "-i",
            sbmlFile,
            "-r",
            "-m",
            "-g",
            "-c",
            "-nt",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        while (line != null) {
            nbLines++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(277,nbLines);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
