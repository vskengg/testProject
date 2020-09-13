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

public class CreatePageForActivation_Local {

	public static void main(String[] args) throws ParseException {
		Session localSession = getSession("http://localhost:4502/crx/server",
				"admin", "admin".toCharArray());
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
			String nodePath = "/content/en/us/products/routers/test-2510/test-series-2510-4/test-3011/"; 
			///content/en/us/products/routers/test-2510/test-series-2510-4/test-3011
			//"Technologies:Broadband Cable/Cable Modems"
			Node docNode = getPrimaryConceptNode("DocTypes:HOME/Products and Services Area Root/Products Category Home/Products Series Home",session);
			Node pageNode = null; 
			Node parentPageNode = null; 
			String changeTo = "obsolete";
			String parentPage = "test-"+changeTo+"-multi-5";
			
			try {
				int a = 1; 
				Node node = session.getNode(nodePath);
				parentPageNode = node.addNode(parentPage,"cq:Page");
				Node parentPageJCRNode = parentPageNode.addNode("jcr:content","cq:PageContent");
				setValues("jcr:title", parentPageJCRNode, parentPage);
				for(int i=0 ; i < 15; i++){
				String title = "t1-ser-0612-"+i ;
				System.out.println("Creating the page "+title);
				if(a == 1){
				String[] tagValues = {
							"DocTypes:HOME/Products and Services Area Root/Products Category Home/Products Series Home",
							prTag,secTag};
				priNode = getPrimaryConceptNode(prTag,session);
				pageNode = parentPageNode.addNode(title,"cq:Page");
				System.out.println("pageNode path is : "+pageNode.getPath());
				Node pageJCRNode = pageNode.addNode("jcr:content","cq:PageContent");
				node = pageJCRNode ;
				setValues("cq:template",node,"/apps/cdc/framework/templates/default/series_products");
				setValues("sling:resourceType",node,"cdc/framework/components/page/default_variations/series_products");
				setValues("jcr:title", node, title);
				setValues("jcr:description", node, "test desc");
				setValues("cq:tags", node, tagValues);
				if(i == 0)
				setValues("lifeCycle",priNode,"active");
				session.save();
				//activate page (this part works only on local) 
				activatePage(pageNode.getPath());
				Thread.sleep(1000);
				setValues("jcr:description", node, "test desc - curent copy");session.save() ;
				}
				System.out.println("Done Processing for  "+title);
				System.out.println("=============================================================================================");
				}
				priNode = getPrimaryConceptNode(prTag,session);setValues("lifeCycle",priNode,changeTo);
				session.save();
				
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
