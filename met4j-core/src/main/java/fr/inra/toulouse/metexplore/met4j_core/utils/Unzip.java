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
/**
 * 5 juil. 2012 
 */
package fr.inra.toulouse.metexplore.met4j_core.utils;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.Enumeration;

/**
 * Classe de création d'une archive Zip
 * à partir des fichiers du répertoire courant.
 *
 * D'après un exemple de la documentation officielle Java Sun
 * http://java.sun.com/developer/technicalArticles/Programming/compression/
 *
 * Author: Hugo ETIEVANT
 */


public class Unzip {
	
	/**
	 * Taille générique du tampon en lecture et écriture
	 */
	static final int BUFFER = 2048;
	
	
	/**
	 * Programme principal.
	 */
	public static void main (String argv[]) {
   	
   	
      
      
		String archive = argv[0]; //nom du archive à compresser, Ex: "/home/user/arc.zip"
      
   		/*************
   		 * Décompression
   		 *************/
   		 
		try {
			
			// fichier destination
			BufferedOutputStream dest = null;
			
			// ouverture fichier entrée
			FileInputStream fis = new FileInputStream(archive);
			
			// ouverture fichier de buffer
			BufferedInputStream buffi = new BufferedInputStream(fis);
			
			// ouverture archive Zip d'entrée
			ZipInputStream zis = new ZipInputStream(buffi);
			
			// entrée Zip
			ZipEntry entry;

			// parcours des entrées de l'archive
			while((entry = zis.getNextEntry()) != null) {
				
				// affichage du nom de l'entrée
				System.out.println("Extracting: " +entry);
				
				int count;
				byte data[] = new byte[BUFFER];
				
				// création fichier
				FileOutputStream fos = new FileOutputStream(entry.getName());
				
				// affectation buffer de sortie
				dest = new BufferedOutputStream(fos, BUFFER);
				
				// écriture sur disque
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				
				// vidage du tampon
				dest.flush();
				
				// fermeture fichier
				dest.close();
			}
			
			// fermeture archive
			zis.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
      
      
	}

}
