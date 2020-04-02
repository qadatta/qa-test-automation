package com.automation.cucumber.S3Examples;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class CreateBucketExample {

	
	public static void main(String[] args) {
		
		String bucketName = "bucket-created-using-java-code1";

		BasicAWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJ3YWD2HZMMLRVMWA", "92lcRBbxs2R6ro6hGjYOWoSNPI1RSJnSJb+IbBoL");
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withRegion("us-east-1").withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		
		try {
			System.out.println("creating s3 bucket...");
			s3Client.createBucket(bucketName);
		} catch (AmazonServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SdkClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
