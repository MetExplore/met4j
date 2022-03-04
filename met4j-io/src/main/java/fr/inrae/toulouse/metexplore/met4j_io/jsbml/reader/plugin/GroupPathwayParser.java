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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ext.groups.Group;
import org.sbml.jsbml.ext.groups.GroupsModelPlugin;
import org.sbml.jsbml.ext.groups.ListOfMembers;
import org.sbml.jsbml.ext.groups.Member;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;

/**
 * <p>GroupPathwayParser class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class GroupPathwayParser implements PackageParser, PrimaryDataTag, ReaderSBML3Compatible {

	private String PackageNamespace = "http://www.sbml.org/sbml/level3/version1/groups/version1";

	private Model model;

	private BioNetwork network;

	private GroupsModelPlugin plugin;

	/** {@inheritDoc} */
	@Override
	public void parseModel(Model model, BioNetwork network) {

		System.err.println("Starting " + this.getAssociatedPackageName() + " plugin...");

		this.model = model;
		this.network = network;

		this.plugin = (GroupsModelPlugin) this.model.getPlugin(PackageNamespace);

		this.parseGroups();

	}

	/** {@inheritDoc} */
	@Override
	public String getAssociatedPackageName() {
		return "groups";
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPackageUseableOnModel(Model model) {
		return model.isPackageURIEnabled(PackageNamespace);
	}

	private void parseGroups() {

		ListOf<Group> groups = this.plugin.getListOfGroups();

		for (Group group : groups) {
			if (this.network.containsPathway(group.getId())) {
				errorsAndWarnings.add(
						"[Warning] Pathway " + group.getId() + " duplicated. The second one has not been processed");
			} else {
				BioPathway pathway = new BioPathway(group.getId(), group.getName());

				network.add(pathway);

				ListOfMembers members = group.getListOfMembers();

				for (Member member : members) {

					String rxnId = member.getIdRef();
					if (!this.network.containsReaction(rxnId)) {
						errorsAndWarnings.add("[Warning] Pathway " + group.getId() + ": the reaction " + rxnId
								+ " does not exist in the network");
					} else {
						BioReaction reaction = this.network.getReaction(rxnId);

						network.affectToPathway(pathway, reaction);
					}
				}
			}
		}
	}
}
