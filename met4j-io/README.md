# met4j-io

- [met4j-io](#met4j-io)
  - [Met4j module for reading and writing metabolic networks](#met4j-module-for-reading-and-writing-metabolic-networks)
    - [Installation](#installation)
    - [Using met4j-io in a maven project](#using-met4j-io-in-a-maven-project)
    - [Documentation](#documentation)
      - [Read a SBML](#read-a-sbml)
        - [Default imports](#default-imports)
        - [FBC plugin](#fbc-plugin)
          - [Metabolites : charge and chemical formula](#metabolites--charge-and-chemical-formula)
          - [Genes and Gene-reaction associations](#genes-and-gene-reaction-associations)
          - [Flux objectives for flux modelling](#flux-objectives-for-flux-modelling)
        - [Group pathway plugin](#group-pathway-plugin)
        - [Miriam annotation Plugin](#miriam-annotation-plugin)
        - [Notes Plugin](#notes-plugin)
          - [Metabolite chemical formula, charge, inchi and SMILES](#metabolite-chemical-formula-charge-inchi-and-smiles)
          - [Reaction EC number, score, status, comment and PMID](#reaction-ec-number-score-status-comment-and-pmid)
          - [Gene-Reaction associations](#gene-reaction-associations)
          - [Pathways](#pathways)
        - [Other attributes in html notes](#other-attributes-in-html-notes)
      - [Write a SBML](#write-a-sbml)

## Met4j module for reading and writing metabolic networks

### Installation

```console
cd met4j-io;
mvn clean install;
```

### Using met4j-io in a maven project

Put this in the pom.xml file of your project:

```xml
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

Adapt the version number if needed.

### Documentation

Examples can be found in
<https://forgemia.inra.fr/metexplore/tutorialmet4j>
in the package fr.inrae.toulouse.metexplore.tutorialmet4j.met4j_io.

#### Read a SBML

<https://forgemia.inra.fr/metexplore/tutorialmet4j/blob/master/src/main/java/fr/inrae/toulouse/metexplore/tutorialmet4j/met4j_io/UseJsbmlReader.java>

```java
JsbmlReader reader = new JsbmlReader(pathOfYourSbmlFile);
BioNetwork network = reader.read();
```

The read method of the [JsbmlReader](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/JsbmlReader.html) imports all the metabolic entities and data in a BioNetwork instance.
It also optimises the reading of a SBML file by using all the known plugins used to create and describe the metabolic entities.

- [FBC](https://sbml.org/documents/specifications/level-3/version-1/fbc/) : [FBCParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/FBCParser.html)
- [groups for pathways](https://sbml.org/documents/specifications/level-3/version-1/groups/) :[GroupPathwayParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/GroupPathwayParser.html)
- [Miriam annotation](https://co.mbine.org/standards/miriam) : [AnnotationParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/AnnotationParser.html)
- Html notes : [NotesParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/NotesParser.html)

If you want to enable only some plugins, you have to specify them.
For instance, here, only FBC and GroupPathway plugins will be used.

```java
ArrayList<PackageParser> pkgs = new ArrayList<>(Arrays.asList(new FBCParser(), new GroupPathwayParser()));
BioNetwork network = reader.read(pkgs);
```

##### Default imports

Without any package, the read method imports:

- Id and name of the BioNetwork
- Unit definitions for parameters (e.g. flux parameters). Can be retrieved with the method getUnitDefinitions of the [NetworkAttributes](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/annotations/network/NetworkAttributes.html) class.
- Compartments
- Metabolites
- Reactions and reactants
- [SBO](https://github.com/EBI-BioModels/SBO) terms (can be retrieved with the method getSboTerm of the [GenericAttributes](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/annotations/GenericAttributes.html) class)
- Flux bounds in the kinetic law tag if the FBC plugin is not activated

Example:

```xml
<sbml xmlns="http://www.sbml.org/sbml/level2" level="2" version="1" xmlns:html="http://www.w3.org/1999/xhtml">
<model id="Ec_iAF1260" name="Ec_iAF1260">
<listOfUnitDefinitions>
 <unitDefinition id="mmol_per_gDW_per_hr">
  <listOfUnits>
   <unit kind="mole" scale="-3"/>
   <unit kind="gram" exponent="-1"/>
   <unit kind="second" multiplier=".00027777" exponent="-1"/>
  </listOfUnits>
 </unitDefinition>
</listOfUnitDefinitions>
...
<reaction id="R_12DGR120tipp" name="R_1_2_diacylglycerol_transport_via_flipping__periplasm_to_cytoplasm__n_C120_" reversible="false">
...
<kineticLaw>
   <math xmlns="http://www.w3.org/1998/Math/MathML">
    <ci> FLUX_VALUE </ci>
   </math>
   <listOfParameters>
    <parameter id="LOWER_BOUND" value="0.000000" units="mmol_per_gDW_per_hr"/>
    <parameter id="UPPER_BOUND" value="999999.000000" units="mmol_per_gDW_per_hr"/>
   </listOfParameters>
  </kineticLaw>
...
</reaction>
```

**Warning** Data in the listOfModifiers tag is not imported by the met4j-io sbml reader !

##### FBC plugin

This plugin is used to read charge, chemical formula, flux bounds and FBA objectives. Please read the [package documentation](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/FBCParser.html) for the specifications.
The corresponding class in met4j-io is [FBCParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/FBCParser.html).

###### Metabolites : charge and chemical formula

Example:

```xml
<species id="M_glc__D_e" fbc:charge="0" fbc:chemicalFormula="C6H12O6" ... >
```

###### Genes and Gene-reaction associations

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

To get lower and upper bounds of a reaction in a network,
use the methods getLowerBound and getUpperBound of the class [ReactionAttributes](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/annotations/reaction/ReactionAttributes.html).

###### Flux objectives for flux modelling

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

To get the list of the objectives set for a network, use the method getObjectives of the class [NetworkAttributes](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/annotations/network/NetworkAttributes.html).

##### Group pathway plugin

This plugin gets information about the metabolic pathways and the reactions involved in them. Please read the [package documentation](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/GroupPathwayParser.html) for specifications.

The corresponding class in met4j-io is [GroupPathwayParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/GroupPathwayParser.html).

Example:

```xml
...
<groups:listOfGroups>
      <groups:group groups:id="g31" groups:kind="classification" groups:name="Glutamate Metabolism">
        <groups:listOfMembers>
          <groups:member groups:idRef="R_GLUN"/>
          <groups:member groups:idRef="R_GLUNpp"/>
          <groups:member groups:idRef="R_GLUDC"/>
          <groups:member groups:idRef="R_GLUSy"/>
          <groups:member groups:idRef="R_GLNS"/>
          <groups:member groups:idRef="R_GLUDy"/>
        </groups:listOfMembers>
      </groups:group>
...
</groups:listOfGroups>
```

Get the list of pathways of a network with the method getPathwaysView
of the [BioNetwork](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-core/latest/fr/inrae/toulouse/metexplore/met4j_core/biodata/BioNetwork.html) class.

##### Miriam annotation Plugin

The Minimum information requested in the annotation of biochemical models ([MIRIAM](https://www.nature.com/articles/nbt1156)) define rules for annotating biological models and especially cross references.
The corresponding class in met4j-io is [AnnotationParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/AnnotationParser.html).

By default, you must note that all the cross references must follow the pattern ```https://identifiers.org/*/*```. You can change the pattern by specifying it when you create a new instance of the [AnnotationParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/AnnotationParser.html) class.

Example:

```xml
<reaction fbc:lowerFluxBound="LOWER_BOUND_1000_0" fbc:upperFluxBound="UPPER_BOUND_1000_0" id="R_AIRC3" metaid="_021f4445-f5d6-492c-8d4a-315a04229545" name="Phosphoribosylaminoimidazole carboxylase (mutase rxn)" reversible="true" sboTerm="SBO:0000176">
        <annotation>
          <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:bqbiol="http://biomodels.net/biology-qualifiers/">
            <rdf:Description rdf:about="#_021f4445-f5d6-492c-8d4a-315a04229545">
              <bqbiol:is>
                <rdf:Bag>
                  <rdf:li rdf:resource="https://identifiers.org/ec-code/5.4.99.18"/>
                  <rdf:li rdf:resource="https://identifiers.org/biocyc/META:RXN0-743"/>
                  <rdf:li rdf:resource="https://identifiers.org/seed.reaction/rxn05115"/>
                  <rdf:li rdf:resource="https://identifiers.org/rhea/13196"/>
                </rdf:Bag>
              </bqbiol:is>
            </rdf:Description>
          </rdf:RDF>
        </annotation>
  ...
</reaction>
```

You can retrieve the annotations using the method getRefs of any [BioEntity](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-core/latest/fr/inrae/toulouse/metexplore/met4j_core/biodata/BioEntity.html). Example:

```java
Set<BioRef> refs = reaction.getRefs("SBML");
```

The ec number of the reaction is automatically set if an annotation starting by ```https://identifiers.org/ec-code/``` is found.

The inchi of a BioMetabolite is also set if an annotation follows this format (example):

```xml
<species metaid="_metaM_10fthf5glu_c" id="M_10fthf5glu_c" name="10-Formyltetrahydrofolate-[Glu](5)" compartment="c" hasOnlySubstanceUnits="false" boundaryCondition="false" constant="false">
  <annotation>
          <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:bqbiol="http://biomodels.net/biology-qualifiers/">
            <rdf:Description rdf:about="#_metaM_10fthf5glu_c">
              <in:inchi xmlns:in="http://biomodels.net/inchi">InChI=1/C40H51N11O19/c41-40-49-32-31(37(66)50-40)43-19(15-42-32)16-51(17-52)20-3-1-18(2-4-20)33(62)47-24(38(67)68)5-10-26(53)44-21(6-11-27(54)55)34(63)45-22(7-12-28(56)57)35(64)46-23(8-13-29(58)59)36(65)48-25(39(69)70)9-14-30(60)61/h1-4,17,19,21-25,43H,5-16H2,(H,44,53)(H,45,63)(H,46,64)(H,47,62)(H,48,65)(H,54,55)(H,56,57)(H,58,59)(H,60,61)(H,67,68)(H,69,70)(H4,41,42,49,50,66)/p-6</in:inchi>
            </rdf:Description>
          </rdf:RDF>
  </annotation>
...
</species>
```

##### Notes Plugin

This plugin aims to read all the HTML notes found in SBML elements.
These notes are often found in old SBML and correspond to ad-hoc rules or [COBRA rules](https://europepmc.org/article/MED/21886097). We have tried to consider all the cases we have encountered but it is certain that we could not be exhaustive.

The corresponding class in met4j-io is [NotesParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/NotesParser.html).

All the recognized patterns can be changed by changing the corresponding fields in the NotesParser instance.

###### Metabolite chemical formula, charge, inchi and SMILES

Example:

```xml
<species id="M1" ...>
  <notes>
    <body xmlns="http://www.w3.org/1999/xhtml">
      <p>Formula: C5H403</p>
      <p>CHARGE: 3.0</p>
      <p>INCHI: InChI=1S/C7H11N3O2/c1-10-3-5(9-4-10)2-6(8)7(11)12/h3-4,6H,2,8H2,1H3,(H,11,12)/t6-/m0/s1</p>
      <p>SMILES: [H]OC(=O)[C@@]([H])(N([H])[H])C([H])([H])C1=C([H])N(C([H])=N1)C([H])([H])[H]</p>
    </body>
  </notes>
</species>
```

Formula and charge can be retrieved by the methods getChemicalFormula and getCharge of the
[BioMetabolite](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-core/latest/fr/inrae/toulouse/metexplore/met4j_core/biodata/BioMetabolite.html) class.

Inchi and smiles can be retrieved by the dedicated methods of the [MetaboliteAttributes](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/annotations/metabolite/MetaboliteAttributes.html) class.

###### Reaction EC number, score, status, comment and PMID

Example:

```xml
<reaction id="R1" ... />
  <notes>
    <body xmlns="http://www.w3.org/1999/xhtml">
     <p>EC-NUMBER: 1.1.1.2</p>
     <p>PMID: 29718355;20444866</p>
     <p>SCORE: 3</p>
     <p>STATUS: Curated</p>
     <p>COMMENTS: Reaction for the example</p>
    </body>
  </notes>

</reaction>

```

EC number can be retrieved thanks to the method getEcNumber of the [BioReaction](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-core/latest/fr/inrae/toulouse/metexplore/met4j_core/biodata/BioReaction.html) class.

PMID, score, status, and comments can be retrieved thanks to the corresponding methods
of the [ReactionAttributes](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/annotations/reaction/ReactionAttributes.html) class.

###### Gene-Reaction associations

Genes, proteins, enzymes and links between enzymes and reactions can be created from reaction html notes.

Example:

```xml

<reaction id="R1" ... />
  <notes>
    <body xmlns="http://www.w3.org/1999/xhtml">
     <p>GENE_ASSOCIATION: G1 or (G2 and G3)</p>
    </body>
  </notes>

</reaction>

```

See the example in [Genes and Gene-reaction associations by FBCParser plugin](#genes-and-gene-reaction-associations) to see how the path between genes and reactions are created.

**Warning** If the FBCParser plugin is activated and that genes have been set, no gene (even if absent in the FBC tags) will be set from the HTML notes.

###### Pathways

Example:

```xml
<reaction id="R1" ... />
  <notes>
    <body xmlns="http://www.w3.org/1999/xhtml">
     <p>SUBSYSTEM: pathway1 || pathway2</p>
    </body>
  </notes>

</reaction>
```

Here the pathways are separated by " || " but this separator can be changed if you change the pathwaySep field in [NotesParser](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/reader/plugin/NotesParser.html).

Two pathways will be created and both will contain the R1 reaction.

**Warning** If the GroupPathwayParser plugin is activated and that pathways have been set, no pathay (even if absent in the groups tags) will be set from the HTML notes.

##### Other attributes in html notes

All other notes can be retrieved by the method getAttributes of a BioEntity instance.

#### Write a SBML

```java
JsbmlWriter writer = new JsbmlWriter("myNetwork.sbml", network);
writer.write();
```

The write method uses similar plugins than the [read method](#read-a-sbml). By default, all the plugins are activated.

- [FBC](https://sbml.org/documents/specifications/level-3/version-1/fbc/) : [FBCWriter](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/writer/plugin/FBCWriter.html)
- [groups for pathways](https://sbml.org/documents/specifications/level-3/version-1/groups/) :[GroupPathwayWriter](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/writer/plugin/GroupPathwayWriter.html)
- [Miriam annotation](https://co.mbine.org/standards/miriam) : [AnnotationWriter](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/writer/plugin/AnnotationWriter.html)
- Html notes : [NotesWriter](https://javadoc.io/doc/fr.inrae.toulouse.metexplore/met4j-io/latest/fr/inrae/toulouse/metexplore/met4j_io/jsbml/writer/plugin/NotesWriter.html).

If you don't want all the plugin data to be written in the SBML file, you can specify the plugins to be activated.

```java
JsbmlWriter writer = new JsbmlWriter("myNetwork.sbml", network);
HashSet<PackageWriter> pkgs = new HashSet<>();
pkgs.add(new AnnotationWriter());
pkgs.add(new FBCWriter());
writer.write();
```
