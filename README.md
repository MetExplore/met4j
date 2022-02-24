![](https://forgemia.inra.fr/uploads/-/system/project/avatar/864/met4J_logo.png?width=64)
# Met4J : the Java library for metabolic networks

[![pipeline status](https://forgemia.inra.fr/metexplore/met4j/badges/master/pipeline.svg)](https://forgemia.inra.fr/metexplore/met4j/-/commits/master)
[![coverage report](https://forgemia.inra.fr/metexplore/met4j/badges/master/coverage.svg)](https://forgemia.inra.fr/metexplore/met4j/-/commits/master)
[![maven](https://img.shields.io/maven-central/v/fr.inrae.toulouse.metexplore/met4j)](https://img.shields.io/maven-central/v/fr.inrae.toulouse.metexplore/met4j)
[![version](https://img.shields.io/gitlab/v/tag/metexplore/met4j?gitlab_url=https%3A%2F%2Fforgemia.inra.fr%2F&include_prereleases&label=version)](https://img.shields.io/gitlab/v/tag/metexplore/met4j?gitlab_url=https%3A%2F%2Fforgemia.inra.fr%2F&include_prereleases&label=version)
[![javadoc](https://javadoc.io/badge2/fr.inrae.toulouse.metexplore/met4j/javadoc.svg)](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j) 

**Met4J is an open-source Java library dedicated to the structural analysis of metabolic networks. It also came with a toolbox gathering CLI for several analyses relevant to metabolism-related research.**


Met4j is composed by several modules:
- [met4j-core](met4j-core/README.md): it's the key module which contains all the core 
classes for handling metabolic networks
- [met4j-io](met4j-io/README.md): for importing/exporting metabolic networks in several 
formats (SBML, MetExploreXml, KEGG)
- [met4j-graph](met4j-graph/README.md): for performing graph-based topological analysis of metabolic networks.  

The other modules contains utilities to serve the main modules listed here.  

The full list of implemented metabolic network analysis can be found in the [met4j-toolbox](met4j-toolbox/README.md) 



## Installation

### From Maven

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

### From Source

```
git clone https://forgemia.inra.fr/metexplore/met4j.git;
cd met4j;
mvn clean install 
```
to build the executable toolbox jar:
```
cd met4j-toolbox
mvn clean compile assembly:single
mv ./target/*-jar-with-dependencies.jar ../../../
```

## Usage

Documentation for the library modules can be found in each module's own README.  
Detailed code examples can be found at [https://forgemia.inra.fr/metexplore/tutorialmet4j](https://cecill.info/licences/Licence_CeCILL_V2.1-en.html).  

The toolbox can be launched using
```
java -jar met4j-toolbox-0.8.0-jar-with-dependencies.jar
```
which will list all the contained applications that can be called using

```
java -cp met4j-toolbox-0.8.0-jar-with-dependencies.jar <App full name> -h
```

## Contributing
Pull requests are welcome **on the gitlab repo** ([https://forgemia.inra.fr/metexplore/met4j](https://forgemia.inra.fr/metexplore/met4j)). For major changes, please open an issue first to discuss what you would like to change.  

Please make sure to update tests as appropriate.  

## License
Met4J is distributed under the open license [CeCILL-2.1](https://cecill.info/licences/Licence_CeCILL_V2.1-en.html) (compatible GNU-GPL).  







