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
 * 10 déc. 2013 
 */
package fr.inrae.toulouse.metexplore.met4j_graph;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.junit.rules.TemporaryFolder;

/**
 * @author lcottret
 * 10 déc. 2013
 *
 */
public class TestUtils {
	
	public static String copyProjectResource(String path, TemporaryFolder tempDirectory) throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();		
		
		InputStream stream = classLoader.getResourceAsStream(path);
		if(stream == null){
			System.err.println("file not found");
			throw new IOException();
		}
		
		File fileList = tempDirectory.newFile();
		
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(fileList, true));
		byte[] bucket = new byte[32*1024];
        int bytesRead = 0;
        while(bytesRead != -1){
          bytesRead = stream.read(bucket); //-1, 0, or more
          if(bytesRead > 0){
            outStream.write(bucket, 0, bytesRead);
          }
        }
		
        outStream.close();
        stream.close();
        
        return fileList.getAbsolutePath();
        
	}
	
	public static String copyProjectResource(String path, File tempDirectory) throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream(path);
		if(stream == null){
			System.err.println("file not found");
			throw new IOException();
		}
		
		File file = new File(tempDirectory+File.pathSeparator+"test.tmp");
		
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(file, true));
		byte[] bucket = new byte[32*1024];
        int bytesRead = 0;
        while(bytesRead != -1){
          bytesRead = stream.read(bucket); //-1, 0, or more
          if(bytesRead > 0){
            outStream.write(bucket, 0, bytesRead);
          }
        }
		
        outStream.close();
        stream.close();
        
        return file.getAbsolutePath();
        
	}
	
	public static BufferedReader readRessourcesFromJar(String path){
		InputStream is = TestUtils.class.getResourceAsStream(path);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		return br;
	}
	
}
