# README for Oracle Cloud Integration Sample

## Sample Name

Excel to CSV Converter

## Author

Kaushal Joshi

## Source Code Location

* GitHub: [https://github.com/oracle/cpq-cloud-with-paas-samples](https://github.com/oracle/cpq-cloud-with-paas-samples)
* OTN: [http://www.oracle.com/technetwork/indexes/samplecode/extend-cpq-samples-3158636.html](http://www.oracle.com/technetwork/indexes/samplecode/extend-cpq-samples-3158636.html)

## Oracle Cloud Products Involved

* Oracle Java Cloud Service - SaaS Extension
* Oracle Configure, Price, and Quote Cloud (2016 Release 1 a.k.a. 2016.1)
* Oracle JDeveloper Cloud specific version (11.1.1.7.1) / Oracle Enterprise pack for Eclipse available at [this](http://www.oracle.com/technetwork/topics/cloud/downloads/index.html) location on OTN
* Note: Project installation instructions below are for the JDeveloper IDE

## Demonstrates

This sample demonstrates the following aspects of Oracle cloud integration:

#### Primary Goals

*   Using OAuth 2.0 as an authorization mechanism when Oracle CPQ Cloud invokes a RESTful web service in JCS - SaaS extension. 
*   Using an OWSM-based security policy to secure the web service for this interaction.

#### Secondary Goals

*   One way to set up an OAuth 2.0 client credentials flow with JCS - SaaS EXtension via BML in CPQ Cloud.

## Functional Overview

Sales representatives can use the Excel to CSV Converter web service to update a quote using additional line-item information provided in an Excel file (.xlsx). Out of the box, CPQ supports this functionality only when using CSV files. For example, with this utility, a sales user can get a new quote after uploading an Excel file containing a list of options/SKUs that a customer bought. Another use case is when a sales user uploads an Excel file with new corporate rates for a set of products and would like to apply the new pricing to a quote.

## Technical Overview

The Excel to CSV Converter web service is deployed to JCS - SaaS Extension. The service exposes a REST endpoint that takes an “.xlsx” file as input in JSON format and responds with the equivalent .csv data in JSON format. The CPQ application is configured by an admin user to call this endpoint within the appropriate workflow, so that the Excel file uploaded by the sales user gets transformed and used as expected by the use case. The web service endpoint is protected using OAuth 2.0. The CPQ web application uses a confidential client to obtain an access token to the JCS - SaaS Extension resource, and using this token, it gains access to the service. This sample demonstrates the setup required to protect web services with OAuth 2.0 in JCS - SaaS Extension, and to show the setup required to access the service using a confidential client.

## Disclaimer

All sample code is provided by Oracle for illustrative purposes only. The objective of this sample is only to demonstrate the goals defined above and may not represent other best practices, functional or technical.These sample code examples have not been thoroughly tested under all conditions. Oracle, therefore, cannot guarantee or imply security, reliability, serviceability, or function of the sample code. All sample code contained herein are provided to you “AS IS” without any warranties of any kind. The implied warranties of non-infringement, merchantability and fitness for a particular purpose are expressly disclaimed.

## User and Role Setup

### For Setting up the Sample

1.  A user with the _Java Administrator_ Role is required to deploy an application to the JCS - SaaS Extension instance. Refer to [Getting a JCS - SaaS Extension Subscription](http://www.oracle.com/pls/topic/lookup?ctx=clouddevportal&id=GUID-9C1BA413-EB5D-429C-AECA-4069995385EF) for more details about the _Java Administrator_ Role. Refer to Chapter 7, "Managing Users and Roles" in [Getting Started with Oracle Cloud](http://www.oracle.com/pls/topic/lookup?ctx=cloud&id=CSGSG166) for more details on creating users.
2.   Register the JCS - SaaS Extension as an OAuth resource server and register an OAuth client with access to the resource. Refer to the note mentioning "Managing OAuth Resources and Clients" in [Authorization Strategies for 
JCS-SaaS Extension](http://www.oracle.com/pls/topic/lookup?ctx=clouddevportal&id=OCPSI-GUID-19C1D17A-195D-4CB3-AD04-0224F629165B) for more details.
3.  For setting up CPQ Cloud, use an existing user of type FullAccess. Refer to [Understanding Your CPQ Cloud Customization and Integration Features](http://www.oracle.com/pls/topic/lookup?ctx=clouddevportal&id=OCPSI-GUID-FB2AEB97-1623-4E4E-BD2D-02EEB275DC07) for more details on one way to administer the CPQ experience for this sample.

### For Running the Sample

1.  For CPQ Cloud, create a new user of type Sales Agent, or use an already existing user of that type. Refer to [Understanding Your CPQ Cloud Customization and Integration Features](http://www.oracle.com/pls/topic/lookup?ctx=clouddevportal&id=OCPSI-GUID-FB2AEB97-1623-4E4E-BD2D-02EEB275DC07) for more details. As an alternative, use a Full Access user like you did to set up the sample.
2.  Use the Transaction Manager to access an existing quote and run the sample as per the CPQ experience you set up via the administration features.  If an existing quote is not avaialble, create a new quote using the UI.

## Install Instructions

### JCS - SaaS Extension

1.  Clone the git repository to a local working directory.
2.  Set up the contents of */JCS-SX* folder as a web application, to generate a WAR. To see JDeveloper specific instructions for this step refer to the next sub-section "For JDeveloper Users".
  *  In addition to the Java code provided, refer to the **Dependencies** topic below to understand the dependencies for compile-time and runtime.
  *  You will have to download these dependencies since they are not provided along with the sample code.
  *  Include dependencies in the WAR if marked as runtime and not provided by the JCS - SaaS Extenstion environment.
  *  Compile and create the WAR package.
4.  Deploy the application to your JCS - SaaS Extension instance. Refer to [Deploying Applications Individually Using JCS - SaaS Extension Control](http://www.oracle.com/pls/topic/lookup?ctx=clouddevportal&id=OCPSI-GUID-8C5519F9-FB34-4D13-B313-BF0C7230A339) for more details.

**Notes**: 
 * The package also includes a unit test (XLSX2CSVTest.java) which works with a resource files test.xlsx and result.csv, to help you determine if your convertor setup will run correctly. You can exclude the test when setting up  the sample project and generating the WAR. It you choose to include the test in compilation, you will need to provide the Junit 4 dependency. 
 * The test.xlsx file is not a valid file with CPQ part numbers and quantity; it serves the purpose only of testing the conversion from .xlsx to .csv

#####For JDeveloper Users:
1. Create a web project in a new generic application or existing application.
2. Select the project in Application Navigator and click on File and select Import. In the Import popup select "Web Source" and click ok to bring up Web Source popup. Click "Copy From:" browse button, navigate to cloned git repository in your working directory and select public_html folder. Click OK to close the "Web Source" popup.
3. Open the Import Popup again like in step 2 and select Java Source. Select the src folder from the cloned repository by clicking on browse button of "Copy From:" option. Click OK.
4. Right-click on the header of the project, and select "Project Properties" from the popup menu. In the Project Properties dialog box, select "Libraries and Classpath" in the left pane. This will reveal a list of classpath entries. For a Web project "JSP Runtime" should already be available, if not, click "Add Library..." and add it from the list of available libraries under extension, also select "Web Service Data Control" and "JUnit 4 Runtime" and click OK. If "Junit 4 Runtime" library is not available then follow instructions to install [JUnit extension to Jdev](http://www.oracle.com/technetwork/articles/adf/part5-083468.html) and repeat the step to add library. Next Click on the "Add JAR/Directory...", to add the dependency libraries(Users are required to download dependency libraries from external sources. They are not packaged with the sample apps. Please refer the table below for list of libraries) Click OK to close the project properties dialog box and then click Save.
  *  Refer to the Dependencies topic below to understand the dependencies for compile-time and runtime.
5. Build the project and ensure there are no errors.
6. Go to the project properties again. Select "Deployment" in the left pane. Delete the default war under "Deployment Profiles:" if any. Click on "New...". Select WAR File from "Archiver Type:". Give a meaningful name and click OK to navigate to "Edit WAR Deployment Profile Properties".In the right pane give the suitable location of war file and leave the remaining as they are. In the left pane select "Library Dependencies". Right pane shows all the libraries that were added in point 4. Leave JSP Runtime selected and select the remaining as given in table below. Delesect the others. Click OK to close the dialog boxes and click save to save the changes.
     * If you do not see a WAR option in the Archive Type, you are probably looking at Application Properties, instead of Project Properties as suggested by the step.
7. Right click the project and select "Deploy" > "warfile". In the popup, select "Deploy to WAR", click Next and Finish to generate a war file.
8. Note that JDeveloper may inject a &lt;context-root&gt; element in weblogic.xml that is already provided in the sample. This will determine the value you should use for replacing the placeholder of {application-name} for bml_advancedModify.txt during step 8 of the CPQ Cloud setup steps later on.

###Dependencies
The following table lists the libraries that are necessary in order for this sample application to work correctly. These are not provided along with the sample app, but will be necessary for compiling and running the application succesfully.

<table>
        <tr>
            <th class="style8" style="width: 349px;">
                <b style="text-align: center;">Dependency Library</b></th>
            <th class="style2" style="width: 479px;">
                <b>Purpose</b></th>
            <th class="style4" style="font-weight: bold; text-align: center;">
                Compile-time</th>
            <th class="style4" style="font-weight: bold; text-align: center;">
                Runtime</th>
            <th class="style4" style="font-weight: bold; text-align: center;">
                Provided in JCS-SaaS Extensions runtime environment as a shared library </th>
        </tr>
        <tr>
            <td class="style8" style="width: 349px;">
                Apache POI OOXML v3.13</td>
            <td class="style2" style="width: 479px;">
                For reading .xlsx files</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
            <td class="style3" style="text-align: center;">
                No</td>
        </tr>
        <tr>
            <td class="style1">
                Open CSV v2.1</td>
            <td class="style2">
                For writing standard csv files</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
            <td class="style3" style="text-align: center;">
                No</td>
        </tr>
        <tr>
            <td class="style1">
                Jersey bundle 1.9</td>
            <td class="style2">
                For handling RESTful services</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
        </tr>
        <tr>
            <td class="style1">
                Weblogic ws-api</td>
            <td class="style2">
                For appying SecurityPolicy annotation on REST services</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
            <td class="style3" style="text-align: center;">
                Yes</td>
        </tr>
</table>

### CPQ Cloud

1.  Log in to the CPQ Cloud instance as a full access user.
2.  To begin, set up a transaction attribute that can be used from the transaction layout to upload a file in .xslx format.
  * To do this, in the admin console, use the Commerce and Documents section link to go to Process Definition. Select one of the process(es) listed, and navigate to its documents. Next, at the top level document (also called Main), navigate to the attributes list. Click the Add button at the bottom of the list, and proceed to add a new attribute of type "file attachment"; enter "**attr_FileAttachment**" as the variable name.
  * After clicking on Add, in the new detail screen that appears, select the "allowed extensions" to be "xlsx" since the sample handles only .xlsx files.  Click update to commit your changes.
3.  Next, set up an action that can be used from the transaction layout to trigger the JCS - SaaS Extension web service call to convert the Excel file to CSV format, and subsequently, add the products to the transaction.
  *   Go to Process Definitions, and navigate to its list of documents. In the Main document dropdown, select the actions list. Proceed to add a new action of type "Modify"; set the label to "Add parts from file", and set the variable name to "**action_AddLineItems**". Click "Add".
  *   In the action administration screen that appears, under General tab, set the "Advanced Validation" option to "Modify without saving or validating".
  *  Note: You'll need to come back to this administration screen in step 8.
4.  Add another transaction attribute to help show the end user a summary of the results of the excuted action every time that the action is triggered.
  *   To do this, in the admin console, use the Commerce and Documents section link to go to Process Definition. Select one of the process(es) listed, and navigate to its documents. Next, top the top level document (also called Main), navigate to the attributes list. Click the Add button at the bottom of the list, and proceed to add a new attribute of type "Text"; enter  "**attr_StatusText**" as the variable name.
5.  Add both the attributes and the action to the transaction layout so that the end user has a means to upload a file, and click a button to trigger the workflow.
  *   Go to Process Definitions, and in the main document dropdown, navigate to the Desktop Layout. Using the add menu at the top, add a panel layout and an action strip. Next, add the 2 attributes created above to the layout as well as the action to the action strip.
  *   Click preview, and if acceptable, save the layout.
6.  Create a datatable with secure columns to hold credentials of the client you registered with JCS - SaaS Extension as well as those of a full access user that you can use to update the transaction via the CPQ REST API (Refer to [Identifying CPQ Cloud RESTful Web Services to Invoke from a JCS - SaaS Extension Application](http://www.oracle.com/pls/topic/lookup?ctx=clouddevportal&id=OCPSI-GUID-FC78CA5F-EB55-4B99-B9FF-EC3CAA9C6DBA) for details)  .
  *   In the admin console, click the Data Tables link located under Developer Tools. In the Default folder, add a datatable named "Credentials". Once the table is added, in the schema tab, add the following columns:
    *   integrationName, String type  - This is a key field. This will hold an admin-friendly unique name for the client and users
    *   username, Secure type
    *   password, Secure type
    *   desc, String type
  *  Next, add a couple of records.
    *  Find the  OAuth client you registered in My Services for your JCS - SaaS Extension instance (during the User And Role Setup->Setting Up the Sample, step 2). Using information of that client, add a row in the data table, setting username={client Id}, password={client secret} and integrationName="**oauth_client**"
    *  Store another row with credentials of a full-access user for CPQ.  Set integrationName="**cpq_admin****_user**"
  *  Deploy this data table, to make the data available to the BMQL script written in **utilLib_getBasicAuthCredentials**.
7.  Write the following three library functions to help make your action script modular. The content of these scripts can be found in _.txt_ files of the source package of the same name. When defining the libraries, keep the variable name of the function the same as the name of the .txt files, else linkage in later steps will be affected.
  *   Utility library function - **utilLib_getBasicAuthCredentials** - This script helps get your retrieve credentials from the "Credentials" data table and base64 encode them, ready to be used while building headers for the REST calls.
    1. The signature of the function should be configured in the editor, as below:- (use exact names for the parameters, to make sure the script body provided in the text file, can compile)
        * Return type: String
        * Parameter1 : String *accountName*
        * Parameter2 : String *defaultReturnValue*
    2. Save the library function.
    3. Deploy the library function; unless this function is deployed, the following commerce library functions cannot reference it.
  *   Commerce library function - **cmLib_addLineItemREST**  - This script is pre-configured to use the CPQ transaction REST API to update a given transaction by adding a line item you provide.
    1. The signature of the function should be configured in the editor, as below:- (use exact names for the parameters, to make sure the script body provided in the text file, can compile)
        * Return type: String
        * Parameter1 : String *bs_id*
        * Parameter2 : String *jsonData*
        * Parameter3 : String *basicAuthCredentials*
    2. Under the "Library function(s)" tab in the function editor, add the utility library **utilLib_getBasicAuthCredentials** as a reference.
    3. Save the library function.
  *   Commerce library function - **cmLib_cleanSaveMainDocREST** - This script is pre-configured to use the CPQ transaction REST API to call a particular action (cleanSave) with the payload you provide to update that attribute of a given transaction.
    1. The signature of the function should be configured in the editor, as below:- (use exact names for the parameters, to make sure the script body provided in the text file, can compile)
        * Return type: String
        * Parameter1 : String *bs_id*
        * Parameter2 : String *jsonData*
        * Parameter3 : String *basicAuthCredentials*
    2. Under the "Library function(s)" tab in the function editor, add the utility library **utilLib_getBasicAuthCredentials** as a reference.
    3. Save the library function.
8.  Finally, write the appropriate BML scripts on the action created in step 4 in order to bring together the setup.
  *   Go to the action administration screen (Process Defintion{process} ->List: Documents->{main document}-> List:Actions-> {your action}).
  *   In the General Tab, in "Advanced Modify - After Formulas", select "define.." and click on the Define Function button
    1.  For inputs, select the following items: (you can come back to this screen by clicking on "Reselect" on the script screen):
        *   **_system****_buyside****_id**  (System Variable Name tab)
        *   **attr_FileAttachment** and **attr_StatusText** (Variable name for Transaction tab)
        *   Three of the library functions added above - **utilLib_getBasicAuthCredentials**, **cmLib_addLineItemREST**  and **cmLib_cleanSaveMainDocREST** (Library functions tab)
    2.  On the script screen, paste the script as provided in **bml_advancedModify**. Then, fill in the essential details in appropriate placeholders within the script.
        *  {JCS-SX-OAuth-token-endpoint}  - This information can be obtained from the interface you used to register the OAuth client during the User and Roles setup in this README.
        *  {JCS-SX-OAuth-Resource} - This is the URI of the OAuth Resoure you registered during the User and Roles setup in this README
        *  {JCS-SX-baseuri} - This is the base URI of the JCS-SaaS Extension application
        *  {application-name} - the name you gave to your application, when you deployed it on JCS - SaaS Extension. In technical terms, this is actually the context-root of the web application. Since this sample does not have a &lt;context-root&gt; element in the weblogic.xml, the name you give your application on the JCS-SX console will become the context-root and hence should be used here. However, if you chose to provide your own web-application-context-root in another way, use that context-root value here to ensure that your application is used by this BML.
        *  {CPQ-base-uri} - the base URI of your CPQ instance
        *  Remember to remove the curly brackets "{", "}" when you replace the placeholders with real values.
    3. Save and Close
  *   In the Destination tab, select "Define Destination Rule" and click on the Define Function button.
    1.  For inputs, select **_transaction****_document****_link**
    2.  On the script screen, paste the script as provided in **bml_destination** and "Save and Close".
  *   Finally, commit these changes by clicking "Update" on the action administration screen.
9.  Ultimately, all these changes need to be deployed to be actually made "live" to the end user. To achieve this, deploy the commerce process and ensure that the deploy is successful.

## Running the Sample

To run the sample:

1.   Launch the CPQ Cloud application in your browser.
2.   If you are not already authenticated, you will be prompted to login. Login as a sales agent user type.
  *  **Note**: the permissions available to a Sales Agent user type for your CPQ setup are very cusomizable. If you find that the sales agent user type does not have permission to Create/Save a transaction, use a full access user instead, and use the home page navigation to follow through the next steps. The behavior with respect to sample app, will be similar.
3.   Once you are logged in, you will see a navigation bar that will allow you to go to the transaction manager of the process you set up earlier. Click it, and you'll see the list of quotes available. A "New Transaction" button should be available too. Either select an existing quote OR create a new transaction (quote) and you'll be redirected to the transaction layout.
4.   In this layout, find the panel you set up earlier. Using the browse button for **attr_FileAttachment** ,  upload a .xlsx file with 2 columns and no headers.  The first column should be part numbers, the second column should be quantity.  After uploading the file, click "Save" on the transaction . The file should now appear in the layout as attached.
  *  To find valid part numbers, you should use the "Parts Search" that appears on your home screen when you logged in.
  
6.   To execute the action, click on the **action_AddLineItems** button  (probably labelled "Add parts from file").  The quote will refresh, and the **attr_StatusText** will show what the action achieved. If it worked correctly, this action would have added the parts from the file as line items to this quote. The line items will be visible in the Line Item Grid in the transaction layout.

## Additional Information

###Customizing sample scripts to work with a different commerce resource
If the REST transaction resource "commerceDocumentsOraclecpqoTransaction" is not available on your CPQ Cloud instance, you will need to modify some BML scripts in the sample, while following the installation steps for CPQ Cloud, as discussed below:

  *  Locate the name of the REST transaction resource you want to use. Refer to [Identifying CPQ Cloud RESTful Web Services to Invoke from a JCS - SaaS Extension Application](http://www.oracle.com/pls/topic/lookup?ctx=clouddevportal&id=OCPSI-GUID-FC78CA5F-EB55-4B99-B9FF-EC3CAA9C6DBA) for details. Also find the variable name of the commerce process linked to this resource.
  *  In both **cmLib_cleanSaveMainDocREST** and **cmLib_addLineItemREST**, change the _resourceName_ literal to this resource's name.  Do not forget the "/" at the end.
  *  In **bml_advancedModify**, replace the one occurence of "_oraclecpqo_" with the variable name of the commerce process.

###Registering an OAuth Client and an OAuth Resource in JCS - SaaS Extension
Refer to the note mentioning "Manage OAuth Resources and Clients" in [Authorization Strategies for JCS-SaaS Extension](http://www.oracle.com/pls/topic/lookup?ctx=clouddevportal&id=OCPSI-GUID-19C1D17A-195D-4CB3-AD04-0224F629165B) for more details

###Visit the Samples Solution Website
For more details about this sample and other related samples, refer to Oracle Cloud Solutions at <https://cloud.oracle.com/developer/solutions>.

## Uninstall Instructions

### JCS - SaaS Extension

Undeploy the application from JCS - SaaS Extension. Refer to the Undeploy section in [Deploying Applications Individually to JCS - SaaS Extension](http://www.oracle.com/pls/topic/lookup?ctx=clouddevportal&id=OCPSI-GUID-8C5519F9-FB34-4D13-B313-BF0C7230A339) for more details.

### CPQ Cloud

Undo the steps you performed above. That is, delete the 1 commerce action, 2 commerce libraries, 2 commerce attributes and the 3 utility libraries, in that order. Then, redeploy the commerce process.

##Documentation

To learn how to further extend this sample, explore other implementation options, or develop your own application following a similar implementation pattern, visit the [Excel to CSV Converter Solution page](https://cloud.oracle.com/developer/solutions?scenarioid=1385172988790) in the Oracle Cloud Developer Portal.

## Known Issues

None


Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved
