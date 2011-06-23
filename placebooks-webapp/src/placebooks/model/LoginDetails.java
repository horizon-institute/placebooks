package placebooks.model;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class LoginDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@JsonIgnore
	@Temporal(TIMESTAMP)
	private Date lastSync;

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

	public Date getLastSync()
	{
		return lastSync;
	}

	public String getPassword()
	{
		return password;
	}

	public String getService()
	{
		return service;
	}

	public User getUser()
	{
		return user;
	}

	public String getUserID()
	{
		return userid;
	}

	public String getUsername()
	{
		return username;
	}

	public void setLastSync()
	{
		lastSync = new Date();
	}

	public void setUserID(final String userID)
	{
		this.userid = userID;
	}
}