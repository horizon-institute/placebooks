package org.placebooks.www;

import android.content.Context;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.util.*;
import android.widget.*;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;



public class MyListAdapter extends BaseAdapter {
	
	View renderer;
    List<MyListItemModel> items;
    ArrayList<HashMap<String, String>> mylist;
    private LayoutInflater mInflater;
    private Context context;


     /*
      * Constructor takes in the context from the shelf class and
      * set the LayoutInflater
      */
    public MyListAdapter(Context c){//(View renderer) {
       // this.renderer = renderer;
    	this.context=c;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    

    /*
     *  Method for setting the list of items
     *  Call it when there is data ready and want to display it
     */
    public void setModel(List<MyListItemModel> items){
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items!=null?items.size():0;
    }
    
    
    
    @Override
    public Object getItem(int position) {
        return items!=null?items.get(position):null;
    }
    
    @Override
    public long getItemId(int position) {
        return items!=null?items.get(position).getBookId():-1;
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     * Method overrides the view in each row of the listview
     * We use the position to get the items
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        if(convertView==null){
            //convertView = renderer;
            convertView = mInflater.inflate(R.layout.shelf, null);
           
        }
        MyListItemModel item = items.get(position);
        TextView label = (TextView)convertView.findViewById(R.id.item_title);
        label.setText(item.getBookTitle());
        TextView label2 = (TextView)convertView.findViewById(R.id.item_subtitle);
        label2.setText(item.getDescription());  
        final Button button = (Button)convertView.findViewById(R.id.btn_download);
        button.setOnClickListener(item.dl_listener);
        final Button button2 = (Button)convertView.findViewById(R.id.btn_view);
        button2.setOnClickListener(item.view_listener);
        
        //check if the package has already been downloaded to the SDCard
		File f = new File(Environment.getExternalStorageDirectory()+ "/PlaceBooks/unzipped" + item.getPackagePath());
		if(f.exists()){
			//If exists then hide 'download' button and show 'view' button
			//button2.setText("View");
			button.setVisibility(View.GONE);
			button2.setVisibility(View.VISIBLE);
		}
		else{
			//If it does not exist then hide 'view' button and show 'download' button
			//button.setText("Download");
			button.setVisibility(View.VISIBLE);
			button2.setVisibility(View.GONE);
			
		}
        
       /*
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                shelf.downloadPlaceBook(item.key);
            	//Toast msg = Toast.makeText(context, "Message " + item.key, Toast.LENGTH_LONG);
        		//msg.show();
            }
        });
        */
        
        return convertView;
    }
   
}
	   
