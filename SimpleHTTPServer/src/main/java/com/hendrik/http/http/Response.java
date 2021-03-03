package com.hendrik.http.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Response {

    private final static String VERSION = "HTTP/1.1";

    /**
     * this response's status code
     */
    private HeaderFields.StatusCode statusCode;

    /**
     * The header of this response
     */
    private Header header;

    /**
     * The body of this response
     */
    private byte[] body;

    public Response(final HeaderFields.StatusCode statusCode, final Header header, final byte[] body) {
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

    /**
     * Getter for the header lines that belong to this request
     * 
     * @return The header lines that belong to this request
     */
    public List<String> getHeaderLines() {
        List<String> result = new ArrayList<String>();

        result.add(createStatusLine());
        result.addAll(header.getLines());

        return result;

    }

    /**
     * Getter to aquire the header line for a specific header field
     * 
     * @param headerField The header field of which the line should be taken
     * @return An optional containing the header line for the inquired field. Empty if no header line belongs to the requested header field
     */
    public Optional<String> getHeaderLine(final HeaderFields.Field headerField) {
        return this.header.getLine(headerField);
    }

}