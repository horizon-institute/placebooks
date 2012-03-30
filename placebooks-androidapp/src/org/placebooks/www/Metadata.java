package org.placebooks.www;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


public class Metadata {
	
	@Element(required=false)
	private String bounds;
	
	@Element(required=false)
	private String name;
	
	@Attribute(required=false)
	private float maxLat;
	
	@Attribute(required=false)
	private float maxLon;
	
	@Attribute(required=false)
	private float minLat;
	
	@Attribute(required=false)
	private float minLon;
	
	
	public String getBounds() {
	      return bounds;           
	   }

	public String getName() {
	     return name;           
	   }
	public float getMaxLat() {
	     return maxLat;           
	   }
	public float getMaxLon() {
	     return maxLon;           
	   }
	public float getMinLat() {
	     return minLat;           
	   }
	public float getMinLon() {
	     return minLon;           
	   }
	

}
