# met4j-toolbox

**Met4j command-line toolbox for metabolic networks**

## Installation

```
cd met4j-toolbox
mvn clean compile assembly:single
```

## Usage

The toolbox can be launched using
```
java -jar met4j-toolbox-<version>-jar-with-dependencies.jar
```
which will list all the contained applications that can be called using

```
java -cp met4j-toolbox-<version>-jar-with-dependencies.jar <Package>.<App name> -h
```

## Features


### Package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes  

| **Name** | **Description** |
| --- | --- |
| SbmlSetChargesFromFile |  Set charge to network metabolites from a tabulated file containing the metabolite ids and the formulas  |
| SbmlSetEcsFromFile |  Set EC numbers to reactions from a tabulated file containing the reaction ids and the EC  |
| SbmlSetFormulasFromFile |  Set Formula to network metabolites from a tabulated file containing the metabolite ids and the formulas  |
| SbmlSetGprsFromFile |  Create a new SBML file from an original sbml file and a tabulated file containing reaction ids and Gene association written in a cobra way  |
| SbmlSetNamesFromFile |  Set names to network objects from a tabulated file containing the object ids and the names  |
| SbmlSetPathwaysFromFile |  Set pathway to reactions in a network from a tabulated file containing the reaction ids and the pathways  |
| SbmlSetRefsFromFile |  Add refs to network objects from a tabulated file containing the metabolite ids and the formulas  |
| SbmlToMetaboliteTable |  Create a tabulated file with metabolite attributes from a SBML file  |

### Package fr.inrae.toulouse.metexplore.met4j_toolbox.bigg  

| **Name** | **Description** |
| --- | --- |
| GetModelProteome |  Get proteome in fasta format of a model present in BIGG  |

### Package fr.inrae.toulouse.metexplore.met4j_toolbox.convert  

| **Name** | **Description** |
| --- | --- |
| FbcToNotes |  Convert FBC package annotations to sbml notes  |
| Kegg2Sbml |  Build a SBML file from KEGG organism-specific pathways. Uses Kegg API.  |
| Sbml2Graph |  Create a graph representation of a SBML file content, and export it in graph file format.  |
| Sbml2Tab |  Create a tabulated file from a SBML file  |
| Tab2Sbml |  Create a Sbml File from a tabulated file that contains the reaction ids and the formulas  |

### Package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis  

| **Name** | **Description** |
| --- | --- |
| CarbonSkeletonNet |  Create a carbon skeleton graph representation of a SBML file content, using GSAM atom-mapping file (see https://forgemia.inra.fr/metexplore/gsam)  |
| ChokePoint |  Compute the Choke points of a metabolic network.  |
| DistanceMatrix |  Create a compound to compound distance matrix.  |
| ExtractSubNetwork |  Create a subnetwork from a GSMN in SBML format, and two files containing lists of compounds of interests ids, one per row.  |
| LoadPoint |  Compute the Load points of a metabolic network. Load points constitute an indicator of lethality and can help identifying drug target.  |
| MetaboRank |  Compute the MetaboRank, a custom personalized PageRank for metabolic network.  |
| NetworkSummary |  Create a report summarizing several graph measures characterising the structure of the network.  |
| PrecursorNetwork |  Perform a network expansion from a set of compound targets to create a precursor network.  |
| ScopeNetwork |  Perform a network expansion from a set of compound seeds to create a scope network  |
| SideCompoundsScan |  Scan a network to identify side-compounds.  |

