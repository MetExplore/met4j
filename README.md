# Met4J

## Java library for metabolic networks

### Modules

Met4j is composed by several maven modules:
- [met4j-core](met4j-core/README.md): it's the key module which contains all the core 
classes for handling metabolic networks
- [met4j-io](met4j-io/README.md): for importing/exporting metabolic networks in several 
formats (SBML, MetExploreXml, KEGG)
- [met4j-flux](met4j-flux/README.md): for performing flux balance analysis, requires CPLEX
or GLPK installed on your computer.
- [met4j-graph](met4j-graph/README.md): for performing graph-based topological analysis of metabolic networks.

The other modules are for the moment not documented or still in progress.



### Installation

#### From gitlab

```
git clone https://forgemia.inra.fr/metexplore/met4j.git;
cd met4j;
mvn clean install  -DskipTests;
```

### Tutorial

You can find tutorials by clicking on the module links above.
Examples can be found here:
https://forgemia.inra.fr/metexplore/tutorialmet4j





