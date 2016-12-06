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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
//import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
// TODO: Auto-generated Javadoc
//import org.jfree.chart.renderer.*;
//import org.jfree.data.statistics.HistogramDataset;
//import org.jfree.data.statistics.HistogramType;

/**
 * The Class BarChart.
 */
public class BarChart extends Chart {
	private boolean is3D;  
	/** The dataset. */
	private Dataset dataset;
	
    /**
     * Instantiates a new bar chart.
     *
     * @param plotTitle the plot title
     * @param data the data
     * @param xaxis the xaxis
     * @param yaxis the yaxis
     */
    public BarChart (String plotTitle, LinkedHashMap <String, LinkedHashMap<String,Number>> data, String xaxis, String yaxis) {
    	is3D = false;
    	createDataset(data);
    	createPlot(plotTitle, xaxis, yaxis);
    }
    
    /**
     * Creates the dataset.
     *
     * @param data the data
     */
    private void createDataset(LinkedHashMap <String, LinkedHashMap<String,Number>> data){
    	if (data.size()==1){
    		hide_legend();
    	}
    	dataset = new DefaultCategoryDataset();
		for (String serieTitle: data.keySet()){
			LinkedHashMap<String,Number> serie = data.get(serieTitle);
			for (String pointTitle: serie.keySet()){
				Number point = serie.get(pointTitle);
				((DefaultCategoryDataset) dataset).addValue(point, serieTitle, pointTitle);
			}
		}
    }
 
	/**
	 * Change colors.
	 *
	 * @param colors the colors
	 */
	public void changeColors(Hashtable<Integer, Color> colors){
		AbstractRenderer barRenderer = null;
		if(is3D){
			barRenderer = new StackedBarRenderer3D();
		}
		else{
			barRenderer = new StackedBarRenderer();
		}
		Enumeration<Integer> legends = colors.keys();

		while (legends.hasMoreElements()){
			Integer legend = legends.nextElement();
			Color color = (Color) colors.get(legend);
			barRenderer.setSeriesPaint(legend, color);
		}
		((CategoryPlot) chart.getPlot()).setRenderer((CategoryItemRenderer) barRenderer);
	}
	
	public void set_3D(){
		is3D = true;
	}
	public void set_2D(){
		is3D = false;
	}
	/**
	 * Creates the plot.
	 *
	 * @param plotTitle the plot title
	 * @param xaxis the xaxis
	 * @param yaxis the yaxis
	 */
	public void createPlot(String plotTitle, String xaxis, String yaxis){
		if (is3D){
			chart = ChartFactory.createStackedBarChart3D(plotTitle, xaxis, yaxis, (CategoryDataset) dataset,orientation, legendRequired, generateToolTips, generateURLs);
		}
		else{
			chart = ChartFactory.createStackedBarChart(plotTitle, xaxis, yaxis, (CategoryDataset) dataset,orientation, legendRequired, generateToolTips, generateURLs);
		}
    	//CategoryPlot plot = (CategoryPlot) chart.getPlot();
    	/*CategoryAxis domainAxis = plot.getDomainAxis();
    	domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);*/
    	
    	//B&W Bar type chart (non-line)
    	/*for (int series=0; series<=plot.getDataset().getRowCount(); series++) {
	    	int seriesOffset = 0;
			// set the hatch pattern for each series...
	    	StackedBarRenderer3D barRenderer = new StackedBarRenderer3D();
	    	barRenderer.setSeriesPaint(series,Texture.getTexturePaint(series+seriesOffset ));
	    	//set Color of the outline of each series - needed because some of our bars may be white in color - don't want a snowman in a snowstorm problem
	    	barRenderer.setSeriesOutlinePaint(series,Color.black);
    	}*/
    	//we use the same kind of loop as above to handle pie charts.
    	
    }
}
