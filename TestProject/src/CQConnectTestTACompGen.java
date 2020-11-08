import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CQConnectTestTACompGen {

	public static void main(String[] args) throws ParseException, IOException, PathNotFoundException,
			RepositoryException, org.json.simple.parser.ParseException {
		try {
			String compName = "indicators";
			Session session = getSession("http://192.168.0.5:4502/crx/server", "admin", "admin".toCharArray());
			Node atomsNode = session.getNode("/apps/abbott-platform/components/autoComponents/atoms");
			Node compNode;
			if (atomsNode.hasNode(compName)) {
				atomsNode.getNode(compName).remove();
				session.save();
				compNode = atomsNode.addNode(compName, "sling:Folder").addNode("v1", "nt:unstructured")
						.addNode(compName, "cq:Component");
				session.save();
				setValues("componentGroup", compNode, "Abbott Globals - Content");
				Node tempNode = createNode("cq:dialog", compNode);
				setValues("jcr:title", tempNode, compName);
				setValues("sling:resourceType", tempNode, "cq/gui/components/authoring/dialog");
				createDialogNode(tempNode);
				session.save();
			}
		} catch (Exception e) {
			System.err.println("Exception is : " + e);
		}
	}

	private static void createDialogNode(Node tempNode)
			throws ValueFormatException, VersionException, LockException, ConstraintViolationException,
			RepositoryException, FileNotFoundException, IOException, org.json.simple.parser.ParseException {
		tempNode = createNode("content", tempNode);
		setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/container");
		tempNode = createNode("items", tempNode);
		tempNode = createNode("tabs", tempNode);
		setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/tabs");
		tempNode = createNode("items", tempNode);
		// Tab node here
		tempNode = createNode("text", tempNode);
		setValues("jcr:title", tempNode, "Text");
		setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/container");
		tempNode = createNode("items", tempNode);
		tempNode = createNode("columns", tempNode);
		setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/fixedcolumns");
		tempNode = createNode("items", tempNode);
		tempNode = createNode("column", tempNode);
		setValues("sling:resourceType", tempNode, "granite/ui/components/coral/foundation/container");
		tempNode = createNode("items", tempNode);
		createDialogProps(tempNode);

	}

	private static void createDialogProps(Node tempNode)
			throws FileNotFoundException, IOException, org.json.simple.parser.ParseException, ValueFormatException,
			VersionException, LockException, ConstraintViolationException, RepositoryException {
		Object obj = new JSONParser().parse(new FileReader("C:\\Users\\Santosh\\Desktop\\compGen\\compProps.json"));

		JSONObject jo = (JSONObject) obj;
		JSONArray ja = (JSONArray) jo.get("properties");
		Iterator itr2 = ja.iterator();
		while (itr2.hasNext()) {
			Iterator<Map.Entry> itr1 = ((Map) itr2.next()).entrySet().iterator();
			Node propNode = null;
			while (itr1.hasNext()) {
				Map.Entry pair = itr1.next();
				if (pair.getKey().toString().equalsIgnoreCase("nodeName")) {
					propNode = createNode((String) pair.getValue(), tempNode);
				} else {
					System.out.println("propNode is : " + propNode.getPath());
					if ((pair.getKey()).toString().contains("options")) {
						Node itemsNode = createNode("items", propNode);
						Node temp1 = null;
						Iterator itr3 = ((JSONArray) pair.getValue()).iterator();
						while (itr3.hasNext()) {
							Iterator<Map.Entry> itr4 = ((Map) itr3.next()).entrySet().iterator();
							while (itr4.hasNext()) {
								Map.Entry pair1 = itr4.next();
								System.out.println(" pair1.getKey() is " + pair1.getKey());
								if (pair1.getKey().toString().equalsIgnoreCase("nodeName")) {
									temp1 = createNode((String) pair1.getValue(), itemsNode);
								} else {
									setValues((String) pair1.getKey(), temp1, (String) pair1.getValue());
								}
							}
						}
					} else {
						setValues((String) pair.getKey(), propNode, (String) pair.getValue());
					}
				}
				System.out.println("Setting the prop : " + pair.getKey());
			}
		}
	}

	public static Node createNode(String attr, Node node) throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		node = node.addNode(attr, "nt:unstructured");
		node.getSession().save();
		System.out.println("Creating the node11 " + attr);
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
		node.getSession().save();
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
