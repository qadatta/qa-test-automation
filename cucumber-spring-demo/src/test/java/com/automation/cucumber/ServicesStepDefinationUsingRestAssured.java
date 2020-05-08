package com.automation.cucumber;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.zip.ZipFile;

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
	static String requestId = "78f8ed8b-1fb2-4a19-ad19-e0e07bc57e4f"; // "af55dc10-8790-45a2-94e8-133ec13cd115";
	String authorizationCode = "eyJraWQiOiJyRzhja1lKNXFnS2FwNitpVG52UWpmM1pSK1lpRG9GOFY5c1pjR1B3MGUwPSIsImFsZyI6IlJTMjU2In0.eyJjdXN0b206b3JnYW5pemF0aW9uIjoiU2FuZ2FuYWt5IFRlY2hub2xvZ3kiLCJzdWIiOiJlZDIzMzhmNy1hNmVkLTQ0YzMtODQ0OC1kYmE3YzgyNWNjMmUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRzVqWUlMRXFYIiwiY29nbml0bzp1c2VybmFtZSI6InFhLmRhdHRhQGdtYWlsLmNvbSIsImN1c3RvbTpqb2JfdGl0bGUiOiJXZWIgRGV2ZWxvcGVyXC9FbmdpbmVlciIsImdpdmVuX25hbWUiOiJEYXR0YSIsImF1ZCI6IjM4MGRpaXRtc2JhN2Q2MjIwaDhsc3ExYnFvIiwiZXZlbnRfaWQiOiI1OWJiMmY0My02NTZmLTQxZTEtYTlhMS0xMzA0NGQ3ODU1YTEiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTU4ODkzMjE4NSwiZXhwIjoxNTg4OTM1ODE2LCJpYXQiOjE1ODg5MzIyMTYsImZhbWlseV9uYW1lIjoiTW9yZSIsImVtYWlsIjoicWEuZGF0dGFAZ21haWwuY29tIn0.jWHDn2mZOtXAEDYPirhyyfdIqoRCLm_oDAH-aQgOP2KvGOt22XOwKhslX6BP4vgumNpsYbQbbAp8E2XKCmnx-DZpUxgHSKztdqZfoQdykM5e2jMEdzODGDxfRQsSZkrkpRPlNvGMzBNvPCg876BrUN2PRwxhyhVve32rWs9twWpvdMTD8nmQDIx0T6rVFBDI7wSWDSUjqD4XEaKMLnVLpIKHzAtAw2NMj3m2rtQyFWslQ8pKvD3gOXCIgLPsqKJI5nnZ2TyehHV_636YW8C32VD5dyKWYRDPTgwl7eNIa76PVW0GT-cXN4NILvRyQqcsKfUs2OjPCl0Wk2I8upGZdQ";
	String s3Uri = "";
	String s3Url_expected = "s3://custom-download/expected-custom-downloads/CAS_custom-donwload_expected.zip";
	Response response;
	ValidatableResponse json;
	RequestSpecification request;
	String ENDPOINT = "https://api.theexchange.fanniemae.com/v1";
	private String expectedFileToCompare;
	public static String temp_dir = "/temp-files/";

	public static String userDir = System.getProperty("user.dir");
	public static Scenario scenario;
	public static String resourceDirectoryPath =  userDir + "/src/test/resources/"  ;
    public static String expectedZipDirectory = System.getProperty("user.dir") + temp_dir ;

	
	@Before
	public void beforeScenario(Scenario scenario)
	{
		this.scenario = scenario;
	}
	
	@Given("Custom-download POST api is up and running")
	public void custom_download_POST_api_is_up_and_running_for_endpoint() {
		request = given().header("Accept", "application/json").header("Authorization", authorizationCode);
		System.out.println("####################################################");
		System.out.println(request.given().log().all());
		 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		   LocalDateTime now = LocalDateTime.now();  
		   System.out.println("Execution started at: "+dtf.format(now));  
		
		scenario.write("Execution started at: " + dtf.format(now));

	}

	@When("Custom-download POST api executed for specification given in file {string}")
	public void custom_download_POST_api_executed_for_specification_given_in_file(String fileName) {
		expectedFileToCompare = fileName +".zip";
		fileName += ".json";
		String jsonString = getDownloadSpecificationFromFile(fileName);
		scenario.write(" Custom download specification : \n"+ jsonString);
		request.contentType(ContentType.JSON).body(jsonString).with();
		response = request.when().post(ENDPOINT + "/connecticut-ave-securities/custom-download/");
		
		try {
			
			
			JsonPath jsonPathEvaluator = response.jsonPath();
			requestId = jsonPathEvaluator.get("request-id");

			System.out.println("----------- generaed request-id ----------- ");
			System.out.println(requestId);
			System.out.println("--------------------------------- ");

		} catch (Exception e) {
			System.out.println("Problem in API response ");
		}

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
		System.out.println("downloading file " + requestId +".zip ...");
		FileUtils fileUtils =  new FileUtils(scenario);
		String actualZipFile = userDir + temp_dir + requestId + ".zip";
		fileUtils.downloadFileusingHttpGetRequest(s3Uri,actualZipFile);
	}


	@Then("compare file counts of both files")
	public void compare_file_counts_of_both_files() {
		try {
			S3Utils s3Utils = new S3Utils();
			System.out.println("downloading file using s3 uri");
//			s3Utils.downloadFileusingS3RI(s3Url_expected, expectedZipFile);
			System.out.println("Comparing file...");

			FileUtils fileUtils = new FileUtils(scenario);

			String actualZipFile = userDir + temp_dir + requestId + ".zip";

			fileUtils.compareFileCountsOfZipFiles(new ZipFile(actualZipFile), new ZipFile(expectedZipDirectory + expectedFileToCompare));
		} catch (IOException e) {
			System.out.println("Problem in comparing file count. " + e.getMessage());
			assertThat("Problem in comparing file count. ",false);

		}
		
	}

	@Then("compare file names present in both zip files")
	public void compare_file_names_present_in_both_zip_files() {
		FileUtils fileUtils =  new FileUtils(scenario);
		String actualZipFile = userDir + temp_dir + requestId + ".zip";

		try {
			fileUtils .compareFileNamesPresentInZipFiles(new ZipFile(actualZipFile), new ZipFile(expectedZipDirectory + expectedFileToCompare));
		} catch (IOException e) {
			System.out.println("Problem in comparing file names. " + e.getMessage());
			assertThat("Problem in comparing file names. ",false);

			
		}

	}
	@Then("check merged file present both zip files")
	public void check_merged_file_names_present_in_both_zip_files() {
		FileUtils fileUtils =  new FileUtils(scenario);
		String actualZipFile = userDir + temp_dir + requestId + ".zip";

		try {
			fileUtils.checkMergedFileNamesPresentInZipFiles(new ZipFile(actualZipFile), new ZipFile(expectedZipDirectory + expectedFileToCompare));
		} catch (IOException e) {
			System.out.println("Problem in comparing file names. " + e.getMessage());
			assertThat("Problem in comparing file names. ",false);

			
		}

	}	
	

	@Then("compare file size of both files")
	public void compare_file_size_of_both_files() {
		FileUtils fileUtils =  new FileUtils(scenario);
		String actualZipFile = userDir + temp_dir + requestId + ".zip";
		try {
			fileUtils .compareFileSizeOfZipFiles(new ZipFile(actualZipFile), new ZipFile(expectedZipDirectory + expectedFileToCompare));
		} catch (IOException e) {
			System.out.println("Problem in comparing file size. " + e.getMessage());
			assertThat("Problem in comparing file size. ",false);

		}


	}

	@Then("compare file content of both files")
	public void compare_file_content_of_both_files() {
		FileUtils fileUtils =  new FileUtils(scenario);
		String actualZipFile = userDir + temp_dir + requestId + ".zip";
		try {
			fileUtils.compareZipFilesContent(new ZipFile(actualZipFile), new ZipFile(expectedZipDirectory + expectedFileToCompare));
		} catch (IOException e) {
			System.out.println("Problem in comparing file contents. " + e.getMessage());
			assertThat("Problem in comparing file contents. ",false);

		}

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
		FileUtils fileUtils =  new FileUtils(scenario);
		String actualZipFile = userDir + temp_dir + requestId + ".zip";
		try {
			fileUtils.getAbsuluteZipFileName(new ZipFile(actualZipFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
