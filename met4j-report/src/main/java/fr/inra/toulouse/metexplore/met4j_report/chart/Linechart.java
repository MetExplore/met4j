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
import java.util.LinkedHashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

// TODO: Auto-generated Javadoc
/**
 * The Class Linechart.
 */
public class Linechart extends Chart{
	
	/** The dataset. */
	private XYSeriesCollection dataset;

    /**
     * Instantiates a new linechart.
     *
     * @param data the data
     * @param chartTitle the chart title
     * @param xLabel the x label
     * @param yLabel the y label
     */
    public Linechart(LinkedHashMap<String, Hashtable<Double, Double>> data, String chartTitle, String xLabel, String yLabel) {
    	this(data, chartTitle, xLabel, yLabel, true);
    }
    
    /**
     * Instantiates a new linechart.
     *
     * @param data the data
     * @param chartTitle the chart title
     * @param xLabel the x label
     * @param yLabel the y label
     * @param showLines the show lines
     */
    public Linechart(LinkedHashMap<String, Hashtable<Double, Double>> data, String chartTitle, String xLabel, String yLabel, boolean showLines) {
    	createDataset(data);
    	createChart(chartTitle, xLabel, yLabel, dataset);
    	if (showLines){
    		showLines();
    	}
    	else{
    		hideLines();
    	}
    }
    
	/**
	 * Creates the dataset.
	 *
	 * @param data the data
	 */
	private void createDataset(LinkedHashMap<String, Hashtable <Double, Double>> data){
		dataset = new XYSeriesCollection();
		for (String serieTitle: data.keySet()){
			XYSeries series = new XYSeries(serieTitle);
			Hashtable <Double, Double> subdata = (Hashtable<Double, Double>) data.get(serieTitle);
			Enumeration<Double> subdataKeys = subdata.keys();
			while(subdataKeys.hasMoreElements()){
				Double x = subdataKeys.nextElement();
				series.add(x, subdata.get(x));
			}
			dataset.addSeries(series);
		}
	}
	
	/**
	 * Change colors.
	 *
	 * @param colors the colors
	 */
	public void changeColors(Hashtable<Integer, Color> colors){
		XYPlot plot = (XYPlot) chart.getPlot();
		XYItemRenderer plotRenderer = plot.getRenderer();
		Enumeration<Integer> legends = colors.keys();
		while (legends.hasMoreElements()){
			Integer legend = legends.nextElement();
			Color color = (Color) colors.get(legend);
			plotRenderer.setSeriesPaint(legend, color);
		}
	}

	/**
	 * Show lines.
	 */
	public void showLines(){
    	final XYPlot plot = chart.getXYPlot();
    	final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();    	
    	for (int i= 0; i<plot.getDataset().getSeriesCount(); i++){
	    	renderer.setSeriesLinesVisible(i, true);	    	
	    	renderer.setSeriesShapesVisible(i, true);
    	}
    	plot.setRenderer(renderer);
	}

	/**
	 * Hide lines.
	 */
	public void hideLines(){
    	XYPlot plot = chart.getXYPlot();
    	XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
    	for (int i= 0; i<plot.getDataset().getSeriesCount(); i++){
	    	renderer.setSeriesLinesVisible(i, false);
	    	renderer.setSeriesShapesVisible(i, true);
    	}
    	plot.setRenderer(renderer);
	}
	
    /**
     * Creates the chart.
     *
     * @param chartTitle the chart title
     * @param xLabel the x label
     * @param yLabel the y label
     * @param dataset the dataset
     */
    private void createChart(String chartTitle, String xLabel, String yLabel, XYDataset dataset) {	        
        chart = ChartFactory.createXYLineChart(chartTitle, xLabel, yLabel, dataset, orientation, legendRequired, generateToolTips, generateURLs);
    }
}
