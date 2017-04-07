/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import fr.inra.toulouse.metexplore.met4j_core.io.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.io.Flux;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * A conversion interaction in which one or more entities (substrates) undergo
 * covalent changes to become one or more other entities (products). The
 * substrates of biochemical reactionNodes are defined in terms of sums of
 * species. This is what is typically done in biochemistry, and, in principle,
 * all of the EC reactionNodes should be biochemical reactionNodes.
 * 
 * Example: ATP + H2O = ADP + Pi.
 * 
 * In this reaction, ATP is considered to be an equilibrium mixture of several
 * species, namely ATP4-, HATP3-, H2ATP2-, MgATP2-, MgHATP-, and Mg2ATP.
 * Additional species may also need to be considered if other ions (e.g. Ca2+)
 * that bind ATP are present. Similar considerations apply to ADP and to
 * inorganic phosphate (Pi). When writing biochemical reactionNodes, it is
 * important not to attach charges to the biochemical reactants and not to
 * include ions such as H+ and Mg2+ in the equation. The reaction is written in
 * the direction specified by the EC nomenclature system, if applicable,
 * regardless of the physiological direction(s) in which the reaction proceeds.
 * (This definition from EcoCyc)
 * 
 * NOTE: Polymerization reactionNodes involving large polymers whose structure
 * is not explicitly captured should generally be represented as unbalanced
 * reactionNodes in which the monomer is consumed but the polymer remains
 * unchanged, e.g. glycogen + glucose = glycogen.
 */

public class BioReaction extends BioInteraction {

	private String deltaG = "";
	private String deltaH = "";
	private String deltaS = "";
	private String ecNumber = "";
	private String keq = "";
	private HashMap<String, BioCatalysis> enzrxnsList = new HashMap<String, BioCatalysis>();
	private HashMap<String, BioPhysicalEntity> enzList = new HashMap<String, BioPhysicalEntity>();
	private HashMap<String, BioPhysicalEntity> listOfSubstrates = new HashMap<String, BioPhysicalEntity>();
	private HashMap<String, BioPhysicalEntity> listOfProducts = new HashMap<String, BioPhysicalEntity>();
	
	private String go= null;
	private String goTerm= null;

	private String reversibility = "irreversible-left-to-right";
	private HashMap<String, BioPhysicalEntity> listOfPrimarySubstrates;
	private HashMap<String, BioPhysicalEntity> listOfPrimaryProducts;

	private String sbmlNote = "";

	private Boolean isGenericReaction = false;

	private Boolean hole = false;

	private Flux lowerBound = new Flux("-99999", new BioUnitDefinition("mmol_per_gDW_per_hr", "mmol_per_gDW_per_hr"));
	private Flux upperBound = new Flux("99999", new BioUnitDefinition("mmol_per_gDW_per_hr", "mmol_per_gDW_per_hr"));

	private HashMap<String, Flux> ListOfAdditionalFluxParam = new HashMap<String, Flux>();

	private String KineticFormula;

	public static String FBA_INTERNAL = "Internal";
	public static String FBA_EXCHANGE = "Exchange";
	public static String FBA_TRANSPORT = "Transport";

	public Boolean getHole() {
		return hole;
	}

	public Set<String> getCofactors() {

		Set<String> cofactors = new HashSet<String>();

		HashMap<String, BioParticipant> participants = new HashMap<String, BioParticipant>(
				this.getLeftParticipantList());
		participants.putAll(this.getRightParticipantList());

		for (BioParticipant bpe : participants.values()) {
			if (bpe.getIsCofactor()) {
				cofactors.add(bpe.getPhysicalEntity().getId());
			}
		}
		return cofactors;
	}

	public Set<String> getSideCompounds() {
		Set<String> sides = new HashSet<String>();

		HashMap<String, BioParticipant> participants = new HashMap<String, BioParticipant>(
				this.getLeftParticipantList());
		participants.putAll(this.getRightParticipantList());

		for (BioParticipant bpe : participants.values()) {
			if (!bpe.getIsPrimaryCompound()) {
				sides.add(bpe.getPhysicalEntity().getId());
			}
		}
		return sides;
	}

	public void setHole(Boolean hole) {
		this.hole = hole;
	}

	public Boolean getIsGenericReaction() {
		return isGenericReaction;
	}

	public void setIsGenericReaction(Boolean isGenericReaction) {
		this.isGenericReaction = isGenericReaction;
	}

	public String getGo() {
		return go;
	}

	public String getGoTerm() {
		return goTerm;
	}

	public void setGo(String go) {
		this.go = go;
	}

	public void setGoTerm(String goTerm) {
		this.goTerm = goTerm;
	}

	public BioReaction(String id) {
		super(id);

	}

	public BioReaction(String id, String name) {
		super(id, name);

	}

	public BioReaction(BioReaction rxn) {

		super(rxn);

		this.setDeltaG(rxn.getDeltaG());
		this.setDeltaH(rxn.getDeltaH());
		this.setDeltaS(rxn.getDeltaS());
		this.setEcNumber(rxn.getEcNumber());

		// copy ot the enz list
		this.copyEnzList(rxn.getEnzList());

		this.copyEnzrxnsList(rxn.getEnzrxnsList());
		for (BioCatalysis cat : this.getEnzrxnsList().values()) {
			BioPhysicalEntity enz = cat.getController().getPhysicalEntity();
			this.addEnz(enz);
		}
		this.setKeq(rxn.getKeq());
		this.setReversibility(rxn.getReversiblity());
		this.setSbmlNote(rxn.getSbmlNote());

		this.setLowerBound(rxn.getLowerBound());
		this.setUpperBound(rxn.getUpperBound());
		for (String db : rxn.getRefs().keySet()) {
			for (BioRef ref : rxn.getRefs().get(db)) {
				this.addRef(ref);
			}
		}

	}

	/**
	 * 
	 */
	@Override
	public String toString() {

		String str = new String();

		str = str.concat(this.getId() + " : ");

		int n = 0;
		for (String cpd : this.getLeftList().keySet()) {

			if (n != 0) {
				str = str.concat(" + ");
			}
			n++;

			str = str.concat(cpd);

		}

		if (this.getReversiblity().compareToIgnoreCase("IRREVERSIBLE-RIGHT-TO-LEFT") == 0) {
			str = str.concat(" <- ");
		} else if (this.getReversiblity().compareToIgnoreCase("IRREVERSIBLE-LEFT-TO-RIGHT") == 0) {
			str = str.concat(" -> ");
		} else {
			str = str.concat(" <-> ");
		}

		n = 0;
		for (String cpd : this.getRightList().keySet()) {

			if (n != 0) {
				str = str.concat(" + ");
			}
			n++;

			str = str.concat(cpd);

		}

		return str;

	}

	/**
	 * @param p
	 *            The participant to add in the list of the participants
	 */
	@Override
	public void addLeftParticipant(BioParticipant p) {
		this.getLeftParticipantList().put(p.getId(), p);
		this.getParticipantList().put(p.getId(), p);
		p.getPhysicalEntity().addReactionAsSubstrate(this);
		if (this.reversibility != null && this.isReversible()) {
			p.getPhysicalEntity().addReactionAsProduct(this);
		}
	}

	/**
	 * @param p
	 *            The participant to add in the list of the participants
	 */
	@Override
	public void addRightParticipant(BioParticipant p) {
		this.getRightParticipantList().put(p.getId(), p);
		this.getParticipantList().put(p.getId(), p);
		this.getRightList().put(p.getPhysicalEntity().getId(), p.getPhysicalEntity());
		p.getPhysicalEntity().addReactionAsProduct(this);
		if (this.reversibility != null && this.isReversible()) {
			p.getPhysicalEntity().addReactionAsSubstrate(this);
		}
	}

	/**
	 * @return Returns the deltaG.
	 */
	public String getDeltaG() {
		return deltaG;
	}

	/**
	 * @param deltaG
	 *            The deltaG to set.
	 */
	public void setDeltaG(String deltaG) {
		this.deltaG = deltaG;
	}

	/**
	 * @return Returns the deltaH.
	 */
	public String getDeltaH() {
		return deltaH;
	}

	/**
	 * @param deltaH
	 *            The deltaH to set.
	 */
	public void setDeltaH(String deltaH) {
		this.deltaH = deltaH;
	}

	/**
	 * @return Returns the deltaS.
	 */
	public String getDeltaS() {
		return deltaS;
	}

	/**
	 * @param deltaS
	 *            The deltaS to set.
	 */
	public void setDeltaS(String deltaS) {
		this.deltaS = deltaS;
	}

	/**
	 * @return Returns the ecNumber.
	 */
	public String getEcNumber() {
		return ecNumber;
	}

	/**
	 * @param ecNumber
	 *            The ecNumber to set.
	 */
	public void setEcNumber(String ecNumber) {
		this.ecNumber = ecNumber;
	}

	/**
	 * @return Returns the keq.
	 */
	public String getKeq() {
		return keq;
	}

	/**
	 * @param keq
	 *            The keq to set.
	 */
	public void setKeq(String keq) {
		this.keq = keq;
	}

	/**
	 * @return Returns the enzrxnsList.
	 */
	public HashMap<String, BioPhysicalEntity> getEnzList() {
		return enzList;
	}

	/**
	 * @param BioPhysicalEntity
	 *            enz
	 */
	public void addEnz(BioPhysicalEntity enz) {
		enzList.put(enz.getId(), enz);
	}

	/**
	 * @return Returns the enzrxnsList.
	 */
	public HashMap<String, BioCatalysis> getEnzrxnsList() {
		return enzrxnsList;
	}

	/**
	 * @param BioCatalysis
	 *            enzrxn
	 */
	public void addEnzrxn(BioCatalysis enzrxn) {
		enzrxnsList.put(enzrxn.getId(), enzrxn);
	}

	public void setEnzrxnsList(HashMap<String, BioCatalysis> enzrxnsList) {
		this.enzrxnsList = enzrxnsList;
	}
	
	//TODO : remove?
	public void copyEnzrxnsList(HashMap<String, BioCatalysis> list) {

		this.setEnzrxnsList(new HashMap<String, BioCatalysis>());

		for (BioCatalysis cat : list.values()) {
			BioCatalysis newCat = new BioCatalysis(cat);
			this.addEnzrxn(newCat);
		}
	}


	/**
	 * 
	 * @return true if the reaction is spontaneous or if it is catalysed by an
	 *         enzyme
	 */
	public Boolean isPossible() {

		if (this.getSpontaneous() != null) {
			return true;
		} else {
			// if(this.getEnzrxnsList().size() > 0) {
			if (this.getEnzList().size() > 0) {
				return true;
			}
		}

		return false;

	}

	/**
	 * @return the reversibility of the reaction if not all enzymatic reaction
	 *         indicate the same reversibility info, returns "reversible"
	 *         Possible values of this property are: REVERSIBLE: Interaction can
	 *         occur in both directions IRREVERSIBLE-LEFT-TO-RIGHT
	 *         IRREVERSIBLE-RIGHT-TO-LEFT For all practical purposes, the
	 *         interactions occurs only in the specified direction in
	 *         physiological settings, because of chemical properties of the
	 *         reaction.
	 */
	public String getReversiblity() {
		return reversibility;
	}

	public void setReversibility(Boolean rev) {

		String oldRev = this.getReversiblity();

		if (rev == false) {
			reversibility = "irreversible-left-to-right";
			if (oldRev != null && oldRev.equalsIgnoreCase("reversible")) {
				for (BioParticipant bpe : this.getLeftParticipantList().values()) {
					if (!this.getRightList().containsKey(bpe.getPhysicalEntity().getId())) {
						bpe.getPhysicalEntity().removeReactionAsProduct(this.getId());
					}
				}
				for (BioParticipant bpe : this.getRightParticipantList().values()) {
					if (!this.getLeftList().containsKey(bpe.getPhysicalEntity().getId())) {
						bpe.getPhysicalEntity().removeReactionAsSubstrate(this.getId());
					}
				}
			}

			this.getLowerBound().value = "0.0";

		} else {
			reversibility = "reversible";
			if (oldRev != null && !oldRev.equalsIgnoreCase("reversible")) {
				for (BioParticipant bpe : this.getLeftParticipantList().values()) {
					bpe.getPhysicalEntity().addReactionAsProduct(this);
				}
				for (BioParticipant bpe : this.getRightParticipantList().values()) {
					bpe.getPhysicalEntity().addReactionAsSubstrate(this);
				}
			}
		}

	}

	//TODO : define reversibility as boolean only
	public void setReversibility(String rev) {

		reversibility = rev;

		String oldRev = this.getReversiblity();

		if (rev.equalsIgnoreCase("reversible") && !oldRev.equalsIgnoreCase("reversible")) {
			for (BioParticipant bpe : this.getLeftParticipantList().values()) {
				bpe.getPhysicalEntity().addReactionAsProduct(this);
			}
			for (BioParticipant bpe : this.getRightParticipantList().values()) {
				bpe.getPhysicalEntity().addReactionAsSubstrate(this);
			}
		}

		if (!rev.equalsIgnoreCase("reversible") && oldRev.equalsIgnoreCase("reversible")) {
			for (BioParticipant bpe : this.getLeftParticipantList().values()) {
				if (!this.getRightList().containsKey(bpe.getPhysicalEntity().getId())) {
					bpe.getPhysicalEntity().removeReactionAsProduct(this.getId());
				}
			}
			for (BioParticipant bpe : this.getRightParticipantList().values()) {
				if (!this.getLeftList().containsKey(bpe.getPhysicalEntity().getId())) {
					bpe.getPhysicalEntity().removeReactionAsSubstrate(this.getId());
				}
			}

			this.getLowerBound().value = "0.0";
		}
	}

	public void setReversibility() {
		String rev = null;

		for (Iterator<String> iter = enzrxnsList.keySet().iterator(); iter.hasNext();) {
			BioCatalysis enzrxn = enzrxnsList.get(iter.next());

			String direction = enzrxn.getDirection().toLowerCase();

			if (rev == null) {
				if (direction.matches("^irreversible.*")) {
					rev = direction;
					this.getLowerBound().value = "0.0";
				} else {
					this.reversibility = "reversible";
					return;
				}
			} else {
				if (direction.matches("^irreversible.*") == false) {
					this.reversibility = "reversible";
					return;
				}

				if (direction.matches("^" + rev + "$") == false) {
					this.reversibility = "reversible";
					return;
				}

				rev = direction;
			}
		}

		if (rev == null)
			this.reversibility = "reversible";
		else
			this.reversibility = rev;

		if (this.reversibility.equalsIgnoreCase("reversible")) {
			for (BioParticipant bpe : this.getLeftParticipantList().values()) {
				bpe.getPhysicalEntity().addReactionAsProduct(this);
			}
			for (BioParticipant bpe : this.getRightParticipantList().values()) {
				bpe.getPhysicalEntity().addReactionAsSubstrate(this);
			}
		}

		if (!this.reversibility.equalsIgnoreCase("reversible")) {
			for (BioParticipant bpe : this.getLeftParticipantList().values()) {
				if (!this.getRightList().containsKey(bpe.getPhysicalEntity().getId())) {
					bpe.getPhysicalEntity().removeReactionAsProduct(this.getId());
				}
			}
			for (BioParticipant bpe : this.getRightParticipantList().values()) {
				if (!this.getLeftList().containsKey(bpe.getPhysicalEntity().getId())) {
					bpe.getPhysicalEntity().removeReactionAsSubstrate(this.getId());
				}
			}
		}

	}



	/**
	 * Set the list of substrates of the reaction. if the reaction is
	 * reversible, returns the list of left and right compounds if the reaction
	 * is irreversible-left-right, returns the list of left compounds if the
	 * reaction is irreversible-right-left, returns the list of right compounds
	 */
	public void setListOfSubstrates() {

		HashMap<String, BioPhysicalEntity> listOfSubstrates = new HashMap<String, BioPhysicalEntity>();

		String rev = this.getReversiblity();

		if (rev.compareToIgnoreCase("irreversible-left-to-right") != 0 && rev.compareToIgnoreCase("reversible") != 0
				&& rev.compareToIgnoreCase("irreversible-right-to-left") != 0) {
			rev = "reversible";
		}

		if (rev.compareToIgnoreCase("irreversible-left-to-right") == 0 || rev.compareToIgnoreCase("reversible") == 0) {

			HashMap<String, BioPhysicalEntity> left = this.getLeftList();

			listOfSubstrates.putAll(left);

		}

		if (rev.compareToIgnoreCase("irreversible-right-to-left") == 0 || rev.compareToIgnoreCase("reversible") == 0) {

			HashMap<String, BioPhysicalEntity> right = this.getRightList();
			listOfSubstrates.putAll(right);

		}

		this.listOfSubstrates = listOfSubstrates;
	}

	/**
	 * @return the list of substrates of the reaction. if the reaction is
	 *         reversible, returns the list of left and right compounds if the
	 *         reaction is irreversible-left-right, returns the list of left
	 *         compounds if the reaction is irreversible-right-left, returns the
	 *         list of right compounds
	 */
	public HashMap<String, BioPhysicalEntity> getListOfSubstrates() {
		this.setListOfSubstrates();

		return (this.listOfSubstrates);

	}

	/**
	 * Set the list of products of the reaction. if the reaction is reversible,
	 * returns the list of left and right compounds if the reaction is
	 * irreversible-left-right, returns the list of right compounds if the
	 * reaction is irreversible-right-left, returns the list of left compounds
	 */
	public void setListOfProducts() {

		HashMap<String, BioPhysicalEntity> listOfProducts = new HashMap<String, BioPhysicalEntity>();

		String rev = this.getReversiblity();

		if (rev.compareToIgnoreCase("irreversible-left-to-right") != 0 && rev.compareToIgnoreCase("reversible") != 0
				&& rev.compareToIgnoreCase("irreversible-right-to-left") != 0) {
			rev = "reversible";
		}

		if (rev.compareToIgnoreCase("irreversible-left-to-right") == 0 || rev.compareToIgnoreCase("reversible") == 0) {

			HashMap<String, BioPhysicalEntity> right = this.getRightList();
			listOfProducts.putAll(right);
		}

		if (rev.compareToIgnoreCase("irreversible-right-to-left") == 0 || rev.compareToIgnoreCase("reversible") == 0) {

			HashMap<String, BioPhysicalEntity> left = this.getLeftList();
			listOfProducts.putAll(left);

		}

		this.listOfProducts = listOfProducts;

	}

	/**
	 * @return the list of products of the reaction. if the reaction is
	 *         reversible, returns the list of left and right compounds if the
	 *         reaction is irreversible-left-right, returns the list of right
	 *         compounds if the reaction is irreversible-right-left, returns the
	 *         list of left compounds
	 */

	public HashMap<String, BioPhysicalEntity> getListOfProducts() {
		this.setListOfProducts();
		return (this.listOfProducts);
	}

	/**
	 * Set the list of primary substrates of the reaction. if the reaction is
	 * reversible, returns the list of left and right compounds if the reaction
	 * is irreversible-left-right, returns the list of left compounds if the
	 * reaction is irreversible-right-left, returns the list of right compounds
	 */
	public void setListOfPrimarySubstrates() {

		HashMap<String, BioPhysicalEntity> listOfSubstrates = new HashMap<String, BioPhysicalEntity>();

		String rev = this.getReversiblity();

		if (rev.compareToIgnoreCase("irreversible-left-to-right") != 0 && rev.compareToIgnoreCase("reversible") != 0
				&& rev.compareToIgnoreCase("irreversible-right-to-left") != 0) {
			rev = "reversible";
		}

		if (rev.compareToIgnoreCase("irreversible-left-to-right") == 0 || rev.compareToIgnoreCase("reversible") == 0) {

			HashMap<String, BioPhysicalEntity> left = this.getPrimaryLeftList();
			listOfSubstrates.putAll(left);

		}

		if (rev.compareToIgnoreCase("irreversible-right-to-left") == 0 || rev.compareToIgnoreCase("reversible") == 0) {

			HashMap<String, BioPhysicalEntity> right = this.getPrimaryRightList();
			listOfSubstrates.putAll(right);

		}

		this.listOfPrimarySubstrates = listOfSubstrates;
	}

	/**
	 * @return the list of substrates of the reaction. if the reaction is
	 *         reversible, returns the list of left and right compounds if the
	 *         reaction is irreversible-left-right, returns the list of left
	 *         compounds if the reaction is irreversible-right-left, returns the
	 *         list of right compounds
	 */

	public HashMap<String, BioPhysicalEntity> getListOfPrimarySubstrates() {
		this.setListOfPrimarySubstrates();

		return (this.listOfPrimarySubstrates);

	}

	/**
	 * Set the list of primary products of the reaction. if the reaction is
	 * reversible, returns the list of left and right compounds if the reaction
	 * is irreversible-left-right, returns the list of right compounds if the
	 * reaction is irreversible-right-left, returns the list of left compounds
	 */
	public void setListOfPrimaryProducts() {

		HashMap<String, BioPhysicalEntity> listOfProducts = new HashMap<String, BioPhysicalEntity>();

		String rev = this.getReversiblity();

		if (rev.compareToIgnoreCase("irreversible-left-to-right") != 0 && rev.compareToIgnoreCase("reversible") != 0
				&& rev.compareToIgnoreCase("irreversible-right-to-left") != 0) {
			rev = "reversible";
		}

		if (rev.compareToIgnoreCase("irreversible-left-to-right") == 0 || rev.compareToIgnoreCase("reversible") == 0) {

			HashMap<String, BioPhysicalEntity> right = this.getPrimaryRightList();
			listOfProducts.putAll(right);
		}

		if (rev.compareToIgnoreCase("irreversible-right-to-left") == 0 || rev.compareToIgnoreCase("reversible") == 0) {

			HashMap<String, BioPhysicalEntity> left = this.getPrimaryLeftList();
			listOfProducts.putAll(left);

		}

		this.listOfPrimaryProducts = listOfProducts;

	}

	/**
	 * @return the list of primary products of the reaction. if the reaction is
	 *         reversible, returns the list of left and right compounds if the
	 *         reaction is irreversible-left-right, returns the list of right
	 *         compounds if the reaction is irreversible-right-left, returns the
	 *         list of left compounds
	 */

	public HashMap<String, BioPhysicalEntity> getListOfPrimaryProducts() {
		this.setListOfPrimaryProducts();
		return (this.listOfPrimaryProducts);
	}


	public String getEquation() {

		int nb = 0;

		String out = "";

		for (BioPhysicalEntity l : this.getLeftList().values()) {
			nb++;
			if (nb > 1) {
				out = out.concat(" + ");
			}

			out = out + l.getId() + "[" + l.getCompartment().getId() + "]";

		}

		String rev = this.getReversiblity();

		if (rev.compareToIgnoreCase("irreversible-left-to-right") == 0) {
			out = out.concat(" -> ");
		} else if (rev.compareToIgnoreCase("irreversible-right-to-left") == 0) {
			out = out.concat(" <- ");
		} else {
			out = out.concat(" <-> ");
		}

		nb = 0;
		for (BioPhysicalEntity r : this.getRightList().values()) {
			nb++;
			if (nb > 1) {
				out = out.concat(" + ");
			}

			out = out + r.getId() + "[" + r.getCompartment().getId() + "]";
		}

		return out;

	}

	public String getEquationForHuman() {

		int nb = 0;

		String out;

		out = "";

		TreeSet<String> left = new TreeSet<String>(
				this.getLeftParticipantList().keySet());

		for (String id : left) {
			
			BioParticipant l = this.getLeftParticipantList().get(id);
			
			nb++;
			if (nb > 1) {
				out = out.concat(" + ");
			}

			String coeff = l.getStoichiometricCoefficient().equals("1") ? "" : l.getStoichiometricCoefficient()+" ";

			out = out + coeff + StringUtils.getNotFormattedString(l.getPhysicalEntity().getName()) + "["
					+ l.getPhysicalEntity().getCompartment().getId() + "]";

		}

		String rev = this.getReversiblity();

		if (rev.compareToIgnoreCase("irreversible-left-to-right") == 0) {
			out = out.concat(" -> ");
		} else if (rev.compareToIgnoreCase("irreversible-right-to-left") == 0) {
			out = out.concat(" <- ");
		} else {
			out = out.concat(" <-> ");
		}

		nb = 0;

		TreeSet<String> right = new TreeSet<String>(
				this.getRightParticipantList().keySet());

		for (String id : right) {
			
			BioParticipant r = this.getRightParticipantList().get(id);
			
			nb++;
			if (nb > 1) {
				out = out.concat(" + ");
			}

			String coeff = r.getStoichiometricCoefficient().equals("1") ? "" : r.getStoichiometricCoefficient()+" ";

			out = out + coeff + StringUtils.getNotFormattedString(r.getPhysicalEntity().getName()) + "["
					+ r.getPhysicalEntity().getCompartment().getId() + "]";
		}

		return out;

	}

	public String getSbmlNote() {
		return sbmlNote;
	}

	public void setSbmlNote(String sbmlNote) {
		this.sbmlNote = sbmlNote;
	}

	public void setEnzList(HashMap<String, BioPhysicalEntity> enzList) {
		this.enzList = enzList;
	}

	public void copyEnzList(HashMap<String, BioPhysicalEntity> enzList) {

		this.setEnzList(new HashMap<String, BioPhysicalEntity>());

		for (BioPhysicalEntity enz : enzList.values()) {
			BioPhysicalEntity newEnz = new BioPhysicalEntity(enz);
			this.getEnzList().put(newEnz.getId(), newEnz);
		}
	}

	/**
	 * 
	 * @param cpd
	 */
	public void removeLeft(String cpd) {

		this.getLeftList().remove(cpd);

		HashMap<String, BioParticipant> bpes = new HashMap<String, BioParticipant>(
				this.getLeftParticipantList());

		for (BioParticipant bpe : bpes.values()) {
			if (bpe.getPhysicalEntity().getId().compareTo(cpd) == 0) {
				this.getLeftParticipantList().remove(bpe.getId());
			}
		}
	}

	/**
	 * 
	 * @param cpd
	 */
	public void removeRight(String cpd) {

		HashMap<String, BioParticipant> bpes = new HashMap<String, BioParticipant>(
				this.getRightParticipantList());

		for (BioParticipant bpe : bpes.values()) {
			if (bpe.getPhysicalEntity().getId().compareTo(cpd) == 0) {
				this.getRightParticipantList().remove(bpe.getId());
			}
		}
	}

	public Flux getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(Flux lowerBound) {
		this.lowerBound = lowerBound;
	}

	public Flux getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(Flux upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * Return true if the reaction is reversible, false otherwise. Default is
	 * set to false.
	 * 
	 * @return reversibility
	 */
	public Boolean isReversible() {
		if (StringUtils.isVoid(this.getReversiblity()))
			return false;
		if (this.getReversiblity().compareToIgnoreCase("reversible") == 0) {
			return true;
		}
		return false;
	}

	/**
	 * @param pathway
	 *            : the pathway to add
	 */
	@Override
	public void addPathway(BioPathway pathway) {
		this.getPathwayList().put(pathway.getId(), pathway);
		pathway.addReaction(this);
	}

	/**
	 * Add a side compound
	 * 
	 * @param cpdId
	 */
	public void addSideCompound(String cpdId) {

		this.getSideCompounds().add(cpdId);

		for (BioParticipant bpe : this.getLeftParticipantList().values()) {
			BioPhysicalEntity cpd = bpe.getPhysicalEntity();

			if (cpd.getId().equalsIgnoreCase(cpdId)) {
				bpe.setIsPrimaryCompound(false);
			}

		}

		for (BioParticipant bpe : this.getRightParticipantList().values()) {
			BioPhysicalEntity cpd = bpe.getPhysicalEntity();

			if (cpd.getId().equalsIgnoreCase(cpdId)) {
				bpe.setIsPrimaryCompound(false);
			}

		}

	}

	/**
	 * Add a primary compound
	 * 
	 * @param cpdId
	 */
	public void addPrimaryCompound(String cpdId) {

		for (BioParticipant bpe : this.getLeftParticipantList().values()) {
			BioPhysicalEntity cpd = bpe.getPhysicalEntity();

			if (cpd.getId().equalsIgnoreCase(cpdId)) {
				bpe.setIsPrimaryCompound(true);
			}

		}

		for (BioParticipant bpe : this.getRightParticipantList().values()) {
			BioPhysicalEntity cpd = bpe.getPhysicalEntity();

			if (cpd.getId().equalsIgnoreCase(cpdId)) {
				bpe.setIsPrimaryCompound(true);
			}

		}

	}

	/**
	 * Add a cofactor
	 * 
	 * @param cpdId
	 */
	public void addCofactor(String cpdId) {

		this.getCofactors().add(cpdId);

		for (BioParticipant bpe : this.getLeftParticipantList().values()) {
			BioPhysicalEntity cpd = bpe.getPhysicalEntity();

			if (cpd.getId().equalsIgnoreCase(cpdId)) {
				bpe.setIsCofactor(true);
			}

		}

		for (BioParticipant bpe : this.getRightParticipantList().values()) {
			BioPhysicalEntity cpd = bpe.getPhysicalEntity();

			if (cpd.getId().equalsIgnoreCase(cpdId)) {
				bpe.setIsCofactor(true);
			}

		}

	}

	public BioCompartment getCompartmentFromSuper() {
		return super.getCompartment();
	}

	/**
	 * @return if the all the substrates are in the same compartment then return
	 *         the compartment else return null
	 */
	@Override
	public BioCompartment getCompartment() {
		BioCompartment compartment = null;

		Set<BioCompartment> compartmentLefts = new HashSet<BioCompartment>();
		Set<BioCompartment> compartmentRights = new HashSet<BioCompartment>();

		for (BioParticipant bpe : this.getLeftParticipantList().values()) {
			BioPhysicalEntity cpd = bpe.getPhysicalEntity();
			BioCompartment cpt = cpd.getCompartment();
			compartmentLefts.add(cpt);
		}

		for (BioParticipant bpe : this.getRightParticipantList().values()) {
			BioPhysicalEntity cpd = bpe.getPhysicalEntity();
			BioCompartment cpt = cpd.getCompartment();
			compartmentRights.add(cpt);
		}

		Vector<BioCompartment> compartments = new Vector<BioCompartment>();

		compartments.addAll(compartmentRights);
		compartments.addAll(compartmentLefts);

		if (compartments.size() == 2 && compartments.get(0).getId().equals(compartments.get(1).getId())) {
			compartment = compartments.get(0);
		}

		return compartment;
	}

	/**
	 * @return true if the reaction is a transport reaction
	 
	// TODO : Pas sur que ca fonctionne à tous les coups
	// Ex : A_a + C_b -> D_a + B_b
	// A recoder en pensant au BioCompartment dans BPE.
	 * @return
	 */
	public Boolean isTransportReaction() {
		
		HashSet<BioCompartment> compartmnts = new HashSet<BioCompartment>();
		for(BioPhysicalEntity s : this.getLeftList().values()){
			compartmnts.add(s.getCompartment());
		}
		for(BioPhysicalEntity p : 
			this.getRightList().values()){
			compartmnts.add(p.getCompartment());
		}
		
		if(compartmnts.size()>1){
			return true;
		}else{
			return false;
		}
		
	}

	/**
	 * @return true if the reaction is an exchange reaction: - if the lefts or
	 *         the rights are empty - if one left or one right is external
	 */
	public Boolean isExchangeReaction() {

		HashMap<String, BioParticipant> lefts = this.getLeftParticipantList();
		HashMap<String, BioParticipant> rights = this.getRightParticipantList();

		if (lefts.size() == 0 || rights.size() == 0) {
			return true;
		}

		for (BioParticipant bpe : lefts.values()) {

			BioPhysicalEntity cpd = bpe.getPhysicalEntity();

			if (cpd.getBoundaryCondition() == true) {
				return true;
			}
		}

		for (BioParticipant bpe : rights.values()) {

			BioPhysicalEntity cpd = bpe.getPhysicalEntity();

			if (cpd.getBoundaryCondition() == true) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Get the type of the reaction (internal, exchange or transport)
	 * 
	 * @return
	 */
	public String getFBAType() {

		String type = FBA_INTERNAL;

		if (this.isExchangeReaction()) {
			type = FBA_EXCHANGE;
		} else if (this.isTransportReaction()) {
			type = FBA_TRANSPORT;
		}

		return type;
	}

	public HashMap<String, Flux> getListOfAdditionalFluxParam() {
		return ListOfAdditionalFluxParam;
	}

	public void setListOfAdditionalFluxParam(HashMap<String, Flux> listOfAdditionalFluxParam) {
		ListOfAdditionalFluxParam = listOfAdditionalFluxParam;
	}

	public void addFluxParam(String id, Flux fluxtoadd) {
		ListOfAdditionalFluxParam.put(id, fluxtoadd);
	}

	public String getKineticFormula() {
		return KineticFormula;
	}

	public void setKineticFormula(String kineticFormula) {
		KineticFormula = kineticFormula;
	}

}
