package org.placebooks.www;

import android.util.Log; 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream; 
import java.io.BufferedInputStream;
import java.util.zip.ZipFile;
import java.util.*;
import java.io.*;
import java.util.zip.ZipException;
 

public class Decompress { 
  private String _zipFile; 
  private String _location; 
 
  public Decompress(String zipFile, String location) { 
    _zipFile = zipFile; 
    _location = location; 
    
    try {
        //unzip(_zipFile);
    	doUnzip(_zipFile, _location);
		} catch (ZipException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		} catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		}    
    
    
    
    
    //_dirChecker(""); 
  } 
  
 /*
  private void _dirChecker(String dir) { 
    File f = new File(_location + dir); 
 
    if(!f.isDirectory()) { 
      f.mkdirs(); 
    } 
  } 
  */
  
  
public void unzip(String zipFile) throws ZipException, IOException {

	int BUFFER = 2048;
	File file = new File(zipFile);
	
	ZipFile zip = new ZipFile(file);
	String newPath = zipFile.substring(0, zipFile.length() - 4);
	
	new File(newPath).mkdir();
	Enumeration zipFileEntries = zip.entries();

	// Process each entry
	while (zipFileEntries.hasMoreElements()) {
	  // grab a zip file entry
	  ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	
	  String currentEntry = entry.getName();
	
	  File destFile = new File(newPath, currentEntry);
	  destFile = new File(newPath, destFile.getName());
	  File destinationParent = destFile.getParentFile();
	  
	
	  // create the parent directory structure if needed
	  destinationParent.mkdirs();
		  if (!entry.isDirectory()) {
		          BufferedInputStream is = new BufferedInputStream(zip
		                          .getInputStream(entry));
		          int currentByte;
		          // establish buffer for writing file
		          byte data[] = new byte[BUFFER];
		
		          // write the current file to disk
		          FileOutputStream fos = new FileOutputStream(destFile);
		          BufferedOutputStream dest = new BufferedOutputStream(fos,
		                          BUFFER);
		
		          // read and write until last byte is encountered
		          while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
		                  dest.write(data, 0, currentByte);
		          }
		          dest.flush();
		          dest.close();
		          is.close();
		  }
		  if (currentEntry.endsWith(".zip")) {
		          // found a zip file, try to open
		          unzip(destFile.getAbsolutePath());
		  }
	}
}




public void doUnzip(String inputZip, String destinationDirectory) throws IOException {
	
	int BUFFER = 2048;
	List zipFiles = new ArrayList();
	File sourceZipFile = new File(inputZip);
	File unzipDestinationDirectory = new File(destinationDirectory);
	unzipDestinationDirectory.mkdir();
	
	ZipFile zipFile;
	// Open Zip file for reading
	zipFile = new ZipFile(sourceZipFile, ZipFile.OPEN_READ);
	
	// Create an enumeration of the entries in the zip file
	Enumeration zipFileEntries = zipFile.entries();
	
	// Process each entry
	while (zipFileEntries.hasMoreElements()) {
	// grab a zip file entry
	ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	
	String currentEntry = entry.getName();
	
	File destFile = new File(unzipDestinationDirectory, currentEntry);
	destFile = new File(unzipDestinationDirectory, destFile.getName());
	
	if (currentEntry.endsWith(".zip")) {
	    zipFiles.add(destFile.getAbsolutePath());
	}
	
	// grab file's parent directory structure
	File destinationParent = destFile.getParentFile();
	
	// create the parent directory structure if needed
	destinationParent.mkdirs();
	
	try {
	    // extract file if not a directory
	    if (!entry.isDirectory()) {
	        BufferedInputStream is =
	                new BufferedInputStream(zipFile.getInputStream(entry));
	        int currentByte;
	        // establish buffer for writing file
	        byte data[] = new byte[BUFFER];
	
	        // write the current file to disk
	        FileOutputStream fos = new FileOutputStream(destFile);
	        BufferedOutputStream dest =
	                new BufferedOutputStream(fos, BUFFER);
	
	        // read and write until last byte is encountered
	        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
	            dest.write(data, 0, currentByte);
	        }
	        dest.flush();
	        dest.close();
	        is.close();
	      //  fileCount++;
	    }
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
	}
	zipFile.close();
	
	for (Iterator iter = zipFiles.iterator(); iter.hasNext();) {
	String zipName = (String)iter.next();
	doUnzip(zipName,destinationDirectory +File.separatorChar +zipName.substring(0,zipName.lastIndexOf(".zip"))
	);
	}

}


  
  
  
  
} 