package com.hendrik.http.http.DB;

public class DBHandler {
   
    /**
     * Adds form data read from a HTTP POST request to a MongoDB database, when the form data represents
     * a comment made on the WALLY side
     * At first, the form data is parsed and then added to a MongoDB instance
     * 
     * @param formData The form data to parse and add to the MongoDB
     * @return True if data was successfully added, false otherwise (invalid form data that does not represent a comment)
     */
    public static boolean addComment(final String formData) {



        return false;
    }

    /**
     * Parses the form data and returns an array containing the username at [0] and the comment at [1]
     * 
     * @param formData The form data to parse
     * @return The parsed username and comment. If it could not be parsed, the username or comment is ""
     */
    private String[] parseFormDataString(final String formData) {

        boolean usernameNext = false;
        boolean commentNext = false;
        String[] userNameComment = new String[2];
        userNameComment[0] = "";
        userNameComment[1] = "";

        if (formData == null) {
            return userNameComment;
        }
        
        String[] lines = formData.split("\n");

        for (String line : lines) {
            System.out.println("Form line: " + line);
            if (line.length() > 1) {
                if (commentNext) {
                    userNameComment[1] = line;
                    commentNext = false;
                } else if (usernameNext) {
                    userNameComment[0] = line;
                    usernameNext = false;
                }
            }

            if (line.contains("name=\"username\"")) {
                usernameNext = true;
            } else if (line.contains("name=\"comment\"")) {
                commentNext = true;
            }

        }

        return userNameComment;

    }

}
