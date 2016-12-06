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


/**
 * Classe de création d'une archive Zip
 * à partir des fichiers du répertoire courant.
 *
 * D'après un exemple de la documentation officielle Java Sun
 * http://java.sun.com/developer/technicalArticles/Programming/compression/
 *
 * Author: Hugo ETIEVANT
 */
public class Zip {
	
	/**
	 * Taille générique du tampon en lecture et écriture
	 */
	static final int BUFFER = 2048;
	
	/*public static String unAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.DECOMP, 0); //(s, Normalizer.DECOMP, 0);
		return temp.replaceAll("[^\\p{ASCII}]","");
	}*/
	
	/**This function searches files in a directory  */
	private static ArrayList<String> showFiles(String path, String nameFile, String extension){
		
		  ArrayList<String> allFiles = new ArrayList<String>();
		  ArrayList<String> selectFiles = new ArrayList<String>();
		    getFilesRec(allFiles, path);
		  
		    for(String file : allFiles){
		    	//if(file.contains(nameFile) && file.contains(extension)){
		    		selectFiles.add(file);
		    	//}

		    }
		    return selectFiles;
		
	}
	
	
	  /**This function records found files' path in a list*/
	  private static void getFilesRec(ArrayList<String> allFiles, String root) {
		    File f = new File(root);
		    File[] listFiles = f.listFiles();
		    for (int i = 0; i < listFiles.length; i++) {
		      if (listFiles[i].isDirectory()) getFilesRec(allFiles, listFiles[i].toString());
		      else allFiles.add(listFiles[i].toString());
		    }
		  } 

//	/**
//	 * Programme principal.
//	 */
//	public static void main (String argv[]) {
//		
//   	
//		String archive = argv[0]; //nom du archive à compresser, Ex: "/home/user/arc.zip"
//   	
//   		/*************
//   		 * Compression
//   		 *************/
//		try {
//      	
//      		// création d'un flux d'écriture sur fichier
//			FileOutputStream dest = new FileOutputStream(archive);
//			
//			// calcul du checksum : Adler32 (plus rapide) ou CRC32
//			CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
//
//      		// création d'un buffer d'écriture
//			BufferedOutputStream buff = new BufferedOutputStream(checksum);
//			
//      		// création d'un flux d'écriture Zip
//			ZipOutputStream out = new ZipOutputStream(buff);
//			
//
//
//         
//         	// spécification de la méthode de compression
//			out.setMethod(ZipOutputStream.DEFLATED);
//			
//			// spécifier la qualité de la compression 0..9
//			out.setLevel(Deflater.BEST_COMPRESSION);
//			
//         
//         	// buffer temporaire des données à écriture dans le flux de sortie
//			byte data[] = new byte[BUFFER];
//         
//         
//         
//			// extraction de la liste des fichiers du répertoire courant
//			
//			ArrayList<String> allFiles = new ArrayList<String>(showFiles("/home/paula/X_Val_MBA", "*", "*"));
//			//File f = new File("/home/paula/X_Val_MBA");
//			
//			System.out.println(allFiles);
//			String files[]= new String[allFiles.size()];
//			
//			for(int j=0; j<files.length;j++){
//				files[j] = allFiles.get(j);
//				
//			}
//			
//			for(int j=0; j<files.length;j++){
//				
//				System.out.println(files[j]);
//			}
//			
//
//			// pour chacun des fichiers de la liste
//			for (int i=0; i<files.length; i++) {
//				
//				// en afficher le nom
//				System.out.println("Adding: "+files[i]);
//           
//           		// création d'un flux de lecture
//	            FileInputStream fi = new FileInputStream(files[i]);
//	            
//	            // création d'un tampon de lecture sur ce flux
//	            BufferedInputStream buffi = new BufferedInputStream(fi, BUFFER);
//	            
//	            // création d'en entrée Zip pour ce fichier
//	           // ZipEntry entry = new ZipEntry(unAccent(files[i]));
//	            ZipEntry entry = new ZipEntry(files[i]);
//	            
//	            // ajout de cette entrée dans le flux d'écriture de l'archive Zip
//	            out.putNextEntry(entry);
//	            
//	            // écriture du fichier par paquet de BUFFER octets
//	            // dans le flux d'écriture
//	            int count;
//	            while((count = buffi.read(data, 0, BUFFER)) != -1) {
//	               out.write(data, 0, count);
//				}
//	            
//				// Close the current entry
//         		out.closeEntry();
//         		
//         	    // fermeture du flux de lecture
//				buffi.close();
//			}
//			
//			
//
//			// fermeture du flux d'écriture
//			out.close();
//			buff.close();
//			checksum.close();
//			dest.close();
//
//			System.out.println("checksum: " + checksum.getChecksum().getValue());
//
//         
//		// traitement de toute exception         
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//      
//   }

}
