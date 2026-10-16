# Installing Met4J

- [Prerequisites](#prerequisites)
- [Java library](#java-library)
  - [From Maven Central](#from-maven-central)
  - [From source](#from-source)
- [Command-line toolbox (met4j-toolbox)](#command-line-toolbox-met4j-toolbox)
  - [Executable jar](#executable-jar)
  - [Conda](#conda)
  - [Singularity](#singularity)
  - [Docker](#docker)
  - [Galaxy](#galaxy)

## Prerequisites

To use Met4J as a library, to build it from source or to run the toolbox jar, you need:

*   **Java Development Kit (JDK)**: version 17 or higher
*   **Apache Maven**: version 3.x or higher (library and build from source only)

The Conda, Singularity, Docker and Galaxy distributions of the toolbox do not need any of them.

## Java library

### From Maven Central

All the modules are published on [Maven Central](https://central.sonatype.com/namespace/fr.inrae.toulouse.metexplore).
Replace `x.y.z` by the latest version
([![maven](https://img.shields.io/maven-central/v/fr.inrae.toulouse.metexplore/met4j)](https://central.sonatype.com/namespace/fr.inrae.toulouse.metexplore)).

All the modules at once:

```xml
<dependency>
    <groupId>fr.inrae.toulouse.metexplore</groupId>
    <artifactId>met4j-toolbox</artifactId>
    <version>x.y.z</version>
</dependency>
```

or a specific module (`met4j-core`, `met4j-io`, `met4j-graph`, `met4j-chemUtils`, `met4j-mapping`,
`met4j-mathUtils`, `met4j-reconstruction`), for example met4j-core:

```xml
<dependency>
    <groupId>fr.inrae.toulouse.metexplore</groupId>
    <artifactId>met4j-core</artifactId>
    <version>x.y.z</version>
</dependency>
```

### From source

```console
git clone https://forge.inrae.fr/metexplore/met4j.git
cd met4j
mvn clean install               # build and install all modules in your local Maven repository
mvn clean install -DskipTests   # same, without running the tests
```

The toolbox jar is then in `met4j-toolbox/target/met4j-toolbox-<version>.jar`.
See [CONTRIBUTING.md](CONTRIBUTING.md) to run the tests and for the development setup.

## Command-line toolbox (met4j-toolbox)

The [met4j-toolbox](met4j-toolbox/README.md) gathers all the met4j command-line applications.
The apps are named after the last component of their package and their class, e.g. `convert.Sbml2Tab`.

### Executable jar

Download `met4j-toolbox.jar` from the [GitLab releases](https://forge.inrae.fr/metexplore/met4j/-/releases)
(or the [GitLab package registry](https://forge.inrae.fr/metexplore/met4j/-/packages)
and the [GitHub releases](https://github.com/MetExplore/met4j/releases)), or [build it from source](#from-source).

List all the apps:

```console
java -jar met4j-toolbox.jar
```

Run a specific app, with its full class name:

```console
java -cp met4j-toolbox.jar fr.inrae.toulouse.metexplore.met4j_toolbox.convert.Sbml2Tab -h
```

Log4j from jsbml can be very verbose. You can make it silent with:

```console
java -Dlog4j.configuration= -cp met4j-toolbox.jar ...
```

### Conda

[![install with bioconda](https://img.shields.io/badge/install%20with-bioconda-brightgreen.svg?style=flat)](https://bioconda.github.io/recipes/met4j/README.html)

```console
conda install met4j -c bioconda -c conda-forge
```

List all the apps:

```console
met4j
```

Run a specific app:

```console
met4j convert.Sbml2Graph -i mySbml.xml -o ./output.txt
```

### Singularity

You need at least [Singularity](https://sylabs.io/guides/3.5/user-guide/quick_start.html) v3.5.

Pull the latest version:

```console
singularity pull met4j-toolbox.sif oras://registry.forge.inrae.fr/metexplore/met4j/met4j-singularity:latest
```

or a specific version:

```console
singularity pull met4j-toolbox.sif oras://registry.forge.inrae.fr/metexplore/met4j/met4j-singularity:x.y.z
```

To build the image yourself, from the root of the repository:

```console
mvn clean package -DskipTests
singularity build met4j-toolbox.sif met4j.singularity
```

List all the apps:

```console
./met4j-toolbox.sif
```

Run a specific app:

```console
./met4j-toolbox.sif convert.Tab2Sbml -h
```

By default, Singularity only sees your home directory. To access other directories,
set the `SINGULARITY_BIND` environment variable, for example:

```console
export SINGULARITY_BIND=/data      # bash
setenv SINGULARITY_BIND /data      # csh, tcsh
```

### Docker

First install [Docker](https://www.docker.com/).

Pull the latest version from Docker Hub:

```console
docker pull metexplore/met4j:latest
```

or a specific version:

```console
docker pull metexplore/met4j:x.y.z
```

The same images are available in the GitLab registry: `registry.forge.inrae.fr/metexplore/met4j/met4j-docker`.

To build the image yourself, from the root of the repository:

```console
mvn clean package -DskipTests
docker build -t metexplore/met4j:myversion .
```

List all the apps:

```console
docker run --rm metexplore/met4j:latest
```

Run a specific app. Don't forget to mount a volume to process local files:

```console
docker run --rm -v /path/to/work:/work metexplore/met4j:latest \
  convert.Sbml2Tab -i /work/toy_model.xml -o /work/toy_model.tsv
```

### Galaxy

The met4j-toolbox apps are available without any installation on a dedicated
[Galaxy instance](https://met4j.usegalaxy.fr/), and on the [French Galaxy server](https://usegalaxy.fr/)
maintained by the [French Institute of Bioinformatics](https://www.ifb-elixir.fr/).

[Galaxy](https://galaxyproject.org/) wrappers are available in the [Galaxy toolshed](https://toolshed.g2.bx.psu.edu/)
(master version) and in the [Galaxy test toolshed](https://testtoolshed.g2.bx.psu.edu/) (develop version).
They launch the met4j Conda package.
