/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */
package oracle.cloud.sampleapps.Excel2Csv;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * A couple of test methods to help you run the convertor without using jersey setup
 *
 */
public class XLSX2CSVTest {


    @Test
    public void testProcess() throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
        OPCPackage p = OPCPackage.open(getClass().getResource("test.xlsx").getPath(), PackageAccess.READ);
        XLSX2CSV xlsx2csv = new XLSX2CSV(p, System.out, 7);
        String output = xlsx2csv.process();
        p.close();
        //System.out.println(a);
        Assert.assertEquals("CSV result does not match expected result (keep in mind, line endings matter)", 
                readFile(getClass().getResource("result.csv").getPath()),
                output);
    }
    
    @Test
    public void testProcessWithEncodedFile() throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
        OPCPackage p = OPCPackage.open(new ByteArrayInputStream( 
                Base64Util.decode(
                        Base64Util.encode( readFile(getClass().getResource("test.xlsx").getPath(), false)))));
        
        XLSX2CSV xlsx2csv = new XLSX2CSV(p, System.out, 7);
        String output = xlsx2csv.process();
        p.close();
        
        Assert.assertEquals("CSV result does not match expected result (keep in mind, line endings matter)", 
                readFile(getClass().getResource("result.csv").getPath()),
                output);
    }
    
    /** Default number of bytes to read from a file at a time. */
    private static final int BUFFER_SIZE = 32768;

    /** Total number of bytes to read from a file when the truncate flag is true. */
    public static final int TRUNCATE_SIZE = 5 << 20;

    // --------------------- Helper methods -------------------- //
    
    public static byte[] readFile(String fileLocation, boolean truncate) throws IOException {
        return readFile(new RandomAccessFile(fileLocation, "r"), truncate);
    }
    private static byte[] readFile(RandomAccessFile rFile, boolean truncate) throws IOException {
            return readBytesFromFile(rFile, truncate).toByteArray();
    }
    
    private String readFile(String fileLocation) throws IOException {
        return readBytesFromFile(new RandomAccessFile(fileLocation, "r"), false).toString("UTF-8");
    }
    
    private static ByteArrayOutputStream readBytesFromFile(RandomAccessFile rFile, boolean truncate)
        throws IOException {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                if (truncate) {
                    long pos = rFile.length() - TRUNCATE_SIZE; // Where to start reading from
                    if (pos > 0) { // Can't skip if file size is less than the threshold
                        rFile.seek(pos); // Skip everything up to the last # bytes as determined by pos
                    }
                }
                for (int length; (length = rFile.read(buffer)) != -1; ) {
                    stream.write(buffer, 0, length);
                }
                return stream;
            } finally {
                close(rFile);
            }
        }
    
    public static void close(Closeable closeable) throws IOException {
        if (closeable != null) {
                closeable.close();
        }
    }

}
