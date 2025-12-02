package uipanels;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import controllers.LoginManager;
import controllers.ViewMovieDetailsInteractor;
import exceptions.MovieAlreadyFavoritedException;
import models.Movie;
import models.User;

public class MovieDetailsWindow extends JFrame {

    private Movie movie;
    private DefaultListModel<Object> favoritesModel;
    private JButton heartButton;
    private ViewMovieDetailsInteractor interactor;

    public MovieDetailsWindow(Movie movie,
                              ViewMovieDetailsInteractor interactor, LoginManager loginManager) {
        this.movie = movie;
        this.interactor = interactor;

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
        heartButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20)); // 支援愛心符號
        heartButton.setBorderPainted(false);
        heartButton.setFocusPainted(false);
        heartButton.setContentAreaFilled(false);
        heartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        heartButton.addActionListener(e -> toggleFavorite(loginManager));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(heartButton, BorderLayout.EAST);
        mainPanel.add(headerPanel);

        // --- 2. Poster Image ---
        JLabel posterLabel = new JLabel("Loading poster...");
        posterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(posterLabel);

        loadPoster(movie.poster, posterLabel);

        // --- 3. Detailed Info Area ---
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

        // --- 4. Fetch Details from API (Async) ---
        fetchDetailedInfo(plotLabel, infoLabel);

        add(mainPanel);
        setVisible(true);
    }

    private void updateHeartState(LoginManager loginManager) {
        User user = loginManager.getLoggedInUser();
        boolean isFav = user.getFavorites().contains(movie);
        if (!isFav) {
            heartButton.setText(isFav ? "❤" : "♡"); // 實心 vs 空心
            heartButton.setForeground(isFav ? Color.RED : Color.BLACK);
        } else {
            heartButton.setText(isFav ? "♡" : "❤"); // 實心 vs 空心
            heartButton.setForeground(isFav ? Color.BLACK : Color.RED);

        }

    }

    private void toggleFavorite(LoginManager loginManager) {
        User user = loginManager.getLoggedInUser();
        if(user == null){
            JOptionPane.showMessageDialog(null, "You must be logged in.");
            return;
        }
        boolean isFav = user.getFavorites().contains(movie);
        if(!isFav) {
            try {
                user.favoriteMovie(movie);
                JOptionPane.showMessageDialog(null, "Added to favorites!");
            } catch (MovieAlreadyFavoritedException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }

        else {
            user.unfavoriteMovie(movie);
            JOptionPane.showMessageDialog(null, "Removed from favorites!");
        }
        updateHeartState(loginManager);
    }

    private void loadPoster(String urlString, JLabel label) {
        if (urlString != null && !urlString.equals("N/A")) {
            new Thread(() -> {
                try {
                    URL url = new URL(urlString);
                    ImageIcon icon = new ImageIcon(url);
                    Image scaled = icon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaled);
                    SwingUtilities.invokeLater(() -> {
                        label.setText("");
                        label.setIcon(scaledIcon);
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

                    // update your local movie instance if you want to preserve it elsewhere
                    movie.plot = detailed.plot;
                    movie.director = detailed.director;
                    movie.genre = detailed.genre;
                    movie.runtime = detailed.runtime;
                    movie.rating = detailed.rating;
                    movie.poster = detailed.poster;
                    movie.year = detailed.year; // optional if it may be updated

                    // update UI
                    plotLabel.setText("<html><p style='width: 350px'>" + movie.plot + "</p></html>");

                    infoLabel.setText("<html><b>Year:</b> " + movie.year +
                            "<br><b>Genre:</b> " + movie.genre +
                            "<br><b>Director:</b> " + movie.director +
                            "<br><b>Runtime:</b> " + movie.runtime +
                            "<br><b>Rating:</b> ⭐ " + movie.rating + "</html>");
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        plotLabel.setText("Failed to load details.")
                );
            }
        }).start();
    }
}