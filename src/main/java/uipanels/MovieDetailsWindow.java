package uipanels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import controllers.AddFavoriteInteractor;
import controllers.LoginManager;
import controllers.ViewMovieDetailsInteractor;
import models.Movie;
import models.User;

public class MovieDetailsWindow extends JFrame {

    private Movie movie;
    private JButton heartButton;
    private ViewMovieDetailsInteractor interactor;
    private Runnable onFavoriteChange;
    private JLabel infoLabel;
    private JLabel plotLabel;
    private JLabel titleLabel; // Declared globally for easier access to fix truncation

    // UI Constants
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.ITALIC, 14);
    private static final Font DETAIL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font HEART_FONT = new Font("Segoe UI Symbol", Font.PLAIN, 24);

    // Color Constants for Dark Theme Readability
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color TEXT_ACCENT = Color.LIGHT_GRAY;
    private static final Color FAVORITE_COLOR = new Color(255, 105, 180);

    // Core Dimensions for alignment
    private static final int POSTER_WIDTH = 250;
    private static final int POSTER_HEIGHT = 375;
    private static final int WINDOW_PADDING = 25;
    private static final int ALIGNED_CONTENT_WIDTH = POSTER_WIDTH;

    public MovieDetailsWindow(Movie movie,
                              ViewMovieDetailsInteractor interactor,
                              LoginManager loginManager,
                              Runnable onFavoriteChange) {
        this.movie = movie;
        this.interactor = interactor;
        this.onFavoriteChange = onFavoriteChange;

        // --- Window Setup ---
        setTitle("Details: " + movie.title);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // --- Main Content Panel (BoxLayout for vertical stacking) ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING));

        int mainPanelWidth = ALIGNED_CONTENT_WIDTH + (WINDOW_PADDING * 2);
        mainPanel.setMaximumSize(new Dimension(mainPanelWidth, Integer.MAX_VALUE));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        // --- Header Panel (Title and Heart Button) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(ALIGNED_CONTENT_WIDTH, 70)); // Increased height for wrapped title
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // FIX 1: Use HTML to force word wrapping AND centering, preventing "..."
        titleLabel = new JLabel("<html><p style='width:" + (ALIGNED_CONTENT_WIDTH - 40) + "px; text-align: center;'>" + movie.title + "</p></html>");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_LIGHT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text within the JLabel's space

        heartButton = createHeartButton();
        heartButton.addActionListener(e -> toggleFavorite(loginManager));

        // Use a wrapper panel to align the title label to the center of the header panel
        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titleWrapper.setOpaque(false);
        titleWrapper.add(titleLabel);

        headerPanel.add(titleWrapper, BorderLayout.CENTER);
        headerPanel.add(heartButton, BorderLayout.EAST);

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // 1. Poster Section
        JLabel posterLabel = new JLabel("Poster Loading...");
        posterLabel.setForeground(TEXT_ACCENT);
        posterLabel.setPreferredSize(new Dimension(POSTER_WIDTH, POSTER_HEIGHT));
        posterLabel.setMaximumSize(new Dimension(POSTER_WIDTH, POSTER_HEIGHT));
        posterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        posterLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        posterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(posterLabel);

        mainPanel.add(Box.createVerticalStrut(15));

        // 2. Info Block (List of details)
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setMaximumSize(new Dimension(ALIGNED_CONTENT_WIDTH, Integer.MAX_VALUE));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoLabel = new JLabel("<html><b>Year:</b> " + movie.year + " | <b>Type:</b> " + movie.type + "</html>");
        infoLabel.setFont(SUBTITLE_FONT);
        infoLabel.setForeground(TEXT_ACCENT);
        infoPanel.add(infoLabel, BorderLayout.WEST);
        mainPanel.add(infoPanel);

        mainPanel.add(Box.createVerticalStrut(15));

        // 3. Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.DARK_GRAY);
        sep.setMaximumSize(new Dimension(ALIGNED_CONTENT_WIDTH, 2));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(sep);

        mainPanel.add(Box.createVerticalStrut(15));

        // 4. Summary Title
        JLabel plotTitleLabel = new JLabel("Summary");
        plotTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        plotTitleLabel.setForeground(TEXT_LIGHT);
        plotTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(plotTitleLabel);

        mainPanel.add(Box.createVerticalStrut(5));

        // 5. Plot/Summary Text
        plotLabel = new JLabel("<html><i>Loading detailed plot...</i></html>");
        plotLabel.setFont(DETAIL_FONT);
        plotLabel.setForeground(TEXT_ACCENT);
        plotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        plotLabel.setHorizontalAlignment(SwingConstants.CENTER); // Essential to center content within JLabel's bounds
        mainPanel.add(plotLabel);

        // --- Final Steps ---
        loadPoster(movie.poster, posterLabel);
        fetchDetailedInfo();
        updateHeartState(loginManager);

        add(mainPanel);
        pack();
        setVisible(true);
    }

    // --- Helper Methods ---

    private JButton createHeartButton() {
        JButton button = new JButton("♡");
        button.setFont(HEART_FONT);
        button.setForeground(TEXT_LIGHT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void updateHeartState(LoginManager loginManager) {
        if (!loginManager.isLoggedIn()) return;

        User user = loginManager.getLoggedInUser();
        boolean isFav = user != null && user.getFavorites() != null && user.getFavorites().contains(movie);

        heartButton.setText(isFav ? "❤" : "♡");
        heartButton.setForeground(isFav ? FAVORITE_COLOR : TEXT_ACCENT);
    }

    private void toggleFavorite(LoginManager loginManager) {
        if (!loginManager.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Please login first to manage favorites.", "Login Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = loginManager.getLoggedInUser();
        boolean isFav = user != null && user.getFavorites() != null && user.getFavorites().contains(movie);

        if (!isFav) {
            new AddFavoriteInteractor().execute(user, movie);
        } else {
            user.unfavoriteMovie(movie);
        }
        updateHeartState(loginManager);

        if (onFavoriteChange != null) {
            onFavoriteChange.run();
        }
    }

    private void loadPoster(String urlString, JLabel label) {
        if (urlString != null && !urlString.equals("N/A")) {
            new Thread(() -> {
                try {
                    URL url = new URL(urlString);
                    ImageIcon icon = new ImageIcon(url);
                    Image scaled = icon.getImage().getScaledInstance(POSTER_WIDTH, POSTER_HEIGHT, Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> {
                        label.setText(null);
                        label.setIcon(new ImageIcon(scaled));
                        pack();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> label.setText("Image Load Failed"));
                }
            }).start();
        } else {
            label.setText("No Poster Available");
        }
    }

    private void fetchDetailedInfo() {
        new Thread(() -> {
            try {
                Movie detailed = interactor.getMovieDetails(movie.imdbID);
                SwingUtilities.invokeLater(() -> {
                    if (detailed == null) {
                        plotLabel.setText("Failed to load details.");
                        return;
                    }
                    movie.plot = detailed.plot;

                    // FIX 2: Added 'text-align: center;' to the HTML style AND set Swing's Horizontal Alignment to center
                    plotLabel.setText("<html><p style='width: " + ALIGNED_CONTENT_WIDTH + "px; line-height: 1.5; color: " + getHtmlColor(TEXT_ACCENT) + "; text-align: center;'>" + movie.plot + "</p></html>");
                    plotLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    // Info in single-column, one-per-line format
                    String infoHtml = String.format(
                            "<html><p style='color: " + getHtmlColor(TEXT_LIGHT) + "; width: " + ALIGNED_CONTENT_WIDTH + "px;'>" +
                                    "<b>Year:</b> %s<br>" +
                                    "<b>Genre:</b> %s<br>" +
                                    "<b>Director:</b> %s<br>" +
                                    "<b>Runtime:</b> %s<br>" +
                                    "<b>Rating:</b> ⭐ %s<br>" +
                                    "<b>IMDb ID:</b> %s" +
                                    "</p></html>",
                            detailed.year, detailed.genre, detailed.director,
                            detailed.runtime, detailed.rating, detailed.imdbID);

                    infoLabel.setText(infoHtml);
                    infoLabel.setFont(DETAIL_FONT);
                    infoLabel.setForeground(TEXT_LIGHT);

                    pack();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> plotLabel.setText("Failed to load details."));
            }
        }).start();
    }

    // Helper method to convert AWT Color to HTML hex string
    private String getHtmlColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}