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
package fr.inra.toulouse.metexplore.met4j_graph.computation.algo.randomWalks;

import java.util.concurrent.Callable;

import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;

/**
 * The Class FundamentalMatrixComputor.
 * @author clement
 */
public class FundamentalMatrixComputor implements Callable<FundamentalMatrixResult>{
	
	/** The source node. */
	String x;
	
	/** The transcient matrix. */
	BioMatrix p;
	
	/** The indentity matrix. */
	BioMatrix i;
	
	/** The starting time. */
	long t0;
	
	/** The ending time. */
	long t1;
	
	/**
	 * Instantiates a new fundamental matrix computor.
	 *
	 * @param x the source node
	 * @param p2 the transcient matrix
	 * @param identity the identity matrix 
	 */
	public FundamentalMatrixComputor(String x, BioMatrix p2, BioMatrix identity){
		this.i=identity;
		this.x=x;
		this.p=p2;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public FundamentalMatrixResult call() throws Exception {
		t0 = System.nanoTime();System.out.println("\t"+x+" on "+Thread.currentThread().getName()+": computing...");
		FundamentalMatrixResult res = new FundamentalMatrixResult(x, this.getFundamentalMatrix(p,i));
		t1 = System.nanoTime();System.out.println("\t"+x+" on "+Thread.currentThread().getName()+": done ("+(((t1-t0)/1000000000))+"sec)");
		return res;
	}
	
	/**
	 * compute fundamental matrix [I-Q]^-1
	 *
	 * @param p2 the transcient matrix
	 * @param i2 the identity matrix
	 * @return the fundamental matrix
	 */
	public BioMatrix getFundamentalMatrix(BioMatrix p2, BioMatrix i2){
//		System.err.print("Computing fundamental BioMatrix...");
//		int size = q.getColumnDimension();
//		assert q.getRowDimension() == size;
		
		//create identity matrix
		
//		BioMatrix i = (new BioMatrix(0,0)).identity(size, size);
//		BlockRealMatrix i = (BlockRealMatrix) MatrixUtils.createRealIdentityMatrix(size); 
		
		//substract transient matrix to identity matrix and invert resulting matrix
		i2=i2.minus(p2);
//		i=i.subtract(q);
		
//		BioMatrix n = i.inverse();
		BioMatrix n = i2.invert();

		return n;
	}
}
