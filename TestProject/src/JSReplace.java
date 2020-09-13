import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class JSReplace {
	public static int count = 0;
	static PrintWriter writer;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		writer = new PrintWriter("C:\\Users\\sa333486\\Desktop\\ATCO_Core\\js.txt", "UTF-8");
		final File folder = new File("D:\\Builds\\13 Oct\\development\\atco\\ui.apps\\src\\main\\content");
		listFilesForFolder(folder);
	}

	public static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith(".js")) {
					getRequiredText(fileEntry.getPath());
				}
			}
		}
	}

	public static void getRequiredText(String file) {
		int i = 0;
		boolean flag = false;
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
				String s = line;
				if (s != null) {
					if (!s.contains("===")) {
						if (s.contains("==")) {
							System.out.println("---> " + s);
						}
					}
				}
				i++;
			}
			if (flag) {
				count++;
				System.out.println(
						"============================================================================================================================================================");
				System.out.println(count + ")" + file);
				System.out.println(
						"============================================================================================================================================================");
			}
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
