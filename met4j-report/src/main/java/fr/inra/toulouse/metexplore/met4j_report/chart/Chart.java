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
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

// TODO: Auto-generated Javadoc
/**
 * The Class Chart.
 */
public class Chart {
	
	/** The chart. */
	protected JFreeChart chart;
	
	/** The legend required. */
	protected boolean legendRequired = true;	//legend - a flag specifying whether or not a legend is required.
	
	/** The generate tool tips. */
	protected boolean generateToolTips = true;	//tooltips - configure chart to generate tool tips?
	
	/** The generate ur ls. */
	protected boolean generateURLs = false;		//urls - configure chart to generate URLs?
	
	/** The orientation. */
	protected PlotOrientation orientation = PlotOrientation.VERTICAL;
	
    /**
     * Show_legend.
     */
    public void show_legend(){
    	legendRequired = true; 
    }
    
    /**
     * Hide_legend.
     */
    public void hide_legend(){
    	legendRequired = false; 
    }
    
    /**
     * Set_horizontal.
     */
    public void set_horizontal(){
		orientation = PlotOrientation.HORIZONTAL;
    }
    
    /**
     * Set_vertical.
     */
    public void set_vertical(){
		orientation = PlotOrientation.VERTICAL;
    }
       
	/**
	 * Save as png.
	 *
	 * @param fileAdr the file adr
	 * @param width the width
	 * @param height the height
	 */
	public void saveAsPng(String fileAdr, int width, int height) {
		try {
			ChartUtilities.saveChartAsPNG(new File(fileAdr), chart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error saving the chart to: "+fileAdr);
		}
    }
}
