/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */
package oracle.cloud.sampleapps.QuoteStatistics;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import oracle.cloud.sampleapps.QuoteStatistics.RestClient.AuthMethod;
import oracle.cloud.sampleapps.QuoteStatistics.authtoken.AuthCodeRedirectEndpoint;
import oracle.cloud.sampleapps.QuoteStatistics.authtoken.CPQTokenConstants;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

/**
 * Wrapper containing methods useful in collecting (REST api) CPQ data and calculating the statistics of interest 
 *
 */
public class CpqCaller { 
    
    //names of URL query parameters
    public static final String  PRM_CUSTOMER_ID = "custId";
    public static final String  PRM_CPQ_SERVER = "cpqServer";
    public static final String  PRM_RESOURCE_NAME = "resourceName";
    public static final String  PRM_CPQ_TOKEN = "cpqToken";
    public static final String  PRM_CURR_RES_ID = "currentResourceId";
    
    private static final String CHARSET = "UTF-8";

    public static RestClient client = RestClient.getSingleton();
    public String cpqServer;
    public String cpqResource;
    public String cpqToken;
    public String excludeResId;
    public AuthCodeRedirectEndpoint clientService = AuthCodeRedirectEndpoint.getInstance();;
    /**
     * Method called from the JSP
     * Figures out the request params and passes control to a another method for actual data accumulation and calculation
     * @return "--could not fetch data--" in case of errors
     */
    public AvgNetPriceStat compileStats(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String customerId = getParameter(request, PRM_CUSTOMER_ID, "");
        cpqServer =  urlDecode(getParameter(request, PRM_CPQ_SERVER, ""));
        cpqResource =  urlDecode(getParameter(request, PRM_RESOURCE_NAME, ""));
        cpqToken =  urlDecode(getParameter(request, PRM_CPQ_TOKEN, ""));
        excludeResId =  urlDecode(getParameter(request, PRM_CURR_RES_ID, ""));
        String state =  urlDecode(getParameter(request, "state", ""));
        
        if(cpqToken == "") { 
            if(state == "") { //i.e. a token negotiation has not been tried so far; initiate it
                clientService.redirectForNegotiatingToken(CPQTokenConstants.OAUTH_CLIENT_KEY, request, response);
                return new AvgNetPriceStat("--redirection failed--");
            } else { //a token negotiation was tried, but failed
                return new AvgNetPriceStat("--could not fetch data--");
            }
        }
        cpqServer = cpqServer == ""? CPQConstants.DEFAULT_SERVER: cpqServer;
        cpqResource = cpqResource == "" ? CPQConstants.DEFAULT_RESOURCE: cpqResource;
        return getNetPrice(customerId);
    }
    
    public AvgNetPriceStat getNetPrice(String customerId) throws Exception {
        String collectionUri = cpqServer  + "/" + cpqResource + "?";
        Map<String, String> params = new HashMap<String, String>();
        
        //add fields to be retrieved
        params.put("fields", CPQConstants.FLD_NET_PRICE + "," + CPQConstants.FLD_ID);
        
        //add filters
        params.put("q", "{" + CPQConstants.FLD_CUST + ":'"+ customerId+ "'}");
        
        JSONArray dataSet;
        try {
             dataSet = client.invokeAsJSONArray(collectionUri, "GET", cpqToken, MediaType.APPLICATION_JSON_TYPE, params, AuthMethod.BEARER_TOKEN);
        }catch(Exception e){
            //log exception here
            //logger.error("Error when invoking server", e);
            //return appropriate result for presentation
            return new AvgNetPriceStat("--could not fetch data--"); 
        }
        
        float total = 0.0f;
        int numDataPoints = 0;
        double localVal = 0.0;
        for(int i=0; i < dataSet.length(); i++) {
            JSONObject transaction = (JSONObject) dataSet.get(i); //assumption; no arrays returned within arrays
            if( !excludeResId.equals(transaction.getString(CPQConstants.FLD_ID))) {
                numDataPoints++;
                if((localVal = transaction.getDouble(CPQConstants.FLD_NET_PRICE))>0) {
                    total += localVal;
                }
            }
        }
        return new AvgNetPriceStat(total, numDataPoints);
    }
    
    /**
     * A class to simply hold results 
     */
    public class AvgNetPriceStat{
        private float avg;
        private int numDataPoints;
        private String msg;
        private boolean data;
        /**
         * Constructor for successful processing scenarios
         */
        AvgNetPriceStat(float total, int numItems) {
             this.numDataPoints = numItems;
             if(numDataPoints > 0 ) {
                 avg= total/numDataPoints;
             }
             this.data = true;
        }
        
        /**
         * Constructor for error scenarios
         */
        AvgNetPriceStat(String msg) {
            this.data = false;
            this.msg=msg;
            this.avg = 0.0f;
            this.numDataPoints = 0;
       }
        
       private void generateMessage() {
            if(numDataPoints == 0 ) {
                this.data = false;
                this.msg= "No similar quotes found for this customer";
            }else {
                this.msg= "This customer has been quoted " + numDataPoints + " other similar " +
                        (numDataPoints>1? "quotes ": "quote ") +
                        " with an average net price of ";
            }
        }
        
        public String getMsg() {
            if(this.msg==null) {
                generateMessage();
            }
            return this.msg;
        }
        
        
        public String getAvg() {
             return "$" + avg;
        }

        public boolean hasData() {
            return data;
        }
    }

    /**
     * Helper method
     */
    private  String getParameter(HttpServletRequest request, String name, String defVal) {
        String value = request.getParameter(name);
        return (value != null ? value.trim() : defVal);
    }
    
    /**
     * Helper method
     */
    public static String urlDecode(String url) throws Exception {
        if (url == null) {
            return "";
        }
        try {
            return URLDecoder.decode(url, CHARSET);
        } catch(UnsupportedEncodingException e) {
            throw new Exception("Error in url decode", e);
        }
    }
}

