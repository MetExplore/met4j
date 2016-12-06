/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.io;

import java.io.BufferedReader;
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

public class PostProcessSBML {

	public String unprocessedFile;
	public String outputFile;

	public FileInputStream inputStream;
	public FileWriter writer;

	public PostProcessSBML(String fileName) throws IOException{

		this.setUnprocessedFile(fileName+".unprocessed");
		this.setOutputFile(fileName);
		this.setInputStream();
		this.setWriter();
	}

	public static void main(String[] args) throws IOException {

		PostProcessSBML postSBML=new PostProcessSBML(args[0]);
		
		InputStreamReader ipsr=new InputStreamReader(postSBML.getInputStream());
		BufferedReader br=new BufferedReader(ipsr);

		String ligne;

		while ((ligne=br.readLine())!=null){
			String newLigne = postSBML.decodeSIDs(ligne);

			postSBML.getWriter().write(newLigne+"\n");

		}

		postSBML.getInputStream().close();
		postSBML.getWriter().close();

	}
	
	private String decodeSIDs(String ligne) {
		
		Set<String> regexlist=new HashSet<String>();
		regexlist.add(".*id=\"([^\"]+)\".*");
		regexlist.add(".*compartment=\"([^\"]+)\".*");
		regexlist.add(".*species=\"([^\"]+)\".*");
		
		for (String regex : regexlist){
			Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(ligne);
	        
	        while (matcher.find()){
	        	String nproSID=matcher.group(1);
	        	String truSiD=StringUtils.sbmlDecode(nproSID);
	        	//System.err.println(regex+" , current: " +SID+" , new: "+ValidSiD);
	        	ligne=ligne.replace(nproSID, truSiD);
	        }
		}
				
		return ligne;
	}

	

	public String getUnprocessedFile() {
		return unprocessedFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public FileInputStream getInputStream() {
		return inputStream;
	}

	public FileWriter getWriter() {
		return writer;
	}


	public void setUnprocessedFile(String unprocessedFile) {
		this.unprocessedFile = unprocessedFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void setInputStream() throws FileNotFoundException {
		this.inputStream = new FileInputStream(this.getUnprocessedFile());
	}

	public void setWriter() throws IOException {
		this.writer =  new FileWriter(this.getOutputFile());
	}

}
