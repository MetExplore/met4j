package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.AnnotationParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.GroupPathwayParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.JsbmlWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import org.kohsuke.args4j.Option;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;

/**
 * <p>GroupRxnByEnzymeClass class.</p>
 * @author clement
 * this class creates pathways from EC numbers found in the model
 * It first removes all original pathway assignments
 * Then, for each EC number found in the model, it creates a pathway containing all reactions
 * with this EC number.
 * EC number assignment are propagated to their parent class (e.g. EC 1.2.3.4 will be added to pathways 1.2.3, 1.2 and 1)
 */
public class GroupRxnByEnzymeClass extends AbstractMet4jApplication {

    @ParameterType(name= EnumParameterTypes.Integer)
    @Option(name="-min", usage="minimum size of the EC class to convert as pathway")
    public int minsize=2;

    @ParameterType(name= EnumParameterTypes.Integer)
    @Option(name="-max", usage="maximum size of the EC class to convert as pathway")
    public int maxsize=200;

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name = EnumFormats.Sbml)
    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Option(name = "-o", usage = "output SBML file", required = true)
    public String outputPath = null;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) throws Met4jSbmlWriterException {
        GroupRxnByEnzymeClass app = new GroupRxnByEnzymeClass();
        app.parseArguments(args);
        app.run();
    }

    private void run() throws Met4jSbmlWriterException {

        //read sbml
        System.out.print("Reading SBML...");
        JsbmlReader reader = new JsbmlReader(inputPath);
        ArrayList<PackageParser> pkgs = new ArrayList<>(Arrays.asList(
                new AnnotationParser(true), new FBCParser(), new GroupPathwayParser()));
        BioNetwork network = null;
        try {
            network = reader.read(pkgs);
        } catch (
                Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println(" Done.");


        //create groups from EC number
        TreeMap<String, BioCollection<BioReaction>> ecmap = new TreeMap<>();
        BioCollection<BioReaction> noEC = new BioCollection<>();
        HashSet<String> ecNumbers = new HashSet<>();
//        ecmap.put("NA",new BioCollection<>());
        for(BioReaction r : network.getReactionsView()){

            Set<BioRef> refs = r.getRefs("ec-code");
            if(refs==null){
//                ecmap.get("NA").add(r);
                noEC.add(r);
            }else{
                for(BioRef ref : refs){
                    if(ref.getId()!=null && !ref.getId().isEmpty()) {

                        String ec = ref.getId();
                        //process EC number
                        //remove "unsaturated" EC numbers x.x.-.- become x.x, + trim non digit suffixes
                        Pattern basePattern = Pattern.compile("(.*\\d)(\\.[^0-9]*)+$");
                        Matcher m = basePattern.matcher(ec);
                        if (m.matches()) {
                            ec = m.group(1);
                        }
                        ec = ec.strip();

                        // check format, malformed ec are kept but raise warning
                        if(!ec.matches("^\\d+(\\.\\d+){0,3}$")) {
                            System.err.println("Warning: EC number format not recognized: " + ec + " in reaction " + r.getId()+". Expected format: 0.0.0.0");
                        }

                        //replace dots by underescore (for sbml pathway id compatibility)
                        ec = ec.replaceAll("[^a-zA-Z0-9]+", "_");
                        ecNumbers.add(ec);

                        //recursive ec class assignment: EC a.b.c.d affect to a.b.c.d, a.b.c, a.b and a
                        Pattern parentLvlsPattern = Pattern.compile("(.+)_[^_]+$"); //keep everything but last digit
                        boolean go = true;
                        while (go) {
                            if (!ecmap.containsKey(ec)) ecmap.put(ec, new BioCollection<>());
                            ecmap.get(ec).add(r);

                            m = parentLvlsPattern.matcher(ec);
                            if (m.matches()) {
                                ec = m.group(1);
                            } else {
                                go = false;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Reactions without EC number: "+noEC.size());
        System.out.println("\nEC numbers found: "+ecNumbers.size());
        System.out.println("Groups created: "+ecmap.size());

        // Purge original pathways:
            //remove original pathway assignments
        for(BioReaction r : network.getReactionsView()){
            for(BioPathway p : network.getPathwaysFromReaction(r)){
                network.removeReactionFromPathway(r,p);
            }
        }
            //remove original pathways
        for(BioPathway p : network.getPathwaysView()){
            network.removeOnCascade(p);
        }


        //create pathways from groups
        for(Map.Entry<String, BioCollection<BioReaction>> entry : ecmap.entrySet()){
            if(entry.getValue().size()>=minsize && entry.getValue().size()<=maxsize){//filter by size
                BioPathway p=new BioPathway(entry.getKey(),entry.getKey());
                network.add(p);
                //affect reactions to pathway
                network.affectToPathway(p,entry.getValue());
            }
        }

        //export network
        System.out.print("Exporting...");
        new JsbmlWriter(outputPath,network).write();
        System.out.println(" Done.");
        return;
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Alternative functional grouping of reactions in model :Replace pathways in model by groups of reactions sharing EC numbers. "  +
                "EC numbers are retrieved from annotation fields, and propagated to their parent class (e.g. EC 1.2.3.4 will be added to groups 1.2.3, 1.2 and 1).\n" +
                "Reactions without EC number are kept in the model but won't have any group assigned.\n" +
                "Original pathway assignments are erased.\n" +
                "EC groups with size out of the range [min-max] are ignored.";
    }

    @Override
    public String getShortDescription() {
        return "Alternative functional grouping of reactions in model : Replace pathways by groups of reactions sharing EC numbers";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
