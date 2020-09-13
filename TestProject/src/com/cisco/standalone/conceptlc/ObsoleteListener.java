package com.cisco.standalone.conceptlc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.log4j.Logger;
import org.apache.sling.commons.json.JSONException;

public class ObsoleteListener {

	private static Logger log = Logger.getLogger(ObsoleteListener.class);
	private static String HOST_NAME = "wemapp-author-prod1-01";
	private static String USER_ID = "wemmigadm.gen";

	public static void main(String[] args) {
		Session session = null;
		FileWriter writer = null;
		BufferedReader br = null;
		String status = null;
		ArrayList<String> statusInfo = new ArrayList<String>();
		try {

			session = getSession("http://wemapp-author-prod1-02:4502/crx/server", "wemmigadm.gen",
					"w3madm@gen".toCharArray());
			String fileName = "obsolete_concepts_1442226.txt";
			String filePath = "C:\\Users\\sanvadak\\Desktop\\WIP\\LifeCycleEvents\\Obsolete_Concepts\\obsolete_concepts_PROD\\"
					+ fileName;
			WebServiceClient webServiceClinet = new WebServiceClient();
			webServiceClinet.setHostName(HOST_NAME);
			webServiceClinet.setUserId(USER_ID);
			writer = new FileWriter(
					"C:\\Users\\sanvadak\\Desktop\\WIP\\LifeCycleEvents\\Obsolete_Concepts\\obsolete_concepts_PROD\\report_"
							+ fileName, true);

			String tempId = null;
			ArrayList<String> conceptIds = new ArrayList<String>();
			ArrayList<String> lcStates = new ArrayList<String>();

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
			for (int i = 0; i < conceptIds.size(); i++) {
				try {
					String processedNodes = "";
					int processedNodesCount = 0;
					status = "";
					status = status
							+ "==========================================================================================================================================================================";
					status = status + "\n";
					System.out.println("Enter 'Y' to proceed: ");
					response = keys.nextLine();
					if (response.equalsIgnoreCase("Y")) {
						log.debug("Processing In Progress. Please wait...");
						log.debug("counter is :" + i);
						System.out.println("counter is :" + i );
						tempId = conceptIds.get(i);
						String queryForConcept = "/jcr:root/etc/tags//element(*,cq:Tag)[@conceptId='" + tempId
								+ "'] order by @jcr:score";
						status = status + tempId + " | ";
						log.debug("queryForConcept is : " + queryForConcept);
						Query query = queryManager.createQuery(queryForConcept, Query.XPATH);
						QueryResult result = query.execute();
						NodeIterator nodeIterator = result.getNodes();
						if (nodeIterator.getSize() == 1 && nodeIterator.hasNext()) {
							Node currentConceptNode = (Node) nodeIterator.next();
							status = status + currentConceptNode.getPath() + " |";
							if (currentConceptNode.hasProperty("lifeCycle")) {
								String currentLC = currentConceptNode.getProperty("lifeCycle").getString();
								if (null != currentLC && currentLC.equalsIgnoreCase(lcStates.get(i))) {
									String currentConceptPath = currentConceptNode.getPath();
									log.debug("=====================================================================================");
									log.debug("Processing stated for the conecpt : " + currentConceptPath);
									System.out.println("Processing stated for the concept : " + currentConceptPath);
									String ETC_TAGS = "/etc/tags/";
									if (isValidConcept(currentConceptPath)) {
										log.debug("Deactivating the obsolete concept");
										status = status + "Deactivate";
										String deactivationStatus = webServiceClinet.replicateContent(
												currentConceptPath, "Deactivate");
										printStatusRelatedMessages(deactivationStatus, status, "Deactivate");
										currentConceptPath = currentConceptPath.substring(ETC_TAGS.length());
										currentConceptPath = currentConceptPath.replaceFirst("/", ":");
										log.debug("conceptPath is : " + currentConceptPath);
										String queryForObsolete = "/jcr:root/content//element(*,nt:unstructured)[ @cq:tags = '"
												+ currentConceptPath + "'] order by @jcr:score";
										log.debug("query for Obsolete concept tagged pages is : " + queryForObsolete);

										Query query1 = queryManager.createQuery(queryForObsolete, Query.XPATH);
										QueryResult result1 = query1.execute();
										NodeIterator nodeIterator1 = result1.getNodes();
										log.debug("Total number of nodes : " + nodeIterator1.getSize());
										status = status + " | Total Nodes - " + nodeIterator1.getSize();
										int counter = 1;
										while (nodeIterator1.hasNext()) {
											try {
												log.debug("==================================================================================================");
												Node currentContentNode = (Node) nodeIterator1.next();
												log.debug("page counter :"+counter);
												System.out.println("page counter :"+counter);
												counter++;
												System.out.println("Processing Started for : " + currentContentNode.getPath());
												log.debug("Processing Started for : " + currentContentNode.getPath());
												ArrayList<String> validConcepts = getTotalValidConceptsOfNode(
														currentContentNode, session);
												String replicationStatus = null;
												if (processedNodes.isEmpty())
													processedNodes = "" + currentContentNode.getPath();
												else
													processedNodes = processedNodes + currentContentNode.getPath();
												if (isAssetActivated(currentContentNode, session)) {
													log.debug("num of valid concepts on the node is "
															+ validConcepts.size());
													if (validConcepts.size() > 1) {
														processedNodes = processedNodes + " | Multi-Concept ";
														log.debug("It is a multi concept asset node.");
														removeObsoleteConcepts(currentContentNode, session);
														if (!isNewPrimaryPre(currentContentNode, session)) {
															processedNodes = processedNodes
																	+ " | Primary-Pre - False | Re-Activation ";
															log.debug("Activating the asset as it is not having the primary concept in pre state");
															replicationStatus = validateAndReplicate(
																	currentContentNode, webServiceClinet, "Activate",
																	null);
															log.debug("replicationStatus : " + replicationStatus);
															processedNodes = printStatusRelatedMessages(
																	replicationStatus, processedNodes, "Activate");
														} else {
															processedNodes = processedNodes
																	+ " | Primary-Pre - True | Deactivation";
															log.debug("The new primary concept is in the pre state so deactivating the asset");
															replicationStatus = validateAndReplicate(
																	currentContentNode, webServiceClinet, "Deactivate",
																	null);
															log.debug("replicationStatus : " + replicationStatus);
															processedNodes = printStatusRelatedMessages(
																	replicationStatus, processedNodes, "Deactivate");
															processedNodes = processedNodes
																	+ " | Set MarkedForActivation";
															boolean markSet = setMarkedForActivation(
																	currentContentNode, session);
															if (markSet)
																processedNodes = processedNodes + " | SUCCESS";
															else
																processedNodes = processedNodes
																		+ " | Failed to Set Marked For Activation";
														}
													} else if (validConcepts.size() == 1) {
														processedNodes = processedNodes + " | Single-Concept ";
														log.debug("It is a single concept node");
														if (isNonEOTPage(currentContentNode, session)) {
															processedNodes = processedNodes + " | NON-EOT";
															log.debug("It is a non eot page so setting the redirect url");
															boolean redirectSet = setRedirect(currentContentNode,
																	session);
															if (redirectSet) {
																processedNodes = processedNodes
																		+ " | Redirect Set | Activate";
																replicationStatus = validateAndReplicate(
																		currentContentNode, webServiceClinet,
																		"Activate", null);
																removeObsoleteConcepts(currentContentNode, session);
																processedNodes = printStatusRelatedMessages(
																		replicationStatus, processedNodes, "Activate");
																Date deactivateLaterDate = getLaterDate(60);
																log.debug("dateToBeDeactivatedOn is : "
																		+ deactivateLaterDate);
																processedNodes = processedNodes + " | Deactivate Later";
																replicationStatus = validateAndReplicate(
																		currentContentNode, webServiceClinet,
																		"Deactivate", deactivateLaterDate);
																processedNodes = printStatusRelatedMessages(
																		replicationStatus, processedNodes, "Deactivate");
															} else {
																processedNodes = processedNodes
																		+ " | Redirect Not Set | Deactivate";
																removeObsoleteConcepts(currentContentNode, session);
																replicationStatus = validateAndReplicate(
																		currentContentNode, webServiceClinet,
																		"Deactivate", null);
																processedNodes = printStatusRelatedMessages(
																		replicationStatus, processedNodes, "Deactivate");
															}
														} else {
															removeObsoleteConcepts(currentContentNode, session);
															processedNodes = processedNodes + " | EOT | Deactivate ";
															replicationStatus = validateAndReplicate(
																	currentContentNode, webServiceClinet, "Deactivate",
																	null);
															processedNodes = printStatusRelatedMessages(
																	replicationStatus, processedNodes, "Deactivate");
														}
													}
												} else {
													log.debug("The asset is not activated before so not deactivating the asset but removing the obsolete concepts tagged to the node");
													processedNodes = processedNodes
															+ " | NOT ACTIVATED BEFORE | Remove Obsolete";
													removeObsoleteConcepts(currentContentNode, session);
													processedNodes = processedNodes + " | SUCCESS ";
												}
												processedNodes = processedNodes + "\n";
												processedNodesCount++;

												log.debug("Processing completed for the content node : "
														+ currentContentNode.getPath());
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
									log.debug("provided lifecycle in the list is not updated to the node so not processing the node "
											+ currentConceptNode.getPath());
									status = status + "NOT MATCHED";
								}
							}
						} else {
							if (nodeIterator.getSize() > 1) {
								log.debug("exiting the process as the conceptID " + tempId
										+ " is not unique and assigned to more than one concept");
								log.debug("exiting the process as the conceptID " + tempId
										+ " is not unique and assigned to more than one concept");
								status = status + "DUPLICATE";
							}
							if (nodeIterator.getSize() < 1) {
								log.debug("exiting the process as the conceptID " + tempId
										+ " is not having any concept");
								log.debug("exiting the process as the conceptID " + tempId
										+ " is not having any concept");
								status = status + "UNAVAILABLE";
							}
						}
					} else {
						System.out.println("Quitting..!");
						log.debug("Quitting..!");
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
					statusInfo.add("\n");
				} catch (Exception e) {
					log.error("Exception caught while finding the concepts ", e);
					status = status + e.getMessage();
				}

			}
			log.debug("Completed processing for the file : " + fileName);
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
		log.debug("isNonEOT : "+isNonEotNode);
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

	public static String[] getStringArrayPropertyForNode(Node node, String propName) throws PathNotFoundException,
			RepositoryException {
		String[] propValues = null;
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

	public static ArrayList<String> getTotalValidConceptsOfNode(Node pageNode, Session session)
			throws PathNotFoundException, RepositoryException {
		String[] propValues = getStringArrayPropertyForNode(pageNode, "cq:tags");
		ArrayList<String> validConceptsList = new ArrayList<String>();
		log.debug("valid concepts are :");
		for (int i = 0; i < propValues.length; i++) {
			log.debug("tag " + i + " is : " + propValues[i]);
			String temp = propValues[i];
			temp = "/etc/tags/" + temp.replace(":", "/");
			if (isValidConcept(temp)) {
				log.debug(temp);
				validConceptsList.add(temp);
			}
		}
		return validConceptsList;
	}

	public static Node removeObsoleteConcepts(Node pageNode, Session session) throws PathNotFoundException,
			RepositoryException {
		String[] tagsOfNode = getStringArrayPropertyForNode(pageNode, "cq:tags");
		ArrayList<String> tagsList = new ArrayList<String>();
		for (int i = 0; i < tagsOfNode.length; i++) {
			String tag = tagsOfNode[i];
			tag = "/etc/tags/" + tag.replaceFirst(":", "/");
			if (isValidConcept(tag)) {
				log.debug(tag + " is a valid concept so verifying the lifeCycle property");
				Node conceptNode = session.getNode(tag);
				if (conceptNode.hasProperty("lifeCycle")) {
					String lcProp = conceptNode.getProperty("lifeCycle").getString();
					if (!lcProp.equalsIgnoreCase("obsolete")) {
						log.debug("adding the concept " + conceptNode.getPath()
								+ "to the list as it is not in obsolete state ");
						tag = tag.substring("/etc/tags/".length()).replaceFirst("/", ":");
						tagsList.add(tag);
					} else {
						log.debug("removing the concept " + conceptNode.getPath()
								+ " from the list as it is in obsolete state ");
					}
				} else {
					log.debug(tag
							+ " is a not having the lifeCycle property so ignoring verification and adding back to the tags list");
					tag = tag.substring("/etc/tags/".length()).replaceFirst("/", ":");
					tagsList.add(tag);
				}
			} else {
				log.debug(tag + " is a not valid concept so not verifying the lifeCycle property");
				tag = tag.substring("/etc/tags/".length()).replaceFirst("/", ":");
				tagsList.add(tag);
			}
		}
		log.debug("Setting the tags after removing the obsolete concepts back to the node : ");
		pageNode.setProperty("cq:tags", tagsList.toArray(new String[tagsList.size()]));
		session.save();
		return pageNode;
	}

	public static boolean isDamAsset(Node assetNode) throws RepositoryException {
		boolean isDamAsset = false;
		String assetPath = assetNode.getPath();
		if (assetPath.endsWith("metadata")) {
			isDamAsset = true;
		} else if (assetPath.endsWith("jcr:content")) {
			isDamAsset = false;
		}
		return isDamAsset;
	}

	public static boolean isNewPrimaryPre(Node pageNode, Session session) throws PathNotFoundException,
			RepositoryException {
		String[] propValues = getStringArrayPropertyForNode(pageNode, "cq:tags");
		boolean preFlag = false;
		for (int i = 0; i < propValues.length; i++) {
			String temp = propValues[i];
			temp = "/etc/tags/" + temp.replace(":", "/");
			log.debug("tag " + i + " is : " + temp);
			if (isValidConcept(temp)) {
				log.debug("It is a valid.. Chceking the lifeCycle");
				Node conceptNode = session.getNode(temp);
				if (conceptNode.hasProperty("lifeCycle")) {
					String lcProp = conceptNode.getProperty("lifeCycle").getString();
					log.debug("lifeCycle property of the concept is : " + lcProp);
					if (lcProp.equalsIgnoreCase("pre")) {
						log.debug("The new primay concept is in the pre state.!!");
						preFlag = true;
					}
				}
				break;
			}
		}
		return preFlag;
	}

	public static boolean setMarkedForActivation(Node node, Session session) throws ValueFormatException,
			VersionException, LockException, ConstraintViolationException, RepositoryException {
		boolean setMark = false;
		if (null != node) {
			log.debug("setting the marked for activation property ");
			node.setProperty("cisco:markedForActivation", true);
			session.save();
			log.debug("setting the marked for activation property - done ");
			setMark = true;
		}
		return setMark;
	}

	public static String validateAndReplicate(Node assetNode, WebServiceClient webServiceClient, String action,
			Date date) throws RepositoryException, JSONException {
		String status = null;
		String assetPathTobeReplicated = null;
		if (null != assetNode) {
			if (isDamAsset(assetNode)) {
				assetPathTobeReplicated = assetNode.getParent().getParent().getPath();
			} else {
				assetPathTobeReplicated = assetNode.getParent().getPath();
			}
		}
		log.debug("assetPathTobeReplicated is : " + assetPathTobeReplicated);
		if (null != assetPathTobeReplicated) {
			if (date == null) {
				if (isDamAsset(assetNode)) {
					status = webServiceClient.replicateBookContent(assetPathTobeReplicated, action);
				} else {
					status = webServiceClient.replicateContent(assetPathTobeReplicated, action);
				}
			} else {
				status = webServiceClient.deactivateLater(assetPathTobeReplicated, action, date);
			}
		}
		return status;
	}

	public static boolean setRedirect(Node assetNode, Session session) throws RepositoryException, JSONException,
			InterruptedException {
		boolean redirectSet = false;
		if (null != assetNode) {
			String redirectTarget = null;
			redirectTarget = PageRedirect.getRedirectPageUrl(assetNode.getPath(), session);
			// Write the redirect code here
			if (null != redirectTarget) {
				assetNode.setProperty("cisco:redirectURL", redirectTarget);
				log.debug("Setting the redirect url");
				session.save();
				Thread.sleep(100);
				redirectSet = true;
			} else {
				log.debug("Couldn't find the active page url so not setting the redirect URL.");
			}
		}
		return redirectSet;
	}

	public static Date getLaterDate(int days) {
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, days);
		dt = c.getTime();
		return dt;
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

	public static boolean isAssetActivated(Node assetMetaNode, Session session) throws RepositoryException {
		boolean activateFlag = false;
		Node assetNode = assetMetaNode;
		if (isDamAsset(assetNode)) {
			assetNode = assetNode.getParent();
		}
		log.debug("assetNode for validation of replication action is :" + assetNode.getPath());
		if (assetNode.hasProperty("cq:lastReplicationAction")
				&& assetNode.getProperty("cq:lastReplicationAction").getString().equalsIgnoreCase("Activate")) {
			log.debug("The last replication action on the node is :"
					+ assetNode.getProperty("cq:lastReplicationAction").getString());
			activateFlag = true;
		}
		return activateFlag;
	}

}
