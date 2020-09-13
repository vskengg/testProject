import java.text.ParseException;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

public class NewSite {

	
	public static void main(String[] args) throws ParseException {
		String repoUrl = "http://localhost:4502/crx/server";
		String userName = "admin";
		char[] password = "admin".toCharArray();
		String workspace = "crx.default";

		try {
			System.out.println("============== started ==============");
			Repository repository = JcrUtils.getRepository(repoUrl);
			System.out.println("logging in");
			Session session = repository.login(new SimpleCredentials(userName,
					password), workspace);
			System.out.println("logged in");
			String projCSSPath = "/apps/yebhi/clientlibs/css";
			String projJSPath ="/apps/yebhi/templates/home_yebhi"; //"/apps/yebhi/clientlibs/js";
			Node jsNode = session.getNode(projJSPath);
			System.out.println("jsNode path is :"+jsNode.getPath());
			jsNode.addNode("1.js");//, "nt:file");
			
			
			session.save();
		} catch (RepositoryException e) {
			System.out.println("Repositroy Exception :" + e.getMessage());
		}
	}
	

}



