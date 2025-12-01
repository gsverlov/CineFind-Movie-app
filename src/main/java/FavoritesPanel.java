import javax.swing.*;
import java.awt.*;

public class FavoritesPanel extends JPanel {

    public JList<Object> favoritesList;
    public JButton favoritesBackButton;

    public FavoritesPanel(DefaultListModel<Object> model) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        favoritesList = new JList<>(model);
        favoritesList.setCellRenderer(new MovieCellRenderer());

        add(new JLabel("Your Favorite Movies:"));

        JScrollPane scrollPane = new JScrollPane(favoritesList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane);

        favoritesBackButton = new JButton("Back");
        add(favoritesBackButton);

        favoritesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Movie m = (Movie) favoritesList.getSelectedValue();
                if (m != null) {
                    ViewMovieDetailsInteractor interactor =
                            new ViewMovieDetailsInteractor(new OMDbApiClient("51f8a124"));
                    new MovieDetailsWindow(m, model, interactor);
                }
                favoritesList.clearSelection();
            }
        });
    }
}