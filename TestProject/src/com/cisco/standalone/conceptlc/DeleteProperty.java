package com.cisco.standalone.conceptlc;

import java.text.ParseException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.log4j.Logger;

public class DeleteProperty {

	private static Logger log = Logger.getLogger(DeleteProperty.class);

	public static void main(String[] args) throws ParseException {
		// localhost:4502
		Session localSession = getSession("http://wemapp-author-dev3-01:4502/crx/server", "vsingara",
				"Armoor02*".toCharArray());
		Session session = localSession;
		try {
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			String queryForNodes = "/jcr:root/content/dam/en/us//element(*, dam:AssetContent)[@newRendition] order by @jcr:score";
			Query query = queryManager.createQuery(queryForNodes, Query.XPATH);
			QueryResult result = query.execute();
			String propName = "newRendition";
			NodeIterator nodeIterator = result.getNodes();
			log.debug("query being run is : " + queryForNodes);
			long size = nodeIterator.getSize();
			log.debug("Total number of nodes : " + size);
			int count = 0;
			while (nodeIterator.hasNext()) {
				log.debug("processing #" + nodeIterator.getPosition() + " Remaining Nodes : "
						+ (size - nodeIterator.getPosition()));
				try {
					Node assetNode = (Node) nodeIterator.next();
					if (assetNode.hasProperty(propName) && !assetNode.isLocked()
							&& assetNode.getParent().isCheckedOut()) {
						log.debug("Count is : " + count);
						count++;
						log.debug("Removing the newRendition property for the node : " + assetNode.getPath());
						assetNode.getProperty(propName).remove();
						if (count % 25 == 0) {
							log.debug("Saving..!!");
							session.save();
						}
					} else if (assetNode.isLocked()) {
						log.debug("The asset : " + assetNode.getPath() + " is locked , so not processing .. !!");
					} else if (!assetNode.getParent().isCheckedOut()) {
						log.debug("The asset : " + assetNode.getPath()
								+ " is checked in , so not processing the same.!!");
					}
					if (count > 300) {
						break;
					}
				} catch (Exception e) {
					log.error("Exception caught :", e);
				}
			}
			session.save();
		} catch (Exception e) {
			log.error("Exception caught : ", e);
		} finally {
			if (session != null)
				session.logout();
		}
		log.debug("Completed...!");
		System.out.println("Completed...!");
	}

	public static Session getSession(String repoUrl, String userName, char[] password) {
		Session session = null;
		try {
			System.out.println("============== started ==============");
			Repository repository = JcrUtils.getRepository(repoUrl);
			System.out.println("logging in : " + repoUrl);
			session = repository.login(new SimpleCredentials(userName, password), "crx.default");
		} catch (RepositoryException e) {
			log.error("Repositroy Exception :" + e.getMessage());
		}
		return session;
	}

}
