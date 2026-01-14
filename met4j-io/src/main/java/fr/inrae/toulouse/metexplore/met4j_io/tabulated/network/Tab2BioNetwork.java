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


package fr.inrae.toulouse.metexplore.met4j_io.tabulated.network;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>Tab2BioNetwork class.</p>
 *
 * @author lcottret
 */
public class Tab2BioNetwork {

    @Setter
    public int colId = 0;
    @Setter
    public int colFormula = 1;
    @Setter
    public String networkId = "TabConvertedNetwork";
    @Setter
    public String irrReaction = "-->";
    @Setter
    public String revReaction = "<==>";
    @Setter
    public String defaultCompartmentId = "c";
    @Setter
    public int nSkip = 0;
    @Setter
    public errorHandling parsingFailure = errorHandling.THROWERROR;

    //match reactant formula parts, with or without coeff and location, like: "2.5 glc__D[c]" and "g6p"
    //will extract if present: coeff (group 1), metabolite id (group 2), compartment id (group 4), giving 2.5, glc__D, c
    //use default values if coeff and location not present, metabolite id is mandatory
    protected Pattern reactantRegex = Pattern.compile("^(\\d+\\.?\\d*)?\\s*(\\w+)(\\[(\\S+)\\])?$");
    protected int regexCoeffIndex = 1;
    protected int regexMetabIndex = 2;
    protected int regexCompIndex = 4;

    private BioCompartment defaultCompartment;

    public enum errorHandling{
        THROWERROR,
        SKIP
    }


    /**
     * Define how to extract reactant information (compound id, stoichiometric coefficient and compartment) from reaction formula using regex groups.
     *
     * @param regex        the regex as {@link String}
     * @param coeffIndex  the regex group index for coefficient (set to 1 if no coefficient in formula)
     * @param metabIndex  the regex group index for metabolite id
     * @param compIndex   the regex group index for compartment id (set to default compartment if no compartment in formula)
     */
    public void setReactantParsing(String regex, int coeffIndex, int metabIndex, int compIndex){
        this.reactantRegex = Pattern.compile(regex);
        this.regexCoeffIndex = coeffIndex;
        this.regexMetabIndex = metabIndex;
        this.regexCompIndex = compIndex;
    }

    /**
     * <p>Constructor for Tab2BioNetwork.</p>
     *
     * @param networkId                         a {@link String} object.
     * @param colId                              a int.
     * @param colFormula                         a int.
     * @param irrReaction                        a {@link String} object.
     * @param revReaction                        a {@link String} object.
     * @param defaultCompartment                 a {@link String} object.
     * @param nSkip                              a int.
     */
    public Tab2BioNetwork(String networkId, int colId, int colFormula, String irrReaction, String revReaction, BioCompartment defaultCompartment, int nSkip) {
        this.networkId=networkId;
        this.colId = colId;
        this.colFormula = colFormula;
        this.irrReaction = irrReaction;
        this.revReaction = revReaction;
        if(defaultCompartment!=null){
            this.defaultCompartment = defaultCompartment;
        }else{
            this.defaultCompartment = new BioCompartment(this.defaultCompartmentId, this.defaultCompartmentId);
        }
        this.nSkip = nSkip;
    }

    //Initialize the default compartment if not set
    private void initDefaultCompartment(){
        if(this.defaultCompartment==null){
            this.defaultCompartment = new BioCompartment(this.defaultCompartmentId, this.defaultCompartmentId);
        }
    }

    //Read the tabulated file
    private Map<String,String> readFile(Reader fr){
        Map<String,String> res = new HashMap<>(); //a map with reaction id as key, reaction formula as value
        BufferedReader br = new BufferedReader(fr);
        String line;
        int nLines = 0;
        try {
            while ((line = br.readLine()) != null) {
                nLines++;
                if (nLines > this.nSkip && !line.startsWith("#")) {
                    String[] tab = line.split("\\t");
                    //check number of columns
                    if(tab.length<=Math.max(this.colId,this.colFormula)){
                        System.err.println("[Warning] line "+nLines+": wrong number of columns.");
                        if(this.parsingFailure==errorHandling.THROWERROR) throw new IllegalArgumentException("[Error] parsing error.");
                        continue;
                    }
                    String id = tab[this.colId].trim();
                    String formula = tab[this.colFormula];
                    //check duplicated id
                    if(res.containsKey(id)){
                        System.err.println("[Warning] Duplicated reaction id "+id+" found at line "+nLines+".");
                        if(this.parsingFailure==errorHandling.THROWERROR) throw new IllegalArgumentException("[Error] parsing error.");
                        continue;
                    }
                    //check formula format
                    if(checkFormula(formula)){
                        res.put(id,formula);
                    }else{
                        System.err.print("[Warning] line "+nLines+": wrong format.");
                        if(this.parsingFailure==errorHandling.THROWERROR) throw new IllegalArgumentException("[Error] parsing error.");
                        continue;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.println("[Error] Error while reading the reaction file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.err.println("[Info] "+res.size()+" reactions read from file.");
        return res;
    }

    /**
     * Check reaction formula format
     * @param formula a {@link String} object.
     * @return a boolean: true if formula is valid, false otherwise
     */
    public boolean checkFormula(String formula){
        //check contains reaction arrow
        if(!formula.contains(this.irrReaction) && !formula.contains(this.revReaction)){
            System.err.println("Error in reaction formula, no side separator ("+this.irrReaction+" or "+this.revReaction+") : "+formula);
            return false;
        }
        //check contains no multiple reaction arrow
        String[] parts = formula.split(this.irrReaction);
        if(parts.length!=2){
            parts = formula.split(this.revReaction);
        }
        if(parts.length>2){
            System.err.println("Error while parsing reaction formula, more than one side separator: "+formula);
            return false;
        }
        //check contains at least one reactant on each side
        if(parts.length<2 || parts[0].trim().isEmpty() && parts[1].trim().isEmpty()){
            System.err.println("Error while parsing reaction formula, both side are empty: "+formula);
            return false;
        }
        parts = String.join(" + ", parts).split("\\s*\\+\\s*");
        //check reactant format
        for(String part : parts){
            if(!part.trim().isEmpty()){
                Matcher m = this.reactantRegex.matcher(part.trim());
                if(!m.matches()){
                    System.err.println("Error while parsing reaction formula, one or more reactant is malformed: "+formula);
                    return false;
                }
            }
        }
        //All good, proceed;
        return true;
    }

    /**
     * Convert a tabulated file to a BioNetwork
     * @param tabfile a {@link Reader} object.
     * @return a {@link BioNetwork} object.
     */
    public BioNetwork convert(Reader tabfile){
        return convert(readFile(tabfile));
    }

    /**
     * Convert a collection of reaction formulas with unique identifiers to a BioNetwork
     * @param reactionFormulas a {@link java.util.Map} object, reaction ids as key, reaction formulas as values.
     * @return a {@link BioNetwork} object.
     */
    public BioNetwork convert(Map<String,String> reactionFormulas){
        BioNetwork bn = new BioNetwork(this.networkId);
        for(Map.Entry<String,String> rxnEntry : reactionFormulas.entrySet()){
            String rxnId = rxnEntry.getKey();
            //check empty id
            if(StringUtils.isVoid(rxnId)){
                System.err.println("[Warning] Reaction with empty id skipped.");
                if(this.parsingFailure==errorHandling.THROWERROR) throw new IllegalArgumentException("[Error] parsing error.");
                continue;
            }
            //create reaction
            BioReaction rxn = new BioReaction(rxnId);
            String rxnFormula = rxnEntry.getValue();
            //set reversibility
            if(rxnEntry.getValue().contains(revReaction)){
                rxn.setReversible(true);
                rxnFormula=rxnFormula.replace(revReaction,irrReaction);
            }else{
                rxn.setReversible(false);
            }
            //get reactant info
            Collection<FormulaPart> parsedFormula = parseRxnFormula(rxnFormula);
            if(parsedFormula.isEmpty()){
                if(this.parsingFailure==errorHandling.THROWERROR) throw new IllegalArgumentException("[Error] parsing error.");
                System.err.println("[Warning] Reaction "+rxn.getId()+" skipped due to parsing error.");
                continue;
            }

            bn.add(rxn);
            //create and affect metabolites and compartments from reactant info
            for(FormulaPart parsedReactant: parsedFormula){
                BioMetabolite metab = bn.getMetabolite(parsedReactant.metabId);
                BioCompartment comp = bn.getCompartment(parsedReactant.compId);
                //create compartment if not exists
                if(comp==null) {
                    if (parsedReactant.compId.equals(defaultCompartmentId)) {
                        if(this.defaultCompartment==null) initDefaultCompartment();
                        comp = this.defaultCompartment;
                    } else {
                        comp = new BioCompartment(parsedReactant.compId, parsedReactant.compId);
                    }
                    bn.add(comp);
                }
                //create metabolite if not exists
                if(metab==null){
                    metab = new BioMetabolite(parsedReactant.metabId,parsedReactant.compId);
                    bn.add(metab);
                }
                //affect metabolite to compartment if not already done
                if(!comp.getComponentsView().contains(metab)){
                    bn.affectToCompartment(comp,metab);
                }
                //affect metabolites to reaction
                if(parsedReactant.left){
                    bn.affectLeft(rxn,parsedReactant.coeff,comp,metab);
                }else{
                    bn.affectRight(rxn,parsedReactant.coeff,comp,metab);
                }
            }
        }
        return bn;
    }

    //Parse reaction formula using regex
    private Collection<FormulaPart> parseRxnFormula(String formula){
        Boolean flag = false; //track if regex matched
        String[] splitFormula = formula.trim().split(irrReaction);
        String[] left = new String[0];
        String[] right = new String[0];
        //define reaction sides. Should be 2 parts (left and right) except for exchange reactions
        if(splitFormula.length==0){
            System.err.println("[Warning] Error reacton is empty: "+formula);
            return new ArrayList<>(); //return empty list on error
        }
        if(splitFormula.length==1){//exchange reaction case
            if(formula.matches("^\\s*"+irrReaction+"\\s*.+$")){//import: empty left
                right = splitFormula[0].split("\\s*\\+\\s*");
            }else if(formula.matches("^.+\\s*"+irrReaction+"\\s*$")){//export: empty right
                left = splitFormula[0].split("\\s*\\+\\s*");
            }else{
                if(!checkFormula(formula)) System.err.print("Error while parsing reaction formula: "+formula);
                return new ArrayList<>(); //return empty list on error
            }
        }else if(StringUtils.isVoid(splitFormula[0])){
            right = splitFormula[1].split("\\s*\\+\\s*");
        }else if(StringUtils.isVoid(splitFormula[1])){
            left = splitFormula[0].split("\\s*\\+\\s*");
        }else{
            left = splitFormula[0].split("\\s*\\+\\s*");
            right = splitFormula[1].split("\\s*\\+\\s*");
        }

        //define reactants
        List<FormulaPart> res = new ArrayList<>();
        ArrayList<String> formulaParts = new ArrayList<String>(List.of(left));
        formulaParts.addAll(List.of(right));
        int i = -1;
        for(String subString : formulaParts){
            i++;
            Matcher m = this.reactantRegex.matcher(subString.trim());
            if(m.matches()){
                //get coefficient
                String coeffStr = m.group(regexCoeffIndex);
                double coeff;
                if (coeffStr == null || coeffStr.isEmpty()) {
                    coeff = 1.0; //default coefficient
                } else {
                    try {
                        coeff = Double.parseDouble(coeffStr);
                    } catch (NumberFormatException e) {
                        System.err.println("[Warning] Error while parsing coefficients in reaction formula, will be set to 1: " + subString);
                        coeff = 1.0; //default coefficient if not a valid number
                    }
                }
                //get compartment
                String compId = m.group(regexCompIndex);
                if(compId==null || compId.isEmpty()){
                    compId = this.defaultCompartmentId; //default compartment
                    System.err.println("[Warning] No compartment define in formula, will be set to "+defaultCompartmentId+": " + subString);
                }
                res.add(new FormulaPart(m.group(regexMetabIndex),coeff,i<left.length,compId));
            }else{
                flag=true;//something went wrong
                break;
            }
        }
        if(flag){
            if(!checkFormula(formula)) System.err.print("Error while parsing reaction formula: "+formula);
            res=new ArrayList<>(); //return empty list on error
        }
        return res;
    }

    //Helper record to store parsed reactant info
    private record FormulaPart(String metabId, Double coeff, Boolean left, String compId){};
}
