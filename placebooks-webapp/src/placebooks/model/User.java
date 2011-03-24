package placebooks.model;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;

	@Persistent
	private String email;

	@Persistent
	private String passwordHash;

	@Persistent
	private String name;

	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT, mappedBy="owner")
	private Collection<PlaceBook> placebooks = new HashSet<PlaceBook>();

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

	public void add(final PlaceBook placebook)
	{
		placebooks.add(placebook);
	}

	public void add(final User friend)
	{
		friends.add(friend);
	}

	public Iterable<User> getFriends()
	{
		return friends;
	}

	public String getEmail()
	{
		return email;
	}

	public String getKey()
	{
		return key;
	}

	public String getName()
	{
		return name;
	}

	public String getPasswordHash()
	{
		return passwordHash;
	}

	public void remove(final PlaceBook placebook)
	{
		placebooks.remove(placebook);
	}
	
	public void remove(final User friend)
	{
		friends.remove(friend);
	}
	
	public Iterable<PlaceBook> getPlacebooks()
	{
		return placebooks;
	}

	public void setName(final String name)
	{
		this.name = name;
	}
}
