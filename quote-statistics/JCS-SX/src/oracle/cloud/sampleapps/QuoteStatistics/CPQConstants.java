package oracle.cloud.sampleapps.QuoteStatistics;

import oracle.cloud.sampleapps.QuoteStatistics.authtoken.CPQTokenConstants;
/**
 * This class holds the meta-data used to execute the functionality of this web app
 * Some constants below will need their values to be altered based on the CPQ commerce process setup you have
 */
public class CPQConstants {
    
    //endpoints
    public static final String DEFAULT_SERVER = CPQTokenConstants.CPQ_HOST + "/rest/v1"; //Version endpoint for resource of interest
    
    public static final String DEFAULT_RESOURCE = "commerceDocumentsOraclecpqoTransaction"; //name of resource of interest
    
    //names of resource fields of interest during calculations; ENSURE that these attributes are available as mapped data columns in the CPQ commerce process of interest
    public static final String FLD_CUST = "_customer_t_company_name";
    public static final String FLD_NET_PRICE = "totalOneTimeNetAmount_t";
    public static final String FLD_ID = "_id"; //will appear as bs_id in data columns

}
