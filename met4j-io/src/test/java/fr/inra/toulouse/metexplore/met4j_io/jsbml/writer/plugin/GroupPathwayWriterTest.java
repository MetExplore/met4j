package fr.inra.toulouse.metexplore.met4j_io.jsbml.writer.plugin;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageWriterException;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.writer.BionetworkToJsbml;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.groups.Group;
import org.sbml.jsbml.ext.groups.GroupsModelPlugin;


import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class GroupPathwayWriterTest {


    @Test
    public void parseBionetwork() throws JSBMLPackageWriterException {

        SBMLDocument doc = new SBMLDocument(3, 1);
        Model model = doc.createModel();

        BioNetwork network = new BioNetwork();

        BioReaction r1 = new BioReaction("r1");
        BioReaction r2 = new BioReaction("r2");

        BioPathway p1 = new BioPathway("p1");
        BioPathway p2 = new BioPathway("p2");

        network.add(r1, r2, p1, p2);

        network.affectToPathway(p1, r1, r2);
        network.affectToPathway(p2, r1);

        BionetworkToJsbml converter = new BionetworkToJsbml();
        GroupPathwayWriter writer = new GroupPathwayWriter();
        converter.addPackage(writer);

        model = converter.parseBioNetwork(network);

        GroupsModelPlugin plugin = (GroupsModelPlugin) model.getPlugin("http://www.sbml.org/sbml/level3/version1/groups/version1");

        assertEquals(network.getPathwaysView().size(), plugin.getGroupCount());

        ListOf<Group> groups = plugin.getListOfGroups();

        Group p1Group = groups.get(p1.getId());
        assertEquals(network.getReactionsFromPathway(p1).size(), p1Group.getMemberCount());

        Group p2Group = groups.get(p2.getId());
        assertEquals(network.getReactionsFromPathway(p2).size(), p2Group.getMemberCount());

        Set<String> refsIdsP1 = network.getReactionsFromPathway(p1).getIds();
        Set<String> testsIdsP1 = p1Group.getListOfMembers().stream().map(member -> member.getIdRef()).collect(Collectors.toSet());

        assertEquals(refsIdsP1, testsIdsP1);

        Set<String> refsIdsP2 = network.getReactionsFromPathway(p2).getIds();
        Set<String> testsIdsP2 = p2Group.getListOfMembers().stream().map(member -> member.getIdRef()).collect(Collectors.toSet());

        assertEquals(refsIdsP2, testsIdsP2);

    }
}