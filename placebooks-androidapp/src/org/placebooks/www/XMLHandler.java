package org.placebooks.www;

//import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//import org.xml.sax.Attributes;
//import java.lang.StringBuilder; 

import android.util.Log;


/**
 * SAX document handler to create instances of our custom object models from the information stored
 * in the XML document. This “document handler” is a listener for the various events that are fired
 * by the SAX parser based on the contents of your XML document.
 */

public class XMLHandler extends DefaultHandler {
	Book myBook;
//	Item item;
	TextItem titem;
	ImageItem imitem;
//	GPSItem gpsitem;	
	VideoItem vitem;
	AudioItem aitem;	
	StringBuilder url,text,filename, panel, order;

	/*
	 * fields
	 */

	 private boolean in_placebooksText = false;
	 private boolean in_textUrl = false;
	 private boolean in_textText = false;
	 private boolean in_textPanel = false;
	 private boolean in_textOrder = false;

	 private boolean in_placebooksImage = false;
	 private boolean in_imageUrl = false;
	 private boolean in_imageFilename = false;
	 private boolean in_imagePanel = false;
	 private boolean in_imageOrder = false;
	 
	 private boolean in_placebooksVideo = false;
	 private boolean in_videoFilename = false;
	 private boolean in_videoPanel = false;
	 private boolean in_videoOrder = false;
	 
	 private boolean in_placebooksAudio = false;
	 private boolean in_audioFilename = false;
	 private boolean in_audioPanel = false;
	 private boolean in_audioOrder = false;
	 
	 private boolean in_key = false;

	 public Book getParsedData() {
		 //return this.books;
		 return myBook;
	 }

	 /* 
	  * methods
	  */
	 @Override
	 public void startDocument() throws SAXException {
		myBook = new Book();
	 }

	 @Override
	 public void endDocument() throws SAXException {
		 // Nothing to do;
		 Log.d("xml", "here");
	 }



	 /** Gets called on opening tags like:
	  * <tag>
	  * Can provide attribute(s), when xml was like:
	  * <tag attribute="attributeValue">*/
	 @Override
	 public void startElement(String namespaceURI, String localName,
			 String qName, Attributes atts) throws SAXException {
		 
		 if(localName.equals("placebooks.model.PlaceBook")){
			 this.in_key = true;	
			 String attr = atts.getValue("key");
             myBook.setKey(attr);
		 }

		 else if (localName.equals("placebooks.model.TextItem")) {
			 this.in_placebooksText = true;
			 titem = new TextItem();
		 }else if (localName.equals("placebooks.model.ImageItem")) {
			 this.in_placebooksImage = true;
			 imitem = new ImageItem();
		 }else if (localName.equals("placebooks.model.VideoItem")) {
			 this.in_placebooksVideo = true;
			 vitem = new VideoItem();
		 }else if (localName.equals("placebooks.model.AudioItem")) {
			 this.in_placebooksAudio = true;
			 aitem = new AudioItem();	 
			 
			 
		 }else if (localName.equals("text")) {
			 if(this.in_placebooksText){
				 this.in_textText = true;
				 text = new StringBuilder();
			 }
		 }else if (localName.equalsIgnoreCase("url")) {
			 if(this.in_placebooksText){
				this.in_textUrl = true;
			 	url = new StringBuilder();
			 }else if(this.in_placebooksImage){
				this.in_imageUrl = true;
				url = new StringBuilder();
			 }
		 }else if (localName.equalsIgnoreCase("filename")) {
			 if(this.in_placebooksImage){
				 this.in_imageFilename = true;
			 	filename = new StringBuilder();
			 } else if (this.in_placebooksVideo){
				 this.in_videoFilename = true;
				 filename = new StringBuilder();
			 }
			 else if (this.in_placebooksAudio){
				 this.in_audioFilename = true;
				 filename = new StringBuilder();
			 }
		  }
		 
		 else if(localName.equalsIgnoreCase("panel")){
			 
			 if(this.in_placebooksText){
				 this.in_textPanel = true;
				 panel = new StringBuilder();
			 }
			 else if (this.in_placebooksImage){
				 this.in_imagePanel = true;
				 panel = new StringBuilder();
			 }
			 else if (this.in_placebooksVideo){
				 this.in_videoPanel = true;
				 panel = new StringBuilder();
			 }
			 else if (this.in_placebooksAudio){
				 this.in_audioPanel = true;
				 panel = new StringBuilder();
			 }
			 
		 } //end of else if panel
		 
		 else if(localName.equalsIgnoreCase("order")){
			 
			  if(this.in_placebooksText){
				  this.in_textOrder = true;
				  order = new StringBuilder();
			  }
			  else if (this.in_placebooksImage){
				  this.in_imageOrder = true;
				  order = new StringBuilder();
			  }
			  else if (this.in_placebooksVideo){
				  this.in_videoOrder = true;
				  order = new StringBuilder();
			  }
			  else if (this.in_placebooksAudio){
				  this.in_audioOrder = true;
				  order = new StringBuilder();
			  }
			  
		   }//end of else if order
			  	
	 }

	 /** Gets called on closing tags like:
	  * </tag> */
	 @Override
	 public void endElement(String namespaceURI, String localName, String qName)
	 throws SAXException {
		 
		 if (localName.equalsIgnoreCase("placebooks.model.PlaceBook")) {
			 this.in_key = false;
		 
		 }

		 if (localName.equalsIgnoreCase("placebooks.model.TextItem")) {
			 this.in_placebooksText = false;
			 //this.CurrentBook.items.add(titem);
			 this.myBook.items.add(titem);

			 titem = null;
		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.ImageItem")) {
			 this.in_placebooksImage = false;
			 this.myBook.items.add(imitem);
			 imitem = null;
		 }  
		 else if (localName.equalsIgnoreCase("placebooks.model.VideoItem")) {
			 this.in_placebooksVideo = false;
			 this.myBook.items.add(vitem);
			 vitem = null;
		 }  
		 else if (localName.equalsIgnoreCase("placebooks.model.AudioItem")) {
			 this.in_placebooksAudio = false;
			 this.myBook.items.add(aitem);
			 aitem = null;
		 } 
		 
		 
		 else if (localName.equalsIgnoreCase("url")) {  
			 if(this.in_placebooksText){
				 this.in_textUrl = false; 
				 titem.setURL(url.toString());
				 url = null;
			 }else if(this.in_placebooksImage){
				 this.in_imageUrl = false;
				 imitem.setURL(url.toString());		
				 url = null;
			 }
		 }
		 else if (localName.equalsIgnoreCase("text")) {
			 if(this.in_placebooksText){
				 this.in_textText = false;
				 titem.setText(text.toString());
				 text = null;
			 }else if(this.in_placebooksImage){
				 this.in_imageUrl = false;
				// imitem.setText(text.toString());
				// text = null;
			 }              	
		 }
		 else if (localName.equalsIgnoreCase("filename")) {
			 this.in_imageFilename = false; 
			 this.in_videoFilename = false;
			 this.in_audioFilename = false;
			 
			 if(this.in_placebooksImage){
				 imitem.setFilename(filename.toString());
				 filename = null;
			 }else if(this.in_placebooksVideo){
				 vitem.setFilename(filename.toString());
				 filename = null;
			 }else if(this.in_placebooksAudio){
				 aitem.setFilename(filename.toString());
				 filename = null;
			 }
			 
		 }
		 
		 else if(localName.equalsIgnoreCase("panel")){
			 
			 if(this.in_placebooksText){
				 this.in_textPanel = false;
				 titem.setPanel(Integer.parseInt(panel.toString()));
				 panel = null;
			 }
			 else if(this.in_placebooksImage){
				 this.in_imagePanel = false;
				 imitem.setPanel(Integer.parseInt(panel.toString()));
				 panel = null;
			 }
			 else if(this.in_placebooksVideo){
				 this.in_videoPanel = false;
				 vitem.setPanel(Integer.parseInt(panel.toString()));
				 panel = null;
			 }
			 else if(this.in_placebooksAudio){
				 this.in_audioPanel = false;
				 aitem.setPanel(Integer.parseInt(panel.toString()));
				 panel = null;
				
			 }
			 
		 }
		 
		 else if (localName.equalsIgnoreCase("order")){
			 
			 if(this.in_placebooksText){
				this.in_textOrder = false;
				titem.setOrder(Integer.parseInt(order.toString()));
				order = null;
			 }
			 else if(this.in_placebooksImage){
				this.in_imageOrder = false;
				imitem.setOrder(Integer.parseInt(order.toString()));
				order = null;
			 }
			 else if(this.in_placebooksVideo){
				 this.in_videoOrder = false;
				 vitem.setOrder(Integer.parseInt(order.toString()));
				 order = null;
			 }
			 else if(this.in_placebooksAudio){
				 this.in_audioOrder = false;
				 aitem.setOrder(Integer.parseInt(order.toString()));
				 order = null;
			 }
		 }//end of else if order
		 
	 }

	 /** Gets called on the following structure:
	  * <tag>characters</tag> */
	 @Override
	 public void characters(char ch[], int start, int length) {
		 if(this.in_textUrl){
			 url.append(ch, start, length).toString();
			 
		 }else if(this.in_textText){
			 text.append(ch, start, length).toString();
			 
		 }else if(this.in_imageUrl){
			 url.append(ch, start, length).toString();
			 
		 }else if(this.in_imageFilename){
			 filename.append(ch, start, length).toString();
		 } 
		 else if (this.in_videoFilename){
			 filename.append(ch, start, length).toString();
		 }
		 else if (this.in_audioFilename){
			 filename.append(ch, start, length).toString();
		 }
		 else if(this.in_textPanel){
			 panel.append(ch, start, length).toString();
		 }
		 else if(this.in_imagePanel){
			 panel.append(ch, start, length).toString();
		 }
		 else if(this.in_videoPanel){
			 panel.append(ch, start, length).toString();
		 }
		 else if(this.in_audioPanel){
			 panel.append(ch, start, length).toString();
		 }
		 else if (this.in_textOrder){
			 order.append(ch, start, length).toString();
		 }
		 else if (this.in_imageOrder){
			 order.append(ch, start, length).toString();
		 }
		 else if (this.in_videoOrder){
			 order.append(ch, start, length).toString();
		 }
		 else if (this.in_audioOrder){
			 order.append(ch, start, length).toString();
		 }
		
		 
		 
	 } //end of void characters

}
