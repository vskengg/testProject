import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class TestString {

	public static void main(String[] args) {
		getLaterDate(60);
		String tag = "/etc/tags/Products/Cisco Products/Flip Video";
		tag = tag.substring("/etc/tags/".length()).replaceFirst("/", ":");
		System.out.println("tag replaced is : "+tag);
		
		String[] tagsList = { "Products:Cisco Products", "Products:Cisco Products/Flip Video",
				"NetworkingSolutions:Data Center and Virtualization",
				"Applications:NetworkingSolutions/Data Center and Virtualization","DocTypes:NetworkingSolutions/Data Center and Virtualization" };
		ArrayList<String> conceptList = new ArrayList<String>();
		for (int i = 0; i < tagsList.length; i++) {
			String temp = tagsList[i];
			temp = "/etc/tags/" + temp.replace(":", "/");
			if (isValidConcept(temp)) {
				conceptList.add(temp);
				System.out.println("valid concept is : "+temp);
			}
		}
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
	
	public static Date getLaterDate(int days) {
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, days);
		dt = c.getTime();
		System.out.println("date is : "+dt);
		return dt;
	}
}

/*
 * String rootPath = "/etc/tags/Products/"; String tag =
 * "Products:Routers/Branch Routers"; String ETC_TAGS = "/etc/tags/"; String
 * nameSpace = rootPath.substring(ETC_TAGS.length()); nameSpace =
 * nameSpace.replace("/", ":"); System.out.println("nameSpace is "+nameSpace);
 */