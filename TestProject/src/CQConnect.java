import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionIterator;

import org.apache.commons.collections.iterators.ReverseListIterator;
import org.apache.jackrabbit.commons.JcrUtils;

public class CQConnect {
	public static int count = 0;
	static PrintWriter writer;

	public static void main(String[] args) throws ParseException, FileNotFoundException, UnsupportedEncodingException {
		writer = new PrintWriter("C:\\Users\\sa333486\\Desktop\\ATCO_Core\\js.txt", "UTF-8");
		try {
			Session session = getSession("http://localhost:4502/crx/server", "admin", "admin".toCharArray());
			Node i18Node = session.getNode("/apps/atco/i18n/fr");
			final File folder = new File(
					"D:\\Builds\\13 Oct\\development\\atco\\ui.apps\\src\\main\\content\\jcr_root\\apps\\atco\\components");
			listFilesForFolder(folder, i18Node);
			writer.println(
					"==========================================================COMPLETED==================================================================================================");
			writer.close();
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void setValues(String attr, Node node, String[] value) throws ValueFormatException, VersionException,
			LockException, ConstraintViolationException, RepositoryException {
		node.setProperty(attr, value);
		writer.println("setting the " + attr);
	}

	public static void setValues(String attr, Node node, String value) throws ValueFormatException, VersionException,
			LockException, ConstraintViolationException, RepositoryException {
		node.setProperty(attr, value);
		writer.println("setting the " + attr + " as :" + value);
	}

	public static void getValue(String attr, Node node) throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		Property prop = node.getProperty(attr);
		if (prop.isMultiple()) {
			Value[] currentValues = prop.getValues();
			for (int i = 0; i < currentValues.length; i++) {
				writer.println("tag " + i + "is :" + currentValues[i].getString());
			}
		} else {
			String property = prop.getValue().getString();
			writer.println(attr + " is :" + property);
		}
	}

	public static Session getSession(String repoUrl, String userName, char[] password) {
		Session session = null;
		try {
			writer.println("The first line");
			writer.println("============== started ==============");
			Repository repository = JcrUtils.getRepository(repoUrl);
			writer.println("logging in");
			session = repository.login(new SimpleCredentials(userName, password), "crx.default");
		} catch (RepositoryException e) {
			writer.println("Repositroy Exception :" + e.getMessage());
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

	public static void listFilesForFolder(final File folder, Node i18Node)
			throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, i18Node);
			} else {
				if (fileEntry.getName().endsWith(".js")) {
					getRequiredText(fileEntry.getPath(), i18Node);
				}
			}
		}
	}

	public static void getRequiredText(String file, Node i18Node)
			throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		int lineNo = 0;
		boolean flag = false;
		String componentName = file.substring(file.lastIndexOf("\\") + 1, file.length() - 5);
		writer.println("file is : " + file);
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
				String s = line;
				if (s != null) {
					if (s.length() > s.indexOf(">") + 1) {
						s = s.substring(s.indexOf(">") + 1);
						if (s.indexOf("<") > 0) {
							s = s.substring(0, s.indexOf("<"));
							String text = s.trim();
							if ((!text.contains("$")) && !text.equalsIgnoreCase("") && !text.startsWith("for(")
									&& !text.startsWith("if(") && !text.startsWith("for (")
									&& !text.startsWith("objbuilder")) {
								writer.println(text);
								//createNodes(text, i18Node, componentName);
								flag = true;
								text = "${'" + text + "' @i18n} ";
								writer.println("lineNo is : " + lineNo);
								writer.println(text + "\n");
							}
						}
					}
				}
				lineNo++;
			}
			if (flag) {
				count++;
				writer.println(
						"============================================================================================================================================================");
				writer.println(count + ")" + file);
				writer.println(
						"============================================================================================================================================================");
			}
			fileReader.close();
		} catch (Exception e) {
			writer.println("error caught");
		}
	}

	public static void createNodes(String text, Node i18Node, String componentName) throws AccessDeniedException,
			ItemExistsException, ReferentialIntegrityException, ConstraintViolationException, InvalidItemStateException,
			VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
		String nodeName = text;
		nodeName = nodeName.replace(":", "");
		nodeName = nodeName.replace("-", ".");
		nodeName = nodeName.replace(" / ", ".");
		nodeName = nodeName.replace(" ", ".");
		nodeName = nodeName.replace("#", "");
		nodeName = nodeName.replace(",", "");
		nodeName = nodeName.replace("?", "");
		nodeName = nodeName.toLowerCase();
		writer.println(nodeName);
		nodeName = componentName + "." + nodeName;
		if (!i18Node.hasNode(nodeName)) {
			Node tempNode = null;
			try {
				tempNode = i18Node.addNode(nodeName, "sling:MessageEntry");
				tempNode.setProperty("sling:key", text);
				tempNode.setProperty("sling:message", text);
			} catch (Exception e) {
				writer.println("exception : " + e.getMessage());
			}
			tempNode.getSession().save();
		}

	}

}
