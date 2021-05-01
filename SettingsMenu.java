package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * SettingsMenu.java
 *
 * This is the settings menu template for
 * our project 5 app.
 *
 * I learned about JFileChooser for selecting
 * files from a youtuber called "GSoft Knowledge"
 *
 * @author Vincent Dubyna
 * @version 2021-4-26
 *
 */

public class SettingsMenu implements Runnable{
    // Class Fields
    JFrame settingsFrame; // window frame
    Container settingsContent; // panel container
    JPanel northPanelSettings;
    JPanel westPanelSettings;
    JPanel eastPanelSettings;
    JPanel southPanelSettings;

    JTextField userText; // username
    JTextField passText; // password
    JComboBox<String> security; // security option
    JTextField contactText; // contact info
    JTextField interestText; // interests
    JTextArea aboutMeText; // about me
    ImageIcon pic; // profile picture
    JLabel picLabel; // profile pic label
    JButton changePic; // button for changing the picture
    JButton changeSettings; // button for changing the settings

    // main()
    public static void main(String[] args) {
        // Use a runnable object to perform all of the GUI execution
        SwingUtilities.invokeLater(new SettingsMenu());
    }

    // run()
    public void run() {
        // Window Frame
        settingsFrame = new JFrame("Settings Menu");

        // Set the frame's size, position, and close operation
        settingsFrame.setSize(700, 565);
        settingsFrame.setLocationRelativeTo(null);
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // App container
        settingsContent = settingsFrame.getContentPane();
        settingsContent.setLayout(new BorderLayout());

        // Set up the north panel
        setUpNorthPanelSettings();

        // Set up the west panel
        setUpWestPanelSettings();

        // Set up the east panel
        setUpEastPanelSettings();

        // Set up the south panel
        setUpSouthPanelSettings();

        // Pack the frame, disable resizing, and set it visible
        settingsFrame.pack();
        settingsFrame.setResizable(false);
        settingsFrame.setVisible(true);
    }

    // setUpNorthPanel()
    private void setUpNorthPanelSettings() {
        // north panel
        northPanelSettings = new JPanel(new BorderLayout());
        northPanelSettings.setBackground(new Color(26,110,163));
        northPanelSettings.setPreferredSize(new Dimension(700, 95));
        settingsContent.add(northPanelSettings, BorderLayout.NORTH);

        // app logo
        ImageIcon appLogo = new ImageIcon("meBook2.png");
        JLabel appLogoLabel = new JLabel(appLogo);

        // extra JPanel for the change settings button
        JPanel changePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        changePanel.setPreferredSize(new Dimension(250, 95));
        changePanel.setBackground(new Color(26,110,163));

        // change settings button
        changeSettings = new JButton("Change Settings");
        changeSettings.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        changeSettings.setPreferredSize(new Dimension(175, 40));

        // space filler
        JLabel spaceFiller1 = new JLabel("");
        spaceFiller1.setPreferredSize(new Dimension(245, 15));
        JLabel spaceFiller2 = new JLabel("");
        spaceFiller2.setPreferredSize(new Dimension(30, 50));

        northPanelSettings.add(appLogoLabel, BorderLayout.WEST);
        changePanel.add(spaceFiller1);
        changePanel.add(spaceFiller2);
        changePanel.add(changeSettings);
        northPanelSettings.add(changePanel, BorderLayout.EAST);

        /*
         * Action Listener for changing the settings
         */
        ActionListener updateSettings = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == changeSettings) {
                    // Get updated info
                    String username = userText.getText();
                    String password = passText.getText();
                    String profileType = (String)security.getSelectedItem();
                    String contact = contactText.getText();
                    String interests = interestText.getText();
                    String aboutMe = aboutMeText.getText();

                    // Display changed settings
                    String info = String.format("Username: %s\n"
                                    + "Password: %s\n"
                                    + "Account Security: %s\n"
                                    + "Contact: %s\n"
                                    + "Interests: %s\n"
                                    + "About Me: %s",
                            username, password, profileType,
                            contact, interests, aboutMe);
                    JOptionPane.showMessageDialog(null,
                            info,
                            "Settings Menu", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        };

        // Add the Action Listener to the change settings button
        changeSettings.addActionListener(updateSettings);
    }

    // setUpWestPanel()
    private void setUpWestPanelSettings() {
        // west panel
        westPanelSettings = new JPanel(new FlowLayout(FlowLayout.LEFT));
        westPanelSettings.setPreferredSize(new Dimension(455, 275));
        westPanelSettings.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
        settingsContent.add(westPanelSettings, BorderLayout.WEST);

        // username label and text field
        JLabel userLabel = new JLabel("    Username: ");
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        userText = new JTextField("Vincent D");
        userText.setPreferredSize(new Dimension(200, 30));

        // password label and text field
        JLabel passLabel = new JLabel("    Password: ");
        passLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        passText = new JTextField("password");
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
        contactText = new JTextField("email@address.com");
        contactText.setPreferredSize(new Dimension(200, 30));

        // interests label and text field
        JLabel interestLabel = new JLabel("    Interests:  ");
        interestLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
        interestText = new JTextField("video games, cats, food");
        interestText.setPreferredSize(new Dimension(200, 30));

        // space filler
        JLabel spaceFiller = new JLabel("");
        spaceFiller.setPreferredSize(new Dimension(400, 35));

        westPanelSettings.add(spaceFiller);
        westPanelSettings.add(userLabel);
        westPanelSettings.add(userText);
        westPanelSettings.add(passLabel);
        westPanelSettings.add(passText);
        westPanelSettings.add(secLabel);
        westPanelSettings.add(security);
        westPanelSettings.add(contactLabel);
        westPanelSettings.add(contactText);
        westPanelSettings.add(interestLabel);
        westPanelSettings.add(interestText);
    }

    // setUpEastPanel()
    private void setUpEastPanelSettings() {
        // east panel
        eastPanelSettings = new JPanel();
        eastPanelSettings.setPreferredSize(new Dimension(245, 275));
        eastPanelSettings.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
        settingsContent.add(eastPanelSettings, BorderLayout.EAST);

        // profile picture
        pic = new ImageIcon("profilePic.png");
        picLabel = new JLabel(pic);
        picLabel.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));

        // change picture button
        changePic = new JButton("Change Picture");
        changePic.setPreferredSize(new Dimension(225, 25));

        eastPanelSettings.add(picLabel);
        eastPanelSettings.add(changePic);

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
                                picLabel.setIcon(newPic);
                                eastPanelSettings.updateUI();
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
    private void setUpSouthPanelSettings() {
        // south panel
        southPanelSettings = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southPanelSettings.setPreferredSize(new Dimension(700, 195));
        southPanelSettings.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
        settingsContent.add(southPanelSettings, BorderLayout.SOUTH);

        // extra panel for "About me:" label
        JPanel aboutMePanel = new JPanel(new GridLayout(2, 1));
        aboutMePanel.setPreferredSize(new Dimension(150, 150));
        southPanelSettings.add(aboutMePanel);

        // about me label
        JLabel aboutme = new JLabel("   About Me:");
        aboutme.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));

        // about me text area
        aboutMeText = new JTextArea("I once caught a fish.");
        aboutMeText.setPreferredSize(new Dimension(375, 180));
        aboutMeText.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 2));
        aboutMeText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        aboutMeText.setLineWrap(true);

        aboutMePanel.add(aboutme);
        southPanelSettings.add(aboutMeText);
    }

}