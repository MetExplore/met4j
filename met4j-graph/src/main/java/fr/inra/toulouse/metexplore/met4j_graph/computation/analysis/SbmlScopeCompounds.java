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
package fr.inra.toulouse.metexplore.met4j_graph.computation.analysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.text.parser.ParseException;

import fr.inra.toulouse.metexplore.met4j_graph.core.ScopeCompounds;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.io.JSBMLToBionetwork;




// TODO: Auto-generated Javadoc
/**
 * The Class SbmlScopeCompounds.
 */
public class SbmlScopeCompounds {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		
		Set<String> listOfInCpds = new HashSet<String>();
		Set<String> listOfBootstrapCpds = new HashSet<String>();
		
		String sbmlFile = args[0];
		String listOfCompoundsFile = args[1];
		String fileOut = args[2];
		String flagOnlyResults = args[3];
		
		String encodeStr = args[4];
		
		String flagRevReactions = args[5];
		
		Boolean useReversibleReactionsOnlyOnce = false;

		if(flagRevReactions.equalsIgnoreCase("T")) {
			useReversibleReactionsOnlyOnce = true;
		}
		
		Boolean encode = false;
		if(encodeStr.compareTo("T") == 0) {
			encode = true;
		}
		
		Boolean onlyResults = false;
		
		
		if(flagOnlyResults.compareTo("T") == 0) {
			onlyResults = true;
		}
		
		
		FileInputStream in;
		
		try {
			in = new FileInputStream(listOfCompoundsFile);
			InputStreamReader ipsr=new InputStreamReader(in);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			
			while ((ligne=br.readLine())!=null){
				String[] tab = ligne.split("\\t");
				String id;
			
				id = tab[0];
				
				String type = tab[1];
				
				if(type.compareTo("I") == 0) 
					listOfInCpds.add(id);
				else if (type.compareTo("B") == 0) {
					listOfBootstrapCpds.add(id);
				}
				
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
			
		
		JSBMLToBionetwork reader = new JSBMLToBionetwork(sbmlFile);
		
		BioNetwork bn = reader.getBioNetwork();
		
		// Here, we do not filter the reactionNodes
		
		ScopeCompounds scope = new ScopeCompounds(bn, listOfInCpds, listOfBootstrapCpds, "", new HashSet<String>(), useReversibleReactionsOnlyOnce, true);
		
		System.err.println("Number of reactions : "+bn.getBiochemicalReactionList().size());
		System.err.println("Number of compounds : "+bn.getPhysicalEntityList().size());
		
		
		while(scope.run() != 0) { // While new compounds are added
			;
		}
		
		scope.createScopeNetwork();
		
		System.err.println("Fileout : "+fileOut);
		
		String fileAttributs = fileOut+".attributes";
		String fileReactions = fileOut+".reactions";
		String fileCpds = fileOut+".cpds";
		String fileSbml = fileOut+".xml";
		String fileInvSbml = fileOut+"_inv.xml";
		
		try {
			scope.writeAttributeFile(fileAttributs, encode, onlyResults);
			scope.writeListOfReactions(fileReactions, encode);
			scope.writeListOfCompounds(fileCpds, encode);
			scope.writeClustersFiles(fileOut, encode);
			try {
				scope.writeScopeAsSbml(fileSbml);
				scope.writeInvScopeAsSbml(fileInvSbml);
			} catch (SBMLException | XMLStreamException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
