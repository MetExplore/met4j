package fr.inra.toulouse.metexplore.met4j_mathUtils.matrix;
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


import java.util.HashMap;

/*
 * TODO : 
 * I was not aware of the bi-directional maps provided by guava and apache libraries when index and labels maps were coded.
 * If some bugs or time/memory issues append, one of this bidirectional map should be used to store labels and index.
 */
/**
 * The Interface BioMatrix.
 * @author clement
 */
public interface BioMatrix {
	
	/**
	 * Sets the row label. Label has to be unique for each row
	 * @param the row index
	 * @param the label
	 */
	public void setRowLabel(int i, String label);
	
	/**
	 * Sets the column label. Label has to be unique for each column
	 * @param the column index
	 * @param the label
	 */
	public void setColumnLabel(int j, String label);
	
	/**
	 * Gets the label from corresponding row index
	 * @param the row index
	 * @return the label
	 */
	public String getRowLabel(int i);
	
	/**
	 * Gets the label from corresponding column index
	 * @param the column index
	 * @return the label
	 */
	public String getColumnLabel(int j);
	
	/**
	 * Gets the row index from corresponding label
	 * @param the row label
	 * @return the index
	 */
	public int getRowFromLabel(String rowLabel);
	
	/**
	 * Gets the column index from corresponding label
	 * @param the column label
	 * @return the index
	 */
	public int getColumnFromLabel(String columnLabel);
	
	/** Gets a copy of the label to row map. 
	 * @return the row label map
	 * */
	public HashMap<String, Integer> getRowLabelMap();
	
	/** Gets a copy of the row index to label map. 
	 * @return the row index map
	 * */
	public HashMap<Integer, String> getRowIndexMap();
	
	/** Sets the label to row index map. Automatically update row index to label map.
	 * @param the row label map
	 * */
	public void setRowLabelMap(HashMap<String, Integer> rowLabelMap);
	
	/** Sets the row index to label map. Automatically update label to row index map.
	 * @param the row index map
	 * */
	public void setRowIndexMap(HashMap<Integer, String> rowIndexMap);
	
	/** Gets a copy of the label to column index map.
	 * @return the column label map
	 * */
	public HashMap<String, Integer> getColumnLabelMap();
	
	/** Gets a copy of the column index to label map.
	 * @return the column index map
	 * */
	public HashMap<Integer, String> getColumnIndexMap();
	
	/** Sets the label to column index map. Automatically update column index to label map. 
	 * @param the column label map
	 * */
	public void setColumnLabelMap(HashMap<String, Integer> colLabelMap);
	
	/** Sets the column index to label map. Automatically update label to column index map.
	 * @param the column index map
	 * */
	public void setColumnIndexMap(HashMap<Integer, String> colIndexMap);
	
    /**
     * Gets the entry value.
     *
     * @param row the rows index
     * @param col the columns index
     * @return the value of the entry
     */
    public double get( int row , int col );

    /**
     * Sets the entry value.
     *
     * @param row the rows index
     * @param col the columns index
     * @param value the value of the entry
     */
    public void set( int row , int col , double value );

    /**
     * Get number of rows.
     *
     * @return the number of rows
     */
    public int numRows();

    /**
     * Get number of columns.
     *
     * @return the number of columns
     */
    public int numCols();
    
    /**
     * Matrix addition
     *
     * @param m the matrix to add
     * @return the result
     */
    public BioMatrix plus(BioMatrix m);
    
    /**
     * Matrix subtraction
     *
     * @param m the matrix to subtract
     * @return the result
     */
    public BioMatrix minus(BioMatrix m);
    
    /**
     * Matrix multiplication
     *
     * @param m the matrix to multiply by
     * @return the result
     */
    public BioMatrix mult(BioMatrix m);
    
    /**
     * Adds the given value to all entry
     *
     * @param value the value to add
     * @return the result
     */
    public BioMatrix add(double value );
    
    /**
     * Scale  the matrix.
     *
     * @param value the factor to scale by
     * @return the result
     */
    public BioMatrix scale(double value );
    
    /**
     * Invert matrix.
     *
     * @return the inverted matrix
     */
    public BioMatrix invert();
    
    /**
     * Build the Identity matrix.
     *
     * @return the identity matrix
     */
    public BioMatrix identity();
    
    /**
     * Transpose the matrix.
     *
     * @return the transposed matrix
     */
    public BioMatrix transpose();
    
    /**
     * Extract sub matrix.
     *
     * @param rows the rows to keep
     * @param cols the columns to keep
     * @return the sub matrix
     */
    public BioMatrix getSubMatrix(int[] rows, int[] cols);
    
    /**
     * Copy.
     *
     * @return the copy of the matrix
     */
    public BioMatrix copy();
    
    /**
     * Gets the original matrix.
     *
     * @param <T> the generic type
     * @return the original matrix
     */
    public <T>T getOriginal();
    
    /**
     * To double array.
     *
     * @return the double[][]
     */
    public double[][] toDoubleArray();
    
    /**
     * Gets the row.
     *
     * @param i the index
     * @return the row
     */
    public double[] getRow(int i);
    
    /**
     * Gets the column.
     *
     * @param j the index
     * @return the column
     */
    public double[] getCol(int j);
    
    /**
     * Gets the principal eigen vector.
     *
     * @return the principal eigen vector
     */
    public BioMatrix getPrincipalEigenVector();
    
    /**
     * Gets the column sum.
     *
     * @param j the index
     * @return the column sum
     */
    public double getColSum(int j);
    
    /**
     * Gets the row sum.
     *
     * @param i the index
     * @return the row sum
     */
    public double getRowSum(int i);

	/**
	 * Prints the matrix
	 */
	void print();
}
