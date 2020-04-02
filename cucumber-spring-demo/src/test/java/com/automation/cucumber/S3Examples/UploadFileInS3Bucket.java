package com.automation.cucumber.S3Examples;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class UploadFileInS3Bucket {


	
	public static void main(String[] args) {
		String bucketName = "bucket-created-using-java-code";
		String FileName = "dm.txt";
		
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJ3YWD2HZMMLRVMWA", "92lcRBbxs2R6ro6hGjYOWoSNPI1RSJnSJb+IbBoL");
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("us-east-1").withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
        s3Client.putObject(bucketName, FileName , "Uploaded String Object");
	}
	
	

	
}
