package com.automation.cucumber;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class RunAPIExample {

	
	static String requestId = "96981159-afe1-4b31-b7c2-d669864578f5"; // "af55dc10-8790-45a2-94e8-133ec13cd115";
	static String authorizationCode = "eyJraWQiOiJyRzhja1lKNXFnS2FwNitpVG52UWpmM1pSK1lpRG9GOFY5c1pjR1B3MGUwPSIsImFsZyI6IlJTMjU2In0.eyJjdXN0b206b3JnYW5pemF0aW9uIjoiU2FuZ2FuYWt5IFRlY2hub2xvZ3kiLCJzdWIiOiJlZDIzMzhmNy1hNmVkLTQ0YzMtODQ0OC1kYmE3YzgyNWNjMmUiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRzVqWUlMRXFYIiwiY29nbml0bzp1c2VybmFtZSI6InFhLmRhdHRhQGdtYWlsLmNvbSIsImN1c3RvbTpqb2JfdGl0bGUiOiJXZWIgRGV2ZWxvcGVyXC9FbmdpbmVlciIsImdpdmVuX25hbWUiOiJEYXR0YSIsImF1ZCI6IjM4MGRpaXRtc2JhN2Q2MjIwaDhsc3ExYnFvIiwiZXZlbnRfaWQiOiJlMjNlZmE3OC0yZWUxLTRhZDQtYTE3Yy0yZDU3OGE0ZmNjNTUiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTU4NTYzNjE2NywiZXhwIjoxNTg1NjM5NzY4LCJpYXQiOjE1ODU2MzYxNjgsImZhbWlseV9uYW1lIjoiTW9yZSIsImVtYWlsIjoicWEuZGF0dGFAZ21haWwuY29tIn0.Bh24yOKOdVUMV26LjDe6sL1kcTui8zcHPDuVqdGjjb5dfjBGL_eDbQTNzxGPfNlfrhyoxZFMNEXtp5Icb_-N52WoWbEDUX2qnIwCbceQdnT5VLvUHQziswb9LbXQyQab-1XZqkYQX30KGwVWe_Gdd3-vSdJ9f4TA5QBeg7tLJhwoF0kBd1sHu7nlmyzOrH1GnO80TVf_oTRQ_0jdC5fssBqTlZ-_OCU8_9JSz2R4MCall0r16soPSab5t-i8Rzby9snYo1WnNc-NQrDW3JKEcXGhFdAaqrtPuPH0pqdWeQ1LHWsjLcw-W4hqMq3WrCe_FuxhTtdXJxTkCkaHIcIBTA";
	String s3Uri = "";
	ValidatableResponse json;
	static String ENDPOINT = "https://api.theexchange.fanniemae.com/v1";
	
	public static void main(String[] args) {
		Response response;
		RequestSpecification request;
		request = given().header("Accept", "application/json").header("Authorization", authorizationCode);;
		response = request.when().get(ENDPOINT + "/connecticut-ave-securities/custom-download/status/" + requestId);

		System.out.println(response.asString());
		
	}
}
