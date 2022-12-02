/*
 * Copyright INRAE (2022)
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
package fr.inrae.toulouse.metexplore.met4j_reconstruction.check.balance;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import lombok.Getter;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to store the result of a reaction balance checking
 */
public class ReactionBalanceAnalysis
{
    @Getter
    private HashMap<String, Double> balances;
    @Getter
    private final BioReaction reaction;
    @Getter
    private final BioCollection<BioMetabolite> metabolitesWithBadFormula;

    public ReactionBalanceAnalysis(BioReaction reaction) {
        this.reaction = reaction;

        this.metabolitesWithBadFormula = new BioCollection<>();

        this.computeBalances();
    }

    private void computeBalances() {

        HashMap<String, Double> tmpBalances = new HashMap<>();

        for(BioReactant left : reaction.getLeftReactantsView()) {
            boolean flag = this.countAtoms(tmpBalances, left, false);
            if(!flag) {
                metabolitesWithBadFormula.add(left.getMetabolite());
            }
        }
        for(BioReactant right : reaction.getRightReactantsView()) {
            boolean flag = this.countAtoms(tmpBalances, right, true);
            if(!flag) {
                metabolitesWithBadFormula.add(right.getMetabolite());
            }
        }

        this.balances = tmpBalances;
    }

    private boolean countAtoms(HashMap<String, Double> tmpBalances, BioReactant reactant, Boolean isRight) {

        Double sto = isRight ? reactant.getQuantity() : - reactant.getQuantity();

        String formula = reactant.getMetabolite().getChemicalFormula();

        if(! StringUtils.checkMetaboliteFormula(formula)) {
            return false;
        }

        String REGEX = "([A-Z][a-z]*)([0-9]*)";

        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(formula);

        while (matcher.find()) {

            String atom = matcher.group(1);

            String numStr = matcher.group(2);

            if (numStr.equals("")) {
                numStr = "1.0";
            }

            Double number = Double.parseDouble(numStr);

            if (!tmpBalances.containsKey(atom)) {
                tmpBalances.put(atom, sto * number);
            } else {
                tmpBalances.put(atom, tmpBalances.get(atom) + sto * number);
            }

        }

        return true;
    }

    /**
     * @return true if the reaction is balanced
     */
    public boolean isBalanced() {
        return metabolitesWithBadFormula.size() == 0 && balances.values().stream().reduce(0.0, Double::sum).equals(0.0);
    }

    /**
     * check if the reaction is an exchange reaction, ie :
     * - has an empty side
     * - or contains a metabolite that has boundary condition equals to true
     * @return a boolean
     */
    public boolean isExchange() {
        if(reaction.getLeftReactantsView().size() == 0 || reaction.getRightReactantsView().size() == 0)
        {
            return true;
        }
        return reaction.getMetabolitesView().stream().anyMatch(MetaboliteAttributes::getBoundaryCondition);

    }

}
