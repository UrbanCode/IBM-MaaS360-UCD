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
	private final void sendRequest(PostMethod post) {
		try {
			HttpClient client = new HttpClient();
			int statusCode = client.executeMethod(post);
			System.out.println("------------------------------------Begin Debug: Request Headers----------------------------------------------------------\n");
			Header[] requestHeaders = post.getRequestHeaders();
			for(int cn = 0;cn<requestHeaders.length;cn++) {
				System.out.println(requestHeaders[cn].toString());
			}
			System.out.println("------------------------------------Begin Debug: Response Headers----------------------------------------------------------\n");
			Header[] responseHeaders = post.getResponseHeaders();
			for(int cn = 0;cn<responseHeaders.length;cn++) {
				System.out.println(responseHeaders[cn].toString());
			}
			System.out.println("------------------------------------End Debug----------------------------------------------------------\n");
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("POST method failed: "
						+ post.getStatusLine());
			} else {
				System.out.println("POST method succeeded: "
						+ post.getStatusLine());
				String httpResponse = post.getResponseBodyAsString();
				System.out.println(httpResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public void createAndSendRequest(String xml, String url_auth, String billing_id) {
		PostMethod post = new PostMethod(url_auth + billing_id + "/");
		try {
			RequestEntity requestEntity = new StringRequestEntity(xml,
					"application/xml", "UTF-8");
			post.setRequestEntity(requestEntity);
			post.addRequestHeader("Accept", "application/xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sendRequest(post);
	}
}
