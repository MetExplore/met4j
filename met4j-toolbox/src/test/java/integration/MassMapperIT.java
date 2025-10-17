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

public class MassMapperIT {
    @Test
    public void testMassMapper0() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-MassMapper", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-MassMapper");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFile = IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputFile =IThelper.copyProjectResource("Human-GEM_pathway_mass_mapping.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.mapping.MassMapper",
                "-i",
                sbmlFile,
                "-m",
                inputFile,
                "-ppm",
                "5",
                "-comp",
                "average",
                "-na",
                "-o",
                actualOutput.toString()
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
        assertTrue(Files.size(actualOutput) > 0);

        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine().trim();
        Pattern pattern0 = Pattern.compile("^662.42\t662.41802\\d+\tM_m02552[csr]$");
        Pattern pattern1 = Pattern.compile("^479.1249\t479.12\\d+\tM_m01623c$");
        Pattern pattern2 = Pattern.compile("^479.125\t479.12\\d+\tM_m01623c$");
        Pattern pattern3 = Pattern.compile("^3615.42\tNA\tNA$");
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
        assertEquals(6,nbLines);
        assertEquals(3,nbMatch0);
        assertEquals(1,nbMatch1);
        assertEquals(1,nbMatch2);
        assertEquals(1,nbMatch3);
    }


}
