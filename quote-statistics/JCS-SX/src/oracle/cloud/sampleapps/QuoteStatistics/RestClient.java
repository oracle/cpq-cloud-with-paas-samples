/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */
package oracle.cloud.sampleapps.QuoteStatistics;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.StringKeyStringValueIgnoreCaseMultivaluedMap;

/**
 * 
 * Jersey based client for RESTful server-server calls 
 * 
 */
public class RestClient {
    public static final String ERROR = "{ \"o:error\" : \"internal server error\"}";
    public static final String ERROR_ARRAY = "[{ \"o:error\" : \"internal server error\"}]";
    public static final String ENCODING = "UTF-8";
    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String PARAM_ITEMS = "items";
    public static final String PARAM_NAME = "name";
    public static enum AuthMethod {
        NONE(""),
        BEARER_TOKEN ("Bearer ");
        
        private String prefix;
        private AuthMethod(String prefix) {
            this.prefix = prefix;
        }
        private String getPrefix() {
            return prefix;
        }
    }
    
    private static RestClient singleton;
    public static RestClient getSingleton() {
        if(singleton == null) {
            singleton = new RestClient();
        }
        return singleton;
    }
    
    public JSONObject invokeAsJSONObject(String url, String operation, String tokenOrPassword,MediaType contentType, Map<String, String> params, AuthMethod authType) throws Exception{
        String json = invokeAsString(url, operation, tokenOrPassword, contentType, params, authType);
        JSONObject jobj = null;
        jobj = new JSONObject(json);
        return jobj != null ? jobj : new JSONObject(ERROR);
    }

    public JSONArray invokeAsJSONArray(String url, String operation, String token,MediaType contentType, Map<String, String> params, AuthMethod authType ) throws Exception {
        JSONObject json = invokeAsJSONObject(url, operation, token, contentType, params, authType);
        JSONArray array = json.getJSONArray(PARAM_ITEMS);
        return array != null ? array : new JSONArray(ERROR_ARRAY);
    }

    public String invokeAsString(String url, String operation, String tokenOrPassword, MediaType contentType, Map<String, String> params, AuthMethod authType) throws Exception {
        return invokeImpl(url, operation, tokenOrPassword, contentType, params, authType);
    }
    
    /**
     * Core method making the invocation 
     * @param url  URL to invoke
     * @param operation  "GET" or "POST"
     * @param token Oauth 2.0 bearer token to authenticate to the remote server
     * @param params URL params
     * @return
     * @throws Exception
     */
    private String invokeImpl(String url, String operation, String tokenOrPassword, MediaType contentType, Map<String, String> params, AuthMethod authType) throws Exception {
      Client c = Client.create();
      WebResource resource = c.resource(url);
      MultivaluedMap<String, String> paramsMap = new StringKeyStringValueIgnoreCaseMultivaluedMap();
      if (params!= null && !params.isEmpty()) {
          for (Entry<String, String> entry : params.entrySet()) {
              paramsMap.add(entry.getKey(), entry.getValue());
          }
      }
      
      resource = resource.queryParams(paramsMap);
      Builder  invocationBuilder = resource.accept(MediaType.APPLICATION_JSON).type(contentType);
      if(authType != AuthMethod.NONE) {
          invocationBuilder.header(AUTHORIZATION_HEADER, authType.getPrefix() + tokenOrPassword);
      }
      
      if(operation == "GET") {
          return invocationBuilder.get(String.class);
      } else if (operation == "POST") {
          return invocationBuilder.post(String.class);
      } else {
          return "";
      }
      
    }
}

