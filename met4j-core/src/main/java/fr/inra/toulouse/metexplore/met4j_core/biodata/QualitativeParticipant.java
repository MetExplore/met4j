/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;
/**
 * This class is a representation of the inputs and outputs of a qualitative transition. As such, 
 * the BioPhysicalEntity participating in this transition must be of type QualitativeSpecie.
 * 
 * 		If this QualitativeSpecie is an input, the stoichiometricCoefficient represent it's threshold
 * 		If it is an output, the stoichiometricCoefficient represent the outputLevel of this species
 * 
 * @author bmerlet
 *
 */
public class QualitativeParticipant extends BioPhysicalEntityParticipant {
	
	private String sign;
	private String transitionEffect;

	
	/*
	 * Constructor
	 * */
	public QualitativeParticipant(String id, BioPhysicalEntity phyEntity, String stoichio, BioCompartment location) {
		super(id, phyEntity, stoichio, location);
		this.setName(id);
		this.setTransitionEffect("none");//default transitionEffect for inputs in regulatory networks
	}

	
	public QualitativeParticipant(BioPhysicalEntity phyEntity, String sto) {
		super(phyEntity, sto);
		this.setName(getId());
		this.setTransitionEffect("none");//default transitionEffect for inputs in regulatory networks
	}

	public QualitativeParticipant(BioPhysicalEntityParticipant in) {
		super(in);
		this.setName(getId());
		this.setTransitionEffect("none");//default transitionEffect for inputs in regulatory networks
	}

	/*
	* getter and setter
	*/	
	public String getSign() {
		return sign;
	}


	public String getTransitionEffect() {
		return transitionEffect;
	}



	public void setSign(String sign) {
		this.sign = sign;
	}


	public void setTransitionEffect(String transitionEffect) {
		this.transitionEffect = transitionEffect;
	}

}
