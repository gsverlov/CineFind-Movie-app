package uipanels;

import controllers.LoginManager;

import javax.swing.*;

public class SignupPanel extends JPanel{
    public JButton signUpButton;
    public JButton backButton;

    public SignupPanel(LoginManager loginManager) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel usernamePanel = new JPanel();
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameBox = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameBox);

        JPanel passwordPanel = new JPanel();
        JLabel passwordLabel = new JLabel("Password:");
        JTextField passwordBox = new JTextField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordBox);

        JPanel buttonPanel = new JPanel();
        signUpButton = new JButton("Signup");
        backButton = new JButton("Back");
        buttonPanel.add(signUpButton);
        buttonPanel.add(backButton);

        signUpButton.addActionListener(e ->{
            String username = usernameBox.getText();
            String password = passwordBox.getText();
            loginManager.createAccount(username, password);

        });

        add(usernamePanel);
        add(passwordPanel);
        add(buttonPanel);
    }
}
