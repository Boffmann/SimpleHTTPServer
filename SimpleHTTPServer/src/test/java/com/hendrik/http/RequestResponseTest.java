package com.hendrik.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.hendrik.http.http.HeaderFields;
import com.hendrik.http.http.Request;
import com.hendrik.http.http.Response;
import com.hendrik.http.http.ResponseBuilder;
import com.hendrik.http.http.resource.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RequestResponseTest {

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
        
        Response getResponse = new ResponseBuilder(getRootRequest).build();
        Assertions.assertTrue(getResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/html", getResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());

        try {
            Assertions.assertArrayEquals(rootHTML.getData(), getResponse.getData());
        } catch (IOException ex) {
            // Should no happen
            Assertions.assertTrue(false);
        }

        Response getFileResponse = new ResponseBuilder(getFileRequest).build();
        Assertions.assertTrue(getFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", getFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertTrue(getFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_LENGTH).isPresent());
        Assertions.assertEquals("Content-Length: 10", getFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_LENGTH).get());
        Assertions.assertTrue(getFileResponse.getHeaderLine(HeaderFields.Field.SERVER).isPresent());
        Assertions.assertEquals("Server: " + HTTPServer.getServerInfo(), getFileResponse.getHeaderLine(HeaderFields.Field.SERVER).get());
        Assertions.assertArrayEquals("Subfolder\n".getBytes(), getFileResponse.getData());

        Response headResponse = new ResponseBuilder(headRequest).build();
        Assertions.assertTrue(headResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/html", headResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("".getBytes(), headResponse.getData());

        Response headFileResponse = new ResponseBuilder(headRequestFile).build();
        Assertions.assertTrue(headFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", headFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("".getBytes(), headFileResponse.getData());

        Response getNotPresentResponse = new ResponseBuilder(getNotPresentRequest).build();
        Assertions.assertTrue(getNotPresentResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", getNotPresentResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("404 Not Found".getBytes(), getNotPresentResponse.getData());

        Response postResponse = new ResponseBuilder(postRequest).build();
        Assertions.assertTrue(postResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", postResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("501 Not Implemented".getBytes(), postResponse.getData());

        Response nullResponse = new ResponseBuilder(nullRequest).build();
        Assertions.assertTrue(nullResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain", nullResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("501 Not Implemented".getBytes(), nullResponse.getData());
    }

}
