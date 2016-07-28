/*
* Licensed Materials - Property of IBM Corp.
* (c) Copyright IBM Corporation 2015. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/

package com.mobilefirst.fiberlink;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
*	Web Service Request Builder and Handler
* 
*	Originally developed by Fiberlink
*	Forked 2015-03-05.
*	@version 20160606
*
*	@author Tyson Lawrie
*	@author Glen Hickman
*/
public class WebServiceRequest {
	protected int statusCode = 0;
	
	private final HttpClient client = new HttpClient();

	private PostMethod postMethod = null;
	private GetMethod getMethod = null;
	private String jsessionId = null;
	private String responseBody = null;

	private Hashtable<String, String> parameters = new Hashtable<String, String>();
	private Hashtable<String, String> headers = new Hashtable<String, String>();
	private Part[] parts;
	
	/**
     * Description: Send Request method
	 * @param method: the type of HTTP Method to send
	 * @param responseToVerify: string to verify in the response
	 * @return whether the response was verified 
	 * @throws Exception
	 */
	private boolean sendRequest(HttpMethod method, String responseToVerify) throws Exception {
		boolean isResponseVerified = false;
		try {
			statusCode = client.executeMethod(method);
			responseBody = method.getResponseBodyAsString();
			System.out.println("Request URL :: " + method.getURI());
			System.out.println("------------------------------------Begin Debug: Request Headers----------------------------------------------------------\n");
			Header[] requestHeaders = method.getRequestHeaders();
			for(int cn = 0;cn<requestHeaders.length;cn++) {
				System.out.println(requestHeaders[cn].toString());
			}
			System.out.println("------------------------------------Begin Debug: Response Headers----------------------------------------------------------\n");
			Header[] responseHeaders = method.getResponseHeaders();
			for(int cn = 0;cn<responseHeaders.length;cn++) {
				System.out.println(responseHeaders[cn].toString());
			}
			System.out.println("------------------------------------End Debug----------------------------------------------------------\n");
			if (statusCode != HttpStatus.SC_OK) {
				throw new Exception("POST method failed :: " + statusCode + " and ResponseBody :: " + responseBody);
			} else {
				System.out.println("------------------------------------Response Start----------------------------------------------------------\n");
				System.out.println(responseBody+"\n");
				System.out.println("------------------------------------Resoonse End----------------------------------------------------------");
				if(null == jsessionId) {
					for(int cnt=0;cnt<responseHeaders.length;cnt++) {
//						System.out.println(headers[cnt].toString());
						if(responseHeaders[cnt].toString().contains("Set-Cookie: JSESSIONID=")) {
							jsessionId = getPatternMatches("JSESSIONID=(.+); Path", responseHeaders[cnt].toString(), false);
							System.out.println("JESSIONID: " + jsessionId);
							break;
						}
					}
				}
				if(responseBody.toLowerCase().contains(responseToVerify.toLowerCase())) {
					System.out.println("RESPONSE VERIFIED. Contains: " + responseToVerify);
					isResponseVerified = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Exception in sendRequest method..." + e.getMessage());
		}
		return isResponseVerified;
	}

	/**
     * Description: Create the Request Object
     * @param authToken: required token passed through from initial Authenticator() request
	 * @param baseURL: Initial base URL of host and port
	 * @param webServiceName: Context root for particular request
	 * @param methodType: Integer to say get or post request
	 * @param billingId: billing ID to populate into the request
	 * @param parametersObjectList: Map containing option parameters, headers and multi-part object
	 * @throws Exception
	 */
	public void createRequest(String authToken, String baseURL, String webServiceName, int methodType, String billingId, Hashtable<String, Object> parametersObjectList, String ...strings ) {
		try {
			String apiVersion;
			if (strings.length>0){
				if(strings[0].contentEquals("not-found")) {
					 apiVersion="1.0";
				} else{
					apiVersion= strings[0];
				}
			} else{
				apiVersion="1.0";
			}
			String uri = baseURL + webServiceName.replace("api-version",apiVersion) + billingId;
			
			System.out.println(">>>Generating Request URL");
			System.out.println("Base URL :: " + uri);
			
			initializeRequestHeadersAndParameters(parametersObjectList);
			switch(methodType) {
				case 0:
					// Setting PARAMETERS if any
					// this must be before setting the getMethod with URI
					// otherwise request will be created with base URI and no parameters
					if(0 != parameters.size()) {
						uri = formulateGetURI(uri, parameters);
					}
					getMethod = new GetMethod(uri);
					
					//Setting Required Authorization Header
					System.out.println(">>>Generating GET Request");
					getMethod.addRequestHeader("Authorization", "MaaS token=\""+authToken+"\"");
					
					// Setting HEADERS if any
					if(0 != headers.size()) {
						Enumeration<String> keys= headers.keys();
						while(keys.hasMoreElements()) {
							String key = keys.nextElement();
							getMethod.addRequestHeader(key, headers.get(key));
						}
					}
					
					System.out.println(">>>Sending GET Request");
					sendRequest(getMethod, "");
					break;
				case 1: postMethod = new PostMethod(uri);
					//Setting Required Authorization Header
					System.out.println(">>>Generating POST Request");
					postMethod.addRequestHeader("Authorization", "MaaS token=\""+authToken+"\"");
					
					// Setting HEADERS if any
					if(0 != headers.size()) {
						Enumeration<String> keys= headers.keys();
						while(keys.hasMoreElements()) {
							String key = keys.nextElement();
							postMethod.addRequestHeader(key, headers.get(key));
						}
					}
				
					// Setting PARAMETERS if any
					if(0 != parameters.size()) {
						postMethod.setParams(setMethodParams(postMethod, parameters));
						
					}
					
					//START special case
					//If you need to send request body without any encoding or name pair values : like ws which involves bulk like GetCoreAttributesBulk.
					if(parametersObjectList.containsKey("body")){
						org.apache.commons.httpclient.methods.StringRequestEntity sre;
						@SuppressWarnings("unchecked")
						Hashtable<String, String> a = (Hashtable<String, String>) parametersObjectList.get("body");
						sre= new StringRequestEntity(a.get("payload"),"application/xml","UTF-8");
						postMethod.setRequestEntity(sre);
					}
					//END of the special case
					
					//Setting multipart body post if required.
					if(null != parts) {
						postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
					}
					
					System.out.println(">>>Sending POST Request");
					sendRequest(postMethod, "");
					break;
			default:
				break;
			}
		} catch(Exception e) {
			
		}
	}
	
	/**
	 * prettyFormatXML translates the input xml and creates the indentation you would like to achieve
	 * @param input: XML String
	 * @param indent: Integer for how much indent to specify
	 */
	public static String prettyFormatXML(String input, Integer indent) {
		try {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        //transformerFactory.setAttribute("indent-number", indent);
	        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", indent.toString());
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); 
	    }
	}
	
	/**
	 * formulateGetURI method formulates the get request URI
	 * @param URI: uri to be formulated
	 * @param params: uri to be formulated using params
	 * @return returns the formulated uri
	 * @throws Exception 
	 */
	public String formulateGetURI(String URI, Hashtable<String, String> params) throws Exception {
		String newURI = null;
		String postURI = "?";
		try {
			Enumeration<String> keys= params.keys();
			while(keys.hasMoreElements()) {
				String key = keys.nextElement();
				postURI = postURI + key +"="+params.get(key)+"&"; 
			}
			postURI=(String) postURI.subSequence(0, postURI.length()-1);
			newURI = URI + postURI.replaceAll(" ", "%20"); 
			//TODO - Further URL Encoding
		} catch(Exception e) {
			throw new Exception(e);
		}
		return newURI;
	}

	/**
	 * setMethodParams sets methods parameters
	 * @param method: method whose parameters to be set
	 * @param params: parameter list to be set
	 * @return HttpMethodParams	Object of HttpMethodParams
	 */
	public HttpMethodParams setMethodParams(HttpMethod method, Hashtable<String, String> params) {
		HttpMethodParams httpMethodParams = new HttpMethodParams();
		Enumeration<String> keys= params.keys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(method instanceof PostMethod) {
				((PostMethod) method).setParameter(key, params.get(key));
				System.out.println(key + " :: " + params.get(key) + "\n");
			}
		}
		return httpMethodParams;
	}
	
	/**
	 * 
	 * @param patternToMatch
	 * @param response
	 * @param relpaceWhiteSpaces
	 * @return
	 */
	public String getPatternMatches(String patternToMatch, String response, boolean relpaceWhiteSpaces) {
		Pattern pattern = null;
		String patternValue = null;
		try {
			patternToMatch = "(?i)"+patternToMatch+"(?i)";
			if(relpaceWhiteSpaces) {
				response = response.replaceAll("\\s","");
			}

			pattern = Pattern.compile(patternToMatch);

			// Now create matcher object.
			Matcher matcher = pattern.matcher(response);
			while (matcher.find()) {
				patternValue = matcher.group(1);
				break;
			}
			if(null != patternValue) {
				if(patternValue.contains("</string>")) {
					patternValue = patternValue.split("</string>")[0];
				}
			} 
		} catch(Exception e) {
			System.out.println("Exception in getPatternMatches method..." + e.getMessage());
		}
		return patternValue;
	}
	
	/**
	 * initializeRequestHeadersAndParameters iterates through Hashtable of attributes and sets against correct level in object
	 * @param objectList: hashtable of parameters
	 */
	@SuppressWarnings("unchecked")
	public void initializeRequestHeadersAndParameters(Hashtable<String, Object> objectList) {
		try {
			if(null != objectList) {
				if(0 != objectList.size()) {
					if(null != objectList.get("parameters")) {
						parameters.clear();
						parameters = (Hashtable<String, String>) objectList.get("parameters");
					}
					if(null != objectList.get("headers")) {
						headers.clear();
						headers = (Hashtable<String, String>) objectList.get("headers");
					}
					if(null != objectList.get("parts")) {
						parts = (Part[]) objectList.get("parts");
					}
				}
			}
		} catch(Exception e){
		}
	}
	
	/**
	   * createAuthTemplateXML creates AuthTemplate xml
	   * @param params HashMap of parameters for AuthTemplate
	   * @return String AuthTemplate
	   */
	public final String createTemplateXML(String xmlParent, Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(xmlParent).append(">").append("\n");

		for (String key : params.keySet()) {
			sb.append("<").append(key).append(">").append(params.get(key)).append("</").append(key).append(">").append("\n");
		}

		sb.append("</").append(xmlParent).append(">").append("\n");

		return sb.toString();
	}
}