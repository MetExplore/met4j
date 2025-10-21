package integration;

import org.junit.Assert;
import org.junit.Test;
import utils.IThelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GroupRxnByEnzymeClassIT {

    @Test
    public void testGroupRxnByEnzymeClass0() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GroupRxnByEnzymeClass", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-GroupRxnByEnzymeClass");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFile = IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GroupRxnByEnzymeClass",
                "-i",
                sbmlFile,
                "-o",
                actualOutput.toString(),
                "-min",
                "0",
                "-max",
                "200"
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));

        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile( ".*<groups:listOfMembers>.*");
        int nbMatch0 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(82,nbMatch0);
    }

    @Test
    public void testGroupRxnByEnzymeClass1() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GroupRxnByEnzymeClass", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-GroupRxnByEnzymeClass");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFile = IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GroupRxnByEnzymeClass",
                "-i",
                sbmlFile,
                "-o",
                actualOutput.toString(),
                "-min",
                "2",
                "-max",
                "9"
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));

        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile( ".*<groups:listOfMembers>.*");
        int nbMatch0 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(35,nbMatch0);
    }
}
