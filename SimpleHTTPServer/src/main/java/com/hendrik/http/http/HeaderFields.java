package com.hendrik.http.http;

/**
 * This enum specifies the supported HTTP Methods allowed in the Request
 */
enum RequestMethod {
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
enum StatusCode {
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
