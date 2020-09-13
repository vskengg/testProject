import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.jcr.Node;
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

public class CQConnectTest {
	
	public static void main(String[] args) throws ParseException {
		
		try {
		Session session = getSession("http://localhost:4508/crx/server","admin","admin".toCharArray());
		Node n1 = session.getNode("/apps/core/wcm/components");
		System.out.println("node name is : "+n1.getName());
		
			Node tagNode = session.getNode("/etc/tags/Products/Cisco Products/Routers/Branch Routers/Cisco 5700 Series Routers/Cisco 5720 Integrated Encryption Router");
		String createdTime = tagNode.getProperty("jcr:created").getValue().getString();
		System.out.println("createdTime is : "+createdTime);
		long createdDate = Date.parse(createdTime);
		System.out.println("createdTime is : "+createdDate);
		long currentTime = System.currentTimeMillis();
		/*
		long diff = currentTime - createdDate ;
		System.out.println("dif is : "+diff);
		*/
		
		
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		
		//Session stageSession = getSession("http://wemapp-author-stage3-01:4502/crx/server","vsingara","Armoor02!".toCharArray());
		//Session devSession = getSession("http://wemapp-author-dev3-01:4502/crx/server","vsingara","Armoor02*".toCharArray());
		//Session devPublishSession = getSession("http://wemapp-publish-dev3-01:4503/crx/server","vsingara","Armoor02*".toCharArray());
		/*Session localSession = getSession("http://wemapp-publish-prod1-01:4503/crx/server","sapola","Abyan786".toCharArray());
		localSession.logout();
		System.out.println("logged out!");*/
		//Session devIntSession = getSession("http://wemapp-author-devint3-01:4502/crx/server","vsingara","Armoor02*".toCharArray());
		/*//Session session = localSession;
		try {
			String nodePath = "/content/en/us/products/routers/cl-testing/t3" ;
			//String conceptPath = "/etc/tags/Products/Cisco Products/Routers/test-series-1610/test-series-1610-3";  //Products:Cisco Products/Switches/Campus LAN Switches - Compact/Cisco Catalyst 3560-C Series Switches
			String[] tagValues = {"DocTypes:HOME/Products and Services Area Root/Products Category Home/Products Series Home","Products:Cisco Products/Switches/Campus LAN Switches - Core and Distribution/Cisco Catalyst 6500 Series Switches1"}; 
			//conceptPath = "/content/dam/en/us/td/docs/dev/santhi/6.fm/jcr:content/metadata";
			//String path = "/etc/tags/NetworkingSolutions/Mktg1.1 Solution Core" ;
			
			try{
			Node node = session.getNode(nodePath);
			setValues("cq:tags", node, tagValues);
			setValues("jcr:description", node, "test desc");
			System.out.println("got the node and the node name is : "+node.getPath());
			}catch(Exception e){
				System.out.println("exception is : "+e.getLocalizedMessage());
			}
			
			
			
			
			
			//Creating the nodes,adding the tags 
			Node node = session.getNode(nodePath);
			
			for(int i=0;i<1000;i++){
			Node cNode = node.addNode("t"+i,"cq:Page");
			cNode = cNode.addNode("jcr:content","cq:PageContent");
			setValues("cq:tags", cNode, tagValues);
			setValues("jcr:title", cNode, "t"+i);
			setValues("jcr:description", cNode, "test desc"+i);
			
			}
			
			
			
			NodeIterator nI = node.getNodes();
			int i = 0;
			while(nI.hasNext()){
				Node tempNode = nI.nextNode();
				if(!tempNode.getName().equalsIgnoreCase("jcr:content")){
				tempNode = session.getNode(tempNode.getPath()+"/jcr:content");
				System.out.println("node paths are : "+tempNode.getPath());
				//setValues("cq:tags", tempNode, tagValues);
				setValues("jcr:title", tempNode, "t"+i);
				i++;
				}
			}
			
			
			//setting the lifeCycle 
			//setValues("lifeCycle", session.getNode(conceptPath), "obsolete");
			//getValue("metaClass", session.getNode(conceptPath));
			//getValue("cq:tags", session.getNode(nodePath));
			//setting the tags for the page
			//setValues("cq:tags", session.getNode(nodePath),tagValues);setValues("jcr:description", session.getNode(nodePath), "test desc");
			VersionManager versionManager = session.getWorkspace()
			.getVersionManager();
			
			8VersionHistory vh = versionManager.getVersionHistory(nodePath);
			Version v1 = versionManager.getBaseVersion(nodePath);
			System.out.println("v1 path is :"+v1.getPath());
			v1.setProperty("testProperty", "testValue");
			System.out.println("vh is :"+vh);
			
			//v1.getFrozenNode().setProperty("cq:tags", "test'");
			//v1.save();
			
			//VersionIterator vI = vh.getAllVersions();
			//System.out.println("vI is :"+vI);
			
			*//**
			  while (vI.hasNext()) {
			 
				Version object = (Version) vI.next();
				System.out.println("vi "+object.getPath());
			}
			**//*
			
			String fmNodePath = "/content/en/us/products/switches/catalyst-2960-series-switches/at-a-glance-listing";
			Node fmNode = session.getNode(fmNodePath);
			fmNode = fmNode.getNode("jcr:content");
			if(fmNode.hasNode("metadata"))
				fmNode = fmNode.getNode("metadata");
			// CONCEPT TAG USING
			//String conceptTag = "/etc/tags/DocTypes/HOME/Products and Services Area Root/Products Category Home/Products Series Home/Products Literature/Products At-a-Glance List";
			// LIFE CYCLE TO BE SET
			//String lifeCycle = "active";
			// PAGE BEING USED
			//String pageJcrPath = "/content/dam/en/us/products/collateral/QA/Sprint-2/NizamQA/new-folder/third-test/test3.docx/jcr:content/metadata";
			// TAGS BEING ATTACHED
			//String[] tags = {"DocTypes:HOME/Products and Services Area Root/Products Warranty Mappings List/Products Warranty Mapping","Products:Cisco Products/Routers/Branch Routers/Cisco 18000 Series Space Routers/Cisco 1803 Integrated Services Router","Products:Cisco Products/Routers/Branch Routers/Cisco 1400 Series Routers/Cisco 1417 Router"};
			//Node conceptNode = session.getNode(conceptTag);
			//Node pageJCRNode = session.getNode(pageJcrPath);
			// SETTING THE PROPERTY
			//setValues("lifeCycle",conceptNode, lifeCycle);
			//setValues("cq:tags",pageJCRNode,tags);
			//setValues("dc:description",pageJCRNode,"test"); //getValue("eolDocType",
				 * conceptNode); //getValue("cq:tags", pageJCRNode); //session.save();
				 * }catch(Exception e){ System.out.println("caught exception :"+e.getMessage());
				 * }
				 */
	}

	public static void setValues(String attr, Node node, String[] value) throws ValueFormatException, VersionException,
			LockException, ConstraintViolationException, RepositoryException {
		node.setProperty(attr, value);
		System.out.println("setting the " + attr);
	}

	public static void setValues(String attr, Node node, String value) throws ValueFormatException, VersionException,
			LockException, ConstraintViolationException, RepositoryException {
		node.setProperty(attr, value);
		System.out.println("setting the " + attr + " as :" + value);
	}

	public static void getValue(String attr, Node node) throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property prop = node.getProperty(attr);
		if (prop.isMultiple()) {
			Value[] currentValues = prop.getValues();
			for (int i = 0; i < currentValues.length; i++) {
				System.out.println("tag " + i + "is :" + currentValues[i].getString());
			}
		} else {
			String property = prop.getValue().getString();
			System.out.println(attr + " is :" + property);
		}
	}

	public static Session getSession(String repoUrl, String userName, char[] password) {
		Session session = null;
		try {
			System.out.println("============== started ==============");
			Repository repository = JcrUtils.getRepository(repoUrl);
			System.out.println("logging in");
			session = repository.login(new SimpleCredentials(userName, password), "crx.default");
		} catch (RepositoryException e) {
			System.out.println("Repositroy Exception :" + e.getMessage());
		}
		return session;

	}

	public static ReverseListIterator getReversalArrayList(VersionIterator vIterator) {
		ArrayList<Version> list = new ArrayList<Version>();
		while (vIterator.hasNext()) {
			Version version = vIterator.nextVersion();
			list.add(version);

		}
		ReverseListIterator reverseListIterator = new ReverseListIterator(list);
		return reverseListIterator;
	}

}

/*
 * try { System.out.println("before checking in");
 * System.out.println("is checked out ?? ---> "+versionManager.isCheckedOut(
 * taggedPage)); newVersion = versionManager.checkin(taggedPage);
 * System.out.println("after checking in"); if (newVersion != null)
 * versionManager.checkout(taggedPage); } catch (javax.jcr.RepositoryException
 * re) { System.out.
 * printf("ConceptListenerUtil - addNewVersion | Repository Exception caught for node while creating a new version: {}\nError: {}"
 * , newVersion, re.toString()); throw re; }
 */
/*
 * String repoUrl = "http://localhost:4502/crx/server"; String userName =
 * "admin"; char[] password = "admin".toCharArray(); String workspace =
 * "crx.default";
 */