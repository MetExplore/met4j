/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;

/**
 * This class handle the qual:sbml functionTerms of Qualitative transition. If defaultTerm is set to true, 
 * the formula representing the MathML nodes is ignored 
 * @author bmerlet
 *
 */
public class QualitativeFunction extends BioUtility {

	
	private Boolean defaultTerm;
	private int resultLevel;
	private String formula;
	
	
	public QualitativeFunction(){}
	
	public QualitativeFunction(boolean deflt, int result,String math){
		this.setDefaultTerm(deflt);
		this.setResultLevel(result);
		this.setFormula(math);
	}
	
	public QualitativeFunction(int result,String math){
		this.setDefaultTerm(false);
		this.setResultLevel(result);
		this.setFormula(math);
	}
	
	public QualitativeFunction(int result){
		this.setDefaultTerm(true);
		this.setResultLevel(result);
	}

	public Boolean getDefaultTerm() {
		return defaultTerm;
	}

	public int getResultLevel() {
		return resultLevel;
	}

	public String getFormula() {
		return formula;
	}

	public void setDefaultTerm(Boolean defaultTerm) {
		this.defaultTerm = defaultTerm;
	}

	public void setResultLevel(int resultLevel) {
		this.resultLevel = resultLevel;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}
	
}
