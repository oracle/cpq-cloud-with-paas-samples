/* ====================================================================
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==================================================================== */
/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */
package oracle.cloud.sampleapps.Excel2Csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import au.com.bytecode.opencsv.CSVWriter;

/**
* A rudimentary XLSX -> CSV processor modeled on the
* POI sample program XLS2CSVmra from the package
* org.apache.poi.hssf.eventusermodel.examples.
* As with the HSSF version, this tries to spot missing
*  rows and cells, and output empty entries for them.
* <p/>
* This sample has been modified to meet the needs of accumulating the data and writing to a sreatm
*/
public class XLSX2CSV {
    
    /**
      * Uses the XSSF Event SAX helpers to do most of the work
      *  of parsing the Sheet XML, and outputs the contents
      *  as a (basic) CSV.
      */
     private class SheetToCSV implements SheetContentsHandler {
         private int currentRow = -1;
         private int currentCol = -1;
         private List<String> currentRowData;
         private void outputMissingRows(int number) {
             for (int i=0; i<number; i++) {
                 currentRowData = new ArrayList<String>();
                 for (int j=0; j<minColumns; j++) {
                     currentRowData.add("");
                 }
                 csvWriter.writeNext(currentRowData.toArray( new String[0]));
                 currentRowData=null;
             }
         }
    
         public void startRow(int rowNum) {
             // If there were gaps, output the missing rows
             outputMissingRows(rowNum-currentRow-1);
             // Prepare for this row
             currentRowData = new ArrayList<String>();
             currentRow = rowNum;
             currentCol = -1;
         }
    
         public void endRow(int rowNum) {
             // Ensure the minimum number of columns
             for (int i=currentCol; i<minColumns; i++) {
                 currentRowData.add("");
             }
             csvWriter.writeNext(currentRowData.toArray( new String[0]));
             currentRowData=null;
         }
    
         public void cell(String cellReference, String formattedValue,
                 XSSFComment comment) {
             
             // Did we miss any cells?
             int thisCol = (new CellReference(cellReference)).getCol();
             int missedCols = thisCol - currentCol - 1;
             for (int i=0; i<missedCols; i++) {
                 currentRowData.add("");
             }
             currentCol = thisCol;
             
             // Number or string?
             try {
                 Double.parseDouble(formattedValue);
                 currentRowData.add(formattedValue);
             } catch (NumberFormatException e) {
                 currentRowData.add(formattedValue);
             }
         }
    
         public void headerFooter(String text, boolean isHeader, String tagName) {
             // Skip, no headers or footers in CSV
         }
 }


 ///////////////////////////////////////

 private final OPCPackage xlsxPackage;

 /**
  * Number of columns to read starting with leftmost
  */
 private final int minColumns;

 /**
  * Destination for data
  */
 private final PrintStream output;
 
 private final CSVWriter csvWriter;
 private StringWriter csvOutput;
 /**
  * Creates a new XLSX -> CSV converter
  *
  * @param pkg        The XLSX package to process
  * @param output     The PrintStream to output the CSV to
  * @param minColumns The minimum number of columns to output, or -1 for no minimum
  */
 public XLSX2CSV(OPCPackage pkg, PrintStream output, int minColumns) {
     this.xlsxPackage = pkg;
     this.output = output;
     this.minColumns = minColumns;
     this.csvOutput =  new StringWriter();
     this.csvWriter = new CSVWriter(csvOutput);
 }

 /**
  * Parses and shows the content of one sheet
  * using the specified styles and shared-strings tables.
  *
  * @param styles
  * @param strings
  * @param sheetInputStream
  */
 public void processSheet(
         StylesTable styles,
         ReadOnlySharedStringsTable strings,
         SheetContentsHandler sheetHandler, 
         InputStream sheetInputStream)
         throws IOException, ParserConfigurationException, SAXException {
     DataFormatter formatter = new DataFormatter();
     InputSource sheetSource = new InputSource(sheetInputStream);
     try {
         XMLReader sheetParser = SAXHelper.newXMLReader();
         ContentHandler handler = new XSSFSheetXMLHandler(
               styles, null, strings, sheetHandler, formatter, false);
         sheetParser.setContentHandler(handler);
         sheetParser.parse(sheetSource);
      } catch(ParserConfigurationException e) {
         throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
      }
 }

 /**
  * Initiates the processing of the XLS workbook file to CSV.
  *
  * @throws IOException
  * @throws OpenXML4JException
  * @throws ParserConfigurationException
  * @throws SAXException
  */
 public String process()
         throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
     ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
     XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
     StylesTable styles = xssfReader.getStylesTable();
     XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
     int index = 0;
     while (iter.hasNext()) {
         InputStream stream = iter.next();
         String sheetName = iter.getSheetName();
         processSheet(styles, strings, new SheetToCSV(), stream);
         stream.close();
         ++index;
     }
     
     
     return csvOutput.toString();
 }
}