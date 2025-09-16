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

public class ChokePointIT {

    @Test
    public void testChokePoint0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-ChokePoint", ".tsv");
            tmpDir = Files.createTempDirectory("test-input-ChokePoint");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis.ChokePoint",
            "-i",
            inputPathFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        int nbLines = 0;
        String sep = "\t";
        int ncol = 0;
        Pattern pattern0 = Pattern.compile("R_ADEtex.*adenine_transport_via_diffusion__extracellular_to_periplasm_.*M_ade_e <==> M_ade_p");
        Pattern pattern1 = Pattern.compile("R_AGDC_r.*R_AGDC_r.*M_acgam6p_c \\+ M_h2o_c --> M_ac_c \\+ M_gam6p_c");
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
        assertEquals(695,nbLines);
        assertEquals(3,ncol);
        assertEquals(1,nbMatch0);
        assertEquals(1,nbMatch1);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    
}
