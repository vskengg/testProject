import java.text.ParseException;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;

import org.apache.jackrabbit.commons.JcrUtils;

public class VersionEditTest {
	public static void main(String[] args) throws ParseException {
		String repoUrl = "http://localhost:4502/crx/server";
		String userName = "admin";
		char[] password = "admin".toCharArray();
		try {
			Repository repository = JcrUtils.getRepository(repoUrl);
			Session session = repository.login(new SimpleCredentials(userName,
					password), "crx.default");
			VersionManager versionManager = session.getWorkspace()
					.getVersionManager();
			String nodePath = "/content/en/us/td/docs/net_mgmt/7000_series_manager/2.0/installation/FourthImport/LinkBook/overview/jcr:content";
			Node node = session.getNode(nodePath);
			Version v1 = versionManager.checkin(nodePath);
			if(!node.isCheckedOut()){
				System.out.println("node is not checked out so checking out.!!");
				versionManager.checkout(nodePath);
			}
			node.setProperty("jcr:title", "test title");
			//Version v1 = versionManager.getBaseVersion(nodePath);
			System.out.println("base version path is :" + v1.getPath());
			//v1.getFrozenNode().setProperty("cq:tags", "test");
			session.save();
		} catch (RepositoryException e) {
			System.out.println("Repositroy Exception :" + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception :" + e.getMessage());
		}
	}
}
