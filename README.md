![](https://forge.inrae.fr/uploads/-/system/project/avatar/864/met4J_logo.png?width=64)
# Met4J : the Java library for metabolic networks

[![MetaboHUB Logo](https://forge.inrae.fr/metabohub/e-tools/template-readme-mth/-/raw/main/logos/metabohub_logo-20x20.png?ref_type=heads)![MetaboHUB title](https://img.shields.io/badge/MetaboHub-Software-0066cc?style=flat-square)](https://www.metabohub.fr)
[![pipeline status](https://forge.inrae.fr/metexplore/met4j/badges/master/pipeline.svg)](https://forge.inrae.fr/metexplore/met4j/-/commits/master)
[![coverage report](https://forge.inrae.fr/metexplore/met4j/badges/master/coverage.svg)](https://forge.inrae.fr/metexplore/met4j/-/commits/master)
[![maven](https://img.shields.io/maven-central/v/fr.inrae.toulouse.metexplore/met4j)](https://img.shields.io/maven-central/v/fr.inrae.toulouse.metexplore/met4j)
[![version](https://img.shields.io/gitlab/v/tag/metexplore/met4j?gitlab_url=https%3A%2F%2Fforge.inrae.fr%2F&include_prereleases&label=version)](https://img.shields.io/gitlab/v/tag/metexplore/met4j?gitlab_url=https%3A%2F%2Fforge.inrae.fr%2F&include_prereleases&label=version)
[![javadoc](https://javadoc.io/badge2/fr.inrae.toulouse.metexplore/met4j/javadoc.svg)](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j)

## Metadata

- authors: <clement.frainay@inrae.fr>, <ludovic.cottret@inrae.fr>
- creation date: `2016-12-06`
- main usage: open-source Java library dedicated to the structural analysis of metabolic networks

## Description

**Met4J is an open-source Java library dedicated to the structural analysis of metabolic networks. It also came with a toolbox gathering CLI for several analyses relevant to metabolism-related research.**


Met4j is composed by three main modules:
- [met4j-core](met4j-core/README.md): it's the key module which contains all the core 
classes for handling metabolic networks
- [met4j-io](met4j-io/README.md): for importing/exporting metabolic networks in several 
formats (SBML, KEGG, TSV)
- [met4j-graph](met4j-graph/README.md): for performing graph-based topological analysis of metabolic networks.  

The package [met4j-toolbox](met4j-toolbox/README.md) contains high-level apps that can be run in command line by using either jar file or Singularity or Docker containers.

## Features

### Metabolic Network Manipulation (met4j-core)

The core module [met4j-core](met4j-core/README.md) provides robust data structures for handling metabolic networks.

### Input/Output & Standards Support (met4j-io)

The [met4j-io module](met4j-io/README.md)  supports reading and writing metabolic networks in various formats, ensuring compatibility with community standards.
*   **SBML Support**: comprehensive support for SBML (Structure-based Model Language), including packages like **FBC** (Flux Balance Constraints), **Groups** (for pathways), **Miriam** annotations, and Notes.
*   **Format Conversion**: Capabilities to import/export from **KEGG**, **TSV**, and generic **matrices**.

### Graph-Based Topology Analysis (met4j-graph)

The  [met4j-graph](met4j-graph/README.md) offers advanced graph representations and algorithms for structural analysis dedicated to metabolic graphs.
*   **Graph Representations**: Converting metabolic networks into **Compound Graphs**, **Reaction Graphs**, and **Bipartite Graphs**.
*   **Topological Metrics**: Algorithms to compute **Load points**, **Choke points**, **Scope networks**, and **Precursor networks**.
*   **Weighting Strategies**: Edge weighting based on degree or **chemical similarity**.
*   **Export**: Compatibility with **Cytoscape** via GML/XGMML export.

### Cheminformatics Utilities (met4j-chemUtils)

The [met4j-chemUtils module](met4j-chemUtils/README.md) handles chemical information associated with metabolites.
*   **Chemical Analysis**: Parsing chemical formulas and computing molecular masses (`MassComputor`).
*   **Chemical Similarity**: Computing similarity between metabolites using molecular fingerprints (`FingerprintBuilder`, `SimilarityComputor`).

### Mapping & Data Integration (met4j-mapping)

The [met4j-mapping module](met4j-mapping/README.md) facilitates the mapping of omics data onto metabolic networks and its statiscal analysis.

*   **Data Mapping**: Features to map omics data or attributes to network entities (`AttributeMapper`).
*   **Enrichment**: Statistical tools for pathway enrichment analysis (`PathwayEnrichment`).

### High-Level Apps & Toolbox (met4j-toolbox)

The [met4j-toolbox module](met4j-toolbox/README.md) is collection of command-line applications making the library's features accessible without coding.
*   **converters**: examples: `Sbml2Graph`, `Tab2Sbml`, `Kegg2Sbml`.
*   **Network Analysis**: examples: `ChokePoint`, `LoadPoint`, `ScopeNetwork`, `MetaboRank`.
*   **Reconstruction**: examples: `SbmlCheckBalance` and `CreateMetaNetwork`

## Getting Started

### Prerequisites

To use Met4J as a library or to build it from source, you need:

*   **Java Development Kit (JDK)**: version 17 or higher
*   **Apache Maven**: version 3.x or higher


### Installing



#### From Maven

Install all modules :  
```
<dependency>
<groupId>fr.inrae.toulouse.metexplore</groupId>
<artifactId>met4j-toolbox</artifactId>
<version>1.4.0</version>
</dependency>
```

or a specific module (example : met4j-core ):  
```
<dependency>
<groupId>fr.inrae.toulouse.metexplore</groupId>
<artifactId>met4j-core</artifactId>
<version>1.4.0</version>
</dependency>
```

#### From Source

```
git clone https://forge.inrae.fr/metexplore/met4j.git;
cd met4j;
mvn clean install 
```

Read [met4j-toolbox](met4j-toolbox/README.md) to see how to get jar, conda, Docker or Singularity packages containing all the met4-toolbox apps.

#### Running tests

Unit tests:
```
mvn clean test
```

Integration tests:
```
mvn clean install -DskipTests
mvn  verify
```


#### Usage

User documentation for the library modules can be found in each module's own README.  
Detailed code examples can be found at [here](https://forge.inrae.fr/metexplore/tutorialmet4j).
Javadoc can be found at [https://javadoc.io/doc/fr.inrae.toulouse.metexplore](https://javadoc.io/doc/fr.inrae.toulouse.metexplore)

## Contributing

Pull requests are welcome **on the gitlab repo** ([https://forge.inrae.fr/metexplore/met4j](https://forge.inrae.fr/metexplore/met4j)). For major changes, please open an issue first to discuss what you would like to change.  

Please make sure to update tests as appropriate.  

## Issues

Issues or suggestions can be posted [here](https://github.com/MetExplore/met4j/issues).

## Changelog

<!-- NOTE: this section is optional -->

All notable changes to this project will be documented in [CHANGELOG.md](CHANGELOG.md).
For the versions available, see the [releases on this repository](https://forge.inrae.fr/metexplore/met4j/-/releases).


## Support &amp; External resources

- Bug reports: [INRAE Forge issues](https://forge.inrae.fr/metabohub/e-tools/template-readme-mth/-/issues) or contact us by email: <contact-metexplore@inrae.fr>.


## License

Met4J is distributed under the open license [CeCILL-2.1](https://cecill.info/licences/Licence_CeCILL_V2.1-en.html) (compatible GNU-GPL).  
