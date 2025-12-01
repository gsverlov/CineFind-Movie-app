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
        add(resultsBackButton);
    }
}