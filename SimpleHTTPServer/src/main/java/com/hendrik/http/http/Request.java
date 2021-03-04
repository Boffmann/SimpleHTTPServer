package com.hendrik.http.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

import com.hendrik.http.http.DB.DBHandler;

/**
 * Class representing a HTTP request
 * 
 * @author Hendrik Tjabben
 */
public class Request extends HTTPMessage {

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
     * Create a new Request by parsing the TCP input stream.
     * It assumes that when the input stream contains a request line, this request line is valid
     * 
     * @param inputStream The input stream to parse
     * @throws IOException An I/O error happened while parsing the stream
     */
    public Request(final InputStream inputStream) throws IOException {
        super(new Header());

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
        } else if (splittedStatusLine[0].toUpperCase().equals("POST")) {
            this.method = HeaderFields.RequestMethod.POST;
        }
        else {
            this.method = HeaderFields.RequestMethod.UNSUPPORTED;
        }

        this.uri = splittedStatusLine[1];
        this.httpVersion = splittedStatusLine[2];

        String line;
        while ( (line = reader.readLine()) != null) {
            if (line.equals("")) {
                break;
            }
            this.header.addEntryWhenSupported(line);
        }

        // Based on
        // https://stackoverflow.com/questions/3033755/reading-post-data-from-html-form-sent-to-serversocket
        if (getMethod() == HeaderFields.RequestMethod.POST) {

            StringBuilder payloadBuilder = new StringBuilder();
            while (reader.ready()) {
                payloadBuilder.append((char) reader.read());
            }
            DBHandler.addComment(payloadBuilder.toString());
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

}