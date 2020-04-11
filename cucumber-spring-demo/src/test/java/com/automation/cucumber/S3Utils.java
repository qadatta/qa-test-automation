package com.automation.cucumber;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

public class S3Utils {

	
	private static String bucket_name = "custom-download";
	private static String actualFileKey = "S3_CAS_custom-download_response.zip";
	private static String expectedFileKey = "CAS_custom-donwload_expected.zip";
	
	private final static Long MILLS_IN_DAY = 86400000L;
	
	public String downloadFileusingS3RI(String s3Uri, String downloadPath) throws IOException {
		
		AWSCredentials credentials = new BasicAWSCredentials("AKIATMEWKHNTLBTJXBGA",
				"JwK0q9xVod6vekWsyIWLVCA6i13cLjM0ZGOxHXYD");

		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion("us-east-2")
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

		
	    AmazonS3URI s3FileUri = new AmazonS3URI(s3Uri);
	    S3Object s3object = s3client.getObject(new GetObjectRequest(s3FileUri.getBucket(), s3FileUri.getKey()));
	    System.out.println(s3object.getBucketName());
	    S3ObjectInputStream inputStream = s3object.getObjectContent();
	    
		BufferedOutputStream bos = new BufferedOutputStream((new FileOutputStream(downloadPath)));

		IOUtils.copy(inputStream, bos);
		bos.close();
		inputStream.close();
		s3object.close();
	    
	    
//	    FileUtils.copyInputStreamToFile(inputStream, new File(downloadPath));
	    return s3object.getBucketName();
	    //return IOUtils.toString(s3object.getObjectContent());
	}
	
	
	
	
public static void main(String[] args) {
	
	S3Utils s3Utils = new S3Utils();
	try {
		String s3uri = "s3://custom-download/expected-custom-downloads/CAS_custom-donwload_expected.zip";
		String downloadPath = System.getProperty("user.dir") + "/temp-files/AS_custom-donwload_expected.zip";
		s3Utils.downloadFileusingS3RI(s3uri,downloadPath);
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


}
	
}
