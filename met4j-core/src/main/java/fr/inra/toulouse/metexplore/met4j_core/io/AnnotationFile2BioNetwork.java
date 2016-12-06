/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;



/**
 * This Class creates a bionetwork object from an annotation file, 
 * It can be used with different files (reaction, metabolite, genes) 
 * and will create the corresponding java objects 
 * @author bmerlet
 * 15 sept 2014
 * 
 */
public abstract class AnnotationFile2BioNetwork extends Tab2BioNetwork {

	public String fileName;
	public boolean usePalssonId=true;



	public AnnotationFile2BioNetwork(String netId, String file, String Flag, String irrRxn, String revRxn, boolean palsson) {

		super(netId, 0, 0, palsson, palsson, Flag, irrRxn, revRxn, true, "x", 1);

		BioUnitDefinition UD=new BioUnitDefinition();
		UD.setDefault();
		this.getBioNetwork().addUnitDefinition(UD);

		BioCompartment fakeCompartment = new BioCompartment();
		fakeCompartment.setAsFakeCompartment();
		this.getBioNetwork().addCompartment(fakeCompartment);

		this.setFileName(file);
		this.setUsePalssonId(palsson);

	}

	public void convertFile(){

		HashMap<Integer,String> keys=new HashMap<Integer,String>();

		BufferedReader br = null;
		String firstLine = null; 
		try {

			br = new BufferedReader(new FileReader(this.getFileName()));

			firstLine = br.readLine();
			int i=0;
			if(firstLine!=null){
				for(String key : firstLine.split("\t")){
					keys.put(i++, key);
				}
			}

			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				
				if(!StringUtils.isVoid(sCurrentLine)){
					this.convertLineto(sCurrentLine,keys);
				}
				
			}
			


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}





	}

	protected abstract void convertLineto(String sCurrentLine, HashMap<Integer, String> keys);





	public String getFileName() {
		return fileName;
	}

	public boolean isUsePalssonId() {
		return usePalssonId;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setUsePalssonId(boolean usePalssonId) {
		this.usePalssonId = usePalssonId;
	}




}
