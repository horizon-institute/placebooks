package org.placebooks.www;

public class VideoItem extends Item {

	private String filename;
	private int order;
	private int panel;
	
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
	public int getPanel(){
		return panel;
	}
	public void setPanel(int p){
		this.panel = p;
	}

	
	@Override
	public String toString() {
		return "\nFilename=" + filename + "\nPanel=" +panel + "\nOrder=" +order; 
	}
	
}
