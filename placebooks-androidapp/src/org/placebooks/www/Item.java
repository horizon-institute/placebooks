package org.placebooks.www;

import com.vividsolutions.jts.geom.Geometry;
//import java.text.ParseException;


public abstract class Item {
	
	//attributes
	private String key;
	private String owner;
	
	private String timestamp;
	private Geometry geometry;
	private String type
	;
	//parameters
	private int panel;
	private int order;
	//metadata
	//key, value
	
	
	//Make a new placebook item
/*	public Item(final String owner, final Geometry geom){
		this.owner = owner;
		this.geometry = geom;
		
	}
	*/

	
	public String getType(){
		return type;
	}
	public void setType(String s){
		this.type = s;
	}
	
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getTimeStamp(){	
		return timestamp;
	}
	public void setTimeStamp(String ts){
		this.timestamp = ts;		
	}
	public Geometry getGeometry(){
		return geometry;
	}
	public void setGeometry(Geometry g) {//throws ParseException{
		this.geometry = g;
	}
	public int getOrder(){
		return order;
	}
	public void setOrder(int o){
		this.order = o;
	}
	public int getPanel(){
		return panel;
	}
	public void setPanel(int p){
		this.panel = p;
	}
	
	//String getMetadata()
	//String setMetadata(String name)
	
	
	
	@Override
	public String toString() {
		return "<geometry>" + geometry + "</geometry>";
	}
	
	
}

