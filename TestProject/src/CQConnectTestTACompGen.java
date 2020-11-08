import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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

	public static ArrayList<String> propsArray = new ArrayList<String>();
	public static String componentName = "indicators";

	public static void main(String[] args) throws ParseException, IOException, PathNotFoundException,
			RepositoryException, org.json.simple.parser.ParseException {
		try {

			Session session = getSession("http://192.168.0.5:4502/crx/server", "admin", "admin".toCharArray());
			Node atomsNode = session.getNode("/apps/abbott-platform/components/autoComponents/atoms");
			Node compNode;
			if (atomsNode.hasNode(componentName)) {
				atomsNode.getNode(componentName).remove();
				session.save();
				compNode = atomsNode.addNode(componentName, "sling:Folder").addNode("v1", "nt:unstructured")
						.addNode(componentName, "cq:Component");
				session.save();
				setValues("jcr:title", compNode, getComponentTitle());
				setValues("componentGroup", compNode, "Abbott Globals - Content");
				Node tempNode = createNode("cq:dialog", compNode);
				setValues("jcr:title", tempNode, getComponentTitle());
				setValues("sling:resourceType", tempNode, "cq/gui/components/authoring/dialog");
				createDialogNode(tempNode);
				createImplClass();
				createImplTestClass();
				session.save();
			}
		} catch (Exception e) {
			System.err.println("Exception is : " + e);
		}
	}

	private static void createImplTestClass() {
		BufferedReader br = null;
		String valGenString = " 	@ValueMapValue\r\n	@Setter(AccessLevel.NONE)\r\n	private String ";
		String semiColonStr = "; \r\n\n";
		String interfaceClass = getComponentTitle();
		String newClass = interfaceClass + "ImplTest.java";
		String valGenerator = "";
		for (int i = 0; i < propsArray.size(); i++) {
			valGenerator = valGenerator + valGenString + propsArray.get(i) + semiColonStr;
		}
		try {
			String sCurrentLine;
			br = new BufferedReader(
					new FileReader("C:\\Users\\Santosh\\Desktop\\compGen\\javaAuto\\ipFiles\\SampleTest.java"));
			FileWriter myWriter = new FileWriter(
					"C:\\Users\\Santosh\\Desktop\\compGen\\javaAuto\\opFiles\\" + newClass);
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("Sample")) {
					sCurrentLine = sCurrentLine.replace("Sample", getComponentTitle());
				}
				if (sCurrentLine.contains("propertiesPlaceHolder")) {
					System.out.println("adding the props");
					sCurrentLine = sCurrentLine.replace("propertiesPlaceHolder", valGenerator);
				}
				myWriter.write(sCurrentLine + "\n");
			}
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	private static void createImplClass() {
		BufferedReader br = null;
		String valGenString = " 	@ValueMapValue\r\n	@Setter(AccessLevel.NONE)\r\n	private String ";
		String semiColonStr = "; \r\n\n";
		String interfaceClass = getComponentTitle();
		String compType = "molecules";
		String newClass = interfaceClass + "Impl.java";
		String valGenerator = "";
		for (int i = 0; i < propsArray.size(); i++) {
			valGenerator = valGenerator + valGenString + propsArray.get(i) + semiColonStr;
		}
		try {
			String sCurrentLine;
			br = new BufferedReader(
					new FileReader("C:\\Users\\Santosh\\Desktop\\compGen\\javaAuto\\ipFiles\\SampleImpl.java"));
			FileWriter myWriter = new FileWriter(
					"C:\\Users\\Santosh\\Desktop\\compGen\\javaAuto\\opFiles\\" + newClass);
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("Sample")) {
					sCurrentLine = sCurrentLine.replace("Sample", interfaceClass);
				}
				if (sCurrentLine.contains("ComponentFullPath")) {
					sCurrentLine = sCurrentLine.replace("ComponentFullPath", "abbott-platform/components/content/"
							+ compType + "/" + componentName + "/v1/" + componentName);
				}
				if (sCurrentLine.contains("propertiesPlaceHolder")) {
					System.out.println("adding the props");
					sCurrentLine = sCurrentLine.replace("propertiesPlaceHolder", valGenerator);
				}
				myWriter.write(sCurrentLine + "\n");
			}
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static String getComponentTitle() {
		String compTitle = null;
		String firstLetter = componentName.substring(0, 1);
		String remainingLetters = componentName.substring(1, componentName.length());
		firstLetter = firstLetter.toUpperCase();
		compTitle = firstLetter + remainingLetters;
		return compTitle;
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
		Object obj = new JSONParser()
				.parse(new FileReader("C:\\Users\\Santosh\\Desktop\\compGen\\aemAuto\\ipFiles\\compProps.json"));
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
								System.out.println("pair1.getKey() is " + pair1.getKey());
								if (pair1.getKey().toString().equalsIgnoreCase("nodeName")) {
									temp1 = createNode((String) pair1.getValue(), itemsNode);
								} else {
									setValues((String) pair1.getKey(), temp1, (String) pair1.getValue());
								}
							}
						}
					} else {
						if (pair.getKey().toString().equalsIgnoreCase("name")) {
							propsArray.add(pair.getValue().toString().replace("./", ""));
						}
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
		node.getSession().save();
		System.out.println("setting the " + attr + " as :" + value);
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
}
