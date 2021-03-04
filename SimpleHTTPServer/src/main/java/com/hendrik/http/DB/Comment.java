package com.hendrik.http.DB;

public class Comment {
   
    private final String NAME;

    private final String COMMENT;

    public Comment(final String name, final String comment) {
        this.NAME = name;
        this.COMMENT = comment;
    }

    public String getName() {
        return this.NAME;
    }

    public String getComment() {
        return this.COMMENT;
    }
}
