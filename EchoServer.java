package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * EchoServer.java
 * <p>
 * Our server for project 5.
 * Gets requests from clients and stores data.
 */

public class EchoServer implements Runnable {
    // Class Fields
    Socket socket;
    static ArrayList<User> userList = new ArrayList<User>();
    private static String dataBase = "dataBase.txt";

    // Class Constructor
    public EchoServer(Socket socket) {
        this.socket = socket;
    }

    // main()
    public static void main(String[] args) throws IOException {
        // Set up a server socket for clients
        ServerSocket serverSocket = new ServerSocket(4243);

        // Read data from dataBase.txt
        readData();

        // Allow multiple clients to connect
        while (true) {
            System.out.printf("Waiting for connection\n");
            Socket socket = serverSocket.accept();
            EchoServer server = new EchoServer(socket);
            new Thread(server).start();
        }
    }

    // run()
    public void run() {
        // Get Requests from Clients
        try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
            //readData();
            //System.out.println(userList);

            //my idea for refrershing is that we have a timer running
            // and in that time we set user list equal to some function that can be run
            // on the server. That method will return an updated userlist

            String taskIdentifier = in.nextLine();
            System.out.println(taskIdentifier);

            // User Login Request
            if (taskIdentifier.equalsIgnoreCase("loginAttempt")) {
                String userName = in.nextLine();
                String password = in.nextLine();

                for (User user : userList) {
                    if (user.getUsername().equals(userName)) {
                        if (user.getPassword().equals(password)) {
                            pw.println("Successful Login");
                            pw.flush();
                        }
                    }
                }
                pw.println("Wrong Login Info");
                pw.flush();
            }
            // User Sign Up Request
            if (taskIdentifier.equalsIgnoreCase("newAccountEnterAttempt")) {
                boolean userNameTaken = false;
                String userName = in.nextLine();
                String password = in.nextLine();
                for (User users : userList) {
                    if (userName.equals(users.getUsername())) {
                        userNameTaken = true;
                    }
                }
                if (!userNameTaken) {
                    userList.add(new User(userName, password));
                    updateData();
                    pw.print("newAccountEnterSuccess");
                    pw.flush();
                } else {
                    pw.print("usernameTaken");
                    pw.flush();
                }
            }
            // User sing up using a csv file
            if (taskIdentifier.equalsIgnoreCase("CSVAccount")) {
                boolean userNameTaken = false;
                boolean loginInfoIncomplete = false;
                String userName = in.nextLine();
                String password = in.nextLine();
                if (userName.equals("") || password.equals("")) {
                    loginInfoIncomplete = true;
                }
                String contact = in.nextLine();
                String interests = in.nextLine();
                String aboutMe = in.nextLine();
                String accountSecurity = in.nextLine();
                for (User users : userList) {
                    if (userName.equals(users.getUsername())) {
                        userNameTaken = true;
                    }
                }
                if (!userNameTaken && !loginInfoIncomplete) {
                    userList.add(new User(userName, password, contact, interests, aboutMe, accountSecurity));
                    updateData();
                    pw.print("newAccountCSVSuccess");
                    pw.flush();
                } else if (userNameTaken) {
                    pw.print("usernameTaken");
                    pw.flush();
                } else {
                    pw.println("infoIncomplete");
                    pw.flush();
                }
            }
            // Load Profile Request
            if (taskIdentifier.equalsIgnoreCase("loadProfileAttempt")) {
                String userName = in.nextLine();
                for (User user : userList) {
                    if (user.getUsername().equals(userName)) {
                        pw.println(user.getUsername());
                        pw.flush();
                        pw.println(user.getContact());
                        pw.flush();
                        pw.println(user.getInterests());
                        pw.flush();
                        pw.println(user.getAboutMe());
                        pw.flush();
                    }
                }
            }
            // Get Friends Request
            if (taskIdentifier.equalsIgnoreCase("getFriendsList")) {
                String userName = in.nextLine();
                for (User user : userList) {
                    if (user.getUsername().equals(userName)) {
                        if (user.getFriends() != null) {
                            pw.println(user.getFriends().size());
                            pw.flush();
                            for (User friend : user.getFriends()) {
                                pw.println(friend.getUsername());
                                pw.flush();
                            }
                        } else {
                            pw.println("null");
                            pw.flush();
                        }
                    }
                }
            }
            // Request A Friend
            if (taskIdentifier.equalsIgnoreCase("requestedFriend")) {
                String loggedIn = in.nextLine();
                String friend = in.nextLine();
                for (User user : userList) {
                    if (user.getUsername().equals(loggedIn)) {
                        for (User theFriend : userList) {
                            if (theFriend.getUsername().equals(friend)) {
                                user.sendRequest(theFriend);
                            }
                        }
                    }
                }
                updateData();
            }
            // Add Friend Request
            if (taskIdentifier.equalsIgnoreCase("addFriend")) {
                String loggedInUser = in.nextLine();
                String friendUser = in.nextLine();

                for (User user : userList) {
                    if (user.getUsername().equals(friendUser)) {
                        for (User logged : userList) {
                            if (logged.getUsername().equals(loggedInUser)) {
                                logged.addFriend(user);
                                user.removeSentRequest(logged);
                            }
                        }
                    }
                }
                updateData();
            }
            // Remove Friend Request
            if (taskIdentifier.equalsIgnoreCase("removeFriend")) {
                String loggedInUser = in.nextLine();
                String currentUser = in.nextLine();

                for (User user : userList) {
                    if (user.getUsername().equals(currentUser)) {
                        for (User logged : userList) {
                            if (logged.getUsername().equals(loggedInUser)) {
                                logged.removeFriend(user);
                            }
                        }
                    }
                }
                updateData();
                pw.println("Successful Remove");
                pw.flush();
            }
            // Get Users Request
            if (taskIdentifier.equalsIgnoreCase("getAllUsers")) {
                pw.println(userList.size());
                pw.flush();
                for (User user : userList) {
                    pw.println(user.toString());
                    pw.flush();
                }

            }
            // Send the List of Sent and Received Requests
            if (taskIdentifier.equalsIgnoreCase("sentAndReceivedRequests")) {
                pw.println(userList.size());
                pw.flush();

                for (User user : userList) {
                    pw.println(user.toStringReceivedRequests());
                    pw.flush();
                    pw.println(user.toStringSentRequests());
                    pw.flush();
                }
            }
            // Change Settings Request
            if (taskIdentifier.equalsIgnoreCase("changeSettings")) {
                String loggedInUser = in.nextLine();
                String newUserName = in.nextLine();
                String newPassword = in.nextLine();
                String newProfileType = in.nextLine();
                String newContact = in.nextLine();
                String newInterests = in.nextLine();
                String newAboutMe = in.nextLine();
                boolean usernameTaken = false;


                for (User user : userList) {
                    if (user.getUsername().equals(loggedInUser)) {
                        for (User userNew : userList) {
                            if (newUserName.equals(userNew.getUsername()) && !userNew.equals(user)) {
                                usernameTaken = true;
                            }
                        }
                        if (!usernameTaken) {
                            user.setUsername(newUserName);
                            user.setPassword(newPassword);
                            user.setAccountSecurity(newProfileType);
                            user.setContact(newContact);
                            user.setInterests(newInterests);
                            user.setAboutMe(newAboutMe);
                            updateData();
                            pw.println("Finished with settings updates");
                            pw.flush();
                        } else {
                            pw.println("usernameTaken");
                            pw.flush();
                        }
                    }
                }
            }

            if (taskIdentifier.equalsIgnoreCase("exportProfile")) {
                String username = in.nextLine();
                for (User user : userList) {
                    if (user.getUsername().equals(username)) {
                        pw.println(user.getUsername());
                        pw.flush();
                        pw.println(user.getPassword());
                        pw.flush();
                        pw.println(user.getContact());
                        pw.flush();
                        pw.println(user.getInterests());
                        pw.flush();
                        pw.println(user.getAboutMe());
                        pw.flush();
                        pw.println(user.getAccountSecurity());
                        pw.flush();
                        pw.println(user.getFriends());
                        pw.flush();
                        pw.println(user.getSentRequests());
                        pw.flush();
                        pw.println(user.getReceivedRequests());
                        pw.flush();
                    }
                }
            }

            if (taskIdentifier.equalsIgnoreCase("deleteProfile")) {
                String username = in.nextLine();


                updateData();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void updateData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dataBase))) {
            for (User user : userList) {
                bw.write("Username: " + user.getUsername() + "\n");
                bw.write("Password: " + user.getPassword() + "\n");
                bw.write("Contact: " + user.getContact() + "\n");
                bw.write("Interests: " + user.getInterests() + "\n");
                bw.write("About Me: " + user.getAboutMe() + "\n");
                bw.write("Account Security: " + user.getAccountSecurity() + "\n");
                bw.write("Friends: " + user.printFriends(user.getFriends()) + "\n");
                bw.write("Sent Requests: " + user.printFriends(user.getSentRequests()) + "\n");
                bw.write("Received Requests: " + user.printFriends(user.getReceivedRequests()) + "\n");
                bw.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void readData() {
        userList = new ArrayList<User>();
        String[] currentFriends = null;
        String[] receivedRequests = null;
        String[] sentRequests = null;
        try (BufferedReader br = new BufferedReader(new FileReader(dataBase))) {
            String line;
            ArrayList<String> userInfo = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                userInfo = new ArrayList<>();
                for (int lineNum = 1; lineNum <= 9; lineNum++) {
                    try {
                        userInfo.add(line.split(": ")[1]);
                    } catch (IndexOutOfBoundsException e) {
                        userInfo.add(null);
                    }
                    line = br.readLine();
                }
                userList.add(new User(userInfo.get(0), userInfo.get(1), userInfo.get(2),
                        userInfo.get(3), userInfo.get(4), userInfo.get(5),
                        null, null, null));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(dataBase))) {
            String line;
            int userNumber = 0;
            User currentUser;
            while ((line = br.readLine()) != null) {
                for (int lineNum = 1; lineNum <= 9; lineNum++) {
                    if (lineNum == 7) {
                        try {
                            currentFriends = line.split(": ")[1].split(", ");
                            if (currentFriends[0].equals("null")) {
                                currentFriends = null;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            currentFriends = null;
                        }
                    } else if (lineNum == 8) {
                        try {
                            sentRequests = line.split(": ")[1].split(", ");
                            if (sentRequests[0].equals("null")) {
                                sentRequests = null;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            sentRequests = null;
                        }
                    } else if (lineNum == 9) {
                        try {
                            receivedRequests = line.split(": ")[1].split(", ");
                            if (receivedRequests[0].equals("null")) {
                                receivedRequests = null;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            receivedRequests = null;
                        }
                    }
                    line = br.readLine();
                }
                currentUser = userList.get(userNumber++);
                if (currentFriends != null) {
                    currentUser.setFriends(findFriends(currentFriends));
                }
                if (receivedRequests != null) {
                    currentUser.setReceivedRequests(findFriends(receivedRequests));
                }
                if (sentRequests != null) {
                    currentUser.setSentRequests(findFriends(sentRequests));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(userList);
    }

    public static ArrayList<User> findFriends(String[] friendNames) {
        ArrayList<User> friendList = new ArrayList<>();
        for (String friend : friendNames) {
            for (User users : userList) {
                if (friend.equals(users.getUsername())) {
                    friendList.add(users);
                }
            }
        }
        return friendList;
    }

}