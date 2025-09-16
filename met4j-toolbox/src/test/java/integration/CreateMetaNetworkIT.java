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

public class CreateMetaNetworkIT {

    @Test
    public void testCreateMetaNetwork0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-CreateMetaNetwork", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-CreateMetaNetwork");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbml1FilePathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String sbml2FilePathFile =IThelper.copyProjectResource("ECOL.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.reconstruction.CreateMetaNetwork",
            "-n1",
            sbml1FilePathFile,
            "-n2",
            sbml2FilePathFile,
            "-n1ex",
            "s",
            "-n2ex",
            "e",
            "-n1px",
            "hsa",
            "-n2px",
            "eco",
            "-mc",
            "by_metanetx",
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
