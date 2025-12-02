package uipanels;

import javax.swing.*;

public class AdvancedPanel extends JPanel {

    public JTextField moodText;
    public JTextField themesText;
    public JButton advancedBackButton;
    public JButton searchButton;

    public AdvancedPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel("Advanced Movie Search");
        titleLabel.setFont(titleLabel.getFont().deriveFont(18f));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        // Mood input
        JPanel moodPanel = new JPanel();
        moodPanel.setLayout(new BoxLayout(moodPanel, BoxLayout.X_AXIS));
        moodPanel.add(new JLabel("Your Mood:"));
        moodPanel.add(Box.createHorizontalStrut(10));
        moodText = new JTextField(20);
        moodText.setMaximumSize(moodText.getPreferredSize());
        moodPanel.add(moodText);
        add(moodPanel);

        add(Box.createVerticalStrut(10));

        // Themes input
        JPanel themesPanel = new JPanel();
        themesPanel.setLayout(new BoxLayout(themesPanel, BoxLayout.X_AXIS));
        themesPanel.add(new JLabel("Movie Themes:"));
        themesPanel.add(Box.createHorizontalStrut(10));
        themesText = new JTextField(20);
        themesText.setMaximumSize(themesText.getPreferredSize());
        themesPanel.add(themesText);
        add(themesPanel);

        add(Box.createVerticalStrut(10));

        // Helper text
        JLabel helpLabel = new JLabel("<html><i>Examples: \"feeling happy\", \"action, mystery, thriller\"</i></html>");
        helpLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(helpLabel);

        add(Box.createVerticalStrut(20));

        // Search button
        searchButton = new JButton("Search");
        searchButton.setAlignmentX(CENTER_ALIGNMENT);
        add(searchButton);

        add(Box.createVerticalStrut(10));

        // Back button
        advancedBackButton = new JButton("Back");
        advancedBackButton.setAlignmentX(CENTER_ALIGNMENT);
        add(advancedBackButton);
    }

    /**
     * Get combined search input (mood + themes)
     */
    public String getSearchInput() {
        String mood = moodText.getText().trim();
        String themes = themesText.getText().trim();

        StringBuilder combined = new StringBuilder();

        if (!mood.isEmpty()) {
            combined.append("I'm feeling ").append(mood).append(". ");
        }

        if (!themes.isEmpty()) {
            combined.append("I like movies with themes: ").append(themes).append(".");
        }

        return combined.toString().trim();
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getBackButton() {
        return advancedBackButton;
    }
}