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
def auth_token = props['auth_token']
def app_bundle_id = props['app_bundle_id']
def app_type = props['app_type']
def app_version = props['app_version']
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

//Generate HTTPParameters
Hashtable<String, Object> parametersObjectList = new Hashtable<String, Object>()

Hashtable<String, String> headersList = new Hashtable<String, String>()
headersList.put("Accept", "application/x-www-form-urlencoded")

Hashtable<String, String> paramsList = new Hashtable<String, String>()
paramsList.put("appType", app_type);
paramsList.put("appId", app_bundle_id);
paramsList.put("appVersion", app_version);

parametersObjectList.put("headers", headersList)
parametersObjectList.put("parameters", paramsList)

//Create Request
try{
	request.createRequest((auth_token, url, WebServices.MarkAsPrimary.getURL(), 1, billing_id, parametersObjectList)
} catch (Exception e){
	println e.getMessage();
	println e.printStackTrace();
	System.exit 1
}

System.exit 0