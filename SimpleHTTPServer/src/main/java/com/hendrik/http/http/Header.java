package com.hendrik.http.http;

import java.util.ArrayList;
import java.util.List;

public class Header {

    /**
     * All header lines
     */
    private List<String> headerLines;

    public Header() {
        this.headerLines = new ArrayList<String>();
    }

    public void addLine(final String key, final String value) {
        StringBuilder headerLineBuilder = new StringBuilder()
            .append(key)
            .append(": ")
            .append(value);
        
        this.headerLines.add(headerLineBuilder.toString());
    }

    public void addLine(final String line) {
        this.headerLines.add(line);
    }

    public List<String> getLines() {
        return this.headerLines;
    }

}