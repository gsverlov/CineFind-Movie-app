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

    // 把這兩個變數變成類別成員
    private JTextField usernameBox;
    private JTextField passwordBox;

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

        // --- [FIX] 修改後的監聽器 ---
        loginButton.addActionListener(e -> {
            String username = usernameBox.getText();
            String password = passwordBox.getText();

            try {
                loginManager.login(username, password);

                // 成功：顯示訊息並清空欄位
                JOptionPane.showMessageDialog(this, "Login Successful!");
                clearFields();

                // 注意：這裡通常需要通知 Main.java 切換畫面
                // 既然你的架構是在 Main 處理切換，使用者現在手動按 Back 即可看到登入後的狀態

            } catch (UserNotFoundException | WrongPasswordException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        add(usernamePanel);
        add(passwordPanel);
        add(buttonPanel);
    }

    // [NEW] 新增這個方法
    public void clearFields() {
        usernameBox.setText("");
        passwordBox.setText("");
    }
}