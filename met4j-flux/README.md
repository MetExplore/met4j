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
 
Example:

```
java .... .... -Djava.library.path=/usr/local/lib/jni:/usr/lib/jni
```
