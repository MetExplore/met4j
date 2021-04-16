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

package fr.inrae.toulouse.metexplore.met4j_core.biodata.utils;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>BioReactionUtils class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class BioReactionUtils {
	
	/**
	 * Comparison of two reactions
	 *
	 * @param network a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork}
	 * @param r1 a first {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
	 * @param r2 a second {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
	 * @return true if the substrates and the products have the same id
	 * @throws java.lang.IllegalArgumentException if one of the reaction is not in the network
	 */
	public static Boolean areRedundant(BioNetwork network, BioReaction r1, BioReaction r2) {

		
		if(! network.contains(r1))
		{
			throw new IllegalArgumentException(r1.getId()+" is not present in the network");
		}
		
		if(! network.contains(r2))
		{
			throw new IllegalArgumentException(r2.getId()+" is not present in the network");
		}
		
		
		if(r1.isReversible() != r2.isReversible())
		{
			return false;
		}
		
		BioCollection<BioReactant> leftR1 = network.getLeftReactants(r1);
		BioCollection<BioReactant> leftR2 = network.getLeftReactants(r2);
		BioCollection<BioReactant> rightR1 = network.getRightReactants(r1);
		BioCollection<BioReactant> rightR2 = network.getRightReactants(r2);
		
		Boolean flag1 = leftR1.containsAll(leftR2) &&
				leftR2.containsAll(leftR1) && 
				rightR1.containsAll(rightR2) &&
				rightR2.containsAll(rightR1);
		
		if(! r1.isReversible())
		{
			return flag1;
		}
		else 
		{
			Boolean flag2 = rightR1.containsAll(leftR2) &&
					leftR2.containsAll(rightR1) && 
					leftR1.containsAll(rightR2) &&
					rightR2.containsAll(leftR1);
			
			return flag1 || flag2;
		}
		

	}
	
	
	
	/**
	 * get Gene association of a reaction in string format
	 *
	 * @param network a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork}
	 * @param r a BioReaction
	 * @param getGeneNames if true returns the gene names instead of the gene ids
	 * @return a String like "G1 OR (G2 AND G3)
	 */
	public static String getGPR(BioNetwork network, BioReaction r, Boolean getGeneNames) {
		
		if(! network.contains(r))
		{
			throw new IllegalArgumentException(r.getId()+" is not present in the network");
		}
		
		BioCollection<BioEnzyme> enzymes = r.getEnzymesView();
		
		TreeSet<String> geneIdSets  = new TreeSet<>();
		
		for(BioEnzyme enz : enzymes)
		{
			BioCollection<BioEnzymeParticipant> participants = enz.getParticipantsView();
			
			TreeSet<String> geneIds = new TreeSet<>();
			
			for(BioEnzymeParticipant p : participants) {
				BioPhysicalEntity ent = p.getPhysicalEntity();
				
				if(ent.getClass().equals(BioProtein.class))
				{
					BioProtein prot = (BioProtein) ent;
					BioGene g = prot.getGene();
					if(g == null)
					{
						System.err.println("[WARNING] No gene for the protein "+prot.getId());
					}
					else {
						String id = getGeneNames ? g.getName() : g.getId();
						geneIds.add(id);
					}
				}
			}
			
			if(geneIds.size() > 0)
			{
				String geneIdString = StringUtils.join(geneIds.toArray(new String[0]), " AND ");
				geneIdSets.add(geneIdString);
			}
			
		}
		
		TreeSet<String> finalSet;
		
		if(geneIdSets.size() > 1 ){
			
			finalSet = geneIdSets.stream().map(s -> "( " + s + " )").collect(Collectors.toCollection(TreeSet::new));
			
		}
		else {
			finalSet = geneIdSets;
		}


		return StringUtils.join(finalSet.toArray(new String[0]), " OR ");
		
	}

	/**
	 * get Gene association of a reaction in string format
	 *
	 * @param network a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork}
	 * @param r a BioReaction
	 * @return a String like "G1 OR (G2 AND G3)
	 */
	public static String getGPR(BioNetwork network, BioReaction r) {
		return getGPR(network, r, false);
	}

	/**
	 * <p>getEquation.</p>
	 *
	 * @param r a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} object.
	 * @param getNames a {@link java.lang.Boolean} object.
	 * @param revSep a {@link java.lang.String} object.
	 * @param irrevSep a {@link java.lang.String} object.
	 * @param getCompartment a {@link java.lang.Boolean} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getEquation(BioReaction r, Boolean getNames, String revSep, String irrevSep, Boolean getCompartment) {

		BioCollection<BioReactant> lefts = r.getLeftReactantsView();
		BioCollection<BioReactant> rights = r.getRightReactantsView();
		Boolean rev = r.isReversible();

		String eq = rev ? " "+revSep+" " : " "+irrevSep+" ";

		return reactantsToString(lefts, getNames, getCompartment) + eq + reactantsToString(rights,getNames, getCompartment);
	}

	/**
	 * <p>getEquation.</p>
	 *
	 * @param r a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} object.
	 * @param getNames a {@link java.lang.Boolean} object.
	 * @param getCompartment a {@link java.lang.Boolean} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getEquation(BioReaction r, Boolean getNames, Boolean getCompartment) {

		BioCollection<BioReactant> lefts = r.getLeftReactantsView();
		BioCollection<BioReactant> rights = r.getRightReactantsView();
		Boolean rev = r.isReversible();

		String revSep = "<==>";
		String irrevSep = "-->";

		String eq = rev ? " "+revSep+" " : " "+irrevSep+" ";

		return reactantsToString(lefts, getNames, getCompartment) + eq + reactantsToString(rights,getNames, getCompartment);
	}

	private static String reactantsToString(BioCollection<BioReactant> reactants, Boolean getNames, Boolean getCompartment)
	{
		ArrayList<String> parts = new ArrayList<>();

		for(BioReactant r : reactants) {
			String id = getNames ? r.getMetabolite().getName() : r.getMetabolite().getId();
			Double sto = r.getQuantity();
			String cptId = r.getLocation().getId();

			String reactantString  = (sto == 1.0 ? "" : sto+" ") + id + (getCompartment ? "["+cptId+"]" : "");

			parts.add(reactantString);
		}

		return String.join(" + ", parts);

	}

	/**
	 * <p>getPathwaysString.</p>
	 *
	 * @param r a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} object.
	 * @param n a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
	 * @param getNames a {@link java.lang.Boolean} object.
	 * @param delim a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getPathwaysString(BioReaction r, BioNetwork n, Boolean getNames, String delim) {

		BioCollection<BioPathway> pathways = n.getPathwaysFromReaction(r);

		Set<String> stringArrayList = pathways.stream().map(p -> getNames ? p.getName() : p.getId()).collect(Collectors.toSet());

		return String.join(delim, stringArrayList);
	}
}
