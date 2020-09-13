package com.cisco.standalone.conceptlc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.log4j.Logger;
import org.apache.sling.commons.json.JSONException;

public class TitleValidateProcessor extends TitleValidateConstants {

	private static Logger log = Logger.getLogger(TitleValidateProcessor.class);
	private static String HOST_NAME = "wemapp-author-prod1-01";
	private static String USER_ID = "wemmigadm.gen";

	public static void main(String[] args) {
		Session session = null;
		FileWriter writer = null;
		BufferedReader br = null;
		String status = null;
		ArrayList<String> statusInfo = new ArrayList<String>();

		try {

			session = getSession("http://wemapp-author-prod1-01:4502/crx/server", "wemmigadm.gen",
					"w3madm@gen".toCharArray());

			// session =
			// getSession("http://wemapp-author-dev3-01:4502/crx/server",
			// "vsingara", "Armoor02*".toCharArray());
			WebServiceClient webServiceClinet = new WebServiceClient();
			webServiceClinet.setHostName(HOST_NAME);
			webServiceClinet.setUserId(USER_ID);

			String fileName = "title_change_events_list_rem_400_prod_200to400.txt";//title_change_events_list_rem_400_prod_150to200,"title_change_events_list_rem_400_prod_100to150.txt", "title_change_events_list_rem_400_prod_1to100.txt";//"title_change_events_list_15to65_prod.txt";
			String folderPath = "C:\\Users\\sanvadak\\Desktop\\WIP\\LifeCycleEvents\\ConceptRename\\";
			String filePath = folderPath + fileName;
			writer = new FileWriter(folderPath + "report_" + fileName, true);
			String currentConceptId = null;
			ArrayList<String> conceptIds = new ArrayList<String>();
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			String sCurrentLine;
			// Scanner keys = new Scanner(System.in);
			// String response;
			br = new BufferedReader(new FileReader(filePath));
			while ((sCurrentLine = br.readLine()) != null) {
				conceptIds.add(sCurrentLine);
			}
			for (int i = 0; i < conceptIds.size(); i++) {
				try {
					String processedNodes = "";
					String skippeNodes = "";
					int processedNodesCount = 0;
					int skippedNodesCount = 0;
					status = "";
					status = status
							+ "=======================================================================================================================================";
					status = status + "\n";
					// System.out.println("Enter 'Y' to proceed: ");
					// response = keys.nextLine();
					// if (response.equalsIgnoreCase("Y")) {
					log.debug("Processing In Progress. Please wait...");
					log.debug("counter is :" + i);
					System.out.println("concepts counter is :" + i);
					currentConceptId = conceptIds.get(i);
					String queryForConcept = "/jcr:root/etc/tags//element(*,cq:Tag)[@conceptId='" + currentConceptId
							+ "'] order by @jcr:score";
					status = status + currentConceptId + " | ";
					log.debug("queryForConcept is : " + queryForConcept);
					Query query = queryManager.createQuery(queryForConcept, Query.XPATH);
					QueryResult result = query.execute();
					NodeIterator nodeIterator = result.getNodes();
					if (nodeIterator.getSize() == 1 && nodeIterator.hasNext()) {
						Node currentConceptNode = (Node) nodeIterator.next();
						status = status + currentConceptNode.getPath() + " |";
						String currentConceptPath = currentConceptNode.getPath();
						log.debug("=====================================================================================");
						log.debug("Processing started for the conecpt : " + currentConceptPath);
						System.out.println("Processing stated for the concept : " + currentConceptPath);
						String ETC_TAGS = "/etc/tags/";
						if (isValidConcept(currentConceptPath)) {
							currentConceptPath = currentConceptPath.substring(ETC_TAGS.length());
							currentConceptPath = currentConceptPath.replaceFirst("/", ":");
							log.debug("conceptPath is : " + currentConceptPath);
							String queryForPageContent = "/jcr:root/content/en/us//element(*,cq:Page)[ jcr:content/@cq:tags = '"
									+ currentConceptPath + "'] order by @jcr:score";
							log.debug("query for concept tagged pages is : " + queryForPageContent);
							Query query1 = queryManager.createQuery(queryForPageContent, Query.XPATH);
							QueryResult result1 = query1.execute();
							NodeIterator nodeIterator1 = result1.getNodes();
							int pagesCount = (int) nodeIterator1.getSize();
							log.debug("Total number of nodes : " + pagesCount);
							System.out.println("Total number of nodes : " + pagesCount);
							status = status + " | Total Nodes - " + pagesCount;
							int counter = 1;
							String conceptTitle = null;
							while (nodeIterator1.hasNext()) {
								try {
									log.debug("==================================================================================================");
									Node pageNode = (Node) nodeIterator1.next();
									log.debug("page counter :" + counter);
									System.out.println("page counter :" + counter + " Remaining pages : "
											+ (pagesCount - counter));
									counter++;
									log.debug("Processing : " + pageNode.getPath());
									if (isValidPageforTitleChange(session, pageNode, currentConceptPath)) {
										if (processedNodes.isEmpty())
											processedNodes = "" + pageNode.getPath();
										else
											processedNodes = processedNodes + pageNode.getPath();
										log.debug("It is valid for the title change");
										if (currentConceptNode.hasProperty("jcr:title"))
											conceptTitle = currentConceptNode.getProperty("jcr:title").getString();
										log.debug("conceptTitle is : " + conceptTitle);
										processedNodes = processedNodes + " | VALID FOR TITLE CHANGE";
										if (null != conceptTitle) {
											if (!hasValidTitle(session, pageNode.getPath(), conceptTitle)) {
												System.out
														.println("==================================================================================================");
												System.out.println("Not matched page found : " + pageNode.getPath());
												System.out
														.println("==================================================================================================");
												log.debug("==================================================================================================");
												log.debug("Not matched page found" + pageNode.getPath());
												log.debug("==================================================================================================");
												processedNodes = processedNodes + " | TITLE NOT MATCHED | UPDATE ";
												log.debug("It doesn't have the valid title so updating and replciating");
												processedNodes = updateAndReplicate(pageNode, conceptTitle,
														webServiceClinet, processedNodes);

											} else {
												log.debug("Title is atching so exiting ");
												processedNodes = processedNodes + " | TITLE MATCHED | COMPLETED";
											}
										} else {
											log.debug("concept doesn't have a title");
										}
										processedNodes = processedNodes + "\n";
										processedNodesCount++;
									} else {
										if (skippeNodes.isEmpty())
											skippeNodes = "" + pageNode.getPath();
										else
											skippeNodes = skippeNodes + pageNode.getPath();
										log.debug("this page is not valid for the title change");
										skippeNodes = skippeNodes + " | NOT VALID FOR TITLE CHANGE | COMPLETED";
										skippeNodes = skippeNodes + "\n";
										skippedNodesCount++;
									}
									log.debug("Processing completed for the content node : " + pageNode.getPath());
									log.debug("==================================================================================================");
								} catch (Exception e) {
									log.error("Exception caught while processing the asset nodes ", e);
									status = status + e.getMessage();
								}

							}
						} else {
							log.debug("Processing skipped as the concept is not a valid concept to process : "
									+ currentConceptPath);
						}
						log.debug("Processing completed for the concept : " + currentConceptPath);
						log.debug("==================================================================================================");

					} else {
						if (nodeIterator.getSize() > 1) {
							log.debug("exiting the process as the conceptID " + currentConceptId
									+ " is not unique and assigned to more than one concept");
							log.debug("exiting the process as the conceptID " + currentConceptId
									+ " is not unique and assigned to more than one concept");
							status = status + "DUPLICATE";
						}
						if (nodeIterator.getSize() < 1) {
							log.debug("exiting the process as the conceptID " + currentConceptId
									+ " is not having any concept");
							log.debug("exiting the process as the conceptID " + currentConceptId
									+ " is not having any concept");
							status = status + "UNAVAILABLE";
						}
					}
					/*
					 * } else { System.out.println("Quitting..!");
					 * log.debug("Quitting..!"); break; }
					 */
					statusInfo.add(status);
					statusInfo
							.add("=======================================================================================================================================");
					statusInfo.add("Processed Nodes : " + processedNodesCount);
					statusInfo
							.add("=======================================================================================================================================");
					statusInfo.add(processedNodes);
					statusInfo
							.add("=======================================================================================================================================");
					statusInfo.add("Skipped Nodes : " + skippedNodesCount);
					statusInfo
							.add("=======================================================================================================================================");
					statusInfo.add(skippeNodes);
					statusInfo
							.add("=======================================================================================================================================");
					statusInfo.add("\n");
				} catch (Exception e) {
					log.error("Exception caught while finding the concepts ", e);
					status = status + e.getMessage();
				}

			}
			log.debug("Completed processing for the file : " + fileName);
			System.out.println("Completed processing for the file : " + fileName);
		} catch (Exception e) {
			log.error("Exception caught ", e);
			status = status + e.getMessage();
		} finally {
			try {
				for (int j = 0; j < statusInfo.size(); j++) {
					writer.append(statusInfo.get(j));
					writer.append("\n");
				}
				if (br != null)
					br.close();
				if (session != null)
					session.logout();
				if (writer != null)
					writer.close();
			} catch (IOException ex) {
				log.error("Exception caught ", ex);
			} finally {
				try {
					if (br != null)
						br.close();
					if (session != null)
						session.logout();
					if (writer != null)
						writer.close();
				} catch (IOException ex) {
					log.error("Exception caught ", ex);
				}
			}
		}
	}

	public static Session getSession(String repoUrl, String userName, char[] password) throws RepositoryException {
		Session session = null;
		log.debug("============== started ==============");
		Repository repository = JcrUtils.getRepository(repoUrl);
		System.out.println("logging in to : " + repoUrl);
		log.debug("logging in to : " + repoUrl);
		session = repository.login(new SimpleCredentials(userName, password), "crx.default");
		return session;

	}

	public static boolean isValidConcept(String conceptPath) {
		boolean isValidConcept = false;
		String[] listOfRootPaths = { "/etc/tags/Products/", "/etc/tags/Technologies/", "/etc/tags/TechnicalSupport/",
				"/etc/tags/NetworkingSolutions/" };
		for (int j = 0; j < listOfRootPaths.length; j++) {
			if (null != conceptPath && conceptPath.contains(listOfRootPaths[j])) {
				isValidConcept = true;
				break;
			}
		}
		return isValidConcept;
	}

	public static String printStatusRelatedMessages(String replicationStatus, String reportMessage, String action) {
		log.debug("replicationStatus is : " + replicationStatus);
		if (null != replicationStatus && replicationStatus.equalsIgnoreCase("200")) {
			log.debug(action + " action successful..");
			reportMessage = reportMessage + " | SUCCESS";
		} else {
			log.debug(action + " action failed..");
			reportMessage = reportMessage + " | FAILED";
		}
		return reportMessage;
	}

	public static boolean isValidPageforTitleChange(Session session, Node pageNode, String conceptTagId)
			throws RepositoryException {
		String pageNodePath = pageNode.getPath();
		String namespace = getTaxonomyNameByFolderPath(pageNodePath);
		String doctype = getDoctype(pageNode.getNode(JcrConstants.JCR_CONTENT));
		if (null == doctype)
			return false;

		String doctypeMetaclass = getDoctypeMetaClass(session, doctype);
		if (null == doctypeMetaclass)
			return false;

		String primaryConcept = getPrimaryConcept(pageNode.getNode(JcrConstants.JCR_CONTENT));

		log.debug("namespace : " + namespace);
		log.debug("doctype : " + doctype);
		log.debug("doctypeMetaclass : " + doctypeMetaclass);
		log.debug("primaryConcept : " + primaryConcept);

		if (namespace != null && null != primaryConcept && (null != conceptTagId)
				&& (primaryConcept.equals(conceptTagId))) {
			log.debug("primary concept matched");
			if (namespace.equalsIgnoreCase(NAMESPACE_PRODUCTS)
					&& (doctypeMetaclass.equalsIgnoreCase(METACLASS_SERIES) || doctypeMetaclass
							.equalsIgnoreCase(METACLASS_MODEL))) {
				log.debug("Valid for title change...1!!");
				return true;
			} else if (namespace.equalsIgnoreCase(NAMESPACE_TECHNOLOGIES)
					&& (doctypeMetaclass.equalsIgnoreCase(METACLASS_TECHNOLOGY_CATEGORY) || doctypeMetaclass
							.equalsIgnoreCase(METACLASS_ADDITIONAL_TECHNOLOGIES))) {
				log.debug("Valid for title change...2!!");
				return true;
			} else if (namespace.equalsIgnoreCase(NAMESPACE_NETWORKING_SOLUTIONS)
					&& (doctypeMetaclass.equalsIgnoreCase(METACLASS_SOLUTION_FAMILY)
							|| doctypeMetaclass.equalsIgnoreCase(METACLASS_NETSOL_SOLUTION_CLASS)
							|| doctypeMetaclass.equalsIgnoreCase(METACLASS_NETSOL_SOLUTION_OFFERING)
							|| doctypeMetaclass.equalsIgnoreCase(METACLASS_NETSOL_CATEGORY1)
							|| doctypeMetaclass.equalsIgnoreCase(METACLASS_NETSOL_CATEGORY2)
							|| doctypeMetaclass.equalsIgnoreCase(METACLASS_NETSOL_SOL_TECH_SEGMENT) || doctypeMetaclass
								.equalsIgnoreCase(METACLASS_NETSOL_SOL_HORIZONTAL_SEGMENT))) {
				log.debug("Valid for title change...3!!");
				return true;
			}
		}
		return false;
	}

	private static String getTaxonomyNameByFolderPath(String folderPath) {
		String taxonomyName = null;
		if (folderPath.indexOf("/products/") != -1) {
			taxonomyName = "Products";
		} else if (folderPath.indexOf("/tech/") != -1) {
			taxonomyName = "Technologies";
		} else if (folderPath.indexOf("/solutions/") != -1) {
			taxonomyName = "NetworkingSolutions";
		}
		return taxonomyName;
	}

	private static String getDoctype(Node metadataNode) throws RepositoryException {
		if (null != metadataNode && metadataNode.hasProperty("cq:tags")) {
			Value[] vals = metadataNode.getProperty("cq:tags").getValues();

			for (int i = 0; i < vals.length; i++) {
				if (vals[i].getString().startsWith("DocTypes")) {
					return vals[i].getString();
				}
			}
		}
		return null;
	}

	public static boolean hasValidTitle(Session session, String resourcePath, String conceptTitle)
			throws RepositoryException {
		Node propertyNode = null;
		boolean hasValidTitle = false;

		if (null != resourcePath) {
			propertyNode = session.getNode(resourcePath);
			if (null != propertyNode) {
				propertyNode = propertyNode.getNode(JcrConstants.JCR_CONTENT);
			}
			if ((null != propertyNode) && (propertyNode.hasProperty("jcr:title"))) {
				Property titleProperty = propertyNode.getProperty("jcr:title");
				String title = null;
				if (titleProperty.isMultiple()) {
					Value[] values = titleProperty.getValues();
					for (Value value : values) {
						title = value.getString();
						break;
					}
				} else {
					Value value = titleProperty.getValue();
					title = value.getString();
				}
				log.debug("current title is : " + title);
				if (null != title && null != conceptTitle && title.equals(conceptTitle)) {
					hasValidTitle = true;
				}
			}
		}
		return hasValidTitle;

	}

	public static String getPrimaryConcept(Node metadataNode) throws RepositoryException {
		if (null != metadataNode && metadataNode.hasProperty("cq:tags")) {
			Value[] vals = metadataNode.getProperty("cq:tags").getValues();
			for (int i = 0; i < vals.length; i++) {
				if (!(vals[i].getString().startsWith("DocTypes"))
						&& !(vals[i].getString().startsWith("ProblemType") && !(vals[i].getString()
								.startsWith("ContentSpot")))) {
					return vals[i].getString();
				}
			}
		}
		return null;
	}

	private static String getDoctypeMetaClass(Session session, String doctype) throws PathNotFoundException,
			RepositoryException {
		log.debug("getting doctype metaclass of doctype : " + doctype);
		String docTypePath = getTagPath(doctype);
		Node docTypeNode = session.getNode(docTypePath);
		String metaClass = null;
		if (docTypeNode.hasProperty(PROPERTY_METACLASS)) {
			metaClass = docTypeNode.getProperty(PROPERTY_METACLASS).getString();
		}
		return metaClass;
	}

	public static String getTagPath(String tag) {
		String tagPath = null;
		if (null != tag) {
			String ETC_TAGS = "/etc/tags/";
			tag = tag.replaceFirst(":", "/");
			tagPath = ETC_TAGS + tag;
		}
		return tagPath;
	}

	public static String updateAndReplicate(Node pageNode, String conceptTitle, WebServiceClient webServiceClient,
			String processedNodes) throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException, JSONException {
		String replicationStatus = null;
		if (null != pageNode) {
			Node pageJCRNode = pageNode.getNode("jcr:content");
			log.debug("Setting the title on the page node");
			pageJCRNode.setProperty("jcr:title", conceptTitle);
			pageJCRNode.getSession().save();
			if (isAssetActivated(pageNode)) {
				processedNodes = processedNodes + " | ACTIVATE";
				log.debug("Replciating the content");
				replicationStatus = webServiceClient.replicateContent(pageJCRNode.getParent().getPath(), "Activate");
				processedNodes = printStatusRelatedMessages(replicationStatus, processedNodes, "Activate");
				processedNodes = processedNodes + " | COMPLETED";
			} else {
				processedNodes = processedNodes + " | NOT ACTIVATED BEFORE | COMPLETED";
				log.debug("page is not already activated so title changed and exiting ");
			}
		}
		return processedNodes;
	}

	public static boolean isAssetActivated(Node assetNode) throws RepositoryException {
		boolean activateFlag = false;
		if (null != assetNode && assetNode.hasNode("jcr:content")) {
			Node assetJCRNode = assetNode.getNode("jcr:content");
			log.debug("assetNode for validation of replication action is :" + assetNode.getPath());
			if (assetJCRNode.hasProperty("cq:lastReplicationAction")
					&& assetJCRNode.getProperty("cq:lastReplicationAction").getString().equalsIgnoreCase("Activate")) {
				log.debug("The last replication action on the node is :"
						+ assetJCRNode.getProperty("cq:lastReplicationAction").getString());
				activateFlag = true;
			}
		}
		return activateFlag;
	}

}