# met4j-flux

## met4j module for flux balance analyses

### Documentation

For using met4j-flux, you have to install one of this library:
- [GLPK](http://glpk-java.sourceforge.net/index.html) 
- [CPLEX](https://www.ibm.com/fr-fr/analytics/cplex-optimizer) 

Both use a cpp library (.so or .dll). To be sure that the jar package
corresponds to the cpp library, you have to specify both in your project.



#### Use met4j-flux with GLPK

##### Local maven installation of GPLK.
- First install [glpk-java](http://glpk-java.sourceforge.net/index.html) 
- Include the jar library into maven local repository

```shell script
mvn install:install-file -Dfile=/usr/share/java/glpk-java.jar \
-DgroupId=glpk -DartifactId=glpk -Dversion=glpk-for-met4j -Dpackaging=jar
```


##### Launch met4j-flux with GLPK

It will be necessary to indicate the installation path
 of the GLPK for Java shared library (.so or .dll)
 when you will run your applications using met4j-flux.

Example:

```
java .... -Djava.library.path=/usr/lib/x86_64-linux-gnu/jni
```
#### Use met4j-flux with CPLEX

##### Local maven installation of CPLEX.
- First install [cplex](https://www.ibm.com/fr-fr/analytics/cplex-optimizer) 
- Include the jar library into maven local repository

```shell script
mvn install:install-file -Dfile=/usr/local/cplex/cplex/lib/cplex.jar -DgroupId=ibm.ilog \
-DartifactId=cplex -Dversion=cplex-for-met4j -Dpackaging=jar
```
Replace by the proper paths and versions.

##### Launch met4j-flux with CPLEX

It will be necessary to indicate the installation path
 of the CPLEX for Java shared library (.so or .dll)
 when you will run your applications using met4j-flux.
 
Example:

```
java .... -Djava.library.path=/usr/local/cplex/cplex/bin/x86-64_sles10_4.1/
```

###### Examples of using met4j-flux


Examples can be found in
https://forgemia.inra.fr/metexplore/tutorialmet4j
in the package fr.inrae.toulouse.metexplore.tutorialmet4j.met4j_flux.
