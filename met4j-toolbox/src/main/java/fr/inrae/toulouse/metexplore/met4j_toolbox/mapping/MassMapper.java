package fr.inrae.toulouse.metexplore.met4j_toolbox.mapping;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_mapping.AttributeMapper;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.*;

/**
 * A tool to map a list of masses against metabolites from a SBML file
 *
 * @author clement
 * @version $Id
 */
public class MassMapper extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String sbmlPath = null;

    @Format(name = EnumFormats.Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-m", usage = "input mass file (one per line)", required = true)
    public String inputPath = null;

    @Option(name = "-ppm", usage = "mass delta tolerance in part per million", required = false)
    public Double ppm = 5.0;

    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Format(name = EnumFormats.Tsv)
    @Option(name = "-o", aliases = {"--output"}, usage = "output mapping file", required = true )
    public String outputPath = null;

    @Option(name = "-na", usage = "Output mass without match in model, with NA value", required = false)
    public Boolean na = false;

    public enum strategy {no, average, monoisotopic}
    @Option(name = "-comp", usage = "Compute mass from formulas for each compounds in the model. Use SBML attributes if not set", required = false)
    public strategy compute = strategy.no;

    public static void main(String[] args) {
        MassMapper app = new MassMapper();
        app.parseArguments(args);
        app.run();
    }

    private void run() {

        //import masses to map
        Set<Double> queries = new HashSet<>();
        try (FileReader fr = new FileReader(inputPath);
             BufferedReader br = new BufferedReader(fr);) {
            String line;
            while ((line = br.readLine()) != null) {
                String s = line.trim();
                if(!s.isEmpty()){
                    try {
                        Double d = Double.parseDouble(s);
                        queries.add(d);
                    }catch (NumberFormatException e){
                        System.err.println("Could not parse mass value: "+s);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error while reading the input file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Number of masses to map: " + queries.size());

        //read smbl
        BioNetwork bn = IOUtils.readSbml(this.sbmlPath, ALL);
        if(compute!=strategy.no) {
            System.out.println("Computing masses from formulas using "+compute+" mass");
            fr.inrae.toulouse.metexplore.met4j_chemUtils.MassComputor mc = new fr.inrae.toulouse.metexplore.met4j_chemUtils.MassComputor();
            if (compute == strategy.average) mc.useAverageMass();
            else if (compute == strategy.monoisotopic) mc.useMonoIsotopicMass();
            mc.setMolecularWeights(bn);
        }else {
            boolean check = false;
            for (BioMetabolite m : bn.getMetabolitesView()) {
                if (m.getMolecularWeight() != null) {
                    check = true;
                    break;
                }
            }
            if (!check) {
                System.err.println("No metabolite with mass found in the model, please check the input file or use the -comp option");
                System.exit(1);
            }
        }

        //map masses
        AttributeMapper<BioMetabolite,Double> mapper = new AttributeMapper<>(bn,
                BioNetwork::getMetabolitesView,
                AttributeMapper.selectByMass());
        mapper.setMatcher(AttributeMapper.useRelativeThreshold(ppm/1000000));//ppm to absolute
        Map<Double, List<BioMetabolite>> res = mapper.map(queries);
        System.out.println("Number of masses mapped: " + res.size());

        //write output: export tab delimited file with three columns: query mass, sbml metabolite mass, sbml metabolite id (one line per match)
        try (FileWriter fr = new FileWriter(outputPath);
             BufferedWriter br = new BufferedWriter(fr);) {
            for(Double q : queries) {
                if(res.get(q)==null || res.get(q).isEmpty()) {
                    if (na) br.write(q + "\t" + "NA" + "\t" + "NA" + "\n");
                }else{
                    for(BioMetabolite m : res.get(q)) {
                        br.write(q + "\t" + m.getMolecularWeight() + "\t" + m.getId() + "\n");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error while writing the output file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Retrieve metabolites in a SBML file from their mass.\n" +
                "The SBML file is expected to contain fbc:chemicalFormula attributes for species entries, in order to compute masses.\n" +
                "The input mass file should contain one mass per line. The output is a tab delimited file with two columns: query mass, sbml metabolite id (one line per match)";

    }

    @Override
    public String getShortDescription() {
        return "Retrieve metabolites in a SBML file from their mass.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
