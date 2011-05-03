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
	Book CurrentBook;
	Item item;
	TextItem titem;
	ImageItem imitem;
//	GPSItem gpsitem;	
	VideoItem vitem;
	AudioItem aitem;	
	StringBuilder url,text,filename;

	/*
	 * fields
	 */

	 private boolean in_placebooksText = false;
	 private boolean in_textUrl = false;
	 private boolean in_textText = false;

	 private boolean in_placebooksImage = false;
	 private boolean in_imageUrl = false;
	 private boolean in_imageFilename = false;
	 
	 private boolean in_placebooksVideo = false;
	 private boolean in_videoFilename = false;
	 
	 private boolean in_placebooksAudio = false;
	 private boolean in_audioFilename = false;
	 
	 private boolean in_key = false;

	 public Book getParsedData() {
		 //return this.books;
		 return CurrentBook;
	 }

	 /* 
	  * methods
	  */
	 @Override
	 public void startDocument() throws SAXException {
		CurrentBook = new Book();
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
             CurrentBook.setKey(attr);
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
			 this.CurrentBook.items.add(titem);
			 titem = null;
		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.ImageItem")) {
			 this.in_placebooksImage = false;
			 this.CurrentBook.items.add(imitem);
			 imitem = null;
		 }  
		 else if (localName.equalsIgnoreCase("placebooks.model.VideoItem")) {
			 this.in_placebooksVideo = false;
			 this.CurrentBook.items.add(vitem);
			 vitem = null;
		 }  
		 else if (localName.equalsIgnoreCase("placebooks.model.AudioItem")) {
			 this.in_placebooksAudio = false;
			 this.CurrentBook.items.add(aitem);
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
		
		 
		 
	 } //end of void characters

}
