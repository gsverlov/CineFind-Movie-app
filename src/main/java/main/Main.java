package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

            JFrame frame = new JFrame("models.Movie Finder");
            frame.setMinimumSize(new java.awt.Dimension(300, 200));
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            CardLayout layout = new CardLayout();
            JPanel container = new JPanel(layout);
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
            frame.setVisible(true);


            // Handle click on Search Result List
            resultsPanel.movieList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    Movie selected = resultsPanel.movieList.getSelectedValue();
                    if (selected != null) {
                        // 1. Add to history
                        searchPanel.addToHistory(selected);
                        ViewMovieDetailsInteractor interactor =
                                new ViewMovieDetailsInteractor(new OMDbApiClient(System.getenv("OMBD_API_KEY")));
                        new MovieDetailsWindow(selected, interactor, loginManager, null);
                    }
                }
            });

            // --- Handle Search Box actions (Enter key or Dropdown selection) ---
            ActionListener searchAction = e -> {
                Object item = searchPanel.searchBox.getSelectedItem();

                if (item == null) return;

                // Case A: models.User selected a history item (models.Movie object)
                if (item instanceof Movie) {
                    Movie historyMovie = (Movie) item;

                    ViewMovieDetailsInteractor interactor =
                            new ViewMovieDetailsInteractor(new OMDbApiClient(System.getenv("OMBD_API_KEY")));
                    new MovieDetailsWindow(historyMovie, interactor, loginManager, null);
                }
                // Case B: models.User typed text and pressed Enter (String)
                else{
                    String searchText = item.toString().trim();
                    if(searchText.equals("")) {
                        JOptionPane.showMessageDialog(frame, "Type something man!");
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
              loginPanel.clearFields(); //empty text field
              cl.show(container, CARD_SEARCH);
            });

            signupPanel.backButton.addActionListener(e -> {
                signupPanel.clearFields(); // empty text field
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
                } catch (UsernameTakenException ex) {
                    JOptionPane.showMessageDialog(frame, "You must be logged in to change username.");
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
                } catch (UserNotFoundException | WrongPasswordException ex) {
                    return;
                }
                profilePanel.buildUI(loginManager); // refresh to show correct user
                cl.show(container, CARD_PROFILE);

            });


            frame.pack();

            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    try {
                        ExtractData.saveToJson(jsonFile);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage());
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