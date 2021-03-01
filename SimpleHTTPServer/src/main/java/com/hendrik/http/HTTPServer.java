package com.hendrik.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.management.InvalidAttributeValueException;

/**
 * The actual HTTPServer.
 * It serves incoming requests using {@link java.net.ServerSocket}.
 * 
 * @author Hendrik Tjabben
 */
public class HTTPServer {

    /**
     * System specific newLine character
     */
    public static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * The root directory from which the server serves files and subdirectories
     */
    private static String rootDirectory;

    /**
     * The server socket used for incoming connections
     */
    private ServerSocket serverSocket;
    
    /**
     * Constructor for HTTPServer.
     * It does a check for the desired port regarding the allowed range.
     * Ports reserved for the OS are not allowed to be taken
     * 
     * @param port The port to bind the HTTPServer to. Must be between 1024-65535
     * @param rootDirectory The root directory from which to serve files
     * @throws InvalidAttributeValueException Thrown when the desired port is not allowed to take or the desired root directory does not exist
     */
    public HTTPServer(final int port, final String rootDirectory) throws InvalidAttributeValueException {

        if (port < 0) {
            throw new InvalidAttributeValueException("Port number cannot be below 0");
        } else if (port > 65535) {
            throw new InvalidAttributeValueException("Port number cannot be above 65535");
        }

        Path rootPath = FileSystems.getDefault().getPath(rootDirectory);
        if (!Files.exists(rootPath)) {
            throw new InvalidAttributeValueException("The specified root path does not exist");
        }
        HTTPServer.rootDirectory = rootDirectory;

        try {
            serverSocket = new ServerSocket(port); 
            System.out.println("The server is started on port " + port);
        } catch (IOException ex) {
            System.out.println("Error creating webserver on port " + port + ". Exception: " + ex.getMessage());
            return;
        }

    }

    /**
     * Getter for the server wide root directory from which to serve files
     * 
     * @return The root directory from which to serve files
     */
    public static final String getRootDirectory() {
        return HTTPServer.rootDirectory;
    }

    /**
     * Static getter for the server info used e.g. in Server: response header field
     * 
     * @return The server information
     */
    public static final String getServerInfo() {
        return "My Simple HTTP Server 0.1";
    }

    /**
     * Starts to accept and serve incoming requests.
     * Spawns a new thread so that multiple connections can be served in parallel.
     * 
     * @throws IOException Thrown if an IO error happens while waiting for connections
     */
    public void serve() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New connection");
            new ConnectionThread(socket).start();
        }
    }




}