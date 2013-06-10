package org.placebooks.www;

import com.vividsolutions.jts.geom.Coordinate;

import java.io.File;
import java.util.*;

import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;

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
    private List<Cookie> cookies;
    private String language;
    private String searchUrl;

	
	 @Override
	    public void onCreate() {
	        super.onCreate();
	        
	        //Set the root
	        setRoot(Environment.getExternalStorageDirectory() + File.separator + "PlaceBooks");
	        //Set the authentication url
	        setAuthenticationUrl("http://placebooks.peoplescollectionwales.com/placebooks/j_spring_security_check");//("http://www.placebooks.org/placebooks/j_spring_security_check");//("http://horizab1.miniserver.com:8080/placebooks/j_spring_security_check");//("http://horizac1.miniserver.com/placebooks/j_spring_security_check");
	        //Set the book shelf url
	        setShelfUrl("http://placebooks.peoplescollectionwales.com/placebooks/a/admin/shelf/");//("http://www.placebooks.org/placebooks/placebooks/a/admin/shelf/");//("http://horizab1.miniserver.com:8080/placebooks/placebooks/a/admin/shelf/");//("http://horizac1.miniserver.com/placebooks/placebooks/a/admin/shelf/");
	        //Set the unzipped dir
	        setUnzippedDir(Environment.getExternalStorageDirectory() + "/PlaceBooks/Unzipped");
	        //Set the unzipped root
	        setUnzippedRoot(Environment.getExternalStorageDirectory() + "/PlaceBooks/Unzipped/");
	        //Set the url for downloading a package
	        setPackageUrl("http://placebooks.peoplescollectionwales.com/placebooks/a/admin/package/");//("http://www.placebooks.org/placebooks/placebooks/a/admin/package/");//("http://horizac1.miniserver.com/placebooks/placebooks/a/admin/package/");//("http://horizab1.miniserver.com:8080/placebooks/placebooks/a/admin/package/");//("http://horizac1.miniserver.com/placebooks/placebooks/a/admin/package/");   //Package Url for Dev Server is = "http://horizab1.miniserver.com:8080/placebooks/placebooks/a/admin/package/"
	        //Set the config file name to whatever it is called in the package
	        setConfigFilename("config.xml");
	        setSearchUrl("http://placebooks.peoplescollectionwales.com/placebooks/a/admin/location_search/placebookbinder/POINT");
	        setLanguage("En");
	        
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
	 
	 public List<Cookie> getCookies(){
		 return cookies;
	 }
	 
	 public void setCookies(List<Cookie> cookies){
		 this.cookies = cookies;
	 }
	 
	 public void setSearchUrl(String searchUrl){
		 this.searchUrl = searchUrl;
	 }
	 
	 public String getSearchUrl(){
		 return searchUrl;
	 }
	 
	 public String getLanguage(){
		 return language;
	 }
	 
	 public void setLanguage(String lang){
		 language = lang;
	 }
	 
	

}
