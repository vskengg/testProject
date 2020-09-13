import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.jcr.PathNotFoundException;

public class RemoveHardCodeUrls {
	public static int fileCount = 1;

	public static void main(String[] args) throws FileNotFoundException {
		try {
			System.out.println(
					"==========================================================STARTED==================================================================================================");
			final File folder = new File("D:\\ATCO_Refactored\\Refactor-Merge2\\atco");
			listFilesForFolder(folder);
			System.out.println(
					"==========================================================COMPLETED==================================================================================================");
			System.out.close();
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void listFilesForFolder(final File folder) throws PathNotFoundException {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith(".java")) {
					findRequiredText(fileEntry.getPath());
				}
			}
		}
	}

	public static void findRequiredText(String file) throws PathNotFoundException {
		int lineNo = 0;
		int currentFileNo = 0;
		boolean sameFileFlag = true;

		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String s = line;
				if (s != null) {
					if (s.indexOf("/content/") != -1) {
						if (sameFileFlag) {
							System.out.println("\n");
							System.out.println(
									"==================================================================================================================================================================");
							System.out.println("file # " + fileCount + "  : " + file);
							System.out.println(
									"==================================================================================================================================================================");
							sameFileFlag = false;
							fileCount++;
						}
						String text = s.substring(s.indexOf("/content/"));
						System.out.println("line # " + lineNo + " : " + s);
					}
				}
				lineNo++;
			}
			fileReader.close();
		} catch (Exception e) {
			System.out.println("error caught");
		}
	}

}
