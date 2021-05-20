package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MessagesManager {
    private static Queue<String> messages = new LinkedList<String>(); // This queue will contain the last 15 messages and be dynamically updated to display them to new clients.
    private static final Integer MAX_NUMBER_OF_MESSAGES = 15;

    /**
     * Static initialization to fill the queue with the content of the .txt file once for all instances of the class.
     */
    static {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader("messages.txt"))); // Scanner to read the txt file content
            while (scanner.hasNextLine())
            {
                String nextLine = scanner.nextLine();
                if (!nextLine.equals("")){ // To skip empty lines
                    messages.add(nextLine);
                    if (messages.size() > MAX_NUMBER_OF_MESSAGES){ // If the queue is at 16 messages, eject the first message
                        messages.poll();
                    }
                }
            }
        } catch (IOException ioException)
        {
            System.out.println("From ClientsInformationManager - " + ioException.getMessage());
        }finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     * Makes a String from the queue content
     * @return last 15 messages
     */
    @Override
    public String toString() {
        String serverMessages = "*** PREVIOUS MESSAGES ***";
        for (String message : messages){
            serverMessages += "\n" + message; // Puts the contents of the queue in a string to display the last 15 messages
        }
        return serverMessages;
    }

    /**
     * Adds a string to the queue, making sure that its content is not greater than the limit value
     * @param s String to add
     * @return true if the sting has been added, false otherwise
     */
    public boolean add(String s) {
        if (messages.size() >= MAX_NUMBER_OF_MESSAGES){
            messages.poll(); // Pop the first message before adding a new one if the queue has reach the max number of messages
        }
        return messages.add(s);
    }
}
