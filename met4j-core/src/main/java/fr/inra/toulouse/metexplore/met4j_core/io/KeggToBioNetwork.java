/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.io;

import fr.inra.toulouse.metexplore.met4j_core.biodata.*;

import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

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




public class KeggToBioNetwork {

	
	BioNetwork bioNetwork=new BioNetwork();
	String keggOrgId;

	private HashMap<String,String> linkECGene= new HashMap<String,String>();
	private HashSet<String> geneList=new HashSet<String>();

	private ClientConfig config;
	private Client client;
	private WebResource Webservice;


	public KeggToBioNetwork(String idOrg){
		keggOrgId=idOrg;

		config = new DefaultClientConfig();
		client = Client.create(this.getConfig());
		Webservice = this.getClient().resource(this.getBaseURI());
	}

	/**
	 * Main for testing class
	 * @param args
	 */
	public static void main(String[] args){

		KeggToBioNetwork ktbn=new KeggToBioNetwork("hsa");

		//ktbn.getEntityDataHasHash("C01290");


		try {
			ktbn.createBionetworkFromKegg();

			System.err.println(ktbn.bioNetwork.getUnitDefinitions().size()+" unit def");
			System.err.println(ktbn.bioNetwork.getGeneList().size()+" genes added");
			System.err.println(ktbn.bioNetwork.getProteinList().size()+" proteins added");
			System.err.println(ktbn.bioNetwork.getPathwayList().size()+" pathway added");
			System.err.println(ktbn.bioNetwork.getBiochemicalReactionList().size()+" reaction added to network");
			System.err.println(ktbn.bioNetwork.getPhysicalEntityList().size()+" metabolites added");


		} catch (Exception e){
			ktbn.bioNetwork=null;
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
			this.retievePathways();

			System.err.println("Retrieving Data on Metabolites...");
			for(BioPhysicalEntity ent: this.bioNetwork.getPhysicalEntityList().values()){
				this.getCompoundData(ent);
			}
			
			System.err.println("Retrieving Data on Reactions...");
			for(BioChemicalReaction rxn: this.bioNetwork.getBiochemicalReactionList().values()){
				this.getReactionData(rxn);
			}

			this.refactorNetworkIDS();
			
			
		} catch (Exception e){
			e.printStackTrace();
			this.bioNetwork=null;
		}
	}

	/**
	 * Set the default value for the compartment and the unitdefinition used in the model, 
	 * also retrieves the name of the network 
	 */
	public void setBionetworkDefaultValue(){

		this.bioNetwork.setId(this.keggOrgId);
		this.getNetWorkName();


		BioCompartment dfltcpt=new BioCompartment("Default", "x");
		this.bioNetwork.addCompartment(dfltcpt);

		BioUnitDefinition unitDef= new BioUnitDefinition("mmol_per_gDW_per_hr", "mmol_per_gDW_per_hr");
		this.bioNetwork.addUnitDefinition(unitDef);

	}

	
	public void retrieveGPR() throws Exception {
		String[] genes= null;

		String geneString=this.Webservice.path("link").path("genome").path(this.keggOrgId).get(String.class);
		
		genes=geneString.split("\\n");
		
		if(genes.length==0){
			throw new Exception("Unable to retrieve the genomic data of the organism "+this.keggOrgId) ;
		}
		
		for (String line :genes){
			String[] geneData=line.split("\\t");

			this.geneList.add(geneData[0]);

			/*
			BioGene gene=new BioGene(geneData[0]);
			BioProtein prot=new BioProtein(geneData[0]);
			prot.addGene(gene);
			prot.setCompartment(this.bioNetwork.getCompartments().get("In"));
			gene.addProtein(prot);

			this.bioNetwork.addGene(gene);
			this.bioNetwork.addProtein(prot);
			this.bioNetwork.getEnzymeList().put(geneData[0], prot);*/
		}

		this.setLinkECGene(this.keggOrgId);

	}

	/**
	 * Retrieves all pathways associated to the organism using the Kegg API.
	 * @throws Exception
	 */
	public void retievePathways() throws Exception{
		String[] pathList= null;

		pathList=this.Webservice.path("list").path("pathway").path(this.keggOrgId).get(String.class).split("\\n");
		for (String line :pathList){
			String[] pathwayData=line.split("\\t");
			String dbid=pathwayData[0].substring(5);
			BioPathway path=new BioPathway(dbid,pathwayData[1]);

			this.getPathwayEntities(path);
			
			if(!path.getReactions().isEmpty()){
				this.bioNetwork.addPathway(path);
			}

		}
	}


	public void getPathwayEntities(BioPathway path) throws Exception {

		String xml=null;
		xml=this.Webservice.path("get").path(path.getId()).path("kgml").accept(MediaType.APPLICATION_XML).get(String.class);

		Document doc=KeggToBioNetwork.loadXMLFromString(xml);
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

				for(String id:reactionIds){

					BioChemicalReaction reaction;
					if(this.bioNetwork.getBiochemicalReactionList().containsKey(id)){
						reaction=this.bioNetwork.getBiochemicalReactionList().get(id);
					}
					else{
						reaction=new BioChemicalReaction(id);

						if(rxn.getAttribute("type").equalsIgnoreCase("reversible")){
							reaction.setReversibility(true);
						}else{
							reaction.setReversibility(false);
						}

						/*
						 * Set the GPR for this reaction
						 */
						String[] genes=RectionInPath.get(rxn.getAttribute("name")).split(" ");
						for(String geneId:genes){

							BioProtein prot;
							if (this.bioNetwork.getEnzymeList().containsKey(geneId)){
								prot=this.bioNetwork.getProteinList().get(geneId);
							}
							else{
								BioGene gene=new BioGene(geneId);
								prot=new BioProtein(geneId);
								prot.addGene(gene);
								prot.setCompartment(this.bioNetwork.getCompartments().get("x"));
								gene.addProtein(prot);

								this.bioNetwork.addGene(gene);
								this.bioNetwork.addProtein(prot);
								this.bioNetwork.getEnzymeList().put(geneId, prot);
							}
							reaction.addEnz(prot);
						}

						//get reaction childnodes to retrieve substrate and products
						NodeList childs=rxn.getChildNodes(); 
						/*
						 *  here it gets opening and closing child nodes
						 *  it is why it is necessary to increment loop by 2
						 */
						for(int j=1,d=childs.getLength();j<d;j=j+2){

							Node child=childs.item(j);
							Element Compound=(Element) child;

							BioPhysicalEntity cpd;
							if(this.bioNetwork.getPhysicalEntityList().containsKey( Compound.getAttribute("name") ) ){
								cpd=this.bioNetwork.getPhysicalEntityList().get(Compound.getAttribute("name"));
							}else{
								cpd=new BioPhysicalEntity(Compound.getAttribute("name"));
								cpd.setCompartment(this.bioNetwork.getCompartments().get("x"));
								this.bioNetwork.addPhysicalEntity(cpd);
							}

							//add compound as substrate or product
							if(child.getNodeName().equalsIgnoreCase("substrate")){
								BioPhysicalEntityParticipant lpart=new BioPhysicalEntityParticipant(cpd);
								lpart.setIsConstant(false);
								reaction.addLeftParticipant(lpart);
							}else{
								BioPhysicalEntityParticipant rpart=new BioPhysicalEntityParticipant(cpd);
								rpart.setIsConstant(false);
								reaction.addRightParticipant(rpart);
							}
						}
						//System.err.println("Reaction "+reaction.getId()+" has "+reaction.getEnzList().size()+" enzymes");
						this.bioNetwork.addBiochemicalReaction(reaction);
					}
					
					reaction.setKineticFormula("FLUX_VALUE");
					
					BioUnitDefinition UD=this.getBioNetwork().getUnitDefinitions().get("mmol_per_gDW_per_hr");
					Flux newflux=new Flux("0",UD);
					reaction.addFluxParam("FLUX_VALUE", newflux);
					
					UD=new BioUnitDefinition("dimensionless","dimensionless");
					newflux=new Flux("0",UD);
					reaction.addFluxParam("OBJECTIVE_COEFFICIENT", newflux);
					
					reaction.addPathway(path);
					path.addReaction(reaction);
				}
			}
		}

		/*
		System.err.println(this.bioNetwork.getBiochemicalReactionList().size()+" reaction added to network");
		System.err.println(this.bioNetwork.getPhysicalEntityList().size()+" metabolites added");
		System.exit(0);*/
	}

	private void getNetWorkName() {
		String[] data= null;
		String Name;
		try {
			data=this.Webservice.path("info").path(this.keggOrgId).get(String.class).split("\\n");
			Name=data[0].split("\\s{2,}")[1];
		}catch(UniformInterfaceException e){
			Name="undefinied";
		}


		this.bioNetwork.setName(Name);

	}

	/**
	 * Retrieve data for each compound from the Kegg database and add the other attributes by using default values
	 * from the sbml specifications (2.4)
	 * Note that those compounds can come from Kegg Compound Database or from Kegg Glycan Database
	 * @param ent
	 */
	private void getCompoundData(BioPhysicalEntity ent){
		ent.setBoundaryCondition(false);
		ent.setConstant(false);
		ent.setHasOnlySubstanceUnit(false);

		HashMap<String, ArrayList<String>> Data=this.getEntityDataHasHash(ent.getId());

		if(Data.get("NAME")!=null){ //kegg glycans entries do not always have names or chemical formulas
			ent.setName( Data.get("NAME").get(0));
		}
		if(Data.get("FORMULA") != null){
			ent.setChemicalFormula(Data.get("FORMULA").get(0));
		}
		if(Data.get("MASS")!=null){
			ent.setMolecularWeight(Data.get("MASS").get(0));
		}else if(Data.get("MOL_WEIGHT")!= null){
			ent.setMolecularWeight(Data.get("MOL_WEIGHT").get(0));
		}

		ent.addRef(new BioRef("Import", "KEGG", ent.getId(), 1));

		if (Data.get("DBLINKS")!=null){
			for (String links: Data.get("DBLINKS")){
				String[] tab=links.split(": ");
				ent.addRef(new BioRef("Import", tab[0], tab[1], 1));
			}
		}
	}


	/**
	 * Retrieve Reaction data from the Kegg database and add the other attributes by using default values
	 * from the sbml specifications (2.4)
	 * @param rxn
	 */
	private void getReactionData(BioChemicalReaction rxn) {

		HashMap<String, ArrayList<String>> Data=this.getEntityDataHasHash(rxn.getId());

		if(Data.get("NAME")!=null){
			rxn.setName( Data.get("NAME").get(0));
		}
		if(Data.get("COMMENT")!=null){
			rxn.setComment( Data.get("COMMENT").get(0));
		}
		if(Data.get("ENZYME")!=null){
			for(String ecNum:Data.get("ENZYME")){
				if(this.linkECGene.containsKey(ecNum)){
					if (rxn.getEcNumber().isEmpty() || rxn.getEcNumber().equalsIgnoreCase(" ")){
						rxn.setEcNumber(ecNum);
					}
					else{
						rxn.setEcNumber(rxn.getEcNumber()+" / "+ecNum);
					}
				}
			}
		}

	}

	

	private void refactorNetworkIDS() {
		for (BioPhysicalEntity ent: this.bioNetwork.getPhysicalEntityList().values()){
			this.simplifyId(ent);
		}
		for (BioProtein prot: this.bioNetwork.getProteinList().values()){
			this.simplifyId(prot);
		}
		for (BioComplex cplx: this.bioNetwork.getComplexList().values()){
			this.simplifyId(cplx);
		}
		for (BioChemicalReaction rxn :this.bioNetwork.getBiochemicalReactionList().values()){
			this.simplifyId(rxn);
		}
	}

	private void simplifyId(BioEntity ent) {
		String[] tempArray=ent.getId().split("[: ]");
		ent.setId(tempArray[1]);
	}

	
	private void simplifyId(BioProtein prot) {
		String newId=prot.getId().replaceAll("[: ]", "_");
		prot.setId(newId);
	}
	
	private void simplifyId(BioComplex cplx) {
		String newId=cplx.getId().replaceAll("[: ]", "_");
		cplx.setId(newId);
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

		String[] Data=this.Webservice.path("get").path(id).get(String.class).split("\\n");

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

		String[] Data=this.Webservice.path("list").path("genome").get(String.class).split("\\n");

		for(String genome: Data){
			String[] tab=genome.split("\\t");
			if (tab[1].split(",")[0].equalsIgnoreCase(keggId)){
				return true;
			}
		}		
		return false;

	}

	public BioNetwork getBioNetwork() {
		return bioNetwork;
	}


	public String getKeggOrgId() {
		return keggOrgId;
	}


	public void setBioNetwork(BioNetwork bioNetwork) {
		this.bioNetwork = bioNetwork;
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

	public WebResource getWebservice() {
		return Webservice;
	}

	public void setConfig(ClientConfig config) {
		this.config = config;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setWebservice(WebResource webservice) {
		Webservice = webservice;
	}

	public HashMap<String,String> getLinkECGene() {
		return linkECGene;
	}

	public void setLinkECGene(String id) {
		String[] Data=this.Webservice.path("link").path("ec").path(id).get(String.class).split("\\n");

		for (String line: Data){
			String[] tmp=line.split("\t");
			linkECGene.put(tmp[1].substring(3, tmp[1].length()) , tmp[0]);
		}

	}

}