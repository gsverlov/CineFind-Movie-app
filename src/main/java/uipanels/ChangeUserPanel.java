package uipanels;
import controllers.LoginManager;
import javax.swing.*;
import java.awt.*;

public class ChangeUserPanel extends JPanel {
    public final JButton backButton;
    public final JButton submitButton;
    public final JTextField userText;

    public ChangeUserPanel(LoginManager loginManager){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.PINK);

        backButton = new JButton("Back");
        submitButton = new JButton("Submit");
        userText = new JTextField(20);

        JPanel headerPanel = new JPanel();
        headerPanel.add(new JLabel("Change Username"));

        JPanel userPanel = new JPanel();
        userPanel.add(new JLabel("Please Enter your desired Username: "));
        userPanel.add(userText);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        // Add components
        add(headerPanel);
        add(userPanel);
        add(buttonPanel);
    }
}
