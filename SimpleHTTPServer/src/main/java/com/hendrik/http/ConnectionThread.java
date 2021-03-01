package com.hendrik.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.hendrik.http.http.Request;
import com.hendrik.http.http.Response;

/**
 * A Thread that allows to serve incoming HTTP GET and HEAD requests
 * 
 * @author Hendrik Tjabben
 */
public class ConnectionThread extends Thread {
    
    /**
     * The socket used to exchange data with the client
     */
    private Socket clientSocket;

    /**
     * Constructor for creating a new ConnectionThread
     * 
     * @param clientSocket The connection's socket
     */
    public ConnectionThread(final Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Convenience method for closing a socket
     * 
     * @param socket The socket to close
     */
    private void closeSocket(Socket socket) {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing the Socket");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            if (clientSocket.isClosed()) {
                System.out.println("The socket is closed");
                return;
            }

            InputStream input = clientSocket.getInputStream();

            if (input == null) {
                System.err.println("Cannot serve Request. Request is empty");
                closeSocket(clientSocket);
                return;
            }

            OutputStream output = clientSocket.getOutputStream();
            DataOutputStream outputStream = new DataOutputStream(output);

            Request request = new Request(input);
            Response response = Response.createForRequest(request);

            for (String line : response.getHeaderLines()) {
                outputStream.writeBytes(line);
                outputStream.writeBytes("\r\n");
            }
            outputStream.writeBytes("\r\n");
            outputStream.write(response.getMessage());
            outputStream.flush();
            outputStream.close();

            // try {
            //     Request request = new Request(input);
            //     Response response = new Response(request);
            //     response.writeToStream(output);
            // } catch (IllegalArgumentException ex) {
            //     answerBadRequest(output);
            //     closeSocket(clientSocket);
            //     return;
            // }

        } catch (IOException ex) {
            System.out.println("Server I/O exception while serving client: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeSocket(clientSocket);
        }
    }
}