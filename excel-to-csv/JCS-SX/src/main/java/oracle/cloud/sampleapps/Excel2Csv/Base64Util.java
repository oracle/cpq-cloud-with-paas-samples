/* Copyright © 2016, Oracle and/or its affiliates. All rights reserved */
package oracle.cloud.sampleapps.Excel2Csv;

import com.sun.jersey.core.util.Base64;

/**
 * Base64 encoder/decoder. Uses the Jersey core base64 implementation.
 * 
 */
public class Base64Util {

    /**
     * Base64-encodes a byte array and returns the resulting string.
     */
    public static String encode(byte[] data) {
        return new String(Base64.encode(data));
    }

    /**
     * Decodes a base64-encoded string and returns the byte array.
     * This method can decode regular or url-safe base64 encoding.
     */
    public static byte[] decode(String data) {
        return Base64.decode(data);
    }
 
}
