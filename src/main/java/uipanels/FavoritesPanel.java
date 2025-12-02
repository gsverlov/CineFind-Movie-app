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

                    // [MODIFIED] 傳入 this::loadFavoritesIntoList 作為回調函數
                    // 這就是 "Instant Update" 的關鍵！
                    new MovieDetailsWindow(m, interactor, loginManager, () -> {
                        loadFavoritesIntoList(); // 當狀態改變時，立即重載列表
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

        // 強制刷新 UI，確保視覺上立即反應
        favoritesList.revalidate();
        favoritesList.repaint();
    }
}