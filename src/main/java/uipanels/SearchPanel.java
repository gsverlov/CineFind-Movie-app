package uipanels;

import controllers.LoginManager;
import models.Movie;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SearchPanel extends JPanel {

    public JComboBox<Object> searchBox;
    public JButton favoriteButton;
    public JButton advancedButton;
    public JButton loginButton;
    public JButton signupButton;
    public JButton profileButton;
    public JPanel topButtonPanel;

    public SearchPanel(LoginManager loginManager) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // 四周留白，增加呼吸感

        topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 靠右對齊
        topButtonPanel.setMaximumSize(new Dimension(1000, 50)); // 限制高度
        topButtonPanel.setOpaque(false); // 透明背景，吃底色

        loginButton = createStyledButton("Login");
        signupButton = createStyledButton("Signup");
        profileButton = createStyledButton("Profile Settings");

        topButtonPanel.add(loginButton);
        topButtonPanel.add(signupButton);
        topButtonPanel.add(profileButton);

        updateButtons(loginManager);

        JLabel titleLabel = new JLabel("CINEFIND");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48)); // 超大字體
        titleLabel.setForeground(new Color(229, 9, 20)); // Netflix 紅
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 置中

        JLabel subtitleLabel = new JLabel("Discover your next favorite movie");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel searchContainer = new JPanel();
        searchContainer.setLayout(new BoxLayout(searchContainer, BoxLayout.Y_AXIS));
        searchContainer.setOpaque(false);
        searchContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel searchLabel = new JLabel("Search Movie Title:");
        searchLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        searchBox = new JComboBox<>();
        searchBox.setEditable(true);
        searchBox.setRenderer(new MovieCellRenderer());

        Dimension searchSize = new Dimension(400, 40);
        searchBox.setPreferredSize(searchSize);
        searchBox.setMaximumSize(searchSize);

        searchBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton();
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setVisible(false);
                return b;
            }
        });

        Component editorComponent = searchBox.getEditor().getEditorComponent();
        editorComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (searchBox.getItemCount() > 0) {
                    searchBox.showPopup();
                }
            }
        });

        searchContainer.add(searchLabel);
        searchContainer.add(Box.createVerticalStrut(10)); // 標籤和輸入框的間距
        searchContainer.add(searchBox);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); // 左右間距 20
        actionButtonPanel.setOpaque(false);
        actionButtonPanel.setMaximumSize(new Dimension(600, 50));

        favoriteButton = createStyledButton("My Favorites");
        advancedButton = createStyledButton("Advanced Search");

        favoriteButton.setPreferredSize(new Dimension(150, 40));
        advancedButton.setPreferredSize(new Dimension(160, 40));

        actionButtonPanel.add(favoriteButton);
        actionButtonPanel.add(advancedButton);

        add(topButtonPanel);

        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalStrut(10));
        add(subtitleLabel);

        add(Box.createVerticalStrut(40));
        add(searchContainer);

        add(Box.createVerticalStrut(30));
        add(actionButtonPanel);
        add(Box.createVerticalGlue());
        add(Box.createVerticalGlue());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void updateButtons(LoginManager loginManager) {
        boolean loggedIn = loginManager.isLoggedIn();
        loginButton.setVisible(!loggedIn);
        signupButton.setVisible(!loggedIn);
        profileButton.setVisible(loggedIn);
        topButtonPanel.revalidate();
        topButtonPanel.repaint();
    }

    public void addToHistory(Movie movie) {
        if (movie == null) return;
        DefaultComboBoxModel<Object> model = (DefaultComboBoxModel<Object>) searchBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Object item = model.getElementAt(i);
            if (item instanceof Movie) {
                Movie m = (Movie) item;
                if (m.imdbID.equals(movie.imdbID)) {
                    model.removeElementAt(i);
                    break;
                }
            }
        }
        model.insertElementAt(movie, 0);
        if (model.getSize() > 5) {
            model.removeElementAt(5);
        }
    }
}