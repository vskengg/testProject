import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class TestClass {
	public static int count = 0;

	public static void main(String[] args) {
		final File folder = new File("D:\\Builds\\13 Oct\\development\\atco\\ui.apps\\src\\main\\content");
		listFilesForFolder(folder);
	}

	public static void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().endsWith(".html")) {
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
					if (s.length() > s.indexOf(">") + 1) {
						s = s.substring(s.indexOf(">") + 1);
						if (s.indexOf("<") > 0) {
							s = s.substring(0, s.indexOf("<"));
							String text = s;
							if ((!text.contains("$")) && !text.trim().equalsIgnoreCase("")) {
								System.out.println(text);
								System.out.println(text.replace(" ","."));
								flag = true;
								text = "${'" + text + "' @i18n} ";
								System.out.println(text + "\n");
							}
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
