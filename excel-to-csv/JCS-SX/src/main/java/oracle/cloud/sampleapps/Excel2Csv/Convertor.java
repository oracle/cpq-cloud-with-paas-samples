/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */
package oracle.cloud.sampleapps.Excel2Csv;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.xml.sax.SAXException;

import weblogic.wsee.jws.jaxws.owsm.SecurityPolicy;

@SecurityPolicy(uri="oracle/multi_token_over_ssl_rest_service_policy")
@Path("/Xlsx2Csv")
public class Convertor {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CsvData convert(FileData excelData) throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
        //validation here
        String output = "";
        CsvData csvData = new CsvData();
        csvData.setData("");
        
        //short circuit if nothing to process
        if(excelData == null || excelData.getData()  == null) {
            return csvData;
        }
        
        try {
            output = convertToCSV(excelData);
            csvData.setData(output);
        } catch(Exception e) {
            throw new RuntimeException("There was a problem processing the request", e);
        }
        
        return csvData;
    }
    
    private String convertToCSV(FileData excelData) throws IOException, OpenXML4JException, ParserConfigurationException, SAXException{
        OPCPackage p = OPCPackage.open(new ByteArrayInputStream( Base64Util.decode(excelData.getData())));
        XLSX2CSV xlsx2csv = new XLSX2CSV(p, System.out, excelData.getCols());
        p.close();
        return xlsx2csv.process();
    }
}
