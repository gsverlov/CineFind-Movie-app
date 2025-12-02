package uipanels;

import controllers.LoginManager;
import exceptions.UserNotFoundException;
import exceptions.WrongPasswordException;

import javax.swing.*;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;

public class LoginPanel extends JPanel {
    public final JButton loginButton;
    public final JButton backButton;
    public final JTextField usernameBox;
    public final JTextField passwordBox;


    public LoginPanel(LoginManager loginManager) {
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

        // 3. [新增] 建立一個 Wrapper 將表單內容包起來 (確保內部垂直排列)
        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        formWrapper.add(usernamePanel);
        formWrapper.add(passwordPanel);
        formWrapper.add(buttonPanel);

        // 4. [新增] 替換主 Layout 為 GridBagLayout (用於全局置中)
        setLayout(new GridBagLayout());
        add(formWrapper, new GridBagConstraints());

        loginButton.addActionListener(e -> {
            String username = usernameBox.getText();
            String password = passwordBox.getText();

            try {
                loginManager.login(username, password);

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