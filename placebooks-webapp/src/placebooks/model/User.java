package placebooks.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;
import static javax.persistence.CascadeType.ALL;

@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class User
{
	@Column(unique = true)
	private String email;

	@ManyToMany
	private Collection<User> friends = new HashSet<User>();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@JsonIgnore
	@OneToMany(mappedBy = "user", cascade = ALL)
	private Collection<LoginDetails> loginDetails = new HashSet<LoginDetails>();

	private String name;

	@JsonIgnore
	private String passwordHash;

	@JsonIgnore
	@OneToMany(mappedBy = "owner", cascade = ALL)
	private Collection<PlaceBook> placebooks = new HashSet<PlaceBook>();

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
		return id;
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