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

public class ORApathwayEnrichmentIT {

    @Test
    public void testORApathwayEnrichment0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ORApathwayEnrichment", ".txt");
            tmpDir = Files.createTempDirectory("test-input-ORApathwayEnrichment");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String inputFile =IThelper.copyProjectResource("XF_network_C_NOI.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.mapping.ORApathwayEnrichment",
            "-i",
            sbmlFile,
            "-d",
            inputFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = "\t";
        int ncol = 0;
        while (line != null) {
            int l = line.split(sep).length;
            if (l>ncol) ncol=l;
            nbLines++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(3,nbLines);
        assertEquals(3,ncol);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testORApathwayEnrichment1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ORApathwayEnrichment", ".txt");
            tmpDir = Files.createTempDirectory("test-input-ORApathwayEnrichment");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String inputFile =IThelper.copyProjectResource("XF_network_C_NOI.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.mapping.ORApathwayEnrichment",
            "-i",
            sbmlFile,
            "-d",
            inputFile,
            "-c",
            "HolmBonferroni",
            "-th",
            "0.005",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = "\t";
        int ncol = 0;
        while (line != null) {
            int l = line.split(sep).length;
            if (l>ncol) ncol=l;
            nbLines++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(2,nbLines);
        assertEquals(3,ncol);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
