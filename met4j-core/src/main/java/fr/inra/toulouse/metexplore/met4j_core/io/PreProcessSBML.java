/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

public class PreProcessSBML {

	public String originalFile;
	public File prePocessedFile;
	public FileInputStream inputStream;
	public FileWriter writer;

	public PreProcessSBML(String fileName) throws IOException{
		this.setOriginalFile(fileName);
		this.setPrePocessedFile(fileName+".processed");
		this.setInputStream();
		this.setWriter();
	}

	public static File process(String fileName) throws IOException {

		PreProcessSBML preSBML=new PreProcessSBML(fileName);
		
		InputStreamReader ipsr=new InputStreamReader(preSBML.getInputStream());
		BufferedReader br=new BufferedReader(ipsr);

		String ligne;

		while ((ligne=br.readLine())!=null){
			String newLigne = preSBML.encodeSIDs(ligne);

			preSBML.getWriter().write(newLigne+"\n");

		}

		preSBML.getInputStream().close();
		preSBML.getWriter().close();
		return preSBML.prePocessedFile;
	}
	
	private String encodeSIDs(String ligne) {
		
		Set<String> regexlist=new HashSet<String>();
		regexlist.add(".*id=\"([^\"]+)\".*");
		regexlist.add(".*compartment=\"([^\"]+)\".*");
		regexlist.add(".*species=\"([^\"]+)\".*");
		
		for (String regex : regexlist){
			Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(ligne);
	        
	        while (matcher.find()){
	        	String SID=matcher.group(1);
	        	String ValidSiD=StringUtils.sbmlEncode(SID);
	        	//System.err.println(regex+" , current: " +SID+" , new: "+ValidSiD);
	        	ligne=ligne.replace(SID, ValidSiD);
	        }
		}
				
		return ligne;
	}

	public String getOriginalFile() {
		return originalFile;
	}

	public File getPrePocessedFile() {
		return prePocessedFile;
	}

	public FileInputStream getInputStream() {
		return inputStream;
	}

	public FileWriter getWriter() {
		return writer;
	}

	public void setOriginalFile(String originalFile) {
		this.originalFile = originalFile;
	}

	public void setPrePocessedFile(String prePocessedFile) throws IOException{
		this.prePocessedFile = File.createTempFile(prePocessedFile, ".tmp");
	}

	public void setInputStream() throws FileNotFoundException {
		this.inputStream = new FileInputStream(this.getOriginalFile());
	}

	public void setWriter() throws IOException {
		this.writer =  new FileWriter(this.getPrePocessedFile());
	}
	
}
