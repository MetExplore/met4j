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
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.ANNOTATIONS;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.NOTES;

/**
 * A tool to map external metabolite identifiers (kegg, metanetx, pubchem CID...) to metabolite ids from a SBML file
 *
 * @author clement
 * @version $Id
 */
public class IdMapper extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String sbmlPath = null;

    @Format(name = EnumFormats.Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-id", usage = "input external id file (one per line)", required = true)
    public String inputPath = null;

    @Option(name = "-db", usage = "name of the referenced database annotations to map against, as listed in identifiers.org base uri", required = true)
    public String db;

    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Format(name = EnumFormats.Tsv)
    @Option(name = "-o", aliases = {"--output"}, usage = "output mapping file", required = true )
    public String outputPath = null;

    @Option(name = "-na", usage = "Output id without matching annotation in model, with NA value", required = false)
    public Boolean na = false;

    public static void main(String[] args) {
        IdMapper app = new IdMapper();
        app.parseArguments(args);
        app.run();
    }

    private void run() {

        //import external ids to map
        Set<String> queries = new HashSet<>();
        try (FileReader fr = new FileReader(inputPath);
             BufferedReader br = new BufferedReader(fr);) {
            String line;
            while ((line = br.readLine()) != null) {
                queries.add(line.trim());
            }

        } catch (IOException e) {
            System.err.println("Error while reading the input file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Number of ids to map: " + queries.size());

        //read smbl
        BioNetwork bn = IOUtils.readSbml(this.sbmlPath, ANNOTATIONS, NOTES);

        //map ids
        AttributeMapper<BioMetabolite,String> mapper = new AttributeMapper<>(bn,
                BioNetwork::getMetabolitesView,
                AttributeMapper.selectByExternalId(db));
        Map<String, List<BioMetabolite>> res = mapper.map(queries);
        System.out.println("Number of ids mapped: " + res.size());

        //write output: export tab delimited file with two columns: query id, sbml metabolite id (one line per match)
        try (FileWriter fr = new FileWriter(outputPath);
             BufferedWriter br = new BufferedWriter(fr);) {
            for(String q : queries) {
                if(res.get(q)==null || res.get(q).isEmpty()) {
                    if (na) br.write(q + "\t" + "NA" + "\n");
                }else{
                    for(BioMetabolite m : res.get(q)) {
                        br.write(q + "\t" + m.getId() + "\n");
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
        return "Map external metabolite identifiers (kegg, metanetx, pubchem CID...) to metabolite ids from a SBML file.\n" +
                "The SBML file is expected to contain annotations in MIRIAM format for the selected database:\n" +
                "i.e, <species> entries in the sbml should contain an <annotation> field where there is references to the given database." +
                "check identifiers.org for valid database names and associated base URIs.\n" +
                "The input id file should contain one id per line. The output is a tab delimited file with two columns: query id, sbml metabolite id (one line per match)";

    }

    @Override
    public String getShortDescription() {
        return "Map external metabolite identifiers (kegg, metanetx, pubchem CID...) to metabolite ids from a SBML file";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
