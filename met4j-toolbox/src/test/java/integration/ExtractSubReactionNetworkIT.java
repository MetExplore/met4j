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

public class ExtractSubReactionNetworkIT {

    @Test
    public void testExtractSubReactionNetwork0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ExtractSubReactionNetwork", ".txt");
            tmpDir = Files.createTempDirectory("test-input-ExtractSubReactionNetwork");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String sourcePathFile =IThelper.copyProjectResource("seeds-r.txt",tmpDir);
        String targetPathFile =IThelper.copyProjectResource("targets-r.txt",tmpDir);
        String sideCompoundFileFile =IThelper.copyProjectResource("sides-r.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ExtractSubReactionNetwork",
            "-i",
            inputPathFile,
            "-s",
            sourcePathFile,
            "-t",
            targetPathFile,
            "-sc",
            sideCompoundFileFile,
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
        assertEquals(3,nbMatch0);
        assertEquals(2,nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
