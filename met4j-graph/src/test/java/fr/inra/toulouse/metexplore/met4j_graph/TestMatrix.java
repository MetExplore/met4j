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
package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.computation.transform.ComputeAdjancyMatrix;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.MtjMatrix;

/**
 * The Matrix related classes : {@link ExportMatrix}, {@link ComputeAdjancyMatrix}, {@link BioMatrix}
 * @author clement
 */
public class TestMatrix {
	
	public static CompoundGraph graph;
	
	public static Class<?>[] matrixClasses = {EjmlMatrix.class, MtjMatrix.class};
	
	@BeforeClass
	public static void init(){
//		{1,1,0,0,1,0,0},
//		{1,1,0,0,1,0,0},
//		{0,0,1,1,1,0,0},
//		{0,0,1,1,1,0,0},
//		{1,1,1,1,1,1,1},
//		{0,0,0,0,1,1,1},
//		{0,0,0,0,1,1,1},
		graph = new CompoundGraph();
		BioPhysicalEntity a = new BioPhysicalEntity("a");graph.addVertex(a);
		BioPhysicalEntity b = new BioPhysicalEntity("b");graph.addVertex(b);
		BioPhysicalEntity c = new BioPhysicalEntity("c");graph.addVertex(c);
		BioPhysicalEntity d = new BioPhysicalEntity("d");graph.addVertex(d);
		BioPhysicalEntity e = new BioPhysicalEntity("e");graph.addVertex(e);
		BioPhysicalEntity f = new BioPhysicalEntity("f");graph.addVertex(f);
		BioPhysicalEntity g = new BioPhysicalEntity("g");graph.addVertex(g);
//		ReactionEdge aa = new ReactionEdge(a, a, new BioChemicalReaction("aa")); graph.addEdge(a, a, aa);
		ReactionEdge ab = new ReactionEdge(a, b, new BioChemicalReaction("ab")); graph.addEdge(a, b, ab);
		ReactionEdge ae = new ReactionEdge(a, e, new BioChemicalReaction("ae")); graph.addEdge(a, e, ae);
		ReactionEdge ba = new ReactionEdge(b, a, new BioChemicalReaction("ba")); graph.addEdge(b, a, ba);
//		ReactionEdge bb = new ReactionEdge(b, b, new BioChemicalReaction("bb")); graph.addEdge(b, b, bb);
		ReactionEdge be = new ReactionEdge(b, e, new BioChemicalReaction("be")); graph.addEdge(b, e, be);
//		ReactionEdge cc = new ReactionEdge(c, c, new BioChemicalReaction("cc")); graph.addEdge(c, c, cc);
		ReactionEdge cd = new ReactionEdge(c, d, new BioChemicalReaction("cd")); graph.addEdge(c, d, cd);
		ReactionEdge ce = new ReactionEdge(c, e, new BioChemicalReaction("ce")); graph.addEdge(c, e, ce);
		ReactionEdge dc = new ReactionEdge(d, c, new BioChemicalReaction("dc")); graph.addEdge(d, c, dc);
//		ReactionEdge dd = new ReactionEdge(d, d, new BioChemicalReaction("dd")); graph.addEdge(d, d, dd);
		ReactionEdge de = new ReactionEdge(d, e, new BioChemicalReaction("de")); graph.addEdge(d, e, de);
		ReactionEdge ea = new ReactionEdge(e, a, new BioChemicalReaction("ea")); graph.addEdge(e, a, ea);
		ReactionEdge eb = new ReactionEdge(e, b, new BioChemicalReaction("eb")); graph.addEdge(e, b, eb);
		ReactionEdge ec = new ReactionEdge(e, c, new BioChemicalReaction("ec")); graph.addEdge(e, c, ec);
		ReactionEdge ed = new ReactionEdge(e, d, new BioChemicalReaction("ed")); graph.addEdge(e, d, ed);
//		ReactionEdge ee = new ReactionEdge(e, e, new BioChemicalReaction("ee")); graph.addEdge(e, e, ee);
		ReactionEdge ef = new ReactionEdge(e, f, new BioChemicalReaction("ef")); graph.addEdge(e, f, ef);
		ReactionEdge eg = new ReactionEdge(e, g, new BioChemicalReaction("eg")); graph.addEdge(e, g, eg);
		ReactionEdge fe = new ReactionEdge(f, e, new BioChemicalReaction("fe")); graph.addEdge(f, e, fe);
//		ReactionEdge ff = new ReactionEdge(f, f, new BioChemicalReaction("ff")); graph.addEdge(f, f, ff);
		ReactionEdge fg = new ReactionEdge(f, g, new BioChemicalReaction("fg")); graph.addEdge(f, g, fg);
		ReactionEdge ge = new ReactionEdge(g, e, new BioChemicalReaction("ge")); graph.addEdge(g, e, ge);
		ReactionEdge gf = new ReactionEdge(g, f, new BioChemicalReaction("gf")); graph.addEdge(g, f, gf);	
//		ReactionEdge gg = new ReactionEdge(g, g, new BioChemicalReaction("gg")); graph.addEdge(g, g, gg);
	}

	
	/**
	 * Test the adjancy matrix.
	 */
	@Test
	public void testAdjancyMatrix() {
		double[][] expectedMatrix = {{0,1,0,0,1,0,0},
									{1,0,0,0,1,0,0},
									{0,0,0,1,1,0,0},
									{0,0,1,0,1,0,0},
									{1,1,1,1,0,1,1},
									{0,0,0,0,1,0,1},
									{0,0,0,0,1,1,0},};
		BioMatrix adjancy = null;
		
		for(Class<?> matrixClass : matrixClasses){
			try {
				adjancy = (new ComputeAdjancyMatrix<BioPhysicalEntity,ReactionEdge,CompoundGraph>(graph, matrixClass)).getAdjancyMatrix();
				//check if matrix is square
				assertEquals("error in "+ComputeAdjancyMatrix.class.getCanonicalName()+" using "+matrixClass.getCanonicalName()+": adjancy matrix not square.",adjancy.numCols(), adjancy.numRows());
				//check matrix size
				assertEquals("error in "+ComputeAdjancyMatrix.class.getCanonicalName()+" using "+matrixClass.getCanonicalName()+": wrong adjancy matrix size.",adjancy.numCols(), 7);
				//check matrix element
				for(int i=0; i<7; i++){
					assertArrayEquals("error in "+ComputeAdjancyMatrix.class.getCanonicalName()+" using "+matrixClass.getCanonicalName()+": wrong element in adjancy matrix.",expectedMatrix[i], adjancy.toDoubleArray()[i], Double.MIN_VALUE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}	
		}
	}
	
	/**
	 * Test the eigen.
	 */
	@Test
	public void testEigen(){
		double[] expectedEigenvector = {0.3162277660168381, 0.3162277660168376, 0.316227766016838, 0.316227766016838, 0.6324555320336759, 0.316227766016838, 0.316227766016838};
		
		for(Class<?> matrixClass : matrixClasses){
			//compute adjancy matrix
			BioMatrix adjancy = null;
			try {
				adjancy = (new ComputeAdjancyMatrix<BioPhysicalEntity,ReactionEdge,CompoundGraph>(graph, matrixClass)).getAdjancyMatrix();
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
			
			//add self loop for example purpose, usually not allowed by CompoundGraph
			for(int i0=0; i0<adjancy.numRows();i0++){
				adjancy.set(i0, i0, 1.0);
			}
			
			//compute principal eigen vector
			BioMatrix eigen = adjancy.getPrincipalEigenVector();
			assertArrayEquals("error in "+adjancy.getClass().getCanonicalName()+" using "+matrixClass.getCanonicalName()+": element of eigen vector not as expected.",expectedEigenvector, (eigen.transpose()).toDoubleArray()[0], 0.000000000000001);
		}
	}
	
	@Test
	public void testLabelToIndex(){
		//create empty matrix
		BioMatrix m = new EjmlMatrix(5, 4);
		
		// set labels
		m.setColumnLabel(0, "a");
		m.setColumnLabel(1, "b");
		m.setColumnLabel(2, "c");
		m.setColumnLabel(3, "d");
		m.setRowLabel(0, "1");
		m.setRowLabel(1, "2");
		m.setRowLabel(2, "3");
		m.setRowLabel(3, "4");
		m.setRowLabel(4, "5");
		Map<String,Integer> rowLabelMap = m.getRowLabelMap();
		Map<String,Integer> colLabelMap = m.getColumnLabelMap();
		Map<Integer,String> rowIndexMap = m.getRowIndexMap();
		Map<Integer,String> colIndexMap = m.getColumnIndexMap();
		
		//test column index to label 
		assertEquals("a", m.getColumnLabel(0));
		assertEquals("b", m.getColumnLabel(1));
		assertEquals("c", m.getColumnLabel(2));
		assertEquals("d", m.getColumnLabel(3));
		assertEquals("a", colIndexMap.get(0));
		assertEquals("b", colIndexMap.get(1));
		assertEquals("c", colIndexMap.get(2));
		assertEquals("d", colIndexMap.get(3));
		
		//test label to column index 
		assertEquals(0, m.getColumnFromLabel("a"));
		assertEquals(1, m.getColumnFromLabel("b"));
		assertEquals(2, m.getColumnFromLabel("c"));
		assertEquals(3, m.getColumnFromLabel("d"));
		assertEquals(0, colLabelMap.get("a").intValue());
		assertEquals(1, colLabelMap.get("b").intValue());
		assertEquals(2, colLabelMap.get("c").intValue());
		assertEquals(3, colLabelMap.get("d").intValue());
		
		//test row index to label 
		assertEquals("1", m.getRowLabel(0));
		assertEquals("2", m.getRowLabel(1));
		assertEquals("3", m.getRowLabel(2));
		assertEquals("4", m.getRowLabel(3));
		assertEquals("5", m.getRowLabel(4));
		assertEquals("1", rowIndexMap.get(0));
		assertEquals("2", rowIndexMap.get(1));
		assertEquals("3", rowIndexMap.get(2));
		assertEquals("4", rowIndexMap.get(3));
		assertEquals("5", rowIndexMap.get(4));
		
		//test label to row index 
		assertEquals(0, m.getRowFromLabel("1"));
		assertEquals(1, m.getRowFromLabel("2"));
		assertEquals(2, m.getRowFromLabel("3"));
		assertEquals(3, m.getRowFromLabel("4"));
		assertEquals(4, m.getRowFromLabel("5"));
		assertEquals(0, rowLabelMap.get("1").intValue());
		assertEquals(1, rowLabelMap.get("2").intValue());
		assertEquals(2, rowLabelMap.get("3").intValue());
		assertEquals(3, rowLabelMap.get("4").intValue());
		assertEquals(4, rowLabelMap.get("5").intValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetLabelOnInvalidRowIndex(){
		//create empty matrix
		BioMatrix m = new EjmlMatrix(2, 2);
		//set label on out of bound index
		m.setRowLabel(3, "42");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetLabelOnInvalidColIndex(){
		//create empty matrix
		BioMatrix m = new EjmlMatrix(2, 2);
		//set label on out of bound index
		m.setColumnLabel(3, "42");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInvalidLabelOnRowIndex(){
		//create empty matrix
		BioMatrix m = new EjmlMatrix(2, 2);
		//set label
		m.setRowLabel(0, "42");
		//set already used label
		m.setRowLabel(1, "42");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSetInvalidLabelOnColIndex(){
		//create empty matrix
		BioMatrix m = new EjmlMatrix(2, 2);
		//set label
		m.setColumnLabel(0, "42");
		//set already used label
		m.setColumnLabel(1, "42");
	}
	
	/**
	 * Test the matrix2 csv.
	 */
	@Test
	public void testMatrix2CSV() {
		ComputeAdjancyMatrix<BioPhysicalEntity,ReactionEdge,CompoundGraph> adj = new ComputeAdjancyMatrix<BioPhysicalEntity,ReactionEdge,CompoundGraph>(graph);
		Path tmpPath = null;
		try {
			tmpPath = Files.createTempFile("test_Matrix2Csv", ".csv");
		} catch (IOException e1) {
			e1.printStackTrace();
			Assert.fail("Creation of the temporary directory");
		}
		File temp = tmpPath.toFile();
		ExportMatrix.toCSV(temp.getAbsolutePath(), adj.getAdjancyMatrix());
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int nbLine = 0;
		try {
			br = new BufferedReader(new FileReader(temp));
			while ((line = br.readLine()) != null) {
				if(!line.matches("^\\s*$")) nbLine++;
				String[] array = line.split(cvsSplitBy);
				assertEquals("Error in Matrix export : wrong number of columns", graph.vertexSet().size()+1,array.length);
			}
			assertEquals("Error in Matrix export : wrong number of rows", graph.vertexSet().size()+1,nbLine);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			if (br != null) {
				try {
					br.close();
					Files.delete(tmpPath);
				} catch (IOException e) {
					fail();
					e.printStackTrace();
				}
			}
		}
	}
}
