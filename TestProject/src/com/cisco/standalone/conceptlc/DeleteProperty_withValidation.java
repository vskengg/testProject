package com.cisco.standalone.conceptlc;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.log4j.Logger;

public class DeleteProperty_withValidation {

	private static Logger log = Logger.getLogger(DeleteProperty.class);

	public static void main(String[] args) throws ParseException {
		String status = "";
		FileWriter writer = null;
		status = status
				+ "============================================================================================================";
		status = status + "\n";
		// localhost:4502
		Session localSession = getSession("http://wemapp-author-dev3-01:4502/crx/server", "vsingara",
				"Armoor02*".toCharArray());
		Session session = localSession;
		ArrayList<String> statusInfo = new ArrayList<String>();
		try {
			writer = new FileWriter(
					"C:\\Users\\sanvadak\\Desktop\\WIP\\LifeCycleEvents\\Delete Property\\Report_Invalid_Tags_Dev.txt",
					true);
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			String queryForNodes = "/jcr:root/content/dam/en/us//element(*, dam:AssetContent)[@newRendition] order by @jcr:score";
			Query query = queryManager.createQuery(queryForNodes, Query.XPATH);
			QueryResult result = query.execute();
			String propName = "newRendition";
			NodeIterator nodeIterator = result.getNodes();
			log.debug("query being run is : " + queryForNodes);
			float size = nodeIterator.getSize();
			log.debug("Total number of nodes : " + size);
			int count = 0 ;
			while (nodeIterator.hasNext()) {
				log.debug("processing #" + nodeIterator.getPosition() + " Remaining Nodes : "
						+ (size - nodeIterator.getPosition()));
				System.out.println("processing #" + nodeIterator.getPosition() + " Remaining Nodes : "
						+ (size - nodeIterator.getPosition()));
				try {
					Node assetNode = (Node) nodeIterator.next();
					if (assetNode.hasProperty(propName)) {
						count++;
						log.debug("Removing the newRendition property for the node : " + assetNode.getPath());
						//assetNode.getProperty(propName).remove();
					}
					if(count % 100 == 0){
						session.save();
					}
					/*if (assetNode.hasNode("metadata")) {
						Node metaNode = assetNode.getNode("metadata");
						if (metaNode.hasProperty("cq:tags")) {
							Property prop = metaNode.getProperty("cq:tags");
							if (prop != null) {
								Value[] values = prop.getValues();
								String[] invalidTags = new String[values.length];
								int j = 0;
								if (values.length > 0) {
									for (int i = 0; i < values.length; i++) {
										String temp = values[i].getString();
										String tagPath = temp.replace(":", "/");
										tagPath = "/etc/tags/" + tagPath;
										if (!session.itemExists(tagPath)) {
											invalidTags[j] = temp;
											j++;
											log.debug("=====================================================================");
											log.debug("the asset " + assetNode.getParent().getPath()
													+ " is having an invalid tag : " + temp);
											log.debug("=====================================================================");
										}
									}
								}
								if (j > 1) {
									status = status + assetNode.getParent().getPath() + " | ";
									for (int k = 0; k < invalidTags.length; k++) {
										if (k == 0) {
											status = status + invalidTags[k];
										} else {
											status = status + " , " + invalidTags[k];
										}
									}
									status = status + "\n";
								}
							}
						}
					} else {
						log.debug("The asset " + assetNode.getParent().getPath()
								+ " is not having the metadata node so skipping the validation");
					}*/

				} catch (Exception e) {
					log.error("Exception caught :", e);
				}
				//statusInfo.add(status);
			}
			session.save();
		} catch (Exception e) {
			log.error("Exception caught : ", e);
		} finally {
			try {
				for (int j = 0; j < statusInfo.size(); j++) {
					writer.append(statusInfo.get(j));
					writer.append("\n");
				}
				if (session != null)
					session.logout();
				if (writer != null)
					writer.close();
			} catch (IOException ex) {
				log.error("Exception caught ", ex);
			}
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
