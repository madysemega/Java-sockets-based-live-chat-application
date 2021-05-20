package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class ClientsInformationManager  {
    private static Map<String,String> clientsInformations = new HashMap<>(); // This map will contain all users and their passwords

    // Static initialization to fill the map with the content of the .txt file once for all instances of the class.
    static {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader("ClientsInformation.txt"))); // Scanner to read the txt file content
            while (scanner.hasNextLine())
            {
                String nextLine = scanner.nextLine();
                if (nextLine != ""){
                    String[] data = nextLine.split(", "); // This separates the line into several parts delimited by the string identified as regex
                    String userName = data[0];
                    String passWord = data[1];
                    clientsInformations.put(userName,passWord);
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
    /**
     * Verifies if the user is registered
     * @param key username of current user
     * @return true if the user is registered, false otherwise
     */
    public boolean containsKey(Object key) {
        return clientsInformations.containsKey(key);
    }

    /**
     * Returns the password of a registered user
     * @param key The user's username
     * @return The password as a string
     */
    public String get(Object key) {
        return clientsInformations.get(key);
    }

    /**
     * Adds new user to database
     * @param key The user's username
     * @param value The user's password
     * @return the previous value associated with key,
     *         or null if there was no mapping for key
     */
    public String put(String key, String value) {
        return clientsInformations.put(key,value);
    }
}
