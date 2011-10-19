/**
 * 
 */
package placebooks.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import placebooks.model.PeoplesCollectionLoginResponse;

/**
 * @author pszmp
 *
 */
public class PeoplesCollectionHelper
{
	public final static String SERVICE_NAME = "PeoplesCollection";
	private static final String apiBaseUrl = "http://www.peoplescollectionwales.com/mobile";


	protected static final ObjectMapper mapper = new ObjectMapper();
	private static final Logger log = Logger.getLogger(PeoplesCollectionHelper.class);
	
	/**
	 * Perform a post to the given People's Collection api destination with the parameters specified
	 * 
	 * @param postDestination
	 *            API destination after http://mubaloo.alpha.peoplescollection.or - e.g. user/trips
	 * @param Hashtable
	 *            <String, String> params a hastable of the parameters to post to the api with name
	 *            / values as strings
	 * @return String A string containing the post response from Everytrail
	 */
	private static String getPostResponseWithParams(final String postDestination, final Hashtable<String, String> params)
	{
		final StringBuilder postResponse = new StringBuilder();

		// Construct data to post by iterating through parameter Hashtable keys Enumeration
		final StringBuilder data = new StringBuilder();
		//final Enumeration<String> paramNames = params.keys();
		try
		{
			/*while (paramNames.hasMoreElements())
			{
				final String paramName = paramNames.nextElement();
				data.append(URLEncoder.encode(paramName, "UTF-8") + "="
						+ URLEncoder.encode(params.get(paramName), "UTF-8"));
				if (paramNames.hasMoreElements())
				{
					data.append("&");
				}
			}*/
			data.append(mapper.writeValueAsString(params));
			log.debug("JSON post: " + data.toString());
			

			final URL url = new URL(apiBaseUrl + postDestination);
			final URLConnection conn = CommunicationHelper.getConnection(url);

			conn.setDoOutput(true);
			final OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data.toString());
			wr.flush();

			// Get the response
			final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null)
			{
				postResponse.append(line);
			}
			wr.close();
			rd.close();
		}
		catch (final Exception ex)
		{
			log.debug(ex.getMessage());
		}
		

		return postResponse.toString();
	}

	/**
	 * Log in to the People's Collection api
	 * @param username
	 * @param password
	 * @return 
	 */
	public static PeoplesCollectionLoginResponse Login(String username, String password)
	{
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("username", username);
		params.put("password", password);
		String response = getPostResponseWithParams("/authenticate", params);
		
		PeoplesCollectionLoginResponse returnResult = null;
		mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
		try
		{
			returnResult = mapper.readValue(response.toString(), PeoplesCollectionLoginResponse.class);
			log.debug("PeoplecCollection response decoded: " + (returnResult.GetIsValid() ? "valid " : "not valid ")  + returnResult.GetReason());
		}
		catch(Exception ex)
		{
			log.error("Couldn't decode response from json : " + response.toString(), ex);
			returnResult = new PeoplesCollectionLoginResponse(false, "Unable to decode response:\n" +  ex.getMessage());			
		}
		
		return returnResult;
	}

}
