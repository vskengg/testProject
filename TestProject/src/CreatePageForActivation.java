import java.text.ParseException;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.commons.JcrUtils;


public class CreatePageForActivation {

	public static void main(String[] args) throws ParseException {
		//http://localhost:4502
		//wemapp-author-dev3-01:4502/siteadmin#/content/en/us/td/docs/dev/test_lc
		Session localSession = getSession("http://wemapp-author-dev3-02:4502/crx/server",
				"vsingara", "Armoor02*".toCharArray());
		Session session = localSession;
		Node priNode = null ;
		String prTag ="Products:Cisco Products/Routers/Branch Routers/Cisco 5700 Series Routers/Cisco 5750 Integrated Encryption Router";// "Products:Cisco Products/Routers/Branch Routers/test-series-0412-9" ;//"Technologies:Broadband Cable/Cable Modems" ;//"NetworkingSolutions:Industry Solutions/Consumer Packaged Goods";
		String secTag = "TechnicalSupport:General Documentation/Internetworking Documentation" ; //"Products:Cisco Products/Cisco IOS and NX-OS Software/Cisco IOS Technologies" ;
				//Products:Cisco Products/Routers/Branch Routers/Cisco 5700 Series Routers/Cisco 5750 Integrated Encryption Router
				//"Products:Cisco Products/Cisco IOS and NX-OS Software/Cisco IOS Technologies";
				// "Technologies:Broadband Cable/Cable Modems" ;
				//"TechnicalSupport:General Documentation/test-tag-eol" ;
				//NetworkingSolutions/Industry Solutions/Consumer Packaged Goods
				//"TechnicalSupport:General Documentation/Internetworking Documentation" ;
				//"TechnicalSupport:General Documentation/test-tag-eol" ;
		try {
			String nodePath = "/content/en/us/td/docs/dev/test_lc/test-0112/t1"; 
			///content/en/us/products/routers/test-2510/test-series-2510-4/test-3011
			//"Technologies:Broadband Cable/Cable Modems"
			Node docNode = getPrimaryConceptNode("DocTypes:HOME/Products and Services Area Root/Products Category Home/Products Series Home",session);
			try {
				int a = 1; 
				Node pageNode = null; 
				String title = "t1-ser-0612-1" ;
				if(a == 1){
				Node node = session.getNode(nodePath);
				String[] tagValues = {
							"DocTypes:HOME/Products and Services Area Root/Products Category Home/Products Series Home",
							prTag};
				priNode = getPrimaryConceptNode(prTag,session);
				Node secNode = getPrimaryConceptNode(secTag,session);
				pageNode = node.addNode(title,"cq:Page");
				Node pageJCRNode = pageNode.addNode("jcr:content","cq:PageContent");
				node = pageJCRNode ;
				setValues("cq:template",node,"/apps/cdc/framework/templates/default/series_products");
				setValues("sling:resourceType",node,"cdc/framework/components/page/default_variations/series_products");
				setValues("jcr:title", node, title);
				setValues("jcr:description", node, "test desc");
				setValues("cq:tags", node, tagValues);
				setValues("metaClass",priNode,"Series");
				setValues("metaClass",docNode,"Series");
				setValues("lifeCycle",priNode,"active");
				setValues("lifeCycle",secNode,"active");
				}
				
				if(a == 2){
					/*String assetNode =  "/content/dam/en/us/td/docs/dev/02DecConceptLCTesting/TechnicalSupport/TechnicalSupportMultiple/TestNew/Test1/Book.book/jcr:content/metadata";
					String secTag1 = "NetworkingSolutions:Consumer";
					String[] tagValues1 = {
							"DocTypes:HOME/Networking Solutions Area Root/Networking Solutions Index Segment Home/Networking Solutions Index Category Home/Networking Solutions Design Guides List/Networking Solutions Design Guide Book",
							prTag,secTag1};
					System.out.println(" a is : "+a);
					//getValue("cq:tags", session.getNode(assetNode));
					System.out.println("====="+session.getNode("/content/dam/en/us/td/docs/dev/02DecConceptLCTesting/TechnicalSupport/TechnicalSupportMultiple/TestNew/Test1/Book.book").getPath());
					setValues("cq:tags", session.getNode(assetNode), tagValues1);*/
					//setValues("metaClass",docNode,"Series");
				    //pageNode = session.getNode(nodePath+"/"+title+"/jcr:content");setValues("jcr:description", pageNode, "test desc - current copy ");
					priNode = getPrimaryConceptNode(prTag,session);setValues("lifeCycle",priNode,"obsolete");
					//setValues("metaClass",priNode,"EOT");
				} 
				session.save();
				
				//UrlHit.urlHitting("http://wemapp-author-dev3-02:4502"+pageNode.getPath()+".infinity.json");
				/*//activate page (this part works only on local)
				activatePage(title);
				Thread.sleep(2000);
				setValues("jcr:description", node, "test desc - modified current copy");
				session.save();*/
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			System.out.println("caught exception :" + e.getMessage());
		}
	}
	
	public static void activatePage(String title){
		
		String urlToHit = "http://localhost:4502/content/repTest.html?pageName=";
		urlToHit = urlToHit + title ; 
		System.out.println("urlToHit is :"+urlToHit);
		UrlHit.urlHitting(urlToHit);
	}
	public static Node getPrimaryConceptNode(String prTag,Session session)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		prTag = prTag.replace(":","/");
		prTag = "/etc/tags/"+prTag ;
		System.out.println("prTag is : "+prTag);
		Node node = session.getNode(prTag);
		return node ;
	}

	public static void setValues(String attr, Node node, String[] value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		if(null != node){
			System.out.println("setting the " + attr);
			node.setProperty(attr, value);
		}else
			System.out.println("null node is passed : "+node);
		
	}

	public static void setValues(String attr, Node node, String value)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		node.setProperty(attr, value);
		System.out.println("setting the " + attr + " as :" + value);
	}

	public static void getValue(String attr, Node node)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property prop = node.getProperty(attr);
		if (prop.isMultiple()) {
			Value[] currentValues = prop.getValues();
			for (int i = 0; i < currentValues.length; i++) {
				System.out.println("tag " + i + "is :"
						+ currentValues[i].getString());
			}
		} else {
			String property = prop.getValue().getString();
			System.out.println(attr + " is :" + property);
		}
	}

	public static Session getSession(String repoUrl, String userName,
			char[] password) {
		Session session = null;
		try {
			System.out.println("============== started ==============");
			Repository repository = JcrUtils.getRepository(repoUrl);
			System.out.println("logging in");
			session = repository.login(
					new SimpleCredentials(userName, password), "crx.default");
		} catch (RepositoryException e) {
			System.out.println("Repositroy Exception :" + e.getMessage());
		}
		return session;

	}


}
/**
/etc/tags/NetworkingSolutions/Small Business/Small Scale Business
/etc/tags/NetworkingSolutions/Mktg3 Service Provider
/etc/tags/Products/Cisco Training and Documentation/Cisco Documentation
/etc/tags/TechnicalSupport/General Documentation/Internetworking Documentation
/etc/tags/Technologies/Broadband Cable/Cable Modems
 */
