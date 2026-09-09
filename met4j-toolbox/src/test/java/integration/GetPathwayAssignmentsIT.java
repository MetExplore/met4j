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

public class GetPathwayAssignmentsIT {

    @Test
    public void testGetPathwayAssignments0() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GetPathwayAssignments", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-GetPathwayAssignments");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFile = IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GetPathwayAssignments",
                "-i",
                sbmlFile,
                "-entity",
                "REACTION",
                "-o",
                actualOutput.toString()
        );


        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = "\t";
        int ncol = 0;
        Pattern pattern0 = Pattern.compile(".*\tGalactose metabolism$");
        int nbMatch0 = 0;
        while (line != null) {
            System.out.println(line);
            int l = line.split(sep).length;
            if (l>ncol) ncol=l;
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(76,nbLines);
        assertEquals(2,ncol);
        assertEquals(21,nbMatch0);

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testGetPathwayAssignments1() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GetPathwayAssignments", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-GetPathwayAssignments");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFile = IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GetPathwayAssignments",
                "-i",
                sbmlFile,
                "-entity",
                "METABOLITE",
                "-o",
                actualOutput.toString()
        );


        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = "\t";
        int ncol = 0;
        Pattern pattern0 = Pattern.compile(".*\tGalactose metabolism$");
        int nbMatch0 = 0;
        while (line != null) {
            System.out.println(line);
            int l = line.split(sep).length;
            if (l>ncol) ncol=l;
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(2,ncol);
        assertEquals(46,nbMatch0);

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testGetPathwayAssignments2() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-GetPathwayAssignments", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-GetPathwayAssignments");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFile = IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.GetPathwayAssignments",
                "-i",
                sbmlFile,
                "-entity",
                "GENE",
                "-o",
                actualOutput.toString()
        );


        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = "\t";
        int ncol = 0;
        Pattern pattern0 = Pattern.compile(".*\tGalactose metabolism$");
        int nbMatch0 = 0;
        while (line != null) {
            System.out.println(line);
            int l = line.split(sep).length;
            if (l>ncol) ncol=l;
            nbLines++;
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertEquals(2,ncol);
        assertEquals(29,nbMatch0);

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

}
