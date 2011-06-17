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

//new
import java.util.zip.ZipOutputStream;

 

public class Decompress { 
  private String _zipFile; 
  private String _location; 
 private File fIn;
 private File fOut;
  
  
  
  
  public Decompress(String zipFile, String location) { 
    _zipFile = zipFile; 
    _location = location;//"sdcard/placebooks/unzipped"; 
    
      // unzipFolder(_zipFile, _location);
   /* fIn = new File(zipFile);
    fOut = new File(location);
    
    try {
	    unzip(fIn, fOut);
    }catch (ZipException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
		} catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		}
*/    

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
       
  	} 

  
  
  /*
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


*/

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

  
  
  
  
  
  
  //STILL WORKING ON
  /**
  * @param zipFile the zip file that needs to be unzipped
  * @param destFolder the folder into which unzip the zip file and create the folder structure
  */
 /* public static void unzipFolder( String zipFile, String destFolder ) {
	  try {
		  ZipFile zf = new ZipFile(zipFile);
		  Enumeration< ? extends ZipEntry> zipEnum = zf.entries();
		  String dir = destFolder;
		
		  	  while( zipEnum.hasMoreElements() ) {
				  ZipEntry item = (ZipEntry) zipEnum.nextElement();
				
				  if (item.isDirectory()) {
					  File newdir = new File(dir + File.separator + item.getName());
					  newdir.mkdir();
				  } 
				  
				  else {
					  String newfilePath = dir + File.separator + item.getName();
					  File newFile = new File(newfilePath);
				  
				  if (!newFile.getParentFile().exists()) {
					  newFile.getParentFile().mkdirs();
				  }
				
				  InputStream is = zf.getInputStream(item);
				  FileOutputStream fos = new FileOutputStream(newfilePath);
				  int ch;
				  
				  while( (ch = is.read()) != -1 ) {
					  fos.write(ch);
				  }
					  is.close();
					  fos.close();
				  }
			  }
		  	  zf.close();
		  } 
	      catch (Exception e) {
	    	  e.printStackTrace();
	      }
  }
  
  */
  
  
  /*
  
  public static void unzip(File file, File targetDir) throws ZipException,
  IOException {
	
	targetDir.mkdirs();
	ZipFile zipFile = new ZipFile(file);
	try {
	  Enumeration<? extends ZipEntry> entries = zipFile.entries();
	  while (entries.hasMoreElements()) {
	      ZipEntry entry = entries.nextElement();
	      File targetFile = new File(targetDir, entry.getName());
	      if (entry.isDirectory()) {
	          targetFile.mkdirs();
	      } else {
	          InputStream input = zipFile.getInputStream(entry);
	          try {
	              OutputStream output = new FileOutputStream(targetFile);
	              try {
	                  copy(input, output);
	              } finally {
	                  output.close();
	              }
	          } finally {
	              input.close();
	          }
	      }
	  }
	} finally {
	  zipFile.close();
	}
}

private static void copy(InputStream input, OutputStream output) 
  throws IOException {
byte[] buffer = new byte[4096];
int size;
while ((size = input.read(buffer)) != -1)
  output.write(buffer, 0, size);
}
  
  
  */
  
  
  
  
  


  
} 