package uipanels;

import apiservices.ApiConfig;
import apiservices.OMDbApiClient;
import controllers.LoginManager;
import controllers.ViewMovieDetailsInteractor;
import models.Movie;
import models.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritesPanel extends JPanel {

    private final LoginManager loginManager;
    private final JList<Movie> favoritesList;
    public final JButton favoritesBackButton;

    public FavoritesPanel(LoginManager loginManager) {

        this.loginManager = loginManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        favoritesList = new JList<>();
        favoritesList.setCellRenderer(new MovieCellRenderer());

        JScrollPane scrollPane = new JScrollPane(favoritesList);

        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        favoritesBackButton = new JButton("Back");


        favoritesList.setCellRenderer(new MovieCellRenderer());

        favoritesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Movie m = favoritesList.getSelectedValue();
                if (m != null) {
                    ViewMovieDetailsInteractor interactor =
                            new ViewMovieDetailsInteractor(new OMDbApiClient(ApiConfig.OMDB_API_KEY));

                    new MovieDetailsWindow(m, interactor, loginManager, () -> {
                        loadFavoritesIntoList();
                    });
                }
                favoritesList.clearSelection();
            }
        });
        add(new JLabel("Your Favorite Movies:"));
        add(scrollPane);
        add(favoritesBackButton);
    }

    public List<Movie> getFavorites() {
        if (!loginManager.isLoggedIn()) {
            return new ArrayList<>();
        }
        User curr = loginManager.getLoggedInUser();
        return curr.getFavorites();
    }

    public void loadFavoritesIntoList(){
        List<Movie> favorites = getFavorites();
        DefaultListModel<Movie> model = new DefaultListModel<>();
        for (Movie m : favorites) {
            model.addElement(m);
        }
        favoritesList.setModel(model);

        // force refresh UI so it appears updated
        favoritesList.revalidate();
        favoritesList.repaint();
    }
}