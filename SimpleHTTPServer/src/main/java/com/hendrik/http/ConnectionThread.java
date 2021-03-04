package com.hendrik.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Optional;

import com.hendrik.http.HeaderFields.Field;

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

        boolean shouldServe = true;

        try {

            // Persistent connections are the default in HTTP/1.1
            // https://www.w3.org/Protocols/rfc2616/rfc2616-sec8.html
            clientSocket.setKeepAlive(true);

            InputStream input = clientSocket.getInputStream();

            if (input == null) {
                System.err.println("Cannot serve Request. Request is empty");
                closeSocket(clientSocket);
                return;
            }

            OutputStream output = clientSocket.getOutputStream();
            DataOutputStream outputStream = new DataOutputStream(output);

            while (shouldServe) {

                Request request = new Request(input);
                ResponseBuilder responseBuilder = new ResponseBuilder(request);
                Response response = responseBuilder
                    .setEtag()
                    .build();
                
                for (String line : response.getHeaderLines()) {
                    outputStream.writeBytes(line);
                    outputStream.writeBytes("\r\n");
                }
                outputStream.writeBytes("\r\n");
                outputStream.write(response.getData());
                outputStream.flush();

                Optional<List<String>> connectionHeader = response.getHeaderValues(Field.CONNECTION);

                if (connectionHeader.isPresent()) {
                    for (String value : connectionHeader.get()) {
                        if (value.toLowerCase().equals("close")) {
                            shouldServe = false; 
                            output.close();
                            input.close();
                        }
                    }
                }
            }

        } catch (IOException ex) {
            System.out.println("Server I/O exception while serving client: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            closeSocket(clientSocket);
        }
    }
}
