package org.placebooks.www;

public class VideoItem extends Item {

	private String filename;
	private int order;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getOrder(){
		return order;
	}
	public void setOrder(int o){
		order = o;
	}
	
	@Override
	public String toString() {
		return "\nFilename=" + filename + "\nOrder=" +order; 
	}
	
}
