package uipanels;

import controllers.LoginManager; // 確保 import 這個
import controllers.UpdateSearchHistoryInteractor; // 確保 import 這個
import models.Movie;
import models.User; // 確保 import 這個

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SearchPanel extends JPanel {

    public JComboBox<Object> searchBox;
    public JButton favoriteButton;
    public JButton advancedButton;
    public JButton loginButton;
    public JButton signupButton;

    // [NEW] 我們需要 LoginManager 來知道是誰在搜尋
    private final LoginManager loginManager;

    // [MODIFIED] 建構子現在需要傳入 LoginManager
    public SearchPanel(LoginManager loginManager) {
        this.loginManager = loginManager; // 儲存起來

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //upper Login and Signup buttons
        JPanel topButtonPanel = new JPanel();
        loginButton = new JButton("Login");
        signupButton = new JButton("Signup");
        topButtonPanel.add(loginButton);
        topButtonPanel.add(signupButton);

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

    // [MODIFIED] 這就是你要展示給 TA 看的 AFTER
    public void addToHistory(Movie movie) {
        if (movie == null) return;

        // 1. 檢查是否登入
        if (!loginManager.isLoggedIn()) {
            return; // 沒登入就不記歷史紀錄
        }

        User currentUser = loginManager.getLoggedInUser();

        // 2. 呼叫 Interactor (Clean Architecture!)
        UpdateSearchHistoryInteractor interactor = new UpdateSearchHistoryInteractor();
        List<Movie> updatedHistory = interactor.execute(currentUser, movie);

        // 3. 更新 UI (View 職責)
        updateSearchBoxUI(updatedHistory);
    }

    // [NEW] Helper method to update the UI specifically
    private void updateSearchBoxUI(List<Movie> history) {
        DefaultComboBoxModel<Object> model = (DefaultComboBoxModel<Object>) searchBox.getModel();
        model.removeAllElements();

        for (Movie m : history) {
            model.addElement(m);
        }
    }
}