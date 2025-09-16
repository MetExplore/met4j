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

public class GetMetaboliteAttributesIT {

    @Test
    public void testGetMetaboliteAttributes0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GetMetaboliteAttributes", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-GetMetaboliteAttributes");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GetMetaboliteAttributes",
            "-i",
            sbmlFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = "\t";
        int ncol = 0;
        Pattern pattern0 = Pattern.compile(".*1S/C2H6O/c1-2-3/h3H,2H2,1H3.*");
        Pattern pattern1 = Pattern.compile(".*C.*NC.*");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        while (line != null) {
            int l = line.split(sep).length;
            if (l>ncol) ncol=l;
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(9,nbLines);
        assertEquals(6,ncol);
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
