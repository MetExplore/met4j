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

package fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;


/**
 * provide method to export Matrix to CSV file
 * @author clement
 */
public final class ExportMatrix {

	/**
	 * Instantiates a new matrix export.
	 */
	public ExportMatrix() {}
	
	/**
	 * Prints the.
	 *
	 * @param output the output
	 * @param m the matrix
	 * @param rowMap the row map
	 * @param colMap the column map
	 */
	public static void toCSV(String output, BioMatrix m){
		try {
			File file = new File(output);
			file.createNewFile();
			PrintWriter out;
			//out = new PrintWriter(new FileWriter(file));
			out = new PrintWriter(new File(output).getAbsoluteFile()); 
			
			out.print("id,");
			for (int j=0; j<m.numCols(); j++){
				out.print(m.getColumnLabel(j));
				if (j!=m.numCols()-1){
					out.print(",");
				}else{
					out.print("\n");
				}
			}
			for (int i=0; i<m.numRows(); i++){
				out.print(m.getRowLabel(i)+",");
				for (int j=0; j<m.numCols(); j++){
					out.print(m.get(i, j));
					if (j!=m.numCols()-1){
						out.print(",");
					}else{
						out.print("\n");
					}
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Convert a matrix into a map with row label as key and a second map as value.
	 * The second map has column label as key and matrix element as value, 
	 * such as map.get(rowLabel).get(colLabel) return the element (row,col) in the matrix.
	 * @param m the matrix to convert
	 * @return the corresponding map
	 */
	public static HashMap<String,HashMap<String,Double>> matrixToMap(BioMatrix m){
		HashMap<String,HashMap<String,Double>> res = new HashMap<String,HashMap<String,Double>>();
		for(Entry<String,Integer> rowEntry : m.getRowLabelMap().entrySet()){
			HashMap<String,Double> map = new HashMap<String, Double>();
			for(Entry<String,Integer> colEntry : m.getColumnLabelMap().entrySet()){
				double value = m.get(rowEntry.getValue(), colEntry.getValue());
				map.put(colEntry.getKey(), value);
			}
			res.put(rowEntry.getKey(), map);
		}
		return res;
	}
		
}
