package com.hendrik.http.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

/**
 * Class representing a HTTP request
 * 
 * @author Hendrik Tjabben
 */
public class Request {

    /**
     * Represents to which method this header belongs
     */
    private HeaderFields.RequestMethod method;

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
     * Create a new Request by parsing the TCP input stream.
     * It assumes that when the input stream contains a request line, this request line is valid
     * 
     * @param inputStream The input stream to parse
     * @throws IOException An I/O error happened while parsing the stream
     */
    public Request(final InputStream inputStream) throws IOException {

        if (inputStream == null) {
            System.out.println("Request created from empty input Stream");
            this.method = HeaderFields.RequestMethod.UNSUPPORTED;
            this.uri = "";
            this.httpVersion = "";
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String statusLine = reader.readLine();

        if (statusLine == null || statusLine == "") {
            System.out.println("Request created from empty input Stream");
            this.method = HeaderFields.RequestMethod.UNSUPPORTED;
            this.uri = "";
            this.httpVersion = "";
            return;
        }

        String[] splittedStatusLine = statusLine.split("\\s+");

        if (splittedStatusLine[0].toUpperCase().equals("GET")) {
            this.method = HeaderFields.RequestMethod.GET;
        } else if (splittedStatusLine[0].toUpperCase().equals("HEAD")) {
            this.method = HeaderFields.RequestMethod.HEAD;
        } else {
            this.method = HeaderFields.RequestMethod.UNSUPPORTED;
        }

        this.uri = splittedStatusLine[1];
        this.httpVersion = splittedStatusLine[2];

        this.header = new Header();

        String line;
        while ( (line = reader.readLine()) != null) {
            if (line.equals("")) {
                break;
            }
            this.header.addEntryWhenSupported(line);
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
    public HeaderFields.RequestMethod getMethod() {
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

    /**
     * Getter to aquire the header line for a specific header field
     * 
     * @param headerField The header field of which the line should be taken
     * @return An optional containing the header line for the inquired field. Empty if the header contains no entry for the requested field
     */
    public Optional<String> getHeaderLine(final HeaderFields.Field headerField) {
        return this.header.getLine(headerField);
    }

    /**
     * Returns a list of all values present for a specific header field
     * 
     * @param headerField The header field of which the entries should be taken
     * @return An optional containing all entries for the specified header field
     */
    public Optional<List<String>> getHeaderValues(final HeaderFields.Field headerField) {
        return this.header.getValues(headerField);
    }

}