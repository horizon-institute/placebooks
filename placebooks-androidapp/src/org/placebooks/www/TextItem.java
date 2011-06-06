package org.placebooks.www;

public class TextItem extends Item {
	
	private String text;
	private String url;
	private int panel;
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
		this.order = o;
	}
	public int getPanel(){
		return panel;
	}
	public void setPanel(int p){
		this.panel = p;
	}
	
	@Override
	public String toString() {
		return "Text=" + text + "\nURL=" + url + "\nPanel=" +panel + "\nOrder=" + order; 
	}
	
}
