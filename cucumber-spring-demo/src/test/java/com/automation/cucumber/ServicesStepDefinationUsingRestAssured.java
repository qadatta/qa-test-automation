package com.automation.cucumber;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class ServicesStepDefinationUsingRestAssured {

	WebDriver driver;
	WebDriverWait wait;
	String requestId = "Request id store here after custom download post api executed"; // "af55dc10-8790-45a2-94e8-133ec13cd115";
	static String authorizationCode = "eyJraWQiOiJyRzhja1lKNXFnS2FwNitpVG52UWpmM1pSK1lpRG9GOFY5c1pjR1B3MGUwPSIsImFsZyI6IlJTMjU2In0.eyJjdXN0b206b3JnYW5pemF0aW9uIjoiU2FuZ2FuYWt5IFRlY2hub2xvZ3kiLCJzdWIiOiJlZDIzMzhmNy1hNmVkLTQ0YzMtODQ0OC1kYmE3YzgyNWNjMmUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRzVqWUlMRXFYIiwiY29nbml0bzp1c2VybmFtZSI6InFhLmRhdHRhQGdtYWlsLmNvbSIsImN1c3RvbTpqb2JfdGl0bGUiOiJXZWIgRGV2ZWxvcGVyXC9FbmdpbmVlciIsImdpdmVuX25hbWUiOiJEYXR0YSIsImF1ZCI6IjM4MGRpaXRtc2JhN2Q2MjIwaDhsc3ExYnFvIiwiZXZlbnRfaWQiOiI2NDk4MTMzZS05MzI1LTQ5ZDktYmUyOC0xNWI1ZjBhYzdhMzkiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTU4NTEwNDg0MiwiZXhwIjoxNTg1MTA4NDQ1LCJpYXQiOjE1ODUxMDQ4NDUsImZhbWlseV9uYW1lIjoiTW9yZSIsImVtYWlsIjoicWEuZGF0dGFAZ21haWwuY29tIn0.hP_XgeMApLhSo8AtRamBBozDOI4srRM1-OaMULP_lAoFHKFgr0EzFbZ6MDOrjW6GZHrOx0s9fILL2wc1J_aNPu-hr9ECyzFWoLIdrv2Ax01BOCI4k1ypvIGTftxqLpPxmONOi_Bd3nc0NnySBgIjQNYhgyNG8AYqIcXq343FReVB2TquHpIoaS4KRPBHK891KeK-BhAzx1dvu7Ecr3ztucI4-jutA4G0rY2FU52fKv1mp5gVDDNASapZjYOg0Rv0XbxlIwPL745O7imWyfogql-5OZUGo5KWeop2gFby3G4AYytH6cDCLhg6r96NudSU6aIuBD44cjw0TkcdmRvdyQ";

	private Response response;
	private ValidatableResponse json;
	private RequestSpecification request;

	
	@Given("User open applicaton {string}")
	public void user_open_applicaton(String appUrl) {
		File directory = new File("./");

		System.setProperty("webdriver.chrome.driver",
				directory.getAbsolutePath().replace(".", "") + "src/test/resources/chromedriver.exe");
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, 30);
		driver.get(appUrl);

	}

	@When("user login using username {string} and password {string}")
	public void user_login_using_username_and_password(String userName, String password) {
		WebElement signIn = wait.until(presenceOfElementLocated(By.linkText("SIGN IN")));

		signIn.click();
		WebElement emailField = wait.until(presenceOfElementLocated(By.id("inputEmail")));
		WebElement pwdField = driver.findElement(By.id("inputPassword"));
		WebElement signInButton = driver.findElement(By.xpath("//button[contains(.,'Sign In')]"));
		emailField.sendKeys(userName);
		pwdField.sendKeys(password);
		signInButton.click();

	}

	@Then("I should see user logged in to application")
	public void i_should_see_user_logged_in_to_application() {

		wait.until(ExpectedConditions.invisibilityOf((WebElement) driver.findElement(By.id("inputPassword"))));
		wait.until(ExpectedConditions
				.visibilityOf((WebElement) driver.findElement(By.cssSelector("span.header-user-icon-span"))));

		WebElement profileIcon = wait.until(presenceOfElementLocated(By.cssSelector("span.header-user-icon-span")));
		profileIcon.click();

		System.out.println("-----------------------------------------------");
		System.out.println("Old authorizationCode: " + authorizationCode);

		WebElement Copy_UserToken_link = wait
				.until(presenceOfElementLocated(By.xpath("//button[contains(.,'(Copy User Token)')]")));
		Copy_UserToken_link.click();
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		try {
			authorizationCode = (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("New authorizationCode: " + authorizationCode);
		System.out.println("-----------------------------------------------");

		driver.close();
		driver.quit();

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

	@Given("User has access to custom-download api")
	public void user_has_access_to_custom_download_api() {

		String jsonString = getDownloadSpecificationFromFile("download_specification.json");

		given().basePath("https://api.theexchange.fanniemae.com/v1/").contentType("application/json")
				.header("Authorization", authorizationCode).body(jsonString).when()
				.post("connecticut-ave-securities/custom-download/").then().statusCode(201);
		;

	}

	@When("I execute custom-download  POST request using spec {string}")
	public void i_execute_custom_download_POST_request_using_spec(String fileName) {

		when().post("connecticut-ave-securities/custom-download/").then().statusCode(200);
	}

	@Given("User has access to custom-download status api")
	public void user_has_access_to_custom_download_status_api() {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}

	@When("I execute custom-download  GET request")
	public void i_execute_custom_download_GET_request() {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}

	@Then("I should see status code as {int}")
	public void i_should_see_status_code_as(Integer int1) {

	}

	@Then("Verify response is having completed status")
	public void verify_response_is_having_completed_status() {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}

	public static void main(String[] args) {

		String jsonString = getDownloadSpecificationFromFile("download_specification.json");
		System.out.println(jsonString);
//		given().baseUri("https://api.theexchange.fanniemae.com/v1/").header("Accept", "application/json").accept(ContentType.JSON).
//
//
//				header("Authorization", authorizationCode).body(jsonString).when()
//				.post("/connecticut-ave-securities/custom-download/").then().statusCode(201);
//		;
//		

		given().baseUri("https://api.theexchange.fanniemae.com/v1/").header("Accept", "application/json").accept(ContentType.JSON).


		header("Authorization", authorizationCode).when()
		.get("/connecticut-ave-securities/custom-download/status/af55dc10-8790-45a2-94e8-133ec13cd115").then().statusCode(201);
;


	}
}
