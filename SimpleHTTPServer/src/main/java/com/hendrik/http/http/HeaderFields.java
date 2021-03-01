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

        CONTENT_TYPE

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
            default:
                return null;
        }
    }

    public static Field getFieldForString(final String fieldString) {

        if (fieldString.toLowerCase().equals(toString(Field.CONTENT_TYPE))) {
            return Field.CONTENT_TYPE;
        }
        
        return null;
    }

}