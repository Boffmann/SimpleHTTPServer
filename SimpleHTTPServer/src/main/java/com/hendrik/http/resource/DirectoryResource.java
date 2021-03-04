package com.hendrik.http.resource;

import java.io.File;

import com.hendrik.http.HTTPServer;

/**
 * A class for Resources that are dictionaries
 *  
 * @author Hendrik Tjabben
 */
public class DirectoryResource extends Resource {

    /**
     * Constructor for Dictionary Resource
     * 
     * @param handle directory handle managed by this resource
     */
    protected DirectoryResource(final File handle) {
        super(handle);
    }
    
    /**
     * Returns the directorie's content in html form
     * 
     * @return the Directory in html in bytes
     */
    @Override
    public byte[] getData() {
        return createDirectoryHTML().getBytes();
    }

    /**
     * Getter for the content type
     * 
     * @return text/html content type
     */
    @Override
    public String getContentType() {
        return "text/html";
    }

    /**
     * Builds the directories file structure as a html file
     * 
     * @return The directorie's structure as html
     */
    private String createDirectoryHTML() {

        StringBuilder stringBuilder = new StringBuilder()
            .append("<html>")
            .append(HTTPServer.NEW_LINE)
            .append("<body>")
            .append(HTTPServer.NEW_LINE)
            .append("<hr><b> Viewing: ")
            .append(getPath())
            .append("</b><hr>")
            .append(HTTPServer.NEW_LINE)
            .append("<ul>")
            .append(HTTPServer.NEW_LINE);

        if (!isRoot()) {
            stringBuilder
                .append("<li> <a href=\"")
                .append(getParentDirectory())
                .append("\">")
                .append("..")
                .append("</a></li>")
                .append(HTTPServer.NEW_LINE)
                .append(HTTPServer.NEW_LINE);
        }

        File[] content = this.handle.listFiles();

        String dilimeter = this.isRoot() ? "" : "/";
        for (File file : content) {
            stringBuilder
                .append("<li> <a href=\"")
                .append(getPath() + dilimeter + file.getName())
                .append("\">")
                .append(file.getName())
                .append("</a></li>")
                .append(HTTPServer.NEW_LINE);
        }

        stringBuilder
            .append("</ul>")
            .append(HTTPServer.NEW_LINE)
            .append("</body>")
            .append(HTTPServer.NEW_LINE)
            .append("</html>")
            .append(HTTPServer.NEW_LINE);

        return stringBuilder.toString();
    }


}
