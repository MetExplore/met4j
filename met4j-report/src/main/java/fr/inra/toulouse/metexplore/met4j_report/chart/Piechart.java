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
package fr.inra.toulouse.metexplore.met4j_report.chart;
import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;
import org.jfree.util.SortOrder;

// TODO: Auto-generated Javadoc
/**
 * The Class Piechart.
 */
public class Piechart extends Chart{
	
	/** The data set. */
	private DefaultPieDataset dataSet;
	
	/** The plot. */
	private PiePlot plot; 
	
	/**
	 * Instantiates a new piechart.
	 *
	 * @param data the data
	 * @param chartTitle the chart title
	 */
	public Piechart(Hashtable<String, Integer> data, String chartTitle){
		createDataset(data);
		createChart(chartTitle, dataSet);
	}
	
	/**
	 * Creates the dataset.
	 *
	 * @param data the data
	 */
	private void createDataset(Hashtable<String, Integer> data){
		 dataSet = new DefaultPieDataset();
         Enumeration<String> dataKeys = data.keys();
         while(dataKeys.hasMoreElements()){
        	 String dataKey = dataKeys.nextElement();
        	 int value = (Integer) data.get(dataKey);
        	 String legend = getCompleteLegend(dataKey,value);
             dataSet.setValue(legend, value);        	 
         }
         dataSet.sortByKeys(SortOrder.ASCENDING);
	}	
	
	/**
	 * Creates the chart.
	 *
	 * @param chartTitle the chart title
	 * @param dataSet the data set
	 */
	private void createChart(String chartTitle, DefaultPieDataset dataSet){
		 chart = ChartFactory.createPieChart3D(chartTitle, dataSet, legendRequired, generateToolTips, generateURLs);
         plot = (PiePlot) chart.getPlot();
         plot.setStartAngle(90);
         plot.setDirection(Rotation.CLOCKWISE);
         plot.setForegroundAlpha(0.65f);
         plot.setLabelBackgroundPaint(Color.WHITE); 
	}
	
	/**
	 * Change colors.
	 *
	 * @param colors the colors
	 * @param data the data
	 */
	public void changeColors(Hashtable<String, Color> colors, Hashtable<String, Integer> data){
		PiePlot ColorConfigurator = (PiePlot) chart.getPlot();
		Enumeration<String> legends = colors.keys();
		while (legends.hasMoreElements()){
			String legend = legends.nextElement();
			int value = data.get(legend);
			String complete_legend = getCompleteLegend(legend,value);
			Color color = (Color) colors.get(legend);
			ColorConfigurator.setSectionPaint(complete_legend, color);
		}
	}
	
	/**
	 * Gets the complete legend.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the complete legend
	 */
	public static String getCompleteLegend(String key, int value){
		return key+" ("+value+")";
	}
 }