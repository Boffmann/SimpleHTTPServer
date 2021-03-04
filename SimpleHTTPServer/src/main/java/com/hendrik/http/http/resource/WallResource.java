package com.hendrik.http.http.resource;

import java.io.IOException;

public class WallResource extends Resource {

    public WallResource() {
        super(null);
    }

    @Override
    public byte[] getData() throws IOException {
        StringBuilder wallBuilder = new StringBuilder();

        wallBuilder
        .append("<html><head><title>WALLy</title></head><body>")
        .append("<form enctype=\"multipart/form-data\" action=\"/WallyComment\" method=\"POST\">")
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

    @Override
    public boolean exists() {
        return true;
    }
    
}
