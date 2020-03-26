package com.automation.cucumber;

import static io.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class ServicesStepDefinationUsingRestAssured {

	WebDriver driver;
	WebDriverWait wait;
	String requestId = "Request id store here after custom download post api executed"; // "af55dc10-8790-45a2-94e8-133ec13cd115";
	 static String authorizationCode = "eyJraWQiOiJyRzhja1lKNXFnS2FwNitpVG52UWpmM1pSK1lpRG9GOFY5c1pjR1B3MGUwPSIsImFsZyI6IlJTMjU2In0.eyJjdXN0b206b3JnYW5pemF0aW9uIjoiU2FuZ2FuYWt5IFRlY2hub2xvZ3kiLCJzdWIiOiJlZDIzMzhmNy1hNmVkLTQ0YzMtODQ0OC1kYmE3YzgyNWNjMmUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRzVqWUlMRXFYIiwiY29nbml0bzp1c2VybmFtZSI6InFhLmRhdHRhQGdtYWlsLmNvbSIsImN1c3RvbTpqb2JfdGl0bGUiOiJXZWIgRGV2ZWxvcGVyXC9FbmdpbmVlciIsImdpdmVuX25hbWUiOiJEYXR0YSIsImF1ZCI6IjM4MGRpaXRtc2JhN2Q2MjIwaDhsc3ExYnFvIiwiZXZlbnRfaWQiOiI3NGM2OTFhOC00NWZlLTQ1YTAtYThjNC0yYWY3MjJmZWQ2ZjUiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTU4NTE5NTk3NywiZXhwIjoxNTg1MTk5NTc5LCJpYXQiOjE1ODUxOTU5NzksImZhbWlseV9uYW1lIjoiTW9yZSIsImVtYWlsIjoicWEuZGF0dGFAZ21haWwuY29tIn0.Xsh1FftPCMM75E97s_-hGUl_6S2zGgs7gni_NT_8vA8dJZiCmUjKRnvaTitzrgG-IDee1sOq3ySG3HdG6YmwUFS3-ewpSk6wG2S3TUwUeVwEwnLRDuPOupKhW2evusE8M_O6LAL37fBqQBOY9SS4xGFfHemykrDMwMTwCJGg1WgsWxH57IgPMpECDCyrv3kOcY409CuFdyrpmexoWHeIt652TCOgNGmDTop7m6syTF38xIT1imfaqC7Izkn9MsMiVHD8Tz2-bquFZbaw2YH3NYgCYY_3gNFWyjlLlO9dGLetvHgIpMFbXlM6iuQ1EFLHBcCzbqbd5eezZ8SjttIbZQ";
	 String s3Uri ;
	Response response;
	ValidatableResponse json;
	RequestSpecification request;
	String ENDPOINT = "https://api.theexchange.fanniemae.com/v1";

	
	
	@Given("Custom-download POST api is up and running")
	public void custom_download_POST_api_is_up_and_running_for_endpoint() {
		request = given().header("Accept", "application/json")
				.header("Authorization", authorizationCode);
				
	}

	@When("Custom-download POST api executed for specification given in file {string}")
	public void custom_download_POST_api_executed_for_specification_given_in_file(String fileName) {
		String jsonString = getDownloadSpecificationFromFile(fileName);

		request.contentType(ContentType.JSON)
		.body(jsonString).with();
		response = request.when().post(ENDPOINT + "/connecticut-ave-securities/custom-download/");
		 JsonPath jsonPathEvaluator = response.jsonPath();
		 requestId = jsonPathEvaluator.get("request-id");
		 
		 System.out.println("----------- generaed request-id ----------- ");
		 System.out.println(requestId);
		 System.out.println("--------------------------------- ");
		 
	}

	@Given("Custom-download GET api is up and running")
	public void custom_download_GET_api_is_up_and_running_for_endpoint() {
		request = given().header("Accept", "application/json")
				.header("Authorization", authorizationCode);
	}

	@When("Custom-download GET api is executed for request-id of custom-download post request")
	public void custom_download_GET_api_is_executed_for_request_id_of_custom_download_post_request() {
		
		response = request.when().get(ENDPOINT + "/connecticut-ave-securities/custom-download/status/"+ requestId);
	}

	@Then("the status code is {int}")
	public void the_status_code_is(Integer statusCode) {
		response.then().statusCode(statusCode);

	}

	@Then("response includes the currentState as {string}")
	public void response_includes_the_currentState_as(String currentState) {
		 JsonPath jsonPathEvaluator = response.jsonPath();
		 String downloadStatus = jsonPathEvaluator.get("currentState");
		 int counter = 0;
		 while(true)
		 {
			 counter++;
			 if(downloadStatus.equals(currentState))
			 {
				 s3Uri = jsonPathEvaluator.get("s3Uri");

				 	System.out.println("-------------------------------------------");
					System.out.println("s3Uri for request \""+requestId+"\"=>  " + s3Uri);
					System.out.println("-------------------------------------------");
					break;
			 }
			 else if (31 == counter) {
						break;
					} else { 
						System.out.println("Current status is: " + downloadStatus);
						System.out.println("waiting for 30 seconds...");
						try {
							Thread.sleep(30000);
							response = request.get(ENDPOINT + "/connecticut-ave-securities/custom-download/status/"+ requestId);
							downloadStatus = jsonPathEvaluator.get("currentState");
						
						} catch (InterruptedException e) {
						}
						
					}
		 }
	}



	@Given("s3URI is available from custom-download api")
	public void s3uri_is_available_from_custom_download_api() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@When("download custom-download zip file using curl command")
	public void download_custom_download_zip_file_using_curl_command() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("files downloaded in source folder")
	public void files_downloaded_in_source_folder() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("compare source zip file with destination zip file")
	public void compare_source_zip_file_with_destination_zip_file() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}



	public static String getDownloadSpecificationFromFile(String fileName) {
		StringBuffer contents = new StringBuffer();

		try {
			File directory = new File("./");
			System.out.println(directory.getAbsolutePath());
			File file = new File(directory.getAbsolutePath().replace(".", "")
					+ "/src/test/resources/custom_download_specs/" + fileName);
			BufferedReader reader = null;

			reader = new BufferedReader(new FileReader(file));
			String text = null;

			// repeat until all lines is read

			while ((text = reader.readLine()) != null) {
				contents.append(text).append(System.getProperty("line.separator"));
			}
			reader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contents.toString();
	}

}
