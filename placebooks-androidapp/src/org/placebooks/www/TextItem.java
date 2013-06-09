package org.placebooks.www;

public class TextItem extends Item {
	
	private String text;
	private String url;
	private int mapPage = -1;
	private int mapMarker = -1;
	
	public String getURL() {
		return url;
	}
	public void setURL(String url) {
		this.url = url;
	}
	public String getText() {
		return this.text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public int getMapPage(){
		return mapPage;
	}
	
	public void setMapPage(int pageNumber){
		this.mapPage = pageNumber;
	}
	

	public int getMapMarker(){
		return mapMarker;
	}
	
	public void setMapMarker(int mapMarker){
		this.mapMarker = mapMarker;
	}
	
	@Override
	public String toString() {
		return "Text=" + text + "\nurl=" + url + "\nColumn=" +super.getColumn() + "\nOrder=" + super.getOrder(); 
	}
	
}
