package com.automation.cucumber;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class RestAssuredTestExample {

	
	
	public static void main(String[] args) {
		
		
		ServicesStepDefinationUsingRestAssured stepDef = new ServicesStepDefinationUsingRestAssured();
		String jsonString = stepDef.getDownloadSpecificationFromFile("download_specification.json");
		
		Response response;
		ValidatableResponse json;
		RequestSpecification request;
		String ENDPOINT = "https://api.theexchange.fanniemae.com/v1/connecticut-ave-securities/custom-download/";
		
//		try {
//			request = given().header("Accept", "application/json").header("Authorization", ServicesStepDefinationUsingRestAssured.authorizationCode).contentType(ContentType.JSON).body(jsonString).with();
//			
//			response = request.when().post(ENDPOINT);
//			
//			response.then().statusCode(200);
//			System.out.println(" POST Response :" +response.prettyPrint());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		ENDPOINT = "https://api.theexchange.fanniemae.com/v1/connecticut-ave-securities/custom-download/status/af55dc10-8790-45a2-94e8-133ec13cd115";
		
		request = given().header("Accept", "application/json").header("Authorization", ServicesStepDefinationUsingRestAssured.authorizationCode).accept(ContentType.JSON);
		
		response = request.when().get(ENDPOINT);
		response.then().statusCode(200);
		System.out.println("GET Response :" +response.prettyPrint());
	    JsonPath jsonPathEvaluator = response.jsonPath();
	    String s3Uri = jsonPathEvaluator.get("s3Uri");
	    System.out.println("s3Uri: "+s3Uri);

	}
}
