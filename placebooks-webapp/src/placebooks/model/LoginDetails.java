package placebooks.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class LoginDetails
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.UUIDHEX)
	private String key;
	
	@Persistent
	private User user;
	
	@Persistent
	private String password;
	
	@Persistent
	private String userid;
	
	@Persistent
	private String username;
	
	@Persistent
	private String service;

	public LoginDetails(final User user, final String service, final String userid, final String username, final String password)
	{
		this.user = user;
		this.userid = userid;
		this.service = service;
		this.username = username;
		this.password = password;
	}
	
	public String getPassword()
	{
		return password;
	}

	public String getUserid()
	{
		return userid;
	}

	public String getUsername()
	{
		return username;
	}

	public String getService()
	{
		return service;
	}
}
