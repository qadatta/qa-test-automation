package com.automation.cucumber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


// From xml beans
public class ZipCompare {
	 
	public void compareZipFiles(String sourceZipFile, String  destinationZipFile){
//    if (args.length != 2) {
//      System.out.println("Usage: zipcompare [file1] [file2]");
//      System.exit(1);
//    }

	  //String [] args1 = {"C:/project/FM/source_file.zip","C:/project/FM/custom-download-destination-s3-file.zip"};
//		sourceZipFile = "C:/project/FM/source_file.zip";
//		destinationZipFile = "C:/project/FM/custom-download-destination-s3-file.zip";

    ZipFile file1;
    try {
      file1 = new ZipFile(sourceZipFile);
    } catch (IOException e) {
      System.out.println("Could not open zip file " + sourceZipFile + ": " + e);
      System.exit(1);
      return;
    }

    ZipFile file2;
    try {
      file2 = new ZipFile(destinationZipFile);
    } catch (IOException e) {
      System.out.println("Could not open zip file " + destinationZipFile + ": " + e);
      System.exit(1);
      return;
    }

    System.out.println("Comparing " + sourceZipFile + " with " + destinationZipFile + ":");
    
    
    
    System.out.println("Following files present in "+ sourceZipFile + " and total file count is: "+ file1.size());
    Set set1 = new LinkedHashSet();
    for (Enumeration e = file1.entries(); e.hasMoreElements();)
    {
    	String fileName = ((ZipEntry) e.nextElement()).getName();
    	System.out.println(fileName);
    	set1.add(fileName);
    }


    System.out.println("Following files present in "+ destinationZipFile + " and total file count is: "+ file2.size());
    Set set2 = new LinkedHashSet();
    for (Enumeration e = file2.entries(); e.hasMoreElements();)
    {
    	String fileName = ((ZipEntry) e.nextElement()).getName();
    	System.out.println(fileName);
    	set2.add(fileName);
    	
    }
	System.out.println("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");

    int errcount = 0;
    int filecount = 0;
    for (Iterator i = set1.iterator(); i.hasNext();) {

      String name = (String) i.next();
      if (!set2.contains(name)) {
        System.out.println("File: " + name + " not found in " + destinationZipFile);
        errcount += 1;
        continue;
      }
      try {
        set2.remove(name);
        ZipEntry csv_file_from_File1 = file1.getEntry(name);
        ZipEntry csv_file_from_File2 = file2.getEntry(name);
        
        InputStream stream1 = file1.getInputStream(csv_file_from_File1);
        InputStream stream2 = file2.getInputStream(csv_file_from_File2);
        
				if (!streamsEqual(stream1, stream2)) {
					stream1 = file1.getInputStream(file1.getEntry(name));
			        stream2 = file2.getInputStream(file2.getEntry(name));
			        			System.out.println("File: "+ name + " does not match with destination");
			        			System.out.println("Source file size is "+ csv_file_from_File1.getSize()/1024 + " KB and destination file size is " + csv_file_from_File2.getSize()/1024 +" KB") ;
				
					getDifferenceInFiles(stream1, stream2);
					System.out.println("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");

					errcount += 1;
					continue;
				}else
				{
        			System.out.println("File: "+ name + " matches with destination and file size is " + csv_file_from_File1.getSize()/1024 +" KB" );
					System.out.println("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");
				}
      } catch (Exception e) {
        System.out.println(name + ": IO Error " + e);
        e.printStackTrace();
        errcount += 1;
        continue;
      }
      filecount += 1;
    }
    for (Iterator i = set2.iterator(); i.hasNext();) {
      String name = (String) i.next();
      System.out.println(name + " not found in " + sourceZipFile);
      errcount += 1;
    }
    }

  private static boolean compareFileSize(ZipFile file1,ZipFile file2) {
	
	  if(file1.size()==file2.size())
		return true;
	  else
		  return false;
	  
}

  static boolean getDifferenceInFiles(InputStream stream1, InputStream stream2)
 {
		boolean filesEquals = false;

		BufferedReader bfReader1 = new BufferedReader(new InputStreamReader(stream1));
		BufferedReader bfReader2 = new BufferedReader(new InputStreamReader(stream2));

		String file1Line = null;
		String file2Line = null;
		int lineCounter = 0;
		try {
			while ((file1Line = bfReader1.readLine()) != null) {
				++lineCounter ;
				file2Line = bfReader2.readLine();

				if (false == file1Line.equals(file2Line)) {
					System.out.println("\n ---------Difference on line number: "+ lineCounter +" ----------------");
					System.out.println("File 1 line: "+ lineCounter +" not matched \n" + file1Line);
					System.out.println("File 2 line: "+ lineCounter +" not matched \n" + file2Line);
					System.out.println();
					String[] file1Columns = file1Line.split("\\|");
					String[] file2Columns = file2Line.split("\\|");
					System.out.println("Following column data not matched");
					
				
					for (int i = 0; i < file1Columns.length; i++) {
						try {
							if (false == file1Columns[i].equals(file2Columns[i])) {
								System.out.println("Column number "+ i + "==> "+ file1Columns[i] + " <<>> "+ file2Columns[i] );
							}
						} catch (Exception e) {
							System.out.println("Line column count for source and destination file does not match");
						}
					
					}
				
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return filesEquals;
	}
  
  
static boolean streamsEqual(InputStream stream1, InputStream stream2) throws IOException {
    byte[] buf1 = new byte[4096];
    byte[] buf2 = new byte[4096];
    boolean done1 = false;
    boolean done2 = false;

    try {
      while (!done1) {
        int off1 = 0;
        int off2 = 0;

        while (off1 < buf1.length) {
          int count = stream1.read(buf1, off1, buf1.length - off1);
          if (count < 0) {
            done1 = true;
            break;
          }
          off1 += count;
        }
        while (off2 < buf2.length) {
          int count = stream2.read(buf2, off2, buf2.length - off2);
          if (count < 0) {
            done2 = true;
            break;
          }
          off2 += count;
        }
        if (off1 != off2 || done1 != done2)
          return false;
        for (int i = 0; i < off1; i++) {
          if (buf1[i] != buf2[i])
            return false;
        }
      }
      return true;
    } finally {
      stream1.close();
      stream2.close();
    }
  }
}
