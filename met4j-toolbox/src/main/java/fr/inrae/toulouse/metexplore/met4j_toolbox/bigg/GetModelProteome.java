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

package fr.inrae.toulouse.metexplore.met4j_toolbox.bigg;

import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class GetModelProteome extends AbstractMet4jApplication {

    public String description = "Get proteome in fasta format of a model present in BIGG";
    @Option(name = "-m", usage = "[ex: iMM904] id of the BIGG model", required = true)
    public String modelId = "iMM904";
    @Option(name = "-o", usage = "[proteome.fas] path of the output file")
    public String outputFile = "proteome.fas";
    private String baseUrl = "http://bigg.ucsd.edu/api/v2/models/";

    public static void main(String[] args) throws ProtocolException {
        GetModelProteome f = new GetModelProteome();
        CmdLineParser parser = new CmdLineParser(f);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println("Error in arguments");
            parser.printUsage(System.err);
            System.exit(0);
        }

        f.run();

    }

    /**
     * Run application
     */
    public void run() throws ProtocolException {

        URL urlGenes = null;
        try {
            urlGenes = this.createGenesUrl();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.err.println("Malformed url : please check that the model id does not include spaces or odd characters");
            System.exit(0);
        }

        Boolean check = this.checkConnection(urlGenes);

        if (!check) {
            System.exit(0);
        }

        String jsonStringGenes = null;
        try {
            jsonStringGenes = this.fetchContent(urlGenes);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem when uploading genes");
            System.exit(0);
        }

        Set<Sequence> sequences = null;
        try {
            sequences = this.readJsonGenes(this.StringToJson(jsonStringGenes));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Problem when reading genes json");
            System.exit(0);
        }

        try {
            this.writeFastaFile(sequences);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem when writing the fasta file");
            System.exit(0);
        }


        return;
    }





    /**
     * @return a @{@link URL} instance to access the genes json file
     * @throws MalformedURLException
     */
    private URL createGenesUrl() throws MalformedURLException {
        URL url = new URL(this.baseUrl + this.modelId + "/genes");
        return url;
    }

    /**
     * @param geneId the bigg gene id
     * @return @{@link URL} instance to access the gene json file
     * @throws MalformedURLException
     */
    private URL createGeneUrl(String geneId) throws MalformedURLException {
        URL url = new URL(this.baseUrl + this.modelId + "/genes/" + geneId);
        return url;
    }

    /**
     * Check a connection from an url
     *
     * @param url @{@link URL} instance
     * @return true if ok, false otherwise
     * @throws ProtocolException
     */
    private Boolean checkConnection(URL url) throws ProtocolException {

        HttpURLConnection conn;

        try {
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                System.err.println("Problem of connection");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Problem of connection");
            return false;
        }

        return true;


    }


    /**
     * Reads an url and store its content in a string
     *
     * @param url a @{@link URL} instance
     * @return a String
     * @throws IOException
     */
    private String fetchContent(URL url) throws IOException {
        String inline = "";

        Scanner sc = new Scanner(url.openStream());
        while (sc.hasNext()) {
            inline += sc.nextLine();
        }

        sc.close();

        return inline;
    }


    /**
     * @param str : a json in string format
     * @return a @{@link JSONObject} instance
     */
    private JSONObject StringToJson(String str) throws ParseException {

        JSONParser parse = new JSONParser();
        JSONObject jsonObject = (JSONObject) parse.parse(str);

        return jsonObject;

    }


    /**
     * Read the genes json
     *
     * @param jsonGenes
     * @return a @{@link Set} of @{@link Sequence}
     * @throws IOException
     * @throws ParseException
     */
    private Set<Sequence> readJsonGenes(JSONObject jsonGenes) throws IOException, ParseException {
        JSONArray jsonArray = (JSONArray) jsonGenes.get("results");

        Set<Sequence> sequences = new HashSet<>();

        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            Sequence seq = new Sequence();
            if(jsonObject.containsKey("bigg_id") && jsonObject.get("bigg_id") != null) {
                seq.id = jsonObject.get("bigg_id").toString();
                if(jsonObject.containsKey("name") && jsonObject.get("name") != null) {
                    seq.name = jsonObject.get("name").toString();
                }
                seq.sequence = this.getProteinSequence(seq.id);
            }
            sequences.add(seq);
        }

        return sequences;


    }

    /**
     * @param geneId bigg GeneId
     * @return the sequence
     * @throws IOException
     * @throws ParseException
     */
    private String getProteinSequence(String geneId) throws IOException, ParseException {
        URL urlGene = this.createGeneUrl(geneId);

        String sequence = "";
        Boolean check = this.checkConnection(urlGene);

        if (!check) {
            System.err.println("Problem while loading sequence of the gene " + geneId);
            return sequence;
        }

        String jsonString = this.fetchContent(urlGene);

        JSONObject jsonObject = this.StringToJson(jsonString);

        if (jsonObject.containsKey("protein_sequence") && jsonObject.get("protein_sequence") != null) {
            sequence = jsonObject.get("protein_sequence").toString();
        }

        return sequence;
    }

    /**
     * Write sequences in the output fasta file
     *
     * @param sequences
     * @throws IOException
     */
    private void writeFastaFile(Set<Sequence> sequences) throws IOException {
        FileWriter writer = new FileWriter(new File(this.outputFile));

        for (Sequence seq : sequences) {
            if(! seq.sequence.isEmpty())
                writer.write(seq.toFasta());
        }

        writer.close();

        return;


    }


    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return description;
    }
}
