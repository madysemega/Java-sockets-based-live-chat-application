package com.company;

import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private static String serverIp = new String();
    private static int serverPort = 0;
    private static ArrayList<ServerThread> activeClients = new ArrayList<ServerThread>();
    private static FileWriter fileWriter;

    /**
     * Server main
     */
    public static void main(String[] args) {
    	
    	System.out.println("Retrieving Server information.");
        Scanner serverInput = new Scanner(System.in);  // Create a Scan
        System.out.println("Enter the server ip adress in format X.X.X.X with X between 0 and 255: ");
	    serverIp = serverInput.nextLine();  // Read IP user input
	    while(validateServerIp() == false){
	    	System.out.println("Incorrect input, try again using format X.X.X.X with X between 0 and 255 : ");
	    	serverIp = serverInput.nextLine();
        }
	    System.out.println("Enter a port number between 5000 and 5050 : ");
 	    serverPort = serverInput.nextInt();  // Read user input
 	    while (validateServerPort() == false){
 		   	System.out.println("Incorrect input, try again using a number between 5000 and 5050 : ");
 		   	serverPort = serverInput.nextInt();
 	    }
 	    serverInput.close();
    	try(ServerSocket serverSocket = new ServerSocket(serverPort)){ // try with resources -> The object destruction will be managed by the try block
            System.out.println("Server started.");
            System.out.println("Waiting for clients ...");
            fileWriter = new FileWriter("messages.txt",true);
            while (true){ // This loop will start a new thread for all clients
                new ServerThread(serverSocket.accept(),activeClients).start(); // When a thread starts, a new ServerThread is created and
            }                                                                  // it waits for the next client to connect
        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }finally {
            try {
                fileWriter.close();
            }catch (IOException e){
                System.out.println("Error - File writer can't be closed " + e.getMessage());
            }
        }
    }
    
    /**
     * Verifies if the client IP address is correct, by checking valid entry and length.
     * @return true if IP address is valid, false otherwise
     */
    private static Boolean validateServerIp() {
    	try {
            if ( serverIp == null || serverIp.isEmpty() ) {
                return false;
            }

            String[] parts = serverIp.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( serverIp.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    /**
     * Verifies if the port is between 5000 and 5050(included)
     * @return true if the port is valid, false otherwise
     */
    private static Boolean validateServerPort(){
        if (serverPort >= 5000 && serverPort <= 5050){
            return true;
        }
         return false;
    }
}
