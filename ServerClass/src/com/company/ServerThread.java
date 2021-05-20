package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ServerThread extends Thread {
    private Socket socket;
    private ClientsInformationManager usersDataBase = new ClientsInformationManager();
    private BufferedReader inputFromClient;
    private PrintWriter outputFromServer;
    private String username = new String();
    private String password = new String();
    private MessagesManager messagesManager = new MessagesManager();
    private ArrayList<ServerThread> activeClients;
    private boolean clientConnected = true;
    public ServerThread(Socket socket, ArrayList<ServerThread> activeClients) {
        this.socket = socket;
        this.activeClients = activeClients;
    }

    /**
     * Override of Thread interface run method to start a new thread for all clients
     */
    @Override
    public void run() {
        try{
            System.out.println("A new client is trying to connect");
            inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputFromServer = new PrintWriter(socket.getOutputStream(),true);
            retrievingClientInformation(); // Identifies clients
            sendPreviousMessagesToClient(); // Sends last 15 messages to new comers
            while (clientConnected) // It will execute until client sends a signal to disconnect
            {
                readMessagesFromClients(); // Reads a client inputs then sends them to other clients
            }
        }catch (IOException ioException)
        {
        	System.out.println(username + " has left the chat."); // Announce client's disconnection on server
        }finally {
            try
            {
                socket.close();
            }catch (IOException ioException)
            {
            	System.out.println(username + " has left the chat."); // Announce client's disconnection on server
            }
        }
    }

    /**
     * Retrieves client informations
     * @throws IOException
     */
    private void retrievingClientInformation() throws IOException {
        System.out.println("Retrieving client information.");
        outputFromServer.println("Enter your username : ");
        username = inputFromClient.readLine();
        if (findUser(username) == false) // If client is not registered
        {
            outputFromServer.println("Enter a new password : ");
            password = inputFromClient.readLine();
            addNewClient(username,password); // Adds a new client to the map and the txt database.
            outputFromServer.println("You are now connected !");
        }else // Loop until client provides a correct password for his username
        {
            outputFromServer.println("Enter your password : ");
            password = inputFromClient.readLine();
            String correctPassword = usersDataBase.get(username);
            while (password.equals(correctPassword) == false )
            {
                outputFromServer.println("Incorrect password, try again : ");
                password = inputFromClient.readLine();
            }
            outputFromServer.println("You are now connected !");
        }
        activeClients.add(this);
        System.out.println("Client connected.");
    }

    /**
     * Method to add a new client to client information databases
     * @param username username of the new client
     * @param password the new client's password
     */
    private void addNewClient(String username,String password){
        try(FileWriter fileWriter = new FileWriter("ClientsInformation.txt",true)){
            fileWriter.write(username + ", " + password + "\n"); // Writes new client information in the database
            usersDataBase.put(username,password); // Updates the map with new client information
        }catch (IOException ioException)
        {
            System.out.println("From ClientsInformationManager - " + ioException.getMessage());
        }
    }

    /**
     * Finds a user from his username
     * @param username username of the user
     * @return true if the user if found, false otherwise
     */
    private Boolean findUser(String username){
        return usersDataBase.containsKey(username);
    }

    /**
     * Sends previous 15 messages to new client
     * @throws IOException
     */
    private void sendPreviousMessagesToClient() throws IOException {
        String previousMessages = messagesManager.toString(); // Gets the contents of the queue containing the last 15 messages
        outputFromServer.println(previousMessages); // Sends a message to client
    }

    /**
     * Reads messages from clients
     * @throws IOException
     */
    private void readMessagesFromClients() throws IOException {
        String message = inputFromClient.readLine(); // Reads data from client
        if (message != null ) {
            if (!message.equals("EXIT")){ // If client still don't want to disconnect
                writeMessageInDataFile(message); // write his message in the txt database
                for (ServerThread client : activeClients) { // then send his message to all active clients
                    if (!client.username.equals(this.username)){
                        client.outputFromServer.println(toString() + message);
                    }
                }
                System.out.println(this + message); // Displays client message in server console
            }else{
                for (ServerThread client : activeClients) { // Send a message to all other clients to announce the client's disconnection
                    if (!client.username.equals(this.username)){
                        client.outputFromServer.println( "\n" + username + " has left the chat.");
                    }
                }
                System.out.println(username + " has left the chat."); // Announce client's disconnection on server
            }

        }
    }

    /**
     * Displays clients information in desired format
     * @return the userInformation in the desired format
     */
    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'@'HH:mm:ss");
        String userInformation = new String();
        Date date = new Date();
        userInformation = "[" + username + " - " +
                socket.getInetAddress().getHostAddress() +
                ":" + socket.getPort() + " - " + formatter.format(date)
                + "]: ";
        return userInformation;
    }

    /**
     * Method to write messages from clients in database
     * @param message new message to add to the database
     */
    private void writeMessageInDataFile(String message){
        try {
            String messageToWrite = "\n" + this + message;
            messagesManager.add(messageToWrite); // adds last message to last 15 messages queue
            Files.write(Paths.get("messages.txt"), messageToWrite.getBytes(), StandardOpenOption.APPEND); // writes last message in messages database
        }catch (IOException e) {
            System.err.println("*** ERROR ***\nCouldn't write in data file.");
        }
    }
}
