/*
 * Copyright INRAE (2021)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.inrae.toulouse.metexplore.met4j_io.kegg;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class Kegg2BioNetworkTest {

    public static Kegg2BioNetwork app;
    public static KeggServices keggServices;


    @Before
    public void init() throws Exception {
        app = new Kegg2BioNetwork("fak", "reaction");

        keggServices = spy(new KeggServices());
        app.keggServices = keggServices;

        doReturn(KeggApiMock.geneList).when(keggServices).getKeggGeneEntries(anyString());
        doReturn(KeggApiMock.geneEcLinks).when(keggServices).getKeggEcGeneEntries(anyString());
        doReturn(KeggApiMock.pathwayList).when(keggServices).getKeggPathwayEntries(anyString());
        doReturn(KeggApiMock.tcaKgml).when(keggServices).getKgml(anyString());
        doReturn(KeggApiMock.orgInfo).when(keggServices).getKeggOrganismInfo(anyString());
        doReturn(KeggApiMock.reactionInfo).when(keggServices).getKeggEntities(anyString());

        app.setBionetworkDefaultValue();

    }


    @Test
    public void setGeneList() throws Exception {
        app.setGeneList();
        assertEquals(599, app.getGeneList().size());
    }

    @Test(expected = Exception.class)
    public void setGeneListWithError() throws Exception {
        doReturn(KeggApiMock.errorResult).when(keggServices).getKeggGeneEntries(anyString());
        app.setGeneList();
    }

    @Test(expected = Exception.class)
    public void setGeneListWithConnectionProblem() throws Exception {
        doThrow(Exception.class).when(keggServices).getKeggGeneEntries(anyString());
        app.setGeneList();
    }

    @Test
    public void setLinkECGene() throws Exception {
        app.setECList();
        assertEquals(806, app.getEcList().size());
    }

    @Test(expected = Exception.class)
    public void setLinkECGeneWithConnectionError() throws Exception {
        doThrow(Exception.class).when(keggServices).getKeggEcGeneEntries(anyString());
        app.setECList();
        assertEquals(806, app.getEcList().size());
    }

    @Test(expected = Exception.class)
    public void setLinkECGeneWithError() throws Exception {
        doReturn(KeggApiMock.errorResult).when(keggServices).getKeggEcGeneEntries(anyString());
        app.setECList();
        assertEquals(806, app.getEcList().size());
    }

    @Test
    public void setPathwayList() throws Exception {
        app.setPathwayList();
        assertEquals(120, app.getPathwayList().size());
    }

    @Test(expected = Exception.class)
    public void setPathwayListWithConnectionProblem() throws Exception {
        doThrow(Exception.class).when(keggServices).getKeggPathwayEntries(anyString());
        app.setPathwayList();
    }

    @Test(expected = Exception.class)
    public void setPathwayListWithError() throws Exception {
        doReturn(KeggApiMock.errorResult).when(keggServices).getKeggPathwayEntries(anyString());
        app.setPathwayList();
    }

    @Test
    public void getPathwayComponents() throws Exception {
        BioPathway pathway = new BioPathway("fakePathway");
        app.origin = "map";

        app.getNetwork().add(pathway);

        app.getPathwayComponents(pathway);

        assertEquals(20, app.getNetwork().getReactionsView().size());
        assertTrue(app.getNetwork().getReactionsView().containsId("R03270"));

        assertEquals(20, app.getNetwork().getMetabolitesView().size());
        assertTrue(app.getNetwork().getMetabolitesView().containsId("C00122"));

        assertEquals(29, app.getNetwork().getGenesView().size());
        assertTrue(app.getNetwork().getGenesView().containsId("bpa_BPP3216"));

        assertEquals(1, app.getNetwork().getPathwaysView().size());
        assertTrue(app.getNetwork().getPathwaysView().containsId("fakePathway"));

        // C15972 + C05125 --> C00068 + C16255
        BioReaction R03270 = app.getNetwork().getReactionsView().get("R03270");
        Set<String> leftIds = new HashSet<>();
        Set<String> rightIds = new HashSet<>();

        leftIds.add("C15972");
        leftIds.add("C05125");

        rightIds.add("C00068");
        rightIds.add("C16255");

        assertEquals(leftIds, R03270.getLeftsView().getIds());
        assertEquals(rightIds, R03270.getRightsView().getIds());

        assertFalse(R03270.isReversible());

        assertTrue(app.getNetwork().getReactionsView().containsId("R07618"));
        BioReaction R07618 = app.getNetwork().getReactionsView().get("R07618");

        Set<String> geneIds = new HashSet<>();
        geneIds.add("bpa_BPP1464");
        geneIds.add("bpa_BPP3047");
        geneIds.add("bpa_BPP3215");

        assertEquals(geneIds, app.getNetwork().getGenesFromReactions(R07618).getIds());

        assertTrue(app.getNetwork().getPathwaysFromReaction(R07618).containsId("fakePathway"));
    }

    @Test(expected = Exception.class)
    public void getPathwayComponentsWithConnectionError() throws Exception {
        doThrow(Exception.class).when(keggServices).getKgml("fakePathway");

        BioPathway pathway = new BioPathway("fakePathway");
        app.origin = "map";

        app.getNetwork().add(pathway);

        app.getPathwayComponents(pathway);
    }

    @Test(expected = Exception.class)
    public void getPathwayComponentsWithError() throws Exception {
        doReturn(KeggApiMock.errorResult).when(keggServices).getKgml("fakePathway");

        BioPathway pathway = new BioPathway("fakePathway");
        app.origin = "map";

        app.getNetwork().add(pathway);

        app.getPathwayComponents(pathway);
    }

    @Test
    public void setNetworkName() throws Exception {
        app.setNetWorkName();

        assertEquals("Buchnera aphidicola 5A (Acyrthosiphon pisum)", app.getNetwork().getName());
    }

    @Test(expected = Exception.class)
    public void setNetworkNameWithErrorConnection() throws Exception {
        doThrow(Exception.class).when(keggServices).getKeggOrganismInfo(anyString());

        app.setNetWorkName();
    }

    @Test(expected = Exception.class)
    public void setNetworkNameWithError() throws Exception {
        doReturn(KeggApiMock.errorResult).when(keggServices).getKeggOrganismInfo(anyString());

        app.setNetWorkName();
    }

    @Test
    public void getEntitiesData() throws Exception {
        ArrayList<String> ids = new ArrayList<>();
        ids.add("R00109");
        ids.add("R00209");
        HashMap<String, HashMap<String, ArrayList<String>>> res = app.getEntitiesData(ids);
        System.err.println(res);
        assertEquals(2, res.size());
        assertEquals(new HashSet<>(ids), res.keySet());

        HashMap<String, ArrayList<String>> R00109info = res.get("R00109");

        assertTrue(R00109info.containsKey("NAME"));
        assertEquals("NADPH:ferrileghemoglobin oxidoreductase", R00109info.get("NAME").get(0));

        assertTrue(R00109info.containsKey("EQUATION"));
        assertEquals("2n C00005 + 2 C02683 <=> C00006(n+1) + 2 C02685 + dd C00080", R00109info.get("EQUATION").get(0));

        assertTrue(R00109info.containsKey("ENZYME"));
        assertEquals("3.4.21.92", R00109info.get("ENZYME").get(0));

        assertTrue(R00109info.containsKey("DBLINKS"));
        assertEquals("RHEA: 16160", R00109info.get("DBLINKS").get(0));

        HashMap<String, ArrayList<String>> R00209info = res.get("R00209");

        assertTrue(R00209info.containsKey("NAME"));
        assertEquals(2, R00209info.get("NAME").size());
        assertEquals("pyruvate dehydrogenase complex", R00209info.get("NAME").get(1));

        assertTrue(R00209info.containsKey("COMMENT"));
        assertEquals(2, R00209info.get("COMMENT").size());
        assertEquals("multi-step reaction (see R01699+R02569+R07618)", R00209info.get("COMMENT").get(1));

        assertTrue(R00209info.containsKey("PATHWAY"));
        assertEquals(4, R00209info.get("PATHWAY").size());
        assertEquals("rn01200  Carbon metabolism", R00209info.get("PATHWAY").get(3));


    }

    @Test(expected = Exception.class)
    public void getEntitiesDataWithErrorConnection() throws Exception {
        doThrow(Exception.class).when(keggServices).getKeggEntities(anyString());
        ArrayList<String> ids = new ArrayList<>();
        ids.add("R00109");
        ids.add("R00209");
        app.getEntitiesData(ids);

    }

    @Test(expected = Exception.class)
    public void getEntitiesDataWithError() throws Exception {
        doReturn(KeggApiMock.errorResult).when(keggServices).getKeggEntities(anyString());
        ArrayList<String> ids = new ArrayList<>();
        ids.add("R00109");
        ids.add("R00209");
        app.getEntitiesData(ids);
    }

    @Test(expected = Exception.class)
    public void getEntitiesDataWithBadNumber() throws Exception {
        doReturn(KeggApiMock.errorResult).when(keggServices).getKeggEntities(anyString());
        ArrayList<String> ids = new ArrayList<>();
        ids.add("R00109");
        ids.add("R00209");
        ids.add("R00210");
        ids.add("R00211");
        ids.add("R00212");
        ids.add("R00213");
        ids.add("R00214");
        ids.add("R00215");
        ids.add("R00216");
        ids.add("R00217");
        ids.add("R00218");
        app.getEntitiesData(ids);
    }

    @Test
    public void createNetworkPathways() throws Exception {
        String pathwayList = "path:bpa00000\tFake pathway without reactions\n" +
                "path:bpa00020\tCitrate cycle (TCA cycle)2\n";

        doReturn(pathwayList).when(keggServices).getKeggPathwayEntries(anyString());
        doReturn(KeggApiMock.noReactionPathwayKgml).when(keggServices).getKgml("bpa00000");
        doReturn(KeggApiMock.tcaKgml).when(keggServices).getKgml("bpa00020");


        app.createNetworkPathways();

        assertEquals(1, app.getNetwork().getPathwaysView().size());

    }

    @Test(expected = Exception.class)
    public void createNetworkPathwaysWithErrorConnection() throws Exception {

        doThrow(Exception.class).when(keggServices).getKeggPathwayEntries(anyString());
        doReturn(KeggApiMock.noReactionPathwayKgml).when(keggServices).getKgml("bpa00000");

        app.createNetworkPathways();

    }

    @Test(expected = Exception.class)
    public void createNetworkPathwaysWithError() throws Exception {

        doThrow(Exception.class).when(keggServices).getKeggPathwayEntries(anyString());
        doReturn(KeggApiMock.errorResult).when(keggServices).getKgml("bpa00000");
        doReturn(KeggApiMock.tcaKgml).when(keggServices).getKgml("bpa00020");

        app.createNetworkPathways();

    }

    @Test(expected = Exception.class)
    public void getReactionDataWithBadReactionId() throws Exception {

        ArrayList<String> reactions = new ArrayList<>();
        reactions.add("R1");
        reactions.add("R2");

        app.getReactionData(reactions);

    }

    @Test(expected = Exception.class)
    public void getReactionDataWithBadNumberOfReactions() throws Exception {

        ArrayList<String> reactions = new ArrayList<>();
        reactions.add("R00209");
        reactions.add("R00109");
        reactions.add("R3");

        String pathwayList = "path:bpa0\tFake pathway\n";
        doReturn(pathwayList).when(keggServices).getKeggPathwayEntries(anyString());
        doReturn(KeggApiMock.fakeKgml).when(keggServices).getKgml("bpa0");

        app.setECList();
        app.createNetworkPathways();

        app.getReactionData(reactions);

    }

    @Test
    public void getReactionData() throws Exception {

        ArrayList<String> reactions = new ArrayList<>();
        reactions.add("R00209");
        reactions.add("R00109");

        String pathwayList = "path:bpa0\tFake pathway\n";

        doReturn(pathwayList).when(keggServices).getKeggPathwayEntries(anyString());
        doReturn(KeggApiMock.fakeKgml).when(keggServices).getKgml("bpa0");

        app.setECList();
        app.createNetworkPathways();

        app.getReactionData(reactions);

        assertEquals(2, app.getNetwork().getReactionsView().size());

        assertTrue(app.getNetwork().getReactionsView().containsId("R00109"));

        BioReaction R00109 = app.getNetwork().getReactionsView().get("R00109");

        assertTrue(R00109.getLeftsView().containsId("C02683"));

        BioReactant C02683 = R00109.getLeftReactantsView().stream().filter(x -> x.getMetabolite().getId().equals("C02683")).findFirst().orElse(null);

        assertNotNull(C02683);

        assertEquals(2.0, C02683.getQuantity(), 0.0);

        assertTrue(R00109.getLeftsView().containsId("C00005"));

        BioReactant C00005 = R00109.getLeftReactantsView().stream().filter(x -> x.getMetabolite().getId().equals("C00005")).findFirst().orElse(null);

        assertNotNull(C00005);

        assertEquals("checks if 2n is moved to 2", 2.0, C00005.getQuantity(), 0.0);

        assertTrue("checks if C00006(n+1) is moved to C00006", R00109.getRightsView().containsId("C00006"));

        assertTrue(R00109.getRightsView().containsId("C00080"));

        BioReactant C00080 = R00109.getRightReactantsView().stream().filter(x -> x.getMetabolite().getId().equals("C00080")).findFirst().orElse(null);

        assertNotNull(C00080);

        assertEquals("checks if dd is replaced by 1.0", 1.0, C00080.getQuantity(), 0.0);

        assertEquals("3.4.21.92 / 1.19.1.1", R00109.getEcNumber());

    }

    @Test
    public void getCompoundData() throws Exception {
        ArrayList<String> reactions = new ArrayList<>();
        reactions.add("R00209");
        reactions.add("R00109");

        String pathwayList = "path:bpa0\tFake pathway\n";

        doReturn(pathwayList).when(keggServices).getKeggPathwayEntries(anyString());
        doReturn(KeggApiMock.fakeKgml).when(keggServices).getKgml("bpa0");

        app.setECList();
        app.createNetworkPathways();

        app.getReactionData(reactions);

        doReturn(KeggApiMock.metaboliteInfo).when(keggServices).getKeggEntities(anyString());

        ArrayList<String> metabolites = new ArrayList<>();

        metabolites.add("C00005");
        metabolites.add("C02683");

        app.getCompoundData(metabolites);

        BioMetabolite C00005 = app.getNetwork().getMetabolitesView().get("C00005");

        assertEquals("NADPH", C00005.getName());

        assertEquals("C21H30N7O17P3", C00005.getChemicalFormula());

        assertEquals(745.4209, C00005.getMolecularWeight(), 0.0);

        BioMetabolite C02683 = app.getNetwork().getMetabolitesView().get("C02683");

        assertEquals(3.0, C02683.getMolecularWeight(), 0.0);



    }
}