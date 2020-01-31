/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */
/**
 * 5 avr. 2013 
 */
package fr.inrae.toulouse.metexplore.met4j_flux.analyses.result;

import fr.inrae.toulouse.metexplore.met4j_flux.general.Bind;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Vars;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import org.jfree.ui.RefineryUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 
 * Class representing the result of a simple FBA analysis.
 * 
 * @author lmarmiesse 5 avr. 2013
 * 
 */
public class FBAResult extends AnalysisResult {

	/**
	 * Value of the objective function.
	 */
	private double objValue;

	/**
	 * Field to search for entities in the plot.
	 */
	private JTextField searchField;

	/**
	 * Table with all results.
	 */
	private JTable resultTable;

	private Bind bind;

	private Map<String, String> entToResult = new HashMap<String, String>();

	public FBAResult(Bind b) {

		this.bind = b;
	}

	public void formatResult() {
		if (!Double.isNaN(objValue)) {

			for (BioEntity entity : bind.getInteractionNetwork().getEntities()) {
				if (!entity.getId().contains(Vars.absolute)) {

					if (bind.constrainedEntities.contains(entity)) {

						if (bind.getDeadReactions().contains(entity)) {

							entToResult.put(entity.getId() + " (Dead)",
									String.valueOf(Vars.round(bind.getSolvedValue(entity))));
						} else {
							entToResult.put(entity.getId(), String.valueOf(Vars.round(bind.getSolvedValue(entity))));
						}
					} else {
						entToResult.put(entity.getId(), "Not Constrained");
					}

				}
			}
		}

	}

	public void writeToFile(String path) {

		try {
			PrintWriter out = new PrintWriter(new File(path));
			out.println("FBA result\n");
			out.println("obj : " + Vars.round(objValue));

			if (Double.isNaN(objValue)) {
				out.println("Unfeasible");
			} else {

				for (String entName : entToResult.keySet()) {
					if (bind.getBioNetwork().getReactionsView().containsId(entName)) {
						out.println(entName + "\t" + entToResult.get(entName));
					}
				}
				for (String entName : entToResult.keySet()) {
					if (!bind.getBioNetwork().getReactionsView().containsId(entName)) {
						out.println(entName + "\t" + entToResult.get(entName));
					}
				}
			}
			out.close();
		} catch (IOException e) {
			System.out.println("path " + path + " is not a valid path, or file could not be created.");
		}

	}

	public void writeHTML(String path) {

		try {
			PrintWriter out = new PrintWriter(new File(path));

			if (Double.isNaN(objValue)) {
				out.println("<p>Unfeasible</p>");
			} else {
				out.println("<p>Objective function value: " + Vars.round(objValue) + "</p>");
				out.println("<table>");

				out.println("<tr>");
				out.println("<th>Entity name</th>");
				out.println("<th>Value</th>");
				out.println("</tr>");

				// Reactions first
				for (String entName : entToResult.keySet()) {

					if (bind.getBioNetwork().getReactionsView().containsId(entName)) {
						out.println("<tr>");
						out.println("<td>" + entName + "</td>");
						out.println("<td>" + entToResult.get(entName) + "</td>");
						out.println("</tr>");
					}
				}
				for (String entName : entToResult.keySet()) {
					if (!bind.getBioNetwork().getReactionsView().containsId(entName)) {
						out.println("<tr>");
						out.println("<td>" + entName + "</td>");
						out.println("<td>" + entToResult.get(entName) + "</td>");
						out.println("</tr>");
					}
				}
				out.println("</table>");
			}
			out.close();
		} catch (IOException e) {
			System.out.println("path " + path + " is not a valid path, or file could not be created.");
		}

	}

	public void setObjValue(double d) {
		this.objValue = d;
	}

	public void plot() {

		JPanel northPanel = new JPanel();
		northPanel.add(new JLabel("obj : " + Vars.round(objValue)));

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

		if (!Double.isNaN(objValue)) {

			String[] columnNames = { "Entity name", "Value" };

			Object[][] data = new Object[entToResult.size()][columnNames.length];

			int i = 0;
			for (String entName : entToResult.keySet()) {

				data[i] = new Object[] { entName, entToResult.get(entName) };
				i++;
			}

			DefaultTableModel model = new MyTableModel(data, columnNames);
			resultTable = new JTable(0, 2);
			resultTable.setModel(model);
			final MyTableRowSorter<TableModel> sorter = new MyTableRowSorter<TableModel>(resultTable.getModel());

			resultTable.setRowSorter(sorter);

			JPanel searchPanel = new JPanel(new FlowLayout());

			searchPanel.add(new JLabel("Search for an entity : "));

			searchField = new JTextField(10);
			searchField.getDocument().addDocumentListener(new DocumentListener() {

				public void changedUpdate(DocumentEvent arg0) {
					updateTable(sorter);
				}

				public void insertUpdate(DocumentEvent arg0) {
					updateTable(sorter);
				}

				public void removeUpdate(DocumentEvent arg0) {
					updateTable(sorter);
				}

			});

			searchPanel.add(searchField);

			centerPanel.add(searchPanel);
			centerPanel.add(new JScrollPane(resultTable));

		} else {
			centerPanel.add(new JLabel("Unfeasible"));
		}

		JFrame frame = new JFrame("FBA results");

		frame.add(northPanel, BorderLayout.PAGE_START);
		frame.add(centerPanel, BorderLayout.CENTER);
		frame.pack();
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);


		Dimension size = frame.getSize ();
        BufferedImage img = new BufferedImage (size.width, size.height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics ();
        frame.paint (g);
        g.dispose ();
        try
        {
            ImageIO.write (img, "png", new File ("screenshot.png"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace ();
        }
	}

	/**
	 * Updates the table when a search is made in the plot.
	 */
	private void updateTable(TableRowSorter<TableModel> sorter) {
		String text = searchField.getText();
		if (sorter.getModelRowCount() != 0) {
			// case insensitive
			sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0));
		}

	}

	/**
	 * Prints a sensitivity analysis in fileName
	 * 
	 * @param fileName
	 */
	public void sensitivityAnalysis(String fileName) {
		this.bind.sensitivityAnalysis(fileName);
	}

	public Double getObjValue() {
		return objValue;
	}

	public Map<String, String> getEntToResult() {
		return entToResult;
	}

	
	
	
	
}
