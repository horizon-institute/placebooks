package org.placebooks.www;

import java.util.ArrayList;

/** Custom Java object model for our placebook data. Contains getter and setter method for varialbles */
public class XMLDataSet {

	/* 
	 * Variables
	 */

	//Text Item
    private String textItemUrl;
	private String textItemText;
	//Image Item
	private String imageItemUrl;
	private String imageItemPath;
	//test
	
	private ArrayList<String> url= new ArrayList<String>();
	
	
	
	/*
	 * accessor methods
	 */
	
	
	public String getTextItemURL(){return textItemUrl;}
	
	public String getTextItemText(){return textItemText;}
	
	public String getImageItemURL(){return imageItemUrl;}
	
	public String getImageItemPath(){return imageItemPath;}
	

	
	public ArrayList<String> getUrl() {
        return url;
}
	
	public void setUrl(String url) {
        this.url.add(url);
}



    /* 
     * mutator methods
     */
	
	public void setTextItemURL(String textItemUrl){	
    	this.textItemUrl = textItemUrl;
    }
    
	public void setTextItemText(String textItemText){	
    	this.textItemText = textItemText;
    }
    
	public void setImageItemURL(String imageItemUrl){	
    	this.imageItemUrl = imageItemUrl;
    }
    
	public void setImageItemPath(String imageItemPath){	
    	this.imageItemPath = imageItemPath;
    }
    
    
	
	/*  public void setExtractedString(String extractedString) {
         this.extractedString = extractedString;
    }
   
    public void setExtractedInt(int extractedInt) {
         this.extractedInt = extractedInt;
    }
    */
	
    
    @Override
	public String toString(){
     //    return "ExtractedString = " + this.extractedString
     //              + "\nTitle: " + title + "\nExtractedInt = " + this.extractedInt;
    	
    	//return "Text Item URL: " + textItemUrl + "\nText Item Text: " + textItemText;
    	return "Text Item URL: " + textItemUrl + "\nText Item Text: " + textItemText + "\nImage Item URL: " + imageItemUrl + "\nImage Item Path: "  + imageItemPath; 
    	
    }
	
}
