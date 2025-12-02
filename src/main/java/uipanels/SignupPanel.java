package uipanels;

import controllers.LoginManager;

import javax.swing.*;

public class SignupPanel extends JPanel{
    public final JButton signUpButton;
    public final JButton backButton;
    public final JTextField usernameBox;
    public final JTextField passwordBox;

    public SignupPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel usernamePanel = new JPanel();
        JLabel usernameLabel = new JLabel("Username:");
        usernameBox = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameBox);

        JPanel passwordPanel = new JPanel();
        JLabel passwordLabel = new JLabel("Password:");
        passwordBox = new JTextField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordBox);

        JPanel buttonPanel = new JPanel();
        signUpButton = new JButton("Signup");
        backButton = new JButton("Back");
        buttonPanel.add(signUpButton);
        buttonPanel.add(backButton);

        add(usernamePanel);
        add(passwordPanel);
        add(buttonPanel);
    }
}
