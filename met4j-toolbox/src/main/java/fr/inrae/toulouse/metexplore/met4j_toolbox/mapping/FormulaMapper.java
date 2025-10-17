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
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.ALL;

/**
 * A tool to map a list of formulas against metabolites from a SBML file
 *
 * @author clement
 * @version $Id
 */
public class FormulaMapper extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String sbmlPath = null;

    @Format(name = EnumFormats.Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-f", usage = "input formula file (one per line)", required = true)
    public String inputPath = null;

    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Format(name = EnumFormats.Tsv)
    @Option(name = "-o", aliases = {"--output"}, usage = "output mapping file", required = true )
    public String outputPath = null;

    @Option(name = "-na", usage = "Output formulas without match in model, with NA value", required = false)
    public Boolean na = false;

    public static void main(String[] args) {
        FormulaMapper app = new FormulaMapper();
        app.parseArguments(args);
        app.run();
    }

    private void run() {

        //import formulas to map
        Set<String> queries = new HashSet<>();
        try (FileReader fr = new FileReader(inputPath);
             BufferedReader br = new BufferedReader(fr);) {
            String line;
            while ((line = br.readLine()) != null) {
                String s = line.trim();
                if(!s.isEmpty()){
                    queries.add(s);
                }
            }

        } catch (IOException e) {
            System.err.println("Error while reading the input file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Number of formulas to map: " + queries.size());

        //read smbl
        BioNetwork bn = IOUtils.readSbml(this.sbmlPath, ALL);

        //map masses
        AttributeMapper<BioMetabolite,String> mapper = new AttributeMapper<>(bn,
                BioNetwork::getMetabolitesView,
                AttributeMapper.selectByFormula());
        Map<String, List<BioMetabolite>> res = mapper.map(queries);
        System.out.println("Number of masses mapped: " + res.size());

        //write output: export tab delimited file with three columns: query formulas, sbml metabolite formulas, sbml metabolite id (one line per match)
        try (FileWriter fr = new FileWriter(outputPath);
             BufferedWriter br = new BufferedWriter(fr);) {
            for(String q : queries) {
                if(res.get(q)==null || res.get(q).isEmpty()) {
                    if (na) br.write(q + "\t" + "NA" + "\t" + "NA" + "\n");
                }else{
                    for(BioMetabolite m : res.get(q)) {
                        br.write(q + "\t" + m.getChemicalFormula() + "\t" + m.getId() + "\n");
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
        return "";

    }

    @Override
    public String getShortDescription() {
        return "";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
