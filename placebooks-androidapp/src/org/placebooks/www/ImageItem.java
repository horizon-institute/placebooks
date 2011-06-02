package org.placebooks.www;

//import java.util.*;

public class ImageItem extends Item {
	private String filename;
	private String URL;
	private int order;


	public String getFilename() {
		return filename;
	}
	
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		this.URL = uRL;
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

		return "\nFilename=" + filename + "\nURL=" + URL + "\nOrder=" +order;
		
	}
}
