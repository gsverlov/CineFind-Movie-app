package uipanels;

import controllers.LoginManager;
import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;

import javax.swing.*;

public class LoginPanel extends JPanel {
    public final JButton loginButton;
    public final JButton backButton;
    public final JTextField usernameBox;
    public final JTextField passwordBox;


    public LoginPanel(LoginManager loginManager) {
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
        loginButton = new JButton("Login");
        backButton = new JButton("Back");
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        loginButton.addActionListener(e -> {
            String username = usernameBox.getText();
            String password = passwordBox.getText();

            try {
                loginManager.loginInteractor(username, password);

                // succes and empty text field
                JOptionPane.showMessageDialog(this, "Login Successful!");
                clearFields();


            } catch (UserNotFoundException | WrongPasswordException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        add(usernamePanel);
        add(passwordPanel);
        add(buttonPanel);
    }

    public void clearFields() {
        usernameBox.setText("");
        passwordBox.setText("");
    }
}