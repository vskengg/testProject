package com.cisco.standalone.conceptlc;

import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krevoori
 * 
 */
public class CRXSessionHelper {
	private static final Logger log = LoggerFactory.getLogger(CRXSessionHelper.class);

	/**
	 * It returns session object for crx repository
	 * 
	 * @param repository
	 * @return
	 * @throws Exception
	 */


	/**
	 * It will logout crx session
	 * 
	 * @param session
	 */
	public static void closeCRXSession(Session session) {
		try {

			if (session != null)
				session.logout();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
