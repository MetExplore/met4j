# met4j-toolbox
**Met4j command-line toolbox for metabolic networks**

## Installation from source
```
git clone https://forgemia.inra.fr/metexplore/met4j.git;
cd met4j;
mvn clean install 

cd met4j-toolbox
mvn clean package
```

## Download executable jar from gitlab registry

The executable jar is downloadable in the [met4j gitlab registry](https://forgemia.inra.fr/metexplore/met4j/-/packages).


## Usage
The toolbox can be launched using
```
java -jar target/met4j-toolbox-<version>.jar
```
which will list all the contained applications that can be called using

```
java -cp target/met4j-toolbox-<version>.jar <Package>.<App name> -h
```

Log4j from jsbml can be very verbose. You can make it silent by adding this command :

```console
java -Dlog4j.configuration= -cp target/met4j-toolbox-<version>.jar ...
```

## From singularity

You need at least [singularity](https://sylabs.io/guides/3.5/user-guide/quick_start.html) v3.5.

```console
singularity pull met4j-toolbox.sif oras://registry.forgemia.inra.fr/metexplore/met4j/met4j-singularity:latest
```

If you want a specific version:

```console
singularity pull met4j-toolbox.sif oras://registry.forgemia.inra.fr/metexplore/met4j/met4j-singularity:x.y.z
```

If you want the last develop version:

```console
singularity pull met4j-toolbox.sif oras://registry.forgemia.inra.fr/metexplore/met4j/met4j-singularity:develop
```

If you want to build by yourself the singularity image:

```console
cd met4j-toolbox
mvn package
cd ../
singularity build met4j-toolbox.sif met4j.singularity
```


This will download a singularity container met4j-toolbox.sif that you can directly launch.

To list all the apps.
```console
met4j-toolbox.sif 
```

To launch a specific app, prefix its name with the last component of its package name. For instance:

```console
met4j-toolbox.sif convert.Tab2Sbml -h -in fic.tsv -sbml fic.sbml
```

By default, singularity does not see the directories that are not descendants of your home directory. To get the directories outside your home directory, you have to specify the SINGULARITY_BIND environment variable.
At least, to get the data in the default reference directory, you have to specify:
In bash:
```console
export SINGULARITY_BIND=/db
```
In csh or in tcsh
```console
setenv SINGULARITY_BIND /db
```

## From docker

First install [Docker](https://www.docker.com/).

Pull the latest met4j image:

```console
sudo docker pull metexplore/met4j:latest
```

If you want a specific version:

```console
sudo docker pull metexplore/met4j:x.y.z
```

If you want the develop version:
```console
sudo docker pull metexplore/met4j:develop
```

If you want to build by yourself the docker image:

```console
cd met4j-toolbox
mvn package
cd ../
sudo docker build -t metexplore/met4j:myversion .
```


To list all the apps:
```console
sudo docker run metexplore/met4j:latest met4j.sh
```

Don't forget to map volumes when you want to process local files.
Example:

```console
sudo docker run -v /home/lcottret/work:/work \
 metexplore/met4j:latest met4j.sh convert.Sbml2Tab \
 -in /work/toy_model.xml -out /work/toy_model.tsv
```

If you change the working directory, you have to specify "sh /usr/bin/met4j.sh":

```console
sudo docker run -w /work -v /home/lcottret/work:/work \
 metexplore/met4j:latest sh /usr/bin/met4j.sh convert.Sbml2Tab \
 -in toy_model.xml -out toy_model.tsv
```

### Galaxy instance

[Galaxy](https://galaxyproject.org/) wrappers for met4j-toolbox apps are available in the [Galaxy toolshed](https://toolshed.g2.bx.psu.edu/) (master version) and in the [Galaxy test toolsdhed](https://testtoolshed.g2.bx.psu.edu/) (develop version).
Wrappers launch the met4j singularity container, so the server where your Galaxy instance is hosted must have Singularity installed.

## Features
<table>
<thead><tr><th colspan="2">Package fr.inrae.toulouse.metexplore.met4j_toolbox</th></tr></thead>
<tbody>
<tr><td>GenerateGalaxyFiles</td><td>Create the galaxy file tree containing met4j-toolbox app wrappers<details><summary><small>more</small></summary>Create the galaxy file tree containing met4j-toolbox app wrappers<br/>Creates a directory for each app with inside the galaxy xml wrapper.<br/><br/><pre><code> -h                        : prints the help (default: false)
 -o VAL                    : output directory where the galaxy wrappers and the
                             tool_conf.xml will be written (directory tools of
                             the Galaxy directory
 -p [Docker | Singularity] : Package type (default: Singularity)
 -v VAL                    : Met4j version (default: MET4J_VERSION_TEST)
</code></pre></details></td></tr>
</tbody>
</table>
<table>
<thead><tr><th colspan="2">Package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes</th></tr></thead>
<tbody>
<tr><td>DecomposeSBML</td><td>Parse SBML to render list of composing entities: metabolites, reactions, genes and others.<details><summary><small>more</small></summary>Parse SBML to render list of composing entities: metabolites, reactions, genes, pathways and compartments. The output file is a tsv with two columns, one with entities identifiers, and one with the entity type. If no entity type is selected, by default all of them are taken into account. Only identifiers are written, attributes can be extracted from dedicated apps or from the SBML2Tab.<br/><br/><pre><code> -c (--compartments) : Extract Compartments (default: false)
 -g (--genes)        : Extract Genes (default: false)
 -h                  : prints the help (default: false)
 -i VAL              : Input SBML file
 -m (--metabolites)  : Extract Metabolites (default: false)
 -nt (--noTypeCol)   : Do not output type column (default: false)
 -o VAL              : Output file
 -p (--pathways)     : Extract Pathways (default: false)
 -r (--reactions)    : Extract Reactions (default: false)
</code></pre></details></td></tr>
<tr><td>ExtractPathways</td><td>Extract pathway(s) from GSMN<details><summary><small>more</small></summary>"Extract pathway(s) from GSMN: From a SBML file, Create a sub-network SBML file including only a selection of pathways<br/><br/><pre><code> -h     : prints the help (default: false)
 -i VAL : input SBML file
 -o VAL : output SBML file
 -p VAL : pathway identifiers, separated by "+" sign if more than one
</code></pre></details></td></tr>
<tr><td>ExtractSbmlAnnot</td><td>Extract databases' references from SBML annotations or notes.<details><summary><small>more</small></summary>Extract databases' references from SBML annotations or notes. The references are exported as a tabulated file with one column with the SBML compound, reaction or gene identifiers, and one column with the corresponding database identifier.The name of the targeted database need to be provided under the same form than the one used in the notes field or the identifiers.org uri<br/><br/><pre><code> -db VAL                                : name of the referenced database to
                                          export annotations from, as listed in
                                          notes or identifiers.org base uri
 -export [METABOLITE | REACTION | GENE] : the type of entity to extract
                                          annotation, either metabolite,
                                          reaction, or gene
 -h                                     : prints the help (default: false)
 -i VAL                                 : input SBML file
 -o VAL                                 : output file path
 -skip                                  : Skip entities without the selected
                                          annotations, by default output them
                                          with NA value (default: false)
 -uniq                                  : keep only one identifier if multiple
                                          are referenced for the same entity
                                          (default: false)
</code></pre></details></td></tr>
<tr><td>GetGenesFromReactions</td><td>Get gene lists from a list of reactions and a GSMN.<details><summary><small>more</small></summary>Get associated gene list from a list of reactions and a GSMN. Parse GSMN GPR annotations and output a tab-separated file with one row per gene, associated reaction identifiers from input file in first column, gene identifiers in second column.<br/><br/><pre><code> -col N   : Column number in reaction file (first as 1) (default: 1)
 -h       : prints the help (default: false)
 -header  : Skip reaction file header (default: false)
 -i VAL   : Input SBML file
 -o VAL   : Output file
 -r VAL   : Input Reaction file
 -sep VAL : Separator in reaction file (default: 	)
</code></pre></details></td></tr>
<tr><td>GetReactantsFromReactions</td><td>Get reactants lists from a list of reactions and a GSMN.<details><summary><small>more</small></summary>Get reactants lists from a list of reactions and a GSMN. Output a tab-separated file with one row per reactant, reaction identifiers in first column, reactant identifiers in second column. It can provides substrates, products, or both (by default). In the case of reversible reactions, all reactants are considered both substrates and products<br/><br/><pre><code> -col N            : Column number in reaction file (first as 1) (default: 1)
 -h                : prints the help (default: false)
 -header           : Skip reaction file header (default: false)
 -i VAL            : Input SBML file
 -o VAL            : Output file
 -p (--products)   : Extract products only (default: false)
 -r VAL            : Input Reaction file
 -s (--substrates) : Extract substrates only (default: false)
 -sep VAL          : Separator in reaction file (default: 	)
</code></pre></details></td></tr>
<tr><td>SbmlSetChargesFromFile</td><td>Set charge to network metabolites from a tabulated file containing the metabolite ids and the formulas<details><summary><small>more</small></summary>Set charge to network metabolites from a tabulated file containing the metabolite ids and the formulas<br/>The charge must be a number. The ids must correspond between the tabulated file and the SBML file.<br/>If prefix or suffix is different in the SBML file, use the -p or the -s options.<br/>The charge will be written in the SBML file in two locations:+<br/>- in the reaction notes (e.g. <p>charge: -1</p>)<br/>- as fbc attribute (e.g. fbc:charge="1")<br/><br/><pre><code> -c VAL    : [#] Comment String in the tabulated file. The lines beginning by
             this string won't be read (default: #)
 -cc N     : [2] number of the column where are the charges (default: 2)
 -ci N     : [1] number of the column where are the metabolite ids (default: 1)
 -h        : prints the help (default: false)
 -n N      : [0] Number of lines to skip at the beginning of the tabulated file
             (default: 0)
 -out VAL  : [out.sbml] Out sbml file (default: out.sbml)
 -p        : [deactivated] To match the objects in the sbml file, adds the
             prefix M_ to metabolite ids (default: false)
 -s        : [deactivated] To match the objects in the sbml file, adds the
             suffix _comparmentID to metabolites (default: false)
 -sbml VAL : Original sbml file
 -tab VAL  : Tabulated file
</code></pre></details></td></tr>
<tr><td>SbmlSetEcsFromFile</td><td>Set EC numbers to reactions from a tabulated file containing the reaction ids and the EC<details><summary><small>more</small></summary>Set EC numbers to reactions from a tabulated file containing the reaction ids and the EC<br/>The ids must correspond between the tabulated file and the SBML file.<br/>If prefix R_ is present in the ids in the SBML file and not in the tabulated file, use the -p option.<br/>The EC will be written in the SBML file in two locations:<br/>- in the reaction notes (e.g. <p>EC_NUMBER: 2.4.2.14</p>)<br/>- as a reaction annotation (e.g. <rdf:li rdf:resource="http://identifiers.org/ec-code/2.4.2.14"/>)<br/><br/><pre><code> -c VAL    : [#] Comment String in the tabulated file. The lines beginning by
             this string won't be read (default: #)
 -cec N    : [2] number of the column where are the ecs (default: 2)
 -ci N     : [1] number of the column where are the reaction ids (default: 1)
 -h        : prints the help (default: false)
 -n N      : [0] Number of lines to skip at the beginning of the tabulated file
             (default: 0)
 -out VAL  : [out.sbml] Out sbml file (default: out.sbml)
 -p        : [deactivated] To match the objects in the sbml file, adds the
             prefix R_ to reactions (default: false)
 -sbml VAL : Original sbml file
 -tab VAL  : Tabulated file
</code></pre></details></td></tr>
<tr><td>SbmlSetFormulasFromFile</td><td>Set Formula to network metabolites from a tabulated file containing the metabolite ids and the formulas<details><summary><small>more</small></summary>Set Formula to network metabolites from a tabulated file containing the metabolite ids and the formulas<br/>The ids must correspond between the tabulated file and the SBML file.<br/>If prefix or suffix is different in the SBML file, use the -p or the -s options.<br/>The formula will be written in the SBML file in two locations:+<br/>- in the metabolite notes (e.g. <p>formula: C16H29O2</p><br/><br/>- as a fbc attribute (e.g. fbc:chemicalFormula="C16H29O2")<br/><br/><pre><code> -c VAL    : [#] Comment String in the tabulated file. The lines beginning by
             this string won't be read (default: #)
 -cf N     : [2] number of the column where are the formulas (default: 2)
 -ci N     : [1] number of the column where are the metabolite ids (default: 1)
 -h        : prints the help (default: false)
 -n N      : [0] Number of lines to skip at the beginning of the tabulated file
             (default: 0)
 -out VAL  : [out.sbml] Out sbml file (default: out.sbml)
 -p        : [deactivated] To match the objects in the sbml file, adds the
             prefix M_ to metabolite ids (default: false)
 -s        : [deactivated] To match the objects in the sbml file, adds the
             suffix _comparmentID to metabolites (default: false)
 -sbml VAL : Original sbml file
 -tab VAL  : Tabulated file
</code></pre></details></td></tr>
<tr><td>SbmlSetGprsFromFile</td><td>Create a new SBML file from an original sbml file and a tabulated file containing reaction ids and Gene association written in a cobra way<details><summary><small>more</small></summary>Create a new SBML file from an original sbml file and a tabulated file containing reaction ids and Gene association written in a cobra way<br/>The ids must correspond between the tabulated file and the SBML file.<br/>If prefix R_ is present in the ids in the SBML file and not in the tabulated file, use the -p option.<br/>GPR must be written in a cobra way in the tabulated file as described in Schellenberger et al 2011 Nature Protocols 6(9):1290-307<br/>(The GPR will be written in the SBML file in two locations:<br/>- in the reaction notes <p>GENE_ASSOCIATION: ( XC_0401 ) OR ( XC_3282 )</p><br/>- as fbc gene product association :<fbc:geneProductAssociation><br/> <fbc:or><br/>  <fbc:geneProductRef fbc:geneProduct="XC_3282"/><br/>  <fbc:geneProductRef fbc:geneProduct="XC_0401"/><br/> </fbc:or><br/></fbc:geneProductAssociation><br/><br/><br/><pre><code> -c VAL    : [#] Comment String in the tabulated file. The lines beginning by
             this string won't be read (default: #)
 -cgpr N   : [2] number of the column where are the gprs (default: 2)
 -ci N     : [1] number of the column where are the reaction ids (default: 1)
 -h        : prints the help (default: false)
 -n N      : [0] Number of lines to skip at the beginning of the tabulated file
             (default: 0)
 -out VAL  : [out.sbml] Out sbml file (default: out.sbml)
 -p        : [deactivated] To match the objects in the sbml file, adds the
             prefix R_ to reactions (default: false)
 -sbml VAL : Original sbml file
 -tab VAL  : Tabulated file
</code></pre></details></td></tr>
<tr><td>SbmlSetIdsFromFile</td><td>Set new ids to network objects from a tabulated file containing the old ids and the new ids<details><summary><small>more</small></summary>Set new ids to network objects from a tabulated file containing the old ids and the new ids<br/>The ids must correspond between the tabulated file and the SBML file.<br/>If prefix or suffix is different in the SBML file, use the -p or the -s options.<br/><br/><br/><pre><code> -c VAL                                 : [#] Comment String in the tabulated
                                          file. The lines beginning by this
                                          string won't be read (default: #)
 -ci N                                  : [1] number of the column where are
                                          the object ids (default: 1)
 -cnew N                                : [2] number of the column where are
                                          the new ids (default: 2)
 -h                                     : prints the help (default: false)
 -n N                                   : [0] Number of lines to skip at the
                                          beginning of the tabulated file
                                          (default: 0)
 -o [REACTION | METABOLITE | GENE |     : [REACTION] Object type in the column
 PROTEIN | PATHWAY | COMPARTMENT]         id : REACTION;METABOLITE;GENE;PATHWAY
                                          (default: REACTION)
 -out VAL                               : [out.sbml] Out sbml file (default:
                                          out.sbml)
 -p                                     : [deactivated] To match the objects in
                                          the sbml file, adds the prefix R_ to
                                          reactions and M_ to metabolites
                                          (default: false)
 -s                                     : [deactivated] To match the objects in
                                          the sbml file, adds the suffix
                                          _comparmentID to metabolites
                                          (default: false)
 -sbml VAL                              : Original sbml file
 -tab VAL                               : Tabulated file
</code></pre></details></td></tr>
<tr><td>SbmlSetNamesFromFile</td><td>Set names to network objects from a tabulated file containing the object ids and the names<details><summary><small>more</small></summary>Set names to network objects from a tabulated file containing the object ids and the names<br/>The ids must correspond between the tabulated file and the SBML file.<br/>If prefix or suffix is different in the SBML file, use the -p or the -s options.<br/><br/><br/><pre><code> -c VAL                                 : [#] Comment String in the tabulated
                                          file. The lines beginning by this
                                          string won't be read (default: #)
 -ci N                                  : [1] number of the column where are
                                          the object ids (default: 1)
 -cname N                               : [2] number of the column where are
                                          the names (default: 2)
 -h                                     : prints the help (default: false)
 -n N                                   : [0] Number of lines to skip at the
                                          beginning of the tabulated file
                                          (default: 0)
 -o [REACTION | METABOLITE | GENE |     : [REACTION] Object type in the column
 PROTEIN | PATHWAY | COMPARTMENT]         id : REACTION;METABOLITE;GENE;PATHWAY
                                          (default: REACTION)
 -out VAL                               : [out.sbml] Out sbml file (default:
                                          out.sbml)
 -p                                     : [deactivated] To match the objects in
                                          the sbml file, adds the prefix R_ to
                                          reactions and M_ to metabolites
                                          (default: false)
 -s                                     : [deactivated] To match the objects in
                                          the sbml file, adds the suffix
                                          _comparmentID to metabolites
                                          (default: false)
 -sbml VAL                              : Original sbml file
 -tab VAL                               : Tabulated file
</code></pre></details></td></tr>
<tr><td>SbmlSetPathwaysFromFile</td><td>Set pathway to reactions in a network from a tabulated file containing the reaction ids and the pathways<details><summary><small>more</small></summary>Set pathway to reactions in a network from a tabulated file containing the reaction ids and the pathways<br/>The ids must correspond between the tabulated file and the SBML file.<br/>If prefix R_ is present in the ids in the SBML file and not in the tabulated file, use the -p option.<br/>Pathways will be written in the SBML file in two ways:- as reaction note (e.g. <p>SUBSYSTEM: purine_biosynthesis</p>)- as SBML group:<br/><groups:group groups:id="purine_biosynthesis" groups:kind="classification" groups:name="purine_biosynthesis"><br/> <groups:listOfMembers><br/>  <groups:member groups:idRef="R_GLUPRT"/><br/>  <groups:member groups:idRef="R_RNDR1b"/><br/>...<br/><br/><br/><pre><code> -c VAL    : [#] Comment String in the tabulated file. The lines beginning by
             this string won't be read (default: #)
 -ci N     : [1] number of the column where are the reaction ids (default: 1)
 -cp N     : [2] number of the column where are the pathways (default: 2)
 -h        : prints the help (default: false)
 -n N      : [0] Number of lines to skip at the beginning of the tabulated file
             (default: 0)
 -out VAL  : [out.sbml] Out sbml file (default: out.sbml)
 -p        : [deactivated] To match the objects in the sbml file, adds the
             prefix R_ to reactions (default: false)
 -sbml VAL : Original sbml file
 -sep VAL  : [|] Separator of pathways in the tabulated file (default: |)
 -tab VAL  : Tabulated file
</code></pre></details></td></tr>
<tr><td>SbmlSetRefsFromFile</td><td>Add refs to network objects from a tabulated file containing the metabolite ids and the formulas<details><summary><small>more</small></summary>Add refs to network objects from a tabulated file containing the metabolite ids and the formulas<br/>Reference name given as parameter (-ref) must correspond to an existing id the registry of  identifiers.org (https://registry.identifiers.org/registry)<br/>The corresponding key:value pair will be written as metabolite or reaction annotation<br/><br/><pre><code> -c VAL                                 : [#] Comment String in the tabulated
                                          file. The lines beginning by this
                                          string won't be read (default: #)
 -ci N                                  : [1] number of the column where are
                                          the object ids (default: 1)
 -cr N                                  : [2] number of the column where are
                                          the references (default: 2)
 -h                                     : prints the help (default: false)
 -n N                                   : [0] Number of lines to skip at the
                                          beginning of the tabulated file
                                          (default: 0)
 -o [REACTION | METABOLITE | GENE |     : [REACTION] Object type in the column
 PROTEIN | PATHWAY | COMPARTMENT]         id : REACTION;METABOLITE;GENE;PATHWAY
                                          (default: REACTION)
 -out VAL                               : [out.sbml] Out sbml file (default:
                                          out.sbml)
 -p                                     : [deactivated] To match the objects in
                                          the sbml file, adds the prefix R_ to
                                          reactions and M_ to metabolites
                                          (default: false)
 -ref VAL                               : Name of the ref. Must exist in
                                          identifiers.org
 -s                                     : [deactivated] To match the objects in
                                          the sbml file, adds the suffix
                                          _comparmentID to metabolites
                                          (default: false)
 -sbml VAL                              : Original sbml file
 -tab VAL                               : Tabulated file
</code></pre></details></td></tr>
<tr><td>SbmlToMetaboliteTable</td><td>Create a tabulated file with metabolite attributes from a SBML file<details><summary><small>more</small></summary>Create a tabulated file with metabolite attributes from a SBML file<br/><br/><pre><code> -h     : prints the help (default: false)
 -o VAL : Output file
 -s VAL : Sbml file
</code></pre></details></td></tr>
</tbody>
</table>
<table>
<thead><tr><th colspan="2">Package fr.inrae.toulouse.metexplore.met4j_toolbox.bigg</th></tr></thead>
<tbody>
<tr><td>GetModelProteome</td><td>Get proteome in fasta format of a model present in BIGG<details><summary><small>more</small></summary>Get proteome in fasta format of a model present in BIGG<br/><br/><pre><code> -h     : prints the help (default: false)
 -m VAL : [ex: iMM904] id of the BIGG model
 -o VAL : [proteome.fas] path of the output file (default: proteome.fas)
</code></pre></details></td></tr>
</tbody>
</table>
<table>
<thead><tr><th colspan="2">Package fr.inrae.toulouse.metexplore.met4j_toolbox.convert</th></tr></thead>
<tbody>
<tr><td>FbcToNotes</td><td>Convert FBC package annotations to sbml notes<details><summary><small>more</small></summary>Convert FBC package annotations to sbml notes<br/><br/><pre><code> -h     : prints the help (default: false)
 -i VAL : input file
 -o VAL : output file
</code></pre></details></td></tr>
<tr><td>Kegg2Sbml</td><td>Build a SBML file from KEGG organism-specific pathways. Uses Kegg API.<details><summary><small>more</small></summary>Build a SBML file from KEGG organism-specific pathways. Uses Kegg API.<br/>Errors returned by this program could be due to Kegg API dysfunctions or limitations. Try later if this problem occurs.<br/><br/><pre><code> -h        : prints the help (default: false)
 -org VAL  : [] Kegg org id. Must be 3 letters ( (default: )
 -sbml VAL : [out.sbml] Out sbml file (default: out.sbml)
</code></pre></details></td></tr>
<tr><td>SBMLwizard</td><td>General SBML model processing<details><summary><small>more</small></summary>General SBML model processing including compound removal (such as side compounds or isolated compounds), reaction removal (ex. blocked or exchange reaction), and compartment merging<br/><br/><pre><code> -h                                     : prints the help (default: false)
 -mc (--mergecomp) [no | by_name |      : merge compartments using the provided
 by_id]                                   strategy. No merge by default.
                                          "by_name" can be used if names are
                                          consistent and unambiguous across
                                          compartments, "by_id" can be used if
                                          compartment suffix is present in
                                          compounds identifiers (id in form
                                          "xxx_y" with xxx as base identifier
                                          and y as compartment label).
                                          (default: no)
 -o VAL                                 : output SBML file
 -r0 (--noFlux)                         : remove reactions with lower and upper
                                          flux bounds both set to 0.0 (default:
                                          false)
 -rEX (--removeExchange) VAL            : remove exchange reactions and species
                                          from given exchange compartment
                                          identifier
 -rc VAL                                : file containing identifiers of
                                          compounds to remove from the
                                          metabolic network
 -rdr (--noDuplicated)                  : remove duplicated reactions (same
                                          reactants, same GPR) (default: false)
 -ric (--noIsolated)                    : remove isolated compounds (not
                                          involved in any reaction) (default:
                                          false)
 -rr VAL                                : file containing identifiers of
                                          reactions to remove from the
                                          metabolic network
 -s VAL                                 : input SBML file
</code></pre></details></td></tr>
<tr><td>Sbml2Graph</td><td>Create a graph representation of a SBML file content, and export it in graph file format.<details><summary><small>more</small></summary>Create a graph representation of a SBML file content, and export it in graph file format.<br/>The graph can be either a compound graph or a bipartite graph, and can be exported in gml or tabulated file format.<br/><br/><pre><code> -b (--bipartite) : create bipartite graph (default: false)
 -c (--compound)  : create compound graph (default: true)
 -gml             : export in GML file (default: true)
 -h               : prints the help (default: false)
 -i VAL           : input SBML file
 -o VAL           : output Graph file
 -r (--reaction)  : create reaction graph (default: false)
 -tab             : export in tabulated file (default: false)
</code></pre></details></td></tr>
<tr><td>Sbml2Tab</td><td>Create a tabulated file from a SBML file<details><summary><small>more</small></summary>Create a tabulated file from a SBML file<br/><br/><pre><code> -h       : prints the help (default: false)
 -i VAL   : [-->] String for irreversible reaction (default: -->)
 -in VAL  : Sbml file
 -out VAL : [out.tsv] Tabulated file (default: out.tsv)
 -r VAL   : [<==>] String for reversible reaction (default: <==>)
</code></pre></details></td></tr>
<tr><td>Tab2Sbml</td><td>Create a Sbml File from a tabulated file that contains the reaction ids and the formulas<details><summary><small>more</small></summary>Create a Sbml File from a tabulated file that contains the reaction ids and the formulas<br/><br/><pre><code> -cf N     : [2] number of the column where are the reaction formulas (default:
             2)
 -ci N     : [1] number of the column where are the reaction ids (default: 1)
 -cpt      : [deactivated] Create compartment from metabolite suffixes. If this
             option is deactivated, only one compartment (the default
             compartment) will be created (default: false)
 -dcpt VAL : [c] Default compartment (default: c)
 -e VAL    : [_b] flag to assign metabolite as external (default: _b)
 -h        : prints the help (default: false)
 -i VAL    : [-->] String for irreversible reaction (default: -->)
 -id VAL   : [NA] Model id written in the SBML file (default: NA)
 -in VAL   : Tabulated file
 -mp       : [deactivated] format the metabolite ids in a Palsson way (M_***_c)
             (default: false)
 -n N      : [0] Number of lines to skip at the beginning of the tabulated file
             (default: 0)
 -r VAL    : [<==>] String for reversible reaction (default: <==>)
 -rp       : [deactivated] format the reaction ids in a Palsson way (R_***)
             (default: false)
 -sbml VAL : [out.sbml] Out sbml file (default: out.sbml)
</code></pre></details></td></tr>
</tbody>
</table>
<table>
<thead><tr><th colspan="2">Package fr.inrae.toulouse.metexplore.met4j_toolbox.mapping</th></tr></thead>
<tbody>
<tr><td>NameMatcher</td><td>This tool runs edit-distance based fuzzy matching to perform near-similar name matching between a metabolic model and a list of chemical names in a dataset. A harmonization processing is performed on chemical names with substitutions of common patterns among synonyms, in order to create aliases on which classical fuzzy matching can be run efficiently.<details><summary><small>more</small></summary>Metabolic models and Metabolomics Data often refer compounds only by using their common names, which vary greatly according to the source, thus impeding interoperability between models, databases and experimental data. This requires a tedious step of manual mapping. Fuzzy matching is a range of methods which can potentially helps fasten this process, by allowing the search for near-similar names. Fuzzy matching is primarily designed for common language search engines and is frequently based on edit distance, i.e. the number of edits to transform a character string into another, effectively managing typo, case and special character variations, and allowing auto-completion. However, edit-distance based search fall short when mapping chemical names: As an example, alpha-D-Glucose et Glucose would require more edits than between Fructose and Glucose.<br/><br/>This tool runs edit-distance based fuzzy matching to perform near-similar name matching between a metabolic model and a list of chemical names in a dataset. A harmonization processing is performed on chemical names with substitutions of common patterns among synonyms, in order to create aliases on which classical fuzzy matching can be run efficiently.<br/><br/><pre><code> -c VAL        : [#] Comment String in the compound file. The lines beginning
                 by this string won't be read (default: #)
 -col N        : [1] column containing compounds' names (default: 1)
 -compound VAL : Compound file containing one column with compound names to
                 search among the SBML entries
 -h            : prints the help (default: false)
 -nMatch N     : [1] Number of match to return per name (default: 1)
 -o VAL        : Output file
 -sbml VAL     : Original sbml file
 -sep VAL      : [\t] separator in the compound file to split the colmumns.
                 (default: 	)
 -skip N       : [0] Number of lines to skip at the beginning of the compound
                 file (default: 0)
</code></pre></details></td></tr>
<tr><td>ORApathwayEnrichment</td><td>Perform Over Representation Analysis for Pathway Enrichment, using one-tailed exact Fisher Test.<br/>The fisher exact test compute the probability p to randomly get the given set of value. <br/>This version compute the probability to get at least the given overlap between the given set and the given modality :<br/>Sum the hypergeometric probability with increasing target/query intersection cardinality.<br/><br/>The hypergeometric probability is computed from the following contingency table entries.<br/>(value in cells correspond to the marginal totals of each intersection groups)<br/>				Query	!Query<br/>	Target		a		b<br/>	!Target		c		d<br/><br/>The probability of obtaining the set of value is computed as following:<br/>p = ((a+b)!(c+d)!(a+c)!(b+d)!)/(a!b!c!d!(a+b+c+d)!)<br/><br/>The obtained p-value is then adjusted for multiple testing using one of the following methods:<br/> - Bonferroni: adjusted p-value = p*n<br/> - Benjamini-Hochberg: adjusted p-value = p*n/k<br/> - Holm-Bonferroni: adjusted p-value = p*(n+1-k)<br/>n : number of tests; k : pvalue rank<details><summary><small>more</small></summary>Perform Over Representation Analysis for Pathway Enrichment, using one-tailed exact Fisher Test.<br/><br/><pre><code> -c (--correction) [Bonferroni |        : Method for multiple testing p-value
 BenjaminiHochberg | HolmBonferroni]      adjustment. (default: BenjaminiHochber
                                          g)
 -h                                     : prints the help (default: false)
 -i (--data) VAL                        : Input data : Compounds of interest
                                          file, as one SBML specie identifier
                                          per line
 -o (--output) VAL                      : Output file : tabulated file with
                                          pathway identifier, pathway name,
                                          adjusted p-value.
 -s (--sbml) VAL                        : Input model : SBML file with pathway
                                          annotation
 -th (--threshold) N                    : threshold to select significant
                                          pathways. No filtering if <=0
                                          (default: 0.0)
</code></pre></details></td></tr>
</tbody>
</table>
<table>
<thead><tr><th colspan="2">Package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis</th></tr></thead>
<tbody>
<tr><td>BipartiteDistanceMatrix</td><td>Create a compound to reactions distance matrix.<details><summary><small>more</small></summary>Create a compound to reactions distance matrix.<br/>The distance between two nodes (metabolite or reaction) is computed as the length of the shortest path connecting the two in the bipartite graph, Bipartite graph are composed of two distinct sets of nodes and two nodes can be linked only if they are from distinct sets.<br/>Therefore a metabolite node can be linked to a reaction node if the metabolite is a substrate or product of the reaction.<br/>An optional custom edge weighting can be used, turning the distances into the sum of edge weights in the lightest path, rather than the length of the shortest path.Custom weighting can be provided in a file. In that case, edges without weight are ignored during path search.<br/>If no edge weighting is set, it is recommended to provide a list of side compounds to ignore during network traversal.<br/><br/><pre><code> -f (--full)          : compute full pairwise matrix from both reactions and
                        compounds lists (default: false)
 -h                   : prints the help (default: false)
 -i VAL               : input SBML file
 -m (--mets) VAL      : an optional file containing list of compounds of
                        interest.
 -o VAL               : output Matrix file
 -r (--rxns) VAL      : an optional file containing list of reactions of
                        interest.
 -re (--rExclude) VAL : an optional file containing list of reactions to ignore
 -sc (--side) VAL     : an optional file containing list of side compounds to
                        ignore
 -u (--undirected)    : Ignore reaction direction (default: false)
 -w (--weights) VAL   : an optional file containing weights for compound pairs
</code></pre></details></td></tr>
<tr><td>CarbonSkeletonNet</td><td>Create a carbon skeleton graph representation of a SBML file content, using GSAM atom-mapping file (see https://forgemia.inra.fr/metexplore/gsam)<details><summary><small>more</small></summary>Metabolic networks used for quantitative analysis often contain links that are irrelevant for graph-based structural analysis. For example, inclusion of side compounds or modelling artifacts such as 'biomass' nodes. Focusing on links between compounds that share parts of their carbon skeleton allows to avoid many transitions involving side compounds, and removes entities without defined chemical structure. This app produce a Carbon Skeleton Network relevant for graph-based analysis of metabolism, in GML or matrix format, from a SBML and an GSAM atom mapping file. GSAM (see https://forgemia.inra.fr/metexplore/gsam) perform atom mapping at genome-scale level using the Reaction Decoder Tool (https://github.com/asad/ReactionDecoder) and allows to compute the number of conserved atoms of a given type between reactants.This app also enable Markov-chain based analysis of metabolic networks by computing reaction-normalized transition probabilities on the Carbon Skeleton Network.<br/><br/><pre><code> -am (--asmatrix)             : export as matrix (implies simple graph
                                conversion). Default export as GML file
                                (default: false)
 -g VAL                       : input GSAM file
 -h                           : prints the help (default: false)
 -i (--fromIndexes)           : Use GSAM output with carbon indexes (default:
                                false)
 -ks (--keepSingleCarbon)     : keep edges involving single-carbon compounds,
                                such as CO2 (requires formulas in SBML)
                                (default: false)
 -main (--onlyMainTransition) : Compute RPAIRS-like tags and keep only main
                                transitions for each reaction (default: false)
 -mc (--nocomp)               : merge compartments (requires unique compound
                                names that are consistent across compartments)
                                (default: false)
 -me (--simple)               : merge parallel edges to produce a simple graph
                                (default: false)
 -o VAL                       : output Graph file
 -ri (--removeIsolatedNodes)  : remove isolated nodes (default: false)
 -s VAL                       : input SBML file
 -tp (--transitionproba)      : set transition probability as weight (default:
                                false)
 -un (--undirected)           : create as undirected (default: false)
</code></pre></details></td></tr>
<tr><td>ChemSimilarityWeighting</td><td>Provides tabulated compound graph edge list, with one column with reactant pair's chemical similarity.<details><summary><small>more</small></summary>Provides tabulated compound graph edge list, with one column with reactant pair's chemical similarity.Chemical similarity has been proposed as edge weight for finding meaningful paths in metabolic networks, using shortest (lightest) path search. See McSha et al. 2003 (https://doi.org/10.1093/bioinformatics/btg217), Rahman et al. 2005 (https://doi.org/10.1093/bioinformatics/bti116) and Pertusi et al. 2014 (https://doi.org/10.1093/bioinformatics/btu760)<br/><br/><pre><code> -d (--asDist)                          : Use distance rather than similarity
                                          (default: false)
 -f (--fingerprint) [EState | Extended  : The chemical fingerprint to use
 | KlekotaRoth | MACCS | PubChem]         (default: Extended)
 -h                                     : prints the help (default: false)
 -in (--inchiFile) VAL                  : If not present in SBML's annotations,
                                          get structure from a tabulated file
                                          with first column as compound id and
                                          second column as InChI string, no
                                          header.
 -mc (--mergecomp) [no | by_name |      : merge compartments. Use names if
 by_id]                                   consistent and unambiguous across
                                          compartments, or identifiers if
                                          compartment suffix is present (id in
                                          form "xxx_y" with xxx as base
                                          identifier and y as compartment
                                          label). (default: no)
 -me (--simple)                         : merge parallel edges to produce a
                                          simple graph (default: false)
 -nan (--removeNaN)                     : do not output edges with undefined
                                          weight (default: false)
 -o VAL                                 : output edge weight file
 -s VAL                                 : input SBML file
 -sc VAL                                : input Side compound file
 -sm (--smileFile) VAL                  : If not present in SBML's annotations,
                                          get structure from a tabulated file
                                          with first column as compound id and
                                          second column as SMILE string, no
                                          header. Ignored if inchi file is
                                          provided
 -tp (--transitionproba)                : set weight as random walk transition
                                          probability, normalized by reaction
                                          (default: false)
 -un (--undirected)                     : create as undirected (default: false)
</code></pre></details></td></tr>
<tr><td>ChokePoint</td><td>Compute the Choke points of a metabolic network.<details><summary><small>more</small></summary>Compute the Choke points of a metabolic network.<br/>Choke points constitute an indicator of lethality and can help identifying drug target Choke points are reactions that are required to consume or produce one compound. Targeting of choke point can lead to the accumulation or the loss of some metabolites, thus choke points constitute an indicator of lethality and can help identifying drug target <br/>See : Syed Asad Rahman, Dietmar Schomburg; Observing local and global properties of metabolic pathways: ‘load points’ and ‘choke points’ in the metabolic networks. Bioinformatics 2006; 22 (14): 1767-1774. doi: 10.1093/bioinformatics/btl181<br/><br/><pre><code> -h     : prints the help (default: false)
 -i VAL : input SBML file
 -o VAL : output results file
</code></pre></details></td></tr>
<tr><td>CompoundNet</td><td>Advanced creation of a compound graph representation of a SBML file content<details><summary><small>more</small></summary>Metabolic networks used for quantitative analysis often contain links that are irrelevant for graph-based structural analysis. For example, inclusion of side compounds or modelling artifacts such as 'biomass' nodes.<br/>While Carbon Skeleton Graph offer a relevant alternative topology for graph-based analysis, it requires compounds' structure information, usually not provided in model, and difficult to retrieve for model with sparse cross-reference annotations.<br/>In contrary to the SBML2Graph app that performs a raw conversion of the SBML content, the present app propose a fine-tuned creation of compound graph from predefined list of side compounds and degree² weighting to get relevant structure without structural data.This app also enable Markov-chain based analysis of metabolic networks by computing reaction-normalized transition probabilities on the network.<br/><br/><pre><code> -am (--asmatrix)                       : export as matrix (implies simple
                                          graph conversion). Default export as
                                          GML file (default: false)
 -cw (--customWeights) VAL              : an optional file containing weights
                                          for compound pairs
 -dw (--degreeWeights)                  : penalize traversal of hubs by using
                                          degree square weighting (default:
                                          false)
 -h                                     : prints the help (default: false)
 -mc (--mergecomp) [no | by_name |      : merge compartments. Use names if
 by_id]                                   consistent and unambiguous across
                                          compartments, or identifiers if
                                          compartment suffix is present (id in
                                          form "xxx_y" with xxx as base
                                          identifier and y as compartment
                                          label). (default: no)
 -me (--simple)                         : merge parallel edges to produce a
                                          simple graph (default: false)
 -o VAL                                 : output Graph file
 -ri (--removeIsolatedNodes)            : remove isolated nodes (default: false)
 -s VAL                                 : input SBML file
 -sc VAL                                : input Side compound file
 -tp (--transitionproba)                : set weight as random walk transition
                                          probability, normalized by reaction
                                          (default: false)
 -un (--undirected)                     : create as undirected (default: false)
</code></pre></details></td></tr>
<tr><td>DegreeWeighting</td><td>Provides tabulated compound graph edge list, with one column with target's degree.<details><summary><small>more</small></summary>Provides tabulated compound graph edge list, with one column with target's degree.Degree has been proposed as edge weight for finding meaningful paths in metabolic networks, using shortest (lightest) path search. See Croes et al. 2006 (https://doi.org/10.1016/j.jmb.2005.09.079) and Croes et al. 2005 (https://doi.org/10.1093/nar/gki437)<br/><br/><pre><code> -h                                     : prints the help (default: false)
 -mc (--mergecomp) [no | by_name |      : merge compartments. Use names if
 by_id]                                   consistent and unambiguous across
                                          compartments, or identifiers if
                                          compartment suffix is present (id in
                                          form "xxx_y" with xxx as base
                                          identifier and y as compartment
                                          label). (default: no)
 -me (--simple)                         : merge parallel edges to produce a
                                          simple graph (default: false)
 -nan (--removeNaN)                     : do not output edges with undefined
                                          weight (default: false)
 -o VAL                                 : output edge weight file
 -pow (--power) N                       : set weights as the degree raised to
                                          the power of number in parameter.
                                          (default: 1)
 -s VAL                                 : input SBML file
 -sc VAL                                : input Side compound file
 -tp (--transitionproba)                : set weight as random walk transition
                                          probability, normalized by reaction
                                          (default: false)
 -un (--undirected)                     : create as undirected (default: false)
</code></pre></details></td></tr>
<tr><td>DistanceMatrix</td><td>Create a compound to compound distance matrix.<details><summary><small>more</small></summary>Create a compound to compound distance matrix.<br/>The distance between two compounds is computed as the length of the shortest path connecting the two in the compound graph, where two compounds are linked if they are respectively substrate and product of the same reaction.<br/>An optional edge weighting can be used, turning the distances into the sum of edge weights in the lightest path, rather than the length of the shortest path.The default weighting use target's degree squared. Alternatively, custom weighting can be provided in a file. In that case, edges without weight are ignored during path search.<br/>If no edge weighting is set, it is recommended to provide a list of side compounds to ignore during network traversal.<br/><br/><pre><code> -dw (--degree)     : penalize traversal of hubs by using degree square
                      weighting (-w must not be set) (default: false)
 -h                 : prints the help (default: false)
 -i VAL             : input SBML file
 -o VAL             : output Matrix file
 -s (--seed) VAL    : an optional file containing list of compounds of
                      interest. The returned distance matrix contains only the
                      corresponding rows and columns
 -sc (--side) VAL   : an optional file containing list of side compounds to
                      ignore
 -u (--undirected)  : Ignore reaction direction (default: false)
 -w (--weights) VAL : an optional file containing weights for compound pairs
</code></pre></details></td></tr>
<tr><td>ExtractSubBipNetwork</td><td>Create a subnetwork from a GSMN in SBML format, and two files containing lists of compounds and/or reactions of interests ids, one per row, plus one file of the same format containing side compounds ids.<details><summary><small>more</small></summary>Create a subnetwork from a GSMN in SBML format, and two files containing lists of compounds and/or reactions of interests ids, one per row, plus one file of the same format containing side compounds ids.<br/>The subnetwork corresponds to part of the network that connects reactions and compounds from the first list to reactions and compounds from the second list.<br/>Sources and targets list can have elements in common. The connecting part can be defined as the union of shortest or k-shortest paths between sources and targets, or the Steiner tree connecting them. Contrary to compound graph, bipartite graph often lacks weighting policy for edge relevance. In order to ensure appropriate network density, a list of side compounds and blocked reactions to ignore during path build must be provided. An optional edge weight file, if available, can also be used.<br/><br/><pre><code> -br (--blokedReactions) VAL : a file containing list of blocked reactions to
                               ignore
 -cw (--customWeights) VAL   : an optional file containing weights for
                               reactions pairs
 -h                          : prints the help (default: false)
 -i VAL                      : input SBML file
 -k N                        : Extract k-shortest paths (default: 1)
 -o VAL                      : output gml file
 -s VAL                      : input sources txt file
 -sc (--side) VAL            : a file containing list of side compounds to
                               ignore
 -st (--steinertree)         : Extract Steiner Tree (default: false)
 -t VAL                      : input targets txt file
 -tab (--asTable)            : Export in tabulated file instead of .GML
                               (default: false)
 -u (--undirected)           : Ignore reaction direction (default: false)
</code></pre></details></td></tr>
<tr><td>ExtractSubNetwork</td><td>Create a subnetwork from a GSMN in SBML format, and two files containing lists of compounds of interests ids, one per row.<details><summary><small>more</small></summary>Create a subnetwork from a GSMN in SBML format, and two files containing lists of compounds of interests ids, one per row.<br/>The subnetwork correspond to part of the network that connects compounds from the first list to compounds from the second list.<br/>Sources and targets list can have elements in common. The connecting part can be defined as the union of shortest or k-shortest paths between sources and targets, or the Steiner tree connecting them. The relevance of considered path can be increased by weighting the edges using degree squared, chemical similarity (require InChI or SMILES annotations) or any provided weighting.<br/><br/>See previous works on subnetwork extraction for parameters recommendations:Frainay, C., & Jourdan, F. Computational methods to identify metabolic sub-networks based on metabolomic profiles. Bioinformatics 2016;1–14. https://doi.org/10.1093/bib/bbv115<br/>Faust, K., Croes, D., & van Helden, J. Prediction of metabolic pathways from genome-scale metabolic networks. Bio Systems 2011;105(2), 109–121. https://doi.org/10.1016/j.biosystems.2011.05.004<br/>Croes D, Couche F, Wodak SJ, et al. Metabolic PathFinding: inferring relevant pathways in biochemical networks. Nucleic Acids Res 2005;33:W326–30.<br/>Croes D, Couche F, Wodak SJ, et al. Inferring meaningful pathways in weighted metabolic networks. J Mol Biol 2006; 356:222–36.<br/>Rahman SA, Advani P, Schunk R, et al. Metabolic pathway analysis web service (Pathway Hunter Tool at CUBIC). Bioinformatics 2005;21:1189–93.<br/>Pertusi DA, Stine AE, Broadbelt LJ, et al. Efficient searching and annotation of metabolic networks using chemical similarity. Bioinformatics 2014;1–9.<br/>McShan DC, Rao S, Shah I. PathMiner: predicting metabolic pathways by heuristic search. Bioinformatics 2003;19:1692–8.<br/><br/><br/><pre><code> -cw (--customWeights) VAL : an optional file containing weights for compound
                             pairs
 -dw (--degreeWeights)     : penalize traversal of hubs by using degree square
                             weighting (default: false)
 -h                        : prints the help (default: false)
 -i VAL                    : input SBML file
 -k N                      : Extract k-shortest paths (default: 1)
 -o VAL                    : output gml file
 -s VAL                    : input sources txt file
 -sc (--side) VAL          : an optional file containing list of side compounds
                             to ignore
 -st (--steinertree)       : Extract Steiner Tree (default: false)
 -sw (--chemSimWeights)    : penalize traversal of non-relevant edges by using
                             chemical similarity weighting (default: false)
 -t VAL                    : input targets txt file
 -tab (--asTable)          : Export in tabulated file instead of .GML (default:
                             false)
 -u (--undirected)         : Ignore reaction direction (default: false)
</code></pre></details></td></tr>
<tr><td>ExtractSubReactionNetwork</td><td>Create a subnetwork from a GSMN in SBML format, and two files containing lists of reactions of interests ids, one per row, plus one file of the same format containing side compounds ids.<details><summary><small>more</small></summary>Create a subnetwork from a GSMN in SBML format, and two files containing lists of reactions of interests ids, one per row, plus one file of the same format containing side compounds ids.<br/>The subnetwork corresponds to part of the network that connects reactions from the first list to reactions from the second list.<br/>Sources and targets list can have elements in common. The connecting part can be defined as the union of shortest or k-shortest paths between sources and targets, or the Steiner tree connecting them. Contrary to compound graph, reaction graph often lacks weighting policy for edge relevance. In order to ensure appropriate network density, a list of side compounds to ignore for linking reactions must be provided. An optional edge weight file, if available, can also be used.<br/><br/><pre><code> -cw (--customWeights) VAL : an optional file containing weights for reactions
                             pairs
 -h                        : prints the help (default: false)
 -i VAL                    : input SBML file
 -k N                      : Extract k-shortest paths (default: 1)
 -o VAL                    : output gml file
 -re (--rExclude) VAL      : an optional file containing list of reactions to
                             ignore
 -s VAL                    : input sources txt file
 -sc (--side) VAL          : a file containing list of side compounds to ignore
 -st (--steinertree)       : Extract Steiner Tree (default: false)
 -t VAL                    : input targets txt file
 -tab (--asTable)          : Export in tabulated file instead of .GML (default:
                             false)
 -u (--undirected)         : Ignore reaction direction (default: false)
</code></pre></details></td></tr>
<tr><td>LoadPoint</td><td>Compute the Load points of a metabolic network. Load points constitute an indicator of lethality and can help identifying drug target.<details><summary><small>more</small></summary>Compute the Load points of a metabolic network. Load points constitute an indicator of lethality and can help identifying drug target.<br/>From Rahman et al. Observing local and global properties of metabolic pathways: ‘load points’ and ‘choke points’ in the metabolic networks. Bioinf. (2006):<br/>For a given metabolic network, the load L on metabolite m can be defined as :<br/>ln [(pm/km)/(∑Mi=1Pi)/(∑Mi=1Ki)]<br/>p is the number of shortest paths passing through a metabolite m;<br/>k is the number of nearest neighbour links for m in the network;<br/>P is the total number of shortest paths;<br/>K is the sum of links in the metabolic network of M metabolites (where M is the number of metabolites in the network).<br/>Use of the logarithm makes the relevant values more distinguishable.<br/><br/><pre><code> -h              : prints the help (default: false)
 -i VAL          : input SBML file
 -k (--npath) N  : Number of alternative paths to consider between a pair of
                   connected metabolites (default: 1)
 -o VAL          : output results file
 -s (--side) VAL : an optional file containing list of side compounds to ignore
</code></pre></details></td></tr>
<tr><td>MetaboRank</td><td>Compute the MetaboRank, a custom personalized PageRank for metabolic network.<details><summary><small>more</small></summary>Compute the MetaboRank, a custom personalized PageRank for metabolic network.<br/>The MetaboRank takes a metabolic network and a list of compounds of interest, and provide a score of relevance for all of the other compounds in the network.<br/>The MetaboRank can, from metabolomics results, be used to fuel a recommender system highlighting interesting compounds to investigate, retrieve missing identification and drive literature mining.<br/>It is a two dimensional centrality computed from personalized PageRank and CheiRank, with special transition probability and normalization to handle the specificities of metabolic networks.<br/>For convenience, a one dimensional centrality rank is also computed from the highest rank from PageRank or CheiRank, and using lowest rank as tie-breaker.<br/>See publication for more information: Frainay et al. MetaboRank: network-based recommendation system to interpret and enrich metabolomics results, Bioinformatics (35-2), https://doi.org/10.1093/bioinformatics/bty577<br/><br/><pre><code> -d N    : damping factor (default: 0.85)
 -h      : prints the help (default: false)
 -i VAL  : input SBML file: path to network used for computing centrality, in
           sbml format.
 -max N  : maximal number of iteration (default: 15000)
 -o VAL  : output file: path to the file where the results will be exported
 -s VAL  : input seeds file: tabulated file containing node of interest ids and
           weight
 -sc VAL : input Side compound file
 -t N    : convergence tolerance (default: 0.001)
 -w VAL  : input edge weight file: (recommended) path to file containing edges'
           weights. Will be normalized as transition probabilities
</code></pre></details></td></tr>
<tr><td>NetworkSummary</td><td>Create a report summarizing several graph measures characterising the structure of the network.<details><summary><small>more</small></summary>Use a metabolic network in SBML file and an optional list of side compounds, and produce a report summarizing several graph measures characterising the structure of the network.This includes (non-exhaustive list): size and order, connectivity, density, degree distribution, shortest paths length, top centrality nodes...<br/><br/><pre><code> -d (--directed)  : use reaction direction for distances (default: false)
 -h               : prints the help (default: false)
 -i VAL           : input SBML file
 -o VAL           : output report file
 -s (--side) VAL  : an optional file containing list of side compounds to
                    ignore (recommended)
 -sd (--skipdist) : skip full distance matrix computation (quick summary)
                    (default: false)
</code></pre></details></td></tr>
<tr><td>PathwayNet</td><td>Creation of a Pathway Network representation of a SBML file content<details><summary><small>more</small></summary>Genome-scale metabolic networks are often partitioned into metabolic pathways. Pathways are frequently considered independently despite frequent coupling in their activity due to shared metabolites. In order to decipher the interconnections linking overlapping pathways, this app proposes the creation of "Pathway Network", where two pathways are linked if they share compounds.<br/><br/><pre><code> -am (--asmatrix)             : export as matrix (implies simple graph
                                conversion). Default export as GML file
                                (default: false)
 -cw (--customWeights) VAL    : an optional file containing weights for pathway
                                pairs
 -h                           : prints the help (default: false)
 -ncw (--connectorWeights)    : set number of connecting compounds as weight
                                (default: false)
 -o VAL                       : output Graph file
 -oss (--onlySourcesAndSinks) : consider only metabolites that are source or
                                sink in the pathway (i.e non-intermediary
                                compounds) (default: false)
 -ri (--removeIsolatedNodes)  : remove isolated nodes (default: false)
 -s VAL                       : input SBML file
 -sc VAL                      : input Side compound file (recommended)
</code></pre></details></td></tr>
<tr><td>PrecursorNetwork</td><td>Perform a network expansion from a set of compound targets to create a precursor network.<details><summary><small>more</small></summary>Perform a network expansion from a set of compound targets to create a precursor network.<br/>The precursor network of a set of compounds (targets) refer to the sub-part of a metabolic network from which a target can be reachedThe network expansion process consist of adding a reaction to the network if any of its products are either a targets or a substrate of a previously added reaction<br/><br/><pre><code> -h                 : prints the help (default: false)
 -i VAL             : input SBML file: path to network used for computing
                      scope, in sbml format.
 -ir (--ignore) VAL : an optional file containing list of reaction to ignore
                      (forbid inclusion in scope)
 -o VAL             : output file: path to the .gml file where the results
                      precursor network will be exported
 -sc (--sides) VAL  : an optional file containing list of ubiquitous compounds
                      to be considered already available
 -t (--targets) VAL : input target file: tabulated file containing node of
                      interest ids
 -tab (--asTable)   : Export in tabulated file instead of .GML (default: false)
</code></pre></details></td></tr>
<tr><td>ReactionDistanceMatrix</td><td>Create a reaction to reaction distance matrix.<details><summary><small>more</small></summary>Create a reaction to reaction distance matrix.<br/>The distance between two reactions is computed as the length of the shortest path connecting the two in the reaction graph, where two reactions are linked if they produce a metabolite consumed by the other or the other way around.<br/>An optional edge weighting can be used, turning the distances into the sum of edge weights in the lightest path, rather than the length of the shortest path.The default weighting use target's degree squared. Alternatively, custom weighting can be provided in a file. In that case, edges without weight are ignored during path search.<br/>If no edge weighting is set, it is recommended to provide a list of side compounds to ignore during network traversal.<br/><br/><pre><code> -dw (--degree)       : penalize traversal of hubs by using degree square
                        weighting (-w must not be set) (default: false)
 -h                   : prints the help (default: false)
 -i VAL               : input SBML file
 -o VAL               : output Matrix file
 -re (--rExclude) VAL : an optional file containing list of reactions to ignore
 -s (--seeds) VAL     : an optional file containing list of reactions of
                        interest.
 -sc (--side) VAL     : an optional file containing list of side compounds to
                        ignore
 -u (--undirected)    : Ignore reaction direction (default: false)
 -w (--weights) VAL   : an optional file containing weights for compound pairs
</code></pre></details></td></tr>
<tr><td>ScopeNetwork</td><td>Perform a network expansion from a set of compound seeds to create a scope network<details><summary><small>more</small></summary>Perform a network expansion from a set of compound seeds to create a scope network<br/>The scope of a set of compounds (seed) refer to the maximal metabolic network that can be extended from them,where the extension process consist of adding a reaction to the network if and only if all of its substrates are either a seed or a product of a previously added reaction<br/>For more information, see Handorf, Ebenhöh and Heinrich (2005). *Expanding metabolic networks: scopes of compounds, robustness, and evolution.* Journal of molecular evolution, 61(4), 498-512. (https://doi.org/10.1007/s00239-005-0027-1)<br/><br/><pre><code> -h                 : prints the help (default: false)
 -i VAL             : input SBML file: path to network used for computing
                      scope, in sbml format.
 -ir (--ignore) VAL : an optional file containing list of reaction to ignore
                      (forbid inclusion in scope
 -o VAL             : output file: path to the .gml file where the results
                      scope network will be exported
 -s (--seeds) VAL   : input seeds file: tabulated file containing node of
                      interest ids
 -sc (--sides) VAL  : an optional file containing list of ubiquitous side
                      compounds to be considered available by default but
                      ignored during expansion
 -ssc (--showsides) : show side compounds in output network (default: false)
 -t (--trace)       : trace inclusion step index for each node in output
                      (default: false)
 -tab (--asTable)   : Export in tabulated file instead of .GML (default: false)
</code></pre></details></td></tr>
<tr><td>SeedsAndTargets</td><td>Identify exogenously acquired compounds, producible compounds exogenously available and/or dead ends metabolites from metabolic network topology<details><summary><small>more</small></summary>Identify exogenously acquired compounds, producible compounds exogenously available and/or dead ends metabolites from metabolic network topology. Metabolic seeds and targets are useful for identifying medium requirements and metabolic capability, and thus enable analysis of metabolic ties within communities of organisms.<br/>This application can use seed definition and SCC-based detection algorithm by Borenstein et al. or, alternatively, degree-based sink and source detection with compartment adjustment.<br/>The first method (see Borenstein et al. 2008 Large-scale reconstruction and phylogenetic analysis of metabolic environments https://doi.org/10.1073/pnas.0806162105) consider strongly connected components rather than individual nodes, thus, members of cycles can be considered as seed. A sink from an external compartment can however be connected to a non sink internal counterpart, thus highlighting what could end up in the external compartment rather than what must be exported.<br/>The second approach is neighborhood based and identify sources and sinks. Since "real" sinks and sources in intracellular compartment(s) may be involved in transport/exchange reactions reversible by default, thus not allowing extracellular source or sink, an option allows to take the degree (minus extracellular neighbors) of intracellular counterparts.<br/><br/><pre><code> -!s (--notSeed)         : export nodes that are not seed (default: false)
 -!t (--notTarget)       : export nodes that are not targets (default: false)
 -B (--useBorensteinAlg) : use Borenstein Algorithm. Please cite Borenstein et
                           al. 2008 Large-scale reconstruction and phylogenetic
                           analysis of metabolic environments
                           https://doi.org/10.1073/pnas.0806162105). ignore
                           internal option (default: false)
 -c (--comp) VAL         : Selected compartment(s), as model identifiers,
                           separated by "+" sign if more than one
 -h                      : prints the help (default: false)
 -i (--inputSBML) VAL    : input SBML file
 -in (--internal)        : if an external compartment is defined, adjust degree
                           by considering internal counterpart (default: false)
 -is (--keepIsolated)    : do not ignore isolated nodes, consider isolated both
                           seed and target (default: false)
 -o (--output) VAL       : output seeds file
 -s (--seeds)            : export seeds (default: false)
 -sc (--sideFile) VAL    : input Side compound file
 -t (--targets)          : export targets (default: false)
</code></pre></details></td></tr>
<tr><td>SideCompoundsScan</td><td>Scan a network to identify side-compounds.<details><summary><small>more</small></summary>Scan a network to identify side-compounds.<br/>Side compounds are metabolites of small relevance for topological analysis. Their definition can be quite subjective and varies between sources.<br/>Side compounds tend to be ubiquitous and not specific to a particular biochemical or physiological process.Compounds usually considered as side compounds include water, atp or carbon dioxide. By being involved in many reactions and thus connected to many compounds, they tend to significantly lower the average shortest path distances beyond expected metabolic relatedness.<br/>This tool attempts to propose a list of side compounds according to specific criteria:  <br/>- *Degree*: Compounds with an uncommonly high number of neighbors can betray a lack of process specificity.  <br/>High degree compounds typically include water and most main cofactors (CoA, ATP, NADPH...) but can also include central compounds such as pyruvate or acetyl-CoA  <br/>- *Neighbor Coupling*: Similar to degree, this criteria assume that side compounds are involved in many reactions, but in pairs with other side compounds.<br/>Therefore, the transition from ATP to ADP will appear multiple time in the network, creating redundant 'parallel edges' between these two neighbors.<br/>Being tightly coupled to another compound through a high number of redundant edges, can point out cofactors while keeping converging pathways' products with high degree like pyruvate aside.  <br/>- *Carbon Count*: Metabolic "waste", or degradation end-product such as ammonia or carbon dioxide are usually considered as side compounds.<br/>Most of them are inorganic compound, another ill-defined concept, sometimes defined as compound lacking C-C or C-H bonds. Since chemical structure is rarely available in SBML model beyond chemical formula, we use a less restrictive criterion by flagging compound with one or no carbons. This cover most inorganic compounds, but include few compounds such as methane usually considered as organic.  - *Chemical Formula*: Metabolic network often contains 'artifacts' that serve modelling purpose (to define a composite objective function for example). Such entities can be considered as 'side entities'. Since they are not actual chemical compounds, they can be detected by their lack of valid chemical formula. However, this can also flag main compounds with erroneous or missing annotation.<br/><br/><pre><code> -cc (--noCarbonSkeleton)            : flag as side compound any compounds with
                                       less than 2 carbons in formula (default:
                                       false)
 -d (--degree) N                     : flag as side compounds any compounds
                                       with degree above threshold (default:
                                       400)
 -dp (--degreep) N                   : flag as side compounds the top x% of
                                       compounds according to their degree
                                       (default: NaN)
 -h                                  : prints the help (default: false)
 -i VAL                              : input SBML file
 -id (--onlyIds)                     : do not report values in output, export
                                       ids list of compounds flagged as
                                       side-Compounds, allowing piping results
                                       (default: false)
 -m (--merge) [no | by_name | by_id] : Degree is shared between compounds in
                                       different compartments. Use names if
                                       consistent and unambiguous across
                                       compartments, or identifiers if
                                       compartment suffix is present (id in
                                       form "xxx_y" with xxx as base identifier
                                       and y as compartment label). (default:
                                       no)
 -nc (--neighborCoupling) N          : flag as side compound any compound with
                                       a number of parallel edges shared with a
                                       neighbor above the given threshold
                                       (default: NaN)
 -o VAL                              : output Side-Compounds file
 -s (--onlySides)                    : output compounds flagged as
                                       side-Compounds only (default: false)
 -uf (--undefinedFormula)            : flag as side compound any compounds with
                                       no valid chemical formula (default:
                                       false)
</code></pre></details></td></tr>
<tr><td>TopologicalPathwayAnalysis</td><td>Run a Topological Pathway Analysis to identify key pathways based on topological properties of its constituting compounds.<details><summary><small>more</small></summary>Run a Topological Pathway Analysis (TPA) to identify key pathways based on topological properties of its mapped compounds. From a list of compounds of interest, the app compute their betweenness centrality (which quantifies how often a compound acts as a intermediary along the shortest paths between pairs of other compounds in the network, which, if high, suggest a critical role in the overall flow within the network). Each pathway is scored according to the summed centrality of its metabolites found in the dataset. Alternatively to the betweenness, one can make use of the out-degree (the number of outgoing link, i.e. number of direct metabolic product) as a criterion of importance. TPA is complementary to statistical enrichment analysis to ensures a more meaningful interpretation of the data, by taking into account the influence of identified compounds on the structure of the pathways.<br/><br/><pre><code> -cw (--customWeights) VAL              : an optional file containing weights
                                          for compound pairs, taken into
                                          account for betweenness computation.
                                          Edges not found in file will be
                                          removed
 -h                                     : prints the help (default: false)
 -mc (--mergecomp) [no | by_name |      : merge compartments. Use names if
 by_id]                                   consistent and unambiguous across
                                          compartments, or identifiers if
                                          compartment suffix is present (id in
                                          form "xxx_y" with xxx as base
                                          identifier and y as compartment
                                          label). (default: no)
 -noi VAL                               : file containing the list of
                                          metabolites of interests (one per
                                          line)
 -o VAL                                 : output result file (tsv format)
 -out (--outDegree)                     : use out-degree as scoring function
                                          instead of betweenness (faster
                                          computation) (default: false)
 -ri (--removeIsolatedNodes)            : remove isolated nodes (default: false)
 -s VAL                                 : input SBML file
 -sc VAL                                : input Side compound file (recommended)
 -un (--undirected)                     : the compound graph built from the
                                          metabolic network and used for
                                          computations will undirected, i.e.
                                          the reaction directions won't be
                                          taken into account (default: false)
</code></pre></details></td></tr>
</tbody>
</table>
<table>
<thead><tr><th colspan="2">Package fr.inrae.toulouse.metexplore.met4j_toolbox.reconstruction</th></tr></thead>
<tbody>
<tr><td>SbmlCheckBalance</td><td>Check balance of all the reactions in a SBML.<details><summary><small>more</small></summary>Check balance of all the reactions in a SBML.<br/>A reaction is balanced if all its reactants have a chemical formula with a good syntax and if the quantity of each atom is the same in both sides of the reaction.<br/>For each reaction, indicates if the reaction is balanced, the list of the atoms and the sum of their quantity, and the list of the metabolites that don't have a correct chemical formula.<br/><br/><pre><code> -h        : prints the help (default: false)
 -out VAL  : [checkBalances.tsv] Output tabulated file (1st col: reaction id,
             2nd col: boolean indicating if the reaction is balanced, 3rd col:
             atom balances, 4th col: metabolites with bad formula (default:
             checkBalances.tsv)
 -sbml VAL : Original sbml file
</code></pre></details></td></tr>
</tbody>
</table>
