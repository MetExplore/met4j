package fr.inra.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity;
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


import java.util.BitSet;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.EStateFingerprinter;
import org.openscience.cdk.fingerprint.ExtendedFingerprinter;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.fingerprint.KlekotaRothFingerprinter;
import org.openscience.cdk.fingerprint.MACCSFingerprinter;
import org.openscience.cdk.fingerprint.PubchemFingerprinter;
import org.openscience.cdk.fingerprint.SubstructureFingerprinter;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;


/**
 * provide static methods to compute chemical fingerprints
 * @author clement
 */
public class FingerprintBuilder {
	
	public static final int MACCS = 0;
	public static final int EXTENDED = 1;
	public static final int KLEKOTAROTH = 2;
	public static final int PUBCHEM = 3;
	public static final int ESTATE = 4;
	public static final int SUBSTRUCTURE = 5;
	private IFingerprinter fingerprinter;
	/**
	 * Instantiates a new fingerprint builder.
	 */
	public FingerprintBuilder(int fingerprintType) {
		switch (fingerprintType) {
		case MACCS:
			fingerprinter = new MACCSFingerprinter();
			break;
		case EXTENDED:
			fingerprinter = new ExtendedFingerprinter();
			break;
		case KLEKOTAROTH:
			fingerprinter = new KlekotaRothFingerprinter();
			break;
		case PUBCHEM:
			fingerprinter = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
			break;
		case ESTATE:
			fingerprinter = new EStateFingerprinter();
			break;
		case SUBSTRUCTURE:
			fingerprinter = new SubstructureFingerprinter();
			break;
		default:
			System.err.println("fingerprint type not handled, switch to default fingerprint");
			fingerprinter = new ExtendedFingerprinter();
			break;
		}
	}

	/**
	 * Returns the fingerprint from:
	 * - the inchi of the metabolite if set
	 * - the smiles if the inchi is not set
	 *
	 * @param e a {@link BioMetabolite}
	 * @return
	 */
	public BitSet getFingerprint(BioMetabolite e){
		String inchi = e.getInchi();
		if(!StringUtils.isVoid(inchi)) {
			return getFingerprintFromInChi(inchi);
		}
		String smiles = e.getSmiles();
		if(!StringUtils.isVoid(smiles)) return getFingerprintFromSmiles(smiles);
		return null;
	}
	
	public BitSet getFingerprintFromInChi(String inchi){
		try {
			InChIToStructure struct = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
			IAtomContainer m = struct.getAtomContainer();				
			BitSet fingerprint = fingerprinter.getBitFingerprint(m).asBitSet();
			return fingerprint;
			
		} catch (CDKException e1) {
			System.err.println("Error: impossible to compute fingerprint from "+inchi);
			e1.printStackTrace();
			return null;
		}
	}
	
	public BitSet getFingerprintFromSmiles(String smiles){
		try {
			SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
			IAtomContainer m = sp.parseSmiles(smiles);			
			BitSet fingerprint = fingerprinter.getBitFingerprint(m).asBitSet();
			return fingerprint;
			
		} catch (CDKException e1) {
			System.err.println("Error: impossible to compute fingerprint from "+smiles);
			e1.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets the MACCS fingerprint.
	 *
	 * @param e the compound
	 * @return the MACCS fingerprint
	 */
	public static BitSet getMACCSFingerprint(BioMetabolite e){
		String inchi = e.getInchi();
		String smiles = e.getSmiles();
		BitSet fingerprint;
		try {
			if (inchi.isEmpty() || inchi.equals("NA")) {
				if (!smiles.equals("NA")){
					MACCSFingerprinter maccs = new MACCSFingerprinter();
					SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
					IAtomContainer m = sp.parseSmiles(smiles);
					fingerprint = maccs.getBitFingerprint(m).asBitSet();
					//return maccs.getFingerprint(m);
				}else{
					//System.err.println("Error: impossible to compute fingerprint from "+e.getId()+", no structural information available");
					fingerprint = null;
					//return null;
				}
			} else {
				MACCSFingerprinter maccs = new MACCSFingerprinter();
				InChIToStructure struct = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
				IAtomContainer m = struct.getAtomContainer();
				fingerprint = maccs.getBitFingerprint(m).asBitSet();
				//return maccs.getFingerprint(m);
			}

		} catch (CDKException e1) {
			System.err.println("Error: impossible to compute fingerprint from "+e.getId());
			e1.printStackTrace();
			fingerprint = null;
			//return null;
		}
		return fingerprint;
	}
	
	/**
	 * Gets the extended fingerprint.
	 *
	 * @param e the compound
	 * @return the extended fingerprint
	 */
	public static BitSet getExtendedFingerprint(BioMetabolite e){
		String inchi = e.getInchi();
		String smiles = e.getSmiles();
		BitSet fingerprint;
		try {
			if(!(inchi.equals("NA") || inchi.isEmpty())){
				ExtendedFingerprinter ext = new ExtendedFingerprinter();
				InChIToStructure struct = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
				IAtomContainer m = struct.getAtomContainer();
				fingerprint = ext.getBitFingerprint(m).asBitSet();						
			}else if (!smiles.equals("NA")){
				ExtendedFingerprinter ext = new ExtendedFingerprinter();
				SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
				IAtomContainer m = sp.parseSmiles(smiles);
				fingerprint = ext.getBitFingerprint(m).asBitSet();
			}else{
				//System.err.println("Error: impossible to compute fingerprint from "+e.getId()+", no structural information available");
				fingerprint = null;
				//return null;
			}
			
		} catch (CDKException e1) {
			System.err.println("Error: impossible to compute fingerprint from "+e.getId());
			e1.printStackTrace();
			fingerprint = null;
			//return null;
		}
		return fingerprint;
	}
	
	/**
	 * Gets the klekota roth fingerprint.
	 *
	 * @param e the compound
	 * @return the klekota roth fingerprint
	 */
	public static BitSet getKlekotaRothFingerprint(BioMetabolite e){
		String inchi = e.getInchi();
		String smiles = e.getSmiles();
		BitSet fingerprint;
		try {
			if(!(inchi.equals("NA") || inchi.isEmpty())){
				KlekotaRothFingerprinter kr = new KlekotaRothFingerprinter();
				InChIToStructure struct = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
				IAtomContainer m = struct.getAtomContainer();
				fingerprint = kr.getBitFingerprint(m).asBitSet();						
			}else if (!smiles.equals("NA")){
				KlekotaRothFingerprinter kr = new KlekotaRothFingerprinter();
				SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
				IAtomContainer m = sp.parseSmiles(smiles);
				fingerprint = kr.getBitFingerprint(m).asBitSet();
			}else{
				//System.err.println("Error: impossible to compute fingerprint from "+e.getId()+", no structural information available");
				fingerprint = null;
				//return null;
			}
			
		} catch (CDKException e1) {
			System.err.println("Error: impossible to compute fingerprint from "+e.getId());
			e1.printStackTrace();
			fingerprint = null;
			//return null;
		}
		return fingerprint;
	}
	
	/**
	 * Gets the pubchem fingerprint.
	 *
	 * @param e the compound
	 * @return the pubchem fingerprint
	 */
	public static BitSet getPubchemFingerprint(BioMetabolite e){
		String inchi = e.getInchi();
		String smiles = e.getSmiles();
		BitSet fingerprint;
		try {
			if(!(inchi.equals("NA") || inchi.isEmpty())){
				PubchemFingerprinter pc = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
				InChIToStructure struct = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
				IAtomContainer m = struct.getAtomContainer();
				fingerprint = pc.getBitFingerprint(m).asBitSet();						
			}else if (!smiles.equals("NA")){
				PubchemFingerprinter pc = new PubchemFingerprinter(DefaultChemObjectBuilder.getInstance());
				SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
				IAtomContainer m = sp.parseSmiles(smiles);
				fingerprint = pc.getBitFingerprint(m).asBitSet();
			}else{
				//System.err.println("Error: impossible to compute fingerprint from "+e.getId()+", no structural information available");
				fingerprint = null;
				//return null;
			}
			
		} catch (CDKException e1) {
			System.err.println("Error: impossible to compute fingerprint from "+e.getId());
			e1.printStackTrace();
			fingerprint = null;
			//return null;
		}
		return fingerprint;
	}
	
	
	/**
	 * Gets the E-state fingerprint.
	 *
	 * @param e the compound
	 * @return the E-state fingerprint
	 */
	public static BitSet getEStateFingerprint(BioMetabolite e){
		String inchi = e.getInchi();
		String smiles = e.getSmiles();
		BitSet fingerprint;
		try {
			if(!(inchi.equals("NA") || inchi.isEmpty())){
				EStateFingerprinter es = new EStateFingerprinter();
				InChIToStructure struct = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
				IAtomContainer m = struct.getAtomContainer();
				fingerprint = es.getBitFingerprint(m).asBitSet();						
			}else if (!smiles.equals("NA")){
				EStateFingerprinter es = new EStateFingerprinter();
				SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
				IAtomContainer m = sp.parseSmiles(smiles);
				fingerprint = es.getBitFingerprint(m).asBitSet();
			}else{
				//System.err.println("Error: impossible to compute fingerprint from "+e.getId()+", no structural information available");
				fingerprint = null;
				//return null;
			}
			
		} catch (CDKException e1) {
			System.err.println("Error: impossible to compute fingerprint from "+e.getId());
			e1.printStackTrace();
			fingerprint = null;
			//return null;
		}
		return fingerprint;
	}
	
	/**
	 * Gets the substructure fingerprint.
	 *
	 * @param e the compound
	 * @return the substructure fingerprint
	 */
	public static BitSet getSubstructureFingerprint(BioMetabolite e){
		String inchi = e.getInchi();
		String smiles = e.getSmiles();
		BitSet fingerprint;
		try {
			if(!(inchi.equals("NA") || inchi.isEmpty())){
				SubstructureFingerprinter sub = new SubstructureFingerprinter();
				InChIToStructure struct = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
				IAtomContainer m = struct.getAtomContainer();
				fingerprint = sub.getBitFingerprint(m).asBitSet();						
			}else if (!(smiles.equals("NA") || smiles.isEmpty())){
				SubstructureFingerprinter sub = new SubstructureFingerprinter();
				SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
				IAtomContainer m = sp.parseSmiles(smiles);
				fingerprint = sub.getBitFingerprint(m).asBitSet();
			}else{
				//System.err.println("Error: impossible to compute fingerprint from "+e.getId()+", no structural information available");
				fingerprint = null;
				//return null;
			}
			
		} catch (CDKException e1) {
			System.err.println("Error: impossible to compute fingerprint from "+e.getId());
			e1.printStackTrace();
			fingerprint = null;
			//return null;
		}
		return fingerprint;
	}
	
	/**
	 * Gets the signature.
	 *
	 * @param e the compound
	 * @return the signature
	 */
	public static MoleculeSignature getSignature(BioMetabolite e){
		String inchi = e.getInchi();
		String smiles = e.getSmiles();
		MoleculeSignature sig;
		try {
			if(!(inchi.equals("NA") || inchi.isEmpty())){
				InChIToStructure struct = InChIGeneratorFactory.getInstance().getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());
				IAtomContainer m = struct.getAtomContainer();
				sig = new MoleculeSignature (m);						
			}else if (!smiles.equals("NA")){
				SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
				IAtomContainer m = sp.parseSmiles(smiles);
				sig = new MoleculeSignature (m);
			}else{
				//System.err.println("Error: impossible to compute fingerprint from "+e.getId()+", no structural information available");
				sig = null;
				//return null;
			}
			
		} catch (CDKException e1) {
			System.err.println("Error: impossible to compute fingerprint from "+e.getId());
			e1.printStackTrace();
			sig = null;
			//return null;
		}		
		return sig;
	}	
	
}
