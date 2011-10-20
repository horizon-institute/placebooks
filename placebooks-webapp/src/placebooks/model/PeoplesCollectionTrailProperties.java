package placebooks.model;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * Class to encapsulate response from Peoples Collection API for Trail Properties
 * @author pszmp
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PeoplesCollectionTrailProperties {
    int id;
    String markup;
    String title;
    String titlecym;
    
    @JsonProperty("ex-small-thumbpath")
    String ex_small_thumbpath;
    String icontype;
    String objecttype;
    String mediaurl;
    String dateupdated;
    
    public PeoplesCollectionTrailProperties()
    {
    	
    }
    
    public PeoplesCollectionTrailProperties(int id, String markup, String title, String titlecym, String ex_small_thumbpath, String icontype, String mediaurl, String objecttype, String dateupdated)
    {
    	this.id = id;
    	this.markup = markup;
    	this.title = title;
    	this.titlecym = titlecym;
    	this.ex_small_thumbpath = ex_small_thumbpath;
    	this.icontype = icontype;
    	this.objecttype = objecttype;
    	this.mediaurl = mediaurl;
    	this.dateupdated = dateupdated;
    }
    
    public int GetId()
    {
    	return id;
    }
    
    public String GetMarkup()
    {
    	return markup;
    }
    
    public String GetTitle()
    {
    	return title;
    }
    
    public String GetExSmallThumbPath()
    {
    	return ex_small_thumbpath;
    }
    
    public String GetIcontype()
    {
    	return icontype;
    }
    
    public String GetTytleCym()
    {
    	return titlecym;
    }
    
    public String GetObjectType()
    {
    	return objecttype;
    }
    public String GetMediaURL()
    {
    	return mediaurl;
    }
    
    public String GetDateUpdated()
    {
    	return dateupdated;
    }
}
