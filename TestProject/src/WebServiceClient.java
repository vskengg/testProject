
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class WebServiceClient {
 
	private static Logger log = Logger.getLogger(WebServiceClient.class.getName());
	private String hostName = null;
	private String userId = null;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * This method calls the activatecontent webservice using the generated OAM
	 * token.
	 * 
	 * @param path
	 * @return
	 * @throws JSONException
	 */
	public String replicateContent(String path, String action) throws JSONException {
		String activationStatus = null;
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			log.debug("path is : " + path);
			log.debug("action is : " + action);
			HttpHost targetHost = new HttpHost(hostName, 4502, "http");
			HttpPost post = new HttpPost("/bin/replicate.json");
			post.addHeader("AUTH_USER", userId);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("path", path));
			nameValuePairs.add(new BasicNameValuePair("cmd", action));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(targetHost, post);
			log.debug(response.getStatusLine());
			StatusLine statusLne = response.getStatusLine();
			int code = statusLne.getStatusCode();
			activationStatus = Integer.toString(code);
			log.debug("the replication status is : " + activationStatus);
		} catch (Exception e) {
			log.debug("Exception in replicateContent is : " + e.toString());
		} finally {
			client.getConnectionManager().shutdown();
		}
		return activationStatus;
	}

	/**
	 * This method calls the activatecontent webservice using the generated OAM
	 * token.
	 * 
	 * @param path
	 * @return
	 * @throws JSONException
	 */
	public String replicateBookContent(String path, String action) throws JSONException {
		DefaultHttpClient client = new DefaultHttpClient();
		BufferedReader rd = null;
		String statusCode = null;
		try {
			log.debug("path is : " + path);
			log.debug("action is : " + action);
			HttpHost targetHost = new HttpHost(hostName, 4502, "http");
			String actionurl = null;
			if (action.equals("Activate")) {
				actionurl = "/c/wcm/servlets/services/wem.activatecontent.json";
			} else if (action.equals("Deactivate")) {
				actionurl = "/c/wcm/servlets/services/wem.deactivatecontent.json";
			}
			HttpPost post = new HttpPost(actionurl);
			post.addHeader("AUTH_USER", userId);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("path", path));
			nameValuePairs.add(new BasicNameValuePair("deactivateSrcContent", "YES"));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(targetHost, post);
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer content = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				content.append(line);
			}
			log.debug(content.toString());
			JSONObject jsonObject = new JSONObject(content.toString());
			statusCode = jsonObject.getString("statuscode");
			String statusMessage = jsonObject.getString("statusmessage");
			String sourcePath = jsonObject.has("sourcepath") ? jsonObject.getString("sourcepath") : "";
			log.debug("statusCode " + statusCode);
			log.debug("statusMessage" + statusMessage);
			log.debug("sourcePath" + sourcePath);
		} catch (Exception e) {
			log.error("Exception in replicateBookContent is : ", e);
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					log.error("IOException in replicateBookContent is : ", e);
				}
			}
			client.getConnectionManager().shutdown();
		}
		return statusCode;
	}

	/*public static void main(String[] args) throws JSONException {
		WebServiceClient client = new WebServiceClient();
		String filePath = "/content/dam/en/us/td/docs/concepts/11.doc";
		// client.replicateContent(filePath, "Deactivate");
		client.replicateBookContent(filePath, "Deactivate");
	}*/

}
