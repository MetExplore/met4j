/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.io;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.Flux;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

public class ReactionFile2BioNetwork extends AnnotationFile2BioNetwork {

	public String txtSep;

	public String pathSep;
	public String pLStart;
	public String pLEnd;

	public boolean logicalGPR=true;

	private String gprStart="(";
	private String gprEnd=")";

	public boolean noCompartInEquation=true;

	public String CompartStart;
	public String CompartEnd;


	public ReactionFile2BioNetwork(String netId, String file, String Flag, String textsep, String irrRxn, String revRxn, boolean palsson,
			String pSep,String pListSt,String pListEnd, boolean logic, String GprSt, String GprEnd, boolean noComp, String defCompId, String stComp, String endComp) {

		super(netId, file, Flag, irrRxn, revRxn, palsson);
		this.setpLStart(pListSt);
		this.setpLEnd(pListEnd);
		this.setPathSep(pSep);
		this.setLogicalGPR(logic);
		this.setGprStart(GprSt);
		this.setGprEnd(GprEnd);

		this.setNoCompartInEquation(noComp);
		this.defaultCompartmentId=defCompId;
		this.setCompartStart(stComp);
		this.setCompartEnd(endComp);

		this.txtSep=textsep;

		this.CompartStart=escapeSpecialRegexChars(this.CompartStart);
		this.CompartEnd=escapeSpecialRegexChars(this.CompartEnd);

	}

	@Override
	protected void convertLineto(String sCurrentLine, HashMap<Integer, String> keys) {

		String[] data=sCurrentLine.split("\t");

		HashMap<String, String> hashData= new HashMap<String, String>();

		for (int i=0, c=data.length; i<c; i++){
			hashData.put(keys.get(i), data[i]);
		}

		this.AddReactionsFromHash(hashData);

	}

	private void AddReactionsFromHash(HashMap<String, String> hashData) {

		BioNetwork bn=this.getBioNetwork();

		BioChemicalReaction rxn=new BioChemicalReaction();

		/*
		 * if the identifier has to be in Palsson's format
		 */
		if(this.isUsePalssonId() && !hashData.get("Identifier").startsWith("R_")){
			rxn.setId("R_"+hashData.get("Identifier").replaceAll(txtSep, ""));
		}else{
			rxn.setId(hashData.get("Identifier").replaceAll(txtSep, ""));
		}

		bn.addBiochemicalReaction(rxn);

		if(hashData.containsKey("Name")){
			rxn.setName(hashData.get("Name").replaceAll(txtSep, ""));
		}

		if(hashData.containsKey("EC Number")){
			rxn.setEcNumber(hashData.get("EC Number").replaceAll(txtSep, ""));
		}

		if(hashData.containsKey("Reaction Status")){
			rxn.setStatus(hashData.get("Reaction Status").replaceAll(txtSep, ""));
		}

		if(hashData.containsKey("Comment")){
			rxn.setComment(hashData.get("Comment").replaceAll(txtSep, ""));
		}

		/*
		 *if the data contains pathway information 
		 */
		if(hashData.containsKey("Pathway List")){
			String pth=hashData.get("Pathway List");

			if (pth.startsWith(pLStart) && pth.endsWith(pLEnd)){
				String[] pthlist=pth.substring(pLStart.length(),pth.length()-pLEnd.length()).split(pathSep);

				for (String name:pthlist){
					BioPathway path;
					if (bn.getPathwayList().containsKey(name)){
						path=bn.getPathwayList().get(name);
					}else{
						path=new BioPathway(name.replaceAll(txtSep, ""));
						bn.addPathway(path);
					}
					rxn.addPathway(path);
				}

			}else{
				BioPathway path;
				if (bn.getPathwayList().containsKey(pth)){
					path=bn.getPathwayList().get(pth);
				}else{
					path=new BioPathway(pth.replaceAll(txtSep, ""));
					bn.addPathway(path);
				}
				rxn.addPathway(path);
			}
		}

		/*
		 * If the data contains formula information
		 */
		if (hashData.containsKey("Reaction Equation")){

			this.ParseReactionFormula(rxn,hashData.get("Reaction Equation"));

		}
		/*
		 * Do GPRs like in normal import 
		 */
		if (hashData.containsKey("GPR") && !hashData.get("GPR").equalsIgnoreCase("''") 
				&& !hashData.get("GPR").equalsIgnoreCase("\"\"")){
			this.parseGPR(rxn,hashData.get("GPR"));
		}

		/*
		 * If the data got fluxes bounds information
		 */
		if(hashData.containsKey("Flux lower Bound")){
			rxn.setLowerBound(new Flux(hashData.get("Flux lower Bound"),null));
		}
		if(hashData.containsKey("Flux upper Bound")){
			rxn.setUpperBound(new Flux(hashData.get("Flux upper Bound"),null));
		}
		if(hashData.containsKey("Biblio")){
			String[] biblios=hashData.get("Biblio").split(",");

			for (String biblio: biblios){
				if (biblio.startsWith("PMID:")){
					biblio=biblio.replaceFirst("PMID:", "");
				}
				rxn.addPmid(biblio);
			}
		}

	}

	/**
	 * Instantiate metabolites from the reaction's formula.
	 * @param BioChemicalReaction rxn
	 * @param String formula
	 */
	private void ParseReactionFormula(BioChemicalReaction rxn,	String formula) {

		String[] reactant = null;

		if	(formula.contains(this.revReaction)){
			rxn.setReversibility(true);

			reactant=	formula.split(this.revReaction)	;

		}else if(formula.contains(this.irrReaction)){

			rxn.setReversibility(false);
			reactant=	formula.split(this.irrReaction)	;

		}else{

			System.err.println("Reaction sign not understood");
			System.err.println(formula);
		}


		if(reactant!=null){
			for (String substrate:reactant[0].split(" \\+ ")){
	
				BioPhysicalEntityParticipant lpart=this.IntstantiateRxnReactant(substrate);
	
				rxn.addLeftParticipant(lpart);
	
			}
	
			for (String product:reactant[1].split(" \\+ ")){
	
				BioPhysicalEntityParticipant rpart=this.IntstantiateRxnReactant(product);
	
				rxn.addRightParticipant(rpart);
	
			}
		}

	}

	/**
	 * Creates the reaction's left and right participants from the splitted reaction formula
	 * @param ReactantFromFormula
	 * @return
	 */
	private BioPhysicalEntityParticipant IntstantiateRxnReactant(String ReactantFromFormula){

		ReactantFromFormula=ReactantFromFormula.trim();

		String coeff="1"; 
		Matcher m;
		m = Pattern.compile("^(\\d*)\\s+(\\S*)").matcher(ReactantFromFormula);
		if (m.matches()){
			coeff=m.group(1);
			ReactantFromFormula=m.group(2).trim();
		}


		String compartId="";
		if(this.hasNoCompartInEquation()){
			compartId=this.defaultCompartmentId;

		}else{
			
			String[] tmpArray=ReactantFromFormula.split("[("+this.CompartStart+")("+this.CompartEnd+")]");
			ReactantFromFormula=tmpArray[0];
			compartId=tmpArray[1];

		}


		if(this.isUsePalssonId() && !ReactantFromFormula.startsWith("R_")){
			ReactantFromFormula="M_"+ReactantFromFormula+"_"+compartId;
		}

		BioPhysicalEntity Metabolite;

		if(this.getBioNetwork().getPhysicalEntityList().containsKey(ReactantFromFormula.replaceAll(txtSep, ""))){
			Metabolite=this.getBioNetwork().getPhysicalEntityList().get(ReactantFromFormula.replaceAll(txtSep, ""));
		}else{

			Metabolite=new BioPhysicalEntity(ReactantFromFormula.replaceAll(txtSep, ""));

			if(this.getBioNetwork().getCompartments().containsKey(compartId)){
				Metabolite.setCompartment(this.getBioNetwork().getCompartments().get(compartId));
			}else{
				BioCompartment cmpt=new BioCompartment(compartId,compartId);
				this.getBioNetwork().addCompartment(cmpt);
				Metabolite.setCompartment(cmpt);
			}

			this.getBioNetwork().addPhysicalEntity(Metabolite);
		}

		BioPhysicalEntityParticipant part=new BioPhysicalEntityParticipant(Metabolite);
		part.setStoichiometricCoefficient(coeff);
		part.setIsConstant(false);


		return part;
	}





	private void parseGPR(BioChemicalReaction rxn, String GPRString) {


		if (this.isLogicalGPR()){
			
			GPRString=GPRString.toLowerCase().replaceAll("[\"']", "");
			
			String start=escapeSpecialRegexChars(this.gprStart);
			String end=escapeSpecialRegexChars(this.gprEnd);
			
			String[] enzymesGroup=GPRString.split(end+"\\s?or\\s?"+start);
			BioCompartment fkCompart;
			
			if(this.getBioNetwork().getCompartments().containsKey("fake_compartment")){
				fkCompart=this.getBioNetwork().getCompartments().get("fake_compartment");
			}else{
				fkCompart=new BioCompartment();
				fkCompart.setAsFakeCompartment();
				this.getBioNetwork().addCompartment(fkCompart);
			}
			
			

			for( String enzyme :enzymesGroup){

				enzyme=enzyme.replaceAll("["+start+end+"]", "");
				
				if(enzyme.isEmpty()){
					continue;
				}
				
				String[] enzCompo=enzyme.split(" and ");

				String enzymeId = StringUtils.implode(enzCompo, "_and_").replaceAll("\\.", "_");

				BioComplex enz;

				if (!this.getBioNetwork().getComplexList().containsKey(enzymeId)) {
					enz = new BioComplex(enzymeId, enzymeId);

					enz.setCompartment(fkCompart);

					this.getBioNetwork().addComplex(enz);
				}

				enz = this.getBioNetwork().getComplexList().get(enzymeId);

				rxn.addEnz(enz);
				this.getBioNetwork().getEnzList().put(enzymeId, enz);

				for (String component:enzCompo){

					BioGene gene;
					if(!this.getBioNetwork().getGeneList().containsKey(component)){
						gene=new BioGene(component);
						this.getBioNetwork().addGene(gene);
					}
					gene=this.getBioNetwork().getGeneList().get(component);


					String protId="_"+component.replaceAll("\\.", "_");

					BioProtein protein;
					if(!this.getBioNetwork().getProteinList().containsKey(protId)){
						protein=new BioProtein(protId);
						protein.setBoundaryCondition(true);
						protein.setCompartment(fkCompart);
						protein.addGene(gene);
						this.getBioNetwork().addProtein(protein);
					}
					protein=this.getBioNetwork().getProteinList().get(protId);

					BioPhysicalEntityParticipant part=new BioPhysicalEntityParticipant(protein);	

					enz.addComponent(part);

				}

			}
		}

	}


	public String getPathSep() {
		return pathSep;
	}

	public boolean isLogicalGPR() {
		return logicalGPR;
	}

	public String getGprStart() {
		return gprStart;
	}


	public String getGprEnd() {
		return gprEnd;
	}


	public void setGprStart(String gprStart) {
		this.gprStart = gprStart;
	}


	public void setGprEnd(String gprEnd) {
		this.gprEnd = gprEnd;
	}


	public String getpLStart() {
		return pLStart;
	}

	public String getpLEnd() {
		return pLEnd;
	}

	public String getCompartStart() {
		return CompartStart;
	}

	public String getCompartEnd() {
		return CompartEnd;
	}

	public boolean hasNoCompartInEquation() {
		return noCompartInEquation;
	}


	public void setNoCompartInEquation(boolean noCompartInEquation) {
		this.noCompartInEquation = noCompartInEquation;
	}


	public void setPathSep(String pathSep) {
		this.pathSep = pathSep;
	}

	public void setLogicalGPR(boolean boolGPR) {
		this.logicalGPR = boolGPR;
	}

	public void setpLStart(String pLStart) {
		this.pLStart = pLStart;
	}

	public void setpLEnd(String pLEnd) {
		this.pLEnd = pLEnd;
	}

	public void setCompartStart(String compartStart) {
		CompartStart = compartStart;
	}

	public void setCompartEnd(String compartEnd) {
		CompartEnd = compartEnd;
	}



	Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");


	String escapeSpecialRegexChars(String str) {

		return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
	}


}
