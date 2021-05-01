
package com.company;

import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 * User.java
 * <p>
 * This is the User class that defines parameters for
 * each of the users of the app.
 *
 * @author Shreya Kurdukar
 * @version 04/26/2021
 */

public class User {
    // Class Fields
    private String username;
    private String password;
    private String contact;
    private String interests;
    private ArrayList<User> friends;
    private ArrayList<User> sentRequests;
    private ArrayList<User> receivedRequests;
    private String aboutMe;
    private String accountSecurity;
    private ImageIcon profilePic;

    // Initial Class Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.contact = "";
        this.interests = "";
        this.friends = new ArrayList<User>();
        this.sentRequests = new ArrayList<User>();
        this.receivedRequests = new ArrayList<User>();
        this.aboutMe = "";
        this.accountSecurity = "Public";
    }

    //WE should start using the constructor below
    public User(String username, String password, String contact, String interests,
                String aboutMe, String accountSecurity, ArrayList<User> friends,
                ArrayList<User> sentRequests, ArrayList<User> receivedRequests) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.interests = interests;
        this.friends = friends;
        this.sentRequests = sentRequests;
        this.receivedRequests = receivedRequests;
        this.aboutMe = aboutMe;
        this.accountSecurity = accountSecurity;
        this.profilePic = new ImageIcon("default.png");
    }

    // Outdated
    public User(String username, String password, String contact, String interests,
                String aboutMe, String accountSecurity) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.interests = interests;
        this.friends = new ArrayList<User>();
        this.sentRequests = new ArrayList<User>();
        this.receivedRequests = new ArrayList<User>();
        this.aboutMe = aboutMe;
        this.accountSecurity = accountSecurity;
        this.profilePic = new ImageIcon("default.png");
    }

    // Getter Methods
    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getContact() {
        return this.contact;
    }

    public String getInterests() {
        return this.interests;
    }

    public String getAboutMe() {
        return this.aboutMe;
    }

    public String getAccountSecurity() {
        return this.accountSecurity;
    }

    public ImageIcon getProfilePic() {
        return profilePic;
    }

    // Setter Methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public void setAboutMe(String about) {
        this.aboutMe = about;
    }

    public void setAccountSecurity(String security) {
        if (security.equalsIgnoreCase("public")) {
            this.accountSecurity = security;
        } else if (security.equalsIgnoreCase("private")) {
            this.accountSecurity = security;
        } else if (security.equalsIgnoreCase("protected")) {
            this.accountSecurity = security;
        } else
            System.out.println("This is not a valid security option!");
    }

    public void setProfilePic(ImageIcon profilePic) {
        this.profilePic = profilePic;
    }

    // Friend Methods
    public ArrayList<User> getFriends() {
        return this.friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public void addFriend(User user) {
        if (!this.friends.contains(user)) {
            this.friends.add(user);
            user.addFriend(this);
        } else {
            System.out.println("This user is already a friend!");
        }
    }

    public void removeFriend(User user) {
        if (this.friends.contains(user)) {
            this.friends.remove(user);
            user.removeFriend(this); //removes them both from each other's friends lists
        } else {
            System.out.println("This user does not exist!");
        }
    }

    // Request Methods
    public ArrayList<User> getSentRequests() {
        return this.sentRequests;
    }

    public ArrayList<User> getReceivedRequests() {
        return this.receivedRequests;
    }

    public void setSentRequests(ArrayList<User> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public void setReceivedRequests(ArrayList<User> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }

    public void sendRequest(User user) {
        this.sentRequests.add(user);
        user.receiveRequest(this);
    }

    public void removeSentRequest(User user) {
        if (this.sentRequests.contains(user)) {
            this.sentRequests.remove(user);
            user.getReceivedRequests().remove(this);

        } else
            System.out.println("You never sent this user a request");
    }

    public void receiveRequest(User user) {
        this.receivedRequests.add(user);
    }

    // toString()
    public String toString() {
        return username + ";" + password + ";" + contact + ";" + interests + ";"
                + aboutMe + ";" + accountSecurity;
    }

    public String toStringSentRequests() {
        String sentTotal = "";
        try {
            for (User user : this.sentRequests) {
                sentTotal = sentTotal + user.toString() + ",,";
            }

            return username + ",," + sentTotal;
        } catch (NullPointerException e) {
            return "null";
        }
    }

    public String toStringReceivedRequests() {
        String recTotal = "";
        try {
            for (User user : this.receivedRequests) {
                recTotal = recTotal + user.toString() + ",,";
            }
            return username + ",," + recTotal;
        } catch (NullPointerException e) {
            return "null";
        }

    }

    // printFriends()
    public String printFriends(ArrayList<User> users) {
        StringBuilder line = new StringBuilder();
        int count = 0;
        for (User user : users) {
            if (count++ == 0) {
                line.append(user.getUsername());
            } else {
                line.append(", " + user.getUsername());
            }
        }
        if (count == 0) {
            return "null";
        }
        return line.toString();
    }

    // exportProfile()
    public void exportProfile(String filename) {
        String line1 = "Username: " + this.username;
        String line2 = "Contact: " + this.contact;
        String line3 = "About me: " + this.aboutMe;
        String line4 = "Interests: " + this.interests;
        String line5 = "All Friends: " + this.friends.toString();

        ArrayList<String> toExport = new ArrayList<String>();
        toExport.add(line1);
        toExport.add(line2);
        toExport.add(line3);
        toExport.add(line4);
        toExport.add(line5);

        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.write(toExport.toString());
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

}