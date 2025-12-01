import javax.swing.*;

public class LoginPanel extends JPanel {
    public JButton loginButton;
    public JButton backButton;

    public LoginPanel(LoginManager loginManager) {
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
        loginButton = new JButton("Login");
        backButton = new JButton("Back");
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        loginButton.addActionListener(e ->{
            String username = usernameBox.getText();
            String password = passwordBox.getText();

            try{
                loginManager.login(username, password);

            } catch (UserNotFoundException | WrongPasswordException ex){
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }

        });

        add(usernamePanel);
        add(passwordPanel);
        add(buttonPanel);
    }
}
