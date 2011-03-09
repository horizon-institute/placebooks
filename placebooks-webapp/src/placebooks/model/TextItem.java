package placebooks.model;

import java.net.URL;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.*;

import org.w3c.dom.Element;
import org.w3c.dom.Document;


@PersistenceCapable
@Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
public class TextItem extends PlaceBookItem
{
  	private static final Logger log = 
		Logger.getLogger(TextItem.class.getName());

	@Persistent
	private String text; 

	public TextItem(int owner, Geometry geom, URL sourceURL, String text)
	{
		super(owner, geom, sourceURL);
		this.text = text;
	}

	public String getEntityName()
	{
		return TextItem.class.getName();
	}

	public void appendConfiguration(Document config, Element root)
	{
		log.info("TextItem.appendConfiguration(), text=" + this.text);
		Element item = getConfigurationHeader(config);
		Element text = config.createElement("text");
		text.appendChild(config.createTextNode(this.text));
		item.appendChild(text);
		root.appendChild(item);
	}
}
