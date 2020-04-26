package com.automation.cucumber.S3Examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class CompareCustomDownloadsFIlesFromS3 {

	
	public static void main(String[] args) {
		

		String bucket_name = "custom-download";
		String key_name = "expected-custom-downloads/CAS_custom-donwload_expected.zip" ; //FIle name to read
        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        S3Object actualFileS3Object = null ;

		BasicAWSCredentials awsCredentials = new BasicAWSCredentials("AKIAJ3YWD2HZMMLRVMWA", "92lcRBbxs2R6ro6hGjYOWoSNPI1RSJnSJb+IbBoL");
   

        
        System.out.format("Downloading %s from S3 bucket %s...\n", key_name, bucket_name);
		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-2").withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
        try {
        	
        	
        	

        	ObjectListing listing = s3.listObjects(bucket_name,"actual-cutom-downloads");

        	
        	
        	

        	
            S3ObjectInputStream s3is = actualFileS3Object.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(key_name));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
        	System.out.println("----------------------");
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
        	System.out.println("---------------------->>>");

            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);

        }
		
	}


}
