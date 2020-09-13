import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.googlecode.sardine.util.SardineException;


public class Sardinetest {

	final static  String WEBDAV_URL = "http://localhost:4502/crx/repository/crx.default/var/dam/ciscopoc/docs/net_mgmt/7000_series_manager/2.0/installation/SecondImport/";
	public Sardinetest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws SardineException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws SardineException, FileNotFoundException {
		
		Sardinetest sat=new Sardinetest();
		sat.copyReferenceOnHost(WEBDAV_URL);
	}
	
	public void copyReferenceOnHost(String webDavURL)throws SardineException, FileNotFoundException{
		 try{// TODO Auto-generated method stub
				
				Sardine sardine = SardineFactory.begin("admin","admin"); 
				List<DavResource> resources = sardine.getResources(WEBDAV_URL); 
				for (DavResource res : resources)
				{ 
					System.out.println(res);
					System.out.println(res.getName());
					System.out.println(res.isDirectory());
					System.out.println("ABS ----------"+res.getAbsoluteUrl());
					System.out.println("BASE -----"+res.getBaseUrl());
					if(!res.isDirectory()){
					// calls the .toString() method. }
					InputStream is=sardine.getInputStream(res.getAbsoluteUrl());
					File file =new File("C:\\Target\\"+res.getName());
					OutputStream os = new FileOutputStream(file);
					 byte buf[]=new byte[1024];
			           int len;
			           while((len=is.read(buf))>0)
			           os.write(buf,0,len);
			           os.close();
			           is.close();
			           System.out.println("\nFile is created...................................");
					}
					else{
						System.out.println("Source is folder");
						System.out.println("----else--- ABS"+res.getAbsoluteUrl());
						System.out.println("----else---Folder Name ---"+res.getName());
						File file =new File("C:\\Target\\"+res.getName());
						if(!file.exists()){
							file.mkdir();
						}
					}
					}
				}
				catch(IOException ioe){
					
				}
	}

}
