package com.hendrik.http.http;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.hendrik.http.HTTPServer;
import com.hendrik.http.http.HeaderFields.Field;
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

        try {

            switch (request.getMethod()) {
                case GET:
                    if (!requestedResource.exists()) {
                        header.addEntry(Field.CONTENT_TYPE, "text/plain");
                        return new Response(HeaderFields.StatusCode.NOTFOUND, header, "404 - File Not Found".getBytes());
                    } else {
                        header.addEntry(Field.CONTENT_TYPE, requestedResource.getContentType());
                        return new Response(HeaderFields.StatusCode.OK, header, requestedResource.getData());
                    }
                case HEAD:
                    if (!requestedResource.exists()) {
                        header.addEntry(Field.CONTENT_TYPE, "text/plain");
                        return new Response(HeaderFields.StatusCode.NOTFOUND, header, "404 - File Not Found".getBytes());
                    } else {
                        header.addEntry(Field.CONTENT_TYPE, requestedResource.getContentType());
                        return new Response(HeaderFields.StatusCode.OK, header, "".getBytes());
                    }
                default:
                    header.addEntry(Field.CONTENT_TYPE, "text/plain");
                    return new Response(HeaderFields.StatusCode.NOTIMPLEMENTED, header, "The requested behaviour is not implemented".getBytes());
            }
        } catch (IOException ex) {
            header.addEntry(Field.CONTENT_TYPE, "text/plain");
            return new Response(HeaderFields.StatusCode.INTERNALERROR, header, "Error while getting the requested resources data".getBytes());
        }
    }

    /**
     * Creates a new HTTP Response. The factory method {@link createForRequest} should be used for creating
     * Responses from HTTP Requests. This is because it makes the distinctions between the different cases more easy
     * 
     * @param statusCode The status code for this HTTP response
     * @param header The header for this HTTP response
     * @param body The body of this HTTP response
     */
    private Response(final HeaderFields.StatusCode statusCode, final Header header, final byte[] body) {
        this.statusCode = statusCode;
        this.header = header;
        this.body = body;

        this.header.addEntry(Field.CONTENT_LENGTH, String.valueOf(this.body.length));
        this.header.addEntry(Field.SERVER, HTTPServer.getServerInfo());
        this.header.addEntry(Field.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetDateTime.now()));
    }

    private String createStatusLine() {
        
        String code = "";
        String phrase = "";

        switch(statusCode) {
            case OK:
                code = "200";
                phrase = "OK";
                break;
            case NOTFOUND:
                code = "404";
                phrase = "Not Found";
                break;
            case INTERNALERROR:
                code = "500";
                phrase = "Internal Server Error";
                break;
            case NOTIMPLEMENTED:
                code = "501";
                phrase = "Not Implemented";
                break;

        }

        StringBuilder statusLineBuilder = new StringBuilder()
            .append(VERSION)
            .append(" ")
            .append(code)
            .append(" ")
            .append(phrase);

        return statusLineBuilder.toString();
    }

    public List<String> getHeaderLines() {
        List<String> result = new ArrayList<String>();

        result.add(createStatusLine());
        result.addAll(header.getLines());

        return result;

    }

    public byte[] getData() {
        return this.body;
    }

    public Optional<String> getHeaderValue(final HeaderFields.Field headerField) {
        return this.header.getLine(headerField);
    }

}