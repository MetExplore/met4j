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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
//import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.IntervalXYDataset;

// TODO: Auto-generated Javadoc
/**
 * The Class Histogram.
 */
public class Histogram extends Chart{
	
	/** The dataset. */
	private Dataset dataset;
	
    /**
     * Instantiates a new histogram.
     *
     * @param plotTitle the plot title
     * @param data the data
     * @param xaxis the xaxis
     * @param yaxis the yaxis
     * @param number the number
     */
    public Histogram (String plotTitle, Hashtable <String, Vector<Integer>> data, String xaxis, String yaxis, Integer number) {
    	createDataset(data,  number);
    	createChart(plotTitle, xaxis, yaxis);
    }    
    
    /**
     * Creates the dataset.
     *
     * @param data the data
     * @param number the number
     */
    private void createDataset(Hashtable <String, Vector<Integer>> data, Integer number){
    	if (data.size()==1){
    		hide_legend();
    	}
    	dataset = new HistogramDataset();			
		((HistogramDataset) dataset).setType(HistogramType.FREQUENCY); //nb
		//dataset.setType(HistogramType.RELATIVE_FREQUENCY); //%
		Enumeration<String> dataE = data.keys();
		while(dataE.hasMoreElements()){
			String serieTitle = dataE.nextElement();
			double[] values = new double[data.get(serieTitle).size()];
			int i=0;
			for (Integer value : data.get(serieTitle)){
				values[i] = value ;
				i += 1;
			}
			((HistogramDataset) dataset).addSeries(serieTitle,values,number);
		}
    } 
   
    /**
     * Creates the chart.
     *
     * @param plotTitle the plot title
     * @param xaxis the xaxis
     * @param yaxis the yaxis
     */
    private void createChart(String plotTitle, String xaxis, String yaxis){
		chart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, (IntervalXYDataset) dataset, orientation, legendRequired, generateToolTips, generateURLs);
    }
}