package org.placebooks.www;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.util.*;

public class XMLConfigHandler extends DefaultHandler {

	
    private boolean in_binderTag = false;
   
    private Binder binder = new Binder();


    public Binder getParsedData() {
            return this.binder;
    }
	

    @Override
    public void startDocument() throws SAXException {
            this.binder = new Binder();
    }

    @Override
    public void endDocument() throws SAXException {
            // Nothing to do
    }
    
    /** Gets be called on opening tags like:
     * <tag>
     * Can provide attribute(s), when xml was like:
     * <tag attribute="attributeValue">*/
    @Override
    public void startElement(String namespaceURI, String localName,
                    String qName, Attributes atts) throws SAXException {
    	
    	if(localName.equalsIgnoreCase("page")){
			 this.in_binderTag = true;			 
		 }
            
    }
   
    /** Gets be called on closing tags like:
     * </tag> */
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
                    throws SAXException {
    	
		 if (localName.equalsIgnoreCase("page")) {
			 this.in_binderTag = false;
		 }
           
    }
    
    
    @Override
    public void characters(char ch[], int start, int length) {
                if(this.in_binderTag){
                //binder.setExtractedString(new String(ch, start, length));
       			 //pages.append(ch, start, length).toString();
                binder.addPageToBinder(new String(ch, start, length));
                	
                }
    }
    
    
    
	
}
