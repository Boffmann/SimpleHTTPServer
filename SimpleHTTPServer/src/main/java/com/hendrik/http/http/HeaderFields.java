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
         * The server had an internal error
         */
        INTERNALERROR
    };

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
        DATE

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
            default:
                return null;
        }
    }

    public static Field getFieldForString(final String fieldString) {

        if (fieldString.toLowerCase().equals(toString(Field.CONTENT_TYPE))) {
            return Field.CONTENT_TYPE;
        } else if (fieldString.toLowerCase().equals(toString(Field.CONTENT_LENGTH))) {
            return Field.CONTENT_LENGTH;
        } else if (fieldString.toLowerCase().equals(toString(Field.SERVER))) {
            return Field.SERVER;
        } else if (fieldString.toLowerCase().equals(toString(Field.DATE))) {
            return Field.DATE;
        }
        
        return null;
    }

}