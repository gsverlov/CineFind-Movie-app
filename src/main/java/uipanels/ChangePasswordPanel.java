package uipanels;

import controllers.LoginManager;
import models.User;

import javax.swing.*;

import java.util.Map;

public class ChangePasswordPanel extends JPanel {
    public final JButton backButton;
    public final JButton submitButton;
    public final JTextField passText1;
    public final JTextField passText2;
    public ChangePasswordPanel(LoginManager loginManager){
        User user = loginManager.getLoggedInUser();

        passText1 = new JTextField(20);
        passText2 = new JTextField(20);
        backButton = new JButton("Back");
        submitButton = new JButton("Submit");

        if(user == null){
            return;
        }

        Map<String, String> userPassmap = User.getUserPasswordMap();

        JPanel headerPanel = new JPanel();
        String password = userPassmap.get(user.getUsername());
        JLabel heading = new JLabel("Your current password is" + password);
        headerPanel.add(heading);

        JPanel passPanel = new JPanel();
        JLabel passLabel = new JLabel("Please Enter your desired Password: ");

        JLabel passSecondLabel = new JLabel("Please Re-enter your desired Password: ");

        passPanel.add(passLabel);
        passPanel.add(passText1);
        passPanel.add(passSecondLabel);
        passPanel.add(passText2);

        JPanel buttonPanel = new JPanel();


        add(headerPanel);
        add(passPanel);
        add(buttonPanel);
    }
}
