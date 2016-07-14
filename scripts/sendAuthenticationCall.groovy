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
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.HttpStatus
import org.apache.commons.httpclient.methods.PostMethod
import org.apache.commons.httpclient.methods.RequestEntity
import org.apache.commons.httpclient.methods.StringRequestEntity
import com.mobilefirst.fiberlink.Authenticator
import com.mobilefirst.fiberlink.WebServices

def apTool = new AirPluginTool(this.args[0], this.args[1])
props = apTool.getStepProperties()
final def workDir = new File('.').canonicalFile

def url = props['url']
def billing_id = props['billing_id']
def platform_id = props['platform_id']
def app_id = props['app_id']
def app_version = props['app_version']
def app_access_key = props['app_access_key']
def username = props['username']
def password = props['password']
def outFileName = props['outFile']
def outFile
if (outFileName) {
    outFile = new File(outFileName)
    if (!outFile.isAbsolute()) {
        outFile = new File(workDir, outFileName)
    }
}
def url_auth = url + WebServices.AuthenticationURI.getURL()

System.out.println("URL :: " + url_auth)

Authenticator authenticator = new Authenticator()

// Build the XML using the parameters
LinkedHashMap<String, String> p = new LinkedHashMap<String, String>()
p.put("billingID", billing_id)
p.put("platformID", platform_id)
p.put("appID", app_id)
p.put("appVersion", app_version)
p.put("appAccessKey", app_access_key)
p.put("userName", username)
p.put("password", password)

String xml = authenticator.createAuthTemplateXML(p)

try {
	authenticator.createAndSendRequest(xml, url_auth, billing_id)

} catch (Exception e){
	println e.getMessage();
	println e.printStackTrace();
	System.exit 1
}

System.exit 0