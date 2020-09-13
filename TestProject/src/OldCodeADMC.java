import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;

public class OldCodeADMC {
	public static void main(String[] args) {

		/* For Copying a class file from virclipse to the admc server */

		String fileCopying = "NationalLatestMultimediaTeaser.class";
		copyClassFilesss(fileCopying);

		/* For Searching a string in all the action classes */

		String checkingFor = null;
		File root = new File(
				"C:/New Folder/trunk/Releases/SourceCode/Source/admc/AdmcWebApp/jar/src/main/java/com/admc/thenational");
		String[] extensions = { "java" };
		boolean recursive = true;
		searchFilesForstring(root, extensions, recursive, checkingFor);

		/* For Doing the RunTime operations like executing an exe file etc. */
		String commandToExecute = null;
		DoRunTimeOperations(commandToExecute);

		/* For Hitting a URL */

		// String url ="http://ht-admctest:8080/manager/html/reload?path=/thenational";
		String url = "http://ht-admctest:8080/portal/site/thenational/menuitem.4f9c10e1093f1a75827d924f359c71ca/?vgnextoid=030bf9fb4c647210VgnVCM10000053c917acRCRD";
		urlHitting(url+"&vgnextrefresh=1");

		/* Write your Stand Lone here */

		try {
			FileWriter file = new FileWriter("C:/jars/t1.txt");
			BufferedWriter out2 = new BufferedWriter(file);
			out2.write("aString");
			out2.close();

		} catch (IOException e) {
		}
	}

	public static void copyClassFilesss(String fileCopying) {
		try {
			if (fileCopying != null) {
				String Src = "C:/Verclipse_workspace/thenational_new/build/classes/com/admc/thenational/action/"
						+ fileCopying;
				String Dest = "//ht-admctest/install/Tomcat6/webapps/thenational/WEB-INF/classes/com/admc/thenational/action/"
						+ fileCopying;
				File f1 = new File(Src);
				File f2 = new File(Dest);

				FileWriter file = new FileWriter("C:/jars/reload.html");
				BufferedWriter out2 = new BufferedWriter(file);

				InputStream in = new FileInputStream(f1);
				int status = 401;
				// For Append the file.
				// OutputStream out = new FileOutputStream(f2,true);
				// For Overwrite the file.
				OutputStream out = new FileOutputStream(f2);
				byte[] buf = new byte[1024];
				int len;
				System.out.println("Copying the file" + fileCopying + "from "
						+ Src + "To" + Dest);
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				System.out.println("The file " + fileCopying + "copied from "
						+ Src + "To" + Dest);
				HttpClient client = new HttpClient();
				String username = "admin";
				String password = "admin";
				// Code for Authentication
				int portNum = 8080;
				String url = "http://ht-admctest:8080/manager/html/reload?path=/thenational";
				client.getState().setCredentials(
						new AuthScope("ht-admctest", portNum),
						new UsernamePasswordCredentials(username, password));
				GetMethod get = new GetMethod(url);
				get.setDoAuthentication(true);
				status = client.executeMethod(get);
				if (status != 401) {
					String resultString = get.getResponseBodyAsString();
					System.out
							.println("Done reloading the portal through the url-- "
									+ get.getURI());
					out2.write(resultString);
					out2.close();
					// System.out.println("Done reloading the portal through the
					// url"+get.getURI()+"result string is "+resultString);

				} else {
					System.out.println("unable to hit the url" + get.getURI());
				}
			} else {
				System.out
						.println("Not Executing the copyClassFiles function as the string passed was NULL");
			}

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void searchFilesForstring(File root, String[] extensions,
			boolean recursive, String checkingFor) {
		try {
			if (checkingFor != null) {
				Collection files = FileUtils.listFiles(root, extensions,
						recursive);
				for (Iterator iterator = files.iterator(); iterator.hasNext();) {
					File file = (File) iterator.next();
					// System.out.println("File = " + file.getAbsolutePath());
					BufferedReader bf = new BufferedReader(new FileReader(file
							.getAbsolutePath()));
					// Start a line count and declare a string to hold our
					// current line.
					int linecount = 0;
					String line;
					// System.out.println("Searching for " + checkingFor + " in
					// file..."+file.getAbsolutePath());
					// Loop through each line, stashing the line into our line
					// variable.
					while ((line = bf.readLine()) != null) {
						// Increment the count and find the index of the word
						linecount++;
						int indexfound = line.indexOf(checkingFor);

						// If greater than -1, means we found the word
						if (indexfound > -1) {
							// System.out.println("Word found "+ indexfound + "
							// on line " + linecount + "in the file
							// "+file.getName()+" in the line"+line);
							System.out.println(checkingFor + " was found in "
									+ file.getName() + " ---- " + line);
						}
					}
					// Close the file after done searching
					bf.close();
				}
			} else {
				System.out
						.println("Not Exectuting the searchFilesForstring as the String Passed was NULL");
			}
		} catch (IOException e) {
			System.out.println("IO Error Occurred: " + e.toString());
		}
	}

	public static void DoRunTimeOperations(String commandToExec) {

		if (commandToExec != null) {
			Runtime rt = Runtime.getRuntime();
			try {
				Process pr = rt.exec(commandToExec);
				BufferedReader input = new BufferedReader(
						new InputStreamReader(pr.getInputStream()));
				String line = null;
				while ((line = input.readLine()) != null) {
					System.out.println(line);
				}
				int exitVal;
				exitVal = pr.waitFor();
				System.out.println("Exited with error code " + exitVal);

			} catch (IOException e) {
				// TODO Auto-generated catch block

				System.out.println(e.toString());
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			System.out
					.println("Not Excuting the DoRunTimeOperations as the String passed was NULL");
		}
	}

	public static void urlHitting(String urlToHit) {

		HttpClient client1 = new HttpClient();
		String urlOn = urlToHit;
		String urlOff = urlToHit + "&vgnextnoice=1";
		GetMethod getOn = new GetMethod(urlOn);
		GetMethod getOff = new GetMethod(urlOff);
		
		try {
			File editinOn = new File(
					"C:/Documents and Settings/santoshv/Desktop/San-JAVA/outPut/forUrlHitting/editinOn.html");
			File editinOff = new File(
					"C:/Documents and Settings/santoshv/Desktop/San-JAVA/outPut/forUrlHitting/editinOff.html");
			OutputStream onOut = new FileOutputStream(editinOn);
			OutputStream offOut = new FileOutputStream(editinOff);
			byte[] bufOn = new byte[1024];
			byte[] bufOff = new byte[1024];
			
			int len1;
			System.out.println("clearing Management Cache--");
			int statusOn = client1.executeMethod(getOn);
			System.out.println("clearing Management Cache--Done");
			int statusOff = client1.executeMethod(getOff);
			if (statusOn != 401) {
				String resultStringOn;
				resultStringOn = getOn.getResponseBodyAsString();
				bufOn = resultStringOn.getBytes();
				onOut.write(bufOn);
				onOut.flush();
			}
			if (statusOff != 401) {
				String resultStringOff;
				resultStringOff = getOff.getResponseBodyAsString();
				bufOff = resultStringOff.getBytes();
				onOut.write(bufOff);
				onOut.flush();
			}

			/*For Reaplacing the string in a File and the File Name as well
			 * 
			 * String match = "/thenational/"; String replacingString =
			 * "./thenational"; File file = new File("C:/jars/test1.html");
			 * //file.delete(); String fileToDelete =
			 * "national_art_culture_conversation.jsp"; System.out.println("b4
			 * replaacing the file name is --"+fileToDelete); String a =
			 * fileToDelete.replaceAll("_" , "_005");
			 * 
			 * String b = a.replaceFirst(".jsp", "_jsp.java");
			 * System.out.println("after replaacing the file name is --"+a+"---
			 * after next deletion "+ b);
			 */
			// String fileToDeletFP =
			// "//ht-admctest/install/Tomcat6/work/Catalina/localhost/thenational/org/apache/jsp/overrides"+fileToDelete;
			/*
			 * RandomAccessFile raf = new RandomAccessFile(file, "rw"); long
			 * pointer = raf.getFilePointer(); String lineData = "";
			 * while((lineData =raf.readLine()) != null){ pointer =
			 * raf.getFilePointer() - lineData.length()-2;
			 * if(lineData.indexOf(match) > 0){ //System.out.println("Changing
			 * string in file "+file); raf.seek(pointer);
			 * raf.writeBytes(replacingString); }
			 */

			// if the replacingString has less number of characters than the
			// matching string line then enter blank spaces.
			/*
			 * if(replacingString.length() < lineData.length()){ int difference =
			 * (lineData.length() - replacingString.length())+1; for(int i=0; i
			 * raf.writeBytes(" ");
			 */
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}

/*******************************************************************************
 * 
 * 
 * 
 * //URL url = new
 * URL("http://t1.gstatic.com/images?q=tbn:8xw9UtkjAscUqM:http://blogs.targetx.com/pbu/Trevor/Nature_Mountains.jpg");
 * //url.openConnection(); //System.out.println("opening the url
 * "+url.getFile()); //System.out.println("opening the url
 * "+url.openConnection()); //URLConnection urlConnection = null; FileReader
 * classsFile = new
 * FileReader("C:/Verclipse_workspace/Custom-Sec-pages/build/classes/com/admc/vignette/thenational/secondarypages/loginTheNationalPreDisplayLoginAction.class");
 * FileCopyUtils copyCmds = new FileCopyUtils();
 * 
 * FileWriter fstream = new FileWriter("C:/out1.txt"); BufferedWriter out = new
 * BufferedWriter(fstream); out.write("testtttttt"); out.append("Hello Java");
 * out.flush();
 ******************************************************************************/
// HttpState state = new HttpState();
// state.setCredentials(null, "hd-bsujatha", new
// UsernamePasswordCredentials(username, password));
// HttpConnection conn = new HttpConnection("http://hd-bsujatha",portNum);
// conn.open();
// get.execute(state,conn);
// System.out.println("executing the get.execute"+get.execute(state,conn));

//URL url = new URL("http://hd-bsujatha:9999/manager/html/reload?path=/portal");
//		url.openConnection();
//url.openStream();
//	System.out.println("hit the url  " + url);