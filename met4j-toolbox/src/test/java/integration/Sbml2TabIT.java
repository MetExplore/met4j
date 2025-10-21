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

public class Sbml2TabIT {

    @Test
    public void testSbml2Tab0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2Tab", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-Sbml2Tab");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2Tab",
            "-i",
            inFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = "\t";
        int ncol = 0;
        Pattern pattern0 = Pattern.compile(".*2.0 E\\[c\\] --> D\\[c\\].*");
        Pattern pattern1 = Pattern.compile(".*A\\[c\\] <==> B\\[c\\].*");
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
        assertEquals(8,nbLines);
        assertEquals(9,ncol);
        assertTrue(0<nbMatch0);
        assertTrue(0<nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbml2Tab1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-Sbml2Tab", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-Sbml2Tab");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2Tab",
            "-i",
            inFile,
            "-irr",
            "->",
            "-rev",
            "=",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = "\t";
        int ncol = 0;
        Pattern pattern0 = Pattern.compile(".*2.0 E\\[c\\] -> D\\[c\\].*");
        Pattern pattern1 = Pattern.compile(".*A\\[c\\] = B\\[c\\].*");
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
        assertEquals(8,nbLines);
        assertEquals(9,ncol);
        assertTrue(0<nbMatch0);
        assertTrue(0<nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
