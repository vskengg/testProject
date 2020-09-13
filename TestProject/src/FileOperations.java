import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class FileOperations {

	public static void main(String[] args) {
		 
		BufferedReader br = null;
		BufferedReader br1 = null;
 
		try {
 
			String sCurrentLine;
			String compString = "it.hasNext() is :";
 
			br = new BufferedReader(new FileReader("C:\\Adobe\\cq\\author\\crx-quickstart\\logs\\concepterror.log"));
			br1 = new BufferedReader(new FileReader("C:\\Adobe\\cq\\author\\crx-quickstart\\logs\\concepterror.log"));
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.indexOf(compString) != -1){
							System.out.println(sCurrentLine);
				}
			}
			/*System.out.println("Products pages are :");
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.indexOf(compString) != -1){
					if(sCurrentLine.indexOf("/us/products/routers") != -1)
							System.out.println(sCurrentLine);
				}
			}
			System.out.println("=================================================");
			System.out.println("Support Pages are : ");
			while ((sCurrentLine = br1.readLine()) != null) {
				if(sCurrentLine.indexOf(compString) != -1){
					if(sCurrentLine.indexOf("/us/support/routers") != -1)
							System.out.println(sCurrentLine);
				}
			}*/
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
 
	}
	
}
