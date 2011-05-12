package placebooks.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class LoginDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	private String password;

	private String service;

	@ManyToOne
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
