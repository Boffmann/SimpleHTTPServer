package com.hendrik.http.http;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hendrik.http.HTTPServer;
import com.hendrik.http.http.HeaderFields.Field;
import com.hendrik.http.http.HeaderFields.StatusCode;
import com.hendrik.http.http.resource.Resource;

public class Response {

    private final static String VERSION = "HTTP/1.1";

    private HeaderFields.StatusCode statusCode;

    /**
     * The header of this response
     */
    private Header header;

    /**
     * The body of this response
     */
    private byte[] body;

    /**
     * Create a new Response for an HTTP Request
     * 
     * @param request The request to create the response for
     */
    public static Response createForRequest(final Request request) {

        Header header = new Header();

        Resource requestedResource = Resource.createFromPath(request.getURI());

        switch (request.getMethod()) {
            case GET:
                if (!requestedResource.exists()) {
                    return new Response(HeaderFields.StatusCode.NOTFOUND, header, null);
                } else {
                    header.addEntry(Field.CONTENT_TYPE, requestedResource.getContentType());
                    
                    /*
                    ETag description taken from https://tools.ietf.org/html/rfc2616#section-14.24
                    If any of the entity tags match the entity tag of the entity that
                    would have been returned in the response to a similar GET request
                    (without the If-Match header) on that resource, or if "*" is given
                    and any current entity exists for that resource, then the server MAY
                    perform the requested method as if the If-Match header field did not
                    exist.
                    */
                    Optional<List<String>> ifMatchEntries = request.getHeaderValues(Field.IF_MATCH);
                    Optional<List<String>> etagEntry = request.getHeaderValues(Field.ENTITIY_TAG);
                    if (ifMatchEntries.isPresent()) {
                        boolean didMatch = false;
                        boolean starMatchProvided = false;

                        try {
                            String bodyHash = Response.hashBytes(requestedResource.getData());
                            for (String etag : ifMatchEntries.get()) {
                                if (etag.equals(bodyHash)) {
                                    didMatch = true;
                                }
                                if (etag.equals("*")) {
                                    starMatchProvided = true;
                                    didMatch = true;
                                }
                            }
                        } catch (IOException ex) {
                            return new Response(HeaderFields.StatusCode.INTERNALERROR, header, null);
                        }

                        // If none of the entity tags match, or if "*" is given and no current entity exists,
                        // the server MUST NOT perform the requested method, and MUST return a 412 (Precondition Failed) response.
                        if (!didMatch || (starMatchProvided && !etagEntry.isPresent())) {
                            return new Response(HeaderFields.StatusCode.PRECONDITION_FAILED, header, null);
                        }
                    }

                    return new Response(HeaderFields.StatusCode.OK, header, requestedResource);
                }
            case HEAD:
                if (!requestedResource.exists()) {
                    return new Response(HeaderFields.StatusCode.NOTFOUND, header, null);
                } else {
                    header.addEntry(Field.CONTENT_TYPE, requestedResource.getContentType());
                    return new Response(HeaderFields.StatusCode.OK, header);
                }
            default:
                return new Response(HeaderFields.StatusCode.NOTIMPLEMENTED, header, null);
        }
    }

    private void init(final HeaderFields.StatusCode statusCode, final Header header, final byte[] body) {
        this.statusCode = statusCode;
        this.header = header;
        this.body = body;

        this.header.addEntry(Field.ENTITIY_TAG, hashBytes(this.body));
        this.header.addEntry(Field.CONTENT_LENGTH, String.valueOf(this.body.length));
        this.header.addEntry(Field.SERVER, HTTPServer.getServerInfo());
        this.header.addEntry(Field.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetDateTime.now()));
    }

    /**
     * Creates a new HTTP Response. The factory method {@link createForRequest}
     * should be used for creating Responses from HTTP Requests. This is because it
     * makes the distinctions between the different cases more easy
     * 
     * @param statusCode The status code for this HTTP response
     * @param header     The header for this HTTP response
     * @param resource   The resource to serve by this request
     */
    private Response(final HeaderFields.StatusCode statusCode, final Header header, final Resource resource) {

        try {
            if (resource == null) {
                header.addEntry(Field.CONTENT_TYPE, "text/plain");
                init(statusCode, header, HeaderFields.toString(statusCode).getBytes());

            } else {
                init(statusCode, header, resource.getData());
            }
        } catch (IOException ex) {
            header.addEntry(Field.CONTENT_TYPE, "text/plain");
            init(StatusCode.INTERNALERROR, header, HeaderFields.toString(StatusCode.INTERNALERROR).getBytes());
        }


    }

    private Response(final HeaderFields.StatusCode statusCode, final Header header) {
        init(statusCode, header, "".getBytes());
    }


    private static String hashBytes(final byte[] bytes) {
        String bodyHash = String.valueOf(Arrays.hashCode(bytes));
        return bodyHash;
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