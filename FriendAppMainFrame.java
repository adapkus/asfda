package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * FriendAppMainFrame.java
 * <p>
 * This class creates the main window for project 5. It displays
 * the app's custom logo, a user search bar and combo box, a
 * friends list, and profile info.
 * <p>
 * I learned about setting custom borders, as well as layout
 * manager and JTextArea tips from a youtuber named "Bro Code"
 * <p>
 * I learned about the KeyAdapter event from a youtuber with the
 * channel name "TheEpicfunnystuff"
 * <p>
 * I learned about the ListSelectionListener event from a
 * youtuber named "Lazic B."
 *
 * @author Vincent Dubyna
 * @version 2021-4-21
 */

public class FriendAppMainFrame implements Runnable {
    // Class Fields
    String[] friends; // String array of friends
    ArrayList<String> friendRequests; //all the requests that need to be approved

    ArrayList<User> allUsers;
    String[] users; // String array of users

    JFrame frame; // the main JFrame for the app

    Container content; // the contentPane of the frame
    JPanel topPanel; // the top panel of the window
    JPanel westPanel; // the west panel of the window
    JPanel middlePanel; // the center panel of the window
    JPanel eastPanel; // the east panel of the window

    JTextField userSearch; // the user search bar
    JComboBox<String> userList; // the object for displaying the users
    JList<String> friendList; // the list of friends
    JScrollPane scroll; // the scrollPane for the friends list
    ImageIcon profilePic; // the profile picture
    JLabel profPicLabel; // the JLabel for the profile picture
    JButton settingsButton; // the settings button
    JLabel profileName; // the profile name
    JTextArea profileInfo; // the profile info
    JButton friendRequestsButton; // the incoming friend requests
    String loggedInUser;   // the name of the user that is logged in
    String currentUser;    // the name of the user that should be displayed

    JButton addFriend; // JButton for adding friends
    JButton removeFriend; // JButton for removing friends
    JButton home; // JButton for going home
    JButton deleteButton;
    JButton exportProfile;


    String name;
    String password;
    String contact;
    String interests;
    String aboutMe;

    // main()
    public static void main(String[] args) throws IOException {
        // Use a runnable object to perform all of the GUI execution
        SwingUtilities.invokeLater(new FriendAppMainFrame());
    }

    // run()
    public void run() {
        // Load all users
        try {
            allUsers = getAllUsers();
            addSentAndReceivedRequests();

        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean loginSuccess = false;
        boolean loginCancelled = false;
        int newAccount;
        int newAccountCreationType;

        // Ask the user to log in or sign up
        while (!loginSuccess) {
            try {
                newAccount = setUpInitialScreen();
                if (newAccount == 1) {
                    try {
                        loginSuccess = setUpLoginScreen();
                    } catch (IOException e) {
                        loginCancelled = true;
                    }
                } else if (newAccount == 2) {
                    newAccountCreationType = setUpNewAccountInitial();
                    if (newAccountCreationType == 1) {
                        if (setUpNewAccountEnter()) {
                            JOptionPane.showMessageDialog(null, "New User Successfully Created", "User Creation", JOptionPane.INFORMATION_MESSAGE);
                            loginSuccess = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "New User Creation Failed", "User Creation", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (newAccountCreationType == 2) {
                        //read in csv file
                        if (setUpNewAccountCSV()) {
                            JOptionPane.showMessageDialog(null, "New User Successfully Created", "User Creation", JOptionPane.INFORMATION_MESSAGE);
                            loginSuccess = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "New User Creation Failed", "User Creation", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        throw new Exception();
                    }
                } else {
                    throw new IOException();
                }
            } catch (Exception e) {
                loginCancelled = true;
            }

            //Asks user to approve or deny requests
            if (loginSuccess) {
              /*  for (User user : allUsers) {
                    if (user.getUsername().equals(loggedInUser)) {
                        if(user.getReceivedRequests().size() > 0) {
                            for (int x = 0; x < user.getReceivedRequests().size(); x++) {
                                String request = user.getReceivedRequests().get(x).getUsername();
                                int option = JOptionPane.showConfirmDialog(null,
                                        "Would you Like to Accept" + request +"'s Friend Request",
                                        "Friend Request", JOptionPane.YES_NO_OPTION);
                                if(option == JOptionPane.YES_OPTION) {
                                    user.addFriend(user.getReceivedRequests().get(x));
                                }
                            }
                        }
                    }
                }*/


                // Open the main window
                // Window Frame
                frame = new JFrame("MeBook");

                // Set the frame's size, position, and close operation
                frame.setSize(900, 600);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // App container
                content = frame.getContentPane();
                content.setLayout(new BorderLayout());
                try {
                    // Create the top panel of the window
                    setUpTopPanel();

                    // Create the west panel of the window
                    setUpWestPanel();

                    // Create the center panel of the window
                    setUpCenterPanel();

                    // Create the east panel of the window
                    setUpEastPanel();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Pack the frame, allow it to be resized, and set it visible
                frame.setMinimumSize(new Dimension(900, 670)); // no smaller than 900 x 618
                frame.pack();
                frame.setResizable(true);
                frame.setVisible(true);
            } else if (loginCancelled) {
                loginSuccess = true; //stops loop
            } else {
                JOptionPane.showMessageDialog(null, "Login Failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private boolean setUpNewAccountCSV() throws IOException {
        JTextField csv = new JTextField();
        Object[] message = {"CSV path name:", csv};
        int option = JOptionPane.showConfirmDialog(null, message, "Create New Account", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Socket socket = new Socket("localhost", 4242);
            try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
                BufferedReader br = new BufferedReader(new FileReader(csv.getText()));
                String info = br.readLine();
                String newUsername = info.split(";")[0];
                String newPassword = info.split(";")[1];
                String newContact = info.split(";")[2];
                String newInterests = info.split(";")[3];
                String newAboutMe = info.split(";")[4];
                String newAccountSecurity = info.split(";")[5];

                System.out.printf("%s, %s, %s, %s, %s, %s", newUsername, newPassword, newContact, newInterests, newAboutMe, newAccountSecurity);

                pw.println("CSVAccount");
                pw.flush();
                pw.println(newUsername);
                pw.flush();
                pw.println(newPassword);
                pw.flush();
                pw.println(newContact);
                pw.flush();
                pw.println(newInterests);
                pw.flush();
                pw.println(newAboutMe);
                pw.flush();
                pw.println(newAccountSecurity);
                pw.flush();
                String line = in.nextLine();
                System.out.println(line);
                if (line.equalsIgnoreCase("newAccountCSVSuccess")) {
                    currentUser = newUsername;
                    loggedInUser = newUsername;
                    socket.close();
                    return true;
                } else if (line.equalsIgnoreCase("usernameTaken")) {
                    JOptionPane.showMessageDialog(null, "Username Already Taken", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    JOptionPane.showMessageDialog(null, "CVS File does not have Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private boolean setUpNewAccountEnter() throws IOException {
        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        Object[] message = {
                "Username:", username,
                "Password:", password
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Create New Account", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Socket socket = new Socket("localhost", 4242);
            try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
                pw.println("newAccountEnterAttempt");
                pw.flush();
                pw.println(username.getText());
                pw.flush();
                pw.println(password.getText());
                pw.flush();
                String line = in.nextLine();
                if (line.equalsIgnoreCase("newAccountEnterSuccess")) {
                    currentUser = username.getText();
                    loggedInUser = username.getText();
                    this.password = password.getText();
                    socket.close();
                    return true;
                } else if (line.equalsIgnoreCase("usernameTaken")) {
                    JOptionPane.showMessageDialog(null, "Username Already Taken", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private int setUpNewAccountInitial() {
        String[] selections = {"Enter User Info?", "Upload CSV File"};
        int option = JOptionPane.showOptionDialog(null,
                "Select an option",
                "New Account Creation Type Selection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                selections,  //the titles of buttons
                selections[0]); //default button title

        if (option == JOptionPane.YES_OPTION) {
            return 1;
        } else if (option == JOptionPane.NO_OPTION) {
            return 2;
        } else {
            return 0;
        }
    }

    private int setUpInitialScreen() {
        String[] selections = {"Login", "Sign-Up"};
        int option = JOptionPane.showOptionDialog(null,
                "Select an option",
                "Login/Sign-Up",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                selections,  //the titles of buttons
                selections[0]); //default button title

        if (option == JOptionPane.YES_OPTION) {
            return 1;
        } else if (option == JOptionPane.NO_OPTION) {
            return 2;
        } else {
            return 0;
        }
    }

    private boolean setUpLoginScreen() throws IOException {
        JTextField username = new JTextField();
        JTextField password = new JPasswordField();
        Object[] message = {
                "Username:", username,
                "Password:", password
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Socket socket = new Socket("localhost", 4242);
            try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
                pw.println("loginAttempt");
                pw.flush();
                pw.println(username.getText());
                pw.flush();
                pw.println(password.getText());
                pw.flush();
                String line = in.nextLine();
                if (line.equalsIgnoreCase("Successful Login")) {
                    System.out.println("Login successful");
                    currentUser = username.getText();
                    loggedInUser = username.getText();
                    this.password = password.getText();
                    socket.close();
                    return true;
                } else {
                    System.out.println("login failed");
                    socket.close();
                    return false;
                }
            }
        } else {
            System.out.println("Login canceled");
            throw new IOException();
        }
    }

    private void setUpTopPanel() {
        // JPanel for the app logo and user search bar
        topPanel = new JPanel(new GridLayout(0, 2));
        topPanel.setBackground(new Color(26, 110, 163));
        topPanel.setPreferredSize(new Dimension(900, 95));
        content.add(topPanel, BorderLayout.NORTH);

        // Extra JPanel for the user search bar and combo box
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        searchPanel.setBackground(new Color(26, 110, 163));

        // ImageIcon and JLabel for the logo
        ImageIcon logo = new ImageIcon("meBook2.png");
        JLabel logoLabel = new JLabel(logo);

        // JTextField for the user search bar
        userSearch = new JTextField("User Search...");
        userSearch.setPreferredSize(new Dimension(250, 45));
        userSearch.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));

        // JComboBox for the list of users
        users = new String[allUsers.size()];
        for (int x = 0; x < allUsers.size(); x++) {
            users[x] = allUsers.get(x).getUsername();
        }

        userList = new JComboBox<String>(users);
        userList.setPreferredSize(new Dimension(125, 35));
        userList.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));

        // JLabel to take up unwanted space
        JLabel spaceLabel = new JLabel("");
        spaceLabel.setPreferredSize(new Dimension(400, 20));

        topPanel.add(logoLabel);
        topPanel.add(searchPanel);
        searchPanel.add(spaceLabel);
        searchPanel.add(userSearch);
        searchPanel.add(userList);

        /*
         *  ActionListener for the settings button and the user combo box
         */
        ActionListener userSelect = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                friendList.clearSelection();
                if (e.getSource() == userList) {
                    // get the username of the user at the selected index
                    String username = userList.getItemAt(userList.getSelectedIndex());
                    currentUser = username;

                    // Strings for user info
                    String name = null;
                    String contact = null;
                    String interests = null;
                    String aboutMe = null;

                    try {
                        Socket socket = new Socket("localhost", 4242);

                        try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
                            // Load the user's profile
                            pw.println("loadProfileAttempt");
                            pw.flush();
                            pw.println(currentUser);
                            System.out.println(currentUser);
                            pw.flush();

                            // Get the user's info
                            name = in.nextLine();
                            contact = in.nextLine();
                            interests = in.nextLine();
                            aboutMe = in.nextLine();
                        }

                        socket.close();
                    } catch (IOException error) {
                        error.printStackTrace();
                    }

                    // Update middlePanel and the buttons
                    profileName.setText("   " + name);
                    profileInfo.setText(String.format
                            ("       Contact: %s\n       Interests: %s\n       About Me: %s\n",
                                    contact, interests, aboutMe));

                    // If the currentUser isn't equal to the loggedInUser,
                    // disable the settings button
                    if (currentUser.equals(loggedInUser) == false) {
                        settingsButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                        friendRequestsButton.setEnabled(false);
                        addFriend.setEnabled(true);
                        removeFriend.setEnabled(true);
                        home.setEnabled(true);
                        // Otherwise enable only the settings button
                    } else {
                        settingsButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                        friendRequestsButton.setEnabled(true);
                        addFriend.setEnabled(false);
                        removeFriend.setEnabled(false);
                        home.setEnabled(false);
                    }

                    // Update the middlePanel
                    middlePanel.updateUI();
                }
            }
        };

        // Add the ActionListener to the user combo box

        userList.addActionListener(userSelect);

        /*
         *  KeyListener for the user search bar
         *
         *  Code borrowed from TheEpicfunnystuff on YouTube
         */
        KeyAdapter searchUser = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Check to see if the user pressed a character for the first time
                String searchText = userSearch.getText();
                String previousText = "";
                if (searchText.length() > 1) {
                    previousText = searchText.substring(0, searchText.length() - 1);
                }
                if (previousText.equals("User Search...")) {
                    // Remove the String "User Search..." from the JTextField
                    userSearch.setText(searchText.substring(searchText.length() - 1));
                }
                // Check to see if the user pressed enter
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    JOptionPane.showMessageDialog(null, "You searched for " + userSearch.getText() + "!",
                            "MeBook", JOptionPane.INFORMATION_MESSAGE);
                    // Update the JComboBox to show only the usernames that match the search
                    userList.removeActionListener(userSelect);
                    userList.removeAllItems();
                    for (String username : users) {
                        if (username.contains(userSearch.getText())) {
                            userList.addItem(username);
                        }
                    }
                    userList.addActionListener(userSelect);
                    searchPanel.updateUI();
                }
                // Check to see if the user reset the text field to empty
                if (userSearch.getText().equals("")) {
                    // Update the JTextField with the String "User Search..."
                    userSearch.setText("User Search...");

                    // Restore all usernames to the JComboBox
                    userList.removeActionListener(userSelect);
                    userList.removeAllItems();
                    for (String username : users) {
                        userList.addItem(username);
                    }
                    userList.addActionListener(userSelect);
                    searchPanel.updateUI();
                }
            }
        };

        // Add the KeyListener to the user search bar
        userSearch.addKeyListener(searchUser);
    }

    private void setUpWestPanel() {
        // JPanel for the friends List
        westPanel = new JPanel();
        westPanel.setBackground(Color.white);
        westPanel.setPreferredSize(new Dimension(125, 505));
        westPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
        content.add(westPanel, BorderLayout.WEST);

        // JTextField for the friends list label
        JTextField friendsLabel = new JTextField("        Friends List        ");
        friendsLabel.setBackground(new Color(200, 200, 200));
        friendsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        friendsLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 3));
        friendsLabel.setEditable(false);

        // JList for the friends list

        try {
            friends = getFriends(loggedInUser);
        } catch (IOException e) {
            e.printStackTrace();
        }

        friendList = new JList<String>(friends);

        // JScrollPane for the friends list
        scroll = new JScrollPane(friendList);
        scroll.setPreferredSize(new Dimension(123, 450));
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

        westPanel.add(friendsLabel);
        westPanel.add(scroll);

        /*
         * ListSelectionListener for the friends list
         *
         * Code borrowed from Lazic B.
         */

        friendList.getSelectionModel().addListSelectionListener(e -> {
            // Get the selected friend
            String friendName = friendList.getSelectedValue();
            currentUser = friendName;

            //Get information about the friend from the server
            String name = null;
            String contact = null;
            String interests = null;
            String aboutMe = null;

            try {
                Socket socket = new Socket("localhost", 4242);

                try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
                    // Request the friend's info
                    pw.println("loadProfileAttempt");
                    pw.flush();
                    pw.println(currentUser);
                    System.out.println(currentUser);
                    pw.flush();

                    // Get the friend's info
                    name = in.nextLine();
                    contact = in.nextLine();
                    interests = in.nextLine();
                    aboutMe = in.nextLine();
                }

                socket.close();
            } catch (IOException error) {
                error.printStackTrace();
            } catch (NoSuchElementException noSuchElementException) {
                //Do nothing
            }

            // Update middlePanel and the buttons
            profileName.setText("   " + name);
            profileInfo.setText(String.format
                    ("       Contact: %s\n       Interests: %s\n       About Me: %s\n",
                            contact, interests, aboutMe));

            // Update the middlePanel
            middlePanel.updateUI();

            // Update Buttons
            settingsButton.setEnabled(false);
            deleteButton.setEnabled(false);
            friendRequestsButton.setEnabled(false);
            addFriend.setEnabled(true);
            removeFriend.setEnabled(true);
            home.setEnabled(true);
            middlePanel.updateUI();
            System.out.println("Running");
        });


    }

    private void showProfileInfo(String friendName) throws IOException {
        Socket socket = new Socket("localhost", 4242);
        try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
            pw.println("loadProfileAttempt");
            pw.flush();
            pw.println(friendName);
            pw.flush();
            String userName = in.nextLine();
            String contact = in.nextLine();
            String interests = in.nextLine();
            String aboutMe = in.nextLine();
            socket.close();

            Object[] message = {
                    "Username: " + userName,
                    "Contact: " + contact,
                    "Interests: " + interests,
                    "About Me: " + aboutMe
            };
            String[] selections = {"Ok", "Remove Friend"};
            int option = JOptionPane.showOptionDialog(null, message, "Friend Display",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, selections, selections[0]);
        }
    }

    private String[] getFriends(String userName) throws IOException {
        ArrayList<String> friendsList = new ArrayList<String>();

        Socket socket = new Socket("localhost", 4242);
        try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
            pw.println("getFriendsList");
            pw.flush();
            pw.println(userName);
            pw.flush();

            int numFriends = Integer.parseInt(in.nextLine());
            for (int i = 1; i <= numFriends; i++) {
                friendsList.add(in.nextLine());
            }
        }

        socket.close();

        //shitty conversion to strings
        Object[] friends = friendsList.toArray();
        String[] friendsString = new String[friends.length];
        int counter = 0;
        for (Object value : friends) {
            friendsString[counter++] = value.toString();
        }
        return friendsString;
    }

    private ArrayList<User> getAllUsers() throws IOException {
        ArrayList<User> allUsers = new ArrayList<User>();
        Socket socket = new Socket("localhost", 4242);

        try (PrintWriter pw = new PrintWriter(socket.getOutputStream());
             Scanner in = new Scanner(socket.getInputStream())) {
            pw.println("getAllUsers");
            pw.flush();

            ArrayList<String> rawUserData = new ArrayList<String>();
//            ArrayList<String> rawFriends = null;
//            ArrayList<String> rawSentRequests = null;
//            ArrayList<String> rawRecievedRequests = null;
//            String line = null;

            int numUsers = Integer.parseInt(in.nextLine());
            for (int x = 0; x < numUsers; x++) {
                rawUserData.add(in.nextLine());
//                line = in.nextLine();
//                if (!line.equals("null")) {
//
//                }
//                line = in.nextLine();
//                if (!line.equals("null")) {
//
//                }
//                line = in.nextLine();
//                if (!line.equals("null")) {
//
//                }
            }

            socket.close();

            for (int y = 0; y < rawUserData.size(); y++) {
                Object[] individualData = rawUserData.get(y).split(";");
                User toAdd = new User(String.valueOf(individualData[0]), String.valueOf(individualData[1]),
                        String.valueOf(individualData[2]), String.valueOf(individualData[3]),
                        String.valueOf(individualData[4]), String.valueOf(individualData[5]));
                allUsers.add(toAdd);
            }
        }

        return allUsers;
    }

    private void addSentAndReceivedRequests() throws IOException {
        Socket socket = new Socket("localhost", 4242);

        try (PrintWriter pw = new PrintWriter(socket.getOutputStream());
             Scanner in = new Scanner(socket.getInputStream())) {
            pw.println("sentAndReceivedRequests");
            pw.flush();

            int numUsers = Integer.parseInt(in.nextLine()); //number of users total

            String[] receivedReq;
            String[] sentReq;

            for (int x = 0; x < numUsers; x++) {
                receivedReq = in.nextLine().split(",,");
                sentReq = in.nextLine().split(",,");
                ArrayList<User> recReqList = new ArrayList<User>();
                ArrayList<User> sentReqList = new ArrayList<User>();

                for (int y = 1; y < receivedReq.length; y++) {
                    String[] userToAdd = receivedReq[y].split(";");
                    User toAdd = new User(userToAdd[0], userToAdd[1], userToAdd[2], userToAdd[3], userToAdd[4],
                            userToAdd[5]);
                    recReqList.add(toAdd);
                }

                for (int z = 1; z < sentReq.length; z++) {
                    String[] userToAdd = sentReq[z].split(";");
                    User toAdd = new User(userToAdd[0], userToAdd[1], userToAdd[2], userToAdd[3], userToAdd[4],
                            userToAdd[5]);
                    sentReqList.add(toAdd);
                }

                for (User user : allUsers) {
                    if (user.getUsername().equals(receivedReq[0])) {
                        user.setReceivedRequests(recReqList);
                        user.setSentRequests(sentReqList);
                    }
                }

            }

        }
    }

    private void setUpCenterPanel() throws IOException {
        // JPanel for the profile name and info
        middlePanel = new JPanel(new GridLayout(2, 0));
        middlePanel.setBackground(new Color(240, 240, 200));
        middlePanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
        middlePanel.setPreferredSize(new Dimension(530, 505));
        content.add(middlePanel, BorderLayout.CENTER);

        //Make the profile legitimately update
        Socket socket = new Socket("localhost", 4242);
        try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
            // Here i should tell server what I plan on doing
            // then i need to send it the currentUser name identification
            // the server should then send me the information i need including profile information
            // update the below to show the information regarding the user.

            pw.println("loadProfileAttempt");
            pw.flush();
            pw.println(currentUser);
            System.out.println(currentUser);
            pw.flush();

            name = in.nextLine();
            contact = in.nextLine();
            interests = in.nextLine();
            aboutMe = in.nextLine();
        }

        socket.close();

        // JLabel for the profile name
        profileName = new JLabel("   " + name);
        profileName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 45));

        // String Format and JTextArea for the profile info
        String infoFormat = String.format
                ("       Contact: %s\n       Interests: %s\n       About Me: %s\n",
                        contact, interests, aboutMe);

        System.out.println("Center panel is updated");
        profileInfo = new JTextArea(infoFormat);
        profileInfo.setBackground(new Color(240, 240, 200));
        profileInfo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        profileInfo.setLineWrap(true);
        profileInfo.setWrapStyleWord(true);

        middlePanel.add(profileName);
        middlePanel.add(profileInfo);
    }

    private void setUpEastPanel() {
        // JPanel for the profile pic and settings button
        eastPanel = new JPanel(new FlowLayout());
        eastPanel.setBackground(new Color(240, 240, 200));
        eastPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
        eastPanel.setPreferredSize(new Dimension(245, 505));
        content.add(eastPanel, BorderLayout.EAST);

        // JLabel for the profile pic
        profilePic = new ImageIcon("default.png");
        profPicLabel = new JLabel(profilePic);
        profPicLabel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

        // ImageIcon and JButton for the settings button
        ImageIcon gear = new ImageIcon("gear.png");
        settingsButton = new JButton(gear);

        addFriend = new JButton("Add Friend");
        addFriend.setPreferredSize(new Dimension(233, 52));
        addFriend.setFont(new Font(Font.SERIF, Font.PLAIN, 25));

        removeFriend = new JButton("Remove Friend");
        removeFriend.setPreferredSize(new Dimension(233, 52));
        removeFriend.setFont(new Font(Font.SERIF, Font.PLAIN, 25));

        home = new JButton("Home");
        home.setPreferredSize(new Dimension(233, 52));
        home.setFont(new Font(Font.SERIF, Font.PLAIN, 25));

        friendRequestsButton = new JButton("Friend Requests");
        friendRequestsButton.setPreferredSize(new Dimension(233, 52));
        friendRequestsButton.setFont(new Font(Font.SERIF, Font.PLAIN, 25));

        deleteButton = new JButton("Delete Account");
        deleteButton.setPreferredSize(new Dimension(233, 52));
        deleteButton.setFont(new Font(Font.SERIF, Font.PLAIN, 25));

        exportProfile = new JButton("Export Profile");
        exportProfile.setPreferredSize(new Dimension(233, 52));
        exportProfile.setFont(new Font(Font.SERIF, Font.PLAIN, 25));


        eastPanel.add(profPicLabel);
        eastPanel.add(settingsButton);
        eastPanel.add(friendRequestsButton);
        eastPanel.add(addFriend);
        eastPanel.add(removeFriend);
        eastPanel.add(home);
        eastPanel.add(deleteButton);
        eastPanel.add(exportProfile);

        addFriend.setEnabled(false);
        removeFriend.setEnabled(false);
        home.setEnabled(false);

        /*
         *  ActionListener for the settings button
         */
        ActionListener buttonClick = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == settingsButton) {
                    //TODO
                    frame.setVisible(false);
                    SettingsMenu menu = new SettingsMenu(name, password,
                            contact, interests, aboutMe, profilePic);
                    menu.run();
                }
                if (e.getSource() == addFriend) {
                    // Check if the username is in the friends list
                    boolean isFriend = false;
                    for (String friend : friends) {
                        if (friend.equals(currentUser)) {
                            isFriend = true;
                        }
                    }

                    // Ask the user if they want to add the user account to their friends list
                    int choice = JOptionPane.showConfirmDialog(null, "Would you like to friend " + currentUser + "?",
                            "MeBook", JOptionPane.YES_NO_OPTION);

                    // If they want to add the user account, and the username isn't already
                    // in their friends list, add the username to the friends list
                    if (choice == JOptionPane.YES_OPTION) {
                        if (isFriend == false) {

                            for (String user : users) {
                                if (user.equals(loggedInUser)) {
                                    for (String user2 : users) {
                                        if (user2.equals(currentUser)) {
                                            JOptionPane.showMessageDialog(null, "Your request to " +
                                                    user2 + " has been sent");

                                        }

                                    }
                                }
                            }
                            /*String[] newFriends = new String[friends.length + 1];
                            for (int i = 0; i < friends.length; i++) {
                                newFriends[i] = friends[i];
                            }
                            newFriends[newFriends.length - 1] = currentUser;
                            friends = newFriends;
                            friendList.setListData(friends);*/

                            try {
                                Socket socket = new Socket("localhost", 4242);
                                try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
                                    pw.println("requestedFriend");
                                    pw.flush();
                                    pw.println(loggedInUser);
                                    pw.flush();
                                    pw.println(currentUser);
                                    pw.flush();
                                }
                            } catch (IOException error) {
                                error.printStackTrace();
                            }


                        } else {
                            JOptionPane.showMessageDialog(null, currentUser + " is already in your friends list!",
                                    "MeBook", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                if (e.getSource() == removeFriend) {
                    //JOptionPane.showMessageDialog(null, "You clicked the remove friend button!",
                    //        "MeBook", JOptionPane.INFORMATION_MESSAGE);
                    try {
                        removeFriendMethod(loggedInUser, currentUser);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    currentUser = loggedInUser;

                    homeButton();
                }
                if (e.getSource() == home) {
                    homeButton();
                }
                if (e.getSource() == friendRequestsButton) {
                    frame.setVisible(false);
                    friendRequestMenu menu = new friendRequestMenu();
                    menu.run();
                }
                if (e.getSource() == deleteButton) {
                    //TODO
                    try {
                        Socket socket = new Socket("localhost", 4242);

                        try (PrintWriter pw = new PrintWriter(socket.getOutputStream());
                             Scanner in = new Scanner(socket.getInputStream())) {
                            pw.println("deleteProfile");
                            pw.flush();
                            pw.println(loggedInUser);
                            pw.flush();

                        }
                        socket.close();

                    } catch (IOException error) {

                    }
                }
                if (e.getSource() == exportProfile) {
                    JTextField fileName = new JTextField();
                    Object[] message = {
                            "Enter name of file to export " + currentUser + " data into.", fileName,
                    };

                    int select = JOptionPane.showConfirmDialog(null, message,
                            "MeBook", JOptionPane.OK_CANCEL_OPTION);
                    if (select == JOptionPane.OK_OPTION) {
                        try {
                            Socket socket = new Socket("localhost", 4242);

                            try (PrintWriter pw = new PrintWriter(socket.getOutputStream());
                                 PrintWriter filePw = new PrintWriter(new FileWriter(fileName.getText()));
                                 Scanner in = new Scanner(socket.getInputStream())) {
                                pw.println("exportProfile");
                                pw.flush();
                                pw.println(currentUser);
                                pw.flush();
                                filePw.println(String.format(
                                        "Username: " + in.nextLine() + "\n" +
                                                "Password: " + in.nextLine() + "\n" +
                                                "Contact: " + in.nextLine() + "\n" +
                                                "Interests: " + in.nextLine() + "\n" +
                                                "About Me: " + in.nextLine() + "\n" +
                                                "Account Security: " + in.nextLine() + "\n" +
                                                "Friends: " + in.nextLine() + "\n" +
                                                "Sent Requests: " + in.nextLine() + "\n" +
                                                "Received Requests: " + in.nextLine() + "\n"));
                                filePw.flush();
                            }
                            socket.close();

                        } catch (IOException error) {

                        }
                    }

                }
            }
        };

        // Add the ActionListener to the settings button
        settingsButton.addActionListener(buttonClick);
        addFriend.addActionListener(buttonClick);
        removeFriend.addActionListener(buttonClick);
        home.addActionListener(buttonClick);
        friendRequestsButton.addActionListener(buttonClick);
        deleteButton.addActionListener(buttonClick);
        exportProfile.addActionListener(buttonClick);

    }

    public void removeFriendMethod(String loggedInUser, String currentUser) throws IOException {
        Socket socket = new Socket("localhost", 4242);
        try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
            pw.println("removeFriend");
            pw.flush();
            pw.println(loggedInUser);
            pw.flush();
            pw.println(currentUser);
            pw.flush();
            if (in.nextLine().equals("Successful Remove")) {
                friendList.setListData(getFriends(loggedInUser));
            }
        }
        socket.close();
    }

    public void homeButton() {
        friendList.clearSelection();

        String name = null;
        String contact = null;
        String interests = null;
        String aboutMe = null;

        try {
            Socket socket = new Socket("localhost", 4242);

            try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
                // Here i should tell server what I plan on doing
                // then i need to send it the currentUser name identification
                // the server should then send me the information i need including profile information
                // update the below to show the information regarding the user.
                currentUser = loggedInUser;
                pw.println("loadProfileAttempt");
                pw.flush();
                pw.println(currentUser);
                System.out.println(currentUser);
                pw.flush();

                name = in.nextLine();
                //System.out.println("This is what is being sent from server");
                //System.out.println(name);
                contact = in.nextLine();
                interests = in.nextLine();
                aboutMe = in.nextLine();
            }

            socket.close();
        } catch (IOException error) {
            error.printStackTrace();
        }

        profileName.setText("   " + name);
        profileInfo.setText(String.format
                ("       Contact: %s\n       Interests: %s\n       About Me: %s\n",
                        contact, interests, aboutMe));

        settingsButton.setEnabled(true);
        deleteButton.setEnabled(true);
        friendRequestsButton.setEnabled(true);
        addFriend.setEnabled(false);
        removeFriend.setEnabled(false);
        home.setEnabled(false);
        middlePanel.updateUI();
    }

    public void goHome(String username, String contact, String interests,
                       String aboutMe, ImageIcon profilePic) throws IOException {
        loggedInUser = username;
        currentUser = username;

        this.name = username;
        this.contact = contact;
        this.interests = interests;
        this.aboutMe = aboutMe;
        this.profilePic = profilePic;

        profileName.setText("   " + username);
        profileInfo.setText(String.format
                ("       Contact: %s\n       Interests: %s\n       About Me: %s\n",
                        contact, interests, aboutMe));
        profPicLabel.setIcon(profilePic);

        middlePanel.updateUI();
    }

    public void updateProfile(String username, String password, String profileType,
                              String contact, String interests, String aboutMe, ImageIcon profilePic) {
//    	this.username = username;
//    	this.password = password;
//    	this.profileType = profileType;
//    	this.contact = contact;
//    	this.interests = interests;
//    	this.aboutMe = aboutMe;
//    	this.profilePic = profilePic;
//
//    	profileName.setText(username);
//    	profileInfo.setText(aboutMe);
//    	profPicLabel.setIcon(profilePic);
//
//    	middlePanel.updateUI();
//    	eastPanel.updateUI();
//    	frame.setVisible(true);
    }

    private class SettingsMenu implements Runnable {
        // Class Fields
        private JFrame settingsFrame; // window frame
        private Container settingsContent; // panel container
        private JPanel northPanel;
        private JPanel westPanel;
        private JPanel eastPanel;
        private JPanel southPanel;

        private JTextField userText; // username
        private JTextField passText; // password
        private JComboBox<String> security; // security option
        private JTextField contactText; // contact info
        private JTextField interestText; // interests
        private JTextArea aboutMeText; // about me
        private ImageIcon pic; // profile picture
        private JLabel picLabel; // profile pic label
        private JButton changePic; // button for changing the picture
        private JButton changeSettings; // button for changing the settings

        private String newName;
        private String newPassword;
        private String newProfileType;
        private String newContact;
        private String newInterests;
        private String newAboutMe;

        // Class Constructor
        public SettingsMenu(String username, String password, String contact,
                            String interests, String aboutMe, ImageIcon profilePic) {
            newName = username;
            newPassword = password;
            newContact = contact;
            newInterests = interests;
            newAboutMe = aboutMe;
            pic = profilePic;
        }

        // run()
        public void run() {
            // Window Frame
            settingsFrame = new JFrame("Settings Menu");

            // Set the frame's size, position, and close operation
            settingsFrame.setSize(700, 565);
            settingsFrame.setLocationRelativeTo(null);
            settingsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            // App container
            settingsContent = settingsFrame.getContentPane();
            settingsContent.setLayout(new BorderLayout());

            // Set up the north panel
            setUpNorthPanel();

            // Set up the west panel
            setUpWestPanel();

            // Set up the east panel
            setUpEastPanel();

            // Set up the south panel
            setUpSouthPanel();

            // Pack the frame, disable resizing, and set it visible
            settingsFrame.pack();
            settingsFrame.setResizable(false);
            settingsFrame.setVisible(true);
        }

        // setUpNorthPanel()
        private void setUpNorthPanel() {
            // north panel
            northPanel = new JPanel(new BorderLayout());
            northPanel.setBackground(new Color(26, 110, 163));
            northPanel.setPreferredSize(new Dimension(700, 95));
            settingsContent.add(northPanel, BorderLayout.NORTH);

            // app logo
            ImageIcon appLogo = new ImageIcon("meBook2.png");
            JLabel appLogoLabel = new JLabel(appLogo);

            // extra JPanel for the change settings button
            JPanel changePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            changePanel.setPreferredSize(new Dimension(250, 95));
            changePanel.setBackground(new Color(26, 110, 163));

            // change settings button
            JButton changeSettings = new JButton("Change Settings");
            changeSettings.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            changeSettings.setPreferredSize(new Dimension(175, 40));

            // space filler
            JLabel spaceFiller1 = new JLabel("");
            spaceFiller1.setPreferredSize(new Dimension(245, 15));
            JLabel spaceFiller2 = new JLabel("");
            spaceFiller2.setPreferredSize(new Dimension(30, 50));

            northPanel.add(appLogoLabel, BorderLayout.WEST);
            changePanel.add(spaceFiller1);
            changePanel.add(spaceFiller2);
            changePanel.add(changeSettings);
            northPanel.add(changePanel, BorderLayout.EAST);

            /*
             * Action Listener for changing the settings
             */
            ActionListener updateSettings = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    if (e.getSource() == changeSettings) {
                        // Get updated info
                        newName = userText.getText();
                        newPassword = passText.getText();
                        newProfileType = (String) security.getSelectedItem();
                        newContact = contactText.getText();
                        newInterests = interestText.getText();
                        newAboutMe = aboutMeText.getText();

                        System.out.printf("This is the settings updates:%n" +
                                        "%s%n" +
                                        "%s%n" +
                                        "%s%n" +
                                        "%s%n" +
                                        "%s%n" +
                                        "%s%n", newName, newPassword, newProfileType, newContact,
                                newInterests, newAboutMe);

                        try {
                            Socket socket = new Socket("localhost", 4242);
                            try (PrintWriter pw = new PrintWriter(socket.getOutputStream()); Scanner in = new Scanner(socket.getInputStream())) {
                                pw.println("changeSettings");
                                pw.flush();
                                pw.println(loggedInUser);
                                pw.flush();
                                pw.println(newName);
                                pw.flush();
                                pw.println(newPassword);
                                pw.flush();
                                pw.println(newProfileType);
                                pw.flush();
                                pw.println(newContact);
                                pw.flush();
                                pw.println(newInterests);
                                pw.flush();
                                pw.println(newAboutMe);
                                pw.flush();
                                String line = in.nextLine();
                                System.out.println(line);


                                if (line.equals("Finished with settings updates")) {
                                    //TODO GO HOME
                                    name = newName;
                                    password = newPassword;
                                    contact = newContact;
                                    interests = newInterests;
                                    aboutMe = newAboutMe;
                                    goHome(name, contact, interests, aboutMe, pic);
                                    socket.close();
                                } else if (line.equals("usernameTaken")) {
                                    JOptionPane.showMessageDialog(null, "Username Already Taken", "Error", JOptionPane.ERROR_MESSAGE);
                                    goHome(name, contact, interests, aboutMe, pic);
                                    socket.close();
                                }
                            }
                        } catch (IOException error) {
                            error.printStackTrace();
                        }

                        settingsFrame.dispose();
                        frame.setVisible(true);
                    }
                }

            };

            // Add the Action Listener to the change settings button
            changeSettings.addActionListener(updateSettings);
        }

        // setUpWestPanel()
        private void setUpWestPanel() {
            // west panel
            westPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            westPanel.setPreferredSize(new Dimension(455, 275));
            westPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
            settingsContent.add(westPanel, BorderLayout.WEST);

            // username label and text field
            JLabel userLabel = new JLabel("    Username: ");
            userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            userText = new JTextField(newName);
            userText.setPreferredSize(new Dimension(200, 30));

            // password label and text field
            JLabel passLabel = new JLabel("    Password: ");
            passLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            passText = new JTextField(newPassword);
            passText.setPreferredSize(new Dimension(200, 30));

            // account security label and combo box
            JLabel secLabel = new JLabel("    Account Security: ");
            secLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            String[] securityOptions = {"public", "protected", "private"};
            security = new JComboBox<String>(securityOptions);
            security.setPreferredSize(new Dimension(130, 30));

            // contact label and text field
            JLabel contactLabel = new JLabel("    Contact:     ");
            contactLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
            contactText = new JTextField(newContact);
            contactText.setPreferredSize(new Dimension(200, 30));

            // interests label and text field
            JLabel interestLabel = new JLabel("    Interests:  ");
            interestLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
            interestText = new JTextField(newInterests);
            interestText.setPreferredSize(new Dimension(200, 30));

            // space filler
            JLabel spaceFiller = new JLabel("");
            spaceFiller.setPreferredSize(new Dimension(400, 35));

            westPanel.add(spaceFiller);
            westPanel.add(userLabel);
            westPanel.add(userText);
            westPanel.add(passLabel);
            westPanel.add(passText);
            westPanel.add(secLabel);
            westPanel.add(security);
            westPanel.add(contactLabel);
            westPanel.add(contactText);
            westPanel.add(interestLabel);
            westPanel.add(interestText);
        }

        // setUpEastPanel()
        private void setUpEastPanel() {
            // east panel
            eastPanel = new JPanel();
            eastPanel.setPreferredSize(new Dimension(245, 275));
            eastPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
            settingsContent.add(eastPanel, BorderLayout.EAST);

            // profile picture
            picLabel = new JLabel(pic);
            picLabel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

            // change picture button
            changePic = new JButton("Change Picture");
            changePic.setPreferredSize(new Dimension(225, 25));

            eastPanel.add(picLabel);
            eastPanel.add(changePic);

            /*
             * Action Listener for changing the profile picture
             */
            ActionListener picButtonClick = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == changePic) {
                        /*
                         *  JFileChooser and showOptionDialog for choosing
                         *  an image file
                         *
                         *  Code borrowed from "GSoft Knowledge" on YouTube
                         */
                        JFileChooser picChooser = new JFileChooser();
                        picChooser.showOpenDialog(null);

                        // try to create a picture with the given filename
                        try {
                            // Get the selected file
                            File file = picChooser.getSelectedFile();

                            // Check the filename's ending for "jpg", "png", or "gif"
                            String filename = file.toString();
                            String endings = "gifjpgpng";
                            if (endings.contains(filename.substring(filename.indexOf(".") + 1))) {

                                // Create a new ImageIcon with the filename
                                ImageIcon newPic = new ImageIcon(filename);

                                // Check to see that it is the right size
                                if (newPic.getIconHeight() <= 225 && newPic.getIconWidth() <= 225) {
                                    // and update the picture label
                                    pic = newPic;
                                    picLabel.setIcon(pic);
                                    eastPanel.updateUI();
                                } else {
                                    // Tell the user that the picture wasn't the right size
                                    JOptionPane.showMessageDialog(null,
                                            "The picture isn't within the size requirement of 225 x 225!",
                                            "Settings Menu", JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                // The file didn't have the correct file ending
                                JOptionPane.showMessageDialog(null,
                                        "The given file isn't a picture!",
                                        "Settings Menu", JOptionPane.ERROR_MESSAGE);
                            }
                            // The file was not compatible with the ImageIcon constructor
                        } catch (Exception exception) {
                            // Tell the user that the image couldn't be uploaded
                            JOptionPane.showMessageDialog(null,
                                    "The picture wasn't able to be uploaded!",
                                    "Settings Menu", JOptionPane.ERROR_MESSAGE);
                        }

                    }

                }
            };

            changePic.addActionListener(picButtonClick);
        }

        // setUpSouthPanel()
        private void setUpSouthPanel() {
            // south panel
            southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            southPanel.setPreferredSize(new Dimension(700, 195));
            southPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
            settingsContent.add(southPanel, BorderLayout.SOUTH);

            // extra panel for "About me:" label
            JPanel aboutMePanel = new JPanel(new GridLayout(2, 1));
            aboutMePanel.setPreferredSize(new Dimension(150, 150));
            southPanel.add(aboutMePanel);

            // about me label
            JLabel aboutme = new JLabel("   About Me:");
            aboutme.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));

            // about me text area
            aboutMeText = new JTextArea(aboutMe);
            aboutMeText.setPreferredSize(new Dimension(375, 180));
            aboutMeText.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
            aboutMeText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            aboutMeText.setLineWrap(true);
            aboutMeText.setWrapStyleWord(true);

            aboutMePanel.add(aboutme);
            southPanel.add(aboutMeText);
        }

    }

    private class friendRequestMenu implements Runnable {
        // Class Fields
        private JFrame friendRequestFrame; // window frame
        private Container friendRequestFrameContent; // panel container
        private JPanel topPanel;
        private JPanel rightPanel;
        private JPanel centerPanel;
        private JPanel leftPanel;
        private JButton finishButton;

        private ArrayList<User> receivedRequests;

        // Class Constructor
        public friendRequestMenu() {
            for (User user : allUsers) {
                if (user.getUsername().equals(loggedInUser)) {
                    receivedRequests = user.getReceivedRequests();

                    System.out.println("REQUEST MENU");

                    for (int i = 0; i < receivedRequests.size(); i++) {
                        System.out.println(receivedRequests.get(i).getUsername());
                        ;
                    }
                }
            }
        }

        // run()
        public void run() {
            // Window Frame
            friendRequestFrame = new JFrame("Friend Requests Menu");

            // Set the frame's size, position, and close operation
            friendRequestFrame.setSize(450, 600);
            friendRequestFrame.setLocationRelativeTo(null);
            friendRequestFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            // App container
            friendRequestFrameContent = friendRequestFrame.getContentPane();
            friendRequestFrameContent.setLayout(new BorderLayout());

            // Set up the panel
            setUpTopPanel();
            setUpRightPanel();
            setUpCenterPanel();
            setUpLeftPanel();

            // Pack the frame, disable resizing, and set it visible
            friendRequestFrame.pack();
            friendRequestFrame.setResizable(false);
            friendRequestFrame.setVisible(true);
        }

        // setUpPanel()
        private void setUpCenterPanel() {
            // JPanel for the friends List
            centerPanel = new JPanel();
            centerPanel.setBackground(Color.white);
            centerPanel.setPreferredSize(new Dimension(171, 500));
            centerPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
            friendRequestFrameContent.add(centerPanel, BorderLayout.CENTER);

            // JTextField for the friends list label
            JTextField requestsLabel = new JTextField("  Received Request List   ");
            requestsLabel.setBackground(new Color(200, 200, 200));
            requestsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            requestsLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 3));
            requestsLabel.setEditable(false);

            // JList for friend requests
            JList<String> requestList = new JList<String>();

            // ListModel for request list
            DefaultListModel<String> requestModel = new DefaultListModel<String>();
            requestList.setModel(requestModel);

            //User[] receivedRequestsList = new User[receivedRequests.size()];
            for (int i = 0; i < receivedRequests.size(); i++) {
                //receivedRequestsList[i] = receivedRequests.get(i);
                //System.out.println("TEST" + receivedRequestsList[i].getUsername());
                User user = receivedRequests.get(i);
                requestModel.addElement(user.getUsername() + "  -  " + user.getAccountSecurity());
            }

            // JScrollPane for the friend requests
            JScrollPane scroll = new JScrollPane(requestList);
            scroll.setPreferredSize(new Dimension(171, 420));
            scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            scroll.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

            // JButton for returning to the main window
            finishButton = new JButton("Go Back");
            finishButton.setPreferredSize(new Dimension(167, 30));
            finishButton.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 22));

            centerPanel.add(requestsLabel);
            centerPanel.add(scroll);
            centerPanel.add(finishButton);

            /*
             * ListSelectionListener for the friends list
             *
             * Code borrowed from Lazic B.
             */
            requestList.getSelectionModel().addListSelectionListener(e -> {
                // Get the selected friend
                User friendUser = null;
                String selectedInfo = requestList.getSelectedValue();
                String username = selectedInfo.split("  -  ")[0];
                for (User user : receivedRequests) {
                    if (user.getUsername().equals(username)) {
                        friendUser = user;
                    }
                }

                int option = JOptionPane.showConfirmDialog(null, "Would you liked to add "
                        + friendUser.getUsername() + " as a friend?", "Friend Request", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.YES_OPTION) {

                    try {
                        Socket socket = new Socket("localhost", 4242);

                        try (PrintWriter pw = new PrintWriter(socket.getOutputStream());
                             Scanner in = new Scanner(socket.getInputStream())) {
                            // Request the friend's info
                            pw.println("addFriend");
                            pw.flush();

                            pw.println(loggedInUser);
                            pw.flush();

                            pw.println(username);
                            pw.flush();


                        } catch (IOException error) {
                            error.printStackTrace();
                        }

                        socket.close();

                    } catch (Exception error) {
                        error.printStackTrace();
                    }

//                    String selectedUser = new String(selectedInfo);
                    requestList.setEnabled(false);
                    requestModel.remove(requestModel.indexOf(selectedInfo));
                    requestList.clearSelection();
                    centerPanel.updateUI();
                    requestList.setEnabled(true);

//
//                    centerPanel.updateUI();

                }

            });

            /*
             * Action Listener for the home button;
             */
            ActionListener returnHome = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == finishButton) {
                        friendRequestFrame.dispose();
                        try {
                            friendList.setListData(getFriends(loggedInUser));
                        } catch (IOException error) {
                            error.printStackTrace();
                        }
                        westPanel.updateUI();
                        frame.setVisible(true);
                    }
                }
            };

            finishButton.addActionListener(returnHome);

        }

        private void setUpTopPanel() {
            topPanel = new JPanel(new GridLayout(1, 1));
            topPanel.setPreferredSize(new Dimension(450, 95));
            topPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
            friendRequestFrameContent.add(topPanel, BorderLayout.NORTH);

            ImageIcon logo = new ImageIcon("meBook2.png");
            JLabel logoLabel = new JLabel(logo);

            topPanel.add(logoLabel);
        }

        private void setUpRightPanel() {
            rightPanel = new JPanel();
            rightPanel.setBackground(new Color(240, 240, 200));
            rightPanel.setPreferredSize(new Dimension(140, 500));
            rightPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
            friendRequestFrameContent.add(rightPanel, BorderLayout.EAST);

        }

        private void setUpLeftPanel() {
            leftPanel = new JPanel();
            leftPanel.setBackground(new Color(240, 240, 200));
            leftPanel.setPreferredSize(new Dimension(140, 500));
            leftPanel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
            friendRequestFrameContent.add(leftPanel, BorderLayout.WEST);
        }

    }
}