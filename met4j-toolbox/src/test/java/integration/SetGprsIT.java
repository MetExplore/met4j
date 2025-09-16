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

public class SetGprsIT {

    @Test
    public void testSetGprs0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SetGprs", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SetGprs");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String tabFile =IThelper.copyProjectResource("gpr.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SetGprs",
            "-i",
            sbmlFile,
            "-tab",
            tabFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*fbc:id=.G1.*");
        Pattern pattern1 = Pattern.compile(".*fbc:id=.G2.*");
        Pattern pattern2 = Pattern.compile(".*fbc:id=.G3.*");
        Pattern pattern3 = Pattern.compile(".*fbc:id=.G4.*");
        Pattern pattern4 = Pattern.compile(".*fbc:geneProductRef fbc:geneProduct=.G1.*");
        Pattern pattern5 = Pattern.compile(".*fbc:geneProductRef fbc:geneProduct=.G2.*");
        Pattern pattern6 = Pattern.compile(".*fbc:geneProductRef fbc:geneProduct=.G3.*");
        Pattern pattern7 = Pattern.compile(".*fbc:geneProductRef fbc:geneProduct=.G4.*");
        Pattern pattern8 = Pattern.compile(".*fbc:and.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        int nbMatch3 = 0;
        int nbMatch4 = 0;
        int nbMatch5 = 0;
        int nbMatch6 = 0;
        int nbMatch7 = 0;
        int nbMatch8 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            if(pattern3.matcher(line).matches()) nbMatch3++;
            if(pattern4.matcher(line).matches()) nbMatch4++;
            if(pattern5.matcher(line).matches()) nbMatch5++;
            if(pattern6.matcher(line).matches()) nbMatch6++;
            if(pattern7.matcher(line).matches()) nbMatch7++;
            if(pattern8.matcher(line).matches()) nbMatch8++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(1,nbMatch2);
        assertEquals(1,nbMatch3);
        assertEquals(1,nbMatch4);
        assertEquals(1,nbMatch5);
        assertEquals(1,nbMatch6);
        assertEquals(1,nbMatch7);
        assertEquals(2,nbMatch8);
        assertTrue(IThelper.isValidXml(actualOutput.toFile()));
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
