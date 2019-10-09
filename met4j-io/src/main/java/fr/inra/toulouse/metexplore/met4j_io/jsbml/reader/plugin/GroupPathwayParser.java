package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import java.util.ArrayList;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ext.groups.Group;
import org.sbml.jsbml.ext.groups.GroupsModelPlugin;
import org.sbml.jsbml.ext.groups.ListOfMembers;
import org.sbml.jsbml.ext.groups.Member;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;

public class GroupPathwayParser implements PackageParser, PrimaryDataTag, ReaderSBML3Compatible {

	private String PackageNamespace = "http://www.sbml.org/sbml/level3/version1/groups/version1";

	private Model model;

	private BioNetwork network;

	private GroupsModelPlugin plugin;

	@Override
	public void parseModel(Model model, BioNetwork network) {

		System.err.println("Starting " + this.getAssociatedPackageName() + " plugin...");

		this.model = model;
		this.network = network;

		this.plugin = (GroupsModelPlugin) this.model.getPlugin(PackageNamespace);

		this.parseGroups();

	}

	@Override
	public String getAssociatedPackageName() {
		return "groups";
	}

	@Override
	public boolean isPackageUseableOnModel(Model model) {
		return model.isPackageURIEnabled(PackageNamespace);
	}

	private void parseGroups() {

		ListOf<Group> groups = this.plugin.getListOfGroups();

		for (Group group : groups) {
			if (this.network.getPathwaysView().containsId(group.getId())) {
				errorsAndWarnings.add(
						"[Warning] Pathway " + group.getId() + " duplicated. The second one has not been processed");
			} else {
				BioPathway pathway = new BioPathway(group.getId(), group.getName());

				network.add(pathway);

				ListOfMembers members = group.getListOfMembers();

				for (Member member : members) {

					String rxnId = member.getIdRef();
					if (!this.network.getReactionsView().containsId(rxnId)) {
						errorsAndWarnings.add("[Warning] Pathway " + group.getId() + ": the reaction " + rxnId
								+ " does not exist in the network");
					} else {
						BioReaction reaction = this.network.getReactionsView().get(rxnId);

						network.affectToPathway(pathway, reaction);
					}
				}
			}
		}
	}
}
