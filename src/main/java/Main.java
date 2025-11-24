import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;
import org.json.JSONArray;
import javax.swing.WindowConstants;

public class Main {

    public static void main(String[] args) {

        final String CARD_SEARCH = "SEARCH";
        final String CARD_RESULTS = "RESULTS";
        final String CARD_FAVORITES = "FAVORITES";
        final String CARD_ADVANCED = "ADVANCED";

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Movie Finder");
            frame.setMinimumSize(new java.awt.Dimension(300, 200));
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JPanel cardPanel = new JPanel(new CardLayout());

            // Panels
            SearchPanel searchPanel = new SearchPanel();
            ResultsPanel resultsPanel = new ResultsPanel();
            FavoritesPanel favoritesPanel = new FavoritesPanel();
            AdvancedPanel advancedPanel = new AdvancedPanel();

            cardPanel.add(searchPanel, CARD_SEARCH);
            cardPanel.add(favoritesPanel, CARD_FAVORITES);
            cardPanel.add(advancedPanel, CARD_ADVANCED);
            cardPanel.add(resultsPanel, CARD_RESULTS);

            frame.setContentPane(cardPanel);
            frame.setVisible(true);

            CardLayout cl = (CardLayout) (cardPanel.getLayout());


            searchPanel.searchField.addActionListener(e -> {
                String txt = searchPanel.searchField.getText().trim();
                if(txt.equals("")) {
                    JOptionPane.showMessageDialog(frame,"Type something man!");
                    return;
                }

                try {
                    OMDbApiClient c = new OMDbApiClient("51f8a124");
                    String j = c.searchMovies(txt);

                    JSONObject obj = new JSONObject(j);
                    if(!obj.getBoolean("Response")) {
                        JOptionPane.showMessageDialog(frame,"No results found");
                        return;
                    }

                    JSONArray a = obj.getJSONArray("Search");
                    Movie[] ms = new Movie[a.length()];

                    for(int i=0;i<a.length();i++){
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
                    cl.show(cardPanel,CARD_RESULTS);

                } catch(Exception ex){
                    JOptionPane.showMessageDialog(frame,"Oops, something broke");
                }
            });

            resultsPanel.resultsBackButton.addActionListener(e -> cl.show(cardPanel,CARD_SEARCH));
            favoritesPanel.favoritesBackButton.addActionListener(e -> cl.show(cardPanel,CARD_SEARCH));
            advancedPanel.advancedBackButton.addActionListener(e -> cl.show(cardPanel,CARD_SEARCH));

            searchPanel.favoriteButton.addActionListener(e -> cl.show(cardPanel,CARD_FAVORITES));
            searchPanel.advancedButton.addActionListener(e -> cl.show(cardPanel,CARD_ADVANCED));

            frame.pack();
        });
    }
}
