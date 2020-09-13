import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

public class UrlHit {
	public static void main(String[] args) {
		/* For Hitting a URL */
		String pageTitle = "t13";
		String url = "http://localhost:4502/content/repTest.html?pageName="+pageTitle;
		urlHitting(url);
	}

	public static void urlHitting(String urlToHit) {
		HttpClient client = new HttpClient();
		String username = "admin";
		String password = "admin";
		// Code for Authentication
		int portNum = 4502;
		client.getState().setCredentials(new AuthScope("localhost", portNum),
				new UsernamePasswordCredentials(username, password));
		GetMethod url = new GetMethod(urlToHit);
		String resultStringOn = null ;
		url.setDoAuthentication(true);
		try {
			int statusOn = client.executeMethod(url);
			if (statusOn != 401) {
				resultStringOn = url.getResponseBodyAsString();
				System.out.println("out put from the hit is : "
						+ resultStringOn);
			}
		} catch (Exception e) {
			System.err.println("exception caught : " + e);
		}
	}
}
