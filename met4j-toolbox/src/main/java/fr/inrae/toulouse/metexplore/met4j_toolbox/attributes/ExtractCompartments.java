package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.GROUPS;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.NOTES;

public class ExtractCompartments extends AbstractMet4jApplication {

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Option(name = "-o", usage = "output SBML file", required = true)
    public String outputPath = null;

    @Option(name = "-id", usage = "identifiers of compartments to keep, separated by \"+\" sign if more than one; if start with \"-\" minus sign: keep all compartments but the following ones", required = true)
    public String compartmentId = null;

    @Option(name = "-tr", usage = "allows to truncate reactions if they involves reactants from both selected and non-selected compartment (Transport reactions will yield empty-sided exchange reactions)")
    public boolean trunc = false;

    public static void main(String[] args) throws IOException, Met4jSbmlReaderException, Met4jSbmlWriterException {

        ExtractCompartments app = new ExtractCompartments();

        app.parseArguments(args);

        app.run();

    }


    public void run() {
        // read smbl
        BioNetwork network = IOUtils.readSbml(this.inputPath, GROUPS, NOTES);

        System.out.println("Number of compartments in original Network: "+network.getCompartmentsView().size());
        System.out.println("Number of reactions in original network: "+network.getReactionsView().size());
        System.out.println("Number of species in original network: "+network.getMetabolitesView().size());
        System.out.println("Number of genes in original network: "+network.getGenesView().size());

        // get all reactions & metabolites
        BioCollection<BioReaction> reactions = new BioCollection<>(network.getReactionsView());
        BioCollection<BioMetabolite> metabolites = new BioCollection<>(network.getMetabolitesView());
        BioCollection<BioGene> genes = new BioCollection<>(network.getGenesView());

        boolean remove = compartmentId.startsWith("-");
        if(remove) compartmentId = compartmentId.substring(1);

        // get compartments
        BioCollection<BioCompartment> compartments = new BioCollection<>();
        for(String id : compartmentId.split("\\+")){
            BioCompartment compartment = network.getCompartmentsView().get(id);
            if(compartment!=null){
                compartments.add(compartment);
            }else{
                System.out.println("Error: Compartment "+id+" not found in network, please check sbml file.");
            }
        }

        if(!trunc){
            BioCollection<BioReaction> compartmentsReactions = new BioCollection<>();
            for(BioReaction r : network.getReactionsView()){
                if(!remove){
                    if (compartments.containsAll(r.getCompartments()))
                        compartmentsReactions.add(r); //keep reaction with all reactants in selected compartment(s)
                }else{
                    if(Collections.disjoint(compartments,r.getCompartments())) compartmentsReactions.add(r); //keep reaction with no reactant in selected compartment(s)
                }
            }
            // remove compartment's reactions and metabolites from list
            reactions.removeAll(compartmentsReactions);
            metabolites.removeAll(network.getMetabolitesFromReactions(compartmentsReactions));
            genes.removeAll(network.getGenesFromReactions(compartmentsReactions));

            // remove remaining reactions
            network.removeOnCascade(reactions);
            network.removeOnCascade(metabolites);
            network.removeOnCascade(genes);
        }else{
            BioCollection<BioMetabolite> compartmentsMetab = new BioCollection<>();
            for(BioCompartment compartment : compartments){
                for(BioEntity e : compartment.getComponentsView()){
                    if (e instanceof BioMetabolite) compartmentsMetab.add((BioMetabolite) e);
                }
            }
            if(remove){
                network.removeOnCascade(compartmentsMetab);
            }else{
                metabolites.removeAll(compartmentsMetab);
                network.removeOnCascade(metabolites);
            }
        }

        System.out.println("\n\nNumber of compartments in Network: "+network.getCompartmentsView().size());
        System.out.println("Number of reactions in network: "+network.getReactionsView().size());
        System.out.println("Number of species in network: "+network.getMetabolitesView().size());
        System.out.println("Number of genes in network: "+network.getGenesView().size());

        // export network
        IOUtils.writeSbml(network, this.outputPath);

        System.err.println("network exported.");
        return;
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Extract compartment(s) from a SBML file and create a sub-network SBML file. The sub-network will retain " +
                "all reactions where **all** of their participants belong in one of the user-defined compartments." +
                "If an exclusion is performed, all reaction where **any** of their participants belong in one of the user-defined compartments will be removed.";
    }

    @Override
    public String getShortDescription() {
        return "Extract compartment(s) from a SBML file and create a sub-network SBML file";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }


}
