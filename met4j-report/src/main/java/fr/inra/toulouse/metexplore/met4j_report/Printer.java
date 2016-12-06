/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: florence.maurier@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_report;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Interface Printer.
 */
public interface Printer {

	/**
	 * Adds the meta data.
	 *
	 * @param Title the title
	 * @param header the header
	 * @param Subject the subject
	 * @param Keywords the keywords
	 * @param Author the author
	 * @param Creator the creator
	 */
	public void addMetaData(String Title, String header, String Subject, String Keywords, String Author, String Creator);

	/**
	 * Close.
	 */
	public void close();
	
	/**
	 * Adds the title page.
	 *
	 * @param reportTitle the report title
	 * @param introduction the introduction
	 */
	public void addTitlePage(String reportTitle, String introduction);

	/**
	 * New chapter.
	 *
	 * @param chapterTitle the chapter title
	 */
	public void newChapter(String chapterTitle);
	
	/**
	 * New section.
	 *
	 * @param sectionTitle the section title
	 */
	public void newSection (String sectionTitle);
	
	/**
	 * New subsection.
	 *
	 * @param subsectionTitle the subsection title
	 */
	public void newSubsection (String subsectionTitle);
		
	/**
	 * Adds the image.
	 *
	 * @param fileAdr the file adr
	 * @param legend the legend
	 * @param title the title
	 */
	public void addImage(String fileAdr, String legend, String title);

	/**
	 * Adds the table.
	 *
	 * @param title the title
	 * @param heading the heading
	 * @param data the data
	 */
	public void addTable(String title, Vector <String> heading, Vector <Vector<String>> data);
	
	/**
	 * Adds the list.
	 *
	 * @param items the items
	 */
	public void addList(Vector<Object> items);
}
