package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ExtractSbmlAnnot extends AbstractMet4jApplication {

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name= EnumFormats.Tsv)
    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Option(name = "-o", usage = "output file path", required = true)
    public String outputPath = null;

    enum entity { METABOLITE,REACTION,GENE}

    @Option(name="-export", usage = "the type of entity to extract annotation, either metabolite, reaction, or gene", required = true)
    public entity export = entity.METABOLITE;

    @Option(name="-db", usage = "name of the referenced database to export annotations from, as listed in notes or identifiers.org base uri", required = true)
    public String db;

    @Option(name="-uniq", usage = "keep only one identifier if multiple are referenced for the same entity", required = false)
    public Boolean uniq = false;

    @Option(name="-skip", usage = "Skip entities without the selected annotations, by default output them with NA value", required = false)
    public Boolean skip = false;

    public String sep = "\t";


    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {

        ExtractSbmlAnnot app = new ExtractSbmlAnnot();

        app.parseArguments(args);

        app.run();

    }

    private void run() throws IOException, Met4jSbmlReaderException {
        //open file
        FileWriter fw = new FileWriter(outputPath);

        //read smbl
        JsbmlReader reader = new JsbmlReader(this.inputPath);
        BioNetwork network = reader.read();

        BioCollection<? extends BioEntity> entities = new BioCollection<>();
        if(export==entity.METABOLITE){
            entities=network.getMetabolitesView();
        }else if(export==entity.REACTION){
            entities=network.getReactionsView();
        }else if(export==entity.GENE){
            entities=network.getGenesView();
        }

        //write header
        fw.write(export.name()+sep+db.toUpperCase()+"\n");

        //export annotations
                //keep track of successful export
        int i = 0;
        for(BioEntity e : entities){

            Set<BioRef> refSet = e.getRefs(db);
            if(refSet!=null){
                i+=1;
                if(uniq)refSet=  new HashSet<BioRef>(Arrays.asList(refSet.iterator().next()));
                for(BioRef ref : refSet){
                    StringBuffer sb = new StringBuffer();
                    sb.append(e.getId());
                    sb.append(sep);
                    sb.append(ref.getId());
                    sb.append("\n");
                    fw.write(sb.toString());
                }
            }else if(!skip){
                StringBuffer sb = new StringBuffer();
                sb.append(e.getId());
                sb.append(sep);
                sb.append("NA\n");
                fw.write(sb.toString());
            }

        }
        fw.close();
        System.out.println("annotations found for "+i+"/"+entities.size()+" "+export.name().toLowerCase()+((i>1)?"s":""));

    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Extract databases' references from SBML annotations or notes. " +
                "The references are exported as a tabulated file with one column with the SBML compound, " +
                "reaction or gene identifiers, and one column with the corresponding database identifier." +
                "The name of the targeted database need to be provided under the same form than the one used " +
                "in the notes field or the identifiers.org uri";
    }

    @Override
    public String getShortDescription() {
        return "Extract databases' references from SBML annotations or notes.";
    }
}
