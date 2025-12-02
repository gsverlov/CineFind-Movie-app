package main;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import DataPersistence.ExtractData;
import apiservices.OMDbApiClient;
import controllers.LoginManager;
import controllers.ViewMovieDetailsInteractor;
import models.Movie;
import models.User;
import uipanels.AdvancedPanel;
import org.json.JSONObject;
import org.json.JSONArray;
import uipanels.*;
import exceptions.*;
import controllers.*;

import javax.swing.WindowConstants;

public class Main {

    public static void main(String[] args) {

        try {
            Color darkBg = new Color(18, 18, 18);
            Color panelBg = new Color(30, 30, 30);
            Color netflixRed = new Color(229, 9, 20);
            Color whiteText = new Color(240, 240, 240);
            Color grayText = new Color(170, 170, 170);

            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

            UIManager.put("control", darkBg);
            UIManager.put("info", darkBg);
            UIManager.put("nimbusBase", netflixRed);
            UIManager.put("nimbusBlueGrey", panelBg);
            UIManager.put("nimbusLightBackground", darkBg);
            UIManager.put("text", whiteText);

            UIManager.put("Panel.background", darkBg);
            UIManager.put("OptionPane.background", darkBg);
            UIManager.put("OptionPane.messageForeground", whiteText);

            UIManager.put("Button.background", netflixRed);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));

            UIManager.put("TextField.background", panelBg);
            UIManager.put("TextField.foreground", whiteText);
            UIManager.put("TextField.caretForeground", Color.WHITE); // 游標顏色

            UIManager.put("List.background", darkBg);
            UIManager.put("List.foreground", whiteText);
            UIManager.put("List.selectionBackground", netflixRed);
            UIManager.put("List.selectionForeground", Color.WHITE);

            UIManager.put("ComboBox.background", panelBg);
            UIManager.put("ComboBox.foreground", whiteText);

            UIManager.put("Label.foreground", whiteText);
            UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Path jsonFile = Paths.get("data/users.json");

        try {
            ExtractData.loadFromJson(jsonFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoginManager loginManager = new LoginManager();

        final String CARD_SEARCH = "SEARCH";
        final String CARD_RESULTS = "RESULTS";
        final String CARD_FAVORITES = "FAVORITES";
        final String CARD_ADVANCED = "ADVANCED";
        final String CARD_LOGIN = "LOGIN";
        final String CARD_SIGNUP = "SIGNUP";
        final String CARD_PROFILE = "PROFILE";
        final String CARD_USER = "USER";
        final String CARD_PASS = "PASS";


        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("CineFind - Movie Discovery"); // 改個比較酷的名字
            frame.setMinimumSize(new java.awt.Dimension(500, 700)); //稍微加大一點預設視窗
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            // 設定全域背景色 (為了讓 CardLayout 的空隙也是黑色的)
            frame.getContentPane().setBackground(new Color(18, 18, 18));

            CardLayout layout = new CardLayout();
            JPanel container = new JPanel(layout);
            container.setBackground(new Color(18, 18, 18)); // 重要：Container 也要黑底
            CardLayout cl = (CardLayout) container.getLayout();

            // Panels
            LoginPanel loginPanel = new LoginPanel(loginManager);
            SignupPanel signupPanel = new SignupPanel(loginManager);
            SearchPanel searchPanel = new SearchPanel(loginManager);
            ResultsPanel resultsPanel = new ResultsPanel(loginManager);
            FavoritesPanel favoritesPanel = new FavoritesPanel(loginManager);
            AdvancedPanel advancedPanel = new AdvancedPanel();
            ProfilePanel profilePanel = new ProfilePanel(loginManager);
            ChangeUserPanel changeUserPanel = new ChangeUserPanel(loginManager);
            ChangePasswordPanel changePasswordPanel = new ChangePasswordPanel();

            container.add(searchPanel, CARD_SEARCH);
            container.add(favoritesPanel, CARD_FAVORITES);
            container.add(advancedPanel, CARD_ADVANCED);
            container.add(resultsPanel, CARD_RESULTS);
            container.add(loginPanel, CARD_LOGIN);
            container.add(signupPanel, CARD_SIGNUP);
            container.add(profilePanel, CARD_PROFILE);
            container.add(changeUserPanel,CARD_USER);
            container.add(changePasswordPanel,CARD_PASS);

            frame.setContentPane(container);
            frame.pack(); // 用 pack 讓視窗自動調整大小
            frame.setLocationRelativeTo(null); // 讓視窗出現在螢幕正中央
            frame.setVisible(true);

            // --- Handle click on Search Result List ---
            resultsPanel.movieList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    Movie selected = resultsPanel.movieList.getSelectedValue();
                    if (selected != null) {
                        searchPanel.addToHistory(selected);
                        ViewMovieDetailsInteractor interactor =
                                new ViewMovieDetailsInteractor(new OMDbApiClient(System.getenv("OMBD_API_KEY")));
                        new MovieDetailsWindow(selected, interactor, loginManager, null);
                    }
                }
            });

            // --- Handle Search Box actions ---
            ActionListener searchAction = e -> {
                Object item = searchPanel.searchBox.getSelectedItem();
                if (item == null) return;

                if (item instanceof Movie) {
                    Movie historyMovie = (Movie) item;
                    ViewMovieDetailsInteractor interactor =
                            new ViewMovieDetailsInteractor(new OMDbApiClient(System.getenv("OMBD_API_KEY")));
                    new MovieDetailsWindow(historyMovie, interactor, loginManager, null);
                }
                else{
                    String searchText = item.toString().trim();
                    if(searchText.equals("")) {
                        JOptionPane.showMessageDialog(frame, "Please enter a movie name!");
                        return;
                    }

                    try {
                        OMDbApiClient c = new OMDbApiClient(System.getenv("OMBD_API_KEY"));
                        String j = c.searchMovies(searchText);
                        JSONObject obj = new JSONObject(j);
                        if(!obj.getBoolean("Response")) {
                            JOptionPane.showMessageDialog(frame, "No results found");
                            return;
                        }

                        JSONArray a = obj.getJSONArray("Search");
                        Movie[] ms = new Movie[a.length()];

                        for(int i=0; i<a.length(); i++){
                            JSONObject m = a.getJSONObject(i);
                            ms[i] = new Movie(
                                    m.getString("Title"),
                                    m.getString("Year"),
                                    m.getString("imdbID"),
                                    m.getString("Type"),
                                    m.getString("Poster")
                            );
                        }
                        resultsPanel.movieList.setListData(ms);
                        cl.show(container, CARD_RESULTS);

                    } catch(Exception ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                        return;
                    }
                }
            };

            searchPanel.searchBox.addActionListener(searchAction);

            // --- Navigation Buttons ---
            loginPanel.backButton.addActionListener(e -> {
                loginPanel.clearFields();
                cl.show(container, CARD_SEARCH);
            });
            signupPanel.backButton.addActionListener(e -> {
                signupPanel.clearFields();
                cl.show(container, CARD_SEARCH);
            });
            profilePanel.backButton.addActionListener(e -> cl.show(container, CARD_SEARCH));
            profilePanel.userButton.addActionListener(e -> cl.show(container, CARD_USER));
            profilePanel.passButton.addActionListener(e -> cl.show(container, CARD_PASS));
            changeUserPanel.backButton.addActionListener(e -> cl.show(container, CARD_PROFILE));
            changePasswordPanel.backButton.addActionListener(e -> cl.show(container, CARD_PROFILE));
            resultsPanel.resultsBackButton.addActionListener(e -> cl.show(container, CARD_SEARCH));
            favoritesPanel.favoritesBackButton.addActionListener(e -> cl.show(container, CARD_SEARCH));
            advancedPanel.advancedBackButton.addActionListener(e -> cl.show(container, CARD_SEARCH));

            searchPanel.favoriteButton.addActionListener(e ->{
                favoritesPanel.loadFavoritesIntoList();
                cl.show(container, CARD_FAVORITES);
            });
            searchPanel.advancedButton.addActionListener(e -> cl.show(container, CARD_ADVANCED));
            searchPanel.loginButton.addActionListener(e-> cl.show(container,CARD_LOGIN));
            searchPanel.signupButton.addActionListener(e-> cl.show(container,CARD_SIGNUP));
            searchPanel.profileButton.addActionListener(e -> cl.show(container, CARD_PROFILE));

            changeUserPanel.submitButton.addActionListener(e -> {
                if (!loginManager.isLoggedIn()) {
                    JOptionPane.showMessageDialog(frame, "You must be logged in to change username.");
                    return;
                }
                User user = loginManager.getLoggedInUser();
                String newUsername = changeUserPanel.userText.getText();
                try {
                    user.changeUsername(newUsername);
                    JOptionPane.showMessageDialog(frame, "Username changed successfully!");
                } catch (UsernameTakenException ex) {
                    JOptionPane.showMessageDialog(frame, "Username already taken.");
                    return;
                }
                profilePanel.buildUI(loginManager);
                cl.show(container, CARD_PROFILE);
            });

            changePasswordPanel.submitButton.addActionListener(e -> {
                try {
                    if (!loginManager.isLoggedIn()){
                        JOptionPane.showMessageDialog(frame, "You must be logged in to change password.");
                        return;
                    }
                    User user = loginManager.getLoggedInUser();
                    String pass1 = changePasswordPanel.passText1.getText();
                    String pass2 = changePasswordPanel.passText2.getText();
                    user.changePassword(pass1,pass2);
                    JOptionPane.showMessageDialog(frame, "Password changed successfully!");
                    profilePanel.buildUI(loginManager);
                    cl.show(container, CARD_PROFILE);
                } catch (PasswordsNotEqualException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                    return;
                }
            });

            signupPanel.signUpButton.addActionListener(e ->{
                String username = signupPanel.usernameBox.getText();
                String password = signupPanel.passwordBox.getText();
                try {
                    loginManager.createAccount(username, password);
                } catch (UsernameTakenException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                    return;
                }

                try {
                    loginManager.login(username, password);
                    JOptionPane.showMessageDialog(frame, "Account created and logged in!");
                } catch (UserNotFoundException | WrongPasswordException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                    return;
                }
                profilePanel.buildUI(loginManager);
                cl.show(container, CARD_PROFILE);
            });

            loginPanel.loginButton.addActionListener(e -> {
                String username = loginPanel.usernameBox.getText();
                String password = loginPanel.passwordBox.getText();

                try {
                    loginManager.login(username, password);
                    JOptionPane.showMessageDialog(frame, "Welcome back, " + username + "!");
                } catch (UserNotFoundException | WrongPasswordException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.");
                    return;
                }
                profilePanel.buildUI(loginManager);
                cl.show(container, CARD_PROFILE);
            });

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    try {
                        ExtractData.saveToJson(jsonFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            AdvancedSearchController advancedSearchController =
                    new AdvancedSearchController(advancedPanel, resultsPanel, new AppController() {
                        @Override
                        public void show(String name) {
                            if (name.equals(AppController.RESULTS)) {
                                cl.show(container, CARD_RESULTS);
                            }
                        }
                    });
        });
    }
}