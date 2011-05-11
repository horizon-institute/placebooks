package placebooks.model;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;

@PersistenceCapable
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class User
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Unique
	@Persistent
	private String email;

	@JsonIgnore
	@Persistent
	private String passwordHash;

	@Persistent
	private String name;

	@JsonIgnore
	@Persistent(persistenceModifier = PersistenceModifier.PERSISTENT, mappedBy = "owner")
	private Collection<PlaceBook> placebooks = new HashSet<PlaceBook>();

	@JsonIgnore
	@Persistent(persistenceModifier = PersistenceModifier.PERSISTENT, mappedBy = "user")
	private Collection<LoginDetails> loginDetails = new HashSet<LoginDetails>();

	@Persistent
	@Join
	private Collection<User> friends = new HashSet<User>();

	public User(final String name, final String email, final String passwordHash)
	{
		this.name = name;
		this.email = email.toLowerCase();
		this.passwordHash = passwordHash;
	}

	User()
	{

	}

	public void add(final LoginDetails loginDetail)
	{
		loginDetails.add(loginDetail);
	}

	public void add(final PlaceBook placebook)
	{
		placebooks.add(placebook);
	}

	public void add(final User friend)
	{
		friends.add(friend);
	}

	public String getEmail()
	{
		return email;
	}

	public Iterable<User> getFriends()
	{
		return friends;
	}

	public String getKey()
	{
		return key;
	}

	public LoginDetails getLoginDetails(final String service)
	{
		for (final LoginDetails login : loginDetails)
		{
			if (login.getService().equals(service)) { return login; }
		}
		return null;
	}

	public String getName()
	{
		return name;
	}

	public String getPasswordHash()
	{
		return passwordHash;
	}

	public Iterable<PlaceBook> getPlacebooks()
	{
		return placebooks;
	}

	public void remove(final PlaceBook placebook)
	{
		placebooks.remove(placebook);
	}

	public void remove(final User friend)
	{
		friends.remove(friend);
	}

	public void setName(final String name)
	{
		this.name = name;
	}
}