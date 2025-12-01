import javax.swing.*;
import java.awt.*;

public class ResultsPanel extends JPanel {

    public JList<Movie> movieList;
    public JButton resultsBackButton;
    public JButton favoriteButton;

    public ResultsPanel(LoginManager loginManager) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        movieList = new JList<>();

        movieList.setCellRenderer(new MovieCellRenderer());

        add(new JLabel("Search Results"));

        JScrollPane scrollPane = new JScrollPane(movieList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane);


        resultsBackButton = new JButton("Back");
        favoriteButton = new JButton("Favorite");
        add(resultsBackButton);
        add(favoriteButton);

        favoriteButton.addActionListener(e -> {
            User user = loginManager.getLoggedInUser();
            if(user == null){
                JOptionPane.showMessageDialog(null, "You must be logged in.");
                return;
            }
            Movie selected = movieList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(null, "Please select a movie first.");
                return;
            }
            try {
                user.favoriteMovie(selected);
                JOptionPane.showMessageDialog(null, "Added to favorites!");
            } catch (MovieAlreadyFavoritedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });
    }
}