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

package fr.inra.toulouse.metexplore.met4j_mathUtils.matrix;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.EVD;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.NotConvergedException;

/**
 * The Matrix Class from Mtj.
 * @author clement
 */
public class MtjMatrix implements BioMatrix{
	
	/** the label to row index map */
	private HashMap<String, Integer> rowLabelMap = new HashMap<String, Integer>();
	
	/** the row index to label map */
	private HashMap<Integer, String> rowIndexMap = new HashMap<Integer, String>();
	
	/** the label to column index map */
	private HashMap<String, Integer> columnLabelMap = new HashMap<String, Integer>();
	
	/** the column index to label map */
	private HashMap<Integer, String> columnIndexMap = new HashMap<Integer, String>();
	
	/** The mat. */
	Matrix mat;
	
	/**
	 * Instantiates a new mtj matrix.
	 *
	 * @param m the matrix
	 */
	public MtjMatrix(Matrix m) {
		this.mat = m;
	}
	
	/**
	 * Instantiates a new mtj matrix.
	 *
	 * @param m the matrix
	 */
	public MtjMatrix(BioMatrix m) {
		this.mat = new DenseMatrix(m.numRows(),m.numCols());
		for(int i=0; i<m.numRows(); i++){
			for(int j=0; j<m.numCols(); j++){
				mat.set(i,j,m.get(i, j));
			}
		}
	}
	
	/**
	 * Instantiates a new empty mtj matrix.
	 *
	 * @param numRows the number of  rows
	 * @param numCols the number of cols
	 */
	public MtjMatrix(int numRows, int numCols){
		this.mat = new DenseMatrix(numRows,numCols);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#get(int, int)
	 */
	@Override
	public double get(int row, int col) {
		 return mat.get(row,col);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#set(int, int, double)
	 */
	@Override
	public void set(int row, int col, double value) {
		mat.set(row,col,value);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#numRows()
	 */
	@Override
	public int numRows() {
		 return mat.numRows();
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#numCols()
	 */
	@Override
	public int numCols() {
		return mat.numColumns();
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#plus(parsebionet.applications.graphe.randomWalk.matrix.BioMatrix)
	 */
	@Override
	public BioMatrix plus(BioMatrix m) {
		DenseMatrix result = new DenseMatrix(m.numRows(),m.numCols());
		DenseMatrix dm = m.getOriginal();
		result=(DenseMatrix) mat.add(dm);
		return new MtjMatrix(result);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#mult(parsebionet.applications.graphe.randomWalk.matrix.BioMatrix)
	 */
	@Override
	public BioMatrix mult(BioMatrix m) {
		DenseMatrix result = new DenseMatrix(m.numRows(),m.numCols());
		DenseMatrix dm = m.getOriginal();
		mat.mult(dm,result);
		return new MtjMatrix(result);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#add(double)
	 */
	@Override
	public BioMatrix add(double value) {
		DenseMatrix result = new DenseMatrix(mat.numRows(),mat.numColumns());
		mat.add(value,result);
		return new MtjMatrix(result);
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#minus(parsebionet.applications.graphe.randomWalk.matrix.BioMatrix)
	 */
	@Override
	public BioMatrix minus(BioMatrix m) {
		return plus(m.scale(-1));
	}
	

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#scale(double)
	 */
	@Override
	public BioMatrix scale(double value) {
		DenseMatrix result = new DenseMatrix(mat);
		result.scale(value);
		return new MtjMatrix(result);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#invert()
	 */
	@Override
	public BioMatrix invert() {
		DenseMatrix I = Matrices.identity(mat.numColumns());
		DenseMatrix inv = new DenseMatrix(mat.numColumns(),mat.numColumns());
		mat.solve(I,inv);
		return new MtjMatrix(inv);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getOriginal()
	 */
	@Override
	public <T> T getOriginal() {
		return (T)mat;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#identity()
	 */
	@Override
	public BioMatrix identity() {
		DenseMatrix i = Matrices.identity(mat.numRows());
		return new MtjMatrix(i);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getSubMatrix(int[], int[])
	 */
	@Override
	public BioMatrix getSubMatrix(int[] rows, int[] cols) {
		Matrix sub = Matrices.getSubMatrix(mat, rows, cols);
		return new MtjMatrix(sub);
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#transpose()
	 */
	@Override
	public BioMatrix transpose() {
		return new MtjMatrix(mat.transpose());
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#toDoubleArray()
	 */
	@Override
	public double[][] toDoubleArray(){
		double[][] doubleArray = new double[mat.numRows()][mat.numColumns()];
		for(int i =0; i<mat.numRows(); i++){
			for(int j =0; j<mat.numColumns(); j++){
				doubleArray[i][j]=mat.get(i,j);
			}
		}
		return doubleArray;
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getPrincipalEigenVector()
	 */
	@Override
	public BioMatrix getPrincipalEigenVector() {
		EVD evd = new EVD(mat.numColumns());
		try {
			evd.factor(new DenseMatrix(mat));
		} catch (NotConvergedException e) {
			e.printStackTrace();
		}
		if(evd.hasRightEigenvectors()) return new MtjMatrix(evd.getRightEigenvectors());
		return null;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getColSum(int)
	 */
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
	@Override
	public double getRowSum(int i) {
		double sum = 0;
		for(int j=0; j<mat.numColumns(); j++){
			sum+=mat.get(i, j);
		}
		return sum;
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getRow(int)
	 */
	@Override
	public double[] getRow(int i) {
		double[] row = new double[mat.numColumns()];
		for(int j =0; j<mat.numColumns(); j++){
			row[j]=mat.get(i,j);
		}
		return row;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.matrix.BioMatrix#getCol(int)
	 */
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
	@Override
	public void print(){	
		for (int i=0; i<mat.numRows(); i++){
			for (int j=0; j<mat.numColumns(); j++){
				System.out.print(mat.get(i, j));
				if (j!=mat.numColumns()-1){
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
	@Override
	public BioMatrix copy() {
		BioMatrix copy = new MtjMatrix(this);
		return copy;
	}
	
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

	@Override
	public String getRowLabel(int i) {
		return rowIndexMap.get(i);
	}

	@Override
	public String getColumnLabel(int j) {
		return columnIndexMap.get(j);
	}

	@Override
	public int getRowFromLabel(String rowLabel) {
		return rowLabelMap.get(rowLabel);
	}

	@Override
	public int getColumnFromLabel(String columnLabel) {
		return columnLabelMap.get(columnLabel);
	}

	@Override
	public HashMap<String, Integer> getRowLabelMap() {
		return new HashMap<String, Integer>(rowLabelMap);
	}

	@Override
	public HashMap<Integer, String> getRowIndexMap() {
		return new HashMap<Integer, String>(rowIndexMap);
	}

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

	@Override
	public HashMap<String, Integer> getColumnLabelMap() {
		return new HashMap<String, Integer>(this.columnLabelMap);
	}

	@Override
	public HashMap<Integer, String> getColumnIndexMap() {
		return new HashMap<Integer, String>(this.columnIndexMap);
	}

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
