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

public class FormulaMapperIT {
    @Test
    public void testFormulaMapper0() throws Exception {

        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-FormulaMapper", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-FormulaMapper");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }

        String sbmlFile = IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        String inputFile =IThelper.copyProjectResource("Human-GEM_pathway_formula_mapping.tsv",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.mapping.FormulaMapper",
                "-i",
                sbmlFile,
                "-f",
                inputFile,
                "-na",
                "-o",
                actualOutput.toString()
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
        assertTrue(Files.size(actualOutput) > 0);

        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine().trim();
        Pattern pattern0 = Pattern.compile("^C21H26N7O14P2\tC21H26N7O14P2\tM_m02552[csr]$");
        Pattern pattern1 = Pattern.compile("^C9H12N3O14P3\tC9H12N3O14P3\tM_m01623c$");
        Pattern pattern2 = Pattern.compile("^NiCeBaCoNSnAcK\tNA\tNA$");
        Pattern pattern3 = Pattern.compile("^IC4NB3Er\tNA\tNA$");
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
