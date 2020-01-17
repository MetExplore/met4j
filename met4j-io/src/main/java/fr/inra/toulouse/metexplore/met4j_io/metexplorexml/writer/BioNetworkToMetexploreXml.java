package fr.inra.toulouse.metexplore.met4j_io.metexplorexml.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEnzymeParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_io.annotations.AnnotatorComment;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.UnitSbml;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;

public class BioNetworkToMetexploreXml {

    private BioNetwork network;
    private String path;
    private OutputStreamWriter writer;

    public BioNetworkToMetexploreXml(BioNetwork network, String path) {

        this.network = network;
        this.path = path;

    }

    public void write() throws IOException {

        writer = new OutputStreamWriter(new FileOutputStream(this.path), "ascii");

        writeBegin();
        writeUnitDefinitions();
        writeCompartments();

        writeCpds();
        writeReactions();
        writeEnd();

        writer.close();
    }

    private void writeBegin() throws IOException {
        String idKb = network.getId();
        String nameKb = network.getName();
        writer.write("<?xml version=\"1.0\"  encoding=\"UTF-8\"?>\n");
        writer.write("<model id=\"" + idKb + "\" name=\"" + nameKb + "\">\n");
    }

    /**
     * @throws IOException
     */
    private void writeUnitDefinitions() throws IOException {

        BioUnitDefinitionCollection unitDefinitions = NetworkAttributes.getUnitDefinitions(network);

        if (unitDefinitions.size() > 0) {
            writer.write("  <listOfUnitDefinitions>\n");

            for (BioUnitDefinition ud : unitDefinitions) {

                String udId = ud.getId();
                String udName = ud.getName();

                writer.write("    <unitDefinition id=\"" + udId + "\"");
                if (udName != "") {
                    writer.write(" name=\"" + udName + "\"");
                }

                writer.write(">\n");

                HashMap<String, UnitSbml> units = ud.getUnits();

                if (units.size() > 0) {

                    writer.write("      <listOfUnits>\n");

                    for (UnitSbml unit : units.values()) {

                        String kind = unit.getKind();
                        Integer scale = unit.getScale();
                        Double exponent = unit.getExponent();
                        Double multiplier = unit.getMultiplier();

                        writer.write("        <unit kind=\"" + kind + "\"");
                        writer.write(" scale=\"" + scale + "\"");
                        writer.write(" exponent=\"" + exponent + "\"");
                        writer.write(" multiplier=\"" + multiplier + "\"");
                        writer.write("/>\n");
                    }
                    writer.write("      </listOfUnits>\n");
                }
                writer.write("    </unitDefinition>\n");

            }

            writer.write("  </listOfUnitDefinitions>\n");

        }

    }

    /**
     * Write compartments
     *
     * @throws IOException
     */
    private void writeCompartments() throws IOException {
        writer.write("<listOfCompartments>\n");

        for (BioCompartment compartment : network.getCompartmentsView()) {
            String compartmentId = StringUtils.sbmlEncode(compartment.getId());

            String compartmentName = StringUtils.htmlEncode(compartment.getName());

            writer.write("<compartment id=\"" + compartmentId + "\" name =\"" + compartmentName + "\"");
            writer.write(" />\n");
        }

        writer.write("</listOfCompartments>\n");
    }

    /**
     * Write compounds
     *
     * @throws IOException
     */
    private void writeCpds() throws IOException {

        writer.write("<listOfSpecies>\n");

        for (BioMetabolite cpd : network.getMetabolitesView()) {
            writeCpd(cpd);
        }

        writer.write("</listOfSpecies>\n");

    }

    /**
     * Write metabolites
     *
     * @param cpd
     * @throws IOException
     */
    private void writeCpd(BioMetabolite cpd) throws IOException {

        String id = StringUtils.sbmlEncode(cpd.getId());
        String name = StringUtils.htmlEncode(cpd.getName());

        String boundaryCondition = "false";

        if (MetaboliteAttributes.getBoundaryCondition(cpd) == true) {
            boundaryCondition = "true";
        }

        String generic = "false";
        if (GenericAttributes.getGeneric(cpd)) {
            generic = "true";
        }

        BioCollection<BioCompartment> cpts = network.getCompartmentsOf(cpd);

        // TODO : to check : if the metabolite has several compartments, it will fail
        for (BioCompartment cpt : cpts) {

            writer.write("  <species id=\"" + id + "\" name=\"" + name + "\" compartment=\"" + cpt.getId()
                    + "\" boundaryCondition=\"" + boundaryCondition + "\"");

            writer.write(" mass=\"" + cpd.getMolecularWeight() + "\" formula=\"" + cpd.getChemicalFormula()
                    + "\" generic=\"" + generic + "\"");

            writer.write(" >\n");

            String inchi = cpd.getInchi();
            String smiles = cpd.getSmiles();
            // if (!(inchi.equals("NA") && smiles.equals("NA"))){
            if (inchi != null && smiles != null) {
                writer.write("    <notes>\n");
                writer.write("      <body xmlns=\"http://www.w3.org/1999/xhtml\">\n");
                if (smiles != null)
                    writer.write("        <p>SMILES: " + smiles + "</p>\n");
                if (inchi != null)
                    writer.write("        <p>INCHI: " + inchi + "</p>\n");
                writer.write("      </body>\n");
                writer.write("    </notes>\n");
            }
            // }

            writer.write("  </species>\n");

        }
    }

    /**
     * Write reactions
     *
     * @throws IOException
     */
    private void writeReactions() throws IOException {
        writer.write("<listOfReactions>\n");
        for (BioReaction reaction : network.getReactionsView()) {

            String reversibility = reaction.isReversible() ? "true" : "false";

            // Write the EC number

            String ec = reaction.getEcNumber() != null ? " ec=\"" + reaction.getEcNumber() + "\"" : "\"";

            String holder = "";

            if (GenericAttributes.getGeneric(reaction) != null) {
                holder = GenericAttributes.getGeneric(reaction) ? " generic=\"true\"" : " generic=\"false\"";
            }

            String type = "";

            if (GenericAttributes.getType(reaction) != null) {
                type = " type=\"" + GenericAttributes.getType(reaction) + "\"";
            }

            String hole = "";

            if (ReactionAttributes.getHole(reaction) != null) {
                hole = ReactionAttributes.getHole(reaction) ? " hole=\"true\"" : " hole=\"false\"";
            }

            writer.write("  <reaction id=\"" + StringUtils.sbmlEncode(reaction.getId()) + "\" name=\""
                    + StringUtils.htmlEncode(reaction.getName()) + "\" reversible=\"" + reversibility + "\"" + ec + hole
                    + holder + type + ">\n");

            // Write score and status

            Double score = ReactionAttributes.getScore(reaction);

            if (score != null) {
                writer.write("    <score>" + score + "</score>\n");
            }

            String status = ReactionAttributes.getStatus(reaction);

            if (status != null) {
                writer.write("    <status>" + status + "</status>\n");
            }

            Set<Integer> pmids = ReactionAttributes.getPmids(reaction);
            if (pmids != null) {
                for (Integer pmid : pmids) {
                    writer.write("    <pmid>" + pmid + "</pmid>\n");
                }
            }

            Set<AnnotatorComment> comments = GenericAttributes.getAnnotatorComments(reaction);

            if (comments != null) {
                // Write comments
                for (AnnotatorComment comment : GenericAttributes.getAnnotatorComments(reaction)) {
                    writer.write("    <comment>\n");
                    writer.write("      <annotator>" + comment.annotator + "</annotator>\n");
                    writer.write("      <text>\n        <![CDATA[" + comment.comment + "]]>\n      </text>\n");
                    writer.write("    </comment>\n");
                }
            }

            // Writing the associations with the proteins and the genes
            // corresponding to the reaction
            // TODO : Hmmm, will fail when an enzyme is composed by complexes....

            for (BioEnzyme enzyme : reaction.getEnzymesView()) {

                writer.write("    <enzyme id=\"" + StringUtils.sbmlEncode(enzyme.getId()) + "\" name=\""
                        + StringUtils.htmlEncode(enzyme.getName()) + "\">\n");

                for (BioEnzymeParticipant p : enzyme.getParticipantsView()) {
                    BioPhysicalEntity e = p.getPhysicalEntity();

                    if (e.getClass().equals(BioProtein.class)) {

                        BioProtein prot = (BioProtein) e;

                        writer.write("      <protein id=\"" + StringUtils.sbmlEncode(prot.getId()) + "\" name=\""
                                + StringUtils.htmlEncode(prot.getName()) + "\">\n");

                        BioGene gene = prot.getGene();

                        if (gene != null) {
                            writer.write("        <gene id=\"" + StringUtils.sbmlEncode(gene.getId()) + "\" name=\""
                                    + StringUtils.htmlEncode(gene.getName()) + "\" />\n");
                        }

                        writer.write("      </protein>\n");

                    }
                }

                writer.write("    </enzyme>\n");

            }

            // Write the pathway where occurs the reaction

            for (BioPathway pathway : network.getPathwaysFromReaction(reaction)) {
                writer.write("    <pathway id=\"" + StringUtils.sbmlEncode(pathway.getId()) + "\" name=\""
                        + StringUtils.htmlEncode(pathway.getName()) + "\" />\n");
            }

            // Write the list of sides if they exist
            Set<String> sides = ReactionAttributes.getSideCompounds(reaction);

            if (sides != null && sides.size() > 0) {
                writer.write("    <side-compounds>\n");
                for (String cpd : sides) {
                    writer.write("      <speciesReference species=\"" + StringUtils.sbmlEncode(cpd) + "\" />\n");
                    writer.write("    </side-compounds>\n");
                }
            }

            // Writing the left and right compounds

            BioCollection<BioReactant> lefts = network.getLeftReactants(reaction);
            BioCollection<BioReactant> rights = network.getRightReactants(reaction);

            if (lefts.size() > 0) {
                writer.write("    <listOfReactants>\n");

                for (BioReactant reactant : lefts) {
                    this.writeReactant(reactant);
                }
                writer.write("    </listOfReactants>\n");
            }

            if (rights.size() > 0) {
                writer.write("    <listOfProducts>\n");

                for (BioReactant reactant : rights) {
                    this.writeReactant(reactant);
                }
                writer.write("    </listOfProducts>\n");
            }
            // Write flux informations

            Flux lb = ReactionAttributes.getLowerBound(reaction);
            Flux ub = ReactionAttributes.getUpperBound(reaction);

            Double lbValue;
            Double ubValue;

            String lbUnits = "mmol_per_gDW_per_hr";
            String ubUnits = "mmol_per_gDW_per_hr";

            if (lb == null) {
                if (reaction.isReversible()) {
                    lbValue = Flux.FLUXMIN;
                } else {
                    lbValue = 0.0;
                }
            } else {
                lbValue = lb.value;
                lbUnits = lb.unitDefinition.getId();
            }

            if (ub == null) {
                ubValue = Flux.FLUXMAX;
            } else {
                ubValue = ub.value;
                ubUnits = lb.unitDefinition.getId();
            }

            writer.write("    <kineticLaw>\n");
            writer.write("    <math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n");
            writer.write("      <ci> FLUX_VALUE </ci>\n");
            writer.write("    </math>\n");
            writer.write("    <listOfParameters>\n");
            writer.write("      <parameter id=\"LOWER_BOUND\" value=\"" + lbValue + "\" units=\"" + lbUnits + "\"/>\n");
            writer.write("      <parameter id=\"UPPER_BOUND\" value=\"" + ubValue + "\" units=\"" + ubUnits + "\"/>\n");
            writer.write("    </listOfParameters>\n");
            writer.write("    </kineticLaw>\n");

            writer.write("  </reaction>\n");
        }

        writer.write("</listOfReactions>\n");

    }

    private void writeEnd() {

        try {
            writer.write("</model>\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void writeReactant(BioReactant reactant) throws IOException {
        String idCpd = StringUtils.sbmlEncode(reactant.getPhysicalEntity().getId());
        Double stoe = reactant.getQuantity();
        writer.write("      <speciesReference species=\"" + idCpd + "\" stoichiometry=\"" + stoe + "\"/>\n");
    }

}
