package uipanels;

import controllers.LoginManager;
import exceptions.UsernameTakenException; // 記得 import 這個異常

import javax.swing.*;

public class SignupPanel extends JPanel {
    public JButton signUpButton;
    public JButton backButton;

    // 把這兩個變數變成類別成員，這樣才能在其他方法清空它們
    private JTextField usernameBox;
    private JTextField passwordBox;

    public SignupPanel(LoginManager loginManager) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel usernamePanel = new JPanel();
        JLabel usernameLabel = new JLabel("Username:");
        usernameBox = new JTextField(20); // 這裡不用再宣告型別，使用上面的成員變數
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameBox);

        JPanel passwordPanel = new JPanel();
        JLabel passwordLabel = new JLabel("Password:");
        passwordBox = new JTextField(20); // 同上
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordBox);

        JPanel buttonPanel = new JPanel();
        signUpButton = new JButton("Signup");
        backButton = new JButton("Back");
        buttonPanel.add(signUpButton);
        buttonPanel.add(backButton);

        // --- [FIX] 修改後的監聽器 ---
        signUpButton.addActionListener(e -> {
            String username = usernameBox.getText();
            String password = passwordBox.getText();

            // 檢查輸入是否為空
            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password cannot be empty!");
                return;
            }

            try {
                // 嘗試建立帳號
                loginManager.createAccount(username, password);

                // 成功：顯示訊息並清空欄位
                JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
                clearFields();

            } catch (UsernameTakenException ex) {
                // 失敗：顯示錯誤訊息
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