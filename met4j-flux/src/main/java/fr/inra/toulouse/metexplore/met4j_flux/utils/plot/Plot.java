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

package fr.inra.toulouse.metexplore.met4j_flux.utils.plot;

import org.math.plot.Plot2DPanel;
import org.math.plot.plotObjects.BaseLabel;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;

public class Plot {
	
	
	/**
	 * Plots an histogram
	 * @param numArray
	 * @param titleString
	 * @param xLabel
	 * @param yLabel
	 */
	public static void plotHistogram(Collection<Integer> numArray, String titleString, String xLabel,String yLabel,int nbBins) 
	{
		
		double[] numbers = new double[numArray.size()+1];

		int i = 0;
		
		for (Integer nb : numArray) {
			numbers[i] = nb;
			i++;
		}
		
		int max = Collections.max(numArray);
		
		// trick to counteract the bug that does not show the last category
		numbers[i] = max + 1;
		
		
		
		Plot2DPanel plot = new Plot2DPanel();
		
		plot.addHistogramPlot(
				titleString,
				numbers, nbBins);
		
		
		 BaseLabel title = new BaseLabel(titleString, Color.BLUE, 0.5, 1.1);
         title.setFont(new Font("Courier", Font.BOLD, 20));
         plot.addPlotable(title);
		
         plot.setAxisLabel(0,  xLabel);
         plot.setAxisLabel(1,  yLabel);

		// put the PlotPanel in a JFrame like a JPanel
		JFrame frame = new JFrame(titleString);
		frame.setSize(600, 600);
		frame.setContentPane(plot);
		frame.setVisible(true);
	}

}
