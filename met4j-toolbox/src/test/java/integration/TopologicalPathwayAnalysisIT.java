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

public class TopologicalPathwayAnalysisIT {

    @Test
    public void testTopologicalPathwayAnalysis0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-TopologicalPathwayAnalysis", ".txt");
            tmpDir = Files.createTempDirectory("test-input-TopologicalPathwayAnalysis");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String dataPathFile =IThelper.copyProjectResource("XF_network_C_NOI.txt",tmpDir);
        String inputSideFile =IThelper.copyProjectResource("XF_network_C_Side.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.TopologicalPathwayAnalysis",
            "-i",
            inputPathFile,
            "-noi",
            dataPathFile,
            "-sc",
            inputSideFile,
            "-un",
            "-ri",
            "-out",
            "-mc",
            "by_id",
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
    
}
