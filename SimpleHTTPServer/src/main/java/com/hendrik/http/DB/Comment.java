package com.hendrik.http.DB;

/**
 * This class represents a comment that was made on the "/Wally" wall
 * 
 * @author Hendrik Tjabben
 */
public class Comment {
   
    /**
     * The name of this comment's author
     */
    private final String NAME;

    /**
     * The content of the comment itself
     */
    private final String COMMENT;

    /**
     * Constructor for a new comment with an author name and a content string
     * 
     * @param name The name of this comment's author
     * @param comment This comment's content
     */
    public Comment(final String name, final String comment) {
        this.NAME = name;
        this.COMMENT = comment;
    }

    /**
     * Getter for the author's name
     * 
     * @return The author's name
     */
    public String getName() {
        return this.NAME;
    }

    /**
     * Getter for this comment's content
     * 
     * @return This comment's content
     */
    public String getComment() {
        return this.COMMENT;
    }
}
