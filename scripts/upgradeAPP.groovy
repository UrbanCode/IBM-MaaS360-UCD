/*
*	Licensed Materials - Property of IBM Corp.
*
*	Copyright (c) 2015 IBM. All rights reserved. 
*	
*	U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
*	GSA ADP Schedule Contract with IBM Corp.
*
*	Author: Tyson Lawrie & Glen Hickman
*	Date: 2016-07-06
*	Plugin: MaaS360 Utilities
*/

import com.urbancode.air.AirPluginTool
import java.util.Map
import java.util.LinkedHashMap
import java.io.File;
import java.util.Hashtable;
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpStatus
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.RequestEntity
import org.apache.commons.httpclient.methods.StringRequestEntity
import org.apache.commons.httpclient.methods.multipart.FilePart
import org.apache.commons.httpclient.methods.multipart.Part
import org.apache.commons.httpclient.methods.multipart.StringPart

import com.mobilefirst.fiberlink.WebServiceRequest
import com.mobilefirst.fiberlink.WebServices

//Pull in properties from Plugin UI
def apTool = new AirPluginTool(this.args[0], this.args[1])
props = apTool.getStepProperties()
final def workDir = new File('.').canonicalFile

def url = props['url']
def billing_id = props['billing_id']
def account_name = props['account_name'] //TestFLK_IBM_MobileCoC
def username = props['username']
def password = props['password']
def maas360hosted = props['maas360hosted']
def app_type = props['app_type']
def app_source = props['app_source']
def app_bundle_id = props['app_bundle_id']
def maintain_as_additional_version = props['maintain_as_additional_version']
def app_version = props['app_version']
def app_attributes = props['app_attribute']
def outFileName = props['outFile']
def outFile
if (outFileName) {
	outFile = new File(outFileName)
	if (!outFile.isAbsolute()) {
		outFile = new File(workDir, outFileName)
	}
}
def auth_token = props['auth_token']

//Start code for creating a WS request
WebServiceRequest request = new WebServiceRequest()

// Build the XML using the parameters
LinkedHashMap<String, String> p = new LinkedHashMap<String, String>()
p.put("appType", app_type)
p.put("appId", app_bundle_id)
p.put("maas360hosted", maas360hosted)
p.put("appSourceURL", "")
if (app_version) {
	p.put("appVersion", app_version)
}
p.put("maintainAsAdditionalVersion", maintain_as_additional_version)
if (app_attributes) {
	p.put("appAttributes", app_attributes)
}

String xml = request.createTemplateXML("appDetails",p)

//Build the multipart Parts
File app_file = new File(app_source)

Part[] parts = [new StringPart("app_details", xml), new FilePart("appSource", app_file, "multipart/form-data", "UTF-8")]

// Set request parameters
Hashtable<String, Object> parametersObjectList = new Hashtable<String, Object>()

Hashtable<String, String> headersList = new Hashtable<String, String>()
headersList.put("Accept", "application/xml")

Hashtable<String, String> paramsList = new Hashtable<String, String>()
paramsList.put("accountType", "Customer")
paramsList.put("accountName", account_name)
paramsList.put("adminEmailAddress", "")
paramsList.put("billingID", billing_id)
paramsList.put("appID", app_bundle_id)
paramsList.put("userName", username)
paramsList.put("password", password)

parametersObjectList.put("headers", headersList)
parametersObjectList.put("parameters", paramsList)
parametersObjectList.put("parts", parts)

//Create Request
System.out.println(xml)
System.out.println("Application Source: " + app_source)

try{
	request.createRequest(auth_token, url, WebServices.UpgradeAppURI.getURL(), 1, billing_id, parametersObjectList)

} catch (Exception e){
	println e.getMessage();
	println e.printStackTrace();
	System.exit 1
}

System.exit 0