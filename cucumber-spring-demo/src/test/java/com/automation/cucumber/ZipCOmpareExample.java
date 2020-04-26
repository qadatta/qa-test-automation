package com.automation.cucumber;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.cucumber.java.Scenario;

public class ZipCOmpareExample {

	static Scenario scenario;

	public ZipCOmpareExample(Scenario scenario) {
		this.scenario = scenario;
	}

	
	
	

	public void compareZipFilesContent( ZipFile actualFile, ZipFile expectedFile) {

		boolean fileMatches = true;
		FileUtils fileUtils = new FileUtils(scenario);

		System.out.println("Comparing " + expectedFile.getName() + " with \n " + actualFile.getName() + ":");
		Set<String> set1 = fileUtils.getZipFileNames(expectedFile);
		Set<String> set2 = fileUtils.getZipFileNames(actualFile);

		for (Iterator i = set1.iterator(); i.hasNext();) {

			String name = (String) i.next();
			if (!set2.contains(name)) {
				System.out.println("File: " + name + " not found in " + actualFile.getName());
				continue;
			}
			try {
				set2.remove(name);
				ZipEntry expectedZipEntry = expectedFile.getEntry(name);
				ZipEntry actualZipEntry = actualFile.getEntry(name);

				if(false==fileUtils.compareZipEntry(actualFile, expectedFile, actualZipEntry, expectedZipEntry))
				{
					fileMatches = false;
					continue;
				}
				
			} catch (Exception e) {
				System.out.println(name + ": IO Error " + e);
				e.printStackTrace();
				continue;
			}
		}
		for (Iterator i = set2.iterator(); i.hasNext();) {
			String name = (String) i.next();
			System.out.println(name + " not found in " + expectedFile.getName());
		}

		if (false == fileMatches) {
			assertThat("S3_CAS_custom-download_response.zip and CAS_custom-donwload_expected.zip does not match",
					false);
		}
	}



	
	public static void main(String[] args) {
		
		ZipCOmpareExample zipCompare = new ZipCOmpareExample(scenario);
		
		String requestId = "96981159-afe1-4b31-b7c2-d669864578f5";
		String actualFilePath = System.getProperty("user.dir") + "/temp-files/"+ requestId +".zip";
		String expectedFilePath = System.getProperty("user.dir") + "/temp-files/S3_CAS_custom-download_response.zip";

		
		try {
			zipCompare.compareZipFilesContent(new ZipFile(actualFilePath),new ZipFile(expectedFilePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
