package org.placebooks.www;


public class Item implements Comparable<Item>{
	
	private String key;
	private String owner;
	private String timestamp;
	private String geometry;
	//private Geometry geom;
	private String type;
	private int panel;
	private int order;
	
	/*
	 * Compare a given order number with this object.
	 * If order of this object is greater than the
	 * received object, then this object is
	 * greater than the other
	 */
	public int compareTo(Item o) {
        return this.order - o.order ;
    }
	

	
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
	public String getGeometry(){
		return geometry;
	}
	public void setGeometry(String g){
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

