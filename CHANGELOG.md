# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.1.0

### Features

[met4j-toolbox] Improve Met4J usability for Galaxy Workflow by adding utilities apps that exploit bioNetworks functions

- decomposeSBML to get bioentities in sbml as list of ids
- GetReactantsFromReactions to get list of metabolites from sbml + list of reactions
- GetGenesFromReactions to get list of genes from sbml + list of reactions

## 1.0.1

### Fixed

[met4j-toolbox] Better error handling

[met4j-toolbox][SbmlSetPathways] Format pathway id to avoid redundancies

## 1.0

### Features

[met4j-toolbox] Executable jar in the [met4j gitlab registry](https://forgemia.inra.fr/metexplore/met4j/-/packages)

### Documentation

[met4j-core] Improve documentation in README.md

## 0.12.0

### Features

[met4j-toolbox] Subnetwork Extraction Improvement : 
- bipartite graph compatibility
- export as table
- allows undirected case for paths and steiner tree computation 

### Fixed
[met4j-graph] shortest paths union and steiner tree optimization

[met4j-graph] fix error in gml export causing import in igraph and cytoscape to fail

[met4j-mathUtils] fix sub-matrix creation not retaining rows&columns labels

[met4j-toolbox] Improvements of GenerateGalaxyFiles and GenerateJson

## 0.11.0

### Features

[met4j-toolbox] New app : GenerateGalaxyFiles to generate automatically wrappers for Galaxy

[met4j-toolbox] New app : GenerateJson to generate a json describing the apps

[devops] Automatic generation of the singularity and docker images for the develop & master versions.

## 0.10.0

### Features

[met4j-toolbox] New app: networkAnalysis.CompoundNet: Advanced compound graph building

[met4j-toolbox] New app: attributes.ExtractPathways: SBML sub network creation from a list of pathways

[met4j-toolbox] New app: attributes.ExtractSbmlAnnot: Extract sbml annotations 

[met4j-toolbox] App improvements: networkAnalysis.SideCompoundsScan: Handle multiple compartments

## 0.9.1

### Fixed

[met4j-io] Great speed improvements to read gene reaction associations

## 0.9.0

### Features

[met4j-toolbox] ExtractSubReactionNetwork app

## 0.8.3 

### Fixed

[met4j-io] Debug Tab2BioNetwork

## 0.8.2

### Fixed

[met4j-io] Deals better when a reactant has a stoichiometry equals to 0. Before, there was an Exception, now the reactant is simply not taken into account.

## 0.8.1

### Fixed

[met4j-io] New stable version of JSBML (1.6.1 that corrects log4j vulnerabilities)

## 0.8.0

### Features

App Kegg2Sbml

 
## 0.7.6

### Fixed

[met4j-io] Accepts stoichiometric coefficient equals to 0

## 0.7.5

### Fixed

[met4j-core] debug remove(gene)

## 0.7.4

### Fixed

[met4j-io] replace negative coefficients by positive coefficients

## 0.7.3

### Fixed

[maven]: Change rule of deploy

## 0.7.2

### Fixed

[met4j-io]: Check that pathways are not created before note parsing

## 0.7.1

### Fixed

- [met4j-io] Removes the exception when no unit definition is set


### Fixed

- [met4j-io] If no unit definition is set in the SBML header, there is no exception anymore when
the FBC parser is used : a new unit definition is created when it is specified in the parameters

## 0.7.0

### Added

- [met4j-core] Reaction.getMetabolitesView method allows to get all left and right metabolites in
the same collection.

- [met4j-core] If the id of a new BioEntity is not valid, a new random one is created

### Fixed

- [met4j-io] ReactionAttributes.getSideCompounds now returns a BioCollection of BioMetabolite instead
of a Set of String

- [met4j-core] Fix issues in BioNetwork.removeOnCascade

## 0.6.0

### Removed
- [met4j-io] MetExploreXml support : classes and apps

## [Unreleased]
### Added
- [met4j-graph] Graph Analysis Apps: chokepoints, distance matrix, subnetwork extraction, loadpoint, metaborank, scopecompounds and other utilities
- [met4j-graph] Improve graph export to gml: attributes can be exported too
### Removed
- FlexFlux
### Fixed
- [met4j-graph] Reaction graph creation was very slow, now fixed
- [met4j-graph] Fixed FloydWarshall undirected with multi-graphs