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

package fr.inrae.toulouse.metexplore.met4j_io.kegg;


import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reactant.ReactantAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;




public class Kegg2BioNetwork {


    public String origin = "map";
    public BioNetwork network;
    public String keggOrgId;

    private HashMap<String,String> linkECGene= new HashMap<String,String>();
    private HashSet<String> geneList=new HashSet<String>();

    private ClientConfig config;
    private Client client;
    private WebResource webResource;


    public Kegg2BioNetwork(String idOrg){
        this.keggOrgId=idOrg;

        this.network = new BioNetwork(idOrg);

        this.config = new DefaultClientConfig();
        this.client = Client.create(this.getConfig());
        this.webResource = this.getClient().resource(this.getBaseURI());
    }

    public Kegg2BioNetwork(String idOrg, String ori) {
        this.keggOrgId = idOrg;

        this.network = new BioNetwork(idOrg);

        this.origin = ori;
        this.config = new DefaultClientConfig();
        this.client = Client.create((ClientConfig)this.getConfig());
        this.webResource = this.getClient().resource(this.getBaseURI());
    }

    /**
     * Main for testing class
     * @param args
     */
    public static void main(String[] args) {
        Kegg2BioNetwork ktbn = new Kegg2BioNetwork("hsa", "map");
        try {
            ktbn.createBionetworkFromKegg();
        }
        catch (Exception e) {
            ktbn.network = null;
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void createBionetworkFromKegg()  {
        try {
            this.setBionetworkDefaultValue();
            System.err.println("Retrieving Genomic Data...");
            this.retrieveGPR();

            System.err.println("Retrieving Pathways topology...");
            this.getPathways();

            System.err.println("Retrieving Data on Reactions...");
            for (BioReaction rxn : this.network.getReactionsView()) {
                this.getReactionData(rxn);
            }

            System.err.println("Retrieving Data on Metabolites...");
            for (BioMetabolite ent : this.network.getMetabolitesView()) {
                this.getCompoundData(ent);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.network = null;
        }
    }

    /**
     * Set the default value for the compartment and the unitdefinition used in the model,
     * also retrieves the name of the network
     */
    public void setBionetworkDefaultValue(){

        this.getNetWorkName();

        BioCompartment defaultCompartment = new BioCompartment("x", "default");
        this.network.add(defaultCompartment );

        BioUnitDefinition unitDef= new BioUnitDefinition();

        NetworkAttributes.addUnitDefinition(this.network, unitDef);

    }


    public void retrieveGPR() throws Exception {
        String[] genes= null;

        String geneString=this.webResource.path("link").path("genome").path(this.keggOrgId).get(String.class);

        genes=geneString.split("\\n");

        if(genes.length==0){
            throw new Exception("Unable to retrieve the genomic data of the organism "+this.keggOrgId) ;
        }

        for (String line :genes){
            String[] geneData=line.split("\\t");

            this.geneList.add(geneData[0]);

        }

        this.setLinkECGene(this.keggOrgId);

    }

    /**
     * Retrieves all pathways associated to the organism using the Kegg API.
     * @throws Exception
     */
    public void getPathways() throws Exception{
        String[] pathList= null;

        pathList=this.webResource.path("list").path("pathway").path(this.keggOrgId).get(String.class).split("\\n");
        for (String line :pathList){
            String[] pathwayData=line.split("\\t");
            String dbid=this.simplifyId(pathwayData[0].substring(5));
            BioPathway path=new BioPathway(dbid,pathwayData[1]);

            this.network.add(path);

            this.getPathwayComponents(path);

        }
    }


    public void getPathwayComponents(BioPathway pathway) throws Exception {

        String xml=null;
        xml=this.webResource.path("get").path(pathway.getId()).path("kgml").accept(MediaType.APPLICATION_XML).get(String.class);

        Document doc=Kegg2BioNetwork.loadXMLFromString(xml);
        doc.getDocumentElement().normalize();


        HashMap<String,String> RectionInPath = new HashMap<String, String>();

        NodeList entryList = doc.getElementsByTagName("entry");
        for (int i=0,c=entryList.getLength();i<c;i++ ){
            Node entry=entryList.item(i);
            if(((Element) entry).getAttribute("type").equalsIgnoreCase("GENE")){

                /*
                 * get all reaction that have known genes associated to it
                 */
                RectionInPath.put(((Element) entry).getAttribute("reaction"), ((Element) entry).getAttribute("name"));
            }
        }

        NodeList reactionList=doc.getElementsByTagName("reaction");
        for(int i=0,c=reactionList.getLength(); i<c ;i++){
            //get the reaction node
            Node entry=reactionList.item(i);
            Element rxn=(Element) entry;

            if(RectionInPath.containsKey(rxn.getAttribute("name"))){
                String[] reactionIds=rxn.getAttribute("name").split(" ");

                for(String longId:reactionIds){

                    String id = this.simplifyId(longId).replace("rn_", "");
                    BioReaction reaction;
                    if(this.network.getReactionsView().containsId(id)){
                        reaction=this.network.getReactionsView().get(id);
                    }
                    else{
                        reaction=new BioReaction(id);

                        this.network.add(reaction);

                        if(rxn.getAttribute("type").equalsIgnoreCase("reversible")){
                            reaction.setReversible(true);
                        }else{
                            reaction.setReversible(false);
                        }

                        /*
                         * Set the GPR for this reaction
                         * TODO : LC 2019-10-09 not really done for complex GPR, no ?
                         */
                        String[] genes=RectionInPath.get(rxn.getAttribute("name")).split(" ");
                        for(String longGeneId:genes){

                                String geneId = this.simplifyId(longGeneId);
                                BioGene gene=new BioGene(geneId);
                                BioProtein prot=new BioProtein(geneId);
                                BioEnzyme enz = new BioEnzyme(geneId);
                                network.add(gene, prot, enz);
                                network.affectGeneProduct(prot, gene);
                                network.affectSubUnit(enz, 1.0, prot);
                                network.affectEnzyme(reaction, enz);
                        }
                        if (this.origin.equals("map")) {
                            NodeList childs = rxn.getChildNodes();
                            int d = childs.getLength();
                            for (int j = 1; j < d; j += 2) {
                                BioMetabolite cpd;
                                Node child = childs.item(j);
                                Element Compound = (Element)child;
                                BioCompartment cpt = this.network.getCompartmentsView().get("x");

                                String metaboliteId = this.simplifyMetaboliteId(Compound.getAttribute("name"));

                                if (this.network.getMetabolitesView().containsId(metaboliteId)) {
                                    cpd = this.network.getMetabolitesView().get(metaboliteId);
                                } else {
                                    cpd = new BioMetabolite(metaboliteId);
                                    this.network.add(cpd);
                                    this.network.affectToCompartment(cpt, cpd);
                                }
                                if (child.getNodeName().equalsIgnoreCase("substrate")) {
                                    BioReactant lpart = new BioReactant(cpd, 1.0, cpt);
                                    ReactionAttributes.setConstant(lpart, false);
                                    network.affectLeft(reaction, lpart);
                                    continue;
                                }
                                BioReactant rpart = new BioReactant(cpd, 1.0, cpt);
                                ReactionAttributes.setConstant(rpart, false);
                                network.affectRight(reaction, rpart);
                            }
                        }

                    }

                    network.affectToPathway(pathway, reaction);
                }
            }
        }

		/*
		System.err.println(this.bioNetwork.getReactionsView().size()+" reaction added to network");
		System.err.println(this.bioNetwork.getPhysicalEntityList().size()+" metabolites added");
		System.exit(0);*/
    }

    private void getNetWorkName() {
        String[] data= null;
        String name;
        try {
            data=this.webResource.path("info").path(this.keggOrgId).get(String.class).split("\\n");
            name=data[0].split("\\s{2,}")[1];
        }catch(UniformInterfaceException e){
            name="undefined";
        }


        this.network.setName(name);

    }

    /**
     * Retrieve Reaction data from the Kegg database and add the other attributes by using default values
     * from the sbml specifications (2.4)
     * @param rxn
     */
    private void getReactionData(BioReaction rxn) {

        HashMap<String, ArrayList<String>> Data=this.getEntityDataHasHash(rxn.getId());

        if(Data.get("NAME")!=null){
            rxn.setName( Data.get("NAME").get(0));
        }
        if(Data.get("COMMENT")!=null){
            rxn.setComment( Data.get("COMMENT").get(0));
        }
        if (this.origin.equals("reaction") && Data.get("EQUATION") != null && Data.get("EQUATION").get(0).contains("<=>")) {
            String longCpdId;
            String[] tmp2;
            BioMetabolite cpd;
            String[] tmp;
            String stoechio;
            String[] eq = Data.get("EQUATION").get(0).split(" <=> ");
            String[] subs = eq[0].split(" \\+ ");
            String[] prod = eq[1].split(" \\+ ");

           BioCompartment cpt = this.network.getCompartmentsView().get("x");

            for (String substrateAndStoechio : subs) {
                tmp = substrateAndStoechio.split("[ ]+");
                if (tmp.length == 2) {
                    stoechio = tmp[0];
                    longCpdId = tmp[1].replaceAll("\\([^\\)]+\\)", "");
                } else if (Pattern.compile("^\\d+[\\D+]+.*").matcher(tmp[0]).matches()) {
                    tmp2 = tmp[0].split("(?<=\\d+)(?=\\D+)");
                    stoechio = tmp2[0];
                    longCpdId = tmp2[1].replaceAll("\\([^\\)]+\\)", "");
                } else {
                    stoechio = "1.0";
                    longCpdId = substrateAndStoechio.replaceAll("\\([^\\)]+\\)", "");
                }

                String cpdId = this.simplifyMetaboliteId(longCpdId);

                if (this.network.getMetabolitesView().containsId(cpdId)) {
                    cpd = this.network.getMetabolitesView().get(cpdId);
                } else {
                    cpd = new BioMetabolite(cpdId);
                    this.network.add(cpd);
                    this.network.affectToCompartment(cpt, cpd);
                }
                BioReactant lpart = new BioReactant(cpd, Double.parseDouble(stoechio), cpt);
                ReactantAttributes.setConstant(lpart, false);
                network.affectLeft(rxn, lpart);
            }
            for (String productAndStoechio : prod) {

                tmp = productAndStoechio.split("[ ]+");
                if (tmp.length == 2) {
                    stoechio = tmp[0];
                    longCpdId = tmp[1].replaceAll("\\([^\\)]+\\)", "");
                } else if (Pattern.compile("^\\d+[\\D+]+.*").matcher(tmp[0]).matches()) {
                    tmp2 = tmp[0].split("(?<=\\d+)(?=\\D+)");
                    stoechio = tmp2[0];
                    longCpdId = tmp2[1].replaceAll("\\([^\\)]+\\)", "");
                } else {
                    stoechio = "1.0";
                    longCpdId = productAndStoechio.replaceAll("\\([^\\)]+\\)", "");
                }

                String cpdId = this.simplifyMetaboliteId(longCpdId);

                if (this.network.getMetabolitesView().containsId(cpdId)) {
                    cpd = this.network.getMetabolitesView().get(cpdId);
                } else {
                    cpd = new BioMetabolite(cpdId);
                    this.network.add(cpd);
                    this.network.affectToCompartment(cpt, cpd);
                }
                BioReactant rpart = new BioReactant(cpd, Double.parseDouble(stoechio), cpt);
                ReactantAttributes.setConstant(rpart, false);
                network.affectRight(rxn, rpart);
            }
        }
        if(Data.get("ENZYME")!=null){
            for(String ecNum:Data.get("ENZYME")){
                if(this.linkECGene.containsKey(ecNum)){
                    if (rxn.getEcNumber() == null || rxn.getEcNumber().isEmpty() || rxn.getEcNumber().equalsIgnoreCase(" ")){
                        rxn.setEcNumber(ecNum);
                    }
                    else{
                        rxn.setEcNumber(rxn.getEcNumber()+" / "+ecNum);
                    }
                }
            }
        }

    }

    /**
     * Retrieve data for each compound from the Kegg database and add the other attributes by using default values
     * from the sbml specifications (2.4)
     * Note that those compounds can come from Kegg Compound Database or from Kegg Glycan Database
     * @param metabolite
     */
    private void getCompoundData(BioMetabolite metabolite){

        MetaboliteAttributes.setBoundaryCondition(metabolite, false);
        MetaboliteAttributes.setConstant(metabolite, false);
        MetaboliteAttributes.setHasOnlySubstanceUnits(metabolite, false);

        HashMap<String, ArrayList<String>> Data=this.getEntityDataHasHash(metabolite.getId());

        if(Data.get("NAME")!=null){ //kegg glycans entries do not always have names or chemical formulas
            metabolite.setName( Data.get("NAME").get(0));
        }
        if(Data.get("FORMULA") != null){
            metabolite.setChemicalFormula(Data.get("FORMULA").get(0));
        }
        if(Data.get("MASS")!=null){
            metabolite.setMolecularWeight(Double.parseDouble(Data.get("MASS").get(0)));
        }else if(Data.get("MOL_WEIGHT")!= null){
            metabolite.setMolecularWeight(Double.parseDouble(Data.get("MOL_WEIGHT").get(0)));
        }

        metabolite.addRef(new BioRef("Import", "kegg.compound", metabolite.getId(), 1));

        if (Data.get("DBLINKS")!=null){
            for (String links: Data.get("DBLINKS")){
                String[] tab=links.split(": ");
                metabolite.addRef(new BioRef("Import", tab[0], tab[1], 1));
            }
        }
    }



    private String simplifyMetaboliteId(String id) {
        String[] tempArray=id.split("[: ]");
        if(tempArray.length>1){
            return tempArray[1];
        }
        else {
            return id;
        }
    }

    private String simplifyId(String id) {
       return id.replaceAll("[: ]", "_");
    }

    private URI getBaseURI() {
        return UriBuilder.fromUri("http://rest.kegg.jp/").build();
    }

    /**
     *
     * @param xml
     * @return
     * @throws Exception
     */
    private static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    /**
     *
     * @param id
     * @return
     */
    public HashMap<String, ArrayList<String>> getEntityDataHasHash(String id) {

        String[] Data=this.webResource.path("get").path(id).get(String.class).split("\\n");

        String lastKey=null;
        HashMap<String, ArrayList<String>> output=new HashMap<String, ArrayList<String>>();

        for (String line: Data){
            //System.err.println(line);

            String[] linedata=line.split("[ ]{2,}");
            //System.err.println(linedata.length);
            if(linedata[0].length()!=0){
                lastKey=linedata[0];
            }
            //System.err.println("value of index 0 '"+linedata[0]+"'. of length "+linedata[0].length());
            //System.err.println("value of last key "+lastKey);


            if(output.containsKey(lastKey) && !lastKey.equalsIgnoreCase("///")){
                //System.err.println("add to last");
                for (int i=1, c=linedata.length;i<c;i++){
                    output.get(lastKey).add(linedata[i].replace(";", ""));
                }
            }else if (!output.containsKey(lastKey) && !lastKey.equalsIgnoreCase("///")){
                //System.err.println("add to new");
                ArrayList<String> tempData = new  ArrayList<String>();
                for (int i=1, c=linedata.length;i<c;i++){
                    tempData.add(linedata[i].replace(";", ""));
                }

                output.put(lastKey, tempData);
            }else{
                break;
            }
        }

        return output;
    }

    public boolean checkKeggOrgId(String keggId){

        String[] Data=this.webResource.path("list").path("genome").get(String.class).split("\\n");

        for(String genome: Data){
            String[] tab=genome.split("\\t");
            if (tab[1].split(",")[0].equalsIgnoreCase(keggId)){
                return true;
            }
        }
        return false;

    }

    public BioNetwork getNetwork() {
        return network;
    }


    public String getKeggOrgId() {
        return keggOrgId;
    }


    public void setNetwork(BioNetwork network) {
        this.network = network;
    }


    public void setKeggOrgId(String keggOrgId) {
        this.keggOrgId = keggOrgId;
    }

    public ClientConfig getConfig() {
        return config;
    }

    public Client getClient() {
        return client;
    }

    public WebResource getWebResource() {
        return webResource;
    }

    public void setConfig(ClientConfig config) {
        this.config = config;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setWebResource(WebResource webResource) {
        this.webResource = webResource;
    }

    public HashMap<String,String> getLinkECGene() {
        return linkECGene;
    }

    public void setLinkECGene(String id) {
        String[] Data=this.webResource.path("link").path("ec").path(id).get(String.class).split("\\n");

        for (String line: Data){
            String[] tmp=line.split("\t");
            linkECGene.put(tmp[1].substring(3, tmp[1].length()) , tmp[0]);
        }

    }

}
