import javax.swing.*;

public class ResultsPanel extends JPanel {

    public JList<Movie> movieList;
    public JButton resultsBackButton;

    public ResultsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        movieList = new JList<>();
        add(new JLabel("Search Results"));
        add(new JScrollPane(movieList));

        movieList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Movie m = movieList.getSelectedValue();
                if (m != null) {
                    new MovieDetailsWindow(m.title, m.year, m.imdbID, m.type, m.poster);
                }
            }
        });

        resultsBackButton = new JButton("Back");
        add(resultsBackButton);
    }
}
