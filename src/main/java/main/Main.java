package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

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
import apiservices.*;
import controllers.*;
import models.*;

import javax.swing.WindowConstants;

public class Main {

    public static void main(String[] args) {
        LoginManager loginManager = new LoginManager();

        final String CARD_SEARCH = "SEARCH";
        final String CARD_RESULTS = "RESULTS";
        final String CARD_FAVORITES = "FAVORITES";
        final String CARD_ADVANCED = "ADVANCED";
        final String CARD_LOGIN = "LOGIN";
        final String CARD_SIGNUP = "SIGNUP";

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("models.Movie Finder");
            frame.setMinimumSize(new java.awt.Dimension(300, 200));
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JPanel cardPanel = new JPanel(new CardLayout());

            // Panels
            LoginPanel loginPanel = new LoginPanel(loginManager);
            SignupPanel signupPanel = new SignupPanel(loginManager);

            SearchPanel searchPanel = new SearchPanel(loginManager);
            ResultsPanel resultsPanel = new ResultsPanel(loginManager);

            FavoritesPanel favoritesPanel = new FavoritesPanel(loginManager);

            AdvancedPanel advancedPanel = new AdvancedPanel();



            cardPanel.add(searchPanel, CARD_SEARCH);
            cardPanel.add(favoritesPanel, CARD_FAVORITES);
            cardPanel.add(advancedPanel, CARD_ADVANCED);
            cardPanel.add(resultsPanel, CARD_RESULTS);
            cardPanel.add(loginPanel, CARD_LOGIN);
            cardPanel.add(signupPanel, CARD_SIGNUP);


            frame.setContentPane(cardPanel);
            frame.setVisible(true);

            CardLayout cl = (CardLayout) (cardPanel.getLayout());

            // --- Handle click on Search Result List ---
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

                    // --- 修正 4: 這裡也要更新為新的建構子 ---
                    ViewMovieDetailsInteractor interactor =
                            new ViewMovieDetailsInteractor(new OMDbApiClient(System.getenv("OMBD_API_KEY")));
                    new MovieDetailsWindow(historyMovie, interactor, loginManager, null);
                }
                // Case B: models.User typed text and pressed Enter (String)
                else {
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
                        cl.show(cardPanel, CARD_RESULTS);

                    } catch(Exception ex){
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                    }
                }
            };

            searchPanel.searchBox.addActionListener(searchAction);

            // --- Navigation Buttons ---
            loginPanel.backButton.addActionListener(e -> {
                loginPanel.clearFields(); // empty text field
                cl.show(cardPanel, CARD_SEARCH);
            });

            signupPanel.backButton.addActionListener(e -> {
                signupPanel.clearFields(); // empty text field
                cl.show(cardPanel, CARD_SEARCH);
            });
            resultsPanel.resultsBackButton.addActionListener(e -> cl.show(cardPanel, CARD_SEARCH));
            favoritesPanel.favoritesBackButton.addActionListener(e -> cl.show(cardPanel, CARD_SEARCH));
            advancedPanel.advancedBackButton.addActionListener(e -> cl.show(cardPanel, CARD_SEARCH));
            searchPanel.favoriteButton.addActionListener(e ->{
                favoritesPanel.loadFavoritesIntoList();
                cl.show(cardPanel, CARD_FAVORITES);
            });
            searchPanel.advancedButton.addActionListener(e -> cl.show(cardPanel, CARD_ADVANCED));
            searchPanel.loginButton.addActionListener(e-> cl.show(cardPanel,CARD_LOGIN));
            searchPanel.signupButton.addActionListener(e-> cl.show(cardPanel,CARD_SIGNUP));

            frame.pack();

            AdvancedSearchController advancedSearchController =
                    new AdvancedSearchController(advancedPanel, resultsPanel, new AppController() {
                        @Override
                        public void show(String name) {
                            if (name.equals(AppController.RESULTS)) {
                                cl.show(cardPanel, CARD_RESULTS);
                            }
                        }
                    });


        });
    }
}