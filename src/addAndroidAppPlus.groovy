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
def auth_token = props['auth_token']
def app_source = props['app_source']
def app_description = props['app_description']
def app_category = props['app_category']
def removeAppMDMRemoval = props['removeAppMDMRemoval']
def removeAppSelWipe = props['removeAppSelWipe']
def enforceAuthentication = props['enforceAuthentication']
def enforceCompliance = props['enforceCompliance']
def instantInstall = props['instantInstall']
def showInADP = props['showInADP']
def appOwner = props['appOwner']

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
if (app_description) {
	p.put("description", app_description)
}
if (app_category) {
	p.put("category", app_category)
}
//Default for program is No. Value defaulted in plugin.xml
p.put("removeAppMDMRemoval", removeAppMDMRemoval)
//Default for program is No. Value defaulted in plugin.xml
p.put("removeAppSelWipe", removeAppSelWipe)
p.put("enforceAuthentication", enforceAuthentication)
p.put("enforceCompliance", enforceCompliance)
p.put("instantInstall", instantInstall)
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

parametersObjectList.put("headers", headersList)
parametersObjectList.put("parameters", paramsList)
parametersObjectList.put("parts", parts)

//Create Request
System.out.println(xml)
System.out.println("Application Source: " + app_source)

try{
	request.createRequest(auth_token, url, WebServices.AddAndroidEnterpriseAppPlusURI.getURL(), 1, billing_id, parametersObjectList)

} catch (Exception e){
	println e.getMessage();
	println e.printStackTrace();
	System.exit 1
}

System.exit 0