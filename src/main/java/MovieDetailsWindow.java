import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MovieDetailsWindow extends JFrame {

    public MovieDetailsWindow(String title, String year, String imdbID, String type, String posterURL) {
        setTitle(title);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Poster image
        JLabel posterLabel = new JLabel();
        posterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            ImageIcon icon = new ImageIcon(new URL(posterURL));
            Image scaled = icon.getImage().getScaledInstance(250, 350, Image.SCALE_SMOOTH);
            posterLabel.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            posterLabel.setText("No Image Available");
        }

        // Text labels
        JLabel titleLabel = new JLabel("Title: " + title);
        JLabel yearLabel = new JLabel("Year: " + year);
        JLabel idLabel = new JLabel("IMDB ID: " + imdbID);
        JLabel typeLabel = new JLabel("Type: " + type);

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        yearLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(posterLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(yearLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(idLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(typeLabel);

        add(panel);
        setVisible(true);
    }
}
