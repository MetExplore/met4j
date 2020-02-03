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

###### Remove elements from a BioNetwork

The method "removeOnCascade" allows to remove several elements from a network.
The OnCascade suffix means that related elements could be removed when some elements are removed.

_Remove on cascade a metabolite_

- The metabolite is removed from each of the reactions where it occurs
- If the reaction does not contain metabolites anymore, it is also removed
- It is removed from each compartment where it occurs. If the compartment does not contain
any component anymore, it is removed.
- It is removed from each enzyme where it occurs. If the enzyme does not contain any enzyme,
it is removed

_Remove on cascade a protein_
- It is removed from each enzyme where it occurs. If the enzyme does not contain any enzyme,
it is removed
- It is removed from each compartment where it occurs. If the compartment does not contain
any component anymore, it is removed

_Remove on cascade a gene_
- Remove proteins coded by this gene

_Remove on cascade a reaction_
- Is removed from each pathway where it occurs. If the pathway is empty after that, the pathway
is also removed

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

###### Add metabolites in a compartment

To participate to a reaction, a metabolite must be localised in a compartment.

```java
BioCompartment c1 = new BioCompartment("c1", "compartment1");
network.add(c1);
network.affectToCompartment(c1, m1);
```

We can add several metabolites in the same compartment at once:
```java
network.add(m2);
network.affectToCompartment(c1, m1, m2);
```

Be careful, either the metabolites and the compartment  must be added before in the network

It's possible to add metabolites stored in a BioCollection
```java
BioCollection<BioMetabolite> c = new BioCollection<>();
c.add(m1);
c.add(m2);
network.affectToCompartment(c1,c);
```

###### Affect metabolites to a reaction

Let's say that we want to build this reaction:
R1 : 2 m1 + 3 m2 -> m3 + m4

```
BioMetabolite m3 = new BioMetabolite("m3");
BioMetabolite m4 = new BioMetabolite("m4");
network.add(m3, m4);
network.affectToCompartment(c1, m3, m4);
network.affectLeft(r1, 2.0, c1, m1);
network.affectLeft(r1, 3.0, c1, m2);
network.affectRight(r1, 1.0, c1, m3);
network.affectRight(r1, 1.0, c1, m4);
```

Since the stoichiometric coefficients are the same for m3 and m4,
it's also possible to do:
```java
network.affectRight(r1, 1.0, m3, m4);
```
or
```java
BioCollection<BioMetabolite> rights = new BioCollection<>();
rights.add(m3, m4);
network.affectRight(r1, 1.0, rights);
```

We can also build BioReactants, that joins together a metabolite, a compartment
and a stoichiometric coefficient

```java
BioReactant reactant1 = new BioReactant(m1, 1.0, c1);
BioReactant reactant2 = new BioReactant(m2, 1.0, c1);
BioReactant reactant3 = new BioReactant(m3, 2.0, c1);

BioReaction r2 = new BioReaction("r2");
network.add(r2);
network.affectLeft(r2, reactant1);
network.affectRight(r2, reactant2, reactant3);
```

###### Affect a gene to a protein

In met4j, the relation between a gene and a protein is 1 gene : several proteins, what means
that one protein is coded by only one gene but that one gene can code for several proteins.

```java
BioGene g1 = new BioGene("g1");
BioProtein p1 = new BioProtein("p1");
network.add(g1, p1);
network.affectGeneProduct(p1, g1);
```

###### Build an enzyme

In met4j, an enzyme is considered as a complex containing several proteins.

```java
BioEnzyme e1 = new BioEnzyme("e1");
BioProtein p2 = new BioProtein("p2");
network.add(e1, p2);
BioCollection<BioProtein> components = new BioCollection<>();
components.add(p1, p2);
network.affectSubUnit(e1, 1.0, components);
```

###### Assign enzymes to a reaction

```java
BioEnzyme e2 = new BioEnzyme("e2");
BioProtein p3 = new BioProtein("p3");
network.add(e2, p3);
network.affectSubUnit(e2, 1.0, p3);
network.affectEnzyme(r1, e2, e1);
```

###### Add reactions to a pathway

```java
BioPathway pathway1 = new BioPathway("p1");
network.add(pathway1);
network.affectToPathway(pathway1, r1, r2);
```







