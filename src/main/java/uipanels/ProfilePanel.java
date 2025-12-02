package uipanels;

import controllers.LoginManager;
import models.User;

import javax.swing.*;
import java.util.Map;

public class ProfilePanel extends JPanel {
    public final JButton userButton;
    public final JButton passButton;
    public final JButton backButton;

    public ProfilePanel(LoginManager loginManager) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        User user = loginManager.getLoggedInUser();
        userButton = new JButton("Username");
        passButton = new JButton("Password");
        backButton = new JButton("Back");

        buildUI(loginManager);
    }

    public void buildUI(LoginManager loginManager){
        removeAll();
        User user = loginManager.getLoggedInUser();

        if (user == null) {
            add(new JLabel("You must be logged in to view your profile."));
            JPanel backPanel = new JPanel();
            backPanel.add(backButton);

            revalidate();
            repaint();
            return;
        }

        Map<String, String> map = User.getUserPasswordMap();

        JPanel headerPanel = new JPanel();
        headerPanel.add(new JLabel("Your current information is:"));

        JPanel userPanel = new JPanel();
        userPanel.add(new JLabel("Username: " + user.getUsername()));
        userPanel.add(new JLabel("Password: " + map.get(user.getUsername())));

        JPanel buttons = new JPanel();
        buttons.add(userButton);
        buttons.add(passButton);

        JPanel backPanel = new JPanel();
        backPanel.add(backButton);

        add(headerPanel);
        add(userPanel);
        add(buttons);
        add(backPanel);

        revalidate();
        repaint();
    }
}
