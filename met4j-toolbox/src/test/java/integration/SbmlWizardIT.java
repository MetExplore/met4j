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

public class SbmlWizardIT {

    @Test
    public void testSbmlWizard0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SbmlWizard", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SbmlWizard");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.SbmlWizard",
            "-i",
            inputPathFile,
            "-ric",
            "-r0",
            "-mc",
            "by_id",
            "-rdr",
            "-rEX",
            "s",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*</sbml>.*");
        int nbMatch0 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbmlWizard1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SbmlWizard", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SbmlWizard");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String inputSideFile =IThelper.copyProjectResource("XF_network_C_Side.tab",tmpDir);
        String inputReactionsFile =IThelper.copyProjectResource("XF_network_R_Seed.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.SbmlWizard",
            "-i",
            inputPathFile,
            "-ric",
            "-r0",
            "-rdr",
            "-rc",
            inputSideFile,
            "-rr",
            inputReactionsFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*</sbml>.*");
        int nbMatch0 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
