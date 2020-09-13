import java.text.ParseException;
import java.util.ArrayList;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionIterator;

import org.apache.commons.collections.iterators.ReverseListIterator;
import org.apache.jackrabbit.commons.JcrUtils;

public class GetI18Nodes {
	
	public static void main(String[] args) throws ParseException {
		
		try {
		Session session = getSession("http://wemapp-author-devint3-01:4502/crx/server","sapola","Abyan786".toCharArray());
		Node i18BaseNode = session.getNode("/apps/cdc/i18n/wcm/handlers/en");
		NodeIterator nI = i18BaseNode.getNodes();
		int i = 0 ;
		while (nI.hasNext()) {
			Node curNode = nI.nextNode();
			//System.out.println("node "+i+ " is : "+curNode.getName());
			//System.out.println("message is : "+curNode.getProperty("sling:message").getValue().getString());
			System.out.println(curNode.getName() + ";" + curNode.getProperty("sling:message").getValue().getString());
			i++;
		}
	
		
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void setValues(String attr,Node node,String[] value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException{
		node.setProperty(attr,value);
		System.out.println("setting the "+attr);
	}
	public static void setValues(String attr,Node node,String value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException{
		node.setProperty(attr,value);
		System.out.println("setting the "+attr+ " as :"+value);
	}
	public static void getValue(String attr,Node node) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException{
		Property prop = node.getProperty(attr);
		if(prop.isMultiple())
		{
			Value[] currentValues = prop.getValues() ;
			for (int i = 0; i < currentValues.length; i++) {
				System.out.println("tag "+i+"is :"+currentValues[i].getString());
			}
		}else{
			String property = prop.getValue().getString();
			System.out.println(attr+" is :"+property);
		}
	}
		public static Session getSession(String repoUrl,String userName,char[] password){
			Session session = null;
			try {
				System.out.println("============== started ==============");
				Repository repository = JcrUtils.getRepository(repoUrl);
				System.out.println("logging in");
				session = repository.login(new SimpleCredentials(userName,
						password), "crx.default");
			}
			catch (RepositoryException e) {
				System.out.println("Repositroy Exception :" + e.getMessage());
			}
			return session;
		
	}
		public static ReverseListIterator getReversalArrayList(
				VersionIterator vIterator) {
			ArrayList<Version> list = new ArrayList<Version>();
			while (vIterator.hasNext()) {
				Version version = vIterator.nextVersion();
				list.add(version);

			}
			ReverseListIterator reverseListIterator = new ReverseListIterator(list);
			return reverseListIterator;
		}

}





