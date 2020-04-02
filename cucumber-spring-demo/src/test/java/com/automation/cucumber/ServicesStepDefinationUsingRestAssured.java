package com.automation.cucumber;

import static io.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
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
	String requestId = "96981159-afe1-4b31-b7c2-d669864578f5"; // "af55dc10-8790-45a2-94e8-133ec13cd115";
	String authorizationCode = "eyJraWQiOiJyRzhja1lKNXFnS2FwNitpVG52UWpmM1pSK1lpRG9GOFY5c1pjR1B3MGUwPSIsImFsZyI6IlJTMjU2In0.eyJjdXN0b206b3JnYW5pemF0aW9uIjoiU2FuZ2FuYWt5IFRlY2hub2xvZ3kiLCJzdWIiOiJlZDIzMzhmNy1hNmVkLTQ0YzMtODQ0OC1kYmE3YzgyNWNjMmUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRzVqWUlMRXFYIiwiY29nbml0bzp1c2VybmFtZSI6InFhLmRhdHRhQGdtYWlsLmNvbSIsImN1c3RvbTpqb2JfdGl0bGUiOiJXZWIgRGV2ZWxvcGVyXC9FbmdpbmVlciIsImdpdmVuX25hbWUiOiJEYXR0YSIsImF1ZCI6IjM4MGRpaXRtc2JhN2Q2MjIwaDhsc3ExYnFvIiwiZXZlbnRfaWQiOiJiMzQ2ZTM4Yi0yYTM1LTQwYzktOGFlZi1mNGRmZTI2ZmU1MDYiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTU4NTg1MDIyNCwiZXhwIjoxNTg1ODU3NTc2LCJpYXQiOjE1ODU4NTM5NzYsImZhbWlseV9uYW1lIjoiTW9yZSIsImVtYWlsIjoicWEuZGF0dGFAZ21haWwuY29tIn0.YPNxDQ8MNnR8KbXjSyMUzPvojYvWS_DrBSP9OLN3-nhc5PQyY6OhvQ3gPsQQ8AmY-eF8ypoRlF6Uo0GeFNtKieuIiqOWJY46swf-k_Y7yAB43Hjrb8g5ZrDQfzScwo0fk_Nsdrnfx0bCghhfJOOYjcTYR5uqhDkCREnMQiY2D-74qQSA3An7UxXWNm962eQ0zYZCakStLTEp9-L6KyMgVmZLAF3yd4oDIBNrjAnOy4hP4Y9EIEBOVvxPQgNbuVqIiF0iUANirITPKb-KBpQ2BtivY7HjYbzau24W85gYeqJJJgqWT5nRuU80NP5M4EELkdHX-0FZM3EyufF7E66kWw";
	String s3Uri = "https://fnma-ppafo0-prodpubex-us-east-1-files.s3.amazonaws.com/cas/response/11d94053-8c32-4578-92e4-603f1f07e267.zip?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEML%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJHMEUCIBXZq9zD0cEGfFD90WMl2HaT3kcMZ4zvyKVx4hpblEMwAiEAysKIHiuZUK4oUCYdVx5NdKC2FvlwkJumZuLDWAm0yIcqvQMIu%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARACGgwwOTAwNDM0NzE5MjciDPLyCXgj8ureEp96xCqRA6%2BleNYA5BPTLkFIsCVIVMXEPcSf2nIDRtVhDscN%2BBltp7CEQhmTq60810o2tllY66GJGKzj4eQRhAYSp3khnZJZqVJeTy80khx3YVdnkL0kIaJU116D9JXbqmX4z79SneX1rtnRSEsFfyFY25hNBScRlyPs6ShAT2AJpZjrtNy1NSxbKgx5XzoLRKsKdlDyef%2B8XULa1YPn4xNbdRZxqjT%2Ba5%2F2%2F7ltMx5JO%2FSf3Jbz%2Bxx1KO8s4SOTUSgZaqPCzLrk%2FY3UPXHVmvmS92UHyr445%2FntRbn%2FFCjiHbJvkH3Pze0latJau87RozCPpx1mdQZ%2Ba8or7UJgTfM22blkZyWjdXXtuSwn%2FXhZAIutWZ9ipQunKd1AxdSSnT0%2FGKoGxWQxVAOgxeKGJnZIS%2BzX5c6D4iQ9RF7rcMCiG06N7SBGVLhsDkcczYBMbkXZAs0DaYFE7%2BpKTZ%2FDoL96acEXvS3fxUYbvyWv%2FHzRGVb%2FWiyDfzsAiZG9mfDJb9pnaEoZpejglgnerzqKZIp00dKOmth%2BMKy1%2FPMFOusByZ2ZkNtwoBwm%2BEFQdZYVQ6Ho4sYx9fO90Xq3FwCkOy%2FVvpX%2FCGrR8w2gRPlSzOHKxeZdyNf5PSkoNwhpZDqon7RO3gyOMOkyO6iyPu0D4tF2Gq6iz7gB0aUFQ1solMSmA0y8VUX2vus5BoJO0OP0nVq%2BLufZO57yKN7P6LXoAf%2FItuQa%2BnkJ9IgFYRdJMpq2%2B3jFhrhaA40EwUMK0ZuHt4YZsmwnxXyyJVnW8Evxmo8ohPg0OaqhZvyRr%2Fx0Zzww%2BvC3SrThSIF0yphw5EbpjLaVA066hTJ5NWqvY1eZ%2BKWqGQg9dzvUOxv2eA%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20200328T094754Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604800&X-Amz-Credential=ASIARJ5YCLA32PTLSEMS%2F20200328%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=f4f5ce221af29a74ed939ca11fa21d0ecd0c9b33183a2505cbb34ac89f720c4a";
	Response response;
	ValidatableResponse json;
	RequestSpecification request;
	String ENDPOINT = "https://api.theexchange.fanniemae.com/v1";
	public static String resourceDirectoryPath;
	public static String sourceZipFile;
	public static String destinationZipFile;
	public Scenario scenario;
	
	
	
	static{
		File directory = new File("./");
		resourceDirectoryPath =  directory.getAbsolutePath().replace(".", "")+ "src/test/resources/"  ;
		sourceZipFile = resourceDirectoryPath + "files_to_compare/S3_CAS_custom-download_response.zip";
		destinationZipFile = resourceDirectoryPath + "files_to_compare/CAS_custom-donwload_expected.zip";
			
	}
	
	@Before
	public void beforeScenario(Scenario scenario)
	{
		this.scenario = scenario;
	}
	
	@Given("Custom-download POST api is up and running")
	public void custom_download_POST_api_is_up_and_running_for_endpoint() {
		request = given().header("Accept", "application/json").header("Authorization", authorizationCode);

	}

	@When("Custom-download POST api executed for specification given in file {string}")
	public void custom_download_POST_api_executed_for_specification_given_in_file(String fileName) {
		String jsonString = getDownloadSpecificationFromFile(fileName);
		scenario.write(" Custom download specification : \n"+ jsonString);
		request.contentType(ContentType.JSON).body(jsonString).with();
		response = request.when().post(ENDPOINT + "/connecticut-ave-securities/custom-download/");
		try {
			JsonPath jsonPathEvaluator = response.jsonPath();
			requestId = jsonPathEvaluator.get("request-id");
		} catch (Exception e) {
			System.out.println("Problem in API response ");
		}

		System.out.println("----------- generaed request-id ----------- ");
		System.out.println(requestId);
		System.out.println("--------------------------------- ");

	}

	@Given("Custom-download GET api is up and running")
	public void custom_download_GET_api_is_up_and_running_for_endpoint() {
		request = null;
		request = given().header("Accept", "application/json").header("Authorization", authorizationCode);
		
	}

	@When("Custom-download GET api is executed for request-id of custom-download post request")
	public void custom_download_GET_api_is_executed_for_request_id_of_custom_download_post_request() {
		scenario.write("Custom download request id: " + requestId);
		response = request.when().get(ENDPOINT + "/connecticut-ave-securities/custom-download/status/" + requestId);
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
		while (true) {
			counter++;
			if (downloadStatus.equals(currentState)) {
				s3Uri = jsonPathEvaluator.get("s3Uri");

				System.out.println("-------------------------------------------");
				System.out.println("s3Uri for request \"" + requestId + "\"=>  " + s3Uri);
				System.out.println("-------------------------------------------");
				break;
			} else if (31 == counter) {
				break;
			} else {
				System.out.println("Current status is: " + downloadStatus);
				System.out.println("waiting for 30 seconds...");
				try {
					Thread.sleep(30000);
					// this.custom_download_GET_api_is_up_and_running_for_endpoint();
					System.out.println("Request Id: " + requestId);
					response = request.when()
							.get(ENDPOINT + "/connecticut-ave-securities/custom-download/status/" + requestId);
					jsonPathEvaluator = response.jsonPath();
					downloadStatus = jsonPathEvaluator.get("currentState");

				} catch (InterruptedException e) {
				}

			}
		}
	}

	@Given("s3URI is available from custom-download api")
	public void s3uri_is_available_from_custom_download_api() {
	}

	@When("download custom-download zip file")
	public void download_custom_download_zip_file() {
		try {
			File directory = new File("./");
			File file = new File(directory.getAbsolutePath().replace(".", "")
					+ "/src/test/resources/custom_download_specs/" );
			
			downloadRequest(s3Uri,sourceZipFile );
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Then("files downloaded in source folder")
	public void files_downloaded_in_source_folder() {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}
	
	@Then("compare file counts of both files")
	public void compare_file_counts_of_both_files() {
		ZipCompare zipCompare = new ZipCompare(scenario);
		zipCompare.compareFileCountsOfZipFiles(sourceZipFile,destinationZipFile);

	}

	@Then("compare file names present in both zip files")
	public void compare_file_names_present_in_both_zip_files() {
		ZipCompare zipCompare = new ZipCompare(scenario);
		zipCompare.compareFileNamesPresentInZipFiles(sourceZipFile,destinationZipFile);

	}

	@Then("compare file size of both files")
	public void compare_file_size_of_both_files() {
		ZipCompare zipCompare = new ZipCompare(scenario);
		zipCompare.compareFileSizeOfZipFiles(sourceZipFile,destinationZipFile);

	}

	@Then("compare file content of both files")
	public void compare_file_content_of_both_files() {

		ZipCompare zipCompare = new ZipCompare(scenario);
		zipCompare.compareZipFilesContent(sourceZipFile,destinationZipFile);
	}

	@Then("compare source zip file with destination zip file")
	public void compare_source_zip_file_with_destination_zip_file() {
		
		ZipCompare zipCompare = new ZipCompare(scenario);
		zipCompare.compareZipFilesContent(sourceZipFile,destinationZipFile);
	}

	public static String getDownloadSpecificationFromFile(String fileName) {
		StringBuffer contents = new StringBuffer();

		try {
			File directory = new File("./");
			System.out.println(directory.getAbsolutePath());
			File file = new File(resourceDirectoryPath +"custom_download_specs/" + fileName);
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

	private static void downloadRequest(String url, String savedFile) {
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
					System.out.println(e.getMessage());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
	
		ServicesStepDefinationUsingRestAssured assured = new ServicesStepDefinationUsingRestAssured();
		assured.compare_source_zip_file_with_destination_zip_file();
	}

}
