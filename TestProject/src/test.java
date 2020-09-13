import java.io.File;


public class test {
	public static void main(String[] args){
		String result = null;
		String test = "/content/dam/en/us/td/docs/net_mgmt/7000_series_manager/2.0/installation/AdminSite/AdminSite.book";
		//result = test.replace("/dam/","").replace(".book","");
		result = test.replace("/dam","");
		result = result.substring(0,result.lastIndexOf("."));
		//System.out.println("===>"+result);
		
		File folder = new File("C:/Users/sanvadak/Desktop/test site/index.aspx_files");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	if(file.toString().endsWith(".jpg"))
		        System.out.println(file.getName());
		    }
		}
		
		
		
		
		/*int ONE = 1;
		
		String eoldDocType = "Test Series";
		String currentNodePath = "/etc/tags/Doc Types/HOME/SUPPORT/FF-netsol-category";
		
		String parentNodePath  = currentNodePath.substring(0,currentNodePath.lastIndexOf("/")+ONE);
		String eolDocTypeString = parentNodePath+eoldDocType;
		System.out.println("temp is :"+eolDocTypeString);
		
		String authorLink = "http://localhost:4502/c/dam/en/us/td/docs/routers/7400/install__config/7401icg.book";
		String newLink = null ;
		if(authorLink.contains("/content/dam/"))
			newLink = authorLink.replace("/content/dam/", "/damadmin#/content/dam/");
		else if(authorLink.contains("/c/dam/"))
			newLink = authorLink.replace("/c/dam/", "/damadmin#/content/dam/");
		System.out.println("new link is "+newLink);
		
		String resPath = "/content/en/us/td/t12/t_24/jcr:content";
		if(resPath.contains("jcr:content"))
			resPath = resPath.substring(0,resPath.indexOf("jcr:content")-1);
		
		System.out.println("resPath is ---> "+resPath);*/

	}
}

		
		























/*		String host = null ;
		String assetPath = null;
		
		if(authorLink.contains("/content/dam/")){
		 host  = authorLink.substring(0,authorLink.indexOf("/content/dam/"));
		assetPath = authorLink.substring(authorLink.indexOf("/content/dam/"));
		}else if(authorLink.contains("/c/dam/")){
			host  = authorLink.substring(0,authorLink.indexOf("/c/dam/"));
			assetPath = authorLink.substring(authorLink.indexOf("/c/dam/"));
			assetPath = assetPath.replace("/c/dam/", "/content/dam/");
			System.out.println("asset path is "+assetPath);
		}
			
 
		assetPath = "/damadmin#"+assetPath ;
		String newPath = host+assetPath ;
		
				//System.out.println(assetPath);
		System.out.println("newPath is "+newPath);
*/		
		


