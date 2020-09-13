package com.cisco.standalone.conceptlc;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krevoori
 * 
 */
public class QueryExecutor {
	private static final Logger log = LoggerFactory.getLogger(QueryExecutor.class);



	/**
	 * It will construct the sql query and invoked queryexecution method then
	 * return active page url
	 * 
	 * @param session
	 * @param doctypeTagId
	 * @param conceptTagId
	 * @return
	 */
	public String getPagePath(Session session, String doctypeTagId, String conceptTagId, String searchScope) {
		log.debug("QueryExecutor->getPagePath() method started");
		log.debug("searchScope::" + searchScope);
		String pagepath = null;
		try {
			String query = "/jcr:root/content//element(*, cq:PageContent) [@cq:tags ='"+ doctypeTagId+ "' and @cq:tags = '"+ conceptTagId+"'] order by @jcr:score";
			log.debug("sql query::" + query);
			Node pageNode = null;
			// get results from xpath query
			QueryManager queryManager = session.getWorkspace().getQueryManager();
			Query xpathQuery = queryManager.createQuery(query, Query.XPATH);
			QueryResult queryResult = xpathQuery.execute();
			if (queryResult != null) {
				NodeIterator pagePathqueryIterator = queryResult
						.getNodes();
				if (pagePathqueryIterator
						.getSize() > 0) {
					while(pagePathqueryIterator.hasNext()) {
					pageNode = (Node) pagePathqueryIterator
							.next();
					}
				}
	
			if (pageNode != null) {
				pageNode = pageNode.getParent();
				pagepath = pageNode.getPath();
			}
			}
		} catch (Exception e) {
			if (pagepath == null)
				log.error("Unable to search for active page ");
			e.printStackTrace();
		}
		log.info("page path::" + pagepath);
		log.debug("QueryExecutor->getPagePath() method ended");
		return pagepath;
	}
}
