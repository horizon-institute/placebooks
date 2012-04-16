package org.placebooks.www;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.content.Context;


public class MyListItemModel { //that's our book
    private String title; // the book's title
    private String description;	//the book's description
    private int id; //book owner id
    private String owner;  //book owner name e.g stuart
    private String key;	//book key
    private String packagePath;	//package path for the book
    private Context context;

    
    public MyListItemModel(Context c){
     	this.context=c;

     }
     
    
    public String getBookTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getKey() {
        return key;
    }    
    public void setKey(String key){
    	this.key = key;
    }
    
    public String getOwner(){
    	return owner;
    }
    
    public void setOwner(String theOwner){
    	this.owner = theOwner;
    }
    
    public int getID() {
        return id;
    }
    
    public void setID(int id){
    	this.id = id;
    }
    
    public String getPackagePath(){
    	return packagePath;
    }
    
    public void setPackagePath(String p){
    	this.packagePath = p;
    }
    
    
    //download button
    OnClickListener dl_listener = new OnClickListener(){ // the book's action
        @Override
        public void onClick(View v) {
            //code for the button action
        	
        	//Toast msg = Toast.makeText(context, "Message " + key, Toast.LENGTH_LONG);
    		//msg.show();
        	
        }
    };
    
    //view button
    OnClickListener view_listener = new OnClickListener(){ 
        @Override
        public void onClick(View v) {
       
        	
        }
    };
    
    
    int getBookId(){
      
        return title.hashCode();
    }
}