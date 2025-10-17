package fr.inrae.toulouse.metexplore.met4j_chemUtils;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * A class to compute molecular weights from chemical formulas using CDK, and set them in BioMetabolites
 *
 * @author clement
 * @version $Id: $Id
 */
public class MassComputor {

    private int type = MolecularFormulaManipulator.MolWeight;
    private boolean warn = true;
    private boolean onlyIfMissing = false;

    /**
     * Set the molecular weights of all metabolites in a BioNetwork from their chemical formulas
     * @param bn a BioNetwork
     */
    public void setMolecularWeights(BioNetwork bn){
        for(BioMetabolite m : bn.getMetabolitesView()){
            if(onlyIfMissing && m.getMolecularWeight()!=null) continue;
            setMolecularWeight(m);
        }
    }

    /**
     * Set the molecular weight of a BioMetabolite from its chemical formula
     * @param m a BioMetabolite
     */
    public void setMolecularWeight(BioMetabolite m){
        String sformula = m.getChemicalFormula();
        if(StringUtils.isVoid(sformula)){
            if(warn)System.err.println("[MassComputor] Warning: metabolite "+m.getId()+" has no formula, cannot compute molecular weight");
            return;
        }
        if(!StringUtils.checkMetaboliteFormula(sformula)){
            if(warn)System.err.println("[MassComputor] Warning: metabolite "+m.getId()+" has an invalid formula ("+sformula+"), cannot compute molecular weight");
            return;
        }
        try {
            Double mw = computeMolecularWeight(sformula);
            if(mw!=null){
                m.setMolecularWeight(mw);
            }else{
                if(warn)System.err.println("[MassComputor] Warning: metabolite "+m.getId()+" unable to compute molecular weight from formula "+sformula);
            }
        } catch (Exception e) {
            if(warn)System.err.println("[MassComputor] Warning: metabolite "+m.getId()+" unable to compute molecular weight from formula "+sformula);
        }
    }

    /**
     * Compute the molecular weight of a given chemical formula
     * @param sformula the formula as a string
     * @return the molecular weight as a Double (null if the formula is invalid)
     */
    public Double computeMolecularWeight(String sformula){
        IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(sformula, DefaultChemObjectBuilder.getInstance());
        return MolecularFormulaManipulator.getMass(formula, type);
    }

    /**
     * Use the average mass of the elements
     */
    public MassComputor useAverageMass(){
        this.type = MolecularFormulaManipulator.MolWeight;
        return this;
    }

    /**
     * Use the mass of the most common isotopes, not taking into account their abundance
     */
    public MassComputor useMonoIsotopicMass(){
        this.type = MolecularFormulaManipulator.MonoIsotopic;
        return this;
    }

    /**
     * Use the most abundant distribution. For example C6Br6 would have three Br 79 and three Br 81 because their abundance is 51%/49%.
     */
    public MassComputor useMostAbundant(){
        this.type = MolecularFormulaManipulator.MostAbundant;
        return this;
    }

    /**
     * Set whether to print warnings when a formula is missing or invalid
     * @param warn true to print warnings, false to be silent
     * @return the current MassComputor
     */
    public MassComputor setWarn(boolean warn) {
        this.warn = warn;
        return this;
    }

    /**
     * Set whether to compute molecular weights only if missing (true) or always (false, default)
     * @param onlyIfMissing true to compute molecular weights only if missing, false to always compute them
     * @return the current MassComputor
     */    public MassComputor setOnlyIfMissing(boolean onlyIfMissing) {
        this.onlyIfMissing = onlyIfMissing;
        return this;
    }

}
