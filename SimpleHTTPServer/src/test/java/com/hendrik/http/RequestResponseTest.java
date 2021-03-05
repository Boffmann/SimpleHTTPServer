package com.hendrik.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.hendrik.http.resource.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RequestResponseTest {

    private static Request getRootRequest;
    private static Request getFileRequest;
    private static Request getFileRequestWithSpaces;
    private static Request getFileRequestUmlaute;
    private static Request getNotPresentRequest;
    private static Request headRequest;
    private static Request headRequestFile;
    private static Request postRequest;
    private static Request nullRequest;

    private static Resource rootHTML;

    /**
     * Sets up a new HTTP server with root directory in test folder. This should not
     * start the server, though
     */
    @BeforeAll
    public static void setUp() {
        try {
            getRootRequest = new Request(new ByteArrayInputStream("GET / HTTP/1.1".getBytes()));
            getFileRequest = new Request(
                    new ByteArrayInputStream("GET /Test2/Test21/subfolder.txt HTTP/1.1".getBytes()));
            getFileRequestWithSpaces = new Request(
                    new ByteArrayInputStream("GET /Test2/File%20With%20Spaces.txt HTTP/1.1".getBytes()));
            getFileRequestUmlaute = new Request(new ByteArrayInputStream("GET /Test1/umläute.txt HTTP/1.1".getBytes()));
            getNotPresentRequest = new Request(new ByteArrayInputStream("GET /fileNotThere HTTP/1.1".getBytes()));
            headRequest = new Request(new ByteArrayInputStream("HEAD / HTTP/1.1".getBytes()));
            headRequestFile = new Request(
                    new ByteArrayInputStream("HEAD /Test2/Test21/subfolder.txt HTTP/1.1".getBytes()));
            postRequest = new Request(new ByteArrayInputStream("PUT /TestPost HTTP/1.1".getBytes()));
            nullRequest = new Request(null);

            rootHTML = Resource.createFromURI("/Test1/root.html");
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

        Assertions.assertEquals(HeaderFields.RequestMethod.GET, getFileRequestWithSpaces.getMethod());
        Assertions.assertEquals("/Test2/File%20With%20Spaces.txt", getFileRequestWithSpaces.getURI());
        Assertions.assertEquals("HTTP/1.1", getFileRequestWithSpaces.getVersion());

        Assertions.assertEquals(HeaderFields.RequestMethod.GET, getFileRequestUmlaute.getMethod());
        Assertions.assertEquals("/Test1/umläute.txt", getFileRequestUmlaute.getURI());
        Assertions.assertEquals("HTTP/1.1", getFileRequestUmlaute.getVersion());

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

        
        Response getResponse = new ResponseBuilder(getRootRequest).build();
        Assertions.assertTrue(getResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/html; charset=utf-8", getResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());

        try {
            Assertions.assertArrayEquals(rootHTML.getData(), getResponse.getData());
        } catch (IOException ex) {
            // Should no happen
            Assertions.assertTrue(false);
        }

        Response getFileResponse = new ResponseBuilder(getFileRequest).build();
        Assertions.assertTrue(getFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain; charset=utf-8", getFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertTrue(getFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_LENGTH).isPresent());
        Assertions.assertEquals("Content-Length: 10", getFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_LENGTH).get());
        Assertions.assertTrue(getFileResponse.getHeaderLine(HeaderFields.Field.SERVER).isPresent());
        Assertions.assertEquals("Server: " + HTTPServer.getServerInfo(), getFileResponse.getHeaderLine(HeaderFields.Field.SERVER).get());
        Assertions.assertArrayEquals("Subfolder\n".getBytes(), getFileResponse.getData());

        Response getFileResponseWithSpaces = new ResponseBuilder(getFileRequestWithSpaces).build();
        Assertions.assertTrue(getFileResponseWithSpaces.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain; charset=utf-8", getFileResponseWithSpaces.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertTrue(getFileResponseWithSpaces.getHeaderLine(HeaderFields.Field.CONTENT_LENGTH).isPresent());
        Assertions.assertEquals("Content-Length: 12", getFileResponseWithSpaces.getHeaderLine(HeaderFields.Field.CONTENT_LENGTH).get());
        Assertions.assertTrue(getFileResponseWithSpaces.getHeaderLine(HeaderFields.Field.SERVER).isPresent());
        Assertions.assertEquals("Server: " + HTTPServer.getServerInfo(), getFileResponseWithSpaces.getHeaderLine(HeaderFields.Field.SERVER).get());
        Assertions.assertArrayEquals("With Spaces\n".getBytes(), getFileResponseWithSpaces.getData());

        Response getFileResponseUmlaute = new ResponseBuilder(getFileRequestUmlaute).build();
        Assertions.assertTrue(getFileResponseUmlaute.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain; charset=utf-8", getFileResponseUmlaute.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertTrue(getFileResponseUmlaute.getHeaderLine(HeaderFields.Field.CONTENT_LENGTH).isPresent());
        // Assertions.assertEquals("Content-Length: 4", getFileResponseUmlaute.getHeaderLine(HeaderFields.Field.CONTENT_LENGTH).get());
        Assertions.assertTrue(getFileResponseUmlaute.getHeaderLine(HeaderFields.Field.SERVER).isPresent());
        Assertions.assertEquals("Server: " + HTTPServer.getServerInfo(), getFileResponseUmlaute.getHeaderLine(HeaderFields.Field.SERVER).get());
        Assertions.assertArrayEquals("äöü\n".getBytes(), getFileResponseUmlaute.getData());

        Response headResponse = new ResponseBuilder(headRequest).build();
        Assertions.assertTrue(headResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/html; charset=utf-8", headResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("".getBytes(), headResponse.getData());

        Response headFileResponse = new ResponseBuilder(headRequestFile).build();
        Assertions.assertTrue(headFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain; charset=utf-8", headFileResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("".getBytes(), headFileResponse.getData());

        Response getNotPresentResponse = new ResponseBuilder(getNotPresentRequest).build();
        Assertions.assertTrue(getNotPresentResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain; charset=utf-8", getNotPresentResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("404 Not Found".getBytes(), getNotPresentResponse.getData());

        Response postResponse = new ResponseBuilder(postRequest).build();
        Assertions.assertTrue(postResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain; charset=utf-8", postResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("501 Not Implemented".getBytes(), postResponse.getData());

        Response nullResponse = new ResponseBuilder(nullRequest).build();
        Assertions.assertTrue(nullResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).isPresent());
        Assertions.assertEquals("Content-Type: text/plain; charset=utf-8", nullResponse.getHeaderLine(HeaderFields.Field.CONTENT_TYPE).get());
        Assertions.assertArrayEquals("501 Not Implemented".getBytes(), nullResponse.getData());
    }

}
