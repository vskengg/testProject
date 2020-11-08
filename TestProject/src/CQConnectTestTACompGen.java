import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

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

public class CQConnectTestTACompGen {

	public static void main(String[] args)
			throws ParseException, IOException, PathNotFoundException, RepositoryException {

		String compName = "indicators";
		Session session = getSession("http://localhost:4502/crx/server", "admin", "admin".toCharArray());
		Node atomsNode = session.getNode("/apps/abbott-platform/components/autoComponents/atoms");
		Node compNode;
		if (!atomsNode.hasNode(compName)) {
			compNode = atomsNode.addNode(compName, "sling:Folder").addNode("v1", "nt:unstructured").addNode(compName,
					"cq:Component");
			session.save();
			System.out.println("Node created for the component " + compNode.getPath());
		} else {
			compNode = atomsNode.getNode(compName + "/v1/" + compName);
			System.out.println("Component found : " + compNode.getPath());
			setValues("componentGroup", compNode, "Abbott Globals - Content");
			session.save();
			Node tempNode = compNode.addNode("cq:dialog", "nt:unstructured");
			session.save();
			setValues("jcr:title", tempNode, compName);
			setValues("sling:resourceType", tempNode, "cq/gui/components/authoring/dialog");
			tempNode = tempNode.addNode("content", "nt:unstructured");
			session.save();
			setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/container");
			tempNode = createNode("items", tempNode, "nt:unstructured");
			tempNode = createNode("tabs", tempNode, "nt:unstructured");
			setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/tabs");
			tempNode = createNode("items", tempNode, "nt:unstructured");
			tempNode = createNode("text", tempNode, "nt:unstructured");
			setValues("jcr:title", tempNode, "Text1");
			setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/container");
			tempNode = createNode("items", tempNode, "nt:unstructured");
			tempNode = createNode("columns", tempNode, "nt:unstructured");
			setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/fixedcolumns");
			tempNode = createNode("items", tempNode, "nt:unstructured");
			tempNode = createNode("column", tempNode, "nt:unstructured");
			setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/container");
			tempNode = createNode("items", tempNode, "nt:unstructured");
			tempNode = createNode("pretitle", tempNode, "nt:unstructured");
			setValues("name", tempNode, "./pretitle");
			setValues("fieldLabel", tempNode, "PreTitle1");
			setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/form/textfield");
			setValues("fieldDescription", tempNode, "Pre Title Text");

		}

	}

	public static Node createNode(String attr, Node node, String value) throws ValueFormatException, VersionException,
			LockException, ConstraintViolationException, RepositoryException {
		node = node.addNode(attr, value);
		node.getSession().save();
		System.out.println("Creating the node " + attr);
		return node;

	}

	public static void setValues(String attr, Node node, String[] value) throws ValueFormatException, VersionException,
			LockException, ConstraintViolationException, RepositoryException {
		node.setProperty(attr, value);
		node.getSession().save();
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
