package org.placebooks.www;
import java.util.*;
/*The Binder class is a model class for a placebooks config.xml file
* The binder binds pages together into 1 big complete set
*/
public class Binder {

	private ArrayList<String> pageFilenames = new ArrayList<String>();


    public void addPageToBinder(String pageFilename) {
        this.pageFilenames.add(pageFilename);
    }
    public ArrayList<String> getAlPages(){
    	return this.pageFilenames;
    }

	
}
