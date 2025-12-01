import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritesPanel extends JPanel {

    private final LoginManager loginManager;
    private final JList<Movie> favoritesList;
    public final JButton favoritesBackButton;
    public JButton unfavoriteButton;

    public FavoritesPanel(LoginManager loginManager) {

        this.loginManager = loginManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        favoritesList = new JList<>();     // <-- initialize here
        favoritesList.setCellRenderer(new MovieCellRenderer());

        JScrollPane scrollPane = new JScrollPane(favoritesList);
        add(scrollPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        favoritesBackButton = new JButton("Back");
        add(favoritesBackButton);
        unfavoriteButton = new JButton("Unfavorite");
        add(unfavoriteButton);

        favoritesList.setCellRenderer(new MovieCellRenderer());

        add(new JLabel("Your Favorite Movies:"));

        favoritesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Movie m = favoritesList.getSelectedValue();
                if (m != null) {
                    ViewMovieDetailsInteractor interactor =
                            new ViewMovieDetailsInteractor(new OMDbApiClient("51f8a124"));
                    new MovieDetailsWindow(m, interactor);
                }
                favoritesList.clearSelection();
            }
        });

        unfavoriteButton.addActionListener(e -> {
            User user = loginManager.getLoggedInUser();
            if(user == null){
                JOptionPane.showMessageDialog(null, "You must be logged in.");
                return;
            }
            Movie selected = favoritesList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(null, "Please select a movie first.");
                return;
            }
            user.unfavoriteMovie(selected);
            JOptionPane.showMessageDialog(null, "Removed to favorites!");

            });
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
    }

}