/*
* Licensed Materials - Property of IBM Corp.
* (c) Copyright IBM Corporation 2015. All Rights Reserved.
*
* U.S. Government Users Restricted Rights - Use, duplication or disclosure restricted by
* GSA ADP Schedule Contract with IBM Corp.
*/


package com.mobilefirst.fiberlink;

/**
*	Custom Enum Object to store all the Web Service Context Roots
* 
*	@version 20160606
*
*	@author Tyson Lawrie
*	@author Glen Hickman
*/
public enum WebServices {
	AuthenticationURI 				("/auth-apis/auth/1.0/authenticate/"),
	DeviceEnrollURI 				("/device-apis/devices/api-version/enrollDevice/"),
	CreateCustomerAccountURI 		("/account-provisioning/account/api-version/createCustomerAccount/"),
	SearchURI						("/device-apis/devices/api-version/search/"),
	UploadAppleMDMCertURI 			("/provisioning-apis/provisioning/api-version/uploadAppleMDMCert/"),
	iTunesAppStoreAppURI			("/application-apis/applications/api-version/addITunesApp/"),
	iOSEnterpriseAppURI				("/application-apis/applications/api-version/addIOSEnterpriseApp/"),
	AddIOSEnterpriseAppPlus			("/application-apis/applications/1.0/addIOSEnterpriseAppPlus/"),
	DeleteApp						("/application-apis/applications/api-version/deleteApp/"),
	SearchUser						("/user-apis/user/api-version/search/"),
	Policies						("/device-apis/devices/api-version/policies/"),
	CheckAccountAvailability		("/account-provisioning/account/api-version/checkAccountNameAvailability/"),
	ExtendAccount					("/account-provisioning/account/api-version/extendAccount/"),
	ConvertToCustomer				("/account-provisioning/account/api-version/convertToCustomer/"),
	GetDeviceGroupsURI				("/device-apis/devices/api-version/getDeviceGroups/"),
	SearchAppURI					("/application-apis/installedApps/api-version/search/"),
	GetWatchListsURI				("/device-apis/devices/api-version/getWatchLists/"),
	SetCustomerConfig				("/account-provisioning/account/api-version/setCustomerConfig/"),
	AddPlayAppURI					("/application-apis/applications/api-version/addPlayApp/"),
	CreateAdministrator				("/account-provisioning/administrator/api-version/createAdministrator/"),
	GetCustomerConfig				("/account-provisioning/account/api-version/getCustomerConfig/"),
	ExpireAccount					("/account-provisioning/account/api-version/expireAccount/"),
	AuthenticateAdministrator		("/device-apis/devices/api-version/authenticateAdministrator/"),
	CheckAdminAccountAvailability	("/account-provisioning/account/api-version/checkAdminAccountAvailability/"),
	GetDeviceEnrollmentSettings		("/provisioning-apis/provisioning/api-version/getDeviceEnrollSettings/" ),
	ConfigureDeviceEnrollSettings	("/provisioning-apis/provisioning/api-version/configureDeviceEnrollSettings/"),
	AddAndroidEnterpriseAppURI		("/application-apis/applications/api-version/addAndroidEnterpriseApp/"),
	GetAppDetailsURI				("/application-apis/applications/api-version/getAppDetails/"),
	CreatePartnerAccount			("/account-provisioning/account/api-version/createPartnerAccount/"),
	SetPartnerAccountConfig			("/account-provisioning/account/api-version/setPartnerAccountConfig/"),
	GetPartnerAccountConfig 		("/account-provisioning/account/api-version/getPartnerAccountConfig/"),
	GetSignedCSR 					("/provisioning-apis/provisioning/api-version/getSignedCSR/"),
	SearchAppsURI					("/application-apis/applications/api-version/search/"),
	DistributeAppURI				("/application-apis/applications/api-version/distributeApp/"),
	SearchDistributionsURI			("/application-apis/applications/api-version/searchDistributions/"),
	GetAppDistributionByDeviceURI	("/application-apis/applications/api-version/getAppDistributionByDevice/"),
	LockDeviceURI					("/device-apis/devices/api-version/lockDevice/"),
	LocateDeviceURI					("/device-apis/devices/api-version/locateDevice/"),
	WipeDeviceURI					("/device-apis/devices/api-version/wipeDevice/"),
	SelectiveWipeDeviceURI			("/device-apis/devices/api-version/selectiveWipeDevice/"),
	SendMessageURI					("/device-apis/devices/api-version/sendMessage/"),
	GetCore							("/device-apis/devices/api-version/core/"),
	ApproveDeviceMessagingSystem	("/device-apis/devices/api-version/approveDeviceMessagingSystem/"),
	GetSummaryAttributes			("/device-apis/devices/api-version/summary/"),
	GetCoreBulk						("/device-apis/devices/api-version/bulkcore/"),
	HardwareInventoryURI			("/device-apis/devices/api-version/hardwareInventory/"),
	SoftwareInstalledURI			("/device-apis/devices/api-version/softwareInstalled/"),
	MdSecurityComplianceURI			("/device-apis/devices/api-version/mdSecurityCompliance/"),
	SecurityApplicationsURI			("/device-apis/devices/api-version/securityApplications/"),
	BulkSummary						("/device-apis/devices/api-version/bulksummary/"),	
	PackageDistributionHistory		("/device-apis/devices/api-version/packageDistributionHistory/"),
	IdentityURI						("/device-apis/devices/api-version/identity/"),
	RevokeSelectiveWipe				("/device-apis/devices/api-version/revokeSelectiveWipe/"),
	CancelPendingWipe				("/device-apis/devices/api-version/cancelPendingWipe/"), 
	RemoveDevice					("/device-apis/devices/api-version/removeDevice/"),
	CheckActionStatus				("/device-apis/devices/api-version/checkActionStatus/"),
	RefreshDeviceInformationURI 	("/device-apis/devices/api-version/refreshDeviceInformation/"),
	SearchActionHistory				("/device-apis/devices/api-version/searchActionHistory/"),
	StopAppDistribution				("/application-apis/applications/api-version/stopAppDistribution/"),
	SetCustomAtributeURI 			("/device-apis/devices/api-version/setCustomAttributeValue/"),
	ManageDeviceEnrollments			("/device-apis/devices/api-version/manageDeviceEnrollments/"),
	SearchByDeviceGroup				("/device-apis/devices/api-version/searchByDeviceGroup/"),
	DeviceActionsURI 				("/device-apis/devices/api-version/deviceActions/"),
	UpgradeAppURI 					("/application-apis/applications/api-version/upgradeApp/"),
	UpgradeAppPlus					("/application-apis/applications/api-version/upgradeAppPlus/"),
	ResetDevicePasscodeURI 			("/device-apis/devices/api-version/resetDevicePasscode/"),
	DeviceDataViewURI 				("/device-apis/devices/api-version/deviceDataViews/"),
	SearchByWatchList 				("/device-apis/devices/api-version/searchByWatchList/"),
	ChangeDevicePolicy				("/device-apis/devices/api-version/changeDevicePolicy/"),
	MdNetworkInformation			("/device-apis/devices/api-version/mdNetworkInformation/"),
	CellularDataUsage 				("/device-apis/devices/api-version/cellularDataUsage/"),
	UpdateProvisioningProfileURI	("/application-apis/applications/api-version/updateProvisioningProfile/"),
	MarkAsPrimary					("application-apis/applications/api-version/markAsPrimary/"), // New as of Jan 2016
	AppUploadRequestStatus			("/application-apis/applications/api-version/appUploadRequestStatus/");
	
	private final String url;

    WebServices (String url) {
        this.url = url;
    }
    
    public String getURL() {
    	return this.url;
    }
}
