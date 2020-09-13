import java.io.File;



public class GetFilesList 
{
 
 public static void main(String[] args) 
{
 
  // Directory path here
  String path = "C:/Users/sanvadak/Desktop/Trg/Cisco Systems, Inc - Cisco_files"; 
 
  String files;
  File folder = new File(path);
  File[] listOfFiles = folder.listFiles(); 
 
  for (int i = 0; i < listOfFiles.length; i++) 
  {
 
   if (listOfFiles[i].isFile()) 
   {
   files = listOfFiles[i].getName();
   if(files.endsWith(".css"))
   System.out.println(files);
      }
  }
}
}