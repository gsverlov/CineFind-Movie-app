import javax.swing.*;

public class SearchPanel extends JPanel {

    public JTextField searchField;
    public JButton favoriteButton;
    public JButton advancedButton;

    public SearchPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel firstSearchPanel = new JPanel();
        firstSearchPanel.add(new JLabel("Enter Keyword"));
        searchField = new JTextField(10);
        searchField.setMaximumSize(searchField.getPreferredSize());
        firstSearchPanel.add(searchField);

        JPanel secondSearchPanel = new JPanel();
        favoriteButton = new JButton("favorite list");
        advancedButton = new JButton("Advanced Search");
        secondSearchPanel.add(favoriteButton);
        secondSearchPanel.add(advancedButton);

        add(firstSearchPanel);
        add(secondSearchPanel);
    }
}

