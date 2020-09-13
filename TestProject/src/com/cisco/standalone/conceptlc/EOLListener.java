package com.cisco.standalone.conceptlc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import org.apache.log4j.Logger;

import org.apache.jackrabbit.commons.JcrUtils;

public class EOLListener {

	private static Logger log = Logger.getLogger(EOLListener.class);
	private static String HOST_NAME = "wemapp-author-prod1-01";
	private static String USER_ID = "wemmigadm.gen";

	public static void main(String[] args) {

		Session session = getSession("http://wemapp-author-prod1-01:4502/crx/server", "wemmigadm.gen",
				"wemmigadm.gen".toCharArray());

		/*
		 * Session session =
		 * getSession("http://wemapp-author-dev3-01:4502/crx/server",
		 * "vsingara", "Armoor02*".toCharArray());
		 */

		String fileName = "eol_concepts_5to10.txt"; // "eol_concepts_1to50.txt";
		String filePath = "C:\\Users\\sanvadak\\Desktop\\WIP\\LifeCycleEvents\\EOL_Concepts\\" + fileName; // eol_concepts_1to50.txt";
		FileWriter writer = null;
		WebServiceClient webServiceClinet = new WebServiceClient();
		webServiceClinet.setHostName(HOST_NAME);
		webServiceClinet.setUserId(USER_ID);

		try {
			writer = new FileWriter("C:\\Users\\sanvadak\\Desktop\\WIP\\LifeCycleEvents\\EOL_Concepts\\report_"
					+ fileName, true);
		} catch (Exception e) {
			log.error("Exception caught : ", e);
		}
		BufferedReader br = null;
		String tempId = null;
		ArrayList<String> conceptIds = new ArrayList<String>();
		ArrayList<String> lcStates = new ArrayList<String>();
		ArrayList<String> statusInfo = new ArrayList<String>();
		String status = null;

		try {
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			String sCurrentLine;
			Scanner keys = new Scanner(System.in);
			String response;
			br = new BufferedReader(new FileReader(filePath));
			while ((sCurrentLine = br.readLine()) != null) {
				String[] value = sCurrentLine.split(",");
				conceptIds.add(value[0]);
				lcStates.add(value[1]);
			}
			int k = 0;

			for (int i = 0; i < conceptIds.size(); i++) {
				String processedNodes = "";
				String skippedNodes = "";
				int processedNodesCount = 0;
				int skippedNodesCount = 0;
				status = "";
				status = status
						+ "==========================================================================================================================================================================";
				status = status + "\n";
				System.out.println("Enter 'Y' to proceed: ");
				response = keys.nextLine();
				if (response.equalsIgnoreCase("Y")) {
					System.out.println("Processing In Progress. Please wait...");
					log.debug("i is :" + i);
					tempId = conceptIds.get(i);
					String queryForConcept = "/jcr:root/etc/tags//element(*,cq:Tag)[@conceptId='" + tempId
							+ "'] order by @jcr:score";
					status = status + tempId + " |";
					log.debug("queryForConcept is : " + queryForConcept);
					Query query = queryManager.createQuery(queryForConcept, Query.XPATH);
					QueryResult result = query.execute();
					NodeIterator nodeIterator = result.getNodes();

					if (nodeIterator.getSize() == 1 && nodeIterator.hasNext()) {
						Node currentNode = (Node) nodeIterator.next();
						status = status + currentNode.getPath() + " |";
						if (currentNode.hasProperty("lifeCycle")) {
							String currentLC = currentNode.getProperty("lifeCycle").getString();
							if (null != currentLC && currentLC.equalsIgnoreCase(lcStates.get(i))) {
								String currentConceptPath = currentNode.getPath();
								log.debug("=====================================================================================");
								log.debug("Processing stated for the conecpt : " + currentConceptPath);
								String ETC_TAGS = "/etc/tags/";
								if (isValidConcept(currentConceptPath)) {
									status = status + " | Processing ";
									currentConceptPath = currentConceptPath.substring(ETC_TAGS.length());
									currentConceptPath = currentConceptPath.replaceFirst("/", ":");
									log.debug("conceptPath is : " + currentConceptPath);
									String queryForEOL = "/jcr:root/content//element(*,nt:unstructured)[ @cq:tags = '"
											+ currentConceptPath + "'] order by @jcr:score";
									log.debug("query for EOL concept tagged pages is : " + queryForEOL);

									Query query1 = queryManager.createQuery(queryForEOL, Query.XPATH);
									QueryResult result1 = query1.execute();
									NodeIterator nodeIterator1 = result1.getNodes();
									log.debug("Total number of nodes : " + nodeIterator1.getSize());
									status = status + " | Total Nodes - " + nodeIterator1.getSize();

									while (nodeIterator1.hasNext()) {
										log.debug("==================================================================================================");
										Node currentContentNode = (Node) nodeIterator1.next();
										log.debug("Processing Started for : " + currentContentNode.getPath());
										boolean isNonEOTPage = isNonEOTPage(currentContentNode, session);
										if (isNonEOTPage) {
											Node docTypeNode = getDocTypeNode(currentContentNode, session);
											if (docTypeNode.hasProperty("eolDocType")) {
												String eolDocTypeStr = docTypeNode.getProperty("eolDocType")
														.getString();
												String currentDocTypeNodepath = docTypeNode.getPath();
												String parentNodePath = currentDocTypeNodepath.substring(0,
														currentDocTypeNodepath.lastIndexOf("/") + 1);
												if (null != eolDocTypeStr && !eolDocTypeStr.startsWith("/etc/tags/"))
													eolDocTypeStr = parentNodePath + eolDocTypeStr;
												eolDocTypeStr = eolDocTypeStr.replace("/etc/tags/DocTypes/",
														"DocTypes:");
												log.debug("eolDocTypeStr is : " + eolDocTypeStr);
												boolean tagsChanged = false;
												String[] tagsOfCurrentNode = getStringArrayPropertyForNode(
														currentContentNode, "cq:tags");
												for (int j = 0; j < tagsOfCurrentNode.length; j++) {
													String tag = tagsOfCurrentNode[j];
													if (null != tag && tag.contains("DocTypes")) {
														tagsOfCurrentNode[j] = eolDocTypeStr;
														tagsChanged = true;
													}
												}
												if (tagsChanged) {
													currentContentNode.setProperty("cq:tags", tagsOfCurrentNode);
													currentContentNode.getSession().save();
													log.debug("Succesfully updated the eolDocType on the node");
													String activationPath = null;
													if (currentContentNode.getPath().endsWith("jcr:content"))
														activationPath = currentContentNode.getParent().getPath();
													if (currentContentNode.getPath().endsWith("metadata"))
														activationPath = currentContentNode.getParent().getParent()
																.getPath();
													log.debug("Starting activation for the path : " + activationPath);
													String statusCode = webServiceClinet.replicateContent(
															currentContentNode.getParent().getPath(), "Activate");
													if (null != statusCode && statusCode.equalsIgnoreCase("200")) {
														log.debug("Sucessfully activated the path : " + activationPath);
													} else {
														log.error("Activation failed for the path : " + activationPath);
													}
												}
												if (processedNodes.isEmpty())
													processedNodes = currentContentNode.getPath()
															+ " | NON-EOT | Processed";
												else
													processedNodes = processedNodes + currentContentNode.getPath()
															+ " | NON-EOT | Processed";
												processedNodes = processedNodes + "\n";

											} else {
												log.debug("eolDocType is not found so skipping the process");
												if (processedNodes.isEmpty())
													processedNodes = currentContentNode.getPath()
															+ " | NON-EOT | eolDocType Not Found";
												else
													processedNodes = processedNodes + currentContentNode.getPath()
															+ " | NON-EOT | eolDocType Not Found";
												processedNodes = processedNodes + "\n";
											}
											processedNodesCount++;
										} else {
											log.debug("Skipped");
											if (skippedNodes.isEmpty())
												skippedNodes = currentContentNode.getPath() + " | EOT | Skipped";
											else
												skippedNodes = skippedNodes + currentContentNode.getPath()
														+ " | EOT | Skipped";
											skippedNodes = skippedNodes + "\n";
											skippedNodesCount++;
										}
										k++;
										log.debug("Processing completed for the content node : "
												+ currentContentNode.getPath());
										log.debug("==================================================================================================");
									}
								} else {
									log.debug("Processing skipped as the concept is not a valid concept to process : "
											+ currentConceptPath);
								}
								log.debug("Processing completed for the concept : " + currentConceptPath);
								log.debug("==================================================================================================");
							} else {
								log.debug("provided lifecycle in the list is not updated to the node so not processing the node "
										+ currentNode.getPath());
								status = status + "NOT MATCHED";
							}
						}
					} else {
						if (nodeIterator.getSize() > 1) {
							System.out.println("exiting the process as the conceptID " + tempId
									+ " is not unique and assigned to more than one concept");
							log.debug("exiting the process as the conceptID " + tempId
									+ " is not unique and assigned to more than one concept");
							status = status + "DUPLICATE";
						}
						if (nodeIterator.getSize() < 1) {
							System.out.println("exiting the process as the conceptID " + tempId
									+ " is not having any concept");
							log.debug("exiting the process as the conceptID " + tempId + " is not having any concept");
							status = status + "UNAVAILABLE";
						}
					}
				} else {
					System.out.println("Quitting..!");
					break;
				}
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
				statusInfo.add(skippedNodes);
				statusInfo
						.add("=======================================================================================================================================");
				statusInfo.add("\n");
			}
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
				ex.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
					if (session != null)
						session.logout();
					if (writer != null)
						writer.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public static Session getSession(String repoUrl, String userName, char[] password) {
		Session session = null;
		try {
			log.debug("============== started ==============");
			Repository repository = JcrUtils.getRepository(repoUrl);
			log.debug("logging in");
			session = repository.login(new SimpleCredentials(userName, password), "crx.default");
		} catch (RepositoryException e) {
			log.debug("Repositroy Exception :" + e.getMessage());
		}
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

	public static boolean isNonEOTPage(Node currentContentNode, Session session) throws PathNotFoundException,
			RepositoryException {
		String nonEotConcepts = "Additional Technologies|Category|Interface/Module|Interfaces/Modules Types|IOS Feature|IOS Feature Set|IOS Protocol Group|IOS Protocol Option|IOS Software Major Release|IOS Software Minor Release|IOS Software Sub-Category|IOS Technologies Sub-Category|IOS Technology|Model|Series|Software Family|Software Version/Option|Solution Class|Solution Core|Solution Family|Solution Horizontal Segment|Solution Hub|Solution Offering|Solution Technology Segment|Technologies Category|Technologies Protocol|Technologies Protocol Group|Sub-Category";
		boolean isNonEotNode = false;
		String docTypeString = null;
		String conceptString = null;
		String docTypeMetaClass = null;
		String conceptMetaClass = null;
		Value[] docTypeConceptMetaClassName = null;
		Node docTypeNode = null;
		Node priConceptNode = null;
		boolean validDocTypeFound = false;
		boolean validConceptFound = false;
		String[] propValues = getStringArrayPropertyForNode(currentContentNode, "cq:tags");
		for (int i = 0; i < propValues.length; i++) {
			String temp = propValues[i];
			String tagPath = temp.replace(":", "/");
			tagPath = "/etc/tags/" + tagPath;
			if (temp.contains("DocTypes:")) {
				docTypeString = temp;
				validDocTypeFound = true;
				log.debug("DocType is : " + docTypeString);
			} else if (isValidConcept(tagPath)) {
				conceptString = temp;
				validConceptFound = true;
				log.debug("Concept is : " + conceptString);
			}
			if (validConceptFound && validDocTypeFound) {
				break;
			}
		}
		if (validConceptFound && validDocTypeFound) {
			docTypeNode = getNodeOfTag(docTypeString, session);
			priConceptNode = getNodeOfTag(conceptString, session);
			if (null != docTypeNode && docTypeNode.hasProperty("metaClass")) {
				docTypeMetaClass = docTypeNode.getProperty("metaClass").getString();
			}
			if (null != priConceptNode && priConceptNode.hasProperty("metaClass")) {
				conceptMetaClass = priConceptNode.getProperty("metaClass").getString();
			}
			log.debug("docTypeMetaClass is : " + docTypeMetaClass);
			log.debug("conceptMetaClass is : " + conceptMetaClass);
			ArrayList<String> listConceptMetaNames = new ArrayList<String>();
			listConceptMetaNames = getList(nonEotConcepts, "|");
			if (docTypeMetaClass != null && listConceptMetaNames.contains(docTypeMetaClass)
					&& docTypeNode.hasProperty("concept.metaclass")) {
				log.debug(docTypeMetaClass + " is in the configured list of non-eot");
				docTypeConceptMetaClassName = docTypeNode.getProperty("concept.metaclass").getValues();
				if (null != docTypeConceptMetaClassName) {
					for (int i = 0; i < docTypeConceptMetaClassName.length; i++) {
						String temp = docTypeConceptMetaClassName[i].getString();
						log.debug("concept.metaClass value " + i + " is : " + temp);
						if (null != conceptMetaClass && temp.equalsIgnoreCase(conceptMetaClass)) {
							log.debug("concept.metaClass value :" + temp + " contains the metaClass of concept");
							isNonEotNode = true;
							break;
						}
					}
					if (!isNonEotNode) {
						log.debug("concept.metaClass values are not matched with primary concept's metaClass");
					}
				}
			} else {
				log.debug(docTypeMetaClass + " is not in the configured list of non-eot");
			}
		} else {
			if (!validConceptFound)
				log.debug("Valid Concept is not found for the path : " + currentContentNode.getPath());
			if (!validDocTypeFound)
				log.debug("Valid Concept is not found " + currentContentNode.getPath());
		}
		return isNonEotNode;
	}

	public static Node getNodeOfTag(String tagPath, Session session) throws PathNotFoundException, RepositoryException {
		Node tagNode = null;
		String absPath = "/etc/tags/";
		if (null != tagPath) {
			absPath = absPath + tagPath.replaceFirst(":", "/");
			tagNode = session.getNode(absPath);
		}
		return tagNode;
	}

	public static ArrayList<String> getList(String input, String separator) {
		StringTokenizer stTokenizer = new StringTokenizer(input, separator);
		ArrayList<String> arrayList = new ArrayList<String>();
		while (stTokenizer.hasMoreTokens()) {
			String token = stTokenizer.nextToken();
			arrayList.add(token);
		}
		return arrayList;
	}

	public static String[] getStringArrayPropertyForNode(Node node, String propName) {
		String[] propValues = null;
		try {
			Property prop = node.getProperty(propName);
			if (prop != null) {
				Value[] values = prop.getValues();
				propValues = new String[values.length];
				if (propValues.length > 0) {
					for (int i = 0; i < values.length; i++) {
						propValues[i] = values[i].getString();
					}
				}
			}
		} catch (javax.jcr.ValueFormatException vfe) {
			log.error("ConceptListenerUtil - getStringArrayPropertyForNode | ValueFormatException : ", vfe);
		} catch (javax.jcr.RepositoryException re) {
			log.error("ConceptListenerUtil - getStringArrayPropertyForNode | Repository Exception caught : ", re);
		}
		return propValues;
	}

	public static Node getDocTypeNode(Node pageNode, Session session) throws PathNotFoundException, RepositoryException {
		String[] propValues = getStringArrayPropertyForNode(pageNode, "cq:tags");
		Node node = null;
		for (int i = 0; i < propValues.length; i++) {
			String temp = propValues[i];
			if (temp.contains("DocTypes:")) {
				node = getNodeOfTag(temp, session);
				break;
			}
		}
		return node;

	}

}
