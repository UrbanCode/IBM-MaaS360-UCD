/*
* Licensed Materials - Property of IBM Corp.
* (c) Copyright IBM Corporation 2015. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/

package com.mobilefirst.fiberlink;

import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

/**
*	Authenticator authenticates against the MaaS360 authetication web services 
*	using the customer's MaaS administrator user name and password.
* 
*	Originally developed by Fiberlink
*	Forked 2015-03-05.
*	@version 20160606
*
*	@author Tyson Lawrie
*	@author Glen Hickman
*/
public class Authenticator 
{	
	static final String ADMIN_ROOT_TAG = "maaS360AdminAuth";
	
	/**
     * Description: Handle sending of request
     * @param post: the object to send
     */
	private final void sendRequest(PostMethod post) throws Exception {
		HttpClient client = new HttpClient();
		int statusCode = client.executeMethod(post);

		
		// Check HTTP status first
		if (statusCode != HttpStatus.SC_OK) {
			throw new Exception("HTTP request failed with status: " + post.getStatusLine());
		}
		
		// Get the response body
		String httpResponse = post.getResponseBodyAsString();
		System.out.println("API Response: " + httpResponse);
		
		// Validate credentials
		if (httpResponse != null) {
			// Check for HTML login page error (wrong credentials returns HTML, not XML)
			if (httpResponse.contains("username or password you have entered is incorrect") 
					|| httpResponse.contains("account is locked")) {
				throw new Exception("LOGIN FAILED: URL must be https://services.m4.maas360.com");
			}
			
			// Check for XML errorCode (non-zero = failure)
			if (httpResponse.contains("<errorCode>") && !httpResponse.contains("<errorCode>0</errorCode>")) {
				throw new Exception("LOGIN FAILED: Invalid credentials. Check username/password and other parameters.");
			}
			
			// Check if response is HTML instead of expected XML (indicates auth failure)
			if (httpResponse.contains("<!DOCTYPE html>") && !httpResponse.contains("<authToken>")) {
				throw new Exception("LOGIN FAILED: Received HTML login page instead of auth token. Credentials may be invalid.");
			}
		}
		
		System.out.println("Authentication completed successfully.");
	}

	/**
     * Description: Create required pre-defined <authRequest> xml structure
     * @param params: Map of parameters to build out into required xml structure
     * @return: XML String
     */
	public final String createAuthTemplateXML(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<authRequest>").append("\n").append("<")
				.append(ADMIN_ROOT_TAG).append(">").append("\n");

		for (String key : params.keySet()) {
			sb.append("<").append(key).append(">").append(params.get(key))
					.append("</").append(key).append(">").append("\n");
		}
		sb.append("</").append(ADMIN_ROOT_TAG).append(">").append("\n")
				.append("</authRequest>").append("\n");

		return sb.toString();
	}
	
	/**
     * Description: Create initial post method
     * @param xml: the xml body payload
     * @param url_auth: URL to post
     * @param billing_id: the unique billing ID for the Fiberlink Customer
     * @return: PostMethod
     */	
	public void createAndSendRequest(String xml, String url_auth, String billing_id) throws Exception {
		PostMethod post = new PostMethod(url_auth + billing_id + "/");
		RequestEntity requestEntity = new StringRequestEntity(xml, "application/xml", "UTF-8");
		post.setRequestEntity(requestEntity);
		post.addRequestHeader("Accept", "application/xml");
		sendRequest(post);
	}
}
