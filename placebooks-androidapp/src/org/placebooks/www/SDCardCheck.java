package org.placebooks.www;

public class SDCardCheck {
	
	    /*
	    * A method that checks if an SDCard is present on the mobile device
	    */  
	   public boolean isSdPresent() {
		   
		   return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		   
	   }

}
