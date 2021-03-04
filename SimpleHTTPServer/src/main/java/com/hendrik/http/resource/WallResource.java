package com.hendrik.http.resource;

import java.io.IOException;
import java.util.List;

import com.hendrik.http.DB.Comment;
import com.hendrik.http.DB.DBHandler;

/**
 * A resource that represents a simple "Wall" which displays comments submitted via a POST request
 * 
 * @author Hendrik Tjabben
 */
public class WallResource extends Resource {

    /**
     * Basic constructor. A WallResource does not have any file handle
     */
    public WallResource() {
        super(null);
    }

    /**
     * WallResource overrides the getData function to provide a HTML file that shows previouly made comments
     * and allows to add new ones using a HTML form
     */
    @Override
    public byte[] getData() throws IOException {
        StringBuilder wallBuilder = new StringBuilder();
        wallBuilder
            .append("<html><head><title>WALLy</title></head><body>");

        List<Comment> allComments = DBHandler.getAllComments();

        for (Comment comment : allComments) {
            wallBuilder
                .append("<p>")
                .append(comment.getName() + " Wrote: ")
                .append(comment.getComment())
                .append("</p>");
        }

        wallBuilder
            .append("<form enctype=\"multipart/form-data\" action=\"/Wally\" method=\"POST\">")
            .append("<input name=\"username\" type=\"text\" value=\"Your Name\">")
            .append("<input name=\"comment\" type=\"text\" value=\"What's on your mind?\">")
            .append("<button>Submit</button>")
            .append("</form>")
            .append("</body></html>");

        return wallBuilder.toString().getBytes();
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    /**
     * A WallResource always exists because it is generated by the application
     */
    @Override
    public boolean exists() {
        return true;
    }
    
}