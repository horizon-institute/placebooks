package org.placebooks.www;

public class TextItem extends Item {

	private String url;
	private String text;
	private int order;
	
	public String getURL() {
		return url;
	}
	public void setURL(String uRL) {
		url = uRL;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getOrder(){
		return order;
	}
	public void setOrder(int o){
		order = o;
	}
	
	@Override
	public String toString() {
		return "Text=" + text + "\nURL=" + url + "\nOrder=" + order; 
	}
	
}
