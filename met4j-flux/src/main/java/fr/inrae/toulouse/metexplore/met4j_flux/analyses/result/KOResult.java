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
 * 15 mars 2013 
 */
package fr.inrae.toulouse.metexplore.met4j_flux.analyses.result;

import fr.inrae.toulouse.metexplore.met4j_flux.general.Vars;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * Class representing the result of a KO analysis.
 *
 * @author lmarmiesse 15 mars 2013
 *
 */
public class KOResult extends AnalysisResult {

    /**
     * Map containing the result for each knocked out entity.
     */
    private Map<BioEntity, Double> map = new HashMap<BioEntity, Double>();

    /**
     * Field to search for entities in the plot.
     */
    private JTextField searchField;

    /**
     * Table with all results.
     */
    private JTable resultTable;

    /**
     * Adds a value to the map.
     */
    public synchronized void addLine(BioEntity entity, double value) {

        map.put(entity, value);
    }

    public void writeToFile(String path) {
        try {
            PrintWriter out = new PrintWriter(new File(path));

            out.println("KO results : \n");

            for (BioEntity entity : map.keySet()) {

                double plotVal = Vars.round(map.get(entity));

                if (Double.isNaN(plotVal)) {
                    out.println(entity.getId() + "(Unfeasible) obj value : " + Vars.round(map.get(entity)));
                } else {
                    out.println(entity.getId() + " obj value : " + Vars.round(map.get(entity)));
                }

            }
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public double getValueForEntity(BioEntity ent) {
        return map.get(ent);
    }

    public void plot() {

        resultTable = new JTable(0, 2);

        String[] columnNames = {"Entity name", "Objective value"};

        Object[][] data = new Object[map.size()][columnNames.length];

        int i = 0;
        for (BioEntity ent : map.keySet()) {

            double plotVal = Vars.round(map.get(ent));

            if (Double.isNaN(plotVal)) {
                data[i] = new Object[]{ent.getId() + "(Unfeasible)", 0.0};
            } else {
                data[i] = new Object[]{ent.getId(), plotVal};
            }
            i++;
        }

        DefaultTableModel model = new MyTableModel(data, columnNames);
        resultTable.setModel(model);

        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(resultTable.getModel());
        resultTable.setRowSorter(sorter);

        JPanel northPanel = new JPanel();

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));

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

        JFrame frame = new JFrame("KO results");

        frame.add(northPanel, BorderLayout.PAGE_START);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    /**
     * Updates the table when a search is made in the plot.
     */
    private void updateTable(TableRowSorter<TableModel> sorter) {
        String text = searchField.getText();
        if (sorter.getModelRowCount() != 0 && text.length() != 0) {
            // case insensitive
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0));
        }

    }

    public Map<BioEntity, Double> getMap() {
        return map;
    }

    /**
     *
     * @return a hashMap of the essential entities
     */
    public BioCollection<BioEntity> getEssentialEntities() {

        BioCollection<BioEntity> essentialEntities = new BioCollection<BioEntity>();

        for (BioEntity entity : map.keySet()) {

            Double value = Vars.round(map.get(entity));

            if (value.isNaN() || value == 0)
                essentialEntities.add(entity);
        }
        return essentialEntities;

    }

    /**
     *
     * @return a hashMap of the optima essential genes a gene is optima essential if
     *         its ko only decreases the objective optimal value
     */
    public HashMap<String, BioEntity> getOptimaEntities(Double optimalValue) {

        HashMap<String, BioEntity> optimaEntities = new HashMap<String, BioEntity>();

        for (BioEntity entity : map.keySet()) {

            Double value = Vars.round(map.get(entity));

            if (!value.isNaN()) {

                optimalValue = Vars.round(optimalValue);

                Double diff = Math.abs(optimalValue) - Math.abs(value);

                if (diff > 0 && value != 0) {
                    optimaEntities.put(entity.getId(), entity);
                }
            }

        }
        return optimaEntities;

    }

    /**
     *
     * @return a hashMap of the neutral genes a gene is neutral if its ko doesn't
     *         decrease the objective optimal value
     */
    public HashMap<String, BioEntity> getNeutralEntities(Double optimalValue) {

        HashMap<String, BioEntity> neutralEntities = new HashMap<String, BioEntity>();

        for (BioEntity entity : map.keySet()) {

            Double value = Vars.round(map.get(entity));

            if (!value.isNaN()) {

                optimalValue = Vars.round(optimalValue);

                Double diff = Math.abs(optimalValue) - Math.abs(value);

                if (diff == 0) {
                    neutralEntities.put(entity.getId(), entity);
                }
            }
        }
        return neutralEntities;

    }

    @Override
    public void writeHTML(String path) {
        try {
            PrintWriter out = new PrintWriter(new File(path));

            out.println("<table>");

            out.println("<tr>");
            out.println("<th>Entity name</th>");
            out.println("<th>Objective value</th>");
            out.println("</tr>");

            for (BioEntity entity : map.keySet()) {
                out.println("<tr>");

                double plotVal = Vars.round(map.get(entity));

                if (Double.isNaN(plotVal)) {
                    out.println("<td>" + entity.getId() + "(Unfeasible)</td>");
                    out.println("<td>" + Vars.round(map.get(entity)) + "</td>");

                } else {
                    out.println("<td>" + entity.getId() + "</td>");
                    out.println("<td>" + Vars.round(map.get(entity)) + "</td>");
                }

                out.println("</tr>");
            }

            out.println("</table>");
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}