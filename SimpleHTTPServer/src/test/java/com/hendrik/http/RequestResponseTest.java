package com.hendrik.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.management.InvalidAttributeValueException;

import com.hendrik.http.HTTPServer;
import com.hendrik.http.http.HeaderFields;
import com.hendrik.http.http.Request;
import com.hendrik.http.http.Response;
import com.hendrik.http.http.resource.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RequestResponseTest {

    /**
     * This HTTP Server is only used to set the root directory
     */
    private static HTTPServer server;

    /**
     * Path into the test directory
     */
    private static String testDirPath;


    private static Request getRootRequest;
    private static Request getFileRequest;
    private static Request getNotPresentRequest;
    private static Request headRequest;
    private static Request headRequestFile;
    private static Request postRequest;
    private static Request nullRequest;

    /**
     * Sets up a new HTTP server with root directory in test folder. This should not
     * start the server, though
     */
    @BeforeAll
    public static void setUp() {
        try {
            getRootRequest = new Request(new ByteArrayInputStream("GET / HTTP/1.1".getBytes()));
            getFileRequest = new Request(new ByteArrayInputStream("GET /Test2/Test21/subfolder.txt HTTP/1.1".getBytes()));
            getNotPresentRequest = new Request(new ByteArrayInputStream("GET /fileNotThere HTTP/1.1".getBytes()));
            headRequest = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1".getBytes()));
            headRequestFile = new Request(new ByteArrayInputStream("HEAD /Test2/Test21/subfolder.txt HTTP/1.1".getBytes()));
            postRequest = new Request(new ByteArrayInputStream("POST /TestPost HTTP/1.1".getBytes()));
            nullRequest = new Request(null);
        } catch (IOException ex) {
            // Should not happen
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void createRequestTest() {
        Assertions.assertEquals(HeaderFields.RequestMethod.GET, getRootRequest.getMethod());
        Assertions.assertEquals("/", getRootRequest.getURI());
        Assertions.assertEquals("HTTP/1.1", getRootRequest.getVersion());

        Assertions.assertEquals(HeaderFields.RequestMethod.GET, getFileRequest.getMethod());
        Assertions.assertEquals("/Test2/Test21/subfolder.txt", getFileRequest.getURI());
        Assertions.assertEquals("HTTP/1.1", getFileRequest.getVersion());

        Assertions.assertEquals(HeaderFields.RequestMethod.GET, getNotPresentRequest.getMethod());
        Assertions.assertEquals("/fileNotThere", getNotPresentRequest.getURI());
        Assertions.assertEquals("HTTP/1.1", getNotPresentRequest.getVersion());

        Assertions.assertEquals(HeaderFields.RequestMethod.HEAD, headRequest.getMethod());
        Assertions.assertEquals("/", headRequest.getURI());
        Assertions.assertEquals("HTTP/1.1", headRequest.getVersion());

        Assertions.assertEquals(HeaderFields.RequestMethod.HEAD, headRequestFile.getMethod());
        Assertions.assertEquals("/Test2/Test21/subfolder.txt", headRequestFile.getURI());
        Assertions.assertEquals("HTTP/1.1", headRequestFile.getVersion());

        Assertions.assertEquals(HeaderFields.RequestMethod.UNSUPPORTED, postRequest.getMethod());
        Assertions.assertEquals("/TestPost", postRequest.getURI());
        Assertions.assertEquals("HTTP/1.1", postRequest.getVersion());

        Assertions.assertEquals(HeaderFields.RequestMethod.UNSUPPORTED, nullRequest.getMethod());
        Assertions.assertEquals("", nullRequest.getURI());
        Assertions.assertEquals("", nullRequest.getVersion());
    }

    @Test
    public void createResponseTest() {

        Resource rootHTML = Resource.createFromPath("/Test1/root.html");
        
        Response getResponse = Response.createForRequest(getRootRequest);
        Assertions.assertTrue(getResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/html", getResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).get());

        try {
            Assertions.assertArrayEquals(rootHTML.getData(), getResponse.getData());
        } catch (IOException ex) {
            // Should no happen
            Assertions.assertTrue(false);
        }

        Response getFileResponse = Response.createForRequest(getFileRequest);
        Assertions.assertTrue(getFileResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", getFileResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("Subfolder\n".getBytes(), getFileResponse.getData());

        Response headResponse = Response.createForRequest(headRequest);
        Assertions.assertTrue(headResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/html", headResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("".getBytes(), headResponse.getData());

        Response headFileResponse = Response.createForRequest(headRequestFile);
        Assertions.assertTrue(headFileResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", headFileResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("".getBytes(), headFileResponse.getData());

        Response getNotPresentResponse = Response.createForRequest(getNotPresentRequest);
        Assertions.assertTrue(getNotPresentResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", getNotPresentResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("404 - File Not Found".getBytes(), getNotPresentResponse.getData());

        Response postResponse = Response.createForRequest(postRequest);
        Assertions.assertTrue(postResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", postResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("The requested behaviour is not implemented".getBytes(), postResponse.getData());

        Response nullResponse = Response.createForRequest(nullRequest);
        Assertions.assertTrue(nullResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", nullResponse.getHeaderValue(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("The requested behaviour is not implemented".getBytes(), nullResponse.getData());
    }
}
