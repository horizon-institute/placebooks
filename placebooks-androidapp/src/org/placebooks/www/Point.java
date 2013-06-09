package org.placebooks.www;

import java.util.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Coordinate;

//This class is the Page type, so a page has items with type, data, panel, order..etc
public class Point implements Comparable<Point>{

	public String type;
	public String data;			//maybe make this an arraylist? e.g filename, geometry, url..etc
	public int panel;
	public int order;
	public String url;
	public String itemKey;
	//public Geometry geometry;		//Every item will have a geometry (point or if it is a mapimageitem then a polygon)
	public Coordinate[] co;
	public String gpxFilename;
	public int height;
	public int mapPage;
	public int mapMarker = -1;
	
	// without coordinates without url
	public Point(String data, int panel, int order, String type, String itemKey){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
		this.itemKey = itemKey;
	}
	
	// without coordinates without url with imageHeight
	public Point(String data, int panel, int order, String type, String itemKey, int height){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
		this.itemKey = itemKey;
		this.height = height;
	}
	
	// without coordinates without url
	public Point(String data, int panel, int order, String type, String itemKey, Coordinate[] co){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
		this.itemKey = itemKey;
		this.co = co;
	}
	
	// with coordinates without url with imageHeight
	public Point(String data, int panel, int order, String type, String itemKey, Coordinate[] co, int height, int mapPage, int marker){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
		this.itemKey = itemKey;
		this.co = co;
		this.height = height;
		this.mapPage = mapPage;
		this.mapMarker = marker;
	}
	
	
	//with coordinates without url with gpx (so for GPX)
	public Point(String data, int panel, int order, String type, String itemKey, Coordinate[] co, String gpxFilename){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
		this.itemKey = itemKey;
		this.co = co;
		this.gpxFilename = gpxFilename;
		
	}
	
	
	//without coordinates, with url - WEB BUNDLE
	public Point(String data, int panel, int order, String type, String itemKey, String url){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
		this.itemKey = itemKey;
		this.url = url;
	}

	/*
	//with coordinates and with url
	public Point(String data, int panel, int order, String type, String itemKey, Coordinate[] co, String url){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
		this.itemKey = itemKey;
		this.co = co;
		this.url = url;
		
	}
*/

	
	/*
	 * Getter methods
	 */
	public String getType(){
		return type;
	}
	public String getData(){
		return data;
	}
	public int getPanel(){
		return panel;
	}
	public int getOrder(){
		return order;
	}
	public String getUrl(){
		return url;
	}
	public String getItemKey(){
		return itemKey;
	}
	public Coordinate[] getGeometryCoordinates(){
		return co;
	}
	public String getGpxFilename(){
		return gpxFilename;
	}
	public int getImageHeight(){
		return height;
	}
	public int getMapPage(){
		return mapPage;
	}
	public int getMapMarker(){
		return mapMarker;
	}
	
	/*
	 * Setter methods
	 */
	public void setType(String t){
		this.type = t;
	}
	public void setData(String d){
		this.data = d;
	}
	public void setPanel(int p){
		this.panel = p;
	}
	public void setOrder(int o){
		this.order = o;
	}
	public void setGpxFilename(String gpxFilename){
		this.gpxFilename = gpxFilename;
	}
	public void setMapMarker(int marker){
		this.mapMarker = marker;
	}
	
	/*
	 * Compare a given order number with this object.
	 * If order of this object is greater than the
	 * received object, then this object is
	 * greater than the other
	 */
	public int compareTo(Point o) {
        return this.order - o.order ;
    }
	
	
	@Override
	public String toString() {
		return "<type>" +type +"</type>" + "<data>" + data + "</data>";
	}

	
}
