/**
 * © Copyright IBM Corporation 2016.  
 * This is licensed under the following license.
 * The Eclipse Public 1.0 License (http://www.eclipse.org/legal/epl-v10.html)
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp. 
 *
 *	Author: Tyson Lawrie & Glen Hickman
 *	Date: 2016-07-06
 *	Plugin: IBM MaaS360
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
def maas360hosted = props['maas360hosted']
def account_name = props['account_name']
def username = props['username']
def password = props['password']
def app_source = props['app_source']
def removeApp = props['removeApp']
def restrictDataBackup = props['restrictDataBackup']
def showInADP = props['showInADP']
def appOwner = props['appOwner']
def app_bundle_id = props['app_bundle_id']
def app_description = props['app_description']
def app_category = props['app_category']
def app_attributes = props['app_attribute']
def auth_token = props['auth_token']

def outFileName = props['outFile']
def outFile
if (outFileName) {
	outFile = new File(outFileName)
	if (!outFile.isAbsolute()) {
		outFile = new File(workDir, outFileName)
	}
}

//Start code for creating a WS request
WebServiceRequest request = new WebServiceRequest()

// Build the XML using the parameters
LinkedHashMap<String, String> p = new LinkedHashMap<String, String>()
//Default for program is Yes. Value defaulted in plugin.xml
p.put("maas360hosted", maas360hosted)
p.put("appSourceURL", "")
if (app_description) {
	p.put("description", app_description)
}
if (app_category) {
	p.put("category", app_category)
}
//Default for program is No. Value defaulted in plugin.xml
p.put("removeApp", removeApp)
//Default for program is No. Value defaulted in plugin.xml
p.put("restrictDataBackup", restrictDataBackup)
if (app_attributes) {
	p.put("appAttributes", app_attributes)
}
//Handle Optional App Discovery Portal options
p.put("showInADP", showInADP)
if ((showInADP == "1" || showInADP == "2") && appOwner) {
	p.put("appOwner", appOwner)
} else if ((showInADP == "1" || showInADP == "2") && !appOwner) {
	System.out.println("App Owner Emaill Address is required when Show in ADP is set to 1 or 2")
	System.exit 1
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
	request.createRequest(auth_token, url, WebServices.iOSEnterpriseAppURI.getURL(), 1, billing_id, parametersObjectList)

} catch (Exception e){
	println e.getMessage();
	println e.printStackTrace();
	System.exit 1
}

System.exit 0