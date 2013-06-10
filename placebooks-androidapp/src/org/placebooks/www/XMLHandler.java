package org.placebooks.www;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;
import com.vividsolutions.jts.io.WKTReader;


/*
 * SAX document handler to create instances of our custom object models from the information stored
 * in the XML document. This “document handler” is a listener for the various events that are fired
 * by the SAX parser based on the contents of your XML document.
 */

public class XMLHandler extends DefaultHandler {
	
	Book myBook;
	TextItem titem;
	ImageItem imitem;
	GPSTraceItem gpsitem;	
	VideoItem vitem;
	AudioItem aitem;
	MapImageItem mimitem;
	WebBundleItem wbitem;
	
	StringBuilder url,text,filename, column, order, mapPage, geometry, name, timestamp, data, gpx, attrLatLon, height, mapMarker;  //<timestamp>Tue Mar 22 17:26:21 GMT 2011</timestamp>

	
	/*
	 * Fields
	 */
	 private boolean in_key = false;
	 private boolean in_timestamp = false;
	
	 private boolean in_placebooksText = false;
	 private boolean in_textGeometry = false;
	 private boolean in_textUrl = false;
	 private boolean in_textText = false;
	 private boolean in_textColumn = false;
	 private boolean in_textOrder = false;
	 private boolean in_textMapPage = false;
	 private boolean in_textMapMarker = false;

	 private boolean in_placebooksImage = false;
	 private boolean in_imageGeometry = false;
	 private boolean in_imageUrl = false;
	 private boolean in_imageFilename = false;
	 private boolean in_imageColumn = false;
	 private boolean in_imageOrder = false;
	 private boolean in_imageMapPage = false;
	 private boolean in_imageHeight = false;
	 private boolean in_imageMapMarker = false;
	 
	 private boolean in_placebooksVideo = false;
	 private boolean in_videoGeometry = false;
	 private boolean in_videoFilename = false;
	 private boolean in_videoColumn = false;
	 private boolean in_videoOrder = false;
	 private boolean in_videoMapPage = false;
	 private boolean in_videoHeight = false;
	 private boolean in_videoMapMarker = false;

	 
	 private boolean in_placebooksAudio = false;
	 private boolean in_audioGeometry = false;
	 private boolean in_audioFilename = false;
	 private boolean in_audioColumn = false;
	 private boolean in_audioOrder = false;
	 private boolean in_audioMapPage = false;
	 private boolean in_audioMapMarker = false;

	 
	 private boolean in_placebooksMapImage = false;
	 private boolean in_mapImageGeometry = false;
	 private boolean in_mapImageFilename = false;
	 /*private boolean in_mapImageColumn = false;
	 private boolean in_mapImageOrder = false;*/
	 
	 private boolean in_placebooksWebBundle = false;
	 private boolean in_webBundleGeometry = false;
	 private boolean in_webBundleFilename = false;
	 private boolean in_webBundleUrl = false;
	 private boolean in_webBundleColumn = false;
	 private boolean in_webBundleOrder = false;
	 
	 private boolean in_placebooksGPSTrace = false;
	 private boolean in_gpsTraceFilename = false;
	 private boolean in_gpsTraceGeometry = false;
	 private boolean in_gpsTraceColumn = false;
	 private boolean in_gpsTraceOrder = false;
	 
	 
	 public Book getParsedData() {
		 //Return the book;
		 return myBook;
	 }

	 /* 
	  * Methods
	  */
	 @Override
	 public void startDocument() throws SAXException {
		//Start of reading the xml document so we want to create a new book for our items
		myBook = new Book();
	 }

	 @Override
	 public void endDocument() throws SAXException {
		 //Nothing to do;
		 Log.d("XMLHandler", "here");
	 }



	 /* Gets called on opening tags like:
	  * <tag>
	  * Can provide attribute(s), when xml was like:
	  * <tag attribute="attributeValue">*/
	 @Override
	 public void startElement(String namespaceURI, String localName,
			 String qName, Attributes atts) throws SAXException {
		 
		 if(localName.equalsIgnoreCase("placebooks.model.PlaceBook")){
			 this.in_key = true;	
			 String attr = atts.getValue("key");
             myBook.setKey(attr);
                                       
		 }

		 else if (localName.equalsIgnoreCase("placebooks.model.TextItem")) {
			 this.in_placebooksText = true;
			 titem = new TextItem();
			 titem.setType("Text");	// NEWLY ADDED
			 
			 String attr = atts.getValue("key");	//Text item key
             titem.setKey(attr);

		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.ImageItem")) {
			 this.in_placebooksImage = true;
			 imitem = new ImageItem();
			 imitem.setType("Image");	// NEWLY ADDED
			 
			 String attr = atts.getValue("key");		//Image item key
             imitem.setKey(attr);

		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.VideoItem")) {
			 this.in_placebooksVideo = true;
			 vitem = new VideoItem();
			 vitem.setType("Video");	// NEWLY ADDED
			 
			 String attr = atts.getValue("key");		//Video item key
             vitem.setKey(attr);

		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.AudioItem")) {
			 this.in_placebooksAudio = true;
			 aitem = new AudioItem();
			 aitem.setType("Audio");	// NEWLY ADDED
			 
			 String attr = atts.getValue("key");		//Audio item key
             aitem.setKey(attr);

		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.MapImageItem")){
			 this.in_placebooksMapImage = true;
			 mimitem = new MapImageItem();
			 mimitem.setType("MapImage");	// NEWLY ADDED
			 
			 String attr = atts.getValue("key");		//Map item key
             mimitem.setKey(attr);

		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.WebBundleItem")){
			 this.in_placebooksWebBundle = true;
			 wbitem = new WebBundleItem();
			 wbitem.setType("WebBundle");	// NEWLY ADDED
			 
			 String attr = atts.getValue("key");		//WB item key
             wbitem.setKey(attr);
		 }
		 
		 else if (localName.equalsIgnoreCase("placebooks.model.GPSTraceItem")){
			 this.in_placebooksGPSTrace = true;
			 gpsitem = new GPSTraceItem();
			 gpsitem.setType("GPSTrace");	//NEWLY ADDED
			 
			 String attr = atts.getValue("key");
			 gpsitem.setKey(attr);
		 }
		 
		 
		 else if (localName.equalsIgnoreCase("text")) {
			 if(this.in_placebooksText){
				 this.in_textText = true;
				 text = new StringBuilder();
			 }
		 }
		 else if (localName.equalsIgnoreCase("url")) {
			 if(this.in_placebooksText){
				this.in_textUrl = true;
			 	url = new StringBuilder();
			 }else if(this.in_placebooksImage){
				this.in_imageUrl = true;
				url = new StringBuilder();
			 }
			 else if(this.in_placebooksWebBundle){
				 this.in_webBundleUrl = true;
				 url = new StringBuilder();
			 }
		 }
		 else if (localName.equalsIgnoreCase("filename")) {
			 if(this.in_placebooksImage){
				 this.in_imageFilename = true;
			 	filename = new StringBuilder();
			 } 
			 else if (this.in_placebooksVideo){
				 this.in_videoFilename = true;
				 filename = new StringBuilder();
			 }
			 else if (this.in_placebooksAudio){
				 this.in_audioFilename = true;
				 filename = new StringBuilder();
			 }
			 else if (this.in_placebooksMapImage){
				 this.in_mapImageFilename = true;
				 filename = new StringBuilder();
			 }
			 else if (this.in_placebooksWebBundle){
				 this.in_webBundleFilename = true;
				 filename = new StringBuilder();
			 }
			 else if (this.in_placebooksGPSTrace){
				 this.in_gpsTraceFilename = true;
				 filename = new StringBuilder();
			 }
		  }
		 else if(localName.equalsIgnoreCase("column")){
			 
			 if(this.in_placebooksText){
				 this.in_textColumn = true;
				 column = new StringBuilder();
			 }
			 else if (this.in_placebooksImage){
				 this.in_imageColumn = true;
				 column = new StringBuilder();
			 }
			 else if (this.in_placebooksVideo){
				 this.in_videoColumn = true;
				 column = new StringBuilder();
			 }
			 else if (this.in_placebooksAudio){
				 this.in_audioColumn = true;
				 column = new StringBuilder();
			 }
			/* else if (this.in_placebooksMapImage){
				  this.in_mapImagePanel = true;
				  panel = new StringBuilder();
			  }*/ //MAP IMAGES DO NOT HAVE PANELS OR ORDERS AS OF NOW..APPARENTLY..
			 else if (this.in_placebooksWebBundle){
				 this.in_webBundleColumn= true;
				 column = new StringBuilder();
			 }
			 else if(this.in_placebooksGPSTrace){
				 this.in_gpsTraceColumn = true;
				 column = new StringBuilder();
			 }
			 
			 
		 } //End of else if column
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
			  /*else if (this.in_placebooksMapImage){
				  this.in_mapImageOrder = true;
				  order = new StringBuilder();
			  }*/ //MAP IMAGES DO NOT HAVE PANELS OR ORDERS AS OF NOW..APPARENTLY..
			  
			  else if (this.in_placebooksWebBundle){
				  this.in_webBundleOrder = true;
				  order = new StringBuilder();
			  }
			  else if (this.in_placebooksGPSTrace){
				  this.in_gpsTraceOrder = true;
				  order = new StringBuilder();
			  }
			  
		   }//End of else if order
		 
		 else if(localName.equalsIgnoreCase("mapPage")){
			 if(this.in_placebooksText){
				 this.in_textMapPage = true;
				 mapPage = new StringBuilder();
			 }
			 else if(this.in_placebooksImage){
				 this.in_imageMapPage = true;
				 mapPage = new StringBuilder();
			 }
			 else if (this.in_placebooksVideo){
				 this.in_videoMapPage = true;
				 mapPage = new StringBuilder();
			 }
			 else if (this.in_placebooksAudio){
				 this.in_audioMapPage = true;
				 mapPage = new StringBuilder();
			 }
		 }
		 
		 else if(localName.equalsIgnoreCase("marker")){
			 if(this.in_placebooksText){
				 this.in_textMapMarker = true;
				 mapMarker = new StringBuilder();
			 }
			 else if(this.in_placebooksImage){
				 this.in_imageMapMarker = true;
				 mapMarker = new StringBuilder();
			 }
			 else if(this.in_placebooksVideo){
				 this.in_videoMapMarker = true;
				 mapMarker = new StringBuilder();
			 }
			 else if(this.in_placebooksAudio){
				 this.in_audioMapMarker = true;
				 mapMarker = new StringBuilder();
			 }
		 }
		 
		 else if(localName.equalsIgnoreCase("height")){
			 
			 if(this.in_placebooksImage){
				 this.in_imageHeight = true;
				 height = new StringBuilder();
			 }
			 else if(this.in_placebooksVideo){
				 this.in_videoHeight = true;
				 height = new StringBuilder();
			 }
		 }
		 
		 else if(localName.equalsIgnoreCase("geometry")){
			 
			 if(this.in_placebooksText){
				 this.in_textGeometry = true;
				 geometry = new StringBuilder();
			 }
			 else if(this.in_placebooksImage){
				 this.in_imageGeometry = true;
				 geometry = new StringBuilder();
			 }
			 else if(this.in_placebooksVideo){
				 this.in_videoGeometry = true;
				 geometry = new StringBuilder();
			 }
			 else if(this.in_placebooksAudio){
				 this.in_audioGeometry = true;
				 geometry = new StringBuilder();
			 }
			 
			 else if(this.in_placebooksMapImage){
				 this.in_mapImageGeometry = true;
				 geometry = new StringBuilder();
			 }
			 else if(this.in_placebooksWebBundle){
				 this.in_webBundleGeometry = true;
				 geometry = new StringBuilder();
			 }
			 
			 
		 } //End of else if geometry
		 
		 
	    else if(localName.equalsIgnoreCase("timestamp")){
	    	this.in_timestamp = true;
	    	timestamp = new StringBuilder();
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
		 else if (localName.equalsIgnoreCase("placebooks.model.TextItem")) {
			 this.in_placebooksText = false;
			 //this.CurrentBook.items.add(titem);
			 this.myBook.textItems.add(titem);
			 titem = null;
		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.ImageItem")) {
			 this.in_placebooksImage = false;
			 this.myBook.imageItems.add(imitem);
			 imitem = null;
		 }  
		 else if (localName.equalsIgnoreCase("placebooks.model.VideoItem")) {
			 this.in_placebooksVideo = false;
			 this.myBook.videoItems.add(vitem);
			 vitem = null;
		 }  
		 else if (localName.equalsIgnoreCase("placebooks.model.AudioItem")) {
			 this.in_placebooksAudio = false;
			 this.myBook.audioItems.add(aitem);
			 aitem = null;
		 } 
		 else if (localName.equalsIgnoreCase("placebooks.model.MapImageItem")){
			 this.in_placebooksMapImage = false;
			 this.myBook.mapImageItems.add(mimitem);	//add a new map image item to my items to my book
			 mimitem = null;
			 
		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.WebBundleItem")){
			 this.in_placebooksWebBundle = false;
			 this.myBook.webBundleItems.add(wbitem);
			 wbitem = null;
		 }
		 else if (localName.equalsIgnoreCase("placebooks.model.GPSTraceItem")){
			 this.in_placebooksGPSTrace = false;
			 this.myBook.gpsTraceItems.add(gpsitem);
			 gpsitem = null;
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
			 else if(this.in_placebooksWebBundle){
				 this.in_webBundleUrl = false;
				 wbitem.setURL(url.toString());
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
				 //imitem.setText(text.toString());
				 //text = null;
			 }              	
		 }
		 else if (localName.equalsIgnoreCase("filename")) {
			 
			 if(this.in_placebooksImage){
				 this.in_imageFilename = false; 
				 imitem.setFilename(filename.toString());
				 filename = null;
			 }else if(this.in_placebooksVideo){
				 this.in_videoFilename = false;
				 vitem.setFilename(filename.toString());
				 filename = null;
			 }else if(this.in_placebooksAudio){
				 this.in_audioFilename = false;
				 aitem.setFilename(filename.toString());
				 filename = null;
			 }
			 else if(this.in_placebooksMapImage){
				 this.in_mapImageFilename = false;
				 mimitem.setFilename(filename.toString());
				 filename = null;
			 }
			 else if(this.in_placebooksWebBundle){
				 this.in_webBundleFilename = false;
				 wbitem.setFilename(filename.toString());
				 filename = null;			 
			 }
			 else if(this.in_placebooksGPSTrace){
				 this.in_gpsTraceFilename = false;
				 gpsitem.setGpxFilename(filename.toString());
			 }
			 
		 }
		 
		 else if(localName.equalsIgnoreCase("column")){
			 
			 if(this.in_placebooksText){
				 this.in_textColumn = false;
				 titem.setColumn(Integer.parseInt(column.toString()));
				 column = null;
			 }
			 else if(this.in_placebooksImage){
				 this.in_imageColumn = false;
				 imitem.setColumn(Integer.parseInt(column.toString()));
				 column = null;
			 }
			 else if(this.in_placebooksVideo){
				 this.in_videoColumn = false;
				 vitem.setColumn(Integer.parseInt(column.toString()));
				 column = null;
			 }
			 else if(this.in_placebooksAudio){
				 this.in_audioColumn = false;
				 aitem.setColumn(Integer.parseInt(column.toString()));
				 column = null;
			 }
			 /*else if(this.in_placebooksMapImage){
				 this.in_mapImagePanel = false;
				 mimitem.setPanel(Integer.parseInt(panel.toString()));
				 panel = null;
			 }*/ //MAP IMAGES DO NOT HAVE PANELS OR ORDERS AS OF NOW..APPARENTLY..
			 
			 else if(this.in_placebooksWebBundle){
				 this.in_webBundleColumn = false;
				 wbitem.setColumn(Integer.parseInt(column.toString()));
				 column = null;
			 }
			 
			 else if(this.in_placebooksGPSTrace){
				 this.in_gpsTraceColumn = false;
				 gpsitem.setColumn(Integer.parseInt(column.toString()));
				 column = null;
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
			 /*else if(this.in_placebooksMapImage){
				this.in_mapImageOrder = false;
				mimitem.setOrder(Integer.parseInt(order.toString()));
				order = null;
			 }*/ //MAP IMAGES DO NOT HAVE PANELS OR ORDERS AS OF NOW..APPARENTLY..
			 else if(this.in_placebooksWebBundle){
				 this.in_webBundleOrder = false;
				 wbitem.setOrder(Integer.parseInt(order.toString()));
				 order = null;
			 }
			 else if(this.in_placebooksGPSTrace){
				 this.in_gpsTraceOrder = false;
				 gpsitem.setOrder(Integer.parseInt(order.toString()));
				 order = null;
			 }
			 
		 }//end of else if order
		 
		 else if (localName.equalsIgnoreCase("mapPage")){
			 if(this.in_placebooksText){
				 this.in_textMapPage = false;
				 titem.setMapPage(Integer.parseInt(mapPage.toString()));
			 }
			 else if(this.in_placebooksImage){
				 this.in_imageMapPage = false;
				 imitem.setMapPage(Integer.parseInt(mapPage.toString()));
			 }
			 else if(this.in_placebooksVideo){
				 this.in_videoMapPage = false;
				 vitem.setMapPage(Integer.parseInt(mapPage.toString()));
			 }
			 else if(this.in_placebooksAudio){
				 this.in_audioMapPage = false;
				 aitem.setMapPage(Integer.parseInt(mapPage.toString()));
			 }
		 }
		 
		 else if (localName.equalsIgnoreCase("marker")){
			 if(this.in_placebooksText){
				 this.in_textMapMarker = false;
				 titem.setMapMarker(Integer.parseInt(mapMarker.toString()));
				 mapMarker = null;
			 }
			 else if(this.in_placebooksImage){
				 this.in_imageMapMarker = false;
				 imitem.setMapMarker(Integer.parseInt(mapMarker.toString()));
				 mapMarker = null;
			 }
			 else if (this.in_placebooksVideo){
				 this.in_videoMapMarker = false;
				 vitem.setMapMarker(Integer.parseInt(mapMarker.toString()));
				 mapMarker = null;
			 }
			 else if (this.in_placebooksAudio){
				 this.in_audioMapMarker = false;
				 aitem.setMapMarker(Integer.parseInt(mapMarker.toString()));
				 mapMarker = null;
			 }
		 }
		 
		 else if (localName.equalsIgnoreCase("height")){
			 
			 if(this.in_placebooksImage){
				 this.in_imageHeight = false;
				 imitem.setImageHeight(Integer.parseInt(height.toString()));
				 height = null;
			 }
			 else if(this.in_placebooksVideo){
				 this.in_videoHeight = false;
				 vitem.setVideoHeight(Integer.parseInt(height.toString()));
				 height = null;
			 }
		 }
		 
		 else if (localName.equalsIgnoreCase("geometry")){
			 
			 if(this.in_placebooksText){
				 this.in_textGeometry = false;
				 WKTReader w = new WKTReader();				 
 
				 try{
					 titem.setGeometry(w.read(geometry.toString()));
					 geometry = null; 

				 }
				 catch (Exception e) {  
					    System.out.println("Exception");  
				 }  
			 }
			 else if(this.in_placebooksImage){
				 this.in_imageGeometry = false;
				 WKTReader w = new WKTReader();				 
 
				 try{
					 imitem.setGeometry(w.read(geometry.toString()));
					 geometry = null; 

				 }
				 catch (Exception e) {  
					    System.out.println("Exception");  
				 }  
			 }
			 else if(this.in_placebooksVideo){
				 this.in_videoGeometry = false;
				 WKTReader w = new WKTReader();				 
 
				 try{
					 vitem.setGeometry(w.read(geometry.toString()));
					 geometry = null; 

				 }
				 catch (Exception e) {  
					    System.out.println("Exception");  
				 }  
			 }
			 else if(this.in_placebooksAudio){
				 this.in_audioGeometry = false;
				 WKTReader w = new WKTReader();				 
 
				 try{
					 aitem.setGeometry(w.read(geometry.toString()));
					 geometry = null; 

				 }
				 catch (Exception e) {  
					    System.out.println("Exception");  
				 }  
			 }			 
			 else if(this.in_placebooksMapImage){
				 this.in_mapImageGeometry = false;
				 WKTReader w = new WKTReader();				 
 
				 try{
					 mimitem.setGeometry(w.read(geometry.toString()));
					 geometry = null;
				 }
				 catch (Exception e) {  
					    System.out.println("Exception");  
				 }  
				  
			 }
			 else if(this.in_placebooksWebBundle){
				 this.in_webBundleGeometry = false;
				 WKTReader w = new WKTReader();				 
 
				 try{
					 wbitem.setGeometry(w.read(geometry.toString()));
					 geometry = null; 
				 }
				 catch (Exception e) {  
					    System.out.println("Exception");  
				 }  
			 }	 
				 
		}
		 
		
		 
		  else if(localName.equalsIgnoreCase("timestamp")){
		    	this.in_timestamp = false;
		    	myBook.setTimestamp(timestamp.toString());
		    	timestamp = null;

		    	//Also get other timestamps..e.g every item will have a timestamp
		    }

  
	 }
	 

	 /* Gets called on the following structure:
	  * <tag>characters</tag> */
	 @Override
	 public void characters(char ch[], int start, int length) {
		 //Text item
		 
		 if (this.in_textGeometry){
			 geometry.append(ch, start, length).toString();
		 }
		 else if(this.in_textUrl){
			 url.append(ch, start, length).toString();
		 }
		 else if(this.in_textText){
			 text.append(ch, start, length).toString();
		 }
		 else if(this.in_textColumn){
			 column.append(ch, start, length).toString();
		 }
		 else if (this.in_textOrder){
			 order.append(ch, start, length).toString();
		 }
		 else if (this.in_textMapPage){
			 mapPage.append(ch, start, length).toString();
		 }
		 else if (this.in_textMapMarker){
			 mapMarker.append(ch, start, length).toString();
		 }
		 
		 //Image item
		 
		 if (this.in_imageGeometry){
			 geometry.append(ch, start, length).toString();
		 }
		 else if(this.in_imageUrl){
			 url.append(ch, start, length).toString();
			 
		 }
		 else if(this.in_imageFilename){
			 filename.append(ch, start, length).toString();
		 } 
		 else if(this.in_imageColumn){
			 column.append(ch, start, length).toString();
		 }
		 else if (this.in_imageOrder){
			 order.append(ch, start, length).toString();
		 }
		 else if (this.in_imageMapPage){
			 mapPage.append(ch, start, length).toString();
		 }
		 else if (this.in_imageMapMarker){
			 mapMarker.append(ch, start, length).toString();
		 }
		 else if (this.in_imageHeight){
			 height.append(ch, start, length).toString();
		 }
		 
		 
		 //Video item
		 
		 else if (this.in_videoGeometry){
			 geometry.append(ch, start, length).toString();
		 }
		 else if (this.in_videoFilename){
			 filename.append(ch, start, length).toString();
		 }
		 else if(this.in_videoColumn){
			 column.append(ch, start, length).toString();
		 }
		 else if (this.in_videoOrder){
			 order.append(ch, start, length).toString();
		 }
		 else if (this.in_videoMapPage){
			 mapPage.append(ch, start, length).toString();
		 }
		 else if (this.in_videoMapMarker){
			 mapMarker.append(ch, start, length).toString();
		 }
		 else if (this.in_videoHeight){
			 height.append(ch, start, length).toString();
		 }
		 
		 //Audio item
		 
		 else if (this.in_audioGeometry){
			 geometry.append(ch, start, length).toString();
		 }
		 else if (this.in_audioFilename){
			 filename.append(ch, start, length).toString();
		 }
		 else if(this.in_audioColumn){
			 column.append(ch, start, length).toString();
		 }
		 else if (this.in_audioOrder){
			 order.append(ch, start, length).toString();
		 }
		 else if (this.in_audioMapPage){
			 mapPage.append(ch, start, length).toString();
		 }
		 else if (this.in_audioMapMarker){
			 mapMarker.append(ch, start, length).toString();
		 }
		
		 //Map image item
		 
		 else if (this.in_mapImageGeometry){
			 geometry.append(ch, start, length).toString();
		 }
		 else if (this.in_mapImageFilename){
			 filename.append(ch, start, length).toString();
		 }
  /*	 else if (this.in_mapImagePanel){
			 panel.append(ch, start, length).toString();
		 }
		 else if (this.in_mapImageOrder){
			 order.append(ch, start, length).toString();
		 }*/ //MAP IMAGES DO NOT HAVE PANELS OR ORDERS AS OF NOW..APPARENTLY..
		 
		 //Web bundle item
		 
		 else if (this.in_webBundleGeometry){
			 geometry.append(ch, start, length).toString();
		 }
		 else if (this.in_webBundleUrl){
			 url.append(ch, start, length).toString();
		 }
		 else if (this.in_webBundleFilename){
			 filename.append(ch, start, length).toString();
		 }
		 else if(this.in_webBundleColumn){
			 column.append(ch, start, length).toString();
		 }
		 else if(this.in_webBundleOrder){
			 order.append(ch, start, length).toString();
		 }
		 
		 //Gps trace item

		 else if (this.in_gpsTraceFilename){
			 filename.append(ch, start, length).toString();
		 }
		 else if (this.in_gpsTraceColumn){
			 column.append(ch, start, length).toString();
		 }
		 else if (this.in_gpsTraceOrder){
			 order.append(ch, start, length).toString();
		 }
		 
		 //Get the geometries too
		 
		 
		 else if (this.in_timestamp){
			 timestamp.append(ch, start, length).toString();
		 }
		 
	 } //End of void characters
	 
 
	 
 }
	 
