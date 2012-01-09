package placebooks.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Temporal;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import placebooks.controller.PropertiesSingleton;
import placebooks.controller.SearchHelper;

import com.vividsolutions.jts.geom.Geometry;

@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class PlaceBookBinder extends BoundaryGenerator
{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@ManyToOne
	private User owner;

	@Temporal(TIMESTAMP)
	private Date timestamp;

	@JsonIgnore
	protected static final Logger log = Logger.getLogger(PlaceBookBinder.class.getName());

	@JsonSerialize(using = placebooks.model.json.GeometryJSONSerializer.class)
	@JsonDeserialize(using = placebooks.model.json.GeometryJSONDeserializer.class)
	private Geometry geom; // Super-geometry of all PlaceBooks in this binder

	// We are relying on ArrayList to preserve PlaceBook ordering
	@OneToMany(mappedBy = "placeBookBinder", cascade = ALL)
	@OrderColumn
	private List<PlaceBook> pages = new ArrayList<PlaceBook>();

	@ElementCollection
	@Column(columnDefinition = "LONGTEXT")
	private Map<String, String> metadata = new HashMap<String, String>();

	@ElementCollection
	private Map<String, Permission> perms = new HashMap<String, Permission>();

	@JsonIgnore
	private String permsUsers;

	private State state;

	@JsonIgnore
	@OneToOne(cascade = ALL, mappedBy = "placebookBinder", orphanRemoval = true, fetch = LAZY)
	private PlaceBookBinderSearchIndex index = new PlaceBookBinderSearchIndex();

	public enum Permission
	{
		R("r"), W("w"), R_W("r+w");

		private String perms;

		private Permission(final String perms)
		{
			this.perms = perms;
		}

		public final String toString()
		{
			return perms;
		}
	}

	public enum State
	{
		UNPUBLISHED(0), PUBLISHED(1);

		private int value;

		private static final Map<Integer, State> lu = new HashMap<Integer, State>();

		static
		{
			for (State s : EnumSet.allOf(State.class))
				lu.put(s.getValue(), s);
		}

		private State(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}

		public static State get(int value)
		{
			return lu.get(value);
		}

		public final String toString()
		{
			return String.valueOf(value);
		}

	}

	public PlaceBookBinder()
	{
		this.state = State.UNPUBLISHED;
		this.timestamp = new Date();
		geom = null;
		index.setPlaceBookBinder(this);
	}

	public PlaceBookBinder(final User owner)
	{
		this();
		this.owner = owner;
		if (owner != null)
		{
			this.owner.add(this);
			perms.put(owner.getEmail(), Permission.R_W);
			permsUsers = getPermissionsAsString();
		}

		log.info("Created new PlaceBookBinder: timestamp=" + this.timestamp.toString());

	}

	public PlaceBookBinder(final PlaceBookBinder p)
	{
		this.owner = p.getOwner();
		if (this.owner != null)
			this.owner.add(this);

		perms.putAll(p.getPermissions());
		permsUsers = getPermissionsAsString();

		if (p.getGeometry() != null)
			this.geom = (Geometry) p.getGeometry().clone();
		else
			this.geom = null;

		this.timestamp = (Date) p.getTimestamp().clone();

		this.metadata = new HashMap<String, String>(p.getMetadata());

		index.setPlaceBookBinder(this);
		this.index.addAll(p.getSearchIndex().getIndex());

		for (final PlaceBook page : p.getPlaceBooks())
			this.addPlaceBook(new PlaceBook(page));

		log.info("Created copy of PlaceBookBinder; old key = " + p.getKey());

	}

	public void calcBoundary()
	{
		final Set<Geometry> geoms = new HashSet<Geometry>();
		for (final PlaceBook p : getPlaceBooks())
		{
			p.calcBoundary();
			geoms.add(p.getGeometry());
		}

		final Geometry geom = calcBoundary(geoms);
		this.geom = geom;
		log.info("calcBoundary()= " + this.geom);
	}

	public Element createConfigurationRoot(final Document config)
	{
		log.info("PlaceBookBinder.createConfigurationRoot(), key=" + this.getKey());
		final Element root = config.createElement(PlaceBookBinder.class.getName());
		root.setAttribute("key", this.getKey());
		root.setAttribute("owner", this.getOwner().getKey());
		root.setAttribute("state", this.getState().toString());

		if (!perms.isEmpty())
		{
			log.info("Setting perms=" + this.getPermissionsAsString());
			final Element permissions = config.createElement("permissions");
			permissions.appendChild(config.createTextNode(this.getPermissionsAsString()));
			root.appendChild(permissions);
		}

		if (getTimestamp() != null)
		{
			log.info("Setting timestamp=" + this.getTimestamp().toString());
			final Element timestamp = config.createElement("timestamp");
			timestamp.appendChild(config.createTextNode(this.getTimestamp().toString()));
			root.appendChild(timestamp);
		}

		if (getGeometry() != null)
		{
			log.info("Setting geometry=" + this.getGeometry().toText());
			final Element geometry = config.createElement("geometry");
			geometry.appendChild(config.createTextNode(this.getGeometry().toText()));
			root.appendChild(geometry);
		}

		if (!metadata.isEmpty())
		{
			log.info("Writing metadata to config");
			final Element sElem = config.createElement("metadata");
			log.info("metadata set size = " + metadata.size());
			for (final Map.Entry<String, String> e : metadata.entrySet())
			{
				log.info("Metadata element key, value=" + e.getKey().toString() + ", " + e.getValue().toString());
				final Element elem = config.createElement(e.getKey().toString());
				elem.appendChild(config.createTextNode(e.getValue().toString()));
				sElem.appendChild(elem);
			}

			root.appendChild(sElem);
		}

		return root;
	}

	public String getPackagePath()
	{
		return PropertiesSingleton.get(this.getClass().getClassLoader()).getProperty(PropertiesSingleton.IDEN_PKG, "")
				+ "/" + getKey();
	}

	public final String getPermissionsAsString()
	{
		final StringBuffer l = new StringBuffer();
		final Iterator<String> i = getPermissions().keySet().iterator();
		while (i.hasNext())
		{
			final String u = i.next();
			l.append("'" + u + "'=" + getPermission(u).toString());
			if (i.hasNext())
				l.append(",");
		}
		return l.toString();
	}

	public void setPermission(final User user, final Permission p)
	{
		if (perms.get(user.getEmail()) != null)
			perms.remove(user.getEmail());

		perms.put(user.getEmail(), p);
		permsUsers = getPermissionsAsString();
	}

	public void removePermission(final User user)
	{
		perms.remove(user.getEmail());
		permsUsers = getPermissionsAsString();
	}

	public final Permission getPermission(final String email)
	{
		return perms.get(email);
	}

	public final Permission getPermission(final User user)
	{
		return perms.get(user.getEmail());
	}

	public final Map<String, Permission> getPermissions()
	{
		return Collections.unmodifiableMap(perms);
	}

	public void addPlaceBook(final PlaceBook page)
	{
		pages.add(page);
		page.setPlaceBookBinder(this);
	}

	public List<PlaceBook> getPlaceBooks()
	{
		return Collections.unmodifiableList(pages);
	}

	public boolean removePlaceBook(final PlaceBook page)
	{
		page.setPlaceBookBinder(null);
		return pages.remove(page);
	}

	public void setPlaceBooks(final List<PlaceBook> pages)
	{
		this.pages.clear();
		this.pages.addAll(pages);
	}

	public Map<String, String> getMetadata()
	{
		return Collections.unmodifiableMap(metadata);
	}

	public void addMetadataEntry(final String key, final String value)
	{
		if (value == null)
		{
			metadata.remove(key);
		}
		else
		{
			metadata.put(key, value);
		}
	}

	public void addMetadataEntryIndexed(final String key, final String value)
	{
		addMetadataEntry(key, value);
		index.addAll(SearchHelper.getIndex(value));
	}

	public void addSearchIndexedData(final String value)
	{
		index.addAll(SearchHelper.getIndex(value));
	}

	public void rebuildSearchIndex()
	{
		log.debug("rebuildSearchIndex...");
		index.clear();
		final Iterator<Map.Entry<String, String>> i = metadata.entrySet().iterator();
		while (i.hasNext())
		{
			index.addAll(SearchHelper.getIndex((i.next()).getValue()));
		}
		for (final PlaceBook page : pages)
		{
			final Iterator<Map.Entry<String, String>> j = page.getMetadata().entrySet().iterator();
			while (j.hasNext())
			{
				index.addAll(SearchHelper.getIndex((j.next()).getValue()));
			}
		}
		/*
		 * final Set<Map.Entry<String, String>> ss = metadata.entrySet(); for (final
		 * Map.Entry<String, String> s : ss) log.debug("... \"" + s.getKey() + "\" => \"" +
		 * s.getValue() + "\"");
		 * 
		 * for (final String term : index.getIndex()) log.debug("... " + term);
		 */

	}

	public String getMetadataValue(final String key)
	{
		return metadata.get(key);
	}

	public State getState()
	{
		return state;
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public boolean hasMetadata()
	{
		return (!metadata.isEmpty());
	}

	public void setGeometry(final Geometry geom)
	{
		this.geom = geom;
	}

	public void setOwner(final User owner)
	{
		this.owner = owner;
	}

	public void setState(State state)
	{
		this.state = state;
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public Geometry getGeometry()
	{
		return geom;
	}

	public String getKey()
	{
		return id;
	}

	public User getOwner()
	{
		return owner;
	}

	public final PlaceBookBinderSearchIndex getSearchIndex()
	{
		return index;
	}

}
