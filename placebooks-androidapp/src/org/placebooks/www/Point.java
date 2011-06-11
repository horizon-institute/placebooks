package org.placebooks.www;

import java.util.*;

public class Point implements Comparable<Point>{

	public String type;
	public String data;			//maybe make this an arraylist? e.g filename, geometry, url..etc
	public int panel;
	public int order;
	public String url;
	//public ArrayList<Float> geometry;		//Every item will have a geometry (point or if it is a mapimageitem then a polygon)
	
	public Point(String data, int panel, int order, String type){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
	}
	
	public Point(String data, int panel, int order, String type, String url){
		this.data = data;
		this.panel = panel;
		this.order = order;
		this.type = type;
		this.url = url;
	}
	
	
	
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
