import java.sql.Array;
import java.text.ParseException;
import java.util.ArrayList;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

public class CreateAndActivatePages {
	public static void main(String[] args) throws ParseException {
		String repoUrl = "http://localhost:4502/crx/server";
		String userName = "admin";
		char[] password = "admin".toCharArray();
		
		
		try { 
			Repository repository = JcrUtils.getRepository(repoUrl);
			Session session = repository.login(new SimpleCredentials(userName,
					password), "crx.default");
			String nodePath = "/content/en/us/support/intqa/routers/wem-int-series-active-concept";
			Node node = session.getNode(nodePath);
			
			NodeIterator nI = node.getNodes();
			int i = 0;
			String mA = "cisco:markedForActivation" ;
			while (nI.hasNext()) {
				Node tempNode = (Node) nI.next();
				if(tempNode.hasNode("jcr:content"))
					tempNode = tempNode.getNode("jcr:content");
				if(tempNode.hasProperty(mA)){
					//System.out.println("node name having MA "+i+" is :"+tempNode.getPath());
					//System.out.println("markedForActivation is : "+tempNode.getProperty(mA).getValue().getString());
					System.out.println("removing the proper for the path "+tempNode.getPath());
					tempNode.getProperty(mA).remove();
				}
				i++;
			}
			session.save();
		} catch (RepositoryException e) {
			System.out.println("Repositroy Exception :"+ e);
		} catch (Exception e) {
			System.out.println("Exception :"+ e);
		}
	}
}
/*	ArrayList<String> conceptList = new ArrayList<String>();
for(int i =0 ; i < 10 ; i++){
conceptList.add("testString"+i);
}
for(int j=0;j<10;j++){
	System.out.println("concept : "+j+ "is : "+conceptList.get(j));
}*/