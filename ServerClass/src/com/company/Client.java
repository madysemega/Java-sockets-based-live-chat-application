package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class Client {
	private static String clientIp = new String();
    private static int clientPort = 0;
    private static BufferedReader outputFromServer;
    private static PrintWriter inputFromClient;
    private static Scanner scanner;
    private static Boolean wantToDisconnect = false;
    private static Integer MAX_MESSAGE_LENGTH = 200;

    /**
     * Client main
     */
    public static void main(String[] args) {
    	
    	System.out.println("Retrieving Client information.");
        Scanner clientInput = new Scanner(System.in);  // Create a Scan
        System.out.println("Enter the server ip adress in the format X.X.X.X with X between 0 and 255: ");
        clientIp = clientInput.nextLine();  // Read IP user input
	    while(validateClientIp() == false){
	    	System.out.println("Incorrect input, try again using format X.X.X.X with X between 0 and 255 : ");
	    	clientIp = clientInput.nextLine();
        }
	    System.out.println("Enter a port number between 5000 and 5050 : ");
	    clientPort = clientInput.nextInt();  // Read user input
 	    while (validateClientPort() == false){
 		   	System.out.println("Incorrect input, try again using a number between 5000 and 5050 : ");
 		   clientPort = clientInput.nextInt();
 	    }
 	    
 	    try(Socket socket =  new Socket(clientIp,clientPort)){ // client socket
            outputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputFromClient = new PrintWriter(socket.getOutputStream(),true);
            connectClient(); // Procedure to connect client
            try{
                IncomingMessagesManager incomingMessagesManager = new IncomingMessagesManager(); // There will be two threads of execution
                incomingMessagesManager.start(); // This will start a thread which will display server output in user console until user don't want to disconnect
                sendNewMessage(); // The loop inside this method will be executed on the main thread until user don't want to disconnect
                incomingMessagesManager.join(); // Nothing will be executed after this instruction until the thread associated with it is finished
                System.out.println("You are disconnected !");
                clientInput.close();
            }catch (InterruptedException e){
                System.out.println(e.getMessage());
            }

        }catch(IOException e){
            System.out.println("Unexpected input - Client disconnected because " + e.getMessage());
        }catch (InputMismatchException inputMismatchException){
            System.out.println("Unexpected input - Client disconnected because " + inputMismatchException.getMessage());
        }catch (NoSuchElementException e){
            System.out.println("Unexpected input - Client disconnected because " + e.getMessage());
        }finally {
	           scanner.close();
	    }
	 	   
    }
  /**
   * Exchanges information with the server to identity client.
   * @throws IOException
   */
    private static void connectClient() throws IOException {
        scanner = new Scanner(System.in); // scanner for user input
        String response = new String(); // String to hold server response
        final String connected = new String("You are now connected !"); // Message from the server indicating that the connection was successful
        updateResponse(); //
        updateInput();
        updateResponse();
        updateInput();
        response = outputFromServer.readLine();
        while (response.equals(connected) == false){ // When user type his password, a server side verification is made in a while loop to
            System.out.println(response);            // ensure user won't connect until the correct password is typed
            updateInput();
            response = outputFromServer.readLine();
        }
        System.out.println(response);
        System.out.println("Enter EXIT or close window to quit the chat.");
    }
    
    /**
     * Displays output from server to the client.
     * @throws IOException
     */
    private static void updateResponse() throws IOException {
        String response = outputFromServer.readLine(); // getting an output from server
        System.out.println(response); // Displays server output in client console
    }
    
    /**
     * Identifies client at the beginning to send client input to the server.
     * @throws IOException
     */
    private static void updateInput() throws IOException{
        String input = scanner.nextLine(); // getting an input from user
        inputFromClient.println(input); // sends user input to server
    }
    
    /**
     * Displays message from server to the client console.
     * @throws IOException
     */
    private static void displayNewMessages() throws IOException {
    	while(!wantToDisconnect){ // Loop to receive output from the server while the client is connected
            if (outputFromServer.ready()) { // When server sends an output
                String message = outputFromServer.readLine();// get the output then display it
                System.out.println(message);
            }
        }
    }
    
    /**
     * Gets message written by the client with a check of 200 caract. and sends it to the output stream.
     * @throws IOException
     */
    private static void sendNewMessage() throws IOException {
        while (!wantToDisconnect){ // Loop to send user input while the client is connected
            String userMessage = scanner.nextLine(); // gets input from user then sends it to server
            if(userMessage.length() > MAX_MESSAGE_LENGTH){
                userMessage.substring(0, MAX_MESSAGE_LENGTH);
            }
            inputFromClient.println(userMessage);
            if (userMessage.equals("EXIT")){
                wantToDisconnect = true;
            }
        }
    }
   
    private static class IncomingMessagesManager extends Thread {
        @Override
        public void run() {
            try{
                displayNewMessages(); // Method contains a loop which will execute on a another thread
            }catch (IOException exception){
                System.out.println(exception.getMessage());
            }
        }
    }
    
    /**
     * Verifies if the client IP address is correct, by checking valid entry and length.
     * @return true if IP address is valid, false otherwise
     */
    private static Boolean validateClientIp() {
    	try {
            if ( clientIp == null || clientIp.isEmpty() ) {
                return false;
            }

            String[] parts = clientIp.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( clientIp.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    /**
     * Verifies if the port is between 5000 and 5050(included)
     * @return true if port is valid, false otherwise
     */
    private static Boolean validateClientPort(){
        if (clientPort >= 5000 && clientPort <= 5050){
            return true;
        }
         return false;
    }
}
