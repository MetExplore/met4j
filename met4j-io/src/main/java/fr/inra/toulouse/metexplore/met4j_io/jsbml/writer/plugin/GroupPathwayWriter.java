package fr.inra.toulouse.metexplore.met4j_io.jsbml.writer.plugin;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ext.groups.Group;
import org.sbml.jsbml.ext.groups.GroupsModelPlugin;
import org.sbml.jsbml.ext.groups.Member;

public class GroupPathwayWriter implements PackageWriter, PrimaryDataTag {

    private String PackageNamespace = "http://www.sbml.org/sbml/level3/version1/groups/version1";

    @Override
    public String getAssociatedPackageName() {
        return "groups";
    }

    @Override
    public boolean isPackageUseableOnLvl(int lvl) {
        if (lvl >= 3) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void parseBionetwork(Model model, BioNetwork bionetwork) {
        System.err.println("Generating Pathways...");

        GroupsModelPlugin plugin = (GroupsModelPlugin) model.getPlugin(PackageNamespace);
        BioCollection<BioPathway> pathways = bionetwork.getPathwaysView();

        for (BioPathway p : pathways) {
            Group group = new Group();
            group.setId(StringUtils.convertToSID(p.getId()));
            group.setName(p.getName());
            plugin.addGroup(group);
            BioCollection<BioReaction> reactions = bionetwork.getReactionsFromPathway(p);
            for (BioReaction r : reactions) {
                Member member = new Member();
                member.setIdRef(StringUtils.convertToSID(r.getId()));
                group.addMember(member);
            }
            if (group.getMemberCount() > 0) {
                plugin.addGroup(group);
            }
        }
    }
}
