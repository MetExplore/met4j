# met4j-io

## Met4j module for reading and writing metabolic networks

### Installation

``` 
cd met4j-io;
mvn clean install;
```

### Using met4j-io in a maven project

Put this in the pom.xml file of your project:
```
<dependencies>
...
		<dependency>
			<groupId>fr.inrae.toulouse.metexplore</groupId>
			<artifactId>met4j-io</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
...
</dependencies>
```

The version number will vary in the feature.


### Documentation

Examples can be found in
https://forgemia.inra.fr/metexplore/tutorialmet4j
in the package fr.inrae.toulouse.metexplore.tutorialmet4j.met4j_io.

##### Read a SBML

https://forgemia.inra.fr/metexplore/tutorialmet4j/blob/master/src/main/java/fr/inrae/toulouse/metexplore/tutorialmet4j/met4j_io/UseJsbmlReader.java

If your SBML follows the last specifications, it's easy:

```java
JsbmlReader reader = new JsbmlReader(pathOfYourSbmlFile);
BioNetwork network = reader.read();
```

