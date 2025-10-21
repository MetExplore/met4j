package integration;

import org.junit.Assert;
import org.junit.Test;
import utils.IThelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SideCompoundsScanIT {

    @Test
    public void testSideCompoundsScan0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SideCompoundsScan", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-SideCompoundsScan");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.SideCompoundsScan",
            "-i",
            inputPathFile,
            "-d",
            "400",
            "-dp",
            "-1",
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
        assertEquals(1108,nbLines);
        assertEquals(4,ncol);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSideCompoundsScan1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SideCompoundsScan", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-SideCompoundsScan");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.SideCompoundsScan",
            "-i",
            inputPathFile,
            "-d",
            "400",
            "-dp",
            "-1",
            "-id",
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
        assertEquals(6,nbLines);
        assertEquals(1,ncol);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testSideCompoundsScan2() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SideCompoundsScan", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-SideCompoundsScan");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.SideCompoundsScan",
                "-i",
                inputPathFile,
                "-id",
                "-s",
                "-m",
                "by_name",
                "-dp",
                "2",
                "-cc",
                "-uf",
                "-o",
                actualOutput.toString()
        );


        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));

        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        String sep = "\t";
        int ncol = 0;
        while (line != null) {
            int l = line.split(sep).length;
            if (l>ncol) ncol=l;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(1,ncol);


    }
    
}
