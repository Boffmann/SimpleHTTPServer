package com.hendrik.http.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class representing a HTTP request
 * 
 * @author Hendrik Tjabben
 */
public class Request {

    /**
     * Represents to which method this header belongs
     */
    private RequestMethod method;

    /**
     * The request's URI
     */
    private String uri;

    /**
     * This request's HTTP Version
     */
    private String httpVersion;

    /**
     * The header of this request
     */
    private Header header;

    /**
     * Create a new Request by parsing the TCP input stream
     * 
     * @param inputStream The input stream to parse
     * @throws IOException An I/O error happened while parsing the stream
     */
    public Request(final InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String statusLine = reader.readLine();

        String[] splittedStatusLine = statusLine.split("\\s+");

        if (splittedStatusLine[0].toUpperCase().equals("GET")) {
            this.method = RequestMethod.GET;
        } else if (splittedStatusLine[0].toUpperCase().equals("HEAD")) {
            this.method = RequestMethod.HEAD;
        } else {
            this.method = RequestMethod.UNSUPPORTED;
        }

        this.uri = splittedStatusLine[1];
        this.httpVersion = splittedStatusLine[2];

        this.header = new Header();

        String line;
        while ( !((line = reader.readLine()).equals(""))) {
            this.header.addLine(line);
        }
    }

    /**
     * Returns the HTTP Version of this request
     * 
     * @return The HTTP version of this request
     */
    public String getVersion() {
        return this.httpVersion;
    }

    /**
     * Returns the HTTP Method for this request
     * 
     * @return The HTTP method for this request
     */
    public RequestMethod getMethod() {
        return this.method;
    }

    /**
     * Returns the requested URI
     * 
     * @return The requested URI
     */
    public String getURI() {
        return this.uri;
    }

}