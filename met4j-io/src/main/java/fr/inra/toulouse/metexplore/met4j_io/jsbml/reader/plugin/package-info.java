/**
 * This package contains the different developed plugins that allow to parse different SMBL lvl 3 packages or some other non required SBML objects.</br>
 * Here is the complete list of plugin-Reader classes:
 * <ul>
 * <li>AnnotationParser : Parse and extract information from SBML annotations 
 * <li>FBC1Parser : Parse the SBML package FBC version 1
 * <li>FBC2Parser : Parse the SBML package FBC version 2
 * <li>NotesParser : Parse and extract information form SBML notes
 * </ul>
 * All these classes implement the PackageParser Interface. Any other plugin developed should also implement this Interface. 
 * @author Benjamin
 * @since 3.0
 */

package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

