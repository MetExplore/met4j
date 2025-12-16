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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.ejml.alg.dense.decomposition.eig.SwitchingEigenDecomposition;
import org.ejml.simple.SimpleMatrix;

/**
 * The Matrix Class from Ejml.
 *
 * @author clement
 */
public class EjmlMatrix implements BioMatrix{
	
	/** the label to row index map */
	private HashMap<String, Integer> rowLabelMap = new HashMap<String, Integer>();
	
	/** the row index to label map */
	private HashMap<Integer, String> rowIndexMap = new HashMap<Integer, String>();
	
	/** the label to column index map */
	private HashMap<String, Integer> columnLabelMap = new HashMap<String, Integer>();
	
	/** the column index to label map */
	private HashMap<Integer, String> columnIndexMap = new HashMap<Integer, String>();
	
	/** The matrix */
	SimpleMatrix mat;
	
	/**
	 * Instantiates a new empty ejml matrix.
	 *
	 * @param m the matrix
	 */
	public EjmlMatrix(SimpleMatrix m) {
		this.mat = m;
	}
	
	/**
	 * Instantiates a new ejml matrix.
	 *
	 * @param m the matrix
	 */
	public EjmlMatrix(BioMatrix m) {
		this.mat = new SimpleMatrix(m.numRows(),m.numCols());
		for(int i=0; i<m.numRows(); i++){
			for(int j=0; j<m.numCols(); j++){
				mat.set(i,j,m.get(i, j));
			}
		}
		this.columnLabelMap = m.getColumnLabelMap();
		this.columnIndexMap = m.getColumnIndexMap();
		this.rowLabelMap = m.getRowLabelMap();
		this.rowIndexMap = m.getRowIndexMap();
	}
	
	/**
	 * Instantiates a new ejml matrix.
	 *
	 * @param numRows the number of rows
	 * @param numCols the number of columns
	 */
	public EjmlMatrix(int numRows, int numCols){
		this.mat = new SimpleMatrix(numRows,numCols);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#get(int, int)
	 */
	/** {@inheritDoc} */
	@Override
	public double get(int row, int col) {
		return mat.get(row, col);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#set(int, int, double)
	 */
	/** {@inheritDoc} */
	@Override
	public void set(int row, int col, double value) {
		mat.set(row,col,value);
		
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#numRows()
	 */
	/** {@inheritDoc} */
	@Override
	public int numRows() {
		return mat.numRows();
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#numCols()
	 */
	/** {@inheritDoc} */
	@Override
	public int numCols() {
		return mat.numCols();
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#plus(parsebionet.applications.graphe.randomWalk.matrix.BioMatrix)
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix plus(BioMatrix m) {
		SimpleMatrix b = m.getOriginal();
		return new EjmlMatrix(mat.plus(b));
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#minus(parsebionet.applications.graphe.randomWalk.matrix.BioMatrix)
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix minus(BioMatrix m) {
		SimpleMatrix b = m.getOriginal();
		return new EjmlMatrix(mat.minus(b));
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#mult(parsebionet.applications.graphe.randomWalk.matrix.BioMatrix)
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix mult(BioMatrix m) {
		SimpleMatrix b = m.getOriginal();
		return new EjmlMatrix(mat.mult(b));
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#add(double)
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix add(double value) {
		return new EjmlMatrix(mat.plus(value));
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#scale(double)
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix scale(double value) {
		return new EjmlMatrix(mat.scale(value));
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#invert()
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix invert() {
		SimpleMatrix inv = mat.invert();
		return new EjmlMatrix(inv);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#identity()
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix identity() {
		SimpleMatrix i = SimpleMatrix.identity(mat.numRows());
		return new EjmlMatrix(i);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getSubMatrix(int[], int[])
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix getSubMatrix(int[] rows, int[] cols) {
		SimpleMatrix sub = new SimpleMatrix(rows.length, cols.length);
		HashMap<Integer,String> subColumnIndexMap = new HashMap<>();
		HashMap<String,Integer> subColumnLabelMap = new HashMap<>();
		HashMap<Integer,String> subRowIndexMap = new HashMap<>();
		HashMap<String,Integer> subRowLabelMap = new HashMap<>();
		for(int i=0; i<rows.length; i++){
			subRowIndexMap.put(i, this.getRowLabel(rows[i]));
			subRowLabelMap.put(this.getRowLabel(rows[i]),i);
			for(int j=0; j<cols.length; j++){
				sub.set(i, j, mat.get(rows[i], cols[j]));
				subColumnIndexMap.put(j, this.getColumnLabel(cols[j]));
				subColumnLabelMap.put(this.getColumnLabel(cols[j]),j);
			}
		}
		BioMatrix subM = new EjmlMatrix(sub);
		subM.setColumnIndexMap(subColumnIndexMap);
		subM.setColumnLabelMap(subColumnLabelMap);
		subM.setRowIndexMap(subRowIndexMap);
		subM.setRowLabelMap(subRowLabelMap);

		System.out.println(subColumnIndexMap);
		System.out.println(subColumnLabelMap);
		System.out.println(subRowIndexMap);
		System.out.println(subRowLabelMap);
		return subM;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getOriginal()
	 */
	/** {@inheritDoc} */
	@Override
	public <T> T getOriginal() {
		return (T)mat;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#transpose()
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix transpose() {
		return new EjmlMatrix(mat.transpose());
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getPrincipalEigenVector()
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix getPrincipalEigenVector() {
		SwitchingEigenDecomposition eigenD = new SwitchingEigenDecomposition(mat.numRows(), true, Double.MIN_VALUE);
		eigenD.decompose(mat.getMatrix());
		return new EjmlMatrix(new SimpleMatrix(eigenD.getEigenVector(0)));
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#toDoubleArray()
	 */
	/** {@inheritDoc} */
	@Override
	public double[][] toDoubleArray(){
		double[][] doubleArray = new double[mat.numRows()][mat.numCols()];
		for(int i =0; i<mat.numRows(); i++){
			for(int j =0; j<mat.numCols(); j++){
				doubleArray[i][j]=mat.get(i,j);
			}
		}
		return doubleArray;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getColSum(int)
	 */
	/** {@inheritDoc} */
	@Override
	public double getColSum(int j) {
		double sum = 0;
		for(int i=0; i<mat.numRows(); i++){
			sum+=mat.get(i, j);
		}
		return sum;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getRowSum(int)
	 */
	/** {@inheritDoc} */
	@Override
	public double getRowSum(int i) {
		double sum = 0;
		for(int j=0; j<mat.numCols(); j++){
			sum+=mat.get(i, j);
		}
		return sum;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getRow(int)
	 */
	/** {@inheritDoc} */
	@Override
	public double[] getRow(int i) {
		double[] row = new double[mat.numCols()];
		for(int j =0; j<mat.numCols(); j++){
			row[j]=mat.get(i,j);
		}
		return row;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getCol(int)
	 */
	/** {@inheritDoc} */
	@Override
	public double[] getCol(int j) {
		double[] col = new double[mat.numRows()];
		for(int i =0; i<mat.numRows(); i++){
			col[i]=mat.get(i,j);
		}
		return col;
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#print()
	 */
	/** {@inheritDoc} */
	@Override
	public void print(){	
		for (int i=0; i<mat.numRows(); i++){
			for (int j=0; j<mat.numCols(); j++){
				System.out.print(mat.get(i, j));
				if (j!=mat.numCols()-1){
					System.out.print(",");
				}else{
					System.out.print("\n");
				}
			}
		}
		System.out.print("\n");
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#copy()
	 */
	/** {@inheritDoc} */
	@Override
	public BioMatrix copy() {
		BioMatrix copy = new EjmlMatrix(this);
		return copy;
	}

	/** {@inheritDoc} */
	@Override
	public void setRowLabel(int i, String label) {
		//checking :
		//if index out of matrix boundaries, throw exception.
		if(i<0 || i>=numRows()) throw new IllegalArgumentException("index out of matrix");
		//if label already used, throw exception.
		if(rowLabelMap.containsKey(label))
			throw new IllegalArgumentException("label must be unique for each row");
		
		//set index to label and label to index maps.
		rowLabelMap.put(label, i);
		rowIndexMap.put(i, label);
	}

	/** {@inheritDoc} */
	@Override
	public void setColumnLabel(int j, String label) {
		//checking :
		//if index out of matrix boundaries, throw exception.
		if(j<0 || j>=numCols()) throw new IllegalArgumentException("index out of matrix");
		//if label already used, throw exception.
		if(columnLabelMap.containsKey(label))
			throw new IllegalArgumentException("label must be unique for each row");
		
		//set index to label and label to index maps.
		columnLabelMap.put(label, j);
		columnIndexMap.put(j, label);
	}

	/** {@inheritDoc} */
	@Override
	public String getRowLabel(int i) {
		return rowIndexMap.get(i);
	}

	/** {@inheritDoc} */
	@Override
	public String getColumnLabel(int j) {
		return columnIndexMap.get(j);
	}

	/** {@inheritDoc} */
	@Override
	public int getRowFromLabel(String rowLabel) {
		return rowLabelMap.get(rowLabel);
	}

	/** {@inheritDoc} */
	@Override
	public int getColumnFromLabel(String columnLabel) {
		return columnLabelMap.get(columnLabel);
	}

	/** {@inheritDoc} */
	@Override
	public HashMap<String, Integer> getRowLabelMap() {
		return new HashMap<String, Integer>(rowLabelMap);
	}

	/** {@inheritDoc} */
	@Override
	public HashMap<Integer, String> getRowIndexMap() {
		return new HashMap<Integer, String>(rowIndexMap);
	}

	/** {@inheritDoc} */
	@Override
	public void setRowLabelMap(HashMap<String, Integer> rowLabelMap) {
		//check no duplicate in values
		HashSet<Integer> testSet =  new HashSet<Integer>(rowLabelMap.values());
		if(testSet.size()!=rowLabelMap.values().size()) throw new IllegalArgumentException("duplicate index in row label map");
				
		//set new label to index map
		this.rowLabelMap=rowLabelMap;
		//update index to label map
		for(Entry<String, Integer> rowLabelEntry : rowLabelMap.entrySet()){
			rowIndexMap.put(rowLabelEntry.getValue(), rowLabelEntry.getKey());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setRowIndexMap(HashMap<Integer, String> rowIndexMap) {
		//check no duplicate in values
		HashSet<String> testSet =  new HashSet<String>(rowIndexMap.values());
		if(testSet.size()!=rowIndexMap.values().size()) throw new IllegalArgumentException("duplicate label in row index map");
		
		//set new index to label map
		this.rowIndexMap=rowIndexMap;
		//update label to index map
		for(Entry<Integer, String> rowIndexEntry : rowIndexMap.entrySet()){
			rowLabelMap.put(rowIndexEntry.getValue(), rowIndexEntry.getKey());
		}
	}

	/** {@inheritDoc} */
	@Override
	public HashMap<String, Integer> getColumnLabelMap() {
		return new HashMap<String, Integer>(this.columnLabelMap);
	}

	/** {@inheritDoc} */
	@Override
	public HashMap<Integer, String> getColumnIndexMap() {
		return new HashMap<Integer, String>(this.columnIndexMap);
	}

	/** {@inheritDoc} */
	@Override
	public void setColumnLabelMap(HashMap<String, Integer> colLabelMap) {
		//check no duplicate in values
		HashSet<Integer> testSet =  new HashSet<Integer>(colLabelMap.values());
		if(testSet.size()!=colLabelMap.values().size()) throw new IllegalArgumentException("duplicate index in column label map");
		
		//set new label to index map
		this.columnLabelMap=colLabelMap;
		//update index to label map
		for(Entry<String, Integer> columnLabelEntry : columnLabelMap.entrySet()){
			columnIndexMap.put(columnLabelEntry.getValue(), columnLabelEntry.getKey());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setColumnIndexMap(HashMap<Integer, String> colIndexMap) {
		//check no duplicate in values
		HashSet<String> testSet =  new HashSet<String>(colIndexMap.values());
		if(testSet.size()!=colIndexMap.values().size()) throw new IllegalArgumentException("duplicate label in column index map");
		
		//set new index to label map
		this.columnIndexMap=colIndexMap;
		//update label to index map
		for(Entry<Integer, String> columnIndexEntry : columnIndexMap.entrySet()){
			columnLabelMap.put(columnIndexEntry.getValue(), columnIndexEntry.getKey());
		}
	}

}
