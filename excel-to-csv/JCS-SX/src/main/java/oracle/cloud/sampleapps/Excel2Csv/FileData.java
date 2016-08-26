/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */
package oracle.cloud.sampleapps.Excel2Csv;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FileData {
    private String data;
    private int cols = 1; //default to atleast 1 column

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }
}
