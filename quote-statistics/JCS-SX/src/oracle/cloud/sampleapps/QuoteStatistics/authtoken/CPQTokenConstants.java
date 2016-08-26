/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */

package oracle.cloud.sampleapps.QuoteStatistics.authtoken;

/**
 * This construct holds values that you will need to alter 
 *   for your sample to work with CPQ OAuth 2.0 access token negotiation, based on your environment and setup.
 *
 * Note that in your implementation of a real product, some of these values will probably need to be dynamic and more secure.
 */
public class CPQTokenConstants {
    //the particular CPQ host base URI that serves the content that this web application will be embedded within
    public static final String CPQ_HOST = "" ;   //e.g. https://<companyname>.us.oracle.com
    
    //useful endpoints for the CPQ OAuth flow
    public static final String CPQ_TOKEN_ENDPOINT = CPQ_HOST + "/oauth/token";
    public static final String CPQ_AUTHZ_ENDPOINT = CPQ_HOST + "/oauth/authorize";
	
    //Client credentials for the client registered with CPQ.
    //For production implementations, use a keystore or other means to securely store the credentials;
    //String literals are simply used for demonstrating the sample.
	public static final String OAUTH_CLIENT_KEY="<the client key as received in the redirection endpoint; for avoiding confusion, keep same as CPQ client key>";
    public static final String OAUTH_CLIENT_ID= "<register client with CPQ host above to get clientId>";
    public static final String OAUTH_CLIENT_SECRET="<register client with CPQ host above, to get clientSecret>";
    
    //Redirect url for the client, exactly as registered with CPQ
    //This needs to be an absolute url of the redirect endpoint, and hence you'll need to know your JCS-SX base URI and your web-app context root.
    //Alternately, you can obtain this dynamically from the servlet request api
    public static final String OAUTH_CLIENT_REDIRECT_URI="<the absolute uri of the endpoint in AuthCodeRedirectEndpoint.java>";
	//i.e. <https://{JCS-base-URI}/{web-app-context-root}/jersey/clientRedirectEndpoint/{oauth_client_key} where you should replace the {} placeholders with appropriate values
 
}
