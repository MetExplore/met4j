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

The version number will vary in the feature.


### Documentation

All the examples can be found in
https://forgemia.inra.fr/metexplore/tutorialmet4j
in the package fr.inrae.toulouse.metexplore.tutorialmet4j.met4j_core.

##### The BioNetwork class

The BioNetwork class is the essential class of met4j.

It contains and links all the entities composing a metabolic network (metabolites, reactions,
pathways, genes, proteins, enzymes, compartments)

```java
BioNetwork network = new BioNetwork("myBioNetwork");
```

myBioNetwork is the id of the network. If you don't specify the id, NA will be set as id.

###### Adding an element in a BioNetwork

The method "add" of the BioNetwork class allows to add any entity cited above in a metabolic network.

Examples:

Add a metabolite:
```java
BioMetabolite m1 = new BioMetabolite("m1");
network.add(m1);
```

Add a reaction:
```java
BioReaction r1 = new BioReaction("r1");
network.add(r1);
```

Be careful, if you add an entity with the same id
than an entity previously added, this one will
be replaced by that one

```java
BioMetabolite m1Bis = new BioMetabolite("m1", "M1Bis");
network.add(m1Bis);
System.err.println("Name of the metabolite m1 : "+network.getMetabolitesView().get("m1").getName());
```

This will print:
```java
Name of the metabolite m1 : M1Bis
```

###### Check if an entity is in a BioNetwork

The method "contains" of the BioNetwork class allows to check the presence of any entity cited
above in a metabolic network.

Let's continue with the same examples. 

```java
network.add(m1);
System.err.println("Does network contains m1 ?\n"+network.contains(m1));
```

This will print:
```java
Does network contains m1 ?
true
```

```java
BioMetabolite m2 = new BioMetabolite("m2");
System.err.println("Does network contains m2 ?\n"+network.contains(m2));
```

This will print:
```java
Does network contains m2 ?
false
```

###### Build a reaction


