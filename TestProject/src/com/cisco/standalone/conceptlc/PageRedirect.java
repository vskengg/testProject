package com.cisco.standalone.conceptlc;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.log4j.Logger;

public class PageRedirect {

	private static Logger log = Logger.getLogger(PageRedirect.class.getName());

	/*public static void main(String[] args) {
		Session session = null;
		try {
			Repository repository;
			String workspace = "crx.default";
			String username = "vsingara";
			char[] password = "Armoor02*".toCharArray();
			repository = JcrUtils.getRepository("http://wemapp-author-dev3-02:4502/crx/server");
			// log into repository
			session = repository.login(new SimpleCredentials(username, password), workspace);
			log.debug("logged in");
			String pageUrl = "/content/en/us/products/devtest/testNode-0912-5/index";
			String redirectUrl = getRedirectPageUrl(pageUrl, session);
			System.out.println("the redirect url is : " + redirectUrl);
		} catch (Exception e) {
			log.error("Exception : ", e);
		}
	}*/

	/**
	 * It returns active page url
	 * 
	 * @param pageurl
	 * @return
	 */
	public static String getRedirectPageUrl(String pageurl, Session session) {
		String activePageUrl = null;
		String conceptMetaClass = null;
		String docTypeMetaClass = null;
		try {
			log.debug("RedirectObsoletePage->getActivePageUrl() method started");
			if (session == null)
				throw new Exception("Unable to get CRS session object");
			if (pageurl.indexOf(PageRedirectConstants.PAGE_CONTENT_NODE) == -1)
				pageurl = pageurl + PageRedirectConstants.PAGE_CONTENT_NODE;
			log.debug("obsolete page url::" + pageurl);
			Node pagecontent = session.getNode(pageurl);
			log.info("sesison object::" + session);
			Node conceptNode = getConceptNode(pagecontent, session);
			Node docTypeNode = getDocTypeNode(pagecontent, session);
			if (conceptNode != null && docTypeNode != null) {
				log.debug("pageConcept::" + conceptNode);
				Node parentconcept = getParentLifecyleActive(conceptNode);
				if (parentconcept == null)
					throw new Exception("Unable to get concept which has lifeCycle active");
				log.debug("parentconcept::" + parentconcept);
				if (parentconcept != null && parentconcept.hasProperty(PageRedirectConstants.METACLASS)) {
					conceptMetaClass = parentconcept.getProperty(PageRedirectConstants.METACLASS).getValue()
							.getString();
				}
				if (docTypeNode != null && docTypeNode.hasProperty(PageRedirectConstants.METACLASS)) {
					docTypeMetaClass = docTypeNode.getProperty(PageRedirectConstants.METACLASS).getValue().getString();
				}
				if (conceptMetaClass != null && docTypeMetaClass != null) {
					while (!conceptMetaClass.equals(docTypeMetaClass)
							&& !docTypeNode.getName().equals(PageRedirectConstants.TAGS_ROOT)) {
						docTypeNode = docTypeNode.getParent();
						if (docTypeNode != null && docTypeNode.hasProperty(PageRedirectConstants.METACLASS)) {
							docTypeMetaClass = docTypeNode.getProperty(PageRedirectConstants.METACLASS).getValue()
									.getString();
						}
						if (docTypeMetaClass == null) {
							throw new Exception("Unable to get Metaclass for doctype");
						}
					}
				} else {
					throw new Exception("Unable to get Metaclass for either concept or doctype");
				}
				String doctypePath = docTypeNode.getPath();
				log.info("doctypeTagId::" + doctypePath);
				if (doctypePath.length() > 10 && doctypePath.contains("/etc/tags/")) {
					doctypePath = doctypePath.substring(10, doctypePath.length());
					doctypePath = doctypePath.replaceFirst("/", ":");
				}
				String conceptPath = parentconcept.getPath();
				if (conceptPath.length() > 10 && conceptPath.contains("/etc/tags/")) {
					conceptPath = conceptPath.substring(10, conceptPath.length());
					conceptPath = conceptPath.replaceFirst("/", ":");
				}
				log.info("conceptTagId::" + conceptPath);
				String searchScope = "/content";
				log.info("searchScope::" + searchScope);
				QueryExecutor qexecutor = new QueryExecutor();
				activePageUrl = qexecutor.getPagePath(session, doctypePath, conceptPath, searchScope);
			}
			//CRXSessionHelper.closeCRXSession(session);
			log.debug("RedirectObsoletePage->getActivePageUrl() method ended");
		} catch (Exception e) {
			//CRXSessionHelper.closeCRXSession(session);
			log.error("Exception in getting page redirect : ", e);
		}
		return activePageUrl;

	}

	private static Node getConceptNode(Node pageContentNode, Session session) throws ValueFormatException,
			PathNotFoundException, RepositoryException {
		Node primaryConcept = null;
		if (pageContentNode.hasProperty(PageRedirectConstants.TAG_NODE_TYPE)) {
			Value[] tagsArray = pageContentNode.getProperty(PageRedirectConstants.TAG_NODE_TYPE).getValues();
			if (tagsArray != null && tagsArray.length > 0) {
				log.debug("tagsArray::" + tagsArray.length);
				for (int i = 0; i < tagsArray.length; i++) {
					String conceptPath = tagsArray[i].getString();
					log.debug("tagPath::" + conceptPath);
					if (conceptPath.contains(":")) {
						if (!conceptPath.substring(0, conceptPath.indexOf(":")).equals("DocTypes")) {
							log.debug("the primary concept is : " + conceptPath);
							conceptPath = "/etc/tags/" + conceptPath.replaceFirst(":", "/");
							if (session.nodeExists(conceptPath)) {
								primaryConcept = session.getNode(conceptPath);
								break;
							}
						}
					}
				}
			}
		}
		return primaryConcept;
	}

	private static Node getDocTypeNode(Node pageContentNode, Session session) throws ValueFormatException,
			PathNotFoundException, RepositoryException {
		Node docTypeNode = null;
		if (pageContentNode.hasProperty(PageRedirectConstants.TAG_NODE_TYPE)) {
			Value[] tagsArray = pageContentNode.getProperty(PageRedirectConstants.TAG_NODE_TYPE).getValues();
			if (tagsArray != null && tagsArray.length > 0) {
				log.debug("tagsArray::" + tagsArray.length);
				for (int i = 0; i < tagsArray.length; i++) {
					String docTypePath = tagsArray[i].getString();
					log.debug("tagPath::" + docTypePath);
					if (docTypePath.contains(":")) {
						if (docTypePath.substring(0, docTypePath.indexOf(":")).equals("DocTypes")) {
							log.debug("the primary doctype is : " + docTypePath);
							docTypePath = "/etc/tags/" + docTypePath.replaceFirst(":", "/");
							if (session.nodeExists(docTypePath)) {
								docTypeNode = session.getNode(docTypePath);
								break;
							}
						}
					}
				}
			}
		}
		return docTypeNode;
	}

	/**
	 * This method returns concept which has lifecycle active
	 * 
	 * @param concept
	 * @return
	 * @throws RepositoryException
	 */
	public static Node getParentLifecyleActive(Node concept) throws RepositoryException {
		Node parentconcept = null;
		log.debug("obsolete concept::" + concept.getName());
			Node temp = concept;
			boolean isLifecyleActive = true;
			temp = temp.getParent();

			while (isLifecyleActive && temp != null && !temp.getName().equals(PageRedirectConstants.TAGS_ROOT)) {

				log.debug("**parent name" + temp.getName());

				if (temp.hasProperty(PageRedirectConstants.METACLASS)) {

					String metaclass = temp.getProperty(PageRedirectConstants.METACLASS).getString();
					log.debug("**metaclass" + metaclass);

					if (!metaclass.equalsIgnoreCase(PageRedirectConstants.SUBCATEGORY)) {

						if (temp.hasProperty(PageRedirectConstants.LIFECYCLE_PROP)) {
							String lifeCycle = temp.getProperty(PageRedirectConstants.LIFECYCLE_PROP).getString();
							log.debug("**lifecycle::" + lifeCycle);
							if (lifeCycle.equals(PageRedirectConstants.LIFECYCLE_ACTIVE)) {
								parentconcept = temp;
								isLifecyleActive = false;
								log.debug("**got concept which has active" + temp.getName());
								break;
							}
						} 
					}
				}
				temp = temp.getParent();
				log.info("parent node::" + parentconcept);
			}
		return parentconcept;

	}

}
