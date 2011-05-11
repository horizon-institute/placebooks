package org.placebooks.www;

import android.util.Log; 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream; 
import java.io.BufferedInputStream;

 

public class Decompress { 
  private String _zipFile; 
  private String _location; 
 
  public Decompress(String zipFile, String location) { 
    _zipFile = zipFile; 
    _location = location; 
 
    _dirChecker(""); 
  } 
 
  public void unzip() { 
    try  { 
      FileInputStream fin = new FileInputStream(_zipFile); 
      BufferedInputStream bin = new BufferedInputStream(fin); //new
      ZipInputStream zin = new ZipInputStream(bin); //new
     // ZipInputStream zin = new ZipInputStream(fin); //old
      ZipEntry ze = null; 
      while ((ze = zin.getNextEntry()) != null) { 
        Log.v("Decompress", "Unzipping " + ze.getName()); 
 
        if(ze.isDirectory()) { 
          _dirChecker(ze.getName()); 
        } else { 
          FileOutputStream fout = new FileOutputStream(_location + ze.getName()); 
          
          /*for (int c = zin.read(); c != -1; c = zin.read()) { 
            fout.write(c); 
          } */
          byte[] buffer = new byte[1024];
          int length;
          // added
          while ((length = zin.read(buffer))>0) {
          fout.write(buffer, 0, length);
          }
 
          zin.closeEntry(); 
          fout.close(); 
        } 
         
      } 
      zin.close(); 
    } catch(Exception e) { 
      Log.e("Decompress", "unzip", e); 
    } 
 
  } 
 
  private void _dirChecker(String dir) { 
    File f = new File(_location + dir); 
 
    if(!f.isDirectory()) { 
      f.mkdirs(); 
    } 
  } 
} 