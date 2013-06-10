package org.placebooks.www;


public class ImageItem extends Item {
	
	private String filename;
	private String url;
	private int imageHeight;
	private int mapPage = -1;
	private int mapMarker = -1;


	public String getFilename() {
		return filename;
	}
	
	public String getURL() {
		return url;
	}
	
	public int getImageHeight(){
		return imageHeight;
	}
	
	public void setURL(String url) {
		this.url = url;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public void setImageHeight(int height){
		this.imageHeight = height;
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

		return "\nFilename=" + filename + "\nurl=" + url + "\nColumn=" +super.getColumn() + "\nOrder=" +super.getOrder();
		
	}
}
