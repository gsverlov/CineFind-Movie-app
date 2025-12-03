package uipanels;

import controllers.LoginManager;
import exceptions.UsernameTakenException;

import javax.swing.*;

public class SignupPanel extends JPanel {
    public JButton signUpButton;
    public JButton backButton;
    public JTextField usernameBox;
    // CRITICAL FIX 1: Use JPasswordField for password security
    public JPasswordField passwordBox;

    public SignupPanel(LoginManager loginManager) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // ... username panel setup (no change) ...
        JPanel usernamePanel = new JPanel();
        JLabel usernameLabel = new JLabel("Username:");
        usernameBox = new JTextField(20);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameBox);

        JPanel passwordPanel = new JPanel();
        JLabel passwordLabel = new JLabel("Password:");
        // CRITICAL FIX 2: Initialize as JPasswordField
        passwordBox = new JPasswordField(20);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordBox);

        // ... button panel setup (no change) ...
        JPanel buttonPanel = new JPanel();
        signUpButton = new JButton("Signup");
        backButton = new JButton("Back");
        buttonPanel.add(signUpButton);
        buttonPanel.add(backButton);

        signUpButton.addActionListener(e -> {
            String username = usernameBox.getText();
            // CRITICAL FIX 3: Get password as char[] and convert to String
            // This is the correct way to retrieve text from a JPasswordField
            String password = new String(passwordBox.getPassword());

            // check if input is empty
            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password cannot be empty!");
                return;
            }

            loginManager.createAccount(username, password);

            // This code runs ONLY on successful creation
            JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
            clearFields(); // FIELDS ARE CLEARED HERE

        });

        add(usernamePanel);
        add(passwordPanel);
        add(buttonPanel);
    }

    public void clearFields() {
        // This method is correct for both JTextField and JPasswordField
        usernameBox.setText("");
        passwordBox.setText("");
    }
}