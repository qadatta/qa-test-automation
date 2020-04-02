package com.automation.cucumber;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class APIExample {

	
	public static void main(String[] args) {
	
		Client client;
		WebTarget webTarget;
		Response response;

		Invocation.Builder invocationBuilder;
		String authorizationCode = "Auth code stored in this variable after login";
		String requestId = "af55dc10-8790-45a2-94e8-133ec13cd115"; //"af55dc10-8790-45a2-94e8-133ec13cd115";

		client = ClientBuilder.newClient();
		String URL = "https://api.theexchange.fanniemae.com/v1/connecticut-ave-securities/custom-download/status/"
				+ requestId;
	
		webTarget = client.target(URL);
		invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("Authorization", authorizationCode);
		response = invocationBuilder.get();
		String jsonRespnse = response.readEntity(String.class);

		System.out.println(jsonRespnse);
	}
}
