package placebooks.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.ManyToMany;
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

	public enum Permission
	{
		R("r"), R_W("r+w");

		private String perms;

		private Permission(final String perms)
		{
			this.perms = perms;
		}

		@Override
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
			for (final State s : EnumSet.allOf(State.class))
			{
				lu.put(s.getValue(), s);
			}
		}

		public static State get(final int value)
		{
			return lu.get(value);
		}

		private State(final int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}

		@Override
		public final String toString()
		{
			return String.valueOf(value);
		}

	}

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

	// The PlaceBookItem's configuration data
	@ElementCollection
	private Map<String, Integer> parameters = new HashMap<String, Integer>();

	@ElementCollection
	private Map<String, Permission> perms = new HashMap<String, Permission>();

	@ManyToMany
	private Collection<PlaceBookGroup> groups = new HashSet<PlaceBookGroup>();
	
	@JsonIgnore
	private String permsUsers;

	private State state;

	@Override
	public int hashCode()
	{
		if(id != null)
		{
			return id.hashCode();
		}
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(id != null && obj instanceof PlaceBookBinder)
		{
			return id.equals(((PlaceBookBinder)obj).getKey());
		}
		return super.equals(obj);
	}

	@JsonIgnore
	@OneToOne(cascade = ALL, mappedBy = "placebookBinder", orphanRemoval = true, fetch = LAZY)
	private PlaceBookBinderSearchIndex index = new PlaceBookBinderSearchIndex();

	public PlaceBookBinder()
	{
		state = State.UNPUBLISHED;
		timestamp = new Date();
		geom = null;
		index.setPlaceBookBinder(this);
	}

	public PlaceBookBinder(final PlaceBookBinder p)
	{
		owner = p.getOwner();
		if (owner != null)
		{
			owner.add(this);
		}

		perms.putAll(p.getPermissions());
		permsUsers = getPermissionsAsString();

		if (p.getGeometry() != null)
		{
			geom = (Geometry) p.getGeometry().clone();
		}
		else
		{
			geom = null;
		}

		timestamp = (Date) p.getTimestamp().clone();

		metadata = new HashMap<String, String>(p.getMetadata());

		index.setPlaceBookBinder(this);
		index.addAll(p.getSearchIndex().getIndex());

		for (final PlaceBook page : p.getPlaceBooks())
		{
			addPlaceBook(new PlaceBook(page));
		}

		log.info("Created copy of PlaceBookBinder; old key = " + p.getKey());

	}

	public boolean canBeRead(User user)
	{
		if(getState() == PlaceBookBinder.State.PUBLISHED)
		{
			return true;
		}
		
		if (user == null)
		{
			return false;
		}
		
		if (getOwner() != user)
		{
			log.debug("This user is not the owner");
			final PlaceBookBinder.Permission perms = getPermission(user);
			if (perms == null)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean canBeWriten(User user)
	{
		if (getOwner() != user)
		{
			log.debug("This user is not the owner");
			final PlaceBookBinder.Permission perms = getPermission(user);
			if (perms == null || (perms != null && perms == PlaceBookBinder.Permission.R))
			{
				return false;
			}
		}
		
		return true;
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

		log.info("Created new PlaceBookBinder: timestamp=" + timestamp.toString());

	}

	public void addMetadataEntry(final String key, String value)
	{
		if (value == null)
		{
			metadata.remove(key);
		}
		else
		{
			// Strip HTML tags
			value = value.replaceAll("<(.|\n)*?>", "");
			metadata.put(key, value);
		}
	}

	public void addMetadataEntryIndexed(final String key, final String value)
	{
		addMetadataEntry(key, value);
		index.addAll(SearchHelper.getIndex(value));
	}

	public void addPlaceBook(final PlaceBook page)
	{
		pages.add(page);
		page.setPlaceBookBinder(this);
	}

	public void addSearchIndexedData(final String value)
	{
		index.addAll(SearchHelper.getIndex(value));
	}

	@Override
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
		log.info("PlaceBookBinder.createConfigurationRoot(), key=" + getKey());
		final Element root = config.createElement(PlaceBookBinder.class.getName());
		root.setAttribute("key", getKey());
		root.setAttribute("owner", getOwner().getKey());
		root.setAttribute("state", getState().toString());
		if (getOwner() == null)
		{
			log.error("Fatal error: PlaceBookBinder " + getKey() + " has no owner");
		}

		if (!perms.isEmpty())
		{
			log.debug("Setting perms=" + getPermissionsAsString());
			final Element permissions = config.createElement("permissions");
			permissions.appendChild(config.createTextNode(getPermissionsAsString()));
			root.appendChild(permissions);
		}

		if (getTimestamp() != null)
		{
			log.debug("Setting timestamp=" + getTimestamp().toString());
			final Element timestamp = config.createElement("timestamp");
			timestamp.appendChild(config.createTextNode(getTimestamp().toString()));
			root.appendChild(timestamp);
		}

		if (getGeometry() != null)
		{
			log.debug("Setting geometry=" + getGeometry().toText());
			final Element geometry = config.createElement("geometry");
			geometry.appendChild(config.createTextNode(getGeometry().toText()));
			root.appendChild(geometry);
		}

		if (!metadata.isEmpty())
		{
			log.debug("Writing metadata to config");
			final Element sElem = config.createElement("metadata");
			log.debug("metadata set size = " + metadata.size());
			for (final Map.Entry<String, String> e : metadata.entrySet())
			{
				log.debug("Metadata element key, value=" + e.getKey().toString() + ", " + e.getValue().toString());
				final Element elem = config.createElement(e.getKey().toString());
				elem.appendChild(config.createTextNode(e.getValue().toString()));
				sElem.appendChild(elem);
			}

			root.appendChild(sElem);
		}

		return root;
	}

	public Geometry getGeometry()
	{
		return geom;
	}

	public String getKey()
	{
		return id;
	}

	public Map<String, String> getMetadata()
	{
		return Collections.unmodifiableMap(metadata);
	}

	public String getMetadataValue(final String key)
	{
		return metadata.get(key);
	}

	public User getOwner()
	{
		return owner;
	}

	public String getPackagePath()
	{
		return PropertiesSingleton.get(this.getClass().getClassLoader()).getProperty(PropertiesSingleton.IDEN_PKG, "")
				+ "/" + getKey();
	}

	public Map<String, Integer> getParameters()
	{
		return Collections.unmodifiableMap(parameters);
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

	public final String getPermissionsAsString()
	{
		final StringBuffer l = new StringBuffer();
		final Iterator<String> i = getPermissions().keySet().iterator();
		while (i.hasNext())
		{
			final String u = i.next();
			l.append("'" + u + "'=" + getPermission(u).toString());
			if (i.hasNext())
			{
				l.append(",");
			}
		}
		return l.toString();
	}

	public List<PlaceBook> getPlaceBooks()
	{
		return Collections.unmodifiableList(pages);
	}

	public final PlaceBookBinderSearchIndex getSearchIndex()
	{
		return index;
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

	public void removePermission(final User user)
	{
		perms.remove(user.getEmail());
		permsUsers = getPermissionsAsString();
	}

	public boolean removePlaceBook(final PlaceBook page)
	{
		page.setPlaceBookBinder(null);
		return pages.remove(page);
	}

	public void setGeometry(final Geometry geom)
	{
		this.geom = geom;
	}

	public void setMetadata(final Map<String, String> metadata)
	{
		this.metadata.clear();
		this.metadata.putAll(metadata);
	}

	public void setOwner(final User owner)
	{
		this.owner = owner;
	}

	public void setParameters(final Map<String, Integer> parameters)
	{
		this.parameters.clear();
		this.parameters.putAll(parameters);
	}

	public void setPermission(final User user, final Permission p)
	{
		if (perms.get(user.getEmail()) != null)
		{
			perms.remove(user.getEmail());
		}

		perms.put(user.getEmail(), p);
		permsUsers = getPermissionsAsString();
	}

	public void setPermissions(final Map<String, Permission> permissions)
	{
		perms = permissions;
		permsUsers = getPermissionsAsString();
	}

	public void setPlaceBooks(final List<PlaceBook> pages)
	{
		this.pages.clear();
		this.pages.addAll(pages);
	}

	public void setState(final State state)
	{
		this.state = state;
	}

	public void setTimestamp(final Date timestamp)
	{
		this.timestamp = timestamp;
	}

	public void add(PlaceBookGroup group)
	{
		for(PlaceBookGroup altGroup: groups)
		{
			if(altGroup.getId().equals(group.getId()))
			{
				return;
			}
		}		
		groups.add(group);		
	}

	public void remove(PlaceBookGroup group)
	{
		groups.remove(group);		
	}
}
