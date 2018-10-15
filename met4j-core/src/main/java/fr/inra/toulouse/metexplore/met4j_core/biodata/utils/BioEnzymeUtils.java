package fr.inra.toulouse.metexplore.met4j_core.biodata.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

public class BioEnzymeUtils {

	/**
	 * Creates an id from ids of proteins
	 * @return
	 */
	public static String createIdFromProteins(BioCollection<BioProtein> proteins) 
	{
		return proteins.getIds().stream().sorted().collect(Collectors.joining("_AND_"));
	}
	
	
}
