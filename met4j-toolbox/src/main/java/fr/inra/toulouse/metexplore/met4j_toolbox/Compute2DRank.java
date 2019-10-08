package fr.inra.toulouse.metexplore.met4j_toolbox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity.FingerprintBuilder;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_core.io.RefHandler;
import fr.inra.toulouse.metexplore.met4j_core.io.Sbml2BioNetworkLite;
import fr.inra.toulouse.metexplore.met4j_graph.computation.algo.EigenVectorCentrality;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.RankUtils;
import fr.inra.toulouse.metexplore.met4j_graph.computation.transform.GraphFilter;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.*;
import fr.inra.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;

/**
 * 
 * @author clement
 *
 */
public class Compute2DRank {
	
	String seedsFilePath;

	private BioNetwork model;
	CompoundGraph firstGraph;
	CompoundGraph reverseGraph;
	HashMap<String,Double> seeds;
	
	int maxNbOfIter = 15000;
	double tolerance = 0.001;
	double dampingFactor = 0.8;
	
	HashMap<String, Double> pageRankScore;
	HashMap<String, Double> cheiRankScore;
	HashMap<String, Integer> pageRank;
	HashMap<String, Integer> cheiRank;
	
	HashMap<String, Double> globalPageRankScore;
	HashMap<String, Double> globalCheiRankScore;
	HashMap<String, Integer> globalPageRank;
	HashMap<String, Integer> globalCheiRank;

	//Scores normalisés
	private HashMap<String, Double> globalVsPersonalizedPageRank;
	private HashMap<String, Double> globalVsPersonalizedCheiRank;


	public BioNetwork getModel() {
		return model;
	}

	public void setModel(BioNetwork model) {
		this.model = model;
	}

	public HashMap<String, Double> getGlobalVsPersonalizedPageRank() {
		return globalVsPersonalizedPageRank;
	}

	public void setGlobalVsPersonalizedPageRank(HashMap<String, Double> globalVsPersonalizedPageRank) {
		this.globalVsPersonalizedPageRank = globalVsPersonalizedPageRank;
	}

	public HashMap<String, Double> getGlobalVsPersonalizedCheiRank() {
		return globalVsPersonalizedCheiRank;
	}

	public void setGlobalVsPersonalizedCheiRank(HashMap<String, Double> globalVsPersonalizedCheiRank) {
		this.globalVsPersonalizedCheiRank = globalVsPersonalizedCheiRank;
	}

	public HashMap<String, Double> getPageRankScore() {
		return pageRankScore;
	}

	public HashMap<String, Double> getCheiRankScore() {
		return cheiRankScore;
	}

	/**
	 * TODO : add custom parameters
	 * @param sbmlFilePath path to the sbml file
	 * @param seedsFilePath path to the seeds file
	 * @param edgeWeightsFilePaths path to the edge weight file
	 */
	public Compute2DRank(String sbmlFilePath, String seedsFilePath, String edgeWeightsFilePaths){
		
		model = importModel(sbmlFilePath);
		
		createCompoundGraph(model);
		setEdgeWeights(firstGraph, edgeWeightsFilePaths);
		
		createEdgeReversedGraph();
		
		turnWeightsIntoProba(firstGraph);
		System.err.println("transition probabilities computed");
		turnWeightsIntoProba(reverseGraph);
		System.err.println("transition probabilities computed (reverse graph)");
		
		this.seedsFilePath=seedsFilePath;
		importSeeds();
		
	}
	
	public Compute2DRank(BioNetwork model, String seedsFilePath, String edgeWeightsFilePaths){
		
		this.model = model;
		
		createCompoundGraph(model);
		setEdgeWeights(firstGraph, edgeWeightsFilePaths);
		
		createEdgeReversedGraph();
		
		turnWeightsIntoProba(firstGraph);
		System.err.println("transition probabilities computed");
		turnWeightsIntoProba(reverseGraph);
		System.err.println("transition probabilities computed (reverse graph)");
		
		this.seedsFilePath=seedsFilePath;
		importSeeds();
	}
	
	
	
	/*
	 * CREATE MODEL FROM SBML FILE
	 * use default parameters for attributes value extraction from notes.
	 * 
	 */
	private BioNetwork importModel(String sbmlFilePath){
		Sbml2BioNetworkLite in = new Sbml2BioNetworkLite(sbmlFilePath, true);
		in.addRefHandlers(RefHandler.HMDB_HANDLER);
		in.setNotesValueSeparator(" || ");
		BioNetwork model = in.getBioNetwork();
		System.err.println("model imported.");
		return model;
	}
	
	
	
	/*
	 * CREATE COMPOUND GRAPH
	 * 
	 * create a compound-graph object from a model : each reaction is split in transition edges
	 *	 example:		reaction in bn:			A + B -> C + D
	 *	 edges in cpd-graph:		A -> C ; A -> D ; B -> C ; B -> D
	 * 
	 */
	private void createCompoundGraph(BioNetwork model){
		firstGraph = new Bionetwork2BioGraph(model).getCompoundGraph();
		System.err.println("compound graph created.");
		return;
	}
	
	
	
	/*
	 *  CREATE EDGE REVERSED GRAPH
	 *  
	 *  create a graph g' from this graph g where for each edge e(x,y) in g their exist an edge e'(y,x) in g'
	 *  This one will be used for chei rank computation.
	 *  /!\ Probabilities have to be re-computed otherwise outgoing weights won't sum up to 1 
	 */
	private void createEdgeReversedGraph(){
		GraphFactory<BioPhysicalEntity, ReactionEdge, CompoundGraph> factory = new GraphFactory<BioPhysicalEntity, ReactionEdge, CompoundGraph>() {
			@Override
			public CompoundGraph createGraph() {
				return new CompoundGraph();
			}
		};
		reverseGraph = factory.reverse(firstGraph);
		System.err.println("reverse graph created.");
		return;
	}
	
	
	
	/*	COMPUTE WEIGHTS
	 * 
	 *  add a weight for each edges, according to a selected criteria (chemical similarity, atom conservation, target's degree ...)
	 *	this weight will be used for probability computation
	 *
	 *  using chemical similarity :
	 *  
	 *		- for each compound convert chemical structure information to bit-vector fingerprint
	 *				each bit represent a structural sub-structure, the value if the sub-structure is present in the molecule
	 *				fingerprint type available : MACCS, KlekotaRoth, ExtendedFingerprint (default), PubchemFingerprint, EStateFingerprint...
	 *		- for each edge compute the fingerprint similarity between source and target (using Tanimoto coef)
	 *		- nodes without known chemical structure are removed
	 *
	 *	using weights from file :
	 *		- import weights from tab separated file using the following format:
	 *			source-node-id	target-node-id	reaction-id	weight-as-double
	 *		- remove edges with weight equals to 0 or NaN
	 *		- remove resulting isolated nodes
	 *
	 */
	public void setSimilarityWeights(CompoundGraph graph){
		SimilarityWeightPolicy wp = new SimilarityWeightPolicy(FingerprintBuilder.EXTENDED, false, false);
		wp.setWeight(graph);
		WeightUtils.removeEdgeWithNaNWeight(graph);
		wp.noStructFilter(graph);
		System.err.println("weights computed.");
		return;
	}
	
	public void setEdgeWeights(CompoundGraph graph, String localFilePath){
		//import weights from file
		WeightsFromFile<BioPhysicalEntity, ReactionEdge, CompoundGraph> wp = new WeightsFromFile<BioPhysicalEntity, ReactionEdge, CompoundGraph>(localFilePath, true);
		//set weights to edges
		wp.setWeight(graph);
		//remove weights below 0.0
		int nb = GraphFilter.weightFilter(graph, 0.0, "<="); System.err.println(nb+" edges removed");
		//remove edges without NaN weight
		WeightUtils.removeEdgeWithNaNWeight(graph);
		//remove disconnected nodes
		graph.removeIsolatedNodes();
		System.err.println("weights computed.");
		return;
	}
	
	
	/*  COMPUTE TRANSITIONS PROBABILITIES
	 * 
	 * 	turn weights into probabilities (the higher is the weight, the higher is the probability)
	 * 		- this probability replace the first weighting
	 * 		- for a given vertex, the sum of its outgoing edges' weight is 1
	 */
	public void turnWeightsIntoProba(CompoundGraph graph){
		ReactionProbabilityWeight pp = new ReactionProbabilityWeight();
		pp.computeProba(graph);
		return;
	}
	
	
	/*  IMPORT SEEDS FROM FILE
	 * 
	 *  create the personalized vector, where each position correspond to a vertex.
	 *  Seeds entries haves 1/(nb of seeds) values, other nodes haves 0 values. 
	 * 
	 */
	public void importSeeds(){
		seeds = new HashMap<String, Double>();
		HashMap<String, Double> tmpSeeds = new HashMap<>();
		Double somme = 0.0;
		try {
			BufferedReader file = new BufferedReader(new FileReader(seedsFilePath));
			String line;

			while((line = file.readLine())!= null){
				String[] splitLine = line.split("\t");
				String node = splitLine[0];
				if(!firstGraph.hasVertex(node)){
					System.err.println(node+" not found in graph!");
				}else{
					Double weight = 0.0;
					if(splitLine.length>1)
						weight = Double.parseDouble(splitLine[1]);

					tmpSeeds.put(node, weight);
					somme+=weight;
				}
			}
			file.close();  
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(somme==0.0)
		{
			for(String node : tmpSeeds.keySet()){
				seeds.put(node, 1.0D/tmpSeeds.size());
			}
		}
		else
		{
			for(String node : tmpSeeds.keySet()){
				seeds.put(node, tmpSeeds.get(node)/somme);
			}
		}
		
		System.err.println("seeds file imported");
		System.err.println(seeds.size()+" seeds");	
		return;
	}
	
	public void compute(){
		
		globalPageRankScore = computeScore(firstGraph, dampingFactor, maxNbOfIter, tolerance);
		globalPageRank = getRankFromScore(globalPageRankScore, null);
		normalizeScore(globalPageRankScore);
		System.err.println("global pageRank computed");
		
		globalCheiRankScore = computeScore(reverseGraph, dampingFactor, maxNbOfIter, tolerance);
		globalCheiRank = getRankFromScore(globalCheiRankScore, null);
		normalizeScore(globalCheiRankScore);
		System.err.println("global cheiRank computed");
		
		pageRankScore = computeScore(firstGraph, dampingFactor, seeds, maxNbOfIter, tolerance);
		pageRank = getRankFromScore(pageRankScore,seeds.keySet());
		normalizeScore(pageRankScore);
		System.err.println("pageRank computed");
		
		cheiRankScore = computeScore(reverseGraph, dampingFactor, seeds, maxNbOfIter, tolerance);
		cheiRank = getRankFromScore(cheiRankScore,seeds.keySet());
		normalizeScore(cheiRankScore);
		System.err.println("cheiRank computed");
		
		globalVsPersonalizedPageRank = computeGlobalVsPersonalized(globalPageRankScore, pageRankScore);
		globalVsPersonalizedCheiRank = computeGlobalVsPersonalized(globalCheiRankScore, cheiRankScore);
		
		return;
	}
	
	public HashMap<String, Double> computeScore(CompoundGraph graph, double dampingFactor, int maxNbOfIter, double tolerance){
		
		HashMap<String, Double> allNodes = new HashMap<String, Double>();
		double probability = 1.0 / (new Integer(graph.vertexSet().size()).doubleValue());
		for(BioPhysicalEntity v : graph.vertexSet()){
			allNodes.put(v.getId(), probability);
		}
		
		return computeScore(graph, dampingFactor, allNodes, maxNbOfIter, tolerance);
	}
	
	
	
	public HashMap<String, Double> computeScore(CompoundGraph graph, double dampingFactor, HashMap<String,Double> seeds, int maxNbOfIter, double tolerance){
		
		EigenVectorCentrality<BioPhysicalEntity, ReactionEdge, CompoundGraph> scoreComputor 
			= new EigenVectorCentrality<BioPhysicalEntity, ReactionEdge, CompoundGraph>(graph);
		scoreComputor.addJumpProb(seeds, 1-dampingFactor);
		HashMap<String, Double> score = scoreComputor.powerIteration(seeds, maxNbOfIter, tolerance);
		return score;
	}
	
	
	public HashMap<String, Integer> getRankFromScore(HashMap<String, Double> score, Set<String> seedsToIgnore){
		HashMap<String, Double> scoreCopy = new HashMap<String, Double>(score);
		if(seedsToIgnore!=null){
			for(String seed : seedsToIgnore){
				scoreCopy.remove(seed);
			}
		}
		return RankUtils.computeRank(scoreCopy);
	}
		
	
	public void normalizeScore(HashMap<String, Double> score){
		double max = 0.0;
		for(double value : score.values()){
			if(max<value) max=value;
		}
		
		for(Entry<String,Double> entry : score.entrySet()){
			score.put(entry.getKey(), entry.getValue()/max);
		}
	}
	
	
	
	public HashMap<String, Double> computeGlobalVsPersonalized(HashMap<String, Double> globalScore, HashMap<String, Double> score ){
		
		HashMap<String, Double> globalVsPersoRatio = new HashMap<String, Double>();
		
		for(Entry<String,Double> globalEntry : globalScore.entrySet()){
			String vertex = globalEntry.getKey();
			double globalValue = globalEntry.getValue();
			double persoValue = score.get(vertex);
			globalVsPersoRatio.put(vertex, persoValue/globalValue);
		}
		
		return globalVsPersoRatio;
	}
	
	public void printCompoundTable(String outputDir){
		try {	
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputDir+"result.tab"), true));
			bw.write("Compound ID\tName\tFormula\tHMDB\tSeed\tglobal PageRank\tglobal CheiRank\tPageRank\tCheiRank\tPageRank*\tCheiRank*");
			bw.newLine();
			for(BioPhysicalEntity compound:firstGraph.vertexSet()){
				
				boolean isSeed = seeds.containsKey(compound.getId());
				
				
				double globalPageRank = globalPageRankScore.get(compound.getId());
				double globalCheiRank = globalCheiRankScore.get(compound.getId());
				double pageRank = pageRankScore.get(compound.getId());
				double cheiRank = cheiRankScore.get(compound.getId());
				
				//format data
				DecimalFormat df = new DecimalFormat("#.####");
				df.setRoundingMode(RoundingMode.CEILING);
				
				Set<BioRef> hmdbEntries = compound.getRefs("HMDB");
				String hmdbId = "?";
				if(hmdbEntries!=null){
					Iterator<BioRef> iter = hmdbEntries.iterator();
					hmdbId = iter.next().getId();
					while(iter.hasNext()){
						hmdbId=hmdbId+"/"+iter.next().getId();
					}
				}
				bw.write(compound.getId()+"\t");
				bw.write(compound.getName()+"\t");
				bw.write(compound.getChemicalFormula()+"\t");
				bw.write(hmdbId+"\t");
				if(isSeed){
					bw.write("yes\t");
				}else{
					bw.write("no\t");
				}
				
				bw.write(df.format(globalPageRank)+"\t");
				bw.write(df.format(globalCheiRank)+"\t");
				bw.write(df.format(pageRank)+"\t");
				bw.write(df.format(cheiRank)+"\t");
				
				if(!isSeed){
					double globalVsPersoPR = globalVsPersonalizedPageRank.get(compound.getId());
					double globalVsPersoCR = globalVsPersonalizedCheiRank.get(compound.getId());
					bw.write(df.format(globalVsPersoPR)+"\t");
					bw.write(df.format(globalVsPersoCR)+"\t");
				}else{
					bw.write("NA\t");
					bw.write("NA\t");
				}
				
				bw.newLine();
				
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
	}
	
	/**
	 * The main method to compute 2D Rank (Page Rank - Chei Rank).
	 * @param args the arguments
	 */
	public static void main (String[] args){
		
		//PARAMETERS
		//==========
		String sbmlFilePath=args[0];		//path to network used for computing centrality, in sbml format. Required for chemical similarity weighting: at least InChI or SMILES or InChIKey
		String seedsFilePath=args[1];	//tabulated file containing node of interest ids and weight
//		String blackNodes=args[1];		//if no weighting, a simple list of ids can be used
										//note: if your using a different identifier system, 
										//		correspondences between metabolites of interest and network's ids can be automatically done using Names2BioNetworkIds in parsebionet
										//		(manual checking recommended)
		String weightsFile=args[2]; 	// path to file containing edges' weights
		String outputDir=args[3];	//Directory where outputs will be exported
		double dampingFactor = 0.85;
		
//		DateFormat df = new SimpleDateFormat("yy-MM-dd_HH:mm_");
//		outputDir+=df.format(new Date());
//		System.err.println(sbmlFilePath);
//		System.err.println(weightsFile);
//		System.err.println(seedsFilePath);
//		Matcher m = Pattern.compile("^.+/([^/]+)$").matcher(sbmlFilePath);
//		m.matches();
//		String sbmlFileName = m.group(1);
//		Matcher m2= Pattern.compile("^.+/([^/]+)$").matcher(seedsFilePath);
//		m2.matches();
//		String noiFileName = m2.group(1);
//
//		outputDir+=sbmlFileName+"_in:"+noiFileName+"_d"+dampingFactor+"_";
//

		Compute2DRank analysis2D = new Compute2DRank(sbmlFilePath, seedsFilePath, weightsFile);
		analysis2D.compute();
		analysis2D.printCompoundTable(outputDir);
		System.err.println("done.");
		
	}
}