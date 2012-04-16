package org.placebooks.www;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Trkpt {
	
    	
	@Attribute
	public Double lat;	
	
	@Attribute
	public Double lon;
	
	@Element(required=false)
	public String ele;
	
	@Element(required=false)
	public String time;
	
	@Element(required=false)
	public String speed;
	
	
	
	public Double getLat() {
		return lat;
     }

     public Double getLon() {
          return lon;
     }

     public String getEle() {
          return ele;
     }

     public String getTime() {
          return time;
     }

     @Override
 	public String toString(){

 		return "latitude=" + lat.toString() + "longitude=" + lon.toString();
 		
 	}



}
