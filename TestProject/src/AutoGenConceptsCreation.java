import java.text.ParseException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

public class AutoGenConceptsCreation {
	public static void main(String[] args) throws ParseException {
		String repoUrl = "http://wemapp-author-dev3-01:4502/crx/server";
		String userName = "vsingara";
		char[] password = "Armoor02*".toCharArray();
		Session session = null; 
		int a = 1111;
		
		try { 
			Repository repository = JcrUtils.getRepository(repoUrl);
			session = repository.login(new SimpleCredentials(userName,
					password), "crx.default");
			String nodePath = "/etc/tags/Products/Cisco Products/Routers/Branch Routers";
			//String nodePath = "/etc/tags/NetworkingSolutions/Enterprise Networks";
			String num = "17";
			String title = "test-solution-offering-09-12-"+num;
			if(a == 1){
			Node node = session.getNode(nodePath);
			String conceptId = "141111"+num ;
			Node conceptNode = node.addNode(title,"cq:Tag");
			conceptNode.setProperty("metaClass", "Series");
			conceptNode.setProperty("lifeCycle", "active");
			conceptNode.setProperty("jcr:title", title);
			//conceptNode.setProperty("folderPath", "/content/en/us/solutions/enterprise-networks/"+title);
			conceptNode.setProperty("folderPath", "content/en/us/products/devtest/test-dev/test-series-0912-1/"+title);
			conceptNode.setProperty("autoPageCreation", "false");
			conceptNode.setProperty("conceptId", conceptId);
			System.out.println("complte creatig the concept : "+title+" and the concept id is : "+conceptId);
			}else{
				String concetPath = nodePath + "/" + title ;
				Node node = session.getNode(concetPath);
				node.setProperty("lifeCycle", "obsolete"); 
				System.out.println("concept : "+concetPath+" is set to obsolete");
			} 
			session.save();
			
		} catch (RepositoryException e) {
			System.out.println("Repositroy Exception :"+ e);
		} catch (Exception e) {
			System.out.println("Exception :"+ e);
		}finally{
			if(null != session)
				session.logout();
		}
	}
}