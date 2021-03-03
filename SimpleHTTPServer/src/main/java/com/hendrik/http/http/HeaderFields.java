package com.hendrik.http.http;

public class HeaderFields{

    /**
     * This enum specifies the supported HTTP Methods allowed in the Request
     */
    public enum RequestMethod {
        /**
         * Represents an HTTP GET request
         */
        GET,

        /**
         * Represents an HTTP HEAD request
         */
        HEAD,

        /**
         * Represents that the desired HTTP Method type is not supported by the server
         */
        UNSUPPORTED
    };

    /**
     * This enum specifies the Response's status code
     */
    public enum StatusCode {
        /**
         * The request was successful
         */
        OK,

        /**
         * The server has not found the requested resource
         */
        NOTFOUND,

        /**
         * The server does not support the functionality to fulfill the request
         */
        NOTIMPLEMENTED,

        /**
         * The client made a bad request
         */
        BADREQUEST,

        /**
         * The requested resource was not modified since the requested If-Modified-Since date
         */
        NOTMODIFIED,

        /**
         * The precondition for this request failed
         */
        PRECONDITION_FAILED,

        /**
         * The server had an internal error
         */
        INTERNALERROR
    };

    /**
     * Get the string representation for the status code
     * 
     * @return The string representation for the status code
     */
    public static String toString(final StatusCode statusCode) {
        
        switch(statusCode) {
            case OK:
                return "200 OK";
            case NOTMODIFIED:
                return "304 Not Modified";
            case BADREQUEST:
                return "400 Bad Request";
            case NOTFOUND:
                return "404 Not Found";
            case PRECONDITION_FAILED:
                return "413 Precondition Failed";
            case INTERNALERROR:
                return "500 Internal Server Error";
            default:
                return "501 Not Implemented";

        }
    }

    /**
     * This enum represents the Header fields that are currently supported by this server
     */
    public enum Field {

        /**
         * The MIME type of the content
         */
        CONTENT_TYPE,

        /**
         * The number of bytes in the response body
         */
        CONTENT_LENGTH,

        /**
         * This server's name
         */
        SERVER,

        /**
         * The data and time that the message was sent
         */
        DATE,

        /**
         * The request or responses entitiy tag
         */
        ENTITIY_TAG,

        /**
         * Header field used for If-Match ETag behaviour
         */
        IF_MATCH,

        /**
         * Header field used for If-Non-Match ETag behaviour
         */
        // IF_NONE_MATCH,

        /**
         * Header field used for If-Modified-Since ETag behaviour
         */
        IF_MODIFIED_SINCE

    };

    /**
     * Gets the header string representation for a supported field
     * 
     * @param field The field to get the string representation of
     * @return The string representing the header field
     */
    public static String toString(final Field field) {
        switch (field) {
            case CONTENT_TYPE:
                return "Content-Type";
            case CONTENT_LENGTH:
                return "Content-Length";
            case SERVER:
                return "Server";
            case DATE:
                return "Date";
            case ENTITIY_TAG:
                return "ETag";
            case IF_MATCH:
                return "If-Match";
            case IF_MODIFIED_SINCE:
                return "If-Modified-Since";
            default:
                return null;
        }
    }

    public static Field getFieldForString(final String fieldString) {

        if (fieldString.toLowerCase().equals(toString(Field.CONTENT_TYPE).toLowerCase())) {
            return Field.CONTENT_TYPE;
        } else if (fieldString.toLowerCase().equals(toString(Field.CONTENT_LENGTH).toLowerCase())) {
            return Field.CONTENT_LENGTH;
        } else if (fieldString.toLowerCase().equals(toString(Field.SERVER).toLowerCase())) {
            return Field.SERVER;
        } else if (fieldString.toLowerCase().equals(toString(Field.DATE).toLowerCase())) {
            return Field.DATE;
        } else if (fieldString.toLowerCase().equals(toString(Field.ENTITIY_TAG).toLowerCase())) {
            return Field.ENTITIY_TAG;
        } else if (fieldString.toLowerCase().equals(toString(Field.IF_MATCH).toLowerCase())) {
            return Field.IF_MATCH;
        } else if (fieldString.toLowerCase().equals(toString(Field.IF_MODIFIED_SINCE).toLowerCase())) {
            return Field.IF_MODIFIED_SINCE;
        }
        
        return null;
    }

    /**
     * Check whether the specified header field allows to have multiple, comma separated, values.
     * 
     * @param field The field to check for
     * @return True if the field can have multiple values, false otherwise
     */
    public static boolean allowsMultipleValues(final Field field) {

        if (field == Field.IF_MATCH) {
            return true;
        }
        
        return false;
    }

    /**
     * Check whether the specified header field represents a date
     * 
     * @param field The field to check for
     * @return True if a RFC1123 Date is represented by this field, false otherwise
     */
    public static boolean isDateField(final Field field) {

        if (field == Field.IF_MODIFIED_SINCE || field == Field.DATE) {
            return true;
        }
        return false;
    }

}