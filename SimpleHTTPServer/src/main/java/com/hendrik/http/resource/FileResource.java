package com.hendrik.http.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;

/**
 * A class for resources that are files
 * 
 * @author Hendrik Tjabben
 */
public class FileResource extends Resource {

    /**
     * Constructor for File Resource
     * 
     * @param handle the file handle managed by this resource
     */
    protected FileResource(final File handle) {
        super(handle);
    }

    /**
     * Getter for the File Resource's data.
     * 
     * @throws IOException An I/O Error happened while getting the file's data
     */
    @Override
    public byte[] getData() throws IOException {

        if (!this.exists()) {
            return "".getBytes();
        }

		InputStream in = new FileInputStream(this.handle);
        return IOUtils.toByteArray(in);

    }

    /**
     * Get the File Resources content type
     * 
     * @return The content type. If the content type cannot be determined, text/plain is used 
     */
    @Override
    public String getContentType() {
        try {
            return Files.probeContentType(this.handle.toPath());
        } catch (IOException e) {
            System.out.println("Content type could not be determined. Handling as plain text");
            return "text/plain";
        }
    }
    
}
