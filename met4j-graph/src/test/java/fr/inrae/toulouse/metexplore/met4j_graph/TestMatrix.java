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
package fr.inrae.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.MtjMatrix;

/**
 * The Matrix related classes : {@link ExportMatrix}, {@link ComputeAdjacencyMatrix}, {@link BioMatrix}
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
		BioMetabolite a = new BioMetabolite("a");graph.addVertex(a);
		BioMetabolite b = new BioMetabolite("b");graph.addVertex(b);
		BioMetabolite c = new BioMetabolite("c");graph.addVertex(c);
		BioMetabolite d = new BioMetabolite("d");graph.addVertex(d);
		BioMetabolite e = new BioMetabolite("e");graph.addVertex(e);
		BioMetabolite f = new BioMetabolite("f");graph.addVertex(f);
		BioMetabolite g = new BioMetabolite("g");graph.addVertex(g);
//		ReactionEdge aa = new ReactionEdge(a, a, new BioReaction("aa")); graph.addEdge(a, a, aa);
		ReactionEdge ab = new ReactionEdge(a, b, new BioReaction("ab")); graph.addEdge(a, b, ab);
		ReactionEdge ae = new ReactionEdge(a, e, new BioReaction("ae")); graph.addEdge(a, e, ae);
		ReactionEdge ba = new ReactionEdge(b, a, new BioReaction("ba")); graph.addEdge(b, a, ba);
//		ReactionEdge bb = new ReactionEdge(b, b, new BioReaction("bb")); graph.addEdge(b, b, bb);
		ReactionEdge be = new ReactionEdge(b, e, new BioReaction("be")); graph.addEdge(b, e, be);
//		ReactionEdge cc = new ReactionEdge(c, c, new BioReaction("cc")); graph.addEdge(c, c, cc);
		ReactionEdge cd = new ReactionEdge(c, d, new BioReaction("cd")); graph.addEdge(c, d, cd);
		ReactionEdge ce = new ReactionEdge(c, e, new BioReaction("ce")); graph.addEdge(c, e, ce);
		ReactionEdge dc = new ReactionEdge(d, c, new BioReaction("dc")); graph.addEdge(d, c, dc);
//		ReactionEdge dd = new ReactionEdge(d, d, new BioReaction("dd")); graph.addEdge(d, d, dd);
		ReactionEdge de = new ReactionEdge(d, e, new BioReaction("de")); graph.addEdge(d, e, de);
		ReactionEdge ea = new ReactionEdge(e, a, new BioReaction("ea")); graph.addEdge(e, a, ea);
		ReactionEdge eb = new ReactionEdge(e, b, new BioReaction("eb")); graph.addEdge(e, b, eb);
		ReactionEdge ec = new ReactionEdge(e, c, new BioReaction("ec")); graph.addEdge(e, c, ec);
		ReactionEdge ed = new ReactionEdge(e, d, new BioReaction("ed")); graph.addEdge(e, d, ed);
//		ReactionEdge ee = new ReactionEdge(e, e, new BioReaction("ee")); graph.addEdge(e, e, ee);
		ReactionEdge ef = new ReactionEdge(e, f, new BioReaction("ef")); graph.addEdge(e, f, ef);
		ReactionEdge eg = new ReactionEdge(e, g, new BioReaction("eg")); graph.addEdge(e, g, eg);
		ReactionEdge fe = new ReactionEdge(f, e, new BioReaction("fe")); graph.addEdge(f, e, fe);
//		ReactionEdge ff = new ReactionEdge(f, f, new BioReaction("ff")); graph.addEdge(f, f, ff);
		ReactionEdge fg = new ReactionEdge(f, g, new BioReaction("fg")); graph.addEdge(f, g, fg);
		ReactionEdge ge = new ReactionEdge(g, e, new BioReaction("ge")); graph.addEdge(g, e, ge);
		ReactionEdge gf = new ReactionEdge(g, f, new BioReaction("gf")); graph.addEdge(g, f, gf);	
//		ReactionEdge gg = new ReactionEdge(g, g, new BioReaction("gg")); graph.addEdge(g, g, gg);
	}

	
	/**
	 * Test the adjacency matrix.
	 */
	@Test
	public void testadjacencyMatrix() {
		double[][] expectedMatrix = {{0,1,0,0,1,0,0},
									{1,0,0,0,1,0,0},
									{0,0,0,1,1,0,0},
									{0,0,1,0,1,0,0},
									{1,1,1,1,0,1,1},
									{0,0,0,0,1,0,1},
									{0,0,0,0,1,1,0},};
		BioMatrix adjacency = null;
		
		for(Class<?> matrixClass : matrixClasses){
			try {
				adjacency = (new ComputeAdjacencyMatrix<BioMetabolite,ReactionEdge,CompoundGraph>(graph, matrixClass)).getadjacencyMatrix();
				//check if matrix is square
				assertEquals("error in "+ComputeAdjacencyMatrix.class.getCanonicalName()+" using "+matrixClass.getCanonicalName()+": adjacency matrix not square.",adjacency.numCols(), adjacency.numRows());
				//check matrix size
				assertEquals("error in "+ComputeAdjacencyMatrix.class.getCanonicalName()+" using "+matrixClass.getCanonicalName()+": wrong adjacency matrix size.",adjacency.numCols(), 7);
				//check matrix element
				for(int i=0; i<7; i++){
					assertArrayEquals("error in "+ComputeAdjacencyMatrix.class.getCanonicalName()+" using "+matrixClass.getCanonicalName()+": wrong element in adjacency matrix.",expectedMatrix[i], adjacency.toDoubleArray()[i], Double.MIN_VALUE);
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
			//compute adjacency matrix
			BioMatrix adjacency = null;
			try {
				adjacency = (new ComputeAdjacencyMatrix<BioMetabolite,ReactionEdge,CompoundGraph>(graph, matrixClass)).getadjacencyMatrix();
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
			
			//add self loop for example purpose, usually not allowed by CompoundGraph
			for(int i0=0; i0<adjacency.numRows();i0++){
				adjacency.set(i0, i0, 1.0);
			}
			
			//compute principal eigen vector
			BioMatrix eigen = adjacency.getPrincipalEigenVector();
			assertArrayEquals("error in "+adjacency.getClass().getCanonicalName()+" using "+matrixClass.getCanonicalName()+": element of eigen vector not as expected.",expectedEigenvector, (eigen.transpose()).toDoubleArray()[0], 0.000000000000001);
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
		ComputeAdjacencyMatrix<BioMetabolite,ReactionEdge,CompoundGraph> adj = new ComputeAdjacencyMatrix<BioMetabolite,ReactionEdge,CompoundGraph>(graph);
		Path tmpPath = null;
		try {
			tmpPath = Files.createTempFile("test_Matrix2Csv", ".csv");
		} catch (IOException e1) {
			e1.printStackTrace();
			Assert.fail("Creation of the temporary directory");
		}
		File temp = tmpPath.toFile();
		ExportMatrix.toCSV(temp.getAbsolutePath(), adj.getadjacencyMatrix());
		
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
