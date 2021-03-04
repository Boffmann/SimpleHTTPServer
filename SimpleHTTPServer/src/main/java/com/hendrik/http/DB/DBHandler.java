package com.hendrik.http.DB;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

/**
 * A wrapper class for a MongoDB instance that allows to store and retrive documents
 * published by users to the wall
 * 
 * @author Hendrik Tjabben 
 */
public class DBHandler {

    /**
     * A static db client for MongoDB
     */
    private static MongoClient dbClient = MongoClients.create();

    /**
     * A static DB instance representing a comment document DB
     */
    private static MongoDatabase db = dbClient.getDatabase("myCommentDB");

    /**
     * The collection where the comments are written to in the DB
     */
    private static MongoCollection<Document> comments = db.getCollection("comments");
   
    /**
     * Adds form data read from a HTTP POST request to a MongoDB database, when the form data represents
     * a comment made on the WALLY side
     * At first, the form data is parsed and then added to a MongoDB instance
     * 
     * @param formData The form data to parse and add to the MongoDB
     * @return True if data was successfully added, false otherwise (invalid form data that does not represent a comment)
     */
    public static boolean addComment(final String formData) {

        String[] nameComment = parseFormDataString(formData);

        if (nameComment[0] == "" || nameComment[1] == "") {
            System.out.println("Name or comment not set");
            return false;
        }

        Document newComment = new Document("name", nameComment[0])
                                .append("comment", nameComment[1]);

        comments.insertOne(newComment);


        return true;
    }

    /**
     * Getter that reads all entries from the Database
     * 
     * @return A list containing all comments made so far
     */
    public static List<Comment> getAllComments() {

        List<Comment> result = new ArrayList<Comment>();

        FindIterable<Document> allCommentsInDB = comments.find();
        for (Document comment : allCommentsInDB) {
            Comment newComment = new Comment((String)comment.get("name"), (String)comment.get("comment"));
            result.add(newComment);
        }

        return result;
    }

    /**
     * Parses the form data and returns an array containing the username at [0] and the comment at [1]
     * 
     * @param formData The form data to parse
     * @return The parsed username and comment. If it could not be parsed, the username or comment is ""
     */
    private static String[] parseFormDataString(final String formData) {

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
