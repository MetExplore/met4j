# met4j-flux

## met4j module for flux balance analyses

### Documentation

For using met4j-flux, you have to install one of this library:
- [GLPK](http://glpk-java.sourceforge.net/index.html) 
- [CPLEX](https://www.ibm.com/fr-fr/analytics/cplex-optimizer) 

Go to the directory met4j-flux and type:
It will be necessary to indicate the installation path
 of the GLPK and /or CPLEX for Java shared library (.so or .dll)
 when you will run your applications using met4j-flux.
 
Example for cplex:

```
java .... -Djava.library.path=/usr/local/cplex/cplex/bin/x86-64_sles10_4.1/
```
Example for glpk:

```
java .... -Djava.library.path=/usr/lib/x86_64-linux-gnu/jni
```

Examples can be found in
https://forgemia.inra.fr/metexplore/tutorialmet4j
in the package fr.inrae.toulouse.metexplore.tutorialmet4j.met4j_flux.
