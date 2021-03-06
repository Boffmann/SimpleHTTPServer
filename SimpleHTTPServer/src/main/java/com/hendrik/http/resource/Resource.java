package com.hendrik.http.resource;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.hendrik.http.HTTPServer;

/**
 * Abstract class representing a resource that the HTTP Server can serve
 * 
 * @author Hendrik Tjabben
 */
public abstract class Resource {

    /** A handle to the file represented by this resource */
    protected final File handle;

    /**
     * Factory Method for creating a new resource
     * 
     * @return A new DirectoryResource when the path references a directory, a new
     *         FileResource otherwise
     * @param uri The URI relative to the server wide root directory
     * @throws UnsupportedEncodingException This error is thrown when UTF-8 encoding is not supported
     */
    public static Resource createFromURI(final String uri) throws UnsupportedEncodingException {

        if (uri.equals("/Wally")) {
            return new WallResource();
        }

        // I hope that the URLDecoder class is not considered cheating
        String urlDecoded = URLDecoder.decode(uri, StandardCharsets.UTF_8.name());

        String absolutePath = HTTPServer.getRootDirectory() + urlDecoded;

        File file = new File(absolutePath);

        if (file.isDirectory()) {
            return new DirectoryResource(file);
        } else {
            return new FileResource(file);
        }
    }

    /**
     * Constructing a new Resource
     * 
     * @param handle Handle to the underlying file
     */
    public Resource(final File handle) {
        this.handle = handle;
    }

    /**
     * Queries whether the resource is the server wide root directory 
     * 
     * @return true if resource is the serve wide root directory, false otherwise or when the resource does not exist
     */
    public boolean isRoot() {

        if (!this.exists()) {
            return false;
        }

        return this.handle.getAbsolutePath().equals(HTTPServer.getRootDirectory());
    }

    /**
     * Queries whether the resource exists in the file system
     * 
     * @return true if the resource exists, false otherwise
     */
    public boolean exists() {
        return this.handle.exists();
    }

    /**
     * Getter for the resource's name
     * 
     * @return The resource's file name. If the resource does not exist, null is returned instead
     */
    public String getName() {

        if (!this.exists()) {
            return null;
        }

        return this.handle.getName();
    }


    /**
     * Allows to check whether this resource was modified after a specific date
     * This method was inspired by
     * https://stackoverflow.com/questions/39279480/how-to-convert-rfc-1123-date-time-formatter-to-local-time
     * https://mkyong.com/java8/java-8-convert-zoneddatetime-to-timestamp/
     * 
     * @param stringDate The date to check. Must be in RFC_1123_DATE_TIME format
     * @return True when the resource was modified after stringDate, false otherwise
     */
    public boolean wasModifiedAfter(final String stringDate) {

        if (!this.exists()) {
            return false;
        }

        Timestamp lastModifiedTimestamp = new Timestamp(this.handle.lastModified());
        ZonedDateTime zdt = ZonedDateTime.parse(stringDate, DateTimeFormatter.RFC_1123_DATE_TIME);
        Timestamp parameterTimestamp = Timestamp.valueOf(zdt.toLocalDateTime());

        //the value 0 if the two Timestamp objects are equal;
        // a value less than 0 if this Timestamp object is before the given argument;
        //and a value greater than 0 if this Timestamp object is after the given argument.
        //https://docs.oracle.com/javase/8/docs/api/java/sql/Timestamp.html
        return ((lastModifiedTimestamp.compareTo(parameterTimestamp)) > 0);
    }

    /**
     * Convenience method to get a path relative to the server wide root directory from an absolute path
     * 
     * @param absolutePath The abolute path to get the relative path from
     * @return The path relative to the server wide root directory. If path is not a subdirectory from the server wide root directory,
     * the root directorie's path is returned 
     */
    private String getRelativePath(final String absolutePath) {
        // Ensure that no other directoy on system is visible but only the root and its subdirs
        if (absolutePath.indexOf(HTTPServer.getRootDirectory()) != 0) {
            return "/";
        }

        if (absolutePath.length() <= HTTPServer.getRootDirectory().length()) {
            return "/";
        }

        return absolutePath.substring(HTTPServer.getRootDirectory().length());
    }

    /**
     * Get the resource's path relative to the system wide root directory
     * 
     * @return The resource's path relative to the system wide root directory. When the file does not exist, null is returned
     */
    public String getPath() {

        if (!this.exists()) {
            return null;
        }

        String fileHandlePath = this.handle.getAbsolutePath();
        
        return getRelativePath(fileHandlePath);
    }

    /**
     * Get the resource parent directorie's path relative to the system wide root directory
     * 
     * @return The resource parent directorie's path relative to the system wide root directory. If the parent directory does not exist, null is returned
     */
    public String getParentDirectory() {

        File parentFileHandle = this.handle.getParentFile();

        if (!parentFileHandle.exists()) {
            return null;
        }

        return getRelativePath(parentFileHandle.getAbsolutePath());
    }

    /**
     * Get the resource's data bytes. To be implemented by actual resources
     * 
     * @return The resource's data bytes
     * @throws IOException An I/O Error happened during the data byte aquisition
     */
    public abstract byte[] getData() throws IOException;

    /**
     * Get the resource's content type. To be implemented by the actual resource.
     * The encoding for the content-type is always set to utf-8.
     * So my assumption is that a client can handle utf-8 encodings
     * 
     * @return The resource's content type
     */
    public abstract String getContentType();

}
