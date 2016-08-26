/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */

package oracle.cloud.sampleapps.QuoteStatistics.authtoken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import oracle.cloud.sampleapps.QuoteStatistics.RestClient;
import oracle.cloud.sampleapps.QuoteStatistics.RestClient.AuthMethod;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * To access CPQ REST API, the application uses OAuth 2.0 authorization code flow for negotiating an access token <br/>
 * This class contains the methods necessary to perform this flow. There are 2 entry points into this class:-
 * a) redirectForNegotiatingToken - this is called from the PaaS application directly when there is a need to negotiate a token
 * b) getTokenWithAuthCode - this is mapped (via Jersey) to respond to CPQ OAuth provider redirect call when it provides the authorization code.
 */
@Path("/clientRedirectEndpoint")
public class AuthCodeRedirectEndpoint {
    
    public static RestClient rc = RestClient.getSingleton();
    public static AuthCodeRedirectEndpoint service;
    
    public synchronized static AuthCodeRedirectEndpoint getInstance() {
        if(service==null) {
            service = new AuthCodeRedirectEndpoint();
        }
        return service;
    }
    
    /**
     * This method is called from the application when it is noticed that no access token was bound to the 
     * application url. The method kicks off a "authorization code" flow with the OAuth provider to negotiate
     * an access token. <br/><br/>
     * A browser cookie set to remember the original application resource url, 
     * as well as for csrf protection. In the second part of the token
     * negotiation {@link #getTokenWithAuthCode(String, String, String, String)}, 
     * the cookie values enable the flow to get routed back to the application resource initially requested. <br/>
     * <b>Note: </b> If you use the client endpoint for more than 1 instance of an application on the embedding CPQ instance,
     * you will be careful since overwriting the cookie. (Perhaps, use a different strategy for cookie name?).
     * @param clientKey
     * @param request
     * @param response
     */
    public void redirectForNegotiatingToken(String clientKey, HttpServletRequest request, HttpServletResponse response) {
        //Fetch info about the particular client
        Client client = getClientInfo(clientKey);
        if (client == null) {
            return;
        }

        //initiate the authorization_code OAuth flow
        //use the "state" parameter for csrf protection, and store a cookie with the user-agent with the csrf details, 
        //as well as the resource url to redirect to once the token negotiation has been successful
        try {
            
            String reqCsrf= formulateCsrfToken();
            String queryParams = getRequestParameters(request);
            Cookie cookie=new Cookie("YourUniqueCookieName", URLEncoder.encode( reqCsrf, "UTF-8") + "~" + 
                    request.getRequestURL().
                    append("?").
                    append(URLEncoder.encode(queryParams, "UTF-8")));
            cookie.setMaxAge(90);
            response.addCookie(cookie);
            
            //redirect to known oauth endpoint, to start the authorization code flow
            StringBuilder redirectUrl= new StringBuilder(CPQTokenConstants.CPQ_AUTHZ_ENDPOINT);
            redirectUrl.append("?response_type=code&client_id=" ).append(client.getClientId()) 
                        .append( "&redirect_uri=").append( URLEncoder.encode(client.getRedirectUri(), "UTF-8"))
                        .append( "&time=" ).append( System.currentTimeMillis()) //avoid caches
                        .append( "&state=").append(  URLEncoder.encode(reqCsrf, "UTF-8"));
            response.sendRedirect(redirectUrl.toString()); 
        } catch (UnsupportedEncodingException e) {
            //log this 
            e.printStackTrace();
            return;
        } catch (IOException e) {
            //log this; if this failed, then token cannot be negotiated
            e.printStackTrace();
            return;
        }
        
    }
    
    @GET
    @Path("{client_key}")
    public Response getTokenWithAuthCode(@PathParam("client_key") String clientKey,
            @QueryParam("code") String authCode, @QueryParam("state") String state,
            @CookieParam("YourUniqueCookieName") String cookieInfo) throws UnsupportedEncodingException  {
        
        int indexOfSeparator = cookieInfo.indexOf("~");
        String originalAppRequestUrl = "";
        String csrf = "";
        if(indexOfSeparator!=-1) {
            csrf = URLDecoder.decode(cookieInfo.substring(0, indexOfSeparator), "UTF-8");
            originalAppRequestUrl = URLDecoder.decode(cookieInfo.substring(indexOfSeparator+1), "UTF-8");
        }
        
        if (clientKey == null || authCode == null || state == null || !csrf.equals(state)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }

        //Fetch info about the particular client
        //The client Key should map to the same clientId used by CPQ header javascript to kick off the flow from the CPQ site
        Client client = getClientInfo(clientKey);
        if (client == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Client not found")
                    .build();
        }

        //PREPARE AND MAKE a request to the token endpoint - this is the second part of the OAuth 2.0 authorization code flow, to get the access token for CPQ API
        JSONObject tokenResponse = new JSONObject();
        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("code", authCode);
        params.put("client_id", client.getClientId());
        params.put("client_secret", client.getSecret());
        params.put("redirect_uri", client.getRedirectUri() );
        try {
            tokenResponse = rc.invokeAsJSONObject(CPQTokenConstants.CPQ_TOKEN_ENDPOINT, "POST", "", MediaType.APPLICATION_FORM_URLENCODED_TYPE, params, AuthMethod.NONE);
        } catch (Exception e) {
            e.printStackTrace(); //preferably, log this instead
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
                    .build();
        }
        
        
        //parse token & pass token to the requesting app (as given in the state).
        String accessToken = "";
        if(tokenResponse!= null) {
            try {
                accessToken = (String)tokenResponse.get("access_token");
            } catch (JSONException e) {
                //no access token
                e.printStackTrace();
            }
        }
        
        try {
            return Response.status(Response.Status.MOVED_PERMANENTLY)
                    .location(new URI(originalAppRequestUrl+accessToken+"&state=negotiationComplete"))
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            //there was a problem with the format of the URI passed in the "state". Fix in earlier flow, if required.
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
    }
    
    /**
     * 
     * Client data
     * In production code, employ appropriate security measures to protect credentials
     *
     */
    class Client {
        private String clientId;
        private String secret;
        private String redirectUri;
        
        public String getClientId() {
            return clientId;
        }
        public String getSecret() {
            return secret;
        }
        public String getRedirectUri() {
            return redirectUri;
        }
        public Client(String id, String secret, String redirectUri) {
            this.clientId = id;
            this.secret = secret;
            this.redirectUri = redirectUri;
        }
    }
    
    private Client getClientInfo(String clientKey) {
        if(CPQTokenConstants.OAUTH_CLIENT_KEY.equalsIgnoreCase(clientKey)) {
            return new Client(
                    CPQTokenConstants.OAUTH_CLIENT_ID,
                    CPQTokenConstants.OAUTH_CLIENT_SECRET,
                    CPQTokenConstants.OAUTH_CLIENT_REDIRECT_URI);
        }
        return null;
    }
    
    
    private String formulateCsrfToken() {
        String reqCsrfString= UUID.randomUUID().toString();
        try {
            return makeCsrfToken(reqCsrfString);
        } catch (Exception e) {
            e.printStackTrace(); //in your implementation, log and fix before production usage
            return reqCsrfString; //fallback to unhashed string
        }
    }
    
    /**
     * This method shows one way of creating the csrf token necessary in the strategy to protect this
     * client from csrf attacks
     * See rfc 6749 - section 10.12 for more details on this attack w.r.t. OAuth 2.0 flow
     */
    private String makeCsrfToken(String input) throws IllegalArgumentException, NoSuchAlgorithmException, UnsupportedEncodingException{
        
        if (input == null) {
            throw new IllegalArgumentException("null string is not a valid input for csrf token generation");
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw e;
        }
        try {
            md.update(input.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
        return new String(Base64.encodeBase64URLSafeString(md.digest()));
    }
    
    
    private String getRequestParameters(HttpServletRequest request) throws UnsupportedEncodingException{
        StringBuilder queryParams = new StringBuilder();
        //the html unescape gets around the issue of converting &amp; to &, in certain situations where
        //the application container is seeing &amp; instead of & incoming in query parameters
        queryParams
        .append(StringEscapeUtils.unescapeHtml( URLDecoder.decode( request.getQueryString(), "UTF-8"))) 
        .append(request.getQueryString()=="" ? "":"&")
        .append("cpqToken=");
        return queryParams.toString();
    }
    
}
