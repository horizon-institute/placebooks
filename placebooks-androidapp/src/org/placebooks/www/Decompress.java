package org.placebooks.www;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.*;

 

public class Decompress { 
	  
		public static void unzip(File file, File targetDir) 
			throws ZipException, IOException 
		{
		
			targetDir.mkdirs();
			ZipFile zipFile = new ZipFile(file);
			try {
			  Enumeration<? extends ZipEntry> entries = zipFile.entries();
			  while (entries.hasMoreElements()) {
				  ZipEntry entry = entries.nextElement();
				  File targetFile = new File(targetDir, entry.getName());
				  System.out.println("targetFile = " + targetFile.toString());
				  if (entry.isDirectory()) 
				  {
					  System.out.println("Entry is dir, value=" + targetFile.toString());
					  targetFile.mkdirs();
				  } 
				  else 
				  {
					  InputStream input = zipFile.getInputStream(entry);
					  try 
					  {
						  if (!targetFile.exists())
						  {
							  targetFile.getParentFile().mkdirs();
						  }
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
			throws IOException 
		{
			byte[] buffer = new byte[4096];
			int size;
			while ((size = input.read(buffer)) != -1)
				output.write(buffer, 0, size);
		}
  
 
  
} 