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
			<version>1.0.1</version>
		</dependency>
...
</dependencies>
```

The version number will vary in the future.


### Documentation

Examples can be found in
https://forgemia.inra.fr/metexplore/tutorialmet4j
in the package fr.inrae.toulouse.metexplore.tutorialmet4j.met4j_io.

##### Read a SBML

https://forgemia.inra.fr/metexplore/tutorialmet4j/blob/master/src/main/java/fr/inrae/toulouse/metexplore/tutorialmet4j/met4j_io/UseJsbmlReader.java

```java
JsbmlReader reader = new JsbmlReader(pathOfYourSbmlFile);
BioNetwork network = reader.read();
```

The read method of the JsbmlReader class optimises the reading of a SBML file
by using all the known plugins used to specify the metabolic entities.

- [FBC](https://sbml.org/documents/specifications/level-3/version-1/fbc/) : fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser
- [groups for pathways](https://sbml.org/documents/specifications/level-3/version-1/groups/) :fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.GroupPathwayParser
- [Miriam annotation](https://co.mbine.org/standards/miriam) : fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.AnnotationParser
- Html notes : fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser

If you want to enable only some plugins, you have to specify them. 
For instance, here, only FBC and GroupPathway plugins will be used.

```java
ArrayList<PackageParser> pkgs = new ArrayList<>(Arrays.asList(new FBCParser(), new GroupPathwayParser()));
BioNetwork network = reader.read(pkgs);
```


###### FBC plugin

Information recorded in the BioNetwork when the FBC plugin is activated:

- Metabolites : charge and chemical formula


Example:
```xml
<species id="M_glc__D_e" fbc:charge="0" fbc:chemicalFormula="C6H12O6" ... >
```

- Genes and Gene-reaction associations : 

Example:

```xml
<fbc:listOfGeneProducts>
 <fbc:geneProduct fbc:id="G_s0001" fbc:label="s0001" fbc:name="s0001" ...>
 </fbc:geneProduct>
 <fbc:geneProduct fbc:id="G_b0451" fbc:label="b0451" fbc:name="amtB" ...>
 </fbc:geneProduct>
</fbc:listOfGeneProducts>
...
<listOfReactions>
 <reaction ...>
 ...
  <fbc:geneProductAssociation>
    <fbc:or>
        <fbc:geneProductRef fbc:geneProduct="G_s0001"/>
        <fbc:geneProductRef fbc:geneProduct="G_b0451"/>
    </fbc:or>
  </fbc:geneProductAssociation>
 ...
 </reaction>
</listOfReactions>
```

For each gene-reaction association, the path BioReaction-BioEnzyme(s)-BioProtein(s) is 
automatically created.
For instance, if in your SBML file, you have a reaction R1 with this gene association : (G1) or (G2 and G3), three BioGene 
and three BioProtein will be created : G1, G2 and G3; and two BioEnzyme will be created : G1 
and G2_AND_G3, both associated to the BioReaction R1.

- Flux lower and upper bounds for flux modelling

Example:

```xml
<listOfParameters>
      <parameter value="-1000" id="cobra_default_lb" sboTerm="SBO:0000626" constant="true" units="mmol_per_gDW_per_hr"/>
      <parameter value="1000" id="cobra_default_ub" sboTerm="SBO:0000626" constant="true" units="mmol_per_gDW_per_hr"/>
</listOfParameters>
...
<listOfReactions>
<reaction id="R_O2t" fast="false" reversible="true" name="O2 transport  diffusion" fbc:upperFluxBound="cobra_default_ub" fbc:lowerFluxBound="cobra_default_lb">
    ...
</reaction>
</listOfReactions>
```

- Flux objectives for flux modelling

Example:

```xml
<fbc:listOfObjectives fbc:activeObjective="obj">
    <fbc:objective fbc:id="obj" fbc:type="maximize">
        <fbc:listOfFluxObjectives>
          <fbc:fluxObjective fbc:reaction="R_BIOMASS_Ecoli_core_w_GAM" fbc:coefficient="1"/>
        </fbc:listOfFluxObjectives>
    </fbc:objective>
</fbc:listOfObjectives>
```

