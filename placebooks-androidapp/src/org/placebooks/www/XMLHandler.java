package org.placebooks.www;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.placebooks.www.XMLDataSet;
import org.xml.sax.Attributes;


/**
 * SAX document handler to create instances of our custom object models from the information stored
 * in the XML document. This “document handler” is a listener for the various events that are fired
 * by the SAX parser based on the contents of your XML document.
 */

public class XMLHandler extends DefaultHandler {

	/*
	 * fields
	 */
    
    private boolean in_placebook = false;
    private boolean in_textitem = false;
    private boolean in_textItemUrl = false;
    private boolean in_textItemText = false;
    private boolean in_imageItemUrl = false;
    private boolean in_imageItemPath = false;
    private boolean in_test = false;
    
    //create new object
    private XMLDataSet myParsedExampleDataSet = new XMLDataSet();
    public static XMLDataSet dataSet = null;
	
    
    /*
     * getter & setter
     */ 
    public XMLDataSet getParsedData() {
        return this.myParsedExampleDataSet;
    }

    /* 
     * methods
     */
    @Override
    public void startDocument() throws SAXException {
         this.myParsedExampleDataSet = new XMLDataSet();
    }

    @Override
    public void endDocument() throws SAXException {
         // Nothing to do
    }


 
    /** Gets called on opening tags like:
     * <tag>
     * Can provide attribute(s), when xml was like:
     * <tag attribute="attributeValue">*/
     @Override
     public void startElement(String namespaceURI, String localName,
             String qName, Attributes atts) throws SAXException {
       
    	 if (localName.equals("placebooks.model.PlaceBook")) {
             this.in_placebook = true;
        }
        else if (localName.equals("placebooks.model.TextItem")) {
            this.in_placebook = true;
       }
        else if (localName.equals("url")) {
            this.in_textItemUrl = true;}
        
        else if (localName.equals("text")) {
            this.in_textItemText = true;}  
        
        else if (localName.equalsIgnoreCase("url")) {
            this.in_imageItemUrl = true;
        }  
        
        else if (localName.equalsIgnoreCase("filename")) {
            this.in_imageItemPath = true;}  
    	 
       
        //else if (localName.equals("tagwithnumber")) {
             // Extract an Attribute
          //   String attrValue = atts.getValue("thenumber");
          //   int i = Integer.parseInt(attrValue);
          //   myParsedExampleDataSet.setExtractedInt(i);
             	 
    }
    
    /** Gets called on closing tags like:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
              throws SAXException {
    
    	if (localName.equals("placebooks.model.PlaceBook")) {
           // this.in_placebook = false;
    		
    	}
        else if (localName.equals("placebooks.model.TextItem")) {
                this.in_textitem = false;       
       }
        else if (localName.equals("url")) {
            this.in_textItemUrl = false;              	
        }
        else if (localName.equals("text")) {
            this.in_textItemText = false;              	
        }
        else if (localName.equalsIgnoreCase("url")) {
            this.in_imageItemUrl = false;              	
        }      
        else if (localName.equalsIgnoreCase("filename")) {
            this.in_imageItemPath = false;              	
        }
    	   
    	//else if (localName.equals("tagwithnumber")) {
            // Nothing to do here
         //}
   
    
    }
    
    /** Gets called on the following structure:
     * <tag>characters</tag> */
    @Override
   public void characters(char ch[], int start, int length) {
      //   if(this.in_mytag){
       //  myParsedExampleDataSet.setExtractedString(new String(ch, start, length));
      
   // }
      if(this.in_textItemUrl){
    	  myParsedExampleDataSet.setTextItemURL(new String(ch, start, length));
      }   
      if(this.in_textItemText){
    	  myParsedExampleDataSet.setTextItemText(new String(ch, start, length));
      }  
      if(this.in_imageItemUrl){
    	  myParsedExampleDataSet.setImageItemURL(new String(ch, start, length));
      } 
      if(this.in_imageItemPath){
    	  myParsedExampleDataSet.setImageItemPath(new String(ch, start, length));
      } 


   } //end of void characters
	
}
