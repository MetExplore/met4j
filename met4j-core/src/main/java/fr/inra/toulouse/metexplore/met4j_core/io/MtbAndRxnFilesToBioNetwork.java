/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.io;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;

public class MtbAndRxnFilesToBioNetwork {
	
	public String mtbFile;
	public BioNetwork mtbBn;
		
	public String rxnFile;
	public BioNetwork rxnBn;
	
	public BioNetwork finalBn;
	
	public MtbAndRxnFilesToBioNetwork(String mtbfile,String rxnfile){
		this.setMtbFile(mtbfile);
		this.setRxnFile(rxnfile);
	}

	private void createBioNetworks() {
		
		// Create mtb bn from file
		MetaboliteFileToBioNetwork mtbfile2bn=new MetaboliteFileToBioNetwork("MetaboliteBioNetwork",
				this.getMtbFile(),"","'",false,"[","]",",","x");
		mtbfile2bn.convertFile();
		
		this.setMtbBn(mtbfile2bn.getBioNetwork());
		
		// Create rxn bn from the file
		ReactionFile2BioNetwork rxnfile2bn=new ReactionFile2BioNetwork("ReactionBioNetwork",
				this.getRxnFile(),"","'","->","<->",false,";","[","]",true,"(",")",false,"x","[","]");
		rxnfile2bn.convertFile();
		
		this.setRxnBn(rxnfile2bn.getBioNetwork());
		
	}

//	private void mergeBioNetWorks() {
//		
//		BioNetworkUnion bnUbn=new BioNetworkUnion(3,"dbIdentifier", "dbIdentifier", false);
//		bnUbn.uniteNetworks( this.getMtbBn() ,this.getRxnBn());
//		
//		this.setFinalBn(bnUbn.getPrimaryBioNet());
//		
//	}

	public String getMtbFile() {
		return mtbFile;
	}

	public BioNetwork getMtbBn() {
		return mtbBn;
	}

	public String getRxnFile() {
		return rxnFile;
	}

	public BioNetwork getRxnBn() {
		return rxnBn;
	}

	public BioNetwork getFinalBn() {
		return finalBn;
	}

	public void setFinalBn(BioNetwork finalBn) {
		this.finalBn = finalBn;
	}

	public void setMtbFile(String mtbFile) {
		this.mtbFile = mtbFile;
	}

	public void setMtbBn(BioNetwork mtbBn) {
		this.mtbBn = mtbBn;
	}

	public void setRxnFile(String rxnFile) {
		this.rxnFile = rxnFile;
	}

	public void setRxnBn(BioNetwork rxnBn) {
		this.rxnBn = rxnBn;
	}
	

}
