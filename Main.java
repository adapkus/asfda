package com.company;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
	// write your code here
        String[] selections = {"Login", "Sign-Up"};
        int option = JOptionPane.showOptionDialog(null,
                "Select an option?",
                "Login/Sign-Up",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                selections,  //the titles of buttons
                selections[0]); //default button title
    }
}
