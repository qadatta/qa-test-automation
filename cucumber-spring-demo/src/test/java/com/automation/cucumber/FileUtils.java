package com.automation.cucumber;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import static org.hamcrest.MatcherAssert.assertThat;

import io.cucumber.java.Scenario;

public class FileUtils {

	
	Scenario scenario;

	public FileUtils(Scenario scenario) {
		this.scenario = scenario;
	}
	
	/**
	 * This method will download file from http get request to provided file
	 * path
	 * 
	 * @param url
	 *            http get url to downloa file
	 * @param savedFile
	 *            download file path
	 */
	public void downloadFileusingHttpGetRequest(String url, String savedFile) {
		System.out.println("File name: "+savedFile);
		HttpClient client = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				try (FileOutputStream outstream = new FileOutputStream(savedFile)) {
					entity.writeTo(outstream);
				} catch (IOException e) {
					System.out.println("problem in downloainf file ");

					System.out.println(e.getMessage());
				}
			}

		} catch (IOException e) {
			System.out.println("problem in downloainf file ");

			e.printStackTrace();
		}
	}

	/**
	 * Get filename set from zip file
	 * @param zipFile
	 * @return set of file names in zip file
	 */
	public Set<String> getZipFileNames(ZipFile zipFile) {
		Set<String> fileNameSet = new LinkedHashSet<String>();
		for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
			String fileName = ((ZipEntry) e.nextElement()).getName();
			System.out.println(fileName);
			fileNameSet.add(fileName);
		}

		return fileNameSet;
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
	
	boolean getDifferenceInFiles(InputStream stream1, InputStream stream2) {
		boolean filesEquals = true;

		BufferedReader bfReader1 = new BufferedReader(new InputStreamReader(stream1));
		BufferedReader bfReader2 = new BufferedReader(new InputStreamReader(stream2));

		String file1Line = null;
		String file2Line = null;
		int lineCounter = 0;
		try {
			scenario.write("<font size='3' color='red'> Actual and expected files not matched. Test FAIL </font>");

			while ((file1Line = bfReader1.readLine()) != null) {
				++lineCounter;
				file2Line = bfReader2.readLine();

				if (false == file1Line.equals(file2Line)) {
					
					ArrayList<String> lineDifference = getDifferenceOfCSVLine(file1Line, file2Line, "\\|");
					filesEquals = false;
					System.out.println("Line not matched. Line Number: " + lineCounter);
					scenario.write("Line not matched. Line Number: " + lineCounter + "\n");
					for (Iterator iterator = lineDifference.iterator(); iterator.hasNext();) {
						String difference = (String) iterator.next();
						System.out.println(difference);
						scenario.write("\n"+ difference);
					}
				}	
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return filesEquals;
	}
	
	/**
	 * Get CSV file line difference
	 * @param file1Line
	 * @param file2Line
	 * @param delimeter
	 * @return
	 */
	public ArrayList<String> getDifferenceOfCSVLine(String file1Line, String file2Line, String delimeter)
	{
		ArrayList<String> differences = new ArrayList<String>();

		String[] file1Columns = file1Line.split("\\|");
		String[] file2Columns = file2Line.split("\\|");

		for (int i = 0; i < file1Columns.length; i++) {
			try {
				if (false == file1Columns[i].equals(file2Columns[i])) {
					differences.add("Column number " + i + "==> Actual value: " + file1Columns[i] + "  and expected value: " + file2Columns[i]);
				}
			} catch (Exception e) {
				System.out.println("Line column count for source and destination file does not match");
			}

		}
		return differences;

	}
	
	/**
	 * Compare zipentry in zip files
	 * @param actualZip
	 * @param expectedZip
	 * @param actualZipEntry
	 * @param expectedZipEntry
	 * @return
	 */
	public boolean compareZipEntry(ZipFile actualZip , ZipFile expectedZip , ZipEntry actualZipEntry,ZipEntry expectedZipEntry)
	{
		boolean isZipEntryMatched = false;
		try {
			scenario.write("Comparing file "+ actualZipEntry.getName());
			InputStream expectedStream = expectedZip.getInputStream(expectedZipEntry);
			InputStream actualStream = actualZip.getInputStream(actualZipEntry);
			if (!streamsEqual(actualStream,expectedStream)) {
				expectedStream = expectedZip.getInputStream(expectedZipEntry);
				actualStream = actualZip.getInputStream(actualZipEntry);

				getDifferenceInFiles(actualStream,expectedStream);

			} else {
				isZipEntryMatched = true;
				scenario.write("<font size='3' color='green'> Actual and expected files matched. Test PASS </font>");
			}
			
		} catch (IOException e) {
			System.out.println("Problem in reading zip Files");
			e.printStackTrace();
		}

		return isZipEntryMatched;
	}
	
	public void compareFileNamesPresentInZipFiles(ZipFile actualZip , ZipFile expectedZip) {

		

		System.out.println("Comparing " + getAbsuluteZipFileName(actualZip)+ " with \n " + getAbsuluteZipFileName(expectedZip) + ":");
		scenario.write("<b>Comparing files names present in " + getAbsuluteZipFileName(actualZip) + " with \n file names present in " + getAbsuluteZipFileName(expectedZip) + "</b> \n");
		scenario.write("<b>Following files present in " + getAbsuluteZipFileName(actualZip) + ". </b>");

		System.out.println("Following files " + actualZip.size() + "present in " + getAbsuluteZipFileName(actualZip) + " and total file count is: " + actualZip.size());
		Set<String> set1 = getZipFileNames(actualZip);
		for (Iterator<String> iterator = set1.iterator(); iterator.hasNext();) {
			String fileName = (String) iterator.next();
			scenario.write(fileName + "\n");
			
		}
		
		scenario.write("<b>Following files " + expectedZip.size() + "present in " + getAbsuluteZipFileName(expectedZip) + ". </b>");
		System.out.println("Following files "+ expectedZip.size() +" present in " + getAbsuluteZipFileName(expectedZip) + " and total file count is: " + expectedZip.size());
		Set<String> set2 = getZipFileNames(expectedZip);
		for (Iterator<String> iterator = set2.iterator(); iterator.hasNext();) {
			String fileName = (String) iterator.next();
			scenario.write(fileName + "\n");
		}
		
		set1.removeAll(set2);
		set2.removeAll(set1);
		
			assertThat("File present in both zip are not matches", set1.isEmpty() && set2.isEmpty());
		
	}
	
	public void compareFileSizeOfZipFiles(ZipFile actualZip , ZipFile expectedZip) {


		System.out.println("Comparing file size of " + actualZip.getName() + " with \n " + expectedZip.getName() + ":");
		scenario.write("<b>Comparing file size of " + getAbsuluteZipFileName(actualZip) + " with \n" + getAbsuluteZipFileName(expectedZip) + "</b> \n");

		int file1Size = 0;
		for (Enumeration e = expectedZip.entries(); e.hasMoreElements();) {
			ZipEntry csvFile = ((ZipEntry) e.nextElement());
			file1Size += csvFile.getSize();
		}

		int file2Size = 0;
		for (Enumeration e = actualZip.entries(); e.hasMoreElements();) {
			ZipEntry csvFile = ((ZipEntry) e.nextElement());
			file2Size += csvFile.getSize();
		}
		scenario.write("File size of "+  getAbsuluteZipFileName(expectedZip) + " : "+ file1Size + " Bytes");
		scenario.write("File size of "+  getAbsuluteZipFileName(actualZip)+ " : "+ file2Size + " Bytes");

//		assertThat("File size not matches", file1Size==file2Size);
		
	}
	
	public void compareFileCountsOfZipFiles(ZipFile actualZip , ZipFile expectedZip) {

		scenario.write("<b> Comparing " + getAbsuluteZipFileName(actualZip) + " with \n" + getAbsuluteZipFileName(expectedZip)+ " file counts <b>\n");
		scenario.write("Total files present in " + getAbsuluteZipFileName(actualZip) + " are: " + actualZip.size());
		scenario.write("Total files present in " + getAbsuluteZipFileName(expectedZip) + " are: " + expectedZip.size());
		assertThat("File count not matches", actualZip.size() == expectedZip.size());
	}
	
	public String getAbsuluteZipFileName(ZipFile zipFile)
	{
		
		String name = zipFile.getName();
		System.out.println(name);
		name = name.substring(name.lastIndexOf(System.getProperty("file.separator"))+1);
		System.out.println(name);
		return name;
	}
	
	
	public void compareZipFilesContent( ZipFile actualFile, ZipFile expectedFile) {

		boolean fileMatches = true;
		FileUtils fileUtils = new FileUtils(scenario);

		System.out.println("Comparing " + fileUtils.getAbsuluteZipFileName(expectedFile) + " with \n " + getAbsuluteZipFileName(actualFile)+ ":");
		
		scenario.write("Comparing " + fileUtils.getAbsuluteZipFileName(expectedFile) + " with  " + getAbsuluteZipFileName(actualFile)+ " for contets of individual files");

		Set<String> set1 = fileUtils.getZipFileNames(expectedFile);
		Set<String> set2 = fileUtils.getZipFileNames(actualFile);

		for (Iterator i = set1.iterator(); i.hasNext();) {

			String name = (String) i.next();
			if (!set2.contains(name)) {
				System.out.println("File: " + name + " not found in " + getAbsuluteZipFileName(actualFile));
				scenario.write("File: " + name + " not found in " + getAbsuluteZipFileName(actualFile));
				continue;
			}
			try {
				set2.remove(name);
				ZipEntry expectedZipEntry = expectedFile.getEntry(name);
				ZipEntry actualZipEntry = actualFile.getEntry(name);

				if(false == fileUtils.compareZipEntry(actualFile, expectedFile, actualZipEntry, expectedZipEntry))
				{
					fileMatches = false;
					continue;
				}
				
			} catch (Exception e) {
				System.out.println(name + ": IO Error " + e);
				fileMatches = false;

				e.printStackTrace();
				continue;
			}
		}
		for (Iterator i = set2.iterator(); i.hasNext();) {
			String name = (String) i.next();
			System.out.println(name + " not found in " + fileUtils.getAbsuluteZipFileName(expectedFile));
			scenario.write(name + " not found in " + fileUtils.getAbsuluteZipFileName(expectedFile));
		}

		System.out.println("fileMatches>>>"+fileMatches);
		if (false == fileMatches) {
			assertThat("S3_CAS_custom-download_response.zip and CAS_custom-donwload_expected.zip does not match",
					false);
		}
	}


}
