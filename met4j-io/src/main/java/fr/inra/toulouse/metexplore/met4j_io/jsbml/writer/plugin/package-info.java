/**
 * This package contains the different developed plugins that allow to write different SMBL lvl 3 packages or some other non required SBML objects.</br>
 * Here is the complete list of plugin-writer classes:
 * <ul>
 * <li>AnnotationWriter : Write MIRIAM compliant SBML annotations 
 * <li>FBCWriter : Write FBC version 2 objects to store GPR and Flux objects
 * <li>ModifierWriter : Write Modifiers in SBML Reactions <b>DEPRECATED</b>
 * <li>NotesParser : Write SBML notes
 * </ul>
 * All these classes implement the PackageWriter Interface. Any other plugin developed should also implement this Interface. 
 * @author Benjamin
 * @since 3.0
 */
package parsebionet.io.jsbml.writer.plugin;

