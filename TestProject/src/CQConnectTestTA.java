import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

public class CQConnectTestTA {

	public static void main(String[] args)
			throws ParseException, IOException, PathNotFoundException, RepositoryException {

		Session session = getSession("http://localhost:4502/crx/server", "admin", "admin".toCharArray());
		// findComponents(session);
		String contentComponentsLocation = "/apps/adc/fds/components/content";
		String atomComponentsLocation = contentComponentsLocation + "/atoms";
		String moleculesComponentsLocation = contentComponentsLocation + "/molecules";
		String organismsComponentsLocation = contentComponentsLocation + "/organisms";
		listComponentsAndNames(atomComponentsLocation, session);
		listComponentsAndNames(moleculesComponentsLocation, session);
		listComponentsAndNames(organismsComponentsLocation, session);
		/*
		 * listComponents(atomComponentsLocation, session);
		 * listComponents(moleculesComponentsLocation, session);
		 * listComponents(organismsComponentsLocation, session);
		 */
	}

	private static void listComponentsAndNames(String componentsLocation, Session session)
			throws PathNotFoundException, RepositoryException {
		Node compNode = session.getNode(componentsLocation);
		NodeIterator nI = compNode.getNodes();
		Node currentNode;
		try {
			while (nI.hasNext()) {
				currentNode = nI.nextNode();
				System.out.println(currentNode.getPath() + " super type is : "
						+ currentNode.getProperty("jcr:title").getValue().getString());
			}
		} catch (Exception e) {
			System.err.println("Exception here ");
		}
	}

	private static void listComponents(String componentsLocation, Session session)
			throws PathNotFoundException, RepositoryException {
		Node compNode = session.getNode(componentsLocation);
		NodeIterator nI = compNode.getNodes();
		while (nI.hasNext()) {
			Node currentNode = nI.nextNode();
			if (currentNode.hasProperty("sling:resourceSuperType")) {
				System.out.println(currentNode.getPath() + " super type is : "
						+ currentNode.getProperty("sling:resourceSuperType").getValue().getString());
			}

		}
	}

	public static void findComponents(Session session) throws IllegalStateException, IOException {

		File file = new File("C:\\Users\\santosh.vadakattu\\Desktop\\Temp\\components-list.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String componentsLocation = "/apps/adc/fds/components/content/";

		try {
			String st;
			int counter = 0;
			while ((st = br.readLine()) != null) {
				st = st.toLowerCase().replaceAll("\\s", "").replace("component", "");
				Node n1 = null;

				if (session.nodeExists(componentsLocation + "atoms/" + st)) {
					n1 = session.getNode(componentsLocation + "atoms/" + st);
				} else if (session.nodeExists(componentsLocation + "molecules/" + st)) {
					n1 = session.getNode(componentsLocation + "molecules/" + st);
				} else if (session.nodeExists(componentsLocation + "organisms/" + st)) {
					n1 = session.getNode(componentsLocation + "organisms/" + st);
				}
				if (n1 != null) {
					counter++;
					System.out.println("component " + counter + " exist at : " + n1.getPath());
					if (n1.hasProperty("sling:resourceSuperType"))
						System.out.println(
								"super type is : " + n1.getProperty("sling:resourceSuperType").getValue().getString());
				} else {

				}

			}
		} catch (PathNotFoundException e) {
			System.err.println("Exception : " + e);
		} catch (RepositoryException e) {
			System.err.println("Exception : " + e);
		}
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