package com.hendrik.http;

import java.io.File;
import java.io.IOException;

import javax.management.InvalidAttributeValueException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * This is the entry point for the HTTP Server.
 * The server can be freely started on any directory and port.
 * Default Port is 8080 and the default directory to serve it the server's working directory
 * 
 * @author Hendrik Tjabben
 * 
 */
public class Main {

    /**
     * The default port value to use when no other is specified
     */
    private static final String DEFAULT_PORT = "8080";

    /**
     * The default root directory to use when no other is specified
     */
    private static String DEFAULT_ROOT_DIR = "./";

    /**
     * Main hook for the HTTP Server
     * The options parsing is taken from https://stackoverflow.com/questions/367706/how-do-i-parse-command-line-arguments-in-java
     * 
     * @param args The program's input parameters
     */
    public static void main(String[] args) {

        File workingDir = new File(System.getProperty("user.dir"));
        DEFAULT_ROOT_DIR = workingDir.getAbsolutePath();

        HTTPServer server;

        Options options = new Options();
        Option portOption = new Option("p", "port", true, "The server's port");
        portOption.setRequired(false);
        options.addOption(portOption);

        Option rootDirOption = new Option("d", "directory", true, "The root directory from which files should be served");
        rootDirOption.setRequired(false);
        options.addOption(rootDirOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            String portString = cmd.getOptionValue("port", DEFAULT_PORT);

            int port = Integer.parseInt(DEFAULT_PORT);

            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException ex) {
                System.err.println("Error: Port must be a number");
                System.exit(1);
            }

            String rootDir = cmd.getOptionValue("directory", DEFAULT_ROOT_DIR);

            try {
                server = new HTTPServer(port, rootDir);
                server.serve();
            } catch (InvalidAttributeValueException ex) {
                System.out.println("The server prohibits to use the specified parameters.");
                ex.printStackTrace();
            } catch (IOException ex) {
                System.out.println("An I/O Error happened while waiting for incoming connections.");
                ex.printStackTrace();
            }

        } catch (ParseException ex) {
            System.out.println("Error parsing the command line arguments. Error: " + ex.getMessage());
            formatter.printHelp("HTTP Server", options);

            System.exit(1);
        }


    }
}
