# met4j-graph

## Met4j module for graph-based topological analysis of metabolic networks

### Installation

``` 
cd met4j-graph;
mvn clean install;
```

### Using met4j-graph in a maven project

Put this in the pom.xml file of your project:
```
<dependencies>
...
		<dependency>
			<groupId>fr.inrae.toulouse.metexplore</groupId>
			<artifactId>met4j-graph</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
...
</dependencies>
```

The version number will vary in the future.


### Documentation

Examples can be found in
https://forgemia.inra.fr/metexplore/tutorialmet4j
in the package fr.inrae.toulouse.metexplore.tutorialmet4j.met4j_graph.

##### Creating a met4j graph

From a BioNetwork build the met4J core API, several graphs can be created:
**Compound graph**, where nodes represent metabolites, and directed edges links two compounds if they are respectively substrates and products of the same reaction.
  A reaction is thus represented by a set of edges.
**Reaction graph**, where nodes represent reactions, and directed edges links two reactions if one produces a substrate of the other.
**Bipartite graph**, where nodes can represent both metabolites and reactions. Directed edges links substrate metabolites to their consuming reactions, and reactions to their products metabolites.

```java
//Read BioNetwork
JsbmlReader reader = new JsbmlReader(pathOfYourSbmlFile);
BioNetwork network = reader.read();

//Build graphs
Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
CompoundGraph cgraph = builder.getCompoundGraph();
ReactionGraph rgraph = builder.getReactionGraph();
BipartiteGraph bgraph = builder.getBipartiteGraph();
```
##### Performing network anlysis using met4j-graph


All graphs in **met4j-graph** extend `BioGraph`, a wrapper class for [JGraphT](https://jgrapht.org/) `DirectedWeightedMultigraph`, and add convenience methods relevant in metabolic network analysis.

Metabolic graphs can be analyzed using the JGraphT generic algorithms or **met4J-graph**
implementation which include more domain-specific analysis, such as *load points*, *choke points*, *precursor network* or *scope network*.

**met4J-graph** also includes many weighting functions tailored for metabolic network analysis, including *Chemical similarity* or *degree squared* as well as edge weighting for Markov-Chain analysis.
```java
//perform chemical similarity weighting
WeightingPolicy wp = new SimilarityWeightPolicy(FingerprintBuilder.KLEKOTAROTH);
wp.setWeight(cgraph);
//Compute lightest path
ShortestPath pathSearch = new ShortestPath<BioMetabolite, ReactionEdge, CompoundGraph>(cgraph);
BioPath<BioMetabolite, ReactionEdge> path = 
        pathSearch.getShortest(cgraph.getVertex("glucose"), cgraph.getVertex("pyruvate"));
return path.getLength();
```
Finally, **met4J-graph** provides export to *gml* and *xgmml* files to perform analysis and visualization in [Cytoscape](https://cytoscape.org/).
