package com.hendrik.http.http;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a HTTP Response message
 * 
 * @author Hendrik Tjabben
 */
public class Response extends HTTPMessage {

    private final static String VERSION = "HTTP/1.1";

    /**
     * this response's status code
     */
    private HeaderFields.StatusCode statusCode;

    public Response(final HeaderFields.StatusCode statusCode, final Header header, final byte[] body) {
        super(header);

        this.statusCode = statusCode;
        this.header = header;
        this.body = body;
    }

    /**
     * Convenience method to create the status line for this response
     * 
     * @return The final status line for this HTTP response
     */
    private String createStatusLine() {
        
        StringBuilder statusLineBuilder = new StringBuilder()
            .append(VERSION)
            .append(" ")
            .append(HeaderFields.toString(this.statusCode));

        return statusLineBuilder.toString();
    }

    /**
     * Getter for the data that are trasmitted as payload for this respond
     * 
     * @return The response's payload bytes
     */
    public byte[] getData() {
        return this.body;
    }

    public List<String> getHeaderLines() {
        List<String> result = new ArrayList<String>();

        result.add(createStatusLine());
        result.addAll(header.getLines());

        return result;

    }


}