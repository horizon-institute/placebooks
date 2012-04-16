package org.placebooks.www;


public class ImageItem extends Item {
	
	private String filename;
	private String url;
	private int imageHeight;


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

	@Override
	public String toString() {

		return "\nFilename=" + filename + "\nurl=" + url + "\nPanel=" +super.getPanel() + "\nOrder=" +super.getOrder();
		
	}
}
