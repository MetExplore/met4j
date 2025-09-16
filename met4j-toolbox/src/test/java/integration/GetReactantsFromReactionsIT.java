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

public class GetReactantsFromReactionsIT {

    @Test
    public void testGetReactantsFromReactions0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GetReactantsFromReactions", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-GetReactantsFromReactions");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String reactionFileFile =IThelper.copyProjectResource("XF_network_R_Seed.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GetReactantsFromReactions",
            "-i",
            sbmlFile,
            "-r",
            reactionFileFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*R_GLUN.*");
        Pattern pattern1 = Pattern.compile(".*R_ACGS.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(4,nbMatch0);
        assertEquals(5,nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testGetReactantsFromReactions1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GetReactantsFromReactions", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-GetReactantsFromReactions");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String reactionFileFile =IThelper.copyProjectResource("XF_network_R_Seed.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GetReactantsFromReactions",
            "-i",
            sbmlFile,
            "-r",
            reactionFileFile,
            "-s",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*R_GLUN.*");
        Pattern pattern1 = Pattern.compile(".*R_ACGS.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(2,nbMatch0);
        assertEquals(5,nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testGetReactantsFromReactions2() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GetReactantsFromReactions", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-GetReactantsFromReactions");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String reactionFileFile =IThelper.copyProjectResource("XF_network_R_Seed.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GetReactantsFromReactions",
            "-i",
            sbmlFile,
            "-r",
            reactionFileFile,
            "-p",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*R_GLUN.*");
        Pattern pattern1 = Pattern.compile(".*R_ACGS.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(2,nbMatch0);
        assertEquals(5,nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
