package uipanels;

import controllers.LoginManager;
import javax.swing.*;
import java.awt.*;

public class ChangePasswordPanel extends JPanel {
    public final JButton backButton;
    public final JButton submitButton;
    public final JTextField passText1;
    public final JTextField passText2;

    public ChangePasswordPanel(){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.CYAN);

        passText1 = new JTextField(20);
        passText2 = new JTextField(20);
        backButton = new JButton("Back");
        submitButton = new JButton("Submit");

        JPanel headerPanel = new JPanel();
        headerPanel.add(new JLabel("Change Password"));

        JPanel passPanel = new JPanel();
        passPanel.add(new JLabel("Enter new password: "));
        passPanel.add(passText1);
        passPanel.add(new JLabel("Re-enter new password: "));
        passPanel.add(passText2);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        add(headerPanel);
        add(passPanel);
        add(buttonPanel);
    }
}
