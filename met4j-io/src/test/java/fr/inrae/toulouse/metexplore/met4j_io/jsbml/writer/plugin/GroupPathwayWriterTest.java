/*
 * Copyright INRAE (2020)
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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageWriterException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.BionetworkToJsbml;
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