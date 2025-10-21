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

public class IdMapperIT {
    @Test
    public void testIdMapper0() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-IdMapper", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-IdMapper");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFile = IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputFile =IThelper.copyProjectResource("Human-GEM_pathway_metanetx_mapping.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.mapping.IdMapper",
                "-i",
                sbmlFile,
                "-id",
                inputFile,
                "-db",
                "metanetx.chemical",
                "-na",
                "-o",
                actualOutput.toString()
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
        assertTrue(Files.size(actualOutput) > 0);

        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine().trim();
        Pattern pattern0 = Pattern.compile("^MNXM8\tM_m02552[csr]$");
        Pattern pattern1 = Pattern.compile("^MNXM63\tM_m01623c$");
        Pattern pattern2 = Pattern.compile("^MNXM6{0,3}\tNA$");
        Pattern pattern3 = Pattern.compile("^CHEBI:16474\tNA$");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbMatch2 = 0;
        int nbMatch3 = 0;
        int nbLines = 0;
        while (line != null) {
            line = line.trim();
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            if(pattern2.matcher(line).matches()) nbMatch2++;
            if(pattern3.matcher(line).matches()) nbMatch3++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(7,nbLines);
        assertEquals(3,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(2,nbMatch2);
        assertEquals(1,nbMatch3);
    }


    @Test
    public void testIdMapper1() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-IdMapper", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-IdMapper");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFile = IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputFile =IThelper.copyProjectResource("Human-GEM_pathway_metanetx_mapping.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.mapping.IdMapper",
                "-i",
                sbmlFile,
                "-id",
                inputFile,
                "-db",
                "metanetx.chemical",
                "-o",
                actualOutput.toString()
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
        assertTrue(Files.size(actualOutput) > 0);

        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine().trim();
        Pattern pattern0 = Pattern.compile("^MNXM8\tM_m02552[csr]$");
        Pattern pattern1 = Pattern.compile("^MNXM63\tM_m01623c$");
        int nbMatch0 = 0;
        int nbMatch1 = 0;
        int nbLines = 0;
        while (line != null) {
            line = line.trim();
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            if(pattern1.matcher(line).matches()) nbMatch1++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(4,nbLines);
        assertEquals(3,nbMatch0);
        assertEquals(1,nbMatch1);
    }



}
