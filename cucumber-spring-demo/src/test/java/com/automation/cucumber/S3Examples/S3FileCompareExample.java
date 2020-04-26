package com.automation.cucumber.S3Examples;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import io.cucumber.java.Scenario;

public class S3FileCompareExample {
	Scenario scenario;

	private static String bucket_name = "custom-download";
	private static String actualFileKey = "S3_CAS_custom-download_response.zip";
	private static String expectedFileKey = "CAS_custom-donwload_expected.zip";

	private final static Long MILLS_IN_DAY = 86400000L;

	public static void main(String[] args) {

		try {

			AWSCredentials credentials = new BasicAWSCredentials("AKIATMEWKHNTLBTJXBGA",
					"JwK0q9xVod6vekWsyIWLVCA6i13cLjM0ZGOxHXYD");

			AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion("us-east-2")
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

			ObjectListing objectListing = s3client.listObjects(bucket_name);

			for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
				if (os.getKey().contains(actualFileKey)) {
					actualFileKey = os.getKey();
				} else if (os.getKey().contains(expectedFileKey)) {
					expectedFileKey = os.getKey();
				}
			}
			S3Object actualFileS3Object = s3client.getObject(bucket_name, actualFileKey);
			S3Object expectedFileS3Object = s3client.getObject(bucket_name, expectedFileKey);

			// readZipFileFromS3(actualFileS3Object);
			// readZipFileFromS3(expectedFileS3Object);

			compareFileSizeOfZipFiles(actualFileS3Object, expectedFileS3Object);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void readZipFileFromS3(S3Object zipFileObject) {
		System.out.println("reading zip file ....");
		S3ObjectInputStream is = zipFileObject.getObjectContent();
		ZipInputStream zis = new ZipInputStream(is);

		ZipEntry ze;

		try {
			while ((ze = zis.getNextEntry()) != null) {

				System.out.format("File: %s Size: %d Last Modified %s %n", ze.getName(), ze.getSize(),
						LocalDate.ofEpochDay(ze.getTime() / MILLS_IN_DAY));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	boolean getDifferenceInFiles(InputStream stream1, InputStream stream2) {
		boolean filesEquals = false;

		BufferedReader bfReader1 = new BufferedReader(new InputStreamReader(stream1));
		BufferedReader bfReader2 = new BufferedReader(new InputStreamReader(stream2));

		String file1Line = null;
		String file2Line = null;
		int lineCounter = 0;
		try {
			while ((file1Line = bfReader1.readLine()) != null) {
				++lineCounter;
				file2Line = bfReader2.readLine();

				if (false == file1Line.equals(file2Line)) {
					// scenario.write("\n ---------Difference on line number: "
					// + lineCounter + " ----------------");
					System.out.println("\n ---------Difference on line number: " + lineCounter + " ----------------");
					// scenario.write("File 1 line: " + lineCounter + " not
					// matched \n" + file1Line);
					System.out.println("File 1 line: " + lineCounter + " not matched \n" + file1Line);
					// scenario.write("File 2 line: " + lineCounter + " not
					// matched \n" + file2Line);
					System.out.println("File 2 line: " + lineCounter + " not matched \n" + file2Line);
					System.out.println();
					String[] file1Columns = file1Line.split("\\|");
					String[] file2Columns = file2Line.split("\\|");
					// scenario.write("Following column data not matched");
					System.out.println("Following column data not matched");

					for (int i = 0; i < file1Columns.length; i++) {
						try {
							if (false == file1Columns[i].equals(file2Columns[i])) {
								// scenario.write(
								// "Column number " + i + "==> " +
								// file1Columns[i] + " <<>> " +
								// file2Columns[i]);
								System.out.println(
										"Column number " + i + "==> " + file1Columns[i] + " <<>> " + file2Columns[i]);
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

	public static void compareFileCountsOfZipFiles(S3Object actualZipFileS3Object, S3Object expectedZipFileS3Object) {

		S3ObjectInputStream is = actualZipFileS3Object.getObjectContent();
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry ze;
		int actualFiles = 0;
		int expectedFiles = 0;
		try {
			while ((ze = zis.getNextEntry()) != null) {
				actualFiles++;
			}
			S3ObjectInputStream is2 = expectedZipFileS3Object.getObjectContent();
			ZipInputStream zis2 = new ZipInputStream(is2);
			while ((ze = zis2.getNextEntry()) != null) {
				expectedFiles++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("File count not matches (actualFiles == expectedFiles) " + (actualFiles == expectedFiles));
		// scenario.write("<b> Comparing " + actualZipFileS3Object.getKey()+ "
		// with \n" + expectedZipFileS3Object.getKey() + " file counts <b>\n");
		// scenario.write("Total files present in "
		// +actualZipFileS3Object.getKey() + " are: " + actualFiles);
		// scenario.write("Total files present in " +
		// expectedZipFileS3Object.getKey() + " are: " + expectedFiles);
		// assertThat("File count not matches", actualFiles == expectedFiles);
	}

	public static void compareFileSizeOfZipFiles(S3Object actualZipFileS3Object, S3Object expectedZipFileS3Object) {

		S3ObjectInputStream is = actualZipFileS3Object.getObjectContent();
		ZipInputStream zis = new ZipInputStream(is);

		int file1Size = 0;
		int file2Size = 0;

		try {
			System.out.println("Comparing file size of " + actualZipFileS3Object.getKey() + " with  "
					+ expectedZipFileS3Object.getKey() + ":");
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				System.out.println("Name: " + ze.getName() + " Size: " + ze.getSize());
				file1Size += ze.getSize();

			}

			is = expectedZipFileS3Object.getObjectContent();
			zis = new ZipInputStream(is);
			System.out.println("expected file size...");
			while ((ze = zis.getNextEntry()) != null) {
				System.out.println("Name: " + ze.getName() + " Size: " + ze.getSize());
				file2Size += ze.getSize();

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Files size - actual:" + file1Size + " and expected :" + file2Size);
		// assertThat("File size not matches", file1Size==file2Size);
	}

	public static void compareFileNameOfZipFiles(S3Object actualZipFileS3Object, S3Object expectedZipFileS3Object) {

		S3ObjectInputStream is = actualZipFileS3Object.getObjectContent();
		ZipInputStream zis = new ZipInputStream(is);
		Set set1 = new LinkedHashSet();
		Set set2 = new LinkedHashSet();

		try {
			System.out.println("Comparing file size of " + actualZipFileS3Object.getKey() + " with  "
					+ expectedZipFileS3Object.getKey() + ":");
			System.out.println("Comparing " + actualZipFileS3Object.getKey() + " with \n "
					+ expectedZipFileS3Object.getKey() + ":");
			// scenario.write("<b>Comparing files names present in " +
			// actualZipFileS3Object.getKey() + " with \n file names present in
			// " + expectedZipFileS3Object.getKey() + "</b> \n");

			ZipEntry ze;

			while ((ze = zis.getNextEntry()) != null) {
				String fileName = ze.getName();
				System.out.println(fileName);
				// scenario.write("\n " + fileName );
				set1.add(fileName);
			}

			is = expectedZipFileS3Object.getObjectContent();
			zis = new ZipInputStream(is);
			System.out.println("expected file size...");
			while ((ze = zis.getNextEntry()) != null) {
				String fileName = ze.getName();
				System.out.println(fileName);
				// scenario.write("\n " + fileName );
				set2.add(fileName);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		set1.removeAll(set2);
		set2.removeAll(set1);
		System.out.println("set1.isEmpty(): " + set1.isEmpty() + " set2.isEmpty(): " + set2.isEmpty());

		// assertThat("File present in both zip are not matches", set1.isEmpty()
		// && set2.isEmpty());
	}

}
