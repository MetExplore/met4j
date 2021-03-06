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
<thead><tr><th colspan="2">Package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes</th></tr></thead>
<tbody>
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
<tr><td>SbmlSetChargesFromFile</td><td>Set charge to network metabolites from a tabulated file containing the metabolite ids and the formulas<details><summary><small>more</small></summary>Set charge to network metabolites from a tabulated file containing the metabolite ids and the formulas<br/>The charge must be a number. The ids must correspond between the tabulated file and the SBML file.<br/>If prefix or suffix is different in the SBML file, use the -p or the -s options.<br/>The charge will be written in the SBML file in two locations:+<br/>- in the reaction notes (e.g. <p>charge: -1</p><br/>- as fbc attribute (e.g. fbc:charge="1")<br/><br/><pre><code> -c VAL    : [#] Comment String in the tabulated file. The lines beginning by
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
<tr><td>SbmlSetEcsFromFile</td><td>Set EC numbers to reactions from a tabulated file containing the reaction ids and the EC<details><summary><small>more</small></summary>Set EC numbers to reactions from a tabulated file containing the reaction ids and the EC<br/>The ids must correspond between the tabulated file and the SBML file.<br/>If prefix R_ is present in the ids in the SBML file and not in the tabulated file, use the -p option.<br/>The EC will be written in the SBML file in two locations:+<br/>- in the reaction notes (e.g. <p>EC_NUMBER: 2.4.2.14</p><br/>- as a reaction annotation (e.g. <rdf:li rdf:resource="http://identifiers.org/ec-code/2.4.2.14"/>)<br/><br/><pre><code> -c VAL    : [#] Comment String in the tabulated file. The lines beginning by
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
<tr><td>SbmlSetGprsFromFile</td><td>Create a new SBML file from an original sbml file and a tabulated file containing reaction ids and Gene association written in a cobra way<details><summary><small>more</small></summary>Create a new SBML file from an original sbml file and a tabulated file containing reaction ids and Gene association written in a cobra way<br/>The ids must correspond between the tabulated file and the SBML file.<br/>If prefix R_ is present in the ids in the SBML file and not in the tabulated file, use the -p option.<br/>GPR must be written in a cobra way in the tabulated file as described in Schellenberger et al 2011 Nature Protocols 6(9):1290-307<br/>(The GPR will be written in the SBML file in two locations:<br/>- in the reaction notes <p>GENE_ASSOCIATION: ( XC_0401 ) OR ( XC_3282 )</p><br/>- as fbc gene product association :       <fbc:geneProductAssociation><br/>          <fbc:or><br/>            <fbc:geneProductRef fbc:geneProduct="XC_3282"/><br/>            <fbc:geneProductRef fbc:geneProduct="XC_0401"/><br/>          </fbc:or><br/>        </fbc:geneProductAssociation><br/><br/><br/><pre><code> -c VAL    : [#] Comment String in the tabulated file. The lines beginning by
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
 PROTEIN | PATHWAY]                       id : REACTION;METABOLITE;PROTEIN;GENE;
                                          PATHWAY (default: REACTION)
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
<tr><td>SbmlSetPathwaysFromFile</td><td>Set pathway to reactions in a network from a tabulated file containing the reaction ids and the pathways<details><summary><small>more</small></summary>Set pathway to reactions in a network from a tabulated file containing the reaction ids and the pathways<br/>The ids must correspond between the tabulated file and the SBML file.<br/>If prefix R_ is present in the ids in the SBML file and not in the tabulated file, use the -p option.<br/>Pathways will be written in the SBML file in two ways:- as reaction note (e.g. <p>SUBSYSTEM: purine_biosynthesis</p>)- as SBML group:<br/>      <groups:group groups:id="purine_biosynthesis" groups:kind="classification" groups:name="purine_biosynthesis"><br/>        <groups:listOfMembers><br/>          <groups:member groups:idRef="R_GLUPRT"/><br/>          <groups:member groups:idRef="R_RNDR1b"/><br/>...<br/><br/><br/><pre><code> -c VAL    : [#] Comment String in the tabulated file. The lines beginning by
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
 PROTEIN | PATHWAY]                       id : REACTION;METABOLITE;PROTEIN;GENE;
                                          PATHWAY (default: REACTION)
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
<thead><tr><th colspan="2">Package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis</th></tr></thead>
<tbody>
<tr><td>CarbonSkeletonNet</td><td>Create a carbon skeleton graph representation of a SBML file content, using GSAM atom-mapping file (see https://forgemia.inra.fr/metexplore/gsam)<details><summary><small>more</small></summary>Metabolic networks used for quantitative analysis often contain links that are irrelevant for graph-based structural analysis. For example, inclusion of side compounds or modelling artifacts such as 'biomass' nodes. Focusing on links between compounds that share parts of their carbon skeleton allows to avoid many transitions involving side compounds, and removes entities without defined chemical structure. This app produce a Carbon Skeleton Network relevant for graph-based analysis of metabolism, in GML or matrix format, from a SBML and an GSAM atom mapping file. GSAM (see https://forgemia.inra.fr/metexplore/gsam) perform atom mapping at genome-scale level using the Reaction Decoder Tool (https://github.com/asad/ReactionDecoder) and allows to compute the number of conserved atoms of a given type between reactants.This app also enable Markov-chain based analysis of metabolic networks by computing reaction-normalized transition probabilities on the Carbon Skeleton Network.<br/><br/><pre><code> -am (--asmatrix)            : export as matrix (implies simple graph
                               conversion). Default export as GML file
                               (default: false)
 -g VAL                      : input GSAM file
 -h                          : prints the help (default: false)
 -i (--fromIndexes)          : Use GSAM output with carbon indexes (default:
                               false)
 -ks (--keepSingleCarbon)    : keep edges involving single-carbon compounds,
                               such as CO2 (requires formulas in SBML)
                               (default: false)
 -mc (--nocomp)              : merge compartments (requires unique compound
                               names that are consistent across compartments)
                               (default: false)
 -me (--simple)              : merge parallel edges to produce a simple graph
                               (default: false)
 -o VAL                      : output Graph file
 -ri (--removeIsolatedNodes) : remove isolated nodes (default: false)
 -s VAL                      : input SBML file
 -tp (--transitionproba)     : set transition probability as weight (default:
                               false)
 -un (--undirected)          : create as undirected (default: false)
</code></pre></details></td></tr>
<tr><td>ChokePoint</td><td>Compute the Choke points of a metabolic network.<details><summary><small>more</small></summary>Compute the Choke points of a metabolic network.<br/>Load points constitute an indicator of lethality and can help identifying drug target Choke points are reactions that are required to consume or produce one compound. Targeting of choke point can lead to the accumulation or the loss of some metabolites, thus choke points constitute an indicator of lethality and can help identifying drug target <br/>See : Syed Asad Rahman, Dietmar Schomburg; Observing local and global properties of metabolic pathways: ???load points??? and ???choke points??? in the metabolic networks. Bioinformatics 2006; 22 (14): 1767-1774. doi: 10.1093/bioinformatics/btl181<br/><br/><pre><code> -h              : prints the help (default: false)
 -i VAL          : input SBML file
 -o VAL          : output results file
 -s (--side) VAL : an optional file containing list of side compounds to ignore
</code></pre></details></td></tr>
<tr><td>CompoundNet</td><td>Advanced creation of a compound graph representation of a SBML file content<details><summary><small>more</small></summary>Metabolic networks used for quantitative analysis often contain links that are irrelevant for graph-based structural analysis. For example, inclusion of side compounds or modelling artifacts such as 'biomass' nodes.<br/>While Carbon Skeleton Graph offer a relevant alternative topology for graph-based analysis, it requires compounds' structure information, usually not provided in model, and difficult to retrieve for model with sparse cross-reference annotations.<br/>In contrary to the SBML2Graph app that performs a raw conversion of the SBML content, the present app propose a fine-tuned creation of compound graph from predefined list of side compounds and degree?? weighting to get relevant structure without structural data.This app also enable Markov-chain based analysis of metabolic networks by computing reaction-normalized transition probabilities on the network.<br/><br/><pre><code> -am (--asmatrix)                    : export as matrix (implies simple graph
                                       conversion). Default export as GML file
                                       (default: false)
 -cw (--customWeights) VAL           : an optional file containing weights for
                                       compound pairs
 -dw (--degreeWeights)               : penalize traversal of hubs by using
                                       degree square weighting (default: false)
 -h                                  : prints the help (default: false)
 -mc (--mergecomp) [by_name | by_id] : merge compartments. Use names if
                                       consistent and unambiguous across
                                       compartments, or identifiers if
                                       compartment suffix is present (id in
                                       form "xxx_y" with xxx as base identifier
                                       and y as compartment label).
 -me (--simple)                      : merge parallel edges to produce a simple
                                       graph (default: false)
 -o VAL                              : output Graph file
 -ri (--removeIsolatedNodes)         : remove isolated nodes (default: false)
 -s VAL                              : input SBML file
 -sc VAL                             : input Side compound file
 -tp (--transitionproba)             : set weight as random walk transition
                                       probability, normalized by reaction
                                       (default: false)
 -un (--undirected)                  : create as undirected (default: false)
</code></pre></details></td></tr>
<tr><td>DistanceMatrix</td><td>Create a compound to compound distance matrix.<details><summary><small>more</small></summary>Create a compound to compound distance matrix.<br/>The distance between two compounds is computed as the length of the shortest path connecting the two in the compound graph, where two compounds are linked if they are respectively substrate and product of the same reaction.<br/>An optional edge weighting can be used, turning the distances into the sum of edge weights in the lightest path, rather than the length of the shortest path.The default weighting use target's degree squared. Alternatively, custom weighting can be provided in a file. In that case, edges without weight are ignored during path search.<br/>If no edge weighting is set, it is recommended to provide a list of side compounds to ignore during network traversal.<br/><br/><pre><code> -dw (--degree)     : penalize traversal of hubs by using degree square
                      weighting (default: false)
 -h                 : prints the help (default: false)
 -i VAL             : input SBML file
 -o VAL             : output Matrix file
 -s (--side) VAL    : an optional file containing list of side compounds to
                      ignore
 -u (--undirected)  : Ignore reaction direction (default: false)
 -w (--weights) VAL : an optional file containing weights for compound pairs
</code></pre></details></td></tr>
<tr><td>ExtractSubNetwork</td><td>Create a subnetwork from a GSMN in SBML format, and two files containing lists of compounds of interests ids, one per row.<details><summary><small>more</small></summary>Create a subnetwork from a GSMN in SBML format, and two files containing lists of compounds of interests ids, one per row.<br/>The subnetwork correspond to part of the network that connects compounds from the first list to compounds from the second list.<br/>Sources and targets list can have elements in common. The connecting part can be defined as the union of shortest or k-shortest paths between sources and targets, or the Steiner tree connecting them. The relevance of considered path can be increased by weighting the edges using degree squared, chemical similarity (require InChI or SMILES annotations) or any provided weighting.<br/><br/>See previous works on subnetwork extraction for parameters recommendations:Frainay, C., & Jourdan, F. Computational methods to identify metabolic sub-networks based on metabolomic profiles. Bioinformatics 2016;1???14. https://doi.org/10.1093/bib/bbv115<br/>Faust, K., Croes, D., & van Helden, J. Prediction of metabolic pathways from genome-scale metabolic networks. Bio Systems 2011;105(2), 109???121. https://doi.org/10.1016/j.biosystems.2011.05.004<br/>Croes D, Couche F, Wodak SJ, et al. Metabolic PathFinding: inferring relevant pathways in biochemical networks. Nucleic Acids Res 2005;33:W326???30.<br/>Croes D, Couche F, Wodak SJ, et al. Inferring meaningful pathways in weighted metabolic networks. J Mol Biol 2006; 356:222???36.<br/>Rahman SA, Advani P, Schunk R, et al. Metabolic pathway analysis web service (Pathway Hunter Tool at CUBIC). Bioinformatics 2005;21:1189???93.<br/>Pertusi DA, Stine AE, Broadbelt LJ, et al. Efficient searching and annotation of metabolic networks using chemical similarity. Bioinformatics 2014;1???9.<br/>McShan DC, Rao S, Shah I. PathMiner: predicting metabolic pathways by heuristic search. Bioinformatics 2003;19:1692???8.<br/><br/><br/><pre><code> -cw (--customWeights) VAL : an optional file containing weights for compound
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
</code></pre></details></td></tr>
<tr><td>ExtractSubReactionNetwork</td><td>Create a subnetwork from a GSMN in SBML format, and two files containing lists of reactions of interests ids, one per row, plus one file of the same format containing side compounds ids.<details><summary><small>more</small></summary>Create a subnetwork from a GSMN in SBML format, and two files containing lists of reactions of interests ids, one per row, plus one file of the same format containing side compounds ids.<br/>The subnetwork corresponds to part of the network that connects reactions from the first list to reactions from the second list.<br/>Sources and targets list can have elements in common. The connecting part can be defined as the union of shortest or k-shortest paths between sources and targets, or the Steiner tree connecting them. Contrary to compound graph, reaction graph often lacks weighting policy for edge relevance. In order to ensure appropriate network density, a list of side compounds to ignore for linking reactions must be provided. An optional edge weight file, if available, can also be used.<br/><br/><pre><code> -cw (--customWeights) VAL : an optional file containing weights for reactions
                             pairs
 -h                        : prints the help (default: false)
 -i VAL                    : input SBML file
 -k N                      : Extract k-shortest paths (default: 1)
 -o VAL                    : output gml file
 -s VAL                    : input sources txt file
 -sc (--side) VAL          : a file containing list of side compounds to ignore
 -st (--steinertree)       : Extract Steiner Tree (default: false)
 -t VAL                    : input targets txt file
</code></pre></details></td></tr>
<tr><td>LoadPoint</td><td>Compute the Load points of a metabolic network. Load points constitute an indicator of lethality and can help identifying drug target.<details><summary><small>more</small></summary>Compute the Load points of a metabolic network. Load points constitute an indicator of lethality and can help identifying drug target.<br/>From Rahman et al. Observing local and global properties of metabolic pathways: ???load points??? and ???choke points??? in the metabolic networks. Bioinf. (2006):<br/>For a given metabolic network, the load L on metabolite m can be defined as :<br/>ln [(pm/km)/(???Mi=1Pi)/(???Mi=1Ki)]<br/>p is the number of shortest paths passing through a metabolite m;<br/>k is the number of nearest neighbour links for m in the network;<br/>P is the total number of shortest paths;<br/>K is the sum of links in the metabolic network of M metabolites (where M is the number of metabolites in the network).<br/>Use of the logarithm makes the relevant values more distinguishable.<br/><br/><pre><code> -h              : prints the help (default: false)
 -i VAL          : input SBML file
 -k (--npath) N  : Number of alternative paths to consider between a pair of
                   connected metabolites (default: 1)
 -o VAL          : output results file
 -s (--side) VAL : an optional file containing list of side compounds to ignore
</code></pre></details></td></tr>
<tr><td>MetaboRank</td><td>Compute the MetaboRank, a custom personalized PageRank for metabolic network.<details><summary><small>more</small></summary>Compute the MetaboRank, a custom personalized PageRank for metabolic network.<br/>The MetaboRank takes a metabolic network and a list of compounds of interest, and provide a score of relevance for all of the other compounds in the network.<br/>The MetaboRank can, from metabolomics results, be used to fuel a recommender system highlighting interesting compounds to investigate, retrieve missing identification and drive literature mining.<br/>It is a two dimensional centrality computed from personalized PageRank and CheiRank, with special transition probability and normalization to handle the specificities of metabolic networks.<br/>See publication for more information: Frainay et al. MetaboRank: network-based recommendation system to interpret and enrich metabolomics results, Bioinformatics (35-2), https://doi.org/10.1093/bioinformatics/bty577<br/><br/><pre><code> -d N   : damping factor (default: 0.85)
 -h     : prints the help (default: false)
 -i VAL : input SBML file: path to network used for computing centrality, in
          sbml format.
 -max N : maximal number of iteration (default: 15000)
 -o VAL : output file: path to the file where the results will be exported
 -s VAL : input seeds file: tabulated file containing node of interest ids and
          weight
 -t N   : convergence tolerance (default: 0.001)
 -w VAL : input edge weight file: (recommended) path to file containing edges'
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
<tr><td>PrecursorNetwork</td><td>Perform a network expansion from a set of compound targets to create a precursor network.<details><summary><small>more</small></summary>Perform a network expansion from a set of compound targets to create a precursor network.<br/>The precursor network of a set of compounds (targets) refer to the sub-part of a metabolic network from which a target can be reachedThe network expansion process consist of adding a reaction to the network if any of its products are either a targets or a substrate of a previously added reaction<br/><br/><pre><code> -h                 : prints the help (default: false)
 -i VAL             : input SBML file: path to network used for computing
                      scope, in sbml format.
 -ir (--ignore) VAL : an optional file containing list of reaction to ignore
                      (forbid inclusion in scope
 -o VAL             : output file: path to the .gml file where the results
                      precursor network will be exported
 -sc (--sides) VAL  : an optional file containing list of ubiquitous compounds
                      to be considered already available
 -t (--targets) VAL : input target file: tabulated file containing node of
                      interest ids
</code></pre></details></td></tr>
<tr><td>ScopeNetwork</td><td>Perform a network expansion from a set of compound seeds to create a scope network<details><summary><small>more</small></summary>Perform a network expansion from a set of compound seeds to create a scope network<br/>The scope of a set of compounds (seed) refer to the maximal metabolic network that can be extended from them,where the extension process consist of adding a reaction to the network if and only if all of its substrates are either a seed or a product of a previously added reaction<br/>For more information, see Handorf, Ebenh??h and Heinrich (2005). *Expanding metabolic networks: scopes of compounds, robustness, and evolution.* Journal of molecular evolution, 61(4), 498-512. (https://doi.org/10.1007/s00239-005-0027-1)<br/><br/><pre><code> -h                 : prints the help (default: false)
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
</code></pre></details></td></tr>
<tr><td>SideCompoundsScan</td><td>Scan a network to identify side-compounds.<details><summary><small>more</small></summary>Scan a network to identify side-compounds.<br/>Side compounds are metabolites of small relevance for topological analysis. Their definition can be quite subjective and varies between sources.<br/>Side compounds tend to be ubiquitous and not specific to a particular biochemical or physiological process.Compounds usually considered as side compounds include water, atp or carbon dioxide. By being involved in many reactions and thus connected to many compounds, they tend to significantly lower the average shortest path distances beyond expected metabolic relatedness.<br/>This tool attempts to propose a list of side compounds according to specific criteria:  <br/>- *Degree*: Compounds with an uncommonly high number of neighbors can betray a lack of process specificity.  <br/>High degree compounds typically include water and most main cofactors (CoA, ATP, NADPH...) but can also include central compounds such as pyruvate or acetyl-CoA  <br/>- *Neighbor Coupling*: Similar to degree, this criteria assume that side compounds are involved in many reactions, but in pairs with other side compounds.<br/>Therefore, the transition from ATP to ADP will appear multiple time in the network, creating redundant 'parallel edges' between these two neighbors.<br/>Being tightly coupled to another compound through a high number of redundant edges, can point out cofactors while keeping converging pathways' products with high degree like pyruvate aside.  <br/>- *Carbon Count*: Metabolic "waste", or degradation end-product such as ammonia or carbon dioxide are usually considered as side compounds.<br/>Most of them are inorganic compound, another ill-defined concept, sometimes defined as compound lacking C-C or C-H bonds. Since chemical structure is rarely available in SBML model beyond chemical formula, we use a less restrictive criterion by flagging compound with one or no carbons. This cover most inorganic compounds, but include few compounds such as methane usually considered as organic.  - *Chemical Formula*: Metabolic network often contains 'artifacts' that serve modelling purpose (to define a composite objective function for example). Such entities can be considered as 'side entities'. Since they are not actual chemical compounds, they can be detected by their lack of valid chemical formula. However, this can also flag main compounds with erroneous or missing annotation.<br/><br/><pre><code> -cc (--noCarbonSkeleton)       : flag as side compound any compounds with less
                                  than 2 carbons in formula (default: false)
 -d (--degree) N                : flag as side compounds any compounds with
                                  degree above threshold (default: 400)
 -dp (--degreep) N              : flag as side compounds the top x% of
                                  compounds according to their degree (default:
                                  NaN)
 -h                             : prints the help (default: false)
 -i VAL                         : input SBML file
 -id (--onlyIds)                : do not report values in output, export ids
                                  list of compounds flagged as side-Compounds,
                                  allowing piping results (default: false)
 -m (--merge) [by_name | by_id] : Degree is shared between compounds in
                                  different compartments. Use names if
                                  consistent and unambiguous across
                                  compartments, or identifiers if compartment
                                  suffix is present (id in form "xxx_y" with
                                  xxx as base identifier and y as compartment
                                  label).
 -nc (--neighborCoupling) N     : flag as side compound any compound with a
                                  number of parallel edges shared with a
                                  neighbor above the given threshold (default:
                                  NaN)
 -o VAL                         : output Side-Compounds file
 -s (--onlySides)               : output compounds flagged as side-Compounds
                                  only (default: false)
 -uf (--undefinedFormula)       : flag as side compound any compounds with no
                                  valid chemical formula (default: false)
</code></pre></details></td></tr>
</tbody>
</table>
