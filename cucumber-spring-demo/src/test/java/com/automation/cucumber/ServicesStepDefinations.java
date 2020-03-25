package com.automation.cucumber;

import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ServicesStepDefinations {

	Client client;
	WebTarget webTarget;
	Invocation.Builder invocationBuilder;
	Response response;
	WebDriver driver ;
	WebDriverWait wait;
	String requestId = "Request id store here after custom download post api executed"; //"af55dc10-8790-45a2-94e8-133ec13cd115";
	String authorizationCode = "Auth code stored in this variable after login";
	String s3Uri ; // After completed sattus for custom-download api s#url stored here
	//static String custom_download_file_name = "download_specification.json";

	@Then("^I should see status code as (\\d+)$")
	public void i_should_see_status_code_as(int statuscode) throws Throwable {

        assertEquals(statuscode, response.getStatus());


	}

	@Given("User has access to custom-download api")
	public void user_has_access_to_custom_download_api() {
		client = ClientBuilder.newClient();
		String URL = "https://api.theexchange.fanniemae.com/v1/connecticut-ave-securities/custom-download/";
		webTarget = client.target(URL);
		invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("Authorization", authorizationCode);

	}

	@Given("User has access to custom-download status api")
	public void user_has_access_to_custom_download_status_api() {
		client = ClientBuilder.newClient();
		String URL = "https://api.theexchange.fanniemae.com/v1/connecticut-ave-securities/custom-download/status/"
				+ requestId;
		webTarget = client.target(URL);
		invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("Authorization", authorizationCode);

	}

	@When("I execute custom-download  GET request")
	public void i_execute_custom_download_GET_request() {
		response = invocationBuilder.get();

	}

	@Then("Verify response is having completed status")
	public void verify_response_is_having_completed_status() {

		response = invocationBuilder.get();
		String jsonRespnse = response.readEntity(String.class);
		JSONObject obj = new JSONObject(jsonRespnse);
		int counter = 0;
		String currentState = obj.getString("currentState");

		while (true) {
			counter++;
			if (currentState.equalsIgnoreCase("completed")) {
				 s3Uri = obj.getString("s3Uri");

				System.out.println("-------------------------------------------");
				System.out.println("s3Uri for request \""+requestId+"\"=>  " + s3Uri);
				System.out.println("-------------------------------------------");

				break;
			} else if (31 == counter) {
				break;
			} else {
				try {
					System.out.println("waiting for 30 seconds...");
					Thread.sleep(30000);
					response = invocationBuilder.get();
					jsonRespnse = response.readEntity(String.class);
					obj = new JSONObject(jsonRespnse);
					currentState = obj.getString("currentState");
				} catch (InterruptedException e) {
				}
			}
		}

        assertEquals(currentState, "completed");

	}

	@When("I execute custom-download  POST request using spec {string}")
	public void i_execute_custom_download_POST_request_using_spec(String fileName) throws URISyntaxException {


		String jsonString = getDownloadSpecificationFromFile(fileName);

		response = invocationBuilder.post(Entity.json(jsonString));
		JSONObject obj = new JSONObject(response.readEntity(String.class));
		requestId = obj.getString("request-id");
		//requestId = obj.getJSONObject("specification").getString("include-all-deals");
		
		

		System.out.println("-------------------------------------------");
		System.out.println("New request id for spec - " + jsonString );
		System.out.println(requestId);
		System.out.println("-------------------------------------------");

	}

	public String getDownloadSpecificationFromFile(String fileName) {
	    StringBuffer contents = new StringBuffer();

		try {
			File directory = new File("./");
			System.out.println(directory.getAbsolutePath());
		File file = new File(directory.getAbsolutePath().replace(".", "")+"/src/test/resources/custom_download_specs/"+fileName);
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
	
	@Given("User open applicaton {string}")
	public void user_open_applicaton(String appUrl) {
		File directory = new File("./");
		   
        System.setProperty("webdriver.chrome.driver", directory.getAbsolutePath().replace(".", "")+"src/test/resources/chromedriver.exe");
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
		wait.until(ExpectedConditions.visibilityOf((WebElement) driver.findElement(By.cssSelector("span.header-user-icon-span"))));
		
		
        WebElement profileIcon = wait.until(presenceOfElementLocated(By.cssSelector("span.header-user-icon-span")));
        profileIcon.click();

        System.out.println("-----------------------------------------------");
        System.out.println("Old authorizationCode: "+authorizationCode);

        WebElement Copy_UserToken_link = wait.until(presenceOfElementLocated(By.xpath("//button[contains(.,'(Copy User Token)')]")));
        Copy_UserToken_link.click();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        
       try {
		authorizationCode = (String) clipboard.getData(DataFlavor.stringFlavor);
	} catch (UnsupportedFlavorException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
        System.out.println("New authorizationCode: "+authorizationCode);
        System.out.println("-----------------------------------------------");

        
        driver.close();
        driver.quit();
		
		
	}
	
	
	   public static void main(String[] args) throws UnsupportedFlavorException, IOException {
	        
		   ServicesStepDefinations definations = new ServicesStepDefinations();
		//   System.out.println(definations.getDownloadSpecificationFromFile(custom_download_file_name));
	   }
}