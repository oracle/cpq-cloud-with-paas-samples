/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */
package oracle.cloud.sampleapps.Excel2Csv;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CsvData {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
