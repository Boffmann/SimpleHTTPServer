package com.hendrik.http;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a HTTP Response message
 * 
 * @author Hendrik Tjabben
 */
public class Response extends HTTPMessage {

    /**
     * this response's status code
     */
    private HeaderFields.StatusCode statusCode;

    /**
     * Constructor for a new Response
     * 
     * @param statusCode The status code for this response
     * @param header The new response's headers
     * @param body The new response's body
     */
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
            .append(HTTPServer.getHTTPVersion())
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
     * Getter for all header lines that are included in this response.
     * Used to write the header lines to the outstream
     * 
     * @return All header lines of this response.
     */
    public List<String> getHeaderLines() {
        List<String> result = new ArrayList<String>();

        result.add(createStatusLine());
        result.addAll(header.getLines());

        return result;

    }


}