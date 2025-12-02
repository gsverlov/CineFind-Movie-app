package uipanels;

import javax.swing.*;
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

    public MovieDetailsWindow(Movie movie,
                              ViewMovieDetailsInteractor interactor,
                              LoginManager loginManager,
                              Runnable onFavoriteChange) {
        this.movie = movie;
        this.interactor = interactor;
        this.onFavoriteChange = onFavoriteChange;

        setTitle(movie.title);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header: Title + Heart Button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setMaximumSize(new Dimension(500, 40));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("<html><h2>" + movie.title + "</h2></html>");

        heartButton = new JButton("♡");
        heartButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        heartButton.setBorderPainted(false);
        heartButton.setFocusPainted(false);
        heartButton.setContentAreaFilled(false);
        heartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        heartButton.addActionListener(e -> toggleFavorite(loginManager));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(heartButton, BorderLayout.EAST);
        mainPanel.add(headerPanel);

        // Poster
        JLabel posterLabel = new JLabel("Loading poster...");
        posterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(posterLabel);

        loadPoster(movie.poster, posterLabel);

        // Info Area
        mainPanel.add(Box.createVerticalStrut(20));

        JLabel infoLabel = new JLabel("<html><b>Year:</b> " + movie.year +
                "<br><b>ID:</b> " + movie.imdbID +
                "<br><b>Type:</b> " + movie.type + "</html>");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(infoLabel);

        mainPanel.add(Box.createVerticalStrut(10));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(500, 2));
        mainPanel.add(sep);
        mainPanel.add(Box.createVerticalStrut(10));

        JLabel plotLabel = new JLabel("<html><i>Loading details...</i></html>");
        plotLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(plotLabel);

        fetchDetailedInfo(plotLabel, infoLabel);

        updateHeartState(loginManager);

        add(mainPanel);
        setVisible(true);
    }

    // heart indicator
    private void updateHeartState(LoginManager loginManager) {
        if (!loginManager.isLoggedIn()) return;

        User user = loginManager.getLoggedInUser();
        boolean isFav = user.getFavorites().contains(movie);

        heartButton.setText(isFav ? "❤" : "♡");
        heartButton.setForeground(isFav ? Color.RED : Color.BLACK);
    }

    private void toggleFavorite(LoginManager loginManager) {
        User user = loginManager.getLoggedInUser();
        if (!loginManager.isLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Please login first.");
            return;
        }
        boolean isFav = user.getFavorites().contains(movie);

        if (!isFav) {
            // add to favorite
            AddFavoriteInteractor addInteractor = new AddFavoriteInteractor();
            addInteractor.execute(user, movie);
        } else {
            // remove from favorite
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
                    Image scaled = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> {
                        label.setText("");
                        label.setIcon(new ImageIcon(scaled));
                    });
                } catch (Exception e) {}
            }).start();
        } else {
            label.setText("No Image");
        }
    }

    private void fetchDetailedInfo(JLabel plotLabel, JLabel infoLabel) {
        new Thread(() -> {
            try {
                Movie detailed = interactor.getMovieDetails(movie.imdbID);
                SwingUtilities.invokeLater(() -> {
                    if (detailed == null) {
                        plotLabel.setText("Failed to load details.");
                        return;
                    }
                    movie.plot = detailed.plot;

                    plotLabel.setText("<html><p style='width: 350px'>" + movie.plot + "</p></html>");
                    infoLabel.setText("<html><b>Year:</b> " + detailed.year +
                            "<br><b>Genre:</b> " + detailed.genre +
                            "<br><b>Director:</b> " + detailed.director +
                            "<br><b>Runtime:</b> " + detailed.runtime +
                            "<br><b>Rating:</b> ⭐ " + detailed.rating + "</html>");
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> plotLabel.setText("Failed to load details."));
            }
        }).start();
    }
}