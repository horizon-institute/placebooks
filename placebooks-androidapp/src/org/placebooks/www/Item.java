package org.placebooks.www;

public class Item {
	
	private String key;
	private String owner;
	private String timestamp;
	private float geometry;
	
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
	public float getGeometry(){
		return geometry;
	}
	public void setGeometry(float g){
		this.geometry = g;
	}
	
	
	
	/*
	@Override
	public String toString() {
		return "Item " + "\nkey=" + key + "\nowner=" + owner;
	}
	*/
	
}

