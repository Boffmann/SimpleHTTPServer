package com.hendrik.http.http;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.hendrik.http.HTTPServer;
import com.hendrik.http.http.HeaderFields.Field;
import com.hendrik.http.http.HeaderFields.RequestMethod;
import com.hendrik.http.http.HeaderFields.StatusCode;
import com.hendrik.http.http.resource.Resource;

public class ResponseBuilder {

    /**
     * Used to set the final result immutable.
     * Further setters do not change the final response after this is set to true.
     * For example useful when an error is detected while checking for ETags.
     * This error should be communicated to the client and not be overwritten by later checks.
     * It ensures that always the first encountered error is transmitted to users
     */
    private boolean isImmutable;

    private StatusCode statusCode;

    private Header header;

    private byte[] body;

    private Request request;

    private Resource resource;

    public ResponseBuilder(final Request request) {
        this.request = request;
        this.resource = Resource.createFromPath(request.getURI());
        this.isImmutable = false;
        this.header = new Header();
        this.body = "".getBytes();

        if (request.getMethod() == RequestMethod.UNSUPPORTED) {
            this.statusCode = StatusCode.NOTIMPLEMENTED;
            this.isImmutable = true;
            return;
        }

        if (!resource.exists()) {
            this.statusCode = StatusCode.NOTFOUND;
            this.isImmutable = true;
        } else {
            this.statusCode = StatusCode.OK;
        }
    }

    public ResponseBuilder setEtag() {

        if (this.isImmutable) {
            return this;
        }

        /*
        ETag description taken from https://tools.ietf.org/html/rfc2616#section-14.24
        If any of the entity tags match the entity tag of the entity that
        would have been returned in the response to a similar GET request
        (without the If-Match header) on that resource, or if "*" is given
        and any current entity exists for that resource, then the server MAY
        perform the requested method as if the If-Match header field did not
        exist.
        // Having both If-Match and If-None-Match or If-Modified-Since is undefined behaviour
        */
        Optional<List<String>> ifMatchEntries = request.getHeaderValues(Field.IF_MATCH);
        Optional<List<String>> ifModifiedSinceEntries = request.getHeaderValues(Field.IF_MODIFIED_SINCE);
        Optional<List<String>> etagEntry = request.getHeaderValues(Field.ENTITIY_TAG);
            if (ifMatchEntries.isPresent()) { 
                boolean didMatch = false;
                boolean starMatchProvided = false;

                try {
                    String bodyHash = hashBytes(this.resource.getData());
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
                    this.statusCode = StatusCode.INTERNALERROR;
                    this.isImmutable = true;
                    return this;
                }

                // If none of the entity tags match, or if "*" is given and no current entity exists,
                // the server MUST NOT perform the requested method, and MUST return a 412 (Precondition Failed) response.
                if (!didMatch || (starMatchProvided && !etagEntry.isPresent())) {
                    this.statusCode = StatusCode.PRECONDITION_FAILED;
                    this.isImmutable = true;
                    return this;
                }
            } else if (ifModifiedSinceEntries.isPresent()) { // My assumption here is that the passed date is always parseable by the server
                for (String isModifiedSinceDate : ifModifiedSinceEntries.get()) {
                    boolean dateValid = isDateValid(isModifiedSinceDate);
                    if (!dateValid) {
                        return this;
                    }
                    if (!this.resource.wasModifiedAfter(isModifiedSinceDate)) {
                        this.statusCode = StatusCode.NOTMODIFIED;
                        this.isImmutable = true;
                        return this;
                    }                    
                }
            }
        
        this.statusCode = StatusCode.OK;

        return this;
    }

    /**
     * Builds the final result by setting general header fields, as well as the response body's
     * content type and data
     * @return
     */
    public Response build() {

        try {
            byte[] resourceBody = this.resource.getData();

            if (this.statusCode == StatusCode.OK) {
                this.header.addEntry(Field.CONTENT_TYPE, resource.getContentType());
                this.setBody(resourceBody);
            } else {
                this.header.addEntry(Field.CONTENT_TYPE, "text/plain");
                this.setBody(HeaderFields.toString(this.statusCode).getBytes());
            }

            if (this.resource.exists()) {
                this.header.addEntry(Field.ENTITIY_TAG, hashBytes(this.resource.getData()));
            }
            this.header.addEntry(Field.CONTENT_LENGTH, String.valueOf(this.body.length));
            this.header.addEntry(Field.SERVER, HTTPServer.getServerInfo());
            this.header.addEntry(Field.DATE, DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetDateTime.now()));

        } catch (IOException e) {
            this.header.addEntry(Field.CONTENT_TYPE, "text/plain");
            this.setBody(HeaderFields.toString(StatusCode.INTERNALERROR).getBytes());
        }


        return new Response(this.statusCode, this.header, this.body);
    }

    /**
     * Sets the body for the final response
     * HEAD requests should not contain a body
     * 
     * @param data The data to set as the response body
     */
    private void setBody(final byte[] data) {

        if (this.request.getMethod() != RequestMethod.HEAD) {
            this.body = data;
        }

    }

    /**
     * Convenience method to hash a byte array.
     * This is used for etag checks
     * 
     * @param bytes The byte array to hash
     * @return The hash code for the provided byte array
     */
    private static String hashBytes(final byte[] bytes) {
        String bodyHash = String.valueOf(Arrays.hashCode(bytes));
        return bodyHash;
    }

    /**
     * Check whether a specified date string is valid
     * The date is invalid when it is later than the server's current time (following RFC2616)
     * 
     * @param date The date string to check
     * @return True if the date string is valid, false otherwise
     */
    private static boolean isDateValid(final String date) {

        ZonedDateTime now = ZonedDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now.toLocalDateTime());

        ZonedDateTime passedDate = ZonedDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME);
        Timestamp passedTimestamp = Timestamp.valueOf(passedDate.toLocalDateTime());
        //the value 0 if the two Timestamp objects are equal;
        // a value less than 0 if this Timestamp object is before the given argument;
        //and a value greater than 0 if this Timestamp object is after the given argument.
        //https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html
        return ((nowTimestamp.compareTo(passedTimestamp)) > 0);
    }
}