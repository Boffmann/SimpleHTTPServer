package com.hendrik.http;

import java.util.List;
import java.util.Optional;

/**
 * Superclass for HTTPRequests and Responses
 */
public class HTTPMessage {
    
    /**
     * The header of this HTTPMessage
     */
    protected Header header;

    /**
     * The body of this HTTPMessage
     */
    protected byte[] body;

    /**
     * Create a new HTTPMessage by setting its headers
     * 
     * @param header The header for this HTTPMessage
     */
    public HTTPMessage(final Header header) {
        this.header = header;
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
