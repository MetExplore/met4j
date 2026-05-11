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

public class SbmlWizardIT {

    @Test
    public void testSbmlWizard0() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SbmlWizard", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SbmlWizard");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("Human-GEM_pathways.xml",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.SbmlWizard",
            "-i",
            inputPathFile,
            "-ric",
            "-r0",
            "-mc",
            "by_id",
            "-rdr",
            "-rEX",
            "s",
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*</sbml>.*");
        int nbMatch0 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }
    

    @Test
    public void testSbmlWizard1() throws Exception {
        
        Path actualOutput = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SbmlWizard", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SbmlWizard");
            tmpDir.toFile().deleteOnExit();
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        
        String inputPathFile =IThelper.copyProjectResource("XF_network.sbml",tmpDir);
        String inputSideFile =IThelper.copyProjectResource("XF_network_C_Side.tab",tmpDir);
        String inputReactionsFile =IThelper.copyProjectResource("XF_network_R_Seed.tab",tmpDir);
        IThelper.ProcessResult result = IThelper.runCli(
            "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.SbmlWizard",
            "-i",
            inputPathFile,
            "-ric",
            "-r0",
            "-rdr",
            "-rc",
            inputSideFile,
            "-rr",
            inputReactionsFile,
            "-o",
            actualOutput.toString()
        );
        
        
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        Pattern pattern0 = Pattern.compile(".*</sbml>.*");
        int nbMatch0 = 0;
        while (line != null) {
            if(pattern0.matcher(line).matches()) nbMatch0++;
            line = reader.readLine();
        }
        reader.close();
        assertTrue(0<nbMatch0);
    

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));
    }

    @Test
    public void testSbmlWizardRemovePaired() throws Exception {

        Path actualOutput = null;
        Path pairedFile = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SbmlWizard", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SbmlWizard");
            tmpDir.toFile().deleteOnExit();

            pairedFile = Files.createTempFile("paired", ".txt");
            String data = "A\tB\n";
            Files.write(pairedFile, data.getBytes());

        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory or files");
        }

        String inputPathFile = IThelper.copyProjectResource("toy_model.xml", tmpDir);

        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.SbmlWizard",
                "-i",
                inputPathFile,
                "-rp",
                pairedFile.toString(),
                "-o",
                actualOutput.toString()
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));

        // Check content
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        boolean inReac2 = false;
        boolean aFound = false;
        boolean bFound = false;

        while (line != null) {
            if (line.contains("id=\"reac2\"")) {
                inReac2 = true;
            }
            if (inReac2) {
                if (line.contains("species=\"A\"")) {
                    aFound = true;
                }
                if (line.contains("species=\"B\"")) {
                    bFound = true;
                }
                if (line.contains("</reaction>")) {
                    inReac2 = false;
                }
            }
            line = reader.readLine();
        }
        reader.close();

        assertTrue("A should not be removed from reac2 as it would empty the reactants", aFound);
        assertTrue("B should not be removed from reac2 as it would empty the products", bFound);
    }

    @Test
    public void testSbmlWizardRemovePairedMultiple() throws Exception {

        Path actualOutput = null;
        Path pairedFile = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SbmlWizard-multiple", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SbmlWizard-multiple");
            tmpDir.toFile().deleteOnExit();

            pairedFile = Files.createTempFile("paired_multiple", ".txt");
            String data = "B,C\tD\n";
            Files.write(pairedFile, data.getBytes());

        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory or files");
        }

        String inputPathFile = IThelper.copyProjectResource("toy_model.xml", tmpDir);

        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.SbmlWizard",
                "-i",
                inputPathFile,
                "-rp",
                pairedFile.toString(),
                "-o",
                actualOutput.toString()
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));

        // Check content
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        boolean inReac4 = false;
        boolean bFound = false;
        boolean cFound = false;
        boolean dFound = false;

        while (line != null) {
            if (line.contains("id=\"reac4\"")) {
                inReac4 = true;
            }
            if (inReac4) {
                if (line.contains("species=\"B\"")) {
                    bFound = true;
                }
                if (line.contains("species=\"C\"")) {
                    cFound = true;
                }
                if (line.contains("species=\"D\"")) {
                    dFound = true;
                }
                if (line.contains("</reaction>")) {
                    inReac4 = false;
                }
            }
            line = reader.readLine();
        }
        reader.close();

        assertTrue("B should not be removed from reac4 as it would empty the reactants", bFound);
        assertTrue("C should not be removed from reac4 as it would empty the reactants", cFound);
        assertTrue("D should not be removed from reac4 as it would empty the products", dFound);
    }

    @Test
    /**
     * Tests that if only one of the species in the pair is present in the reaction, it is not removed
     */
    public void testSbmlWizardRemovePairedIncompletePair() throws Exception {

        Path actualOutput = null;
        Path pairedFile = null;
        Path tmpDir = null;
        try {
            actualOutput = Files.createTempFile("test-output-SbmlWizard-multiple", ".sbml");
            tmpDir = Files.createTempDirectory("test-input-SbmlWizard-multiple");
            tmpDir.toFile().deleteOnExit();

            pairedFile = Files.createTempFile("paired_multiple", ".txt");
            String data = "B,C\tZ\n";
            Files.write(pairedFile, data.getBytes());

        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory or files");
        }

        String inputPathFile = IThelper.copyProjectResource("toy_model.xml", tmpDir);

        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.SbmlWizard",
                "-i",
                inputPathFile,
                "-rp",
                pairedFile.toString(),
                "-o",
                actualOutput.toString()
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));

        // Check content
        BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()));
        String line = reader.readLine();
        boolean inReac4 = false;
        boolean bFound = false;
        boolean cFound = false;
        boolean dFound = false;

        while (line != null) {
            if (line.contains("id=\"reac4\"")) {
                inReac4 = true;
            }
            if (inReac4) {
                if (line.contains("species=\"B\"")) {
                    bFound = true;
                }
                if (line.contains("species=\"C\"")) {
                    cFound = true;
                }
                if (line.contains("species=\"D\"")) {
                    dFound = true;
                }
                if (line.contains("</reaction>")) {
                    inReac4 = false;
                }
            }
            line = reader.readLine();
        }
        reader.close();

        assertTrue("B should not be removed from reac4", bFound);
        assertTrue("C should not be removed from reac4", cFound);
        assertTrue("D should not be removed from reac4", dFound);
    }

    @Test
    public void testSbmlWizardRemovePairedWithEmptySide() throws Exception {
        Path actualOutput = Files.createTempFile("test-output-SbmlWizard-paired", ".sbml");
        Path inputSbml = Files.createTempFile("test-input-SbmlWizard-paired", ".sbml");
        Path pairedFile = Files.createTempFile("test-paired-reactants", ".txt");

        // Create simple SBML: r1 -> m1 + m2
        String sbmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<sbml xmlns=\"http://www.sbml.org/sbml/level3/version1/core\" level=\"3\" version=\"1\">\n" +
                "  <model id=\"test_model\">\n" +
                "    <listOfCompartments>\n" +
                "      <compartment id=\"c\" constant=\"true\"/>\n" +
                "    </listOfCompartments>\n" +
                "    <listOfSpecies>\n" +
                "      <species id=\"m1\" compartment=\"c\" hasOnlySubstanceUnits=\"false\" boundaryCondition=\"false\" constant=\"false\"/>\n" +
                "      <species id=\"m2\" compartment=\"c\" hasOnlySubstanceUnits=\"false\" boundaryCondition=\"false\" constant=\"false\"/>\n" +
                "    </listOfSpecies>\n" +
                "    <listOfReactions>\n" +
                "      <reaction id=\"r1\" reversible=\"false\" fast=\"false\">\n" +
                "        <listOfProducts>\n" +
                "          <speciesReference species=\"m1\" stoichiometry=\"1\" constant=\"true\"/>\n" +
                "          <speciesReference species=\"m2\" stoichiometry=\"1\" constant=\"true\"/>\n" +
                "        </listOfProducts>\n" +
                "      </reaction>\n" +
                "    </listOfReactions>\n" +
                "  </model>\n" +
                "</sbml>";
        Files.write(inputSbml, sbmlContent.getBytes());

        // Create paired file: empty left, m1 right.
        // Format: coll1\tcoll2
        // We want mismatch on left (empty), match on right (m1).
        // Since r1 is -> m1 + m2. Left is empty. Right has m1.
        // So coll1={} (matches Left empty), coll2={m1} (matches Right subset).
        // Tab separated. First column empty.
        String pairedContent = "\tm1";
        Files.write(pairedFile, pairedContent.getBytes());

        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.SbmlWizard",
                "-i", inputSbml.toString(),
                "-rp", pairedFile.toString(),
                "-o", actualOutput.toString()
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));

        // Check if m1 was removed from products of r1 (by checking file content roughly)
        // Or using BioNetwork (better) but this is an IT, usually we check output file.
        // I can just read the file and check for speciesReference.
        String outputContent = new String(Files.readAllBytes(actualOutput));

        // m1 should still exist as species? Yes, we didn't remove isolated (unless default).
        // But the relation in reaction should be gone.
        // <speciesReference species="m1" ...> should be absent inside <listOfProducts>
        // But regex parsing XML is fragile.

        // simpler check:
        // Input had: <speciesReference species="m1"
        // Output should NOT have it if strictly removed.
        // Wait, m1 is defined in listOfSpecies, so "species=\"m1\"" appears there too.
        // In reaction, it is <speciesReference species="m1"...>

        // Let's check counts of "speciesReference species=\"m1\""
        // Input: 1. Output: 0.

        Pattern p = Pattern.compile("species=\"m1\"");
        Matcher m = p.matcher(outputContent);
        assertFalse("m1 should be removed from reaction products", m.find());

        // Check integrity: m2 should be there.
        Pattern p2 = Pattern.compile("species=\"m2\"");
        Matcher m2 = p2.matcher(outputContent);
        assertTrue("m2 should remain in reaction products", m2.find());
    }

    @Test
    public void testSbmlWizardRemovePairedSupersetFirst() throws Exception {
        Path actualOutput = Files.createTempFile("test-output-SbmlWizard-paired-order", ".sbml");
        Path inputSbml = Files.createTempFile("test-input-SbmlWizard-paired-order", ".sbml");
        Path pairedFile = Files.createTempFile("test-paired-reactants-order", ".txt");

        // R1: adp + pi + e -> atp + h2o
        String sbmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<sbml xmlns=\"http://www.sbml.org/sbml/level3/version1/core\" level=\"3\" version=\"1\">\n" +
                "  <model id=\"test_model\">\n" +
                "    <listOfCompartments>\n" +
                "      <compartment id=\"c\" constant=\"true\"/>\n" +
                "    </listOfCompartments>\n" +
                "    <listOfSpecies>\n" +
                "      <species id=\"adp\" compartment=\"c\" hasOnlySubstanceUnits=\"false\" boundaryCondition=\"false\" constant=\"false\"/>\n" +
                "      <species id=\"pi\" compartment=\"c\" hasOnlySubstanceUnits=\"false\" boundaryCondition=\"false\" constant=\"false\"/>\n" +
                "      <species id=\"e\" compartment=\"c\" hasOnlySubstanceUnits=\"false\" boundaryCondition=\"false\" constant=\"false\"/>\n" +
                "      <species id=\"atp\" compartment=\"c\" hasOnlySubstanceUnits=\"false\" boundaryCondition=\"false\" constant=\"false\"/>\n" +
                "      <species id=\"h2o\" compartment=\"c\" hasOnlySubstanceUnits=\"false\" boundaryCondition=\"false\" constant=\"false\"/>\n" +
                "    </listOfSpecies>\n" +
                "    <listOfReactions>\n" +
                "      <reaction id=\"R1\" reversible=\"false\" fast=\"false\">\n" +
                "        <listOfReactants>\n" +
                "          <speciesReference species=\"adp\" stoichiometry=\"1\" constant=\"true\"/>\n" +
                "          <speciesReference species=\"pi\" stoichiometry=\"1\" constant=\"true\"/>\n" +
                "          <speciesReference species=\"e\" stoichiometry=\"1\" constant=\"true\"/>\n" +
                "        </listOfReactants>\n" +
                "        <listOfProducts>\n" +
                "          <speciesReference species=\"atp\" stoichiometry=\"1\" constant=\"true\"/>\n" +
                "          <speciesReference species=\"h2o\" stoichiometry=\"1\" constant=\"true\"/>\n" +
                "        </listOfProducts>\n" +
                "      </reaction>\n" +
                "    </listOfReactions>\n" +
                "  </model>\n" +
                "</sbml>";
        Files.write(inputSbml, sbmlContent.getBytes());

        // Intentionally provide subset first, then superset.
        // Without sorting, removing subset first would block superset removal.
        String pairedContent = "adp\tatp\n" +
                "adp,pi\tatp\n";
        Files.write(pairedFile, pairedContent.getBytes());

        IThelper.ProcessResult result = IThelper.runCli(
                "fr.inrae.toulouse.metexplore.met4j_toolbox.convert.SbmlWizard",
                "-i", inputSbml.toString(),
                "-rp", pairedFile.toString(),
                "-o", actualOutput.toString()
        );

        assertEquals(0, result.exitCode());
        assertTrue(Files.exists(actualOutput));

        boolean inR1 = false;
        boolean adpInR1 = false;
        boolean piInR1 = false;
        boolean eInR1 = false;
        boolean atpInR1 = false;
        boolean h2oInR1 = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(actualOutput.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<reaction") && line.contains("id=\"R1\"")) {
                    inR1 = true;
                }
                if (inR1) {
                    if (line.contains("species=\"adp\"")) adpInR1 = true;
                    if (line.contains("species=\"pi\"")) piInR1 = true;
                    if (line.contains("species=\"e\"")) eInR1 = true;
                    if (line.contains("species=\"atp\"")) atpInR1 = true;
                    if (line.contains("species=\"h2o\"")) h2oInR1 = true;
                }
                if (inR1 && line.contains("</reaction>")) {
                    inR1 = false;
                }
            }
        }

        assertFalse("adp should be removed from R1", adpInR1);
        assertFalse("pi should be removed from R1 (superset processed first)", piInR1);
        assertTrue("e should remain in R1", eInR1);
        assertFalse("atp should be removed from R1", atpInR1);
        assertTrue("h2o should remain in R1", h2oInR1);
    }
}
