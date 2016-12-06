/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.io;

import java.util.HashMap;
import java.util.regex.Pattern;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

public class MetaboliteFileToBioNetwork extends AnnotationFile2BioNetwork {

	
	public String txtSep;

	private String compartStart="(";
	private String compartEnd=")";
	private String compartSep=",";
	
	private String defCompId="x";

	public static void main(String[] args){

		String file="/home/bmerlet/Téléchargements/TrypanocycMetabolites.csv";

		MetaboliteFileToBioNetwork mtbfile2bn=new MetaboliteFileToBioNetwork("annot",file,"","'",false,"[","]",",","x");
		
		mtbfile2bn.convertFile();		
		
		mtbfile2bn.getBioNetwork().printBioNetworkSizeToErr();
	}
	
	
	
	public MetaboliteFileToBioNetwork(String netId, String file, String Flag,String txtsep, boolean palsson,String compSt,
			String compEd,String compsep,String defcomp) {
		
		super(netId, file, Flag, "", "", palsson);
		this.setTxtSep(txtsep);
		this.setCompartStart(compSt);
		this.setCompartEnd(compEd);
		this.setCompartSep(compsep);
		this.setDefCompId(defcomp);
		
		
		if(this.getBioNetwork().getCompartments().containsKey("NA")){
			System.err.println("added na has compartment");
			System.exit(1);
		}
		
	}

	@Override
	protected void convertLineto(String sCurrentLine, HashMap<Integer, String> keys) {

		String[] data=sCurrentLine.split("\t");

		HashMap<String, String> hashData= new HashMap<String, String>();

		for (int i=0, c=data.length; i<c; i++){
			hashData.put(keys.get(i), data[i]);
		}
		
		this.AddMetabolitesFromHash(hashData);
		
		if(this.getBioNetwork().getCompartments().containsKey("NIL")){
			System.err.println(sCurrentLine);
			System.exit(1);
		}

	}



	private void AddMetabolitesFromHash(HashMap<String, String> hashData) {

		if(hashData.containsKey("Compartments")){
			
			String lists=hashData.get("Compartments").replaceAll(this.txtSep, "");
			
			if(lists.startsWith(this.getCompartStart()) && lists.endsWith(this.getCompartEnd())){
					
				lists=lists.replaceAll(escapeSpecialRegexChars(this.getCompartStart()), "");
				lists=lists.replaceAll(escapeSpecialRegexChars(this.getCompartEnd()), "");
				lists=lists.replaceAll(" ", "");				
				
				String[] list=lists.split(escapeSpecialRegexChars(this.getCompartSep()));
							
				for(String id: list){
					
					this.createMetaboliteInCompartment(hashData, id);
				}
				
			}else{
				
				lists=lists.replaceAll(escapeSpecialRegexChars(this.getCompartStart()), "");
				lists=lists.replaceAll(escapeSpecialRegexChars(this.getCompartEnd()), "");
				lists=lists.replaceAll(" ", "");
								
				this.createMetaboliteInCompartment(hashData, lists);
			}	
			
		}else{		
			
			System.err.println(hashData);
			this.createMetaboliteInCompartment(hashData, this.getDefCompId());
			
		}

	}



	private void createMetaboliteInCompartment(HashMap<String, String> hashData, String compId) {
		
		if( StringUtils.isVoid(compId)){
			System.err.println(hashData.get("Identifier")+" in "+compId);
		}
		
		BioNetwork bn=this.getBioNetwork();
		
		BioCompartment cmp;
		if(!bn.getCompartments().containsKey(compId)){
			cmp=new BioCompartment(compId,compId);
			bn.addCompartment(cmp);
		}else{
			cmp=bn.getCompartments().get(compId);
		}
				
		String id;
		if(this.isUsePalssonId() && !hashData.get("Identifier").startsWith("M_")){
			id="M_"+hashData.get("Identifier").replaceAll(txtSep, "");
		}else{
			id=hashData.get("Identifier").replaceAll(txtSep, "");
		}
		
		if(!id.endsWith("_"+compId)){
			id+="_"+compId;
		}
		
		BioPhysicalEntity met=new BioPhysicalEntity(id);
		met.setCompartment(cmp);
		

		bn.addPhysicalEntity(met);

		if(hashData.containsKey("Name")){
			met.setName(hashData.get("Name").replaceAll(txtSep, ""));
		}
		
		if(hashData.containsKey("Chemical Formula")){
			met.setChemicalFormula(hashData.get("Chemical Formula").replaceAll(txtSep, ""));
		}
		
		if(hashData.containsKey("Molecular Weight")){
			met.setMolecularWeight(hashData.get("Molecular Weight").replaceAll(txtSep, ""));
		}
		
		if(hashData.containsKey("Inchi")){
			met.addRef("inchi", hashData.get("Inchi"), 1, "is", "User File");
		}
		
		if(hashData.containsKey("InchiKey")){
			met.addRef("inchikey", hashData.get("InchiKey"), 1, "is", "User File");
		}		

		if(hashData.containsKey("SMILES")){
			met.addRef("inchikey", hashData.get("SMILES"), 1, "is", "User File");
		}
		
			
		
		
	}



	public String getTxtSep() {
		return txtSep;
	}



	public String getCompartStart() {
		return compartStart;
	}



	public String getCompartEnd() {
		return compartEnd;
	}



	public String getCompartSep() {
		return compartSep;
	}



	public String getDefCompId() {
		return defCompId;
	}



	public void setDefCompId(String defCompId) {
		this.defCompId = defCompId;
	}



	public void setTxtSep(String txtSep) {
		this.txtSep = txtSep;
	}



	public void setCompartStart(String compartStart) {
		this.compartStart = compartStart;
	}



	public void setCompartEnd(String compartEnd) {
		this.compartEnd = compartEnd;
	}



	public void setCompartSep(String compartSep) {
		this.compartSep = compartSep;
	}
	

	Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");


	String escapeSpecialRegexChars(String str) {

		return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
	}


}
