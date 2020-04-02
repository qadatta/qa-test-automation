package com.automation.cucumber.S3Examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class ReadS3FileCOntent {

	
	public static void main(String[] args) {
		String bucketName = "bucket-created-using-java-code";
		String FileName = "dm.txt" ; //FIle name to read
        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;

        
        try {
        	
    		BasicAWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJ3YWD2HZMMLRVMWA", "92lcRBbxs2R6ro6hGjYOWoSNPI1RSJnSJb+IbBoL");
    		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-east-1").withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
       

            // Get an object and print its contents.
            System.out.println("Downloading an object");
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, FileName));
            System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
            System.out.println("Content: ");
            displayTextInputStream(fullObject.getObjectContent());
        }catch (Exception e) {
        	System.out.println("Problem in reading object");
        }
	}
	
	 private static void displayTextInputStream(InputStream input) throws IOException {
	        // Read the text input stream one line at a time and display each line.
	        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            System.out.println(line);
	        }
	        System.out.println();
	    }
}
