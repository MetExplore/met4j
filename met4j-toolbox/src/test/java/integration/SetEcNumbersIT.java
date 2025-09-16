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

public class SetEcNumbersIT {

    @Test
    public void testSetEcNumbers0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetEcNumbers", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetEcNumbers");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("ec.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetEcNumbers",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*1.1.1.1.*");
        Pattern pattern1 = Pattern.compile(".*1.2.3.4.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(2,nbMatch0);
        assertEquals(2,nbMatch1);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSetEcNumbers1() throws Exception {
        
        Path tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory("test-input-SetEcNumbers");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("ecBad.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetEcNumbers",
            "-i",
            sbmlFile,
            "-tab",
            tabFile
        );

        assertEquals(1, result.exitCode());
    }
    
}
