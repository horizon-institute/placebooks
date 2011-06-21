package placebooks.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class LoginDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@JsonIgnore
	private String password;

	private String service;

	@ManyToOne
	@JsonIgnore
	private User user;

	private String userid;

	private String username;

	public LoginDetails(final User user, final String service, final String userid, final String username,
			final String password)
	{
		this.user = user;
		this.userid = userid;
		this.service = service;
		this.username = username;
		this.password = password;
	}

	LoginDetails()
	{
	}

	public String getID()
	{
		return id;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public String getPassword()
	{
		return password;
	}

	public String getService()
	{
		return service;
	}

	public String getUserID()
	{
		return userid;
	}

	public String getUsername()
	{
		return username;
	}
}
