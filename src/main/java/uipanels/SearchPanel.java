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

        topButtonPanel = new JPanel();
        add(topButtonPanel); // make sure you actually add it to this panel

        loginButton = new JButton("Login");
        signupButton = new JButton("Signup");
        profileButton = new JButton("Profile Settings");


        topButtonPanel.add(loginButton);
        topButtonPanel.add(signupButton);
        topButtonPanel.add(profileButton);

        updateButtons(loginManager);

        // Focus Fix
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        // --- 1. Search Area ---
        JPanel firstSearchPanel = new JPanel();
        firstSearchPanel.add(new JLabel("Search Movie:"));

        firstSearchPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SearchPanel.this.requestFocusInWindow();
            }
        });

        // Initialize ComboBox
        searchBox = new JComboBox<>();
        searchBox.setEditable(true);

        searchBox.setPreferredSize(new Dimension(500, 30));

        searchBox.setRenderer(new MovieCellRenderer());

        // Hide arrow
        searchBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                return new JButton() {
                    @Override
                    public int getWidth() { return 0; }
                };
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

        firstSearchPanel.add(searchBox);

        // Buttons Area
        JPanel secondSearchPanel = new JPanel();
        favoriteButton = new JButton("Favorite List");
        advancedButton = new JButton("Advanced Search");
        secondSearchPanel.add(favoriteButton);
        secondSearchPanel.add(advancedButton);

        secondSearchPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SearchPanel.this.requestFocusInWindow();
            }
        });

        // Add components
        add(Box.createVerticalStrut(50));
        add(topButtonPanel);
        add(firstSearchPanel);
        add(secondSearchPanel);
        add(Box.createVerticalStrut(100));
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