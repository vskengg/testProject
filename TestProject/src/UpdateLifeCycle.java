import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

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

public class UpdateLifeCycle {
	public static void main(String[] args) {
		Session session = getSession("http://wemapp-author-prod1-01:4502/crx/server",
				"wemmigadm.gen", "wemmigadm.gen".toCharArray());
		String fileName = "eol_concepts_1to50.txt";
		String filePath = "C:\\Users\\sanvadak\\Desktop\\WIP\\LifeCycleEvents\\EOL_Concepts\\"+fileName ; //eol_concepts_1to50.txt";
		// C:\Users\sanvadak\Desktop\WIP\LifeCycleEvents\EOL_Concepts\eol_concepts_1to50.txt
		FileWriter writer = null ;
		try {
			 writer = new FileWriter("C:\\Users\\sanvadak\\Desktop\\WIP\\LifeCycleEvents\\EOL_Concepts\\report_"+fileName ,true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		BufferedReader br = null;
		String tempId = null;
		ArrayList<String> conceptIds = new ArrayList<String>();
		ArrayList<String> lcStates = new ArrayList<String>();
		ArrayList<String> statusInfo = new ArrayList<String>();
		String status = null;
		try {
			QueryManager queryManager = session.getWorkspace()
					.getQueryManager();
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
				status = "";
				System.out.println("Enter 'Y' to proceed: ");
				response = keys.nextLine();
				if (response.equalsIgnoreCase("Y")) {
					System.out.println("i is :" + i);
					System.out.println("processing for the concept :"
							+ conceptIds.get(i));
					tempId = conceptIds.get(i);
					String queryForConcept = "/jcr:root/etc/tags//element(*,cq:Tag)[@conceptId='"
							+ tempId + "'] order by @jcr:score";
					status = tempId + "|";
					System.out.println("queryForConcept is : "
							+ queryForConcept);
					Query query = queryManager.createQuery(queryForConcept,
							Query.XPATH);
					QueryResult result = query.execute();
					NodeIterator nodeIterator = result.getNodes();
					System.out.println("size of the iterator is : "
							+ nodeIterator.getSize());
					if (nodeIterator.getSize() == 1 && nodeIterator.hasNext()) {
						Node currentNode = (Node) nodeIterator.next();
						System.out.println("currentNode is : "
								+ currentNode.getPath());
						status = status + currentNode.getPath() + "|";
						if (currentNode.hasProperty("lifeCycle")) {
							String currentLC = currentNode.getProperty(
									"lifeCycle").getString();
							System.out
									.println("current LC that node having is : "
											+ currentLC);
							System.out
									.println("lcs that we are going to set is :"
											+ lcStates.get(i));
							if (null != currentLC
									&& currentLC.equalsIgnoreCase(lcStates
											.get(i))) {
								System.out
										.println("setting LifeCycle property :: "+lcStates.get(i));
								currentNode.setProperty("lifeCycle",
										lcStates.get(i));
								session.save();
								status = status + "SUCESS";
							} else {
								System.out
										.println("provided lifecycle in the list is not updated to the node so not processing the node "
												+ currentNode.getPath());
								status = status + "NOT MATCHED";
							}
						}
					} else {
						if(nodeIterator.getSize() > 1){
						System.out
								.println("exiting the process as the conceptID "
										+ tempId
										+ " is not unique and assigned to more than one concept");
						status = status + "DUPLICATE";
						}if(nodeIterator.getSize() < 1){
							System.out
							.println("exiting the process as the conceptID "
									+ tempId
									+ " is not having any concept");
							status = status + "UNAVAILABLE";
						}
					}
				}else{
					break ;
				}
				statusInfo.add(status);
			}
		} catch (Exception e) {
			System.err.println("IOException caught");
			status = status + e.getMessage();
			e.printStackTrace();
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
			}finally{
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

	public static Session getSession(String repoUrl, String userName,
			char[] password) {
		Session session = null;
		try {
			System.out.println("============== started ==============");
			Repository repository = JcrUtils.getRepository(repoUrl);
			System.out.println("logging in");
			session = repository.login(
					new SimpleCredentials(userName, password), "crx.default");
		} catch (RepositoryException e) {
			System.out.println("Repositroy Exception :" + e.getMessage());
		}
		return session;

	}

}


//active_concepts_301to350_prod,active_concepts_251to300_prod,active_concepts_201to250_prod,active_concepts_151to200_prod,active_concepts_101to150_prod,active_concepts_51to100_prod