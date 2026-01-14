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

public class ScopeNetworkIT {

    @Test
    public void testScopeNetwork0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ScopeNetwork", ".txt");
            tmpDir = Files.createTempDirectory("test-input-ScopeNetwork");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String sbmlFilePathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String seedsFilePathFile =IThelper.copyProjectResource("seeds-scope.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ScopeNetwork",
            "-i",
            sbmlFilePathFile,
            "-s",
            seedsFilePathFile,
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
        assertEquals(9,nbMatch0);
        assertEquals(11,nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testScopeNetwork1() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ScopeNetwork", ".txt");
            tmpDir = Files.createTempDirectory("test-input-ScopeNetwork");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFilePathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String seedsFilePathFile =IThelper.copyProjectResource("seeds-scope.txt",tmpDir);
        String sideCompoundFileFile =IThelper.copyProjectResource("sides-scope.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ScopeNetwork",
                "-i",
                sbmlFilePathFile,
                "-s",
                seedsFilePathFile,
                "-sc",
                sideCompoundFileFile,
                "-f",
                "gml",
                "-ssc",
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
        assertEquals(9,nbMatch0);
        assertEquals(11,nbMatch1);


        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }


    @Test
    public void testScopeNetwork2() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ScopeNetwork", ".txt");
            tmpDir = Files.createTempDirectory("test-input-ScopeNetwork");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFilePathFile =IThelper.copyProjectResource("toy_model.xml",tmpDir);
        String seedsFilePathFile =IThelper.copyProjectResource("seeds-scope.txt",tmpDir);
        String sideCompoundFileFile =IThelper.copyProjectResource("sides-scope.txt",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ScopeNetwork",
                "-i",
                sbmlFilePathFile,
                "-s",
                seedsFilePathFile,
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
        assertEquals(8,nbMatch0);
        assertEquals(9,nbMatch1);


        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
