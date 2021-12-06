![](https://forgemia.inra.fr/uploads/-/system/project/avatar/864/met4J_logo.png?width=64)
# Met4J

## Java library for metabolic networks


[![pipeline status](https://forgemia.inra.fr/metexplore/met4j/badges/master/pipeline.svg)](https://forgemia.inra.fr/metexplore/met4j/-/commits/master)

[![coverage report](https://forgemia.inra.fr/metexplore/met4j/badges/master/coverage.svg)](https://forgemia.inra.fr/metexplore/met4j/-/commits/master)

**Met4J is an open-source Java library dedicated to the structural analysis of metabolic networks. It also came with a toolbox gathering CLI for several analyses relevant to metabolism-related research.**

### Modules

Met4j is composed by several maven modules:
- [met4j-core](met4j-core/README.md): it's the key module which contains all the core 
classes for handling metabolic networks
- [met4j-io](met4j-io/README.md): for importing/exporting metabolic networks in several 
formats (SBML, MetExploreXml, KEGG)
- [met4j-graph](met4j-graph/README.md): for performing graph-based topological analysis of metabolic networks.

The other modules are for the moment not documented or still in progress.



### Installation

#### From Maven

Install all modules :
```
<dependency>
<groupId>fr.inrae.toulouse.metexplore</groupId>
<artifactId>met4j-toolbox</artifactId>
<version>0.5.2</version>
</dependency>
```

or a specific module (example : met4j-core ):
```
<dependency>
<groupId>fr.inrae.toulouse.metexplore</groupId>
<artifactId>met4j-core</artifactId>
<version>0.5.2</version>
</dependency>
```

Replace the version number by the last version of met4j.

#### From gitlab

```
git clone https://forgemia.inra.fr/metexplore/met4j.git;
cd met4j;
mvn clean install 
```

### Tutorial

You can find tutorials by clicking on the module links above.
Examples can be found here:
https://forgemia.inra.fr/metexplore/tutorialmet4j





