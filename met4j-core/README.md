# met4j-core

## Met4j module for basic manipulation of metabolic networks

### Installation

``` 
cd met4j-core;
mvn clean install;
```

### Using met4j-core in a maven project

Put this in the pom.xml file of your project:
```
<dependencies>
...
		<dependency>
			<groupId>fr.inrae.toulouse.metexplore</groupId>
			<artifactId>met4j-core</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
...
</dependencies>
```

The version number will vary in the future.


### Documentation

Examples can be found in
https://forgemia.inra.fr/metexplore/tutorialmet4j
in the package fr.inrae.toulouse.metexplore.tutorialmet4j.met4j_core.

##### The BioNetwork class

The BioNetwork class is the essential class of met4j.

It contains and links all the entities composing a metabolic network (metabolites, reactions,
pathways, genes, proteins, enzymes, compartments)

All examples can be found here :
https://forgemia.inra.fr/metexplore/tutorialmet4j/blob/master/src/main/java/fr/inrae/toulouse/metexplore/tutorialmet4j/met4j_core/UseBioNetwork.java




       

