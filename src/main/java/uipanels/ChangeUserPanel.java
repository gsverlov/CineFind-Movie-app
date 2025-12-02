package uipanels;

import controllers.LoginManager;
import models.User;

import javax.swing.*;

public class ChangeUserPanel extends JPanel {
    public final JButton backButton;
    public final JButton submitButton;
    public JTextField userText;
    public ChangeUserPanel(LoginManager loginManager){
        User user = loginManager.getLoggedInUser();

        backButton = new JButton("Back");
        submitButton = new JButton("Submit");
        userText = new JTextField(20);

        if(user == null){
            return;
        }

        JPanel headerPanel = new JPanel();
        JLabel heading = new JLabel("\"Your current username is \" + user.getUsername()");
        headerPanel.add(heading);

        JPanel userPanel = new JPanel();
        JLabel userLabel = new JLabel("Please Enter your desired Username");
        userPanel.add(userLabel);
        userPanel.add(userText);

        JPanel buttonPanel = new JPanel();


        add(headerPanel);
        add(userPanel);
        add(buttonPanel);
    }
}
