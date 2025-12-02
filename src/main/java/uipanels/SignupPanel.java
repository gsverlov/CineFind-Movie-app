package uipanels;

import controllers.LoginManager;
import exceptions.UsernameTakenException;

import javax.swing.*;

public class SignupPanel extends JPanel {
    public JButton signUpButton;
    public JButton backButton;
    public JTextField usernameBox;
    public JTextField passwordBox;

    public SignupPanel(LoginManager loginManager) {
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

        signUpButton.addActionListener(e -> {
            String username = usernameBox.getText();
            String password = passwordBox.getText();

            // check if input is empty
            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password cannot be empty!");
                return;
            }

            try {
                loginManager.createAccount(username, password);

                // empty text field
                JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
                clearFields();

            } catch (UsernameTakenException ex) {
                JOptionPane.showMessageDialog(this, "Username already taken. Please choose another one.");
            }
        });

        add(usernamePanel);
        add(passwordPanel);
        add(buttonPanel);
    }

    // empty text field
    public void clearFields() {
        usernameBox.setText("");
        passwordBox.setText("");
    }
}