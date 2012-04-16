package org.placebooks.www;

import com.vividsolutions.jts.geom.Coordinate;

import java.io.File;
import java.util.*;

import android.app.Application;
import android.os.Environment;

/*
 * My Custom Application. Extends android.app.Application
 * This class is used to guarantee that this application context
 * will exist as a single instance across the whole PlaceBoooks app.
 * Therefore this class is ideal for storing variables and methods that
 * need to be accessed across multiple Activities.
 */
public class CustomApp extends Application {

	private String root;
	private String authenticationUrl;
	private String shelfUrl;
	private String unzippedDir;
	private String unzippedRoot;
	private String packageUrl;
    private String configFilename;

	
	 @Override
	    public void onCreate() {
	        super.onCreate();
	        
	        //Set the root
	        setRoot(Environment.getExternalStorageDirectory() + File.separator + "PlaceBooks");
	        //Set the authentication url
	        setAuthenticationUrl("http://horizac1.miniserver.com/placebooks/j_spring_security_check");
	        //Set the book shelf url
	        setShelfUrl("http://horizac1.miniserver.com/placebooks/placebooks/a/admin/shelf/");
	        //Set the unzipped dir
	        setUnzippedDir(Environment.getExternalStorageDirectory() + "/PlaceBooks/Unzipped");
	        //Set the unzipped root
	        setUnzippedRoot(Environment.getExternalStorageDirectory() + "/PlaceBooks/Unzipped/");
	        //Set the url for downloading a package
	        setPackageUrl("http://horizac1.miniserver.com/placebooks/placebooks/a/admin/package/");   //Package Url for Dev Server is = "http://horizab1.miniserver.com:8080/placebooks/placebooks/a/admin/package/"
	        //Set the confi file name to whatever it is called in the package
	        setConfigFilename("config.xml");
	        
	    }
	 
	 
	 public String getRoot(){
		    return root;
	 }
	 
	 public void setRoot(String root){
		    this.root = root;
	 }
	 
	 public String getAuthenticationUrl(){
		 return authenticationUrl;
	 }
	 
	 public void setAuthenticationUrl(String url){
		this.authenticationUrl = url; 
	 }
	 
	 public String getShelfUrl(){
		 return shelfUrl;
	 }
	 
	 public void setShelfUrl(String url){
		 this.shelfUrl = url;
	 }
	 
	 public String getUnzippedRoot(){
		 return unzippedRoot;
	 }
	 
	 public String getUnzippedDir(){
		 return unzippedDir;
	 }
	 
	 public void setUnzippedDir(String uDir){
		 this.unzippedDir = uDir;
	 }
	 
	 public void setUnzippedRoot(String uRoot){
		 this.unzippedRoot = uRoot;
	 }
	 
	 public String getPackageUrl(){
		 return packageUrl;
	 }
	 
	 public void setPackageUrl(String packageUrl){
		 this.packageUrl = packageUrl;
	 }
	 
	 public String getConfigFilename(){
		 return configFilename;
	 }
	 
	 public void setConfigFilename(String filename){
		 this.configFilename = filename;
	 }
	 

	

}
